package restmule.github.interceptor;

import static restmule.core.util.PropertiesUtil.ACCEPT;
import static restmule.core.util.PropertiesUtil.RATE_LIMIT_LIMIT;
import static restmule.core.util.PropertiesUtil.RATE_LIMIT_REMAINING;
import static restmule.core.util.PropertiesUtil.RATE_LIMIT_RESET;
import static restmule.core.util.PropertiesUtil.USER_AGENT;

import okhttp3.Interceptor;
import restmule.core.interceptor.AbstractInterceptor;
import restmule.github.session.GitHubSession;
import restmule.github.util.GitHubPropertiesUtil;

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