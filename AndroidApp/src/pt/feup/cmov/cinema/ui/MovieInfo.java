package pt.feup.cmov.cinema.ui;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Movie;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MovieInfo extends Activity {

	DBDataSource dataSource;
	Movie movie;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_info);
		Bundle b = getIntent().getExtras();
		dataSource = new DBDataSource(this);
		movie = dataSource.getMovie(b.getLong("id"));
		
		this.setTitle(movie.getName());
		
		TextView title_name = (TextView) this.findViewById(R.id.movie_name);
		title_name.setText("Name: "+movie.getName());
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
		
	    switch (item.getItemId()) 
	    {
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
