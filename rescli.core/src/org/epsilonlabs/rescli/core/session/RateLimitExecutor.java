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
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public class RateLimitExecutor extends ThreadPoolExecutor {

	private static final Logger LOG = LogManager.getLogger(RateLimitExecutor.class);

	private RateLimiter maxRequestsPerSecond;
	private AtomicInteger requestCounter;
	private Class<? extends AbstractSession> sessionClass;
	private String sessionId;
	
	RateLimitExecutor(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId, ThreadFactory factory){
		super(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(), factory); // SINGLE THREAD
		this.maxRequestsPerSecond = RateLimiter.create(maxRequestsPerSecond);
		this.requestCounter = new AtomicInteger(0);
		this.sessionClass = session;
		this.sessionId = sessionId;
	}
	
	RateLimitExecutor(int maxRequestsPerSecond, Class<? extends AbstractSession> session, String sessionId){
		super(1, 1, 0L, MILLISECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory()); // SINGLE THREAD
		this.maxRequestsPerSecond = RateLimiter.create(maxRequestsPerSecond);
		this.requestCounter = new AtomicInteger(0);
		this.sessionClass = session;
		this.sessionId = sessionId;
	}
	
	public static RateLimitExecutor create(
			int maxRequestsPerSecond,
			Class<? extends AbstractSession> session, 
			String sessionId){
		/*
		ThreadFactory factory = new ThreadFactoryBuilder()
				.setNameFormat(sessionId + "- %d")
				.setDaemon(false)
				.build();
		return new RateLimitExecutor(maxRequestsPerSecond, session, sessionId, factory);
		*/
		if (maxRequestsPerSecond > 30){
			maxRequestsPerSecond = 30;
		}
		return new RateLimitExecutor(maxRequestsPerSecond, session, sessionId);
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		maxRequestsPerSecond.acquire();
	}

	@Override
	public void execute(Runnable command) {
		int count = requestCounter.incrementAndGet();
		IRateLimiter limiter = getLimiter();
		Integer rateLimit = limiter.getRateLimit();
		if (rateLimit > 0 && count > 1){
			if((count % rateLimit) == 1){
				try{
					long timeout = limiter.getRateLimitResetInMilliSeconds() - System.currentTimeMillis();
					LOG.info("Sleeping for " +MILLISECONDS.toSeconds(timeout) + " s");
					MILLISECONDS.sleep(timeout);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage());
				} 
			}
		}
		LOG.info("executing request # " + count);
		super.execute(command);
	}

	private IRateLimiter getLimiter(){
		return (IRateLimiter) AbstractSession.getSession(sessionClass, sessionId);
	}

}
