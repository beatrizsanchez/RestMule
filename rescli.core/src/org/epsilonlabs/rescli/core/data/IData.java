package org.epsilonlabs.rescli.core.data;

import io.reactivex.Observable;

/**
 * 
 * {@link IData}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public interface IData<T> {

	Status status();
	Observable<T> observe();
}
