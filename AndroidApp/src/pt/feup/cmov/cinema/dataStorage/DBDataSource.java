package pt.feup.cmov.cinema.dataStorage;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pt.feup.cmov.cinema.commonModels.*;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class DBDataSource {

	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumnsMovie = { DBHelper.MOVIE_ID,
			DBHelper.MOVIE_NAME, DBHelper.MOVIE_DURATION,
			DBHelper.MOVIE_SINOPSIS, DBHelper.MOVIE_COVER,
			DBHelper.MOVIE_TRAILER, DBHelper.MOVIE_DATE_FROM,
			DBHelper.MOVIE_DATE_UNTIL, DBHelper.MOVIE_UPDATE_DATE };
	private String[] allColumnsSession = { DBHelper.SESSION_ID,
			DBHelper.SESSION_MOVIE_ID, DBHelper.SESSION_TIME,
			DBHelper.SESSION_ROOM };
	private String[] allColumnsReservation = { DBHelper.RESERVATION_ID,
			DBHelper.RESERVATION_ID_USER, DBHelper.RESERVATION_PLACES,
			DBHelper.RESERVATION_DATE, DBHelper.RESERVATION_UPDATE_DATE };

	public DBDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Movie createMovie(String name, Integer duration, String sinopsis,
			String cover, String trailer, Date dateFrom, Date dateUntil,
			Date updateDate) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.MOVIE_COVER, cover);
		values.put(DBHelper.MOVIE_DATE_FROM, dateFrom.toString());
		values.put(DBHelper.MOVIE_DATE_UNTIL, dateUntil.toString());
		values.put(DBHelper.MOVIE_DURATION, duration);
		values.put(DBHelper.MOVIE_NAME, name);
		values.put(DBHelper.MOVIE_SINOPSIS, sinopsis);
		values.put(DBHelper.MOVIE_TRAILER, trailer);
		values.put(DBHelper.MOVIE_UPDATE_DATE, updateDate.toString());

		long insertId = database.insert(DBHelper.TABLE_MOVIE, null, values);
		Cursor cursor = database.query(DBHelper.TABLE_MOVIE, allColumnsMovie,
				DBHelper.MOVIE_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Movie movie = cursorToMovie(cursor);
		cursor.close();
		return movie;
	}

	public void insertMovie(Movie movie) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.MOVIE_ID, movie.getIdMovie());
		values.put(DBHelper.MOVIE_COVER, movie.getCover());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		values.put(DBHelper.MOVIE_DATE_FROM, df.format(movie.getDateFrom()));
		values.put(DBHelper.MOVIE_DATE_UNTIL, df.format(movie.getDateUntil()));
		values.put(DBHelper.MOVIE_DURATION, movie.getDuration());
		values.put(DBHelper.MOVIE_NAME, movie.getName());
		values.put(DBHelper.MOVIE_SINOPSIS, movie.getSinopsis());
		values.put(DBHelper.MOVIE_TRAILER, movie.getTrailer());
		values.put(DBHelper.MOVIE_UPDATE_DATE, df.format(movie.getUpdateDate()));

		database.insertOrThrow(DBHelper.TABLE_MOVIE, null, values);
	}

	public void deleteMovie(Movie movie) {
		long id = movie.getIdMovie();
		database.delete(DBHelper.TABLE_MOVIE, DBHelper.MOVIE_ID + " = " + id,
				null);
	}

	public List<Movie> getAllMovie() {
		List<Movie> movies = new ArrayList<Movie>();

		Cursor cursor = database.query(DBHelper.TABLE_MOVIE, allColumnsMovie,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Movie movie = cursorToMovie(cursor);
			movies.add(movie);
			cursor.moveToNext();
		}
		cursor.close();
		return movies;
	}

	public Cursor getMoviesCursor() {
		return database.query(DBHelper.TABLE_MOVIE, allColumnsMovie, null,
				null, null, null, null);
	}

	private Movie cursorToMovie(Cursor cursor) {
		Movie movie = new Movie();
		movie.setIdMovie((int) cursor.getLong(0));
		movie.setName(cursor.getString(1));
		movie.setDuration(cursor.getInt(2));
		movie.setSinopsis(cursor.getString(3));
		movie.setCover(cursor.getString(4));
		movie.setTrailer(cursor.getString(5));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			movie.setDateFrom(df.parse(cursor.getString(6)));
		} catch (ParseException e) {
		}
		try {
			movie.setDateUntil(df.parse(cursor.getString(7)));
		} catch (ParseException e) {
		}
		try {
			movie.setUpdateDate(df.parse(cursor.getString(8)));
		} catch (ParseException e) {
		}

		return movie;
	}

	public void cleanOld() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
		database.delete(DBHelper.TABLE_MOVIE, DBHelper.MOVIE_DATE_UNTIL + " < " + sdfDate.format(now).toString(),
				null);
		database.delete(DBHelper.TABLE_RESERVATION, DBHelper.RESERVATION_UPDATE_DATE + " < " + sdfDate.format(now).toString(),
				null);
	}
	
	public void cleanAllMovies() {
		database.delete(DBHelper.TABLE_MOVIE, null,null);
	}
	
	public void cleanAllReservations() {
		database.delete(DBHelper.TABLE_RESERVATION, null,null);
	}

	public Cursor getReservationsCursor() {
		return database.query(DBHelper.TABLE_RESERVATION, allColumnsReservation, null,
				null, null, null, null);
	}
}
