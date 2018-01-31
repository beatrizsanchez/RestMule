package org.epsilonlabs.restmule.github.cache;

import org.epsilonlabs.restmule.core.cache.AbstractCacheManager;
import org.epsilonlabs.restmule.core.cache.ICache;

public class GitHubCacheManager extends AbstractCacheManager {

	private static final String AGENT_NAME = "github"; 

	private static GitHubCacheManager agent;

	public static ICache getInstance() {
		if (GitHubCacheManager.agent == null){
			GitHubCacheManager.agent = new GitHubCacheManager();
		} 
		return GitHubCacheManager.agent;
	}

	private GitHubCacheManager() { 
		super(AGENT_NAME);
	}
}
