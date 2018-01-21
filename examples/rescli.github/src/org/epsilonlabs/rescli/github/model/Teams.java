package org.epsilonlabs.rescli.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Teams {

	@JsonProperty("name") 
	private String name;
	
	@JsonProperty("id") 
	private Integer id;
	
	@JsonProperty("url") 
	private String url;
	
	public String getName() {
		return this.name;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	@Override
	public String toString() {
		return "Teams [ "
			+ "name = " + this.name + ", "
			+ "id = " + this.id + ", "
			+ "url = " + this.url + ", "
			+ "]"; 
	}	
}	
