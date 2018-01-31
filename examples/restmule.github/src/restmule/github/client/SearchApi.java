package restmule.github.client;

import static restmule.core.util.PropertiesUtil.API_BASE_URL;

import java.util.List;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient.Builder;
import restmule.core.client.AbstractClient;
import restmule.core.client.IClientBuilder;
import restmule.core.data.Data;
import restmule.core.data.IData;
import restmule.core.data.IDataSet;
import restmule.core.session.ISession;
import restmule.core.session.RateLimitExecutor;
import restmule.github.cache.GitHubCacheManager;
import restmule.github.interceptor.GitHubInterceptor;
import restmule.github.model.*;
import restmule.github.page.GitHubPaged;
import restmule.github.page.GitHubPagination;
import restmule.github.session.GitHubSession;
import restmule.github.util.GitHubPropertiesUtil;

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
	
		private ISession session;
		private boolean activeCaching = true;
	
		@Override
		public ISearchApi build() {
			return (ISearchApi) new SearchClient(session, activeCaching);
		}
	
		@Override
		public IClientBuilder<ISearchApi> setSession(ISession session){
			this.session = session;
			return this;
		}
		
		@Override
		public IClientBuilder<ISearchApi> setActiveCaching(boolean activeCaching) {
			this.activeCaching = activeCaching;
			return this;
		}
	
	}
	
	/** CLIENT */
	private static class SearchClient extends AbstractClient<ISearchEndpoint> 
	implements ISearchApi 
	{
		private GitHubPagination paginationPolicy;
		
		SearchClient(ISession session, boolean activeCaching) {
			super();

			ExecutorService executor = RateLimitExecutor.create(30, GitHubSession.class, session.id());
			GitHubInterceptor interceptors = new GitHubInterceptor(session.id());
			String baseurl = GitHubPropertiesUtil.get(API_BASE_URL);

			if (!baseurl.endsWith("/")) baseurl += "/"; // FIXME Validate in Model with EVL 

			Builder clientBuilder = AbstractClient.okHttp(executor);
			
			if (activeCaching) clientBuilder = clientBuilder.cache(GitHubCacheManager.getInstance().getOkHttpCache()); // FIXME Use Lucene Instead
			clientBuilder = clientBuilder.addInterceptor(interceptors.mainInterceptor(activeCaching));
						
			this.client = clientBuilder.build();

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
