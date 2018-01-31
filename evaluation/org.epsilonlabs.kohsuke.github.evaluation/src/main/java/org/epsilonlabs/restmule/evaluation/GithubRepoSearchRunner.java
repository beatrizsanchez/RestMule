package org.epsilonlabs.restmule.evaluation;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

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

	// (full repo name;file download url), github user email, number of commits
	// for [file download url, github user email] combination
	Table<String, String, Integer> resultTable = HashBasedTable.create();

	String reportPath;
	
	public GithubRepoSearchRunner(String reportPath, String githubUserName, String githubUserPass, String githubQuery) {
		this.searchResultPath = Paths.get(Paths.get(reportPath) + File.separator + SEARCH_RESULT_SUB_DIRECTORY); // output
																													// folder
																													// is
																													// where
																													// the
																													// report
																													// file
																													// has
																													// been
																													// defined
																													// to
																													// be
																													// placed
		this.reportPath= reportPath;
		this.githubUserName = githubUserName;
		this.githubUserPass = githubUserPass;
		this.githubQuery = githubQuery;
	}

	public void setQuery(String githubQuery) {
		this.githubQuery = githubQuery;
	}

	public void runSearch() {
		try {

			Cache cache = new Cache(new File("cache-"+reportPath+"/"), 1000 * 1024 * 1024); // 1GB
																				// cache
			OkHttpClient client = new OkHttpClient().setCache(cache);
			OkUrlFactory urlFactory = new OkUrlFactory(client);
			HttpConnector connector = new ImpatientHttpConnector(new OkHttpConnector(urlFactory),
					DEFAULT_CONNECTION_TIMEOUT_IN_MILLI_SECONDS, DEFAULT_READ_TIMEOUT_IN_MILLI_SECONDS);
			GitHub github = new GitHubBuilder().withPassword(githubUserName, githubUserPass).withConnector(connector)
					.withAbuseLimitHandler(AbuseLimitHandler.WAIT).withRateLimitHandler(RateLimitHandler.WAIT).build();

			PagedSearchIterable<GHContent> initialFiles = github.searchContent().q(githubQuery).list()
					.withPageSize(DEFAULT_PAGE_SIZE);
			totalCount = initialFiles.getTotalCount();
			logger.info("TOTAL RESULT COUNT: " + Integer.toString(totalCount));

			int currResNo = 1;

			HashSet<String> repoNameSet = new HashSet<>();
			// files
			for (GHContent initialResultItem : initialFiles) {
				GHRepository initialResultRepo = initialResultItem.getOwner();
				String initialResultItemFullRepoName = initialResultRepo.getFullName();

				if (!repoNameSet.contains(initialResultItemFullRepoName)) {

					// files in current repo
					PagedSearchIterable<GHContent> repoFiles = github.searchContent().q(githubQuery)
							.repo(initialResultItemFullRepoName).list().withPageSize(DEFAULT_PAGE_SIZE);

					HashSet<String> filePathSet = new HashSet<>();

					for (GHContent resultItem : repoFiles) {

						GHRepository resultRepo = resultItem.getOwner();
						String resultItemPath = java.net.URLDecoder.decode(resultItem.getPath(), "UTF-8");

						if (!filePathSet.contains(resultItemPath)) {

							String resultItemFullRepoName = resultRepo.getFullName();
							String resultItemDownloadUrl = java.net.URLDecoder
									.decode(resultItem.getDownloadUrl(), "UTF-8");

							// commits of file
							for (GHCommit commit : resultRepo.queryCommits().path(resultItemPath).list()) {
								String authorEmail = commit.getCommitShortInfo().getAuthor().getEmail();
								int count = 0;
								String tableKey = resultItemFullRepoName + ";" + resultItemDownloadUrl;
								if (resultTable.get(tableKey, authorEmail) != null) {
									count = resultTable.get(tableKey, authorEmail);
								}
								resultTable.put(tableKey, authorEmail, count + 1);
								++totalCommitCount;
							} // commits of file

							logger.info("ADDED (" + currResNo + " of " + totalCount + "): " + resultItemFullRepoName);
							++currResNo;
						}
					} // files in current repo
				}
			} // files end

			logger.info("TOTAL COMMIT COUNT: " + totalCommitCount);
			logger.info("=============== RESULT TABLE PRINTOUT ===============");
			logger.info(resultTable.toString());
			logger.info("=============== RESULT TABLE PRINTOUT (END) =========");

		} catch (Exception e1) {
			logger.info("Failed to connect to GitHub (bad credentials or timeout).");
			e1.printStackTrace();
		}
	}

}