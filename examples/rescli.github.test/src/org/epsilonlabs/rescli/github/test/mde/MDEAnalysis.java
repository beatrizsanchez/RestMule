package org.epsilonlabs.rescli.github.test.mde;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class MDEAnalysis extends GitHubTestUtil {

	private static final Logger LOG = LogManager.getLogger(MDEAnalysis.class);

	private static CloseableHttpAsyncClient asyncClient;
	private static IGitHubApi localApi;

	@BeforeClass
	public static void prepareClass() {
		setup();
		clearGitHubCache();

		localApi = GitHubTestUtil.getOAuthClient();
		asyncClient = HttpAsyncClients.createDefault();

		LOG.info("DAEMON? " + Thread.currentThread().isDaemon());
		// Thread.currentThread().setDaemon(!Thread.currentThread().isDaemon());
	}

	@Test
	public void testMDETech() throws InterruptedException {

		for (MDE mde : MDE.values()) {
			String query = mde.query();
			LOG.info(query);
			IDataSet<SearchCode> searchCode = localApi.getSearchCode("asc", query, null);

			// Queues
			FileToRepo f2r = new FileToRepo();
			RepoToFile r2f = new RepoToFile(mde);
			FileToCommits f2c = new FileToCommits();

			// Subscriptions
			searchCode.observe().subscribe(f2r);
			f2r.repos().subscribe(r2f);
			r2f.files().subscribe(f2c);

			// Logging
			RepoAndFileDataConsumer rfdc = new RepoAndFileDataConsumer();
			CommitDataConsumer cdc = new CommitDataConsumer();
			// searchCode.observe().subscribe(out);
			// f2r.repos().subscribe(out);
			// r2f.files().subscribe(out);
			r2f.files().subscribe(rfdc);
			f2c.commits().subscribe(cdc);

			// searchCode.observe().blockingSubscribe();
			
			//keep thread alive without forcing blocking etc.
			Thread.sleep(10000);

			rfdc.dumpData();
			cdc.dumpData();
			
			// Initializing
			// f2r.repos().doOnNext(out -> LOG.info("repo:
			// "+out.getFullName()));
			// r2f.files().doOnNext(out -> LOG.info("file: "+out.getName()));

		}

	}
}
