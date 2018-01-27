package org.epsilonlabs.rescli.core.interceptor;

import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.cache.ICache;
import org.epsilonlabs.rescli.core.session.AbstractSession;
import org.epsilonlabs.rescli.core.session.ISession;
import org.epsilonlabs.rescli.core.util.OkHttpUtil;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 
 * {@link AbstractInterceptor}
 * <p>
 * Copyright &copy; 2017 University of York.
 * 
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public abstract class AbstractInterceptor {

	private static final String TAG_FORCE_NETWORK = "FORCE NETWORK";
	private static final String TAG_RETRY_WITH_FORCE_NETWORK = "RETRY WITH FORCE NETWORK";
	private static final String TAG_FROM_CACHE = "FROM CACHE";
	
	private static final String RM_CACHE = "RM-CACHE";

	private static final Logger LOG = LogManager.getLogger(AbstractInterceptor.class);
	private static final CacheControl FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(365, TimeUnit.DAYS).build();
	private static final CacheControl FORCE_NETWORK = new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build();
	private static final CacheControl NORMAL = new CacheControl.Builder().build();
	
	protected static String headerLimit;
	protected static String headerRemaining;
	protected static String headerReset;
	protected static String userAgent;
	protected static String accept;
	protected static ICache cache;
	protected static Class<? extends AbstractSession> sessionClass;

	protected String sessionId;
	protected String agent;

	protected static Set<String> cacheKeys = new HashSet<String>();

	protected static final Interceptor sessionRequestInterceptor(final String userAgent, final String accept,
			final String sessionId) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				LOG.info("sessionRequestInterceptor");

				ISession session = AbstractSession.getSession(sessionClass, sessionId);
				Request request = chain.request();

				Headers headers = headers(userAgent, accept, session, request);
				Request.Builder requestBuilder = request.newBuilder().headers(headers);
				if (!session.isSet().get()) {
					LOG.info(TAG_FORCE_NETWORK);
					//CacheControl cacheControl = CacheControl.FORCE_NETWORK;
					requestBuilder.cacheControl(FORCE_CACHE).tag(TAG_FORCE_NETWORK);
				} else {
					LOG.info(TAG_FROM_CACHE);
					requestBuilder.tag(TAG_FROM_CACHE);
				}
				Request r = requestBuilder.build();
				//LOG.info(r +"\n"+r.headers());
				return chain.proceed(r);
			}

			private Headers headers(final String userAgent, final String accept, ISession session,
					Request originalRequest) {

				Headers.Builder headerBuilder = new Headers.Builder();
				headerBuilder = headerBuilder.add(USER_AGENT, userAgent);
				headerBuilder = headerBuilder.add(ACCEPT, accept);
				Headers headers = headerBuilder.build();

				return OkHttpUtil.headers(session, originalRequest, headers);
			}
		};
	}

	protected static final Interceptor cacheRequestInterceptor(final ICache cache, final String sessionId) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				LOG.info("cacheRequestInterceptor");

				Request request = chain.request();
				Response response = chain.proceed(request);
				LOG.info("NW?"+ (response.networkResponse()!=null));
				LOG.info("CA?"+ (response.cacheResponse()!=null));
				
				if (response.code() == HttpStatus.SC_GATEWAY_TIMEOUT) { 	// Failed Cache
					LOG.info(TAG_RETRY_WITH_FORCE_NETWORK + " (" + response.message() + ")");
					Request newRequest = request.newBuilder()
							//.cacheControl(CacheControl.FORCE_NETWORK)
							.cacheControl(NORMAL)
							.tag(TAG_RETRY_WITH_FORCE_NETWORK)
							.build();
					//LOG.info(newRequest +"\n"+newRequest.headers());
					return chain.proceed(newRequest);
				}
				return response;
				
				/*
				 * ISession session = AbstractSession.getSession(sessionClass,
				 * sessionId); if (response.cacheResponse() != null) {
				 * LOG.info("RESPONSE FROM CACHE"); //
				 * session.cacheCounter().incrementAndGet(); //Headers headers =
				 * headers(response); //LOG.info(headers); //response =
				 * OkHttpUtil.cloneFromOriginal(response, headers);
				 *//*
					 * response = response.newBuilder()
					 * .request(response.request())
					 * .protocol(response.protocol()) .headers(headers)
					 * .code(response.code()) .message(response.message())
					 * //.body(ResponseBody.create(MediaType.parse(contentType),
					 * body)) .body(response.body()) .build();
					 */
				/*
				 * }
				 */
			}

			private Headers headers(Response response) {
				Headers headers = new Headers.Builder().add(RM_CACHE, Boolean.TRUE.toString()).build();
				return OkHttpUtil.headers(null, response, headers);
			}
		};
	}	

	protected static final Interceptor sessionResponseInterceptor(final String limit, final String remaining,
			final String reset, final String sessionId) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				LOG.info("sessionResponseInterceptor");

				ISession session = AbstractSession.getSession(sessionClass, sessionId);
				Request request = chain.request();
				//LOG.info(Cache.key(request.url()));
				Response response = chain.proceed(request);
				LOG.info("NW?"+ (response.networkResponse() != null));
				LOG.info("CA?"+ (response.cacheResponse() != null));
				if (response.networkResponse() != null) {
					
					if (!response.isSuccessful()){
						LOG.error(response.code() + ":" + response.message());
						new BufferedReader(response.body().charStream()).lines().forEach(l -> LOG.info(l));
						while (response.code() == HttpStatus.SC_FORBIDDEN){
							try {
								LOG.info("retrying");
								TimeUnit.SECONDS.sleep(1);
								response = chain.proceed(request);
							} catch (InterruptedException e) {}
						}
					} else {
				/*if (request.tag() != null && (request.tag().equals(TAG_FORCE_NETWORK) ||
						request.tag().equals(TAG_RETRY_WITH_FORCE_NETWORK))) {*/
					LOG.info("UPDATING SESSION DETAILS");
					
					session.setRateLimit(response.header(limit));
					session.setRateLimitReset(response.header(reset));
					session.setRateLimitRemaining(response.header(remaining));
					//LOG.info("NW counter: "+ session.networkCounter().incrementAndGet());
					LOG.info(session);
					}
				} else {
					//session.cacheCounter().incrementAndGet();
					LOG.info("cache counter: "+ session.cacheCounter().incrementAndGet());
				}
				return response;
			}
		};
	}

	// Application Interceptor, TODO remove Authentication headers from original
	// request
	protected static final Interceptor sessionResponseRetryInterceptor(final String limit, final String remaining,
			final String reset) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				LOG.info("sessionResponseRetryInterceptor");
				Response response = chain.proceed(chain.request());
				if (response.code() == SC_FORBIDDEN && response.header(remaining).equals("0")) {
					return chain.proceed(chain.request().newBuilder().build());
				}
				return response;
			}
		};
	}

}
