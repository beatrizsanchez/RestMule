package org.epsilonlabs.rescli.core.page;

/**
 * 
 * {@link IWrap}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public interface IWrap<T> extends IPage<T>{

	Integer getTotalCount();
	Boolean isIncomplete();
	
}
