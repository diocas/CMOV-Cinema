package pt.feup.cmov.cinema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import pt.feup.cmov.common.Movie;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class MenuMain extends Activity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		new GetMoviesRunnable().execute();

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.menu_main, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * action bar item clicks here. The action bar will // automatically handle
	 * clicks on the Home/Up button, so long // as you specify a parent activity
	 * in AndroidManifest.xml. int id = item.getItemId(); if (id ==
	 * R.id.action_settings) { return true; } return
	 * super.onOptionsItemSelected(item); }
	 */

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return new Movies();
			case 1:
				return new Reservations();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.movies).toUpperCase(l);
			case 1:
				return getString(R.string.reservations).toUpperCase(l);
			}
			return null;
		}
	}

	public class Movies extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View android = inflater.inflate(R.layout.fragment_movies,
					container, false);
			return android;
		}
	}

	public class Reservations extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View android = inflater.inflate(R.layout.fragment_reservations,
					container, false);
			return android;
		}
	}

	class GetMoviesRunnable extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			HttpURLConnection con = null;
			String payload = "Error";
			String result = "Error";
			try {

				// Build RESTful query (GET)
				// URL url = new
				// URL("http://192.168.104.114:8080/RestClinic/Doctors/Docs/" +
				// key);
				URL url = new URL(
						"http://169.254.248.128:8080/cinemaServer/arrabida20/movies/2014-08-24");

				con = (HttpURLConnection) url.openConnection();
				con.setReadTimeout(10000); 
				con.setConnectTimeout(15000); 
				con.setRequestMethod("GET");
				con.setDoInput(true);

				// Start the query
				con.connect();

				// Read results from the query
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "UTF-8"));
				payload = reader.readLine();
				reader.close();
			} catch (IOException e) {
			} finally {
				if (con != null)
					con.disconnect();
			}
			System.out.println(payload);
			if (payload != "Error") {
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				Type listOfMovie = new TypeToken<List<Movie>>() {
				}.getType();
				ArrayList<Movie> movies = gson.fromJson(payload, listOfMovie);

				result = movies.get(0).getName();

			}
			final String p = payload;
			final String r = result;
			runOnUiThread(new Runnable() {
				public void run() {
					EditText text = (EditText) findViewById(R.id.info_servidor);
					text.setText("P: " + p + " \nR: " + r);
				}
			});
			
			
			return null;
		}
	}

}
