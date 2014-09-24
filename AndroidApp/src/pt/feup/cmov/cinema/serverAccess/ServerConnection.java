package pt.feup.cmov.cinema.serverAccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Async task to execute an action in the server.
 * @author diogo
 *
 * @param <T>
 */
public class ServerConnection<T> extends AsyncTask<ServerAction<T>, Void, Integer> {
	private static final String TAG = "ServiceConnection";
	
	private ServerResultHandler<T> handler;
	private Exception resultFailed;
	private T resultSucceeded;
	private Type type;
	
	public ServerConnection(ServerResultHandler<T> handler, Type type)
	{
		this.handler = handler;
		this.type = type;
	}
	

	@Override
	protected Integer doInBackground(ServerAction<T>... requestServiceActions) {
		ServerAction<T> requestServiceAction = requestServiceActions[0];
		
		int responseStatusCode = -1;
		try {
			URL requestURL = requestServiceAction.getURL();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpRequest httpRequest = requestServiceAction.getHttpRequest(requestURL.toURI());
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			
			httpRequest.setParams(httpParameters);
			
		    HttpResponse response = httpClient.execute((HttpUriRequest) httpRequest);
		    
		    responseStatusCode = response.getStatusLine().getStatusCode(); 
	        Log.v(TAG, "Request sent. Response Status code: " + responseStatusCode);

	        HttpEntity entity = response.getEntity();

	        if (entity != null)
	        {
	            InputStream instream = entity.getContent();
	            String jsonResponse = convertStreamToString(instream);
	            
	            // Attempt to convert json string to an actual object
	            resultSucceeded = (T) requestServiceAction.convertFromJson(jsonResponse, type);
	            instream.close();
	            
	        }

			
		} catch (MalformedURLException e) {
			Log.e(TAG, "", e);
			resultFailed = e;
		} catch (Exception e) {
			Log.e(TAG, "", e);
			resultFailed = e;
		}
		return responseStatusCode;
	}

	/**
	 * Check the result and execute the corresponding function. These functions are passed
	 * on the creation of the request.
	 */
	@Override
	protected void onPostExecute(Integer HttpStatusCode) {
        if (resultFailed != null) {
        	handler.onServerResultFailure(resultFailed);
        }
        else {
        	handler.onServerResultSucess(resultSucceeded, HttpStatusCode);
        }
	}
	

	private static String convertStreamToString(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		
		try {
			String line = null;
		    while ((line = reader.readLine()) != null) {
		        sb.append(line + "\n");
	        }
            is.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return sb.toString();
	}
	
}
