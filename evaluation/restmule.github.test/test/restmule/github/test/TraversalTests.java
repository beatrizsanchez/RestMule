package restmule.github.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restmule.github.api.IGitHubApi;
import org.junit.BeforeClass;
import org.junit.Test;

import restmule.github.test.query.GitHubTestUtil;

public class TraversalTests extends GitHubTestUtil {

	private static final String ERROR = "something went wrong";

	private static final Logger LOG = LogManager.getLogger(DataAccessObjectTests.class);

	private static IGitHubApi api;

	@BeforeClass
	public static void setup(){
		GitHubTestUtil.setup();
		api = GitHubTestUtil.getOAuthClient();
	}

	@Test
	public void test() {
		//api.getReposCommits("epsilonlabs", "emc-json", null, null, null, null, null)
		api.getReposCommits("square", "okhttp", null, null, null, null, null)
			.observe()
			.doOnNext(item -> LOG.info(item.getCommit().getAuthorInner()))
			.doOnError(e -> e.printStackTrace())
			.blockingSubscribe();

	}
}
