package org.epsilonlabs.rescli.github.client;

import static org.epsilonlabs.rescli.core.util.PropertiesUtil.API_BASE_URL;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.epsilonlabs.rescli.core.client.AbstractClient;
import org.epsilonlabs.rescli.core.client.IClientBuilder;
import org.epsilonlabs.rescli.core.data.IData;
import org.epsilonlabs.rescli.core.data.Data;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.core.session.ISession;
import org.epsilonlabs.rescli.core.session.RateLimitExecutor;
import org.epsilonlabs.rescli.github.cache.GitHubCacheManager;
import org.epsilonlabs.rescli.github.interceptor.GitHubInterceptor;
import org.epsilonlabs.rescli.github.model.*;
import org.epsilonlabs.rescli.github.page.GitHubPaged;
import org.epsilonlabs.rescli.github.page.GitHubPagination;
import org.epsilonlabs.rescli.github.session.GitHubSession;
import org.epsilonlabs.rescli.github.util.GitHubPropertiesUtil;

public class SearchApi  {

	public static SearchBuilder create(){
		return new SearchBuilder(); 
	}
	
	public static ISearchApi createDefault(){ 
		return new SearchBuilder().setSession(GitHubSession.createPublic()).build(); 
	}
	
	/** BUILDER */
	public static class SearchBuilder 
	implements IClientBuilder<ISearchApi> { 
	
		private String sessionId;
	
		@Override
		public ISearchApi build() {
			return (ISearchApi) new SearchClient(sessionId);
		}
	
		@Override
		public IClientBuilder<ISearchApi> setSession(String sessionId){
			this.sessionId = sessionId;
			return this;
		}
	
		@Override
		public IClientBuilder<ISearchApi> setSession(ISession session){
			this.sessionId = session.id();
			return this;
		}
	}
	
	/** CLIENT */
	private static class SearchClient extends AbstractClient<ISearchEndpoint> 
	implements ISearchApi 
	{
		private GitHubPagination paginationPolicy;
		
		SearchClient(String sessionId) {
			super();

			ExecutorService executor = RateLimitExecutor.create(30, GitHubSession.class, sessionId);
			GitHubInterceptor interceptors = new GitHubInterceptor(sessionId);
			String baseurl = GitHubPropertiesUtil.get(API_BASE_URL);

			if (!baseurl.endsWith("/")) baseurl += "/"; // FIXME Validate in Model with EVL 

			this.client = AbstractClient.okHttp(executor)
					.addInterceptor(interceptors.cacheRequestInterceptor())
					.addInterceptor(interceptors.requestInterceptor())
					.addNetworkInterceptor(interceptors.sessionResponseInterceptor())
					.addNetworkInterceptor(interceptors.cacheResponseInterceptor())
					.cache(GitHubCacheManager.getInstance().getOkHttpCache()) // FIXME Use Lucene Instead
					.build();

			this.callbackEndpoint = AbstractClient.retrofit(client, baseurl).create(ISearchEndpoint.class);
			this.paginationPolicy = GitHubPagination.get();
		}

		/** WRAPED METHODS FOR PAGINATION */
	
		@Override
		public IDataSet<SearchUsers> getSearchUsers(String order, String q, String sort){
			Class<?>[] types = { String.class, String.class, String.class};
			Object[] vals = { order, q, sort};
			return paginationPolicy.<SearchUsers, GitHubPaged<SearchUsers>, ISearchEndpoint> 
				traverse("getSearchUsers", types, vals, callbackEndpoint);
		}
		
		@Override
		public IDataSet<SearchCode> getSearchCode(String order, String q, String sort){
			Class<?>[] types = { String.class, String.class, String.class};
			Object[] vals = { order, q, sort};
			return paginationPolicy.<SearchCode, GitHubPaged<SearchCode>, ISearchEndpoint> 
				traverse("getSearchCode", types, vals, callbackEndpoint);
		}
		
		@Override
		public IDataSet<SearchIssues> getSearchIssues(String order, String q, String sort){
			Class<?>[] types = { String.class, String.class, String.class};
			Object[] vals = { order, q, sort};
			return paginationPolicy.<SearchIssues, GitHubPaged<SearchIssues>, ISearchEndpoint> 
				traverse("getSearchIssues", types, vals, callbackEndpoint);
		}
		
		@Override
		public IDataSet<SearchRepositories> getSearchRepositories(String order, String q, String sort){
			Class<?>[] types = { String.class, String.class, String.class};
			Object[] vals = { order, q, sort};
			return paginationPolicy.<SearchRepositories, GitHubPaged<SearchRepositories>, ISearchEndpoint> 
				traverse("getSearchRepositories", types, vals, callbackEndpoint);
		}
		
		
	}
}
