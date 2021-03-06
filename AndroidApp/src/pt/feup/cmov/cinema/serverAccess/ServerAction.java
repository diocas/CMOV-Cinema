package pt.feup.cmov.cinema.serverAccess;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Action for server access. Used in the request creation and coding/decoding the transmited messages.
 * @author diogo
 *
 * @param <T> Type of the return data
 */
public class ServerAction<T> {
	private static final String TAG = "ServerAction";
	
	public ServerActions Action;
	public String[] Arguments;
	public Object RequestBody;
	
	public ServerAction(ServerActions Action, String... Arguments)
	{
		this(Action, null, Arguments);
	}
	
	public ServerAction(ServerActions Action, Object RequestBody, String... Arguments)
	{
		this.Action = Action;
		this.Arguments = Arguments;
		this.RequestBody = RequestBody;
	}
	
	/**
	 * Get an URL of the request.
	 * @return
	 * @throws MalformedURLException
	 */
	public URL getURL() throws MalformedURLException
	{
		return new URL(String.format(Action.getUrl(), (Object[])Arguments));
	}
	
	/**
	 * Return the request with the defined type and configurations.
	 * @param requestURL
	 * @return
	 * @throws Exception
	 */
	public HttpRequestBase getHttpRequest(URI requestURL) throws Exception
	{
		String actionString = Action.toString();
		if (actionString.contains("Get"))
		{
			return new HttpGet(requestURL);
		}
		if (actionString.contains("Post"))
		{
			HttpPost httpPost = new HttpPost(requestURL);

		    try 
		    {
			    StringEntity stringEntity = null;
		    	stringEntity = new StringEntity(getRequestBody(), "UTF-8");
		    	stringEntity.setContentType("application/json; charset=UTF-8");
				httpPost.setEntity(stringEntity);
		    }
		    catch (Exception e)
		    {
		    	Log.wtf(TAG, e);
		    	throw e;
		    }
			return httpPost;
		}
		if (actionString.contains("Put"))
		{
			HttpPut httpPut = new HttpPut(requestURL);
		    try 
		    {
			    StringEntity stringEntity = null;
		    	stringEntity = new StringEntity(getRequestBody(), "UTF-8");
		    	stringEntity.setContentType("application/json; charset=UTF-8");
		    	httpPut.setEntity(stringEntity);
		    }
		    catch (Exception e)
		    {
		    	Log.wtf(TAG, e);
		    	throw e;
		    }
			return httpPut;
		}
		if (actionString.contains("Delete"))
		{

			return new HttpDelete(requestURL);
		}
		throw new Exception("Unresolved HttpEntityEnclosingRequest");
	}
	
	
	public boolean hasRequestBody()
	{
		return RequestBody != null;
	}
	
	/**
	 * Convert the request body to json.
	 * @return
	 */
	public String getRequestBody()
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		return gson.toJson(RequestBody);
	}

	/**
	 * Convert a json string into the defined object type.
	 * @param json Json to decode
	 * @param type Type of the objecto to convert to
	 * @return Object of the typed passed 
	 */
	public Object convertFromJson(String json, Type type)
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		return gson.fromJson(json, type);
	}
}
