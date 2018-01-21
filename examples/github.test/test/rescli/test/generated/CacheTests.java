package rescli.test.generated;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.model.Commits;
import org.epsilonlabs.rescli.test.github.query.GitHubTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class CacheTests  extends GitHubTestUtil {

	private static final Logger LOG = LogManager.getLogger(BlockingTests.class);

	private static IGitHubApi api;

	@BeforeClass
	public static void setup(){
		GitHubTestUtil.setup();
		api = GitHubTestUtil.getOAuthClient();
	}
	
	/** Assuming a sufficiently time-consuming request */
	@Test
	public void testResponseTimeDifferenceBetweenNetworkAndCache(){
		// clear cache
		GitHubTestUtil.clearGitHubCache();
		
		AtomicLong firstTimeToComplete = new AtomicLong();
		AtomicLong secondTimeToComplete = new AtomicLong();
		
		final long firstStart = System.currentTimeMillis();
		// Send request to network
		//api.getSearchRepositories("asc", "epsilon", "stars").observe().doOnComplete( () -> {
		api.getReposCommits("epsilonlabs", "emc-json", null, null, null, null, null).observe().doOnComplete( () -> {
			firstTimeToComplete.set(System.currentTimeMillis() - firstStart);
		}).blockingSubscribe();
		
		// Send again but retrieve from cache
		final long secondStart = System.currentTimeMillis();
		//api.getSearchRepositories("asc", "epsilon", "stars").observe().doOnComplete( () -> {
		api.getReposCommits("epsilonlabs", "emc-json", null, null, null, null, null).observe().doOnComplete( () -> {
			secondTimeToComplete.set(System.currentTimeMillis() - secondStart);
		}).blockingSubscribe();
		
		long timeDifference = firstTimeToComplete.get() - secondTimeToComplete.get();
		LOG.info(TimeUnit.MILLISECONDS.toSeconds(timeDifference) + " s of difference");
		assertTrue(timeDifference > 0);

	}
	
}
