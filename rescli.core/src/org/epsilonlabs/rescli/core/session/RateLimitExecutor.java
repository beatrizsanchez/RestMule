package org.epsilonlabs.rescli.core.session;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 
 * {@link RateLimitExecutor}
 * <p>
 * Copyright &copy; 2017 University of York.
 * 
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public class RateLimitExecutor extends ThreadPoolExecutor {

	private static final Logger LOG = LogManager.getLogger(RateLimitExecutor.class);

	private RateLimiter maxRequestsPerSecond;
	private AtomicInteger requestCounter;
	private AtomicInteger dispatchCounter;
	private Class<? extends AbstractSession> sessionClass;
	private String sessionId;
	private long jitter = 100;

	RateLimitExecutor(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId,
			ThreadFactory factory) {
		super(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(), factory); // SINGLE
																						// THREAD
		this.maxRequestsPerSecond = RateLimiter.create(maxRequestsPerSecond);
		this.requestCounter = new AtomicInteger(0);
		this.dispatchCounter = new AtomicInteger(0);
		this.sessionClass = session;
		this.sessionId = sessionId;
	}

	RateLimitExecutor(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId) {
		super(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory()); // SINGLE
																												// THREAD
		this.maxRequestsPerSecond = RateLimiter.create(maxRequestsPerSecond);
		this.requestCounter = new AtomicInteger(0);
		this.dispatchCounter = new AtomicInteger(0);
		this.sessionClass = session;
		this.sessionId = sessionId;
	}

	public static RateLimitExecutor create(int maxRequestsPerSecond, Class<? extends AbstractSession> session,
			String sessionId) {
		if (maxRequestsPerSecond > 30) {
			maxRequestsPerSecond = 30;
		}
		return new RateLimitExecutor(maxRequestsPerSecond, session, sessionId);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		LOG.debug("Before execute");
		maxRequestsPerSecond.acquire();

		IRateLimiter limiter = getLimiter();

		// Wait for first request to return
		while ( ! limiter.isSet().get() && dispatchCounter.get() == 1) {
			LOG.debug("Inside While");
			try {
				LOG.info("UNSET: Sleeping for " + MILLISECONDS.toSeconds(1000) + " s");
				MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
		}

		LOG.info("COUNTER IS " + (limiter.isSet().get() ? "SET" : "UNSET"));

		if (limiter.isSet().get()) {
			LOG.debug("");
			// Adjust request counter
			if (dispatchCounter.get() == 1) {
				LOG.debug("adjust request counter");
				requestCounter.set(getLimiter().getRateLimit() - getLimiter().getRateLimitRemaining().get());
			}
			// Reset counter
			if (requestCounter.get() == getLimiter().getRateLimit()) {
				LOG.debug("Reset counter");
				try {
					long timeout = limiter.getRateLimitResetInMilliSeconds() - System.currentTimeMillis() + jitter;
					LOG.info("Sleeping for " + MILLISECONDS.toSeconds(timeout) + " s");
					MILLISECONDS.sleep(timeout);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				}
				requestCounter.set(0);
			}
		}
	}

	@Override
	public void execute(Runnable command) {
		super.execute(command);
		LOG.debug("Executing");
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		LOG.debug("REQUEST_COUNT: " + requestCounter.incrementAndGet() + ", DISPATCH_COUNT: "
				+ dispatchCounter.incrementAndGet());
	}
	private IRateLimiter getLimiter() {
		return (IRateLimiter) AbstractSession.getSession(sessionClass, sessionId);
	}

}
