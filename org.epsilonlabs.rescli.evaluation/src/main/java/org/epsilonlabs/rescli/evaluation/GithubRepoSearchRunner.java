package org.epsilonlabs.rescli.evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.kohsuke.github.AbuseLimitHandler;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedSearchIterable;
import org.kohsuke.github.RateLimitHandler;
import org.kohsuke.github.extras.ImpatientHttpConnector;
import org.kohsuke.github.extras.OkHttpConnector;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import uk.ac.york.cs.ecss.ecssal.EcssalFactory;
import uk.ac.york.cs.ecss.ecssal.Root;
import uk.ac.york.cs.ecss.ecssal.SearchElement;
import uk.ac.york.cs.ecss.ecssal.SourceGrammar;
import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;


public class GithubRepoSearchRunner {
	
	private static final Logger logger = LogManager.getLogger(GithubRepoSearchRunner.class);

	private static final String SEARCH_RESULT_SUB_DIRECTORY = "search";
	private static final String DEFAULT_QUERY = "grammar extension:xtext size:>2000"; // replace this with something simpler
	private static final String DEFAULT_SEARCH_REPORT_FILE_NAME = "search-report.txt";
	private static final int DEFAULT_CONNECTION_TIMEOUT_IN_MILLI_SECONDS = 60000;
	private static final int DEFAULT_READ_TIMEOUT_IN_MILLI_SECONDS = 25000;
	private static final int COST = 4; // cost to create one SearchElement
	private static final int RATE_LIMIT_SLEEP_BUFFER = 100;
	private static final int SLEEP_DURATION = 3600000;
	private static final int DEFAULT_PAGE_SIZE = 100; // 100 = maximum
	private static final File DEFAULT_CACHE_DIRECTORY = new File("cache/");
	private static final long DEFAULT_CACHE_SIZE = 10 * 1024 * 1024; // 10 * 1024 * 1024 = 10 MB

	
	private Path searchResultPath;
	private LocalDateTime startTime;
	private boolean sleepWhileWaitingForRateLimitRelease;
	
	private String githubUserName;
	private String githubUserPass;
	
	private String githubQuery;
	
	private String ecssalFile;
	private String reportFile;
	
	private Root ecssalRoot;
	private int totalCount;
	

	// (full repo name;file download url), github user email, number of commits for [file download url, github user email] combination
	Table<String, String, Integer> resultTable = HashBasedTable.create();

	public GithubRepoSearchRunner(String reportPath, String ecssalModelFileLocation, String githubUserName, String githubUserPass) {
		this.reportFile = reportPath + DEFAULT_SEARCH_REPORT_FILE_NAME;
		this.ecssalFile = ecssalModelFileLocation;
		this.githubQuery = DEFAULT_QUERY;
		this.githubUserName = githubUserName;
		this.githubUserPass = githubUserPass;
	}	
	
	public GithubRepoSearchRunner(String reportPath, String ecssalModelFileLocation, String githubUserName, String githubUserPass, String githubQuery, boolean sleepWhileWaitingForRateLimitRelease) {		
		this(reportPath, ecssalModelFileLocation, githubUserName, githubUserPass);
		this.githubQuery = githubQuery;
		this.sleepWhileWaitingForRateLimitRelease = sleepWhileWaitingForRateLimitRelease;
	}
	
	private void serializeModel() {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

		Resource ecssalModelResource =  resourceSet.createResource(URI.createFileURI(ecssalFile));
		ecssalModelResource.getContents().add(ecssalRoot);
		try {
			ecssalModelResource.save(Collections.emptyMap());
		} catch (IOException e) {
			logger.info("Unable to serialize/save ECSSAL model.");
			e.printStackTrace();
		}
	}
	
