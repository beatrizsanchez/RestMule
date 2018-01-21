package org.epsilonlabs.rescli.github.data;

import org.epsilonlabs.rescli.core.data.AbstractDataSet;
import org.epsilonlabs.rescli.github.page.GitHubPagination;

public class GitHubDataSet<T> extends AbstractDataSet<T> {

	public GitHubDataSet(){
		super(GitHubPagination.get());
	}
	
}
