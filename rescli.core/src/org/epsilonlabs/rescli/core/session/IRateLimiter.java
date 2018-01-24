package org.epsilonlabs.rescli.core.session;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * {@link IRateLimiter}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public interface IRateLimiter {

	Integer getRateLimit();
	
	AtomicInteger getRateLimitRemaining();
	
	long getRateLimitResetInMilliSeconds();
	
	Date getRateLimitReset();
	
	AtomicBoolean isSet();

	
}