	private void reset() {
		totalCount = 0;
		startTime = LocalDateTime.now();
		// new ecssal model		  
		ecssalRoot = EcssalFactory.eINSTANCE.createRoot();
		ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("start time", Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()).toString()));
		ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("github query", githubQuery));		
		
		searchResultPath = Paths.get(Paths.get(reportFile).getParent() + File.separator + SEARCH_RESULT_SUB_DIRECTORY); // output folder is where the report file has been defined to be placed
	}

	public void setQuery(String githubQuery) {
		this.githubQuery = githubQuery;
	}
	
	public Root getEcssalRoot() {
		return ecssalRoot;
	}

	public void runSearch() {
		reset();
		try {
			
//			Cache cache = new Cache(DEFAULT_CACHE_DIRECTORY, DEFAULT_CACHE_SIZE); 
//			GitHub github = new GitHubBuilder().withPassword(githubUserName, githubUserPass)
//			    .withConnector(new OkHttpConnector(new OkUrlFactory(new OkHttpClient().setCache(cache))))
//			    .build();
			
			OkHttpClient client = new OkHttpClient();
			OkUrlFactory urlFactory = new OkUrlFactory(client);
			HttpConnector connector = new ImpatientHttpConnector(new OkHttpConnector(urlFactory), DEFAULT_CONNECTION_TIMEOUT_IN_MILLI_SECONDS, DEFAULT_READ_TIMEOUT_IN_MILLI_SECONDS);
			GitHub github = new GitHubBuilder().withPassword(githubUserName, githubUserPass).withConnector(connector).withAbuseLimitHandler(AbuseLimitHandler.WAIT).withRateLimitHandler(RateLimitHandler.WAIT).build();
//			GitHub github = GitHub.connectUsingPassword(githubUserName, githubUserPass);

			GHRateLimit rateLimit = github.getRateLimit();
			Date resetDate = rateLimit.getResetDate();
			boolean credentialValid = github.isCredentialValid();
				
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("user", githubUserName));
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("credentials valid", String.valueOf(credentialValid)));
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("limit", String.valueOf(rateLimit)));
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("remaining", String.valueOf(rateLimit.remaining)));
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("reset date", resetDate.toString()));

			logger.info("USER: " + githubUserName);
			logger.info("CREDENTIALS VALID: " + credentialValid);
			logger.info("LIMIT: " + rateLimit);
			logger.info("REMAINING: " + rateLimit.remaining);
			logger.info("RESET DATE: " + resetDate);
	
			PagedSearchIterable<GHContent> result = github.searchContent().q(githubQuery).list().withPageSize(DEFAULT_PAGE_SIZE);
			logger.info("QUERY: " + githubQuery);
			totalCount = result.getTotalCount();
			String totalCountString = "RESULTS: " + Integer.toString(totalCount);
			logger.info(totalCountString);
			
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("query", githubQuery));
			ecssalRoot.getSearchParameters().add(EcssalFactory.eINSTANCE.createParameter("total result count", totalCountString));
			
