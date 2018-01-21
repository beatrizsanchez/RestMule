package org.epsilonlabs.rescli.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

	@JsonProperty("body") 
	private String body;
	
	public String getBody() {
		return this.body;
	}
	
	@Override
	public String toString() {
		return "Comment [ "
			+ "body = " + this.body + ", "
			+ "]"; 
	}	
}	
