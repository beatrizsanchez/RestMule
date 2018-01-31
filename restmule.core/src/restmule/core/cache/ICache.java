package restmule.core.cache;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Request;
import okhttp3.Response;
import restmule.core.session.ISession;

/**
 * 
 * {@link ICache}
 * <p>
 * @version 1.0.0
 *
 */
public interface ICache {

	boolean exists(Request request, ISession session);
	boolean exists(Response response, ISession session);
	
	Response load(Response response, ISession session);
	Response load(Request request, ISession session);
	
	Response put(Response response, ISession session) throws IOException;
	
	void clear();

	Cache getOkHttpCache(); // TODO Use lucene instead
}
