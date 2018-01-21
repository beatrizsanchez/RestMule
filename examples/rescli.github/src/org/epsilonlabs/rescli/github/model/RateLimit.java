package org.epsilonlabs.rescli.github.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RateLimit {

	@JsonProperty("rate") 
	private Rate rate;
	
	public Rate getRate() {
		return this.rate;
	}
	
	@Override
	public String toString() {
		return "RateLimit [ "
			+ "rate = " + this.rate + ", "
			+ "]"; 
	}	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Rate {
	
		@JsonProperty("limit") 
		private Integer limit;
		
		@JsonProperty("reset") 
		private Integer reset;
		
		@JsonProperty("remaining") 
		private Integer remaining;
		
		public Integer getLimit() {
			return this.limit;
		}
		
		public Integer getReset() {
			return this.reset;
		}
		
		public Integer getRemaining() {
			return this.remaining;
		}
		
		@Override
		public String toString() {
			return "Rate [ "
				+ "limit = " + this.limit + ", "
				+ "reset = " + this.reset + ", "
				+ "remaining = " + this.remaining + ", "
				+ "]"; 
		}	
	}
	
}	
