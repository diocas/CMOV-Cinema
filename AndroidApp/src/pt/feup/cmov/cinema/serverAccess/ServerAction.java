package pt.feup.cmov.cinema.serverAccess;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	
	public URL getURL() throws MalformedURLException
	{
		return new URL(String.format(Action.getUrl(), (Object[])Arguments));
	}
	
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
			return new HttpGet(requestURL);
		}
		throw new Exception("Unresolved HttpEntityEnclosingRequest");
	}
	
	public boolean hasRequestBody()
	{
		return RequestBody != null;
	}
	
	public String getRequestBody()
	{
		return new Gson().toJson(RequestBody);
	}

	public Object convertFromJson(String json, Type type)
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		return gson.fromJson(json, type);
	}
}
