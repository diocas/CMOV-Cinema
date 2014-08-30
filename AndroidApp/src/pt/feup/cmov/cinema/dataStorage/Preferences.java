package pt.feup.cmov.cinema.dataStorage;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;

public class Preferences {

	private static SharedPreferences storedData;
	private static String lastUpdateDate;
    private static String userId;
	
	public Preferences(Activity main) {
		storedData = main.getPreferences(main.MODE_PRIVATE);
		lastUpdateDate = storedData.getString("lastUpdateDate", "0000-00-00");
		userId = storedData.getString("userId", "1");
	}
	
	static String getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	static String getUserId() {
		return userId;
	}
	
	static void setUserId(String id) {
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.putString("userId", id);
		storedDataEditor.commit();
	}
	
	static void updateLastUpdateDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.putString("lastUpdateDate", sdfDate.format(now));
		storedDataEditor.commit();
	}
}
