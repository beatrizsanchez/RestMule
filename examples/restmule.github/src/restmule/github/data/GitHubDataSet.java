package restmule.github.data;

import restmule.core.data.AbstractDataSet;
import restmule.github.page.GitHubPagination;

public class GitHubDataSet<T> extends AbstractDataSet<T> {

	public GitHubDataSet(){
		super(GitHubPagination.get());
	}
	
}
