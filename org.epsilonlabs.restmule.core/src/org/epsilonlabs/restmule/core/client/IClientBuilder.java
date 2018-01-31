package org.epsilonlabs.restmule.core.client;

import org.epsilonlabs.restmule.core.session.ISession;

/**
 * 
 * {@link IClientBuilder}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public interface IClientBuilder<T> {

	T build();
	IClientBuilder<T> setSession(ISession session);
	IClientBuilder<T> setActiveCaching(boolean activeCaching);

}
