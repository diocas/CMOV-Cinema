package pt.feup.cmov.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import pt.feup.cmov.common.Movie;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class Server  {
	
	
	
	public void Server(){
		
	}
//	
//	public void run() {
//		HttpURLConnection con = null;
//		String payload = "Error";
//		String result = "Error";
//		try {
//
//			// Build RESTful query (GET)
//			// URL url = new
//			// URL("http://192.168.104.114:8080/RestClinic/Doctors/Docs/" +
//			// key);
//			URL url = new URL("http://portatil-diogo:8080/cinemaServer/arrabida20/movies/2014-08-24");
//
//			con = (HttpURLConnection) url.openConnection();
//			con.setReadTimeout(10000); /* milliseconds */
//			con.setConnectTimeout(15000); /* milliseconds */
//			con.setRequestMethod("GET");
//			con.setDoInput(true);
//
//			// Start the query
//			con.connect();
//
//			// Read results from the query
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					con.getInputStream(), "UTF-8"));
//			payload = reader.readLine();
//			reader.close();
//		} catch (IOException e) {
//		} finally {
//			if (con != null)
//				con.disconnect();
//		}
//		if (payload != "Error")
//		{
//				Gson gson = new GsonBuilder().create();
//				Type listOfMovie = new TypeToken<List<Movie>>(){}.getType();
//	            ArrayList<Movie> movies = gson.fromJson(payload, listOfMovie);
//	            
//	            result = movies.get(0).getName();
//				
//			}
//		final String p = payload;
//		final String r = result;
//		runOnUiThread(new Runnable() {
//			public void run() {
//			}
//		});
//	}
}
