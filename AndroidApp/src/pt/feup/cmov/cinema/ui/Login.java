package pt.feup.cmov.cinema.ui;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.dataStorage.Preferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Preferences(this);

		System.out.println(Preferences.getLastUpdateDate());
		if (Preferences.getUserId() != "-1") {
			goToMenu();
		} else {
			setContentView(R.layout.activity_login);
		}
		
//		
		
//		
//
//		
//		ServerConnection<ArrayList<Reservation>> serverConnectionReservations = new ServerConnection<ArrayList<Reservation>>(
//				new ServerResultHandler<ArrayList<Reservation>>() {
//
//					@Override
//					public void onServerResultSucess(
//							ArrayList<Reservation> response, int httpStatusCode) {
//						
//					}
//
//					@Override
//					public void onServerResultFailure(Exception exception) {
//						
//					}
//				}, new TypeToken<List<Reservation>>() {
//				}.getType());
//
//		try {
//			
//			serverConnectionReservations.execute(new ServerAction(
//					ServerActions.ReservationGetByUserUpdateDate, Preferences
//							.getUserId(), Preferences.getLastUpdateDate()));
//		} catch (Exception e) {
//			Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT)
//					.show();
//		}
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.login, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	void goToMenu() {
		Intent intent = new Intent(this, MenuMain.class);
		startActivity(intent);
		finish();
	}
	
}
