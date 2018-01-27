package org.epsilonlabs.rescli.core.interceptor;

import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import okhttp3.Request;
import okhttp3.RequestBody;
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

	private static final String RM_CACHE = "RM-CACHE";

	private static final Logger LOG = LogManager.getLogger(AbstractInterceptor.class);

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
					LOG.info("FORCING NETWORK");
					CacheControl cacheControl = CacheControl.FORCE_NETWORK;
					requestBuilder.cacheControl(cacheControl).tag("Force Network");
				} else {
					requestBuilder.tag("Load Cache");
				}
				Request r = requestBuilder.build();
				LOG.info(r +"\n"+r.headers());
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

				if (response.code() == HttpStatus.SC_GATEWAY_TIMEOUT) { // Failed
					LOG.info("RETRY WITH FORCE NETWORK (" + response.message() + ")");
					//request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
					Request newRequest = request.newBuilder()
							.cacheControl(CacheControl.FORCE_NETWORK)
							.tag("RETRY WITH FORCE NETWORK")
							.build();
					LOG.info(newRequest +"\n"+newRequest.headers());
					return response.newBuilder().request(newRequest).build();
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
				 * } LOG.info("returning");
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
				Response response = chain.proceed(request);

				LOG.info(request +"\n"+request.headers());

				// if (response.header(RM_CACHE) == null) {
				//if (response.cacheResponse() == null) {
					// Response networkResponse = response.networkResponse();
					// if (networkResponse != null) {
					LOG.info("Response served from the network");
					session.setRateLimit(response.header(limit));
					session.setRateLimitReset(response.header(reset));
					session.setRateLimitRemaining(response.header(remaining));
					// }
					LOG.info(session);
					// return networkResponse;
				//}
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
