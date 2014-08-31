package pt.feup.cmov.cinema.dataStorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "cinemaDB";
	private static final int DATABASE_VERSION = 2;
	
	
	public static final String TABLE_MOVIE = "movie";
	public static final String MOVIE_ID = "_id";
	public static final String MOVIE_NAME = "name";
	public static final String MOVIE_DURATION = "duration";
	public static final String MOVIE_SINOPSIS = "sinopsis";
	public static final String MOVIE_COVER = "cover";
	public static final String MOVIE_TRAILER = "trailer";
	public static final String MOVIE_DATE_FROM = "dateFrom";
	public static final String MOVIE_DATE_UNTIL = "dateUntil";
	public static final String MOVIE_UPDATE_DATE = "updateDate";


	private static final String TABLE_MOVIE_CREATE = "create table "
			+ TABLE_MOVIE + "(" + MOVIE_ID + " integer primary key autoincrement, "
					+ MOVIE_NAME + " text not null, "
					+ MOVIE_DURATION + " integer, "
					+ MOVIE_SINOPSIS + " text, "
					+ MOVIE_COVER + " text, "
					+ MOVIE_TRAILER + " text, "
					+ MOVIE_DATE_FROM + " text not null, "
					+ MOVIE_DATE_UNTIL + " text not null, "
					+ MOVIE_UPDATE_DATE + " text not null"
			+ ");";
	

	
	public static final String TABLE_SESSION = "session";
	public static final String SESSION_ID = "_id";
	public static final String SESSION_MOVIE_ID = "movie_id";
	public static final String SESSION_TIME = "time";
	public static final String SESSION_ROOM = "room";


	private static final String TABLE_SESSION_CREATE = "create table "
			+ TABLE_SESSION + "(" + SESSION_ID + " integer primary key autoincrement, "
					+ SESSION_MOVIE_ID + " integer, "
					+ SESSION_TIME + " text not null, "
					+ SESSION_ROOM + " text not null, "
					+ " FOREIGN KEY ("+SESSION_MOVIE_ID+") REFERENCES "+TABLE_MOVIE+" ("+MOVIE_ID+"));";
	

	
	public static final String TABLE_RESERVATION = "reservation";
	public static final String RESERVATION_ID = "_id";
	public static final String RESERVATION_SESSION_ID = "reservation_id";
	public static final String RESERVATION_PLACES = "places";
	public static final String RESERVATION_DATE = "date";
	public static final String RESERVATION_UPDATE_DATE = "updateDate";


	private static final String TABLE_RESERVATION_CREATE = "create table "
			+ TABLE_RESERVATION + "(" + RESERVATION_ID + " integer primary key autoincrement, "
					+ RESERVATION_SESSION_ID + " integer not null, "
					+ RESERVATION_PLACES + " text not null, "
					+ RESERVATION_DATE + " text not null, "
					+ RESERVATION_UPDATE_DATE + " text, "
					+ " FOREIGN KEY ("+RESERVATION_SESSION_ID+") REFERENCES "+TABLE_SESSION+" ("+SESSION_ID+"));";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_MOVIE_CREATE);
		database.execSQL(TABLE_SESSION_CREATE);
		database.execSQL(TABLE_RESERVATION_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
		onCreate(db);
	}

}