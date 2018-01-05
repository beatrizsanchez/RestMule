package org.epsilonlabs.rescli.test.github;

import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PASSWORD;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.PERSONAL_ACCESS_TOKEN;
import static org.epsilonlabs.rescli.core.util.PropertiesUtil.USERNAME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.github.cache.GitHubCacheManager;
import org.epsilonlabs.rescli.github.session.GitHubSession;
import org.epsilonlabs.rescli.test.util.PrivateProperties;

public class AbstractGitHubTest {

	private static final Logger LOG = LogManager.getLogger(AbstractGitHubTest.class);
	
	private static String tokenA1;
	private static String usernameA2;
	private static String passwordA2;
	protected static String sessionPublic;
	protected static String sessionBasicA1;
	protected static String sessionBasicA2;
	
	protected static void setup(){
		LOG.info("setting up properties");
		tokenA1 = PrivateProperties.get(PERSONAL_ACCESS_TOKEN);
		usernameA2 = PrivateProperties.get(USERNAME);
		passwordA2 = PrivateProperties.get(PASSWORD);
		sessionPublic = GitHubSession.createPublic();
		sessionBasicA1 = GitHubSession.createWithBasicAuth(tokenA1);
		sessionBasicA2 = GitHubSession.createWithBasicAuth(usernameA2, passwordA2);
	}
	
	static void clearGitHubCache(){
		LOG.info("Clearing cache");
		GitHubCacheManager.getInstance().clear();
	}	
	
}
