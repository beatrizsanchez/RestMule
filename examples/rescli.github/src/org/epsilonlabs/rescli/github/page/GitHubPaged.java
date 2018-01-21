package org.epsilonlabs.rescli.github.page;

import java.util.ArrayList;
import java.util.List;

import org.epsilonlabs.rescli.core.page.IWrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Page Wrapper */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubPaged<T> implements IWrap<T> {

	/** Wrapper Properties */
	@JsonProperty("items")
	private List<T> items = new ArrayList<T>();

	@JsonProperty("total_count")
	private Integer totalCount;

	@JsonProperty("incomplete_results")
	private Boolean incompleteResults;

	/** Wrapper Property Getters */
	
	@Override
	public Boolean isIncomplete() {
		return incompleteResults;
	}

	@Override
	public List<T> getItems() {
		return items;
	}

	@Override
	public Integer getTotalCount() {
		return totalCount;
	}

}