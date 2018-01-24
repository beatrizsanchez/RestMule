package org.epsilonlabs.rescli.github.test.mde;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.model.Commits;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.model.SearchCode.Repository;
import org.epsilonlabs.rescli.github.test.SearchCodeResult;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

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
	public void testMDETech() {

		for (MDE mde : MDE.values()) {
			String query = mde.query();
			LOG.info(query);
			IDataSet<SearchCode> searchCode = localApi.getSearchCode("asc", query, null);

			// Queues
			FileToRepo f2r = new FileToRepo();
			RepoToFile r2f = new RepoToFile(mde);

			// Subscriptions
			searchCode.observe().subscribe(f2r);
			f2r.repos().subscribe(r2f);
			//...
			
			// Logging
			ConsoleOutput out = new ConsoleOutput();
			searchCode.observe().subscribe(out);
			f2r.repos().subscribe(out);
			r2f.files().subscribe(out);
			
			searchCode.observe().blockingSubscribe();
			
			// Initializing
			//f2r.repos().doOnNext(out -> LOG.info("repo: "+out.getFullName()));
			//r2f.files().doOnNext(out -> LOG.info("file: "+out.getName()));
			
		}

	}
}
