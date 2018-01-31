package org.epsilonlabs.restmule.github.test.util;

import java.util.Properties;

import org.epsilonlabs.restmule.core.util.PropertiesUtil;

public class PrivateProperties {

	private static final String GITHUB_PRIVATE_PROPERTIES_FILE = "githubprivate.properties"; 
	
	public static String get(String property){
		Properties properties = PropertiesUtil.load(PrivateProperties.class, GITHUB_PRIVATE_PROPERTIES_FILE);
		return properties.getProperty(property);
	}

	public static boolean exists() {
		return PrivateProperties.class.getClassLoader().getResourceAsStream(GITHUB_PRIVATE_PROPERTIES_FILE) != null;
	}

}
