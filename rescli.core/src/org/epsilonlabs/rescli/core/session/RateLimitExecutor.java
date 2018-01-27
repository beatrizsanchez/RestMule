package org.epsilonlabs.rescli.core.session;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

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
	private static final String COUNTER_DEBUG = ">>\n>>\n>>[{}] Total={}, Remaining={}, Dispatched={}\n>>";
	
	private RateLimiter maxRequestsPerSecond;
	private AtomicInteger remainingRequestCounter;
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
		this.dispatchCounter = new AtomicInteger(0);
		this.remainingRequestCounter = new AtomicInteger(1);
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

	public String getId() {
		return id;
	}

	private ISession getLimiter() {
		return (ISession) AbstractSession.getSession(sessionClass, sessionId);
	}

	@Override
	public void execute(Runnable command) {
		LOG.info("ENTERING EXECUTE");
		maxRequestsPerSecond.acquire();

		// Wait for first request to return
		while (!getLimiter().isSet().get() && dispatchCounter.get() == 1) {
			try {

				LOG.info("AWAITING FOR FIRST REQUEST TO RETURN");
				MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage());
			}
		}
		dispatchCounter.incrementAndGet();

		if (getLimiter().isSet().get()) {
			// Adjust request counter
			if (dispatchCounter.get() == 2) {
				LOG.info("ADJUSTING REQUEST COUNTER");
				remainingRequestCounter.set(getLimiter().getRateLimitRemaining().get());
			}
			// Reset counter
			if (remainingRequestCounter.get() == 0) {
				long timeout = getLimiter().getRateLimitResetInMilliSeconds() - System.currentTimeMillis() + jitter;
				timeout = (timeout > 0) ? timeout : 1000; 
				LOG.info("SLEEPING for " + MILLISECONDS.toSeconds(timeout) + " s");
				try {
					awaiting.set(true);
					MILLISECONDS.sleep(timeout);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				}
				awaiting.set(false);
				LOG.info("RESETING COUNTER");
				remainingRequestCounter.set(getLimiter().getRateLimit());
			}
			// TODO HANDLE CACHED OR FILLING UP AVAILABLE REQUESTS 
			/*
			 * if (requestCounter.get() < getLimiter().getRateLimit() &&
			 * System.currentTimeMillis() >
			 * getLimiter().getRateLimitResetInMilliSeconds()){
			 * LOG.info("FILLING UP AVAILABLE REQUESTS"); requestCounter.set(1);
			 * }
			 */
		} else {
			LOG.info("LIMITER HAS NOT YET BEEN SET");
		}
		remainingRequestCounter.decrementAndGet();
		logCounterDebug("beforeExec");
		super.execute(command);
	}

	private void logCounterDebug(String step) {
		LOG.debug(COUNTER_DEBUG, sessionId, getLimiter().getRateLimit(), remainingRequestCounter.get(),
				dispatchCounter.get());
	}
}
