package org.epsilonlabs.restmule.github.interceptor;

import static org.epsilonlabs.restmule.core.util.PropertiesUtil.ACCEPT;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.RATE_LIMIT_LIMIT;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.RATE_LIMIT_REMAINING;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.RATE_LIMIT_RESET;
import static org.epsilonlabs.restmule.core.util.PropertiesUtil.USER_AGENT;

import org.epsilonlabs.restmule.core.interceptor.AbstractInterceptor;
import org.epsilonlabs.restmule.github.session.GitHubSession;
import org.epsilonlabs.restmule.github.util.GitHubPropertiesUtil;

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
	}

	public Interceptor mainInterceptor(boolean activeCaching){
		return mainInterceptor(activeCaching, userAgent, accept, sessionId, headerLimit, headerRemaining, headerReset);
	}
	
}