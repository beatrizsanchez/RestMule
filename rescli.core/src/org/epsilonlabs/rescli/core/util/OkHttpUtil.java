package org.epsilonlabs.rescli.core.util;

import java.io.IOException;

import org.apache.http.HttpStatus;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 
 * {@link OkHttpUtil}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public class OkHttpUtil {

	public static ResponseBody cloneResponseBody(final ResponseBody body){		 
		final ResponseBody responseBody = body;
		BufferedSource source = responseBody.source();
		try {
			source.request(Long.MAX_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		final Buffer bufferClone = source.buffer().clone();
		return ResponseBody.create(responseBody.contentType(), responseBody.contentLength(), bufferClone);
	}

	public static Response response(Request request, String body, String contentType, Headers headers){
		return new Response.Builder()
				.request(request)
				.protocol(Protocol.HTTP_2)
				.code(HttpStatus.SC_OK)
				.headers(headers)
				.message("Loaded from Cache")
				.body(ResponseBody.create(MediaType.parse(contentType), body))
				.build();
	}
	
	public static Response clone(Response response){
		ResponseBody body = OkHttpUtil.cloneResponseBody(response.body());
		return new Response.Builder()
				.request(response.request())
				.protocol(response.protocol())
				.code(response.code())
				.headers(response.headers())
				.message(response.message())
				.body(body)
				.build();
	}

	public static String path(Request request) {
		HttpUrl url = request.url();
		String query = (url.query()!=null) ? "?" + url.encodedQuery() : "";
		return String.valueOf(new String(url.encodedPath() + query).hashCode());
	}
	
}
