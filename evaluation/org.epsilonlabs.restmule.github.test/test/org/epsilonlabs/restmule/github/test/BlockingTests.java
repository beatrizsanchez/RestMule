package org.epsilonlabs.restmule.github.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.restmule.github.api.IGitHubApi;
import org.epsilonlabs.restmule.github.model.SearchRepositories;
import org.epsilonlabs.restmule.core.data.IDataSet;
import org.epsilonlabs.restmule.core.data.Status;
import org.epsilonlabs.restmule.github.test.query.GitHubTestUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class BlockingTests extends GitHubTestUtil {

	private static final Logger LOG = LogManager.getLogger(BlockingTests.class);

	private static IGitHubApi api;

	@BeforeClass
	public static void setup(){
		GitHubTestUtil.setup();
		api = GitHubTestUtil.getOAuthClient();
	}

	@Before
	public void clearCache(){
		GitHubTestUtil.clearGitHubCache();
	}

	@Test @Ignore
	public void testBackgroundExecution() throws InterruptedException {
		IDataSet<SearchRepositories> searchRepositories = api.getSearchRepositories("asc", "epsilon", "stars");
		assertEquals(Status.CREATED, searchRepositories.status());

		// Waiting to see if it has not changed of status
		TimeUnit.SECONDS.sleep(2);
		assertEquals(Status.CREATED, searchRepositories.status());

		// Blocking Subscribe
		searchRepositories.observe().doOnNext(next -> {
			// verify is active
			assertTrue(searchRepositories.status() == Status.ADDING || searchRepositories.status() == Status.AWAITING);	
		}).doOnComplete(()->{
			// status = completed
			assertEquals(Status.COMPLETED, searchRepositories.status());
		}).blockingSubscribe();

	}

	@Test
	public void testReplaySubject(){
		// clear cache
		GitHubTestUtil.clearGitHubCache();

		AtomicLong firstTimeToComplete = new AtomicLong();
		AtomicLong secondTimeToComplete = new AtomicLong();

		final long firstStart = System.currentTimeMillis();
		IDataSet<SearchRepositories> searchRepositories = api.getSearchRepositories("asc", "epsilon", "stars");

		// Execute request
		searchRepositories.observe().doOnComplete( () -> {
			firstTimeToComplete.set(System.currentTimeMillis() - firstStart);
		}).blockingSubscribe();
		
		Long firstCount = searchRepositories.observe().doOnComplete( () -> {
			firstTimeToComplete.set(System.currentTimeMillis() - firstStart);
		}).count().blockingGet();
		
		final long secondStart = System.currentTimeMillis();
		Long secondCount = searchRepositories.observe().doOnComplete( () -> {
			firstTimeToComplete.set(System.currentTimeMillis() - secondStart);
		}).count().blockingGet();
		
		long timeDifference = firstTimeToComplete.get() - secondTimeToComplete.get();
		LOG.info(TimeUnit.MILLISECONDS.toSeconds(timeDifference) + " s of difference");
		assertTrue(firstCount.equals(secondCount));
		assertTrue(timeDifference > 0);

	}

}
