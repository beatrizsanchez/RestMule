package org.epsilonlabs.rescli.github.test;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.model.Commits;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MDEAnalysis extends GitHubTestUtil{

	private static final Logger LOG = LogManager.getLogger(MDEAnalysis.class);

	private static CloseableHttpAsyncClient asyncClient;
	private static IGitHubApi api;

	@BeforeClass
	public static void prepareClass(){
		setup();
		clearGitHubCache();
		api = GitHubTestUtil.getOAuthClient();
		asyncClient = HttpAsyncClients.createDefault();	
		 
		LOG.info("DAEMON? " + Thread.currentThread().isDaemon());
		//Thread.currentThread().setDaemon(!Thread.currentThread().isDaemon());
	}

	@Test
	public void testMDETech() {

		asyncClient.start();
		PublishSubject<SearchCodeResult> results = PublishSubject.create();
		results.doOnComplete(() -> asyncClient.close());

		MDE lastMDE = MDE.values()[MDE.values().length - 1];

		//for (MDE e : MDE.values()){
		MDE e = MDE.ATL;
		boolean last = lastMDE == e;

		SearchCodeResult result = new SearchCodeResult(e);

		IDataSet<SearchCode> searchCode = api.getSearchCode("asc", e.query() , null);
		searchCode.observe()
		.doOnError(error -> LOG.error(error.getMessage()))
		.doOnComplete(() -> {
			results.onNext(result.getFor(searchCode));//, api, asyncClient));
			if (last) { results.onComplete(); }
		})
		.subscribeOn(Schedulers.io()).subscribe();
		//}
		results.blockingSubscribe(
				next ->  LOG.info(next),
				error -> LOG.error(error)
				);

		/*
		searchCode.observe()
		.doOnNext( s -> Utils.isValid(s.getHtmlUrl(), e.getKeyword(), asyncClient))
		.map( html -> html.getRepository())
		//.doOnNext( x -> LOG.info("SNIPPET MAP: " +x.getHtmlUrl()))
		.map((repo) -> {
			LOG.info("FLAT MAP: " + repo.getCommitsUrl());
			return api.getReposCommits(repo.getOwner().getLogin(), repo.getName(), null, null, null, null, null)
					.observe();
			//client.getReposGitTreesTreeByShaCode(repo.getOwner(), repo.getName(), repo., recursive);
		}).flatMap(a -> a.iterator().next())
		.doOnNext( commit -> {
			LOG.info("COMMIT DATE: " + commit.getCommit().getCommitterInner().getDate());
			LOG.info("COMMIT SHA: " + commit.getSha());
		});
		*/
		
		
		/*.map(a -> a.getCommit())
		.subscribe(
				a-> LOG.info(),
				er -> LOG.info(er.getMessage()),
				() -> LOG.info("complete")
				);*/
	}



}