//			if (!sleepWhileWaitingForRateLimitRelease && rateLimit.remaining < totalCount * COST) {
//				logger.info("NOT ENOUGH REMAINING RATE TO EXECUTE THIS QUERY!");
//				logger.info("THIS QUERY REQUIRES A RATE OF " + totalCount * COST);
//				long waitingTime = startTime.until(LocalDateTime.ofInstant(resetDate.toInstant(), ZoneId.systemDefault()),
//						ChronoUnit.MINUTES);
//				logger.info("PLEASE USE DIFFERENT CREDENTIALS, QUERY, OR WAIT UNTIL " + resetDate + " (" + waitingTime + "min)\n");
//				System.exit(0);	// Alternative: implement/use a mechanism that restarts the search run after the waiting time has passed?
//			}
	
			logger.info("THIS QUERY REQUIRES A RATE OF " + totalCount * COST);
			String reportLine = "=============== START ITERATING GITHUB (" + new Date().toString() + ") =================";
			logger.info(reportLine);
			
	
			int currResNo = 1;
			
			for (GHContent resultItem : result) {
				GHRepository resultRepo = resultItem.getOwner();
				String resultItemFullRepoName = resultRepo.getFullName();
				String resultItemPath = java.net.URLDecoder.decode(resultItem.getPath(),"UTF-8");
				String resultItemDownloadUrl = java.net.URLDecoder.decode(resultItem.getDownloadUrl(),"UTF-8");
				
				// new query for commits
				for (GHCommit commit : resultRepo.queryCommits().path(resultItemPath).list()) {
				     String authorEmail = commit.getCommitShortInfo().getAuthor().getEmail();
//				     logger.info("commit=" + commit.getHtmlUrl());
//				     logger.info("authorEmail=" + authorEmail);
				     int count = 0;
				     String tableKey = resultItemFullRepoName+";"+resultItemDownloadUrl;
				     if ( resultTable.get(tableKey, authorEmail) != null) {
				    	 	count = resultTable.get(tableKey, authorEmail);
				     }
				     resultTable.put(tableKey, authorEmail, count + 1);
				 }
								
				SearchElement searchElement = EcssalFactory.eINSTANCE.createSearchElement();
				SourceGrammar sourceGrammar =  EcssalFactory.eINSTANCE.createSourceGrammar();
				searchElement.setGithubUserAndRepo(resultRepo.getFullName());
				searchElement.setProvider(resultRepo.getOwnerName());
				searchElement.setRespository(resultRepo.getName());
				try {
					Path localDownloadTargetFile = obtainAvailableLocalFilePath(resultItem);
					searchElement.setName(localDownloadTargetFile.getFileName().toString().replaceFirst("[.][^.]+$", ""));

					downloadElement(resultItem, localDownloadTargetFile);
					
					sourceGrammar.setRemoteFileLocation(resultItemDownloadUrl);
					sourceGrammar.setLocalFileLocation(localDownloadTargetFile.toString());
					
					searchElement.setGrammar(sourceGrammar);
					
				} catch (Exception e) {
					logger.info("Unable to obtain source grammar file.");
					e.printStackTrace();
				}
				
			/*	if (!resultRepo.isPrivate()) {
	
					// assignees
					AtomicInteger repoAssignees = null;
					try {
						PagedIterable<GHUser> assigneesList = resultRepo.listAssignees();
						repoAssignees = countGHUsers(resultRepo, assigneesList);
						searchElement.setAssigneesCount(repoAssignees.intValue());		
						
					} catch (IOException e) {
						logger.info("Failed to retrieve list of assignees for '" + searchElement.getName() + "'.");
						e.printStackTrace();
					}
	
					// stargazers
					PagedIterable<GHUser> stargazersList = resultRepo.listStargazers();
					AtomicInteger repoStargazers = countGHUsers(resultRepo, stargazersList);
					searchElement.setStargazersCount(repoStargazers.intValue());
	
					// subscribers
					PagedIterable<GHUser> subscribersList = resultRepo.listSubscribers();
					AtomicInteger repoSubscribers = countGHUsers(resultRepo, subscribersList);
					searchElement.setSubscribersCount(repoSubscribers.intValue());
					
					String repoInfo = "REPO '" + resultRepo.getFullName() + "' COUNTS " + repoStargazers + " STARGAZERS, " + repoSubscribers + " SUBSCRIBERS, and ";
					if (repoAssignees != null) {
						repoInfo += repoAssignees + " ASSIGNEES.";
					} else {
						repoInfo += "N/A ASSIGNEES.";
					}
					logger.info(repoInfo);
 	
				}*/
				
				// add individual search result, i.e., one particular grammar, to analysis model
				ecssalRoot.getSearchElements().add(searchElement);
				String reportAddedSearchElement = "ADDED SearchElement (" + currResNo + " of " + totalCount + "): " + searchElement.getName();
				logger.info(reportAddedSearchElement);
				
//				if (rateLimit.remaining < RATE_LIMIT_SLEEP_BUFFER) {
//					logger.info("Remaining Github requests exceed configured RATE_LIMIT_SLEEP_BUFFER.");
//					logger.info("Thus, sleeping for 1 hour ...");
//					try {
//						Thread.sleep(SLEEP_DURATION);
//					} catch (InterruptedException e) {
//						logger.error("Unable to put threat to sleep. Message: " + e.getMessage());
//						e.printStackTrace();
//					}
//					logger.info("... finished sleeping, continuing thread now.");
//				}
				
//				if (currResNo % 1000 == 0) {
//				try {
//					logger.info("Going to sleep for 1 hour ...");
//					Thread.sleep(SLEEP_DURATION);
//				} catch (InterruptedException e) {
//					logger.warn("Unable to put threat to sleep. Message: " + e.getMessage());
//					e.printStackTrace();
//				}
//				logger.info("... finished sleeping, continuing thread now.");
//			}

				++currResNo;
				
			}// GHContent loop end
	
			reportLine =  "=============== FINISHED ITERATING GITHUB (" + new Date().toString() + ") =================";
			logger.info(reportLine);
			
			logger.info("=============== RESULT TABLE PRINTOUT ===============");
			logger.info(resultTable.toString());
			logger.info("=============== RESULT TABLE PRINTOUT (END) =========");
			
			serializeModel();
			
		} catch (IOException e1) {
			logger.info("Failed to connect to GitHub (bad credentials or timeout).");
			e1.printStackTrace();
		}
	}
	
	private Path obtainAvailableLocalFilePath(GHContent resultItem) throws IOException {
		String fileName = new FileUtils(reportFile).getProperFileNameFromUrl(resultItem.getDownloadUrl());

		String extension = "";
		String name = "";
		
		int idxOfDot = fileName.lastIndexOf('.');   //Get the last index of . to separate extension
		extension = fileName.substring(idxOfDot + 1);
		name = fileName.substring(0, idxOfDot);

		Path path = Paths.get(searchResultPath + File.separator + fileName);
		int counter = 1;
		File availableFile = null;
		while(Files.exists(path)){
		    fileName = name+"_"+counter + "." + extension;
		    path = Paths.get(searchResultPath + File.separator + fileName);
		    counter++;
		}
		availableFile = new File(fileName);
		
		return Paths.get(searchResultPath + File.separator + availableFile);
	}
	
	private void downloadElement(GHContent item, Path localFilePath) throws Exception {
		try {
			String downloadUrlString = item.getDownloadUrl();
			URL remoteUrl = new URL(downloadUrlString);
			
			Files.createDirectories(localFilePath.getParent());
						
			if (Files.notExists(localFilePath)) {
				Files.createFile(localFilePath); 
				ReadableByteChannel rbc = Channels.newChannel((remoteUrl).openConnection().getInputStream());
				FileOutputStream fos = new FileOutputStream(localFilePath.toFile());
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.flush();
				fos.close();	
				
				logger.info("Downloaded file " + remoteUrl);
				logger.info("TO: " + localFilePath.toString());
			}

		} catch (MalformedURLException e1) {
			logger.warn("FAILED TO TRANSFORM URL STRING TO URL OBJECT");
			throw new Exception("FAILED TO TRANSFORM URL STRING TO URL OBJECT " + e1); 
		} catch (IOException e) {
			throw new Exception("FAILED TO DOWNLOAD ELEMENT " + e); 
		}
	}

	private AtomicInteger countGHUsers(GHRepository resultRepo, PagedIterable<GHUser> countList) {
		AtomicInteger counter = new AtomicInteger();

		countList.forEach((countingElement) -> {
			counter.incrementAndGet();
		});
		return counter;
	}
	
}