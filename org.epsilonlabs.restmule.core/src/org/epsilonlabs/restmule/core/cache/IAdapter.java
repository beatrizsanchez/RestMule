package org.epsilonlabs.restmule.core.cache;

/**
 * 
 * {@link IAdapter}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public interface IAdapter<T> {

	T response();
	String body();
}
