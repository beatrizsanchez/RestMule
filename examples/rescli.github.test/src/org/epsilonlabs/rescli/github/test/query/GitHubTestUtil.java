package org.epsilonlabs.rescli.github.test.query;

import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PASSWORD;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PERSONAL_ACCESS_TOKEN;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.USERNAME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.session.ISession;
import org.epsilonlabs.rescli.github.api.GitHubApi;
import org.epsilonlabs.rescli.github.api.IGitHubApi;
import org.epsilonlabs.rescli.github.cache.GitHubCacheManager;
import org.epsilonlabs.rescli.github.session.GitHubSession;
import org.epsilonlabs.rescli.github.test.util.PrivateProperties;

public class GitHubTestUtil {

	private static final Logger LOG = LogManager.getLogger(GitHubTestUtil.class);
	
	private static String token;
	private static String username;
	private static String password;
	
	private static IGitHubApi publicApi;
	private static IGitHubApi basicApi;
	private static IGitHubApi oauthApi;
	
	protected static ISession publicSession;
	protected static ISession OAuthSessionWithToken;
	protected static ISession basicSession;
	
	protected static void setup(){
		LOG.info("setting up properties");
		
		if(PrivateProperties.exists()){
		token = PrivateProperties.get(PERSONAL_ACCESS_TOKEN);
		username = PrivateProperties.get(USERNAME);
		password = PrivateProperties.get(PASSWORD);
		OAuthSessionWithToken = GitHubSession.createWithBasicAuth(username, token);
		basicSession = GitHubSession.createWithBasicAuth(username, password);
		}
		
		publicSession = GitHubSession.createPublic();

	}
	
	protected static void clearGitHubCache(){
		GitHubCacheManager.getInstance().clear();
	}
	
	public static IGitHubApi getOAuthClient(){
		if (OAuthSessionWithToken != null && oauthApi == null)
			oauthApi = GitHubApi.create().setSession(OAuthSessionWithToken).build();
		else if(oauthApi == null) 
			oauthApi = GitHubApi.create().setSession(publicSession).build();
		return oauthApi;
	}	
	
}
