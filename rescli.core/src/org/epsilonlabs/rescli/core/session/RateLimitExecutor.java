package org.epsilonlabs.rescli.core.session;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
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
	private AtomicBoolean awaiting;
	private Class<? extends AbstractSession> sessionClass;
	private String sessionId;

	private long jitter = 100;
	private String id;

	RateLimitExecutor(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId,
			ThreadFactory factory) {
		super(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(), factory); // SINGLE
		setup(maxRequestsPerSecond, session, sessionId);
	}

	RateLimitExecutor(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId) {
		super(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory()); // SINGLE
																												// THREAD
		setup(maxRequestsPerSecond, session, sessionId);
	}

	private void setup(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId) {
		this.id = UUID.randomUUID().toString();
		this.maxRequestsPerSecond = RateLimiter.create(maxRequestsPerSecond);
		this.requestCounter = new AtomicInteger(0);
		this.dispatchCounter = new AtomicInteger(0);
		this.awaiting = new AtomicBoolean(false);
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
	public void execute(Runnable command) {
		LOG.info("ENTERING EXECUTE");
		maxRequestsPerSecond.acquire();
		
		// Wait for first request to return
		while (!getLimiter().isSet().get() && dispatchCounter.get() == 1) {
			try {
				LOG.info("AWAITING (" + MILLISECONDS.toSeconds(1000) + " s) FOR FIRST REQUEST TO RETURN");
				MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
		}
		dispatchCounter.incrementAndGet();
		requestCounter.incrementAndGet();
		
		if (getLimiter().isSet().get()) {
			// Adjust request counter
			if (dispatchCounter.get() == 2) {
				LOG.info("ADJUSTING REQUEST COUNTER");
				LOG.info("PRE: REQUEST: " + requestCounter.get());
				requestCounter.set(getLimiter().getRateLimit() - getLimiter().getRateLimitRemaining().get() + 1);
				LOG.info("POST: REQUEST: " + requestCounter.get());
			}
			// Reset counter
			if (requestCounter.get() == getLimiter().getRateLimit()) {
				LOG.info("RESETING COUNTER");
				try {
					long timeout = getLimiter().getRateLimitResetInMilliSeconds() - System.currentTimeMillis() + jitter;
					LOG.info("SLEEPING for " + MILLISECONDS.toSeconds(timeout) + " s");
					awaiting.set(true);
					MILLISECONDS.sleep(timeout);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				}
				awaiting.set(false);
				requestCounter.set(0);
			}
			while (awaiting.get()){
				try {
					LOG.info("AWAITING");
					MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				}
			}
			/*if (requestCounter.get() < getLimiter().getRateLimit() && System.currentTimeMillis() > getLimiter().getRateLimitResetInMilliSeconds()){
				LOG.info("FILLING UP AVAILABLE REQUESTS");
				requestCounter.set(1);
			}*/
		} else {
			LOG.info("LIMITER HAS NOT YET BEEN SET");
		}
		super.execute(command);
		LOG.info("POST: DISPATCH:"+dispatchCounter.get() + " REQUEST: " + requestCounter.get());
	}


	private ISession getLimiter() {
		return (ISession) AbstractSession.getSession(sessionClass, sessionId);
	}

	public String getId() {
		return id;
	}

}
