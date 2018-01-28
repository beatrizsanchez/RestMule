package org.epsilonlabs.rescli.core.interceptor;

import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.cache.ICache;
import org.epsilonlabs.rescli.core.session.AbstractSession;
import org.epsilonlabs.rescli.core.session.ISession;
import org.epsilonlabs.rescli.core.util.OkHttpUtil;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
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

	private static final Logger LOG = LogManager.getLogger(AbstractInterceptor.class);

	private static final CacheControl FORCE_CACHE = new CacheControl.Builder().onlyIfCached()
			.maxStale(365, TimeUnit.DAYS).build();
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

	protected static Set<String> cacheKeys = new HashSet();

	protected static final Interceptor mainInterceptor(final String userAgent, final String accept, final ICache cache,
			final String sessionId, final String limit, final String remaining, final String reset) {
		return new Interceptor() {

			private AtomicInteger remainingRequestCounter = new AtomicInteger(1);

			@Override
			public Response intercept(Chain chain) throws IOException {
				remainingRequestCounter.decrementAndGet();

				LOG.info("INTERCEPTOR (" + remainingRequestCounter.get() + ")");

				Request request = chain.request();

				ISession session = AbstractSession.getSession(sessionClass, sessionId);
				Headers headers = headers(userAgent, accept, session, request);

				Request.Builder requestBuilder = request.newBuilder().headers(headers);
				Request networkRequest = null;

				if (session.isSet().get() && (remainingRequestCounter.get() + session.cacheCounter().get()) == 0) {
					LOG.info("UNSETTING SESSION");
					session.unset();
				}

				if (!session.isSet().get()) {
					LOG.info(TAG_FORCE_NETWORK);
					requestBuilder.cacheControl(FORCE_NETWORK).tag(TAG_FORCE_NETWORK);
					networkRequest = requestBuilder.build();
				} else {
					requestBuilder.tag(TAG_FROM_CACHE);
					Request loadFromCacheRequest = requestBuilder.build();
					Response loadFromCacheResponse = chain.proceed(loadFromCacheRequest);
					if (loadFromCacheResponse.cacheResponse() != null && loadFromCacheResponse.cacheResponse().code() != HttpStatus.SC_GATEWAY_TIMEOUT) {
						session.cacheCounter().incrementAndGet();
						LOG.info(TAG_FROM_CACHE + ", cacheCounter="+session.cacheCounter().get());
						LOG.info(loadFromCacheResponse.message());
						return loadFromCacheResponse;
					} else {
						LOG.info(TAG_RETRY_WITH_FORCE_NETWORK);
						networkRequest = loadFromCacheRequest.newBuilder()
								//.cacheControl(NORMAL)
								.cacheControl(FORCE_NETWORK)
								.tag(TAG_RETRY_WITH_FORCE_NETWORK).build();
					}
				}
				if (networkRequest != null) {
					LOG.info("DEALING WITH NETWORK RESPONSE");
					Response networkResponse = chain.proceed(networkRequest);
					if (networkResponse.networkResponse() != null) {
						if (!networkResponse.isSuccessful()) {
							LOG.error(networkResponse.code() + ":" + networkResponse.message());
							new BufferedReader(networkResponse.body().charStream()).lines().forEach(l -> LOG.info(l));
							int code = networkResponse.code();
							while (code == HttpStatus.SC_FORBIDDEN) {
								try {
									LOG.info("RETRYING");
									TimeUnit.MINUTES.sleep(1);
									networkResponse = chain.proceed(networkRequest);
									code = networkResponse.code();
								} catch (InterruptedException e) {
								}
							}
						} else {
							LOG.info("UPDATING SESSION DETAILS FROM NETWORK RESPONSE");
							// LOG.info(networkResponse.networkResponse().headers());
							session.setRateLimit(networkResponse.networkResponse().header(limit));
							session.setRateLimitReset(networkResponse.networkResponse().header(reset)); // THis will reset the cache counter
							session.setRateLimitRemaining(networkResponse.networkResponse().header(remaining));
							remainingRequestCounter.set(session.getRateLimitRemaining().get() + 1);
							LOG.info(session);
						}
						LOG.info(networkResponse.message());

						return networkResponse;
					}
				}
				LOG.info("SOMETHING WENT WRONG");
				return null;
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
				// TODO remove. It is a temporary fix using okhttp
				Request request = chain.request();
				Response response = chain.proceed(request);// getOkttpResponse(session,
															// chain);
				/*
				 * chain.proceed(request); Response cacheResponse =
				 * response.cacheResponse(); LOG.info("b"); if (cacheResponse !=
				 * null){ LOG.info("RETURNING RESPONSE FROM CACHE");
				 * cacheKeys.add(Cache.key(request.url())); return
				 * cacheResponse; } else { return response; }
				 */
				return response;

				// TODO uncomment
				/*
				 * Request request = chain.request(); LOG.info(request.url());
				 * ISession session = AbstractSession.getSession(sessionClass,
				 * sessionId); LOG.info(session); if (cache.exists(request,
				 * session)) { Response response = cache.load(request, session);
				 * LOG.info("RETURNING RESPONSE FROM CACHE"); return response; }
				 * return chain.proceed(request);
				 */
			}
		};
	}

	protected static final Interceptor sessionRequestInterceptor(final String userAgent, final String accept,
			final String sessionId) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				ISession session = AbstractSession.getSession(sessionClass, sessionId);
				return chain.proceed(chain.request().newBuilder().header(USER_AGENT, userAgent).header(ACCEPT, accept)
						.headers(session.getHeaders()).build());
			}
		};
	}

	protected static final Interceptor sessionResponseInterceptor(final String limit, final String remaining,
			final String reset, final String sessionId) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				ISession session = AbstractSession.getSession(sessionClass, sessionId);

				Response response = chain.proceed(chain.request());// getOkttpResponse(session,
																	// chain);

				session.setRateLimit(response.header(limit));
				session.setRateLimitReset(response.header(reset));
				session.setRateLimitRemaining(response.header(remaining));

				LOG.info(session);
				return response;
			}
		};
	}

	protected static final Interceptor cacheResponseInterceptor(final ICache cache, final String sessionId) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {

				// TODO comment this section. This is a temporary fix
				return chain.proceed(chain.request());

				// TODO Delegate this to an entity to do this asynchronously and
				// DONT wait until the cache persistence is finished

				/*
				 * Request request = chain.request(); Response response =
				 * chain.proceed(request); ISession session =
				 * AbstractSession.getSession(sessionClass, sessionId);
				 * 
				 * String code = String.valueOf(response.code()); if
				 * (code.startsWith("2") || code.startsWith("3")){ if
				 * (response.code() == SC_NOT_MODIFIED) {
				 * LOG.info(SC_NOT_MODIFIED); return cache.load(request,
				 * session); } else { LOG.info("PERSISTING RESPONSE IN CACHE");
				 * Response clonedResponse = OkHttpUtil.clone(response);
				 * cache.put(clonedResponse, session); return clonedResponse; }
				 * } else { LOG.error("Something went wrong : " +response.code()
				 * + " " + response.message() ) ; return response; }
				 */
			}
		};
	}

	protected static final Interceptor sessionResponseRetryInterceptor(final String limit, final String remaining,
			final String reset) {
		return new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response response = chain.proceed(chain.request());
				if (response.code() == SC_FORBIDDEN && response.header(remaining).equals("0")) {
					return chain.proceed(chain.request().newBuilder().build());
				}
				return response;
			}
		};
	}

	protected static Response getOkttpResponse(ISession session, Chain chain) {
		Response response = null;
		try {
			response = chain.proceed(chain.request());
			if (// session.isCacheable() && FIXME allow this
			response.cacheResponse() != null) {
				response = response.cacheResponse();
			} else {
				response = response.networkResponse();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;

	}

}
