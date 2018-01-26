package org.epsilonlabs.rescli.github.test;

import static org.junit.Assert.assertTrue;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.test.mde.MDE;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;

import io.reactivex.schedulers.Schedulers;

@FixMethodOrder(NAME_ASCENDING)
public class RateLimitAndPaginationTests extends GitHubTestUtil {

	private static final Logger LOG = LogManager.getLogger(RateLimitAndPaginationTests.class);

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
	
	@Test
	public void testPagedRequestForWrapped() {
		// Knowing that 100 elements per endpoint requests are provided
		Long count = api.getSearchRepositories("asc", "epsilon", "stars").observe().count().blockingGet();
		assertTrue(count > 100);
	}
	
	@Test
	public void testPagedRequestForList() {
		// Knowing that 100 elements per endpoint requests are provided
		Long count = api.getReposCommits("epsilonlabs", "emc-json", null, null, null, null, null).observe().count().blockingGet();
		assertTrue(count > 100);
	}
	
	/** Expected Limit For Search = 30 */  
	@Test
	@Ignore
	public void testRequestsThatExcedTheRequestLimit() throws InterruptedException {
		int expectedNumberOfQueries = MDE.values().length;
		AtomicInteger count = new AtomicInteger(0);
		AtomicLong elements = new AtomicLong(0);
		for (MDE m : MDE.values()){
			elements.addAndGet(api.getSearchCode("asc", m.query() , null).observe()
				.doOnError(e -> { throw new Exception(e.getMessage());})
				.doOnComplete(() -> count.incrementAndGet())
				.subscribeOn(Schedulers.io()).count().blockingGet());
		}
		while(count.get() != expectedNumberOfQueries){
			TimeUnit.SECONDS.sleep(3);
		}
		assertTrue(count.get() == expectedNumberOfQueries);
		LOG.info("retrieved " + elements.get() + " elements");
		
	}

}
