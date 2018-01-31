package restmule.github.page;

import static restmule.core.util.PropertiesUtil.PAGE_INCREMENT;
import static restmule.core.util.PropertiesUtil.PAGE_LABEL;
import static restmule.core.util.PropertiesUtil.PAGE_MAX_VALUE;
import static restmule.core.util.PropertiesUtil.PAGE_START_VALUE;
import static restmule.core.util.PropertiesUtil.PER_ITERATION_LABEL;
import static restmule.core.util.PropertiesUtil.PER_ITERATION_VALUE;

import io.reactivex.annotations.NonNull;
import restmule.core.data.IDataSet;
import restmule.core.page.AbstractPagination;
import restmule.github.callback.GitHubCallback;
import restmule.github.callback.GitHubWrappedCallback;
import restmule.github.data.GitHubDataSet;
import restmule.github.util.GitHubPropertiesUtil;

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
