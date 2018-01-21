package org.epsilonlabs.rescli.github.page;

import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PAGE_INCREMENT;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PAGE_LABEL;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PAGE_MAX_VALUE;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PAGE_START_VALUE;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PER_ITERATION_LABEL;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PER_ITERATION_VALUE;

import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.core.page.AbstractPagination;
import org.epsilonlabs.rescli.github.callback.GitHubCallback;
import org.epsilonlabs.rescli.github.data.GitHubDataSet;
import org.epsilonlabs.rescli.github.util.GitHubPropertiesUtil;

import io.reactivex.annotations.NonNull;

public class GitHubPagination extends AbstractPagination{

	private static GitHubPagination instance;

	public static GitHubPagination get(){
		if (instance == null){
			instance = new GitHubPagination();
		}
		return instance;
	}

	private GitHubPagination() {
		super(	GitHubPropertiesUtil.get(PAGE_LABEL),
				GitHubPropertiesUtil.get(PER_ITERATION_LABEL), 
				Integer.valueOf(GitHubPropertiesUtil.get(PER_ITERATION_VALUE)),
				Integer.valueOf(GitHubPropertiesUtil.get(PAGE_MAX_VALUE)), 
				Integer.valueOf(GitHubPropertiesUtil.get(PAGE_START_VALUE)),
				Integer.valueOf(GitHubPropertiesUtil.get(PAGE_INCREMENT)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T, WRAP extends GitHubPaged<T>, END> IDataSet<T> traverse(
			@NonNull String methodName, 
			@NonNull Class<?>[] types, 
			@NonNull Object[] vals, 
			@NonNull END client)
	{
		return super.<T, WRAP, END, GitHubDataSet<T>, GitHubCallback>
		traverse(new GitHubCallback<T, WRAP>(), methodName, types, vals, client);
	}

}
