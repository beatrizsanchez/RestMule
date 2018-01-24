package org.epsilonlabs.rescli.evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.kohsuke.github.AbuseLimitHandler;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.kohsuke.github.PagedSearchIterable;
import org.kohsuke.github.RateLimitHandler;
import org.kohsuke.github.extras.ImpatientHttpConnector;
import org.kohsuke.github.extras.OkHttpConnector;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;


public class GithubRepoSearchRunner {
	
	private static final Logger logger = LogManager.getLogger(GithubRepoSearchRunner.class);

	private static final String SEARCH_RESULT_SUB_DIRECTORY = "search";
	private static final int DEFAULT_CONNECTION_TIMEOUT_IN_MILLI_SECONDS = 60000;
	private static final int DEFAULT_READ_TIMEOUT_IN_MILLI_SECONDS = 25000;
	private static final int DEFAULT_PAGE_SIZE = 100; // 100 = maximum
	
	private Path searchResultPath;
	
	private String githubUserName;
	private String githubUserPass;
	
	private String githubQuery;
	
	private int totalCount = 0;
	private int totalCommitCount = 0;
	
	// (full repo name;file download url), github user email, number of commits for [file download url, github user email] combination
	Table<String, String, Integer> resultTable = HashBasedTable.create();

	public GithubRepoSearchRunner(String reportPath, String githubUserName, String githubUserPass, String githubQuery) {		
		this.searchResultPath = Paths.get(Paths.get(reportPath) + File.separator + SEARCH_RESULT_SUB_DIRECTORY); // output folder is where the report file has been defined to be placed
		this.githubUserName = githubUserName;
		this.githubUserPass = githubUserPass;
		this.githubQuery = githubQuery;
	}
	
	public void setQuery(String githubQuery) {
		this.githubQuery = githubQuery;
	}
	
	public void runSearch() {
		try {
			
			OkHttpClient client = new OkHttpClient();
			OkUrlFactory urlFactory = new OkUrlFactory(client);
			HttpConnector connector = new ImpatientHttpConnector(new OkHttpConnector(urlFactory), DEFAULT_CONNECTION_TIMEOUT_IN_MILLI_SECONDS, DEFAULT_READ_TIMEOUT_IN_MILLI_SECONDS);
			GitHub github = new GitHubBuilder().withPassword(githubUserName, githubUserPass).withConnector(connector).withAbuseLimitHandler(AbuseLimitHandler.WAIT).withRateLimitHandler(RateLimitHandler.WAIT).build();

			PagedSearchIterable<GHContent> result = github.searchContent().q(githubQuery).list().withPageSize(DEFAULT_PAGE_SIZE);
			totalCount = result.getTotalCount();
			logger.info("TOTAL RESULT COUNT: " + Integer.toString(totalCount));
			
			int currResNo = 1;
			
			for (GHContent resultItem : result) {
				GHRepository resultRepo = resultItem.getOwner();
				String resultItemFullRepoName = resultRepo.getFullName();
				String resultItemPath = java.net.URLDecoder.decode(resultItem.getPath(),"UTF-8");
				String resultItemDownloadUrl = java.net.URLDecoder.decode(resultItem.getDownloadUrl(),"UTF-8");
				
				for (GHCommit commit : resultRepo.queryCommits().path(resultItemPath).list()) {
				     String authorEmail = commit.getCommitShortInfo().getAuthor().getEmail();
				     int count = 0;
				     String tableKey = resultItemFullRepoName+";"+resultItemDownloadUrl;
				     if ( resultTable.get(tableKey, authorEmail) != null) {
				    	 	count = resultTable.get(tableKey, authorEmail);
				     }
				     resultTable.put(tableKey, authorEmail, count + 1);
				     ++totalCommitCount;
				 }
								
				try {
					Path localDownloadTargetFile = obtainAvailableLocalFilePath(resultItem);
					downloadElement(resultItem, localDownloadTargetFile);
					
					logger.info("ADDED (" + currResNo + " of " + totalCount + "): " + resultItemFullRepoName);
				} catch (Exception e) {
					logger.info("Unable to obtain source grammar file.");
					e.printStackTrace();
				}
				
				++currResNo;
				
			}// GHContent loop end
			
			logger.info("TOTAL COMMIT COUNT: " + totalCommitCount);
			logger.info("=============== RESULT TABLE PRINTOUT ===============");
			logger.info(resultTable.toString());
			logger.info("=============== RESULT TABLE PRINTOUT (END) =========");
			
		} catch (IOException e1) {
			logger.info("Failed to connect to GitHub (bad credentials or timeout).");
			e1.printStackTrace();
		}
	}
	
	private Path obtainAvailableLocalFilePath(GHContent resultItem) throws IOException {
		String fileName = getProperFileNameFromUrl(resultItem.getDownloadUrl());

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
	
	public String getProperFileNameFromUrl(String filePath) {
		String fileName = null;
		try {
			fileName = java.net.URLDecoder.decode(new File(filePath).getName(), "UTF-8");
			fileName = fileName.replaceAll(" ", "-");
			String regex = "[\\(|\\[|\\{|\\)|\\}|\\]]";
			fileName = fileName.replaceAll(regex, "");
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to get a proper file name from string file path.");
			e.printStackTrace();
		}
		return fileName;
	}

}