package restmule.github.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restmule.github.api.IGitHubApi;
import restmule.github.model.Commits;
import restmule.github.model.Repo;
import restmule.github.model.SearchRepositories;
import restmule.core.data.IData;
import restmule.core.data.IDataSet;
import restmule.core.data.Status;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import restmule.github.test.query.GitHubTestUtil;

public class DataAccessObjectTests extends GitHubTestUtil {

	private static final String ERROR = "something went wrong";

	private static final Logger LOG = LogManager.getLogger(DataAccessObjectTests.class);

	private static IGitHubApi api;

	@BeforeClass
	public static void setup(){
		GitHubTestUtil.setup();
		api = GitHubTestUtil.getOAuthClient();
	}

	@Before
	public void clearCache(){
		//GitHubTestUtil.clearGitHubCache();
	}

	@Test
	public void testSingleObjectReturnType() {
		IData<Repo> repo = api.getReposRepoByRepo("epsilonlabs", "emc-json");
		repo.observe()
			.doOnNext(r -> {
				assertNotNull(r);	
			})
			.doOnError(e -> fail(ERROR))
			.doOnComplete(() -> assertEquals(Status.COMPLETED, repo.status()))
			.blockingSubscribe();
	}
	
	@Test
	public void testDataSetFromArray() {
		IDataSet<Commits> reposCommits = api.getReposCommits("epsilonlabs", "emc-json", null, null, null, null, null);
		assertEquals(Status.CREATED, reposCommits.status());
		
		reposCommits
			.observe()
			.doOnNext(commits -> {
				assertNotNull(commits);
				assertTrue(reposCommits.status() == Status.ADDING || reposCommits.status() == Status.AWAITING);	
			})
			.doOnError(e -> fail(ERROR))
			.doOnComplete(() -> assertEquals(Status.COMPLETED, reposCommits.status()))
			.blockingSubscribe();
		
		Long count = reposCommits.observe().count().blockingGet();
		assertTrue(count > 0);
	}
	
	@Test
	public void testDataSetFromWapper() {
		IDataSet<SearchRepositories> searchRepositories = api.getSearchRepositories("asc", "epsilon", "stars");
		assertEquals(Status.CREATED, searchRepositories.status());
		
		searchRepositories.observe()
			.doOnNext(repo -> {
				assertNotNull(repo);
				assertTrue(searchRepositories.status() == Status.ADDING || searchRepositories.status() == Status.AWAITING);	
			})
			.doOnError(e -> fail(ERROR))
			.doOnComplete(() -> assertEquals(Status.COMPLETED, searchRepositories.status()))
			.blockingSubscribe();
	
		Long count = searchRepositories.observe().count().blockingGet();
		assertTrue(count > 0);
	}

}
