package restmule.github.callback;

import static restmule.core.util.PropertiesUtil.PAGE_INFO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Headers;
import restmule.core.callback.AbstractWrappedCallback;
import restmule.core.page.IWrap;
import restmule.github.data.GitHubDataSet;
import restmule.github.page.GitHubPagination;
import restmule.github.util.GitHubPropertiesUtil;
import retrofit2.Call;
import retrofit2.Response;

public class GitHubWrappedCallback<D,R extends IWrap<D>> extends AbstractWrappedCallback<D,R, GitHubDataSet<D>>  {

	private static final Logger LOG = LogManager.getLogger(GitHubWrappedCallback.class);
	
	private static GitHubPagination paginationPolicy = GitHubPagination.get();
	
	public GitHubWrappedCallback() {
		super(new GitHubDataSet<D>());
	}

	// FIXME move these methods to super abstract class <--
	
	@Override
	public void handleResponse(Response<R> response) {
		this.dataset.addElements(response.body().getItems());
	}

	@Override
	public void handleTotal(Response<R> response) {
		Integer totalCount = response.body().getTotalCount();
		this.dataset.setTotal(totalCount);
	}

	@Override
	public void handleError(Call<R> call, Throwable t) {
		LOG.error(t.getMessage());
		LOG.error(call.request().url()); // TODO RETRY
	}
	
	// --->
	
	@Override
	public Integer totalIterations(Response<R> response) { // FIXME
		Headers headers = response.headers();
		String pagination = GitHubPropertiesUtil.get(PAGE_INFO);
		String headerValue;
		if ((headerValue = headers.get(pagination)) != null){
			HashMap<String, String> links = getLinks(headerValue);
			return getPageFromURL(links.get("LAST"));
		}
		return null; // FIXME!! Return pp.start()
	}
	
	public static final HashMap<String, String> getLinks(String headerValue) { // FIXME
		HashMap<String, String> result = new HashMap<>();
		if (headerValue!=null){
			Iterator<String> iterator = Arrays.asList(headerValue.split(", ")).iterator();
			while(iterator.hasNext()){
				String[] split= iterator.next().split(">; rel=\"");
				result.put(split[1].substring(0,split[1].length()-1).toUpperCase(), split[0].substring(1));	
			}
		} 
		return result;
	}

	public static Integer getPageFromURL(String url){ // FIXME
		String regex = "page=(\\d*)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()){
			return Integer.valueOf(matcher.group(1));
		} else {
			return null;
		}
	}
	
}
