package org.epsilonlabs.restmule.github.page;

import static org.epsilonlabs.restmule.core.util.PropertiesUtil.PAGE_INCREMENT;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.PAGE_LABEL;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.PAGE_MAX_VALUE;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.PAGE_START_VALUE;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.PER_ITERATION_LABEL;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.PER_ITERATION_VALUE;

import org.epsilonlabs.restmule.core.data.IDataSet;
import org.epsilonlabs.restmule.core.page.AbstractPagination;
import org.epsilonlabs.restmule.github.callback.GitHubCallback;
import org.epsilonlabs.restmule.github.callback.GitHubWrappedCallback;
import org.epsilonlabs.restmule.github.data.GitHubDataSet;
import org.epsilonlabs.restmule.github.util.GitHubPropertiesUtil;

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
		return super.<T, WRAP, END, GitHubDataSet<T>, GitHubWrappedCallback>
		traverse(new GitHubWrappedCallback<T, WRAP>(), methodName, types, vals, client);
	}
	
	public <T, END> IDataSet<T> traverseList(
			@NonNull String methodName, 
			@NonNull Class<?>[] types, 
			@NonNull Object[] vals, 
			@NonNull END client)
	{
		return super.<T, END, GitHubDataSet<T>, GitHubCallback<T>>
		traversePages(new GitHubCallback<T>(), methodName, types, vals, client);		
	}

}
