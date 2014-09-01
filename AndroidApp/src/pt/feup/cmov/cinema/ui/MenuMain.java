package pt.feup.cmov.cinema.ui;

import java.util.Locale;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.dataStorage.CinemaUpdater;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import pt.feup.cmov.cinema.dataStorage.DBHelper;
import pt.feup.cmov.cinema.dataStorage.Preferences;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MenuMain extends Activity {

	public static SimpleCursorAdapter moviesAdapter;
	public static SimpleCursorAdapter reservationsAdapter;
	
	private DBDataSource dataSource;
	private CinemaUpdater cinemaUpdater;

	private boolean moviesUpdateInProgress = false;
	private boolean reservationsUpdateInProgress = false;

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

		String[] columnsMovies = new String[] { DBHelper.MOVIE_NAME };
		int[] toMovies = new int[] { R.id.movie_info_name };

		moviesAdapter = new ListMovie(this, R.layout.list_item_movie,
				dataSource.getMoviesCursor(), columnsMovies, toMovies, 0);

		String[] columnsReservations = new String[] { DBHelper.RESERVATION_DATE };
		int[] toReservations = new int[] { R.id.reservation_name };

		reservationsAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item_reservation,
				dataSource.getReservationsCursor(), columnsReservations,
				toReservations, 0);

		cinemaUpdater = new CinemaUpdater(dataSource, this);

		updateData();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_update) {
			updateData();
			return true;
		} else if (id == R.id.action_logout) {

			Preferences.clearAll();
			dataSource.cleanAll();
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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

			ListView moviesList = (ListView) moviesView
					.findViewById(R.id.moviesList);
			moviesList.setAdapter(moviesAdapter);

			moviesList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(view.getContext(),
							MovieInfo.class);
					Bundle b = new Bundle();
					b.putLong("id", id);
					intent.putExtras(b);
					startActivity(intent);
				}
			});

			if (moviesUpdateInProgress) {
				RelativeLayout moviesSpiner = (RelativeLayout) moviesView
						.findViewById(R.id.movies_progressBar);
				try {
					moviesSpiner.setVisibility(View.VISIBLE);
				} catch (Exception e) {
				}
				try {
					moviesList.setVisibility(View.GONE);
				} catch (Exception e) { 
				}
			}

			return moviesView;
		}
	}

	public class Reservations extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View reservationsView = inflater.inflate(
					R.layout.fragment_reservation, container, false);

			ListView reservationsList = (ListView) reservationsView
					.findViewById(R.id.reservationsList);
			reservationsList.setAdapter(reservationsAdapter);

			reservationsList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(view.getContext(),
							ReservationInfo.class);
					Bundle b = new Bundle();
					b.putLong("id", id);
					intent.putExtras(b);
					startActivity(intent);
				}
			});

			if (reservationsUpdateInProgress) {
				RelativeLayout reservationsSpiner = (RelativeLayout) reservationsView
						.findViewById(R.id.reservations_progressBar);
				try {
					reservationsSpiner.setVisibility(View.VISIBLE);
				} catch (Exception e) {
				}
				try {
					reservationsList.setVisibility(View.GONE);
				} catch (Exception e) { 
				}
			}

			return reservationsView;
		}
	}

	public void updateData() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
			reservationsUpdateInProgress = true;
			moviesUpdateInProgress = true;
			initUpdateReservationData();
			initUpdateMovieData();
			cinemaUpdater.updateFromServer();
		} else {
			finishUpdateReservationData();
			finishUpdateMovieData();
			Toast.makeText(this, R.string.internet_not_available,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void initUpdateReservationData() {

		reservationsUpdateInProgress = false;

		ListView reservationsList = (ListView) findViewById(R.id.reservationsList);
		RelativeLayout reservationsSpiner = (RelativeLayout) findViewById(R.id.reservations_progressBar);
		try {
			reservationsList.setVisibility(View.GONE);
		} catch (Exception e) {
		}
		try {
			reservationsSpiner.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
	}

	public void finishUpdateReservationData() {
		ListView reservationsList = (ListView) findViewById(R.id.reservationsList);
		RelativeLayout reservationsSpiner = (RelativeLayout) findViewById(R.id.reservations_progressBar);

		reservationsAdapter.changeCursor(dataSource.getReservationsCursor());

		try {
			reservationsSpiner.setVisibility(View.GONE);
		} catch (Exception e) {
		}
		try {
			reservationsList.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
	}

	public void initUpdateMovieData() {

		ListView moviesList = (ListView) findViewById(R.id.moviesList);
		RelativeLayout moviesSpiner = (RelativeLayout) findViewById(R.id.movies_progressBar);
		try {
			moviesList.setVisibility(View.GONE);
		} catch (Exception e) {
		}
		try {
			moviesSpiner.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
	}

	public void finishUpdateMovieData() {

		moviesUpdateInProgress = false;

		ListView moviesList = (ListView) findViewById(R.id.moviesList);
		RelativeLayout moviesSpiner = (RelativeLayout) findViewById(R.id.movies_progressBar);

		moviesAdapter.changeCursor(dataSource.getMoviesCursor());

		try {
			moviesSpiner.setVisibility(View.GONE);
		} catch (Exception e) {
		}
		try {
			moviesList.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		dataSource.close();
		super.onDestroy();
	}
}