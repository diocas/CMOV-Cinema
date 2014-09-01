package pt.feup.cmov.cinema.ui;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Reservation;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ReservationInfo extends Activity {

	DBDataSource dataSource;
	Reservation reservation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_info);
		Bundle b = getIntent().getExtras();
		dataSource = new DBDataSource(this);
		reservation = dataSource.getReservation(b.getLong("id"));
		
		TextView res_date = (TextView) this.findViewById(R.id.reservation_date);
		res_date.setText("Lugares: "+reservation.getPlaces());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reservation_info, menu);
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
