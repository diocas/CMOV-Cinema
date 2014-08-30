package pt.feup.cmov.cinema.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.*;
import pt.feup.cmov.cinema.dataStorage.CinemaData;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import pt.feup.cmov.cinema.dataStorage.DBHelper;
import pt.feup.cmov.cinema.dataStorage.Preferences;
import pt.feup.cmov.cinema.dataStorage.dataResultHandler;
import pt.feup.cmov.cinema.serverAccess.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MenuMain extends Activity {


	private SimpleCursorAdapter moviesAdapter;
	private SimpleCursorAdapter reservationsAdapter;
	private DBDataSource dataSource;
	private CinemaData database;

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

		// Set the preferences of the application
		new Preferences(this);
		
		dataSource = new DBDataSource(this);
		dataSource.open();
		
		String[] columnsMovies = new String[] { DBHelper.MOVIE_NAME };
		int[] toMovies = new int[] { R.id.movie_info_name };

		moviesAdapter = new SimpleCursorAdapter(this, R.layout.movie_info,
				dataSource.getMoviesCursor(), columnsMovies, toMovies, 0);

		
//		String[] columnsMovies = new String[] { DBHelper.re };
//		int[] toMovies = new int[] { R.id.movie_info_name };
//		
//		reservationsAdapter = new SimpleCursorAdapter(this, R.layout.movie_info,
//				dataSource.getReservationsCursor(), columnsReservations, toReservations, 0);
//		
		database = new CinemaData(dataSource, this, moviesAdapter, reservationsAdapter);
		database.updateFromServer();
	
		
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
			View moviesView = inflater.inflate(R.layout.fragment_movies,
					container, false);

			ListView moviesList = (ListView) moviesView.findViewById(R.id.moviesList);
			moviesList.setAdapter(moviesAdapter);
			return moviesView;
		}
	}

	public class Reservations extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View reservationsView = inflater.inflate(R.layout.fragment_reservations,
					container, false);

//			ListView reservationsList = (ListView) reservationsView.findViewById(R.id.moviesList);
//			reservationsList.setAdapter(reservationsAdapter);
			return reservationsView;
		}
	}

}
