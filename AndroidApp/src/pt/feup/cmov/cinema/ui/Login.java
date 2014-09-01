package pt.feup.cmov.cinema.ui;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Useracount;
import pt.feup.cmov.cinema.dataStorage.Preferences;
import pt.feup.cmov.cinema.serverAccess.ServerAction;
import pt.feup.cmov.cinema.serverAccess.ServerActions;
import pt.feup.cmov.cinema.serverAccess.ServerConnection;
import pt.feup.cmov.cinema.serverAccess.ServerResultHandler;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

public class Login extends Activity {

	Button login_submit;
	EditText login_email;
	Button register_submit;
	EditText register_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Preferences(this);

		if (Preferences.getUserId() != "-1") {
			goToMenu();
		} else {
			setContentView(R.layout.activity_login);

			login_submit = (Button) findViewById(R.id.login_submit);
			login_email = (EditText) findViewById(R.id.login_email);
			register_submit = (Button) findViewById(R.id.register_submit);
			register_email = (EditText) findViewById(R.id.register_email);

			login_submit.setOnClickListener(new View.OnClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {
					ServerConnection<Useracount> serverConnectionMovies = new ServerConnection<Useracount>(
							new ServerResultHandler<Useracount>() {

								@Override
								public void onServerResultSucess(
										Useracount response, int httpStatusCode) {
									Preferences.setUserId(response.getIdUser()
											.toString());
									goToMenu();
								}

								@Override
								public void onServerResultFailure(
										Exception exception) {
									showFailure(R.string.user_not_found);
								}
							}, new TypeToken<Useracount>() {
							}.getType());

					serverConnectionMovies.execute(new ServerAction<Useracount>(
							ServerActions.UserGetAccount, login_email.getText()
									.toString()));
				}
			});

			register_submit.setOnClickListener(new View.OnClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {
					ServerConnection<String> serverConnectionMovies = new ServerConnection<String>(
							new ServerResultHandler<String>() {

								@Override
								public void onServerResultSucess(
										String response, int httpStatusCode) {
									System.out.println("userId: " + response);
									Preferences.setUserId(response);
									goToMenu();
								}

								@Override
								public void onServerResultFailure(
										Exception exception) {
									showFailure(R.string.failed_register);
								}
							}, new TypeToken<String>() {
							}.getType());

					serverConnectionMovies.execute(new ServerAction<String>(
							ServerActions.UserPost, new Useracount(null,
									register_email.getText().toString())));
				}
			});
		}
	}

	private void showFailure(int msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	void goToMenu() {
		Intent intent = new Intent(this, MenuMain.class);
		startActivity(intent);
		finish();
	}

}
