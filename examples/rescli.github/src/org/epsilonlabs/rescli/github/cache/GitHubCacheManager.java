package org.epsilonlabs.rescli.github.cache;

import org.epsilonlabs.rescli.core.cache.AbstractCacheManager;
import org.epsilonlabs.rescli.core.cache.ICache;

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
