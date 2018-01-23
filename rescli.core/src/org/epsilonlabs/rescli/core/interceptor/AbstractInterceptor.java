package org.epsilonlabs.rescli.core.interceptor;

import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_MODIFIED;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.cache.ICache;
import org.epsilonlabs.rescli.core.session.AbstractSession;
import org.epsilonlabs.rescli.core.session.ISession;
import org.epsilonlabs.rescli.core.util.OkHttpUtil;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 
 * {@link AbstractInterceptor}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public abstract class AbstractInterceptor {

	private static final Logger LOG = LogManager.getLogger(AbstractInterceptor.class);

	protected static String headerLimit;
	protected static String headerRemaining;
	protected static String headerReset;
	protected static String userAgent;
	protected static String accept;
	protected static ICache cache;
	protected static Class<? extends AbstractSession> sessionClass;

	protected String sessionId;

	protected static final Interceptor cacheRequestInterceptor(final ICache cache, final String sessionId){
		return new Interceptor(){
			@Override public Response intercept(Chain chain) throws IOException {
				// TODO remove. It is a temporary fix 
				return chain.proceed(chain.request());

				// TODO uncomment
				/*
				Request request = chain.request();
				LOG.info(request.url());
				ISession session = AbstractSession.getSession(sessionClass, sessionId);
				LOG.info(session);
				if (cache.exists(request, session)) {
					Response response = cache.load(request, session);
					LOG.info("RETURNING RESPONSE FROM CACHE");
					return response;
				}
				return chain.proceed(request);
				*/		
			}
		};
	}

	protected static final Interceptor requestInterceptor(final String userAgent, final String accept, final String sessionId){ 
		return new Interceptor() {
			@Override public Response intercept(Chain chain) throws IOException {
				ISession session = AbstractSession.getSession(sessionClass, sessionId);
				return chain.proceed(chain.request().newBuilder()
						.header(USER_AGENT, userAgent)
						.header(ACCEPT, accept)
						.headers(session.getHeaders())
						.build());
			}
		};
	}

	protected static final Interceptor sessionResponseInterceptor(final String limit, final String remaining, final String reset, final String sessionId){ 
		return new Interceptor() {
			@Override public Response intercept(Chain chain) throws IOException {
				Response response = chain.proceed(chain.request());		
				ISession session = AbstractSession.getSession(sessionClass, sessionId);

				session.setRateLimit(response.header(limit));
				session.setRateLimitRemaining(response.header(remaining));
				session.setRateLimitReset(response.header(reset));

				LOG.info(session);
				return response;
			}
		};
	}

	protected static final Interceptor cacheResponseInterceptor(final ICache cache, final String sessionId){
		return new Interceptor(){
			@Override public Response intercept(Chain chain) throws IOException {
				
				// TODO comment this section. This is a temporary fix 
				return chain.proceed(chain.request());
				
				// TODO Delegate this to an entity to do this asynchronously and DONT wait until the cache persistence is finished
				
				/*Request request = chain.request();
				Response response = chain.proceed(request);
				ISession session = AbstractSession.getSession(sessionClass, sessionId);

				String code = String.valueOf(response.code());
				if (code.startsWith("2") || code.startsWith("3")){
					if (response.code() == SC_NOT_MODIFIED) {
						LOG.info(SC_NOT_MODIFIED);
						return cache.load(request, session);
					} else {
						LOG.info("PERSISTING RESPONSE IN CACHE");
						Response clonedResponse = OkHttpUtil.clone(response);
						cache.put(clonedResponse, session);
						return clonedResponse;
					}
				} else {
					LOG.error("Something went wrong : " +response.code() + " " + response.message() ) ;
					return response;
				}*/
			}
		};
	}

	protected static final Interceptor sessionResponseRetryInterceptor(final String limit, final String remaining, final String reset){ 
		return new Interceptor() {
			@Override public Response intercept(Chain chain) throws IOException {
				Response response = chain.proceed(chain.request());
				if (response.code() == SC_FORBIDDEN &&
						response.header(remaining).equals("0")){
					return chain.proceed(chain.request().newBuilder().build());
				}
				return response;
			}
		};
	}

}
