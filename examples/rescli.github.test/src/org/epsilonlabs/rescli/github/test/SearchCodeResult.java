package org.epsilonlabs.rescli.github.test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.model.Commits;
import org.epsilonlabs.rescli.github.model.Commits.Author;
import org.epsilonlabs.rescli.github.model.Commits.Committer;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.model.SearchCode.Repository;
import org.epsilonlabs.rescli.github.test.mde.MDE;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;

public class SearchCodeResult {

	private static final Logger LOG = LogManager.getLogger(SearchCodeResult.class);

	MDE type;
	IDataSet<SearchCode> dataset;

	Integer totalItems;
	Integer totalResults;
	long totalRepos;
	long totalDevelopers;
	
	private List<Commits> commits;

	public SearchCodeResult(MDE type){
		this.type = type;
	}

	public SearchCodeResult getFor(IDataSet<SearchCode> dataset){
		this.dataset = dataset;
		getTotalItems();
		getTotalRepos();
		getTotalResults();
		//getTotalDevelopers(client);
		return this;
	}

	public void getTotalItems(){
		this.totalItems = dataset.count();
	}

	public void getTotalResults(){
		this.totalResults = dataset.total();
	}

	public void getTotalRepos(){
		this.totalRepos = dataset.observe().distinct( item -> item.getRepository().getId() ).count().blockingGet(); 
	}
	public void getCommits(String owner, String repo, String path){
		this.commits = GitHubTestUtil.getPublicClient()
				.getReposCommits(owner, repo, null, null, path, null, null)
				.observe().toList().blockingGet();
	}

	public void getTotalDevelopers(IGitHubApi client){
		this.totalDevelopers = dataset.observe()
				.distinct( item -> item.getPath())
				.map( e -> {
					Repository r = e.getRepository();
					return client
							.getReposCommits(r.getOwner().getLogin(), r.getFullName(), null, null, e.getPath(), null, null)
							.observe()
							//.doOnError(error -> LOG.info(error.getMessage()))
							//.doOnNext(n -> LOG.info("NEXT " + n.getAuthor().getId()))
							.map(commit -> commit.getCommitter().getLogin());
				})
				.distinct().count()
				.doOnError(onError -> {
					onError.printStackTrace();
				}).blockingGet();
	}
	
	@Override
	public String toString() {
		return "Results for " + type + ":\n"
				+ "-> total items: " + totalItems + ",\n"
				+ "-> total results: " + totalResults+ ",\n"
				+ "-> total repositories: " + totalRepos+ ",\n"
				+ "-> total developers=" + totalDevelopers ;
	}

}