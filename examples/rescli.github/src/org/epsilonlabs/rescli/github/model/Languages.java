package org.epsilonlabs.rescli.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Languages {

	@Override
	public String toString() {
		return "Languages [ "
			+ "]"; 
	}	
}	
