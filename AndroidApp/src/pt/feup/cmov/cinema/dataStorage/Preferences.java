package pt.feup.cmov.cinema.dataStorage;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	private String SharedPreferencesName = "cinemaprefs";
	private static SharedPreferences storedData;
	private static String lastUpdateDate;
    private static String userId;
	
	public Preferences(Activity main) {
		storedData = main.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		lastUpdateDate = storedData.getString("lastUpdateDate", "0000-00-00");
		userId = storedData.getString("userId", "-1");
	}
	
	public static String getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public static String getUserId() {
		return userId;
	}
	
	public static void setUserId(String id) {
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.putString("userId", id);
		storedDataEditor.commit();
		userId = id;
	}
	
	public static void updateLastUpdateDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.putString("lastUpdateDate", sdfDate.format(now));
		storedDataEditor.commit();
		lastUpdateDate = sdfDate.format(now);
	}

	public static void clearAll() {
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.remove("userId");
		storedDataEditor.remove("lastUpdateDate");
		storedDataEditor.clear();
		storedDataEditor.commit();
		lastUpdateDate = null;
		userId = null;
	}
}
