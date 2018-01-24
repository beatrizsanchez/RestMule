package org.epsilonlabs.rescli.github.interceptor;

import static org.epsilonlabs.rescli.core.util.PropertiesUtil.ACCEPT;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.RATE_LIMIT_LIMIT;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.RATE_LIMIT_REMAINING;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.RATE_LIMIT_RESET;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.USER_AGENT;

import org.epsilonlabs.rescli.core.interceptor.AbstractInterceptor;
import org.epsilonlabs.rescli.github.cache.GitHubCacheManager;
import org.epsilonlabs.rescli.github.session.GitHubSession;
import org.epsilonlabs.rescli.github.util.GitHubPropertiesUtil;

import okhttp3.Interceptor;

public class GitHubInterceptor extends AbstractInterceptor {
	
	public GitHubInterceptor(String session){
		this.sessionId = session;
	}

	static {
		sessionClass = GitHubSession.class;
		headerLimit = GitHubPropertiesUtil.get(RATE_LIMIT_LIMIT);
		headerRemaining = GitHubPropertiesUtil.get(RATE_LIMIT_REMAINING);
		headerReset = GitHubPropertiesUtil.get(RATE_LIMIT_RESET);
		userAgent = GitHubPropertiesUtil.get(USER_AGENT);
		accept = GitHubPropertiesUtil.get(ACCEPT);
		cache = GitHubCacheManager.getInstance();
	}

	public Interceptor requestInterceptor(){
		return sessionRequestInterceptor(userAgent, accept, sessionId);
	}
	
	public Interceptor sessionResponseInterceptor(){
		return sessionResponseInterceptor(headerLimit, headerRemaining, headerReset, sessionId);
	}

	public Interceptor cacheRequestInterceptor(){
		return cacheRequestInterceptor(cache, sessionId);
	}
	
	public Interceptor cacheResponseInterceptor(){
		return cacheResponseInterceptor(cache, sessionId); 
	}
	
}