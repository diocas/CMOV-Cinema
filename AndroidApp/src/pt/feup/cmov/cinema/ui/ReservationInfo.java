package pt.feup.cmov.cinema.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.google.gson.reflect.TypeToken;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Reservation;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import pt.feup.cmov.cinema.serverAccess.ServerAction;
import pt.feup.cmov.cinema.serverAccess.ServerActions;
import pt.feup.cmov.cinema.serverAccess.ServerConnection;
import pt.feup.cmov.cinema.serverAccess.ServerResultHandler;
import pt.feup.cmov.cinema.utils.MovieImages;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


		ImageView movie_cover = (ImageView) this.findViewById(R.id.movie_cover);
		try {
			movie_cover.setImageBitmap(MovieImages
					.getBitmap(this,reservation.getIdSession().getIdMovie().getIdMovie()));
		} catch (IOException e) {
		}

		TextView movie_name = (TextView) this.findViewById(R.id.movie_name);
		movie_name.setText(reservation.getIdSession().getIdMovie().getName());
		
		TextView reservation_date = (TextView) this.findViewById(R.id.reservation_date);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM");
		reservation_date.setText(df.format(reservation.getDate()));
		
		TextView reservation_time = (TextView) this.findViewById(R.id.reservation_time);
		SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
		reservation_time.setText(df.format(reservation.getIdSession().getTime()));
		
		TextView res_places = (TextView) this.findViewById(R.id.reservation_places);
		res_places.setText(reservation.getPlaces());
		
		Button reservation_button_delete = (Button) this.findViewById(R.id.reservation_button_delete);
		reservation_button_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				ServerConnection<String> serverConnectionReservations = new ServerConnection<String>(
						new ServerResultHandler<String>() {

							@Override
							public void onServerResultSucess(String response,
									int httpStatusCode) {
								deleteReservationComplete();
							}

							@Override
							public void onServerResultFailure(Exception exception) {
								Toast.makeText(getBaseContext(),
										R.string.error_delete_reservation,
										Toast.LENGTH_SHORT).show();
							}
						}, new TypeToken<String>() {
						}.getType());

				serverConnectionReservations.execute(new ServerAction<String>(
						ServerActions.ReservationDelete, reservation.getIdReservation().toString()));
				
				
				
			}
		});
	}
	
	private void deleteReservationComplete() {

		dataSource.deleteReservation(reservation);
		
		Intent intent=getIntent();
		intent.putExtra("DELETE","ok");
        setResult(RESULT_OK, intent);
        finish();
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
