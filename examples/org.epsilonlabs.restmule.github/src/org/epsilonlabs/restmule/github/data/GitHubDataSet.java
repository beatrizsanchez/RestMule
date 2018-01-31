package org.epsilonlabs.restmule.github.data;

import org.epsilonlabs.restmule.core.data.AbstractDataSet;
import org.epsilonlabs.restmule.github.page.GitHubPagination;

public class GitHubDataSet<T> extends AbstractDataSet<T> {

	public GitHubDataSet(){
		super(GitHubPagination.get());
	}
	
}
