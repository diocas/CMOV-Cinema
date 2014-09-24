package pt.feup.cmov.cinema.dataStorage;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preferences of the application, stored in shared preferences.
 * @author diogo
 *
 */
public class Preferences {

	private String SharedPreferencesName = "cinemaprefs";
	private static SharedPreferences storedData;
	private static String lastUpdateDate;
    private static String userId;
	
    /**
     * Update the static preferences class
     */
	public Preferences(Activity main) {
		storedData = main.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		lastUpdateDate = storedData.getString("lastUpdateDate", "0000-00-00");
		userId = storedData.getString("userId", "-1");
	}
	
	/**
	 * Get the last sync with server date.
	 * @return
	 */
	public static String getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	/**
	 * Get the user id
	 * @return
	 */
	public static String getUserId() {
		return userId;
	}
	
	/**
	 * Set the user id
	 * @param id
	 */
	public static void setUserId(String id) {
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.putString("userId", id);
		storedDataEditor.commit();
		userId = id;
	}
	
	/**
	 * The the date of update to current time.
	 */
	public static void updateLastUpdateDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
		SharedPreferences.Editor storedDataEditor = storedData.edit();
		storedDataEditor.putString("lastUpdateDate", sdfDate.format(now));
		storedDataEditor.commit();
		lastUpdateDate = sdfDate.format(now);
	}

	/**
	 * Clear all preferences from the application.
	 */
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
