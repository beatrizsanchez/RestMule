package org.epsilonlabs.restmule.core.data;

/**
 * 
 * {@link IDataSet}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public interface IDataSet<T> extends IData<T> {

	Integer total(); 
	Integer percentage();
	Integer count();
	String id();

}
