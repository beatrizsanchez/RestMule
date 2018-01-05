package org.epsilonlabs.rescli.test.github;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IData;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.api.GitHubApi;
import org.epsilonlabs.rescli.github.api.IGitHub;
import org.epsilonlabs.rescli.github.client.ISearchWrapper;
import org.epsilonlabs.rescli.github.client.SearchApi;
import org.epsilonlabs.rescli.github.model.Repository;
import org.epsilonlabs.rescli.test.github.query.RepositoryQuery;
import org.epsilonlabs.rescli.test.util.MonitorUtil;

public class GitHubTest extends AbstractGitHubTest{

	private static final Logger LOG = LogManager.getLogger(GitHubTest.class);

	public static void prepareClass(){
		setup();
		clearGitHubCache();
	}

	public static void main(String[] args) throws InterruptedException {
		prepareClass();
		//testGitHubV3(sessionBasicA2);
		globalGitHub(sessionBasicA2);
	}

	public static void globalGitHub(String session) throws InterruptedException {
		LOG.info(session);
		IGitHub client = GitHubApi.create()
				.setSession(session)
				.build();

		String query1 = RepositoryQuery.create().keywords("epsilon").getQuery();
		
		IDataSet<Repository> repositories1 = client.searchRepositories(query1, "stars", "desc");
		IData<Repository> repos = client.getRepos("beatrizsanchez", "PRMS");

		MonitorUtil.logResponse(repositories1, "SEARCH 1");
		MonitorUtil.logResponse(repos, "SEARCH 2");
		
		repositories1.observe().subscribe(e-> LOG.info(e.getName()));
		repos.observe().subscribe(e-> LOG.info(e.getName()));
		
		
		LOG.info("finished exec");
	}	
	
	public static void testGitHubV3(String session) {
		LOG.info(session);
		ISearchWrapper client = SearchApi.create()
				.setSession(session)
				.build();

		String query1 = RepositoryQuery.create()
				.keywords("epsilon")
				.getQuery();
		String query2 = RepositoryQuery.create()
				.keywords("http")
				.getQuery();
		String query3 = RepositoryQuery.create()
				.keywords("apache")
				.getQuery();
		String query4 = RepositoryQuery.create()
				.keywords("ruby")
				.getQuery();
		
		IDataSet<Repository> repositories1 = client.searchRepositories(query1, "stars", "desc");
		IDataSet<Repository> repositories2 = client.searchRepositories(query2, "stars", "desc");
		IDataSet<Repository> repositories3 = client.searchRepositories(query3, "stars", "desc");
		IDataSet<Repository> repositories4 = client.searchRepositories(query4, "stars", "desc");

		MonitorUtil.logResponse(repositories1, "SEARCH 1");
		MonitorUtil.logResponse(repositories2, "SEARCH 2");
		MonitorUtil.logResponse(repositories3, "SEARCH 3");
		MonitorUtil.logResponse(repositories4, "SEARCH 4");

		repositories1.observe().subscribe(e-> LOG.info(e.getName()));
		repositories2.observe().subscribe(e-> LOG.info(e.getName()));
		repositories3.observe().subscribe(e-> LOG.info(e.getName()));
		repositories4.observe().subscribe(e-> LOG.info(e.getName()));
		
		LOG.info("finished exec");
		
		
	}	

	

	
}
