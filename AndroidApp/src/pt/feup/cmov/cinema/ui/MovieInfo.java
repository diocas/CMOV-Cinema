package pt.feup.cmov.cinema.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Movie;
import pt.feup.cmov.cinema.commonModels.Session;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import pt.feup.cmov.cinema.dataStorage.DBHelper;
import pt.feup.cmov.cinema.utils.MovieImages;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MovieInfo extends Activity {
	
	private DBDataSource dataSource;
	private Movie movie;
	private List<Session> sessions;
	private SimpleAdapter sessionsAdapter;
	private ListView sessionsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_info);
		Bundle b = getIntent().getExtras();
		dataSource = new DBDataSource(this);
		movie = dataSource.getMovie(b.getLong("id"));
		sessions = dataSource.getSessionByMovie(b.getLong("id"));

		this.setTitle(movie.getName());

		TextView title_name = (TextView) this.findViewById(R.id.movie_name);
		title_name.setText(movie.getName());

		String[] columnsSession = new String[] { DBHelper.SESSION_TIME };
		int[] toSession = new int[] { android.R.id.text1 };

		List<Map<String, String>> sessionsArrayList = new ArrayList<Map<String, String>>();

		for (Session session : sessions) {
			Map<String, String> row = new HashMap<String, String>(2);
			row.put("id", session.getIdSession().toString());
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			row.put(DBHelper.SESSION_TIME, df.format(session.getTime()));
			sessionsArrayList.add(row);

		}

		sessionsAdapter = new SimpleAdapter(this, sessionsArrayList,
				android.R.layout.simple_list_item_1, columnsSession, toSession);

		sessionsList = (ListView) findViewById(R.id.movie_sessions);
		sessionsList.setAdapter(sessionsAdapter);

		sessionsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> row = (Map<String, String>) sessionsList
						.getItemAtPosition(position);
				Intent intent = new Intent(view.getContext(),
						NewReservation.class);
				Bundle b = new Bundle();
				b.putLong("id", Long.parseLong(row.get("id")));
				intent.putExtras(b);
				startActivity(intent);

			}
		});

		try {
			ImageView img = (ImageView) this.findViewById(R.id.movie_cover);
			img.setImageBitmap(MovieImages.getBitmap(this, movie.getIdMovie()));
		} catch (IOException e) {
		}


		SimpleDateFormat dff = new SimpleDateFormat("dd/MM");
		String dateFrom = dff.format(movie.getDateFrom());
		String dateTo = dff.format(movie.getDateUntil());
		TextView dates = (TextView) this.findViewById(R.id.movie_dates);
		dates.setText(dateFrom + " - " + dateTo);
		
		TextView duration = (TextView) this.findViewById(R.id.movie_duration);
		duration.setText(movie.getDuration() + "m");
		
		TextView sinopsis = (TextView) this.findViewById(R.id.movie_sinopsis);
		sinopsis.setText(movie.getSinopsis());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.movie_info, menu);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		dataSource.close();
		super.onDestroy();
	}
}
