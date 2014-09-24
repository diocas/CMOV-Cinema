package pt.feup.cmov.cinema.dataStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pt.feup.cmov.cinema.commonModels.Movie;
import pt.feup.cmov.cinema.commonModels.Reservation;
import pt.feup.cmov.cinema.commonModels.Session;
import pt.feup.cmov.cinema.utils.MovieImages;
import pt.feup.cmov.cinema.utils.DownloadImages;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Access to the database to retrieve, store, update and delete data.
 * @author diogo
 *
 */
public class DBDataSource {

	Context context;

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
			DBHelper.RESERVATION_SESSION_ID, DBHelper.RESERVATION_PLACES,
			DBHelper.RESERVATION_DATE, DBHelper.RESERVATION_UPDATE_DATE };

	/**
	 * Opens a database connection.
	 * @param context
	 */
	public DBDataSource(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Close the database connection.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Clean data that is no longer valid: reservations older than the current date and
	 * movies not in show.
	 */
	public void cleanOld() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);

		Cursor cursor = database.query(DBHelper.TABLE_MOVIE,
				allColumnsMovie,
				DBHelper.MOVIE_DATE_UNTIL + " < Datetime(?)",
				new String[] { sdfDate.format(cal.getTime()) }, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Movie movie = cursorToMovie(cursor);
			MovieImages.removeImage(context, movie.getIdMovie());
			cursor.moveToNext();
		}
		cursor.close();

		database.execSQL("DELETE FROM " + DBHelper.TABLE_MOVIE + " WHERE "
				+ DBHelper.MOVIE_DATE_UNTIL + " < Datetime('"
				+ sdfDate.format(cal.getTime()) + "')");

		database.execSQL("DELETE FROM " + DBHelper.TABLE_RESERVATION
				+ " WHERE " + DBHelper.RESERVATION_DATE + " < Datetime('"
				+ sdfDate.format(cal.getTime()) + "')");
	}
	
	/**
	 * Clen all entries in the database.
	 */
	public void cleanAll() {
		cleanAllMovies();
		cleanAllReservations();
		cleanAllSessions();
		close();
	}

	/**
	 * Get a list of all movies stored in the database.
	 * @return Movies currently showing.
	 */
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

	/**
	 * Get database cursor for movies currently showing.
	 * @return
	 */
	public Cursor getMoviesCursor() {
		return database.query(DBHelper.TABLE_MOVIE, allColumnsMovie, null,
				null, null, null, null);
	}

	/**
	 * Get a specific movie from the showing list.
	 * @param id Identification of the movie
	 * @return
	 */
	public Movie getMovie(long id) {

		Cursor cursor = database.query(DBHelper.TABLE_MOVIE, allColumnsMovie,
				DBHelper.MOVIE_ID + "=?", new String[] { String.valueOf(id) },
				null, null, null);

		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			return cursorToMovie(cursor);
		} else {
			return null;
		}
	}

	/**
	 * Create a new movie in the database.
	 * @param name
	 * @param duration
	 * @param sinopsis
	 * @param cover
	 * @param trailer
	 * @param dateFrom
	 * @param dateUntil
	 * @param updateDate
	 * @return
	 */
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

		if (movie.getCover() != null && movie.getCover().length() > 10) {

			DownloadImages downloader = new DownloadImages(context);
			downloader.execute(movie.getCover(), movie.getIdMovie().toString());
		}

		return movie;
	}

	/**
	 * Insert a movie in the database
	 * @param movie
	 */
	public void insertMovie(Movie movie) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.MOVIE_ID, movie.getIdMovie());

		if (movie.getCover() != null) {
			try {
				String[] parts = movie.getCover().split("/");
				String cover = parts[parts.length - 1];
				values.put(DBHelper.MOVIE_COVER, cover);
			} catch (Exception e) {
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		values.put(DBHelper.MOVIE_DATE_FROM, df.format(movie.getDateFrom()));
		values.put(DBHelper.MOVIE_DATE_UNTIL, df.format(movie.getDateUntil()));
		values.put(DBHelper.MOVIE_DURATION, movie.getDuration());
		values.put(DBHelper.MOVIE_NAME, movie.getName());
		values.put(DBHelper.MOVIE_SINOPSIS, movie.getSinopsis());
		values.put(DBHelper.MOVIE_TRAILER, movie.getTrailer());
		values.put(DBHelper.MOVIE_UPDATE_DATE, df.format(movie.getUpdateDate()));

		database.insertOrThrow(DBHelper.TABLE_MOVIE, null, values);

		if (movie.getCover() != null && movie.getCover().length() > 10) {

			DownloadImages downloader = new DownloadImages(context);
			downloader.execute(movie.getCover(), movie.getIdMovie().toString());
		}
	}

	/**
	 * Delete a specific movie from the database.
	 * @param movie
	 */
	public void deleteMovie(Movie movie) {
		long id = movie.getIdMovie();
		database.delete(DBHelper.TABLE_MOVIE, DBHelper.MOVIE_ID + " = " + id,
				null);
		MovieImages.removeImage(context, movie.getIdMovie());
	}

	/**
	 * Deletes all movies and removes their covers from the application. 
	 */
	public void cleanAllMovies() {

		for (Movie movie : getAllMovie()) {
			MovieImages.removeImage(context, movie.getIdMovie());
		}

		database.delete(DBHelper.TABLE_MOVIE, null, null);
	}

	/**
	 * Convert a movie cursor in a movie object
	 * @param cursor
	 * @return
	 */
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

	/**
	 * List of all currently valid reservations.
	 * @return
	 */
	public List<Reservation> getAllReservation() {
		List<Reservation> reservations = new ArrayList<Reservation>();

		Cursor cursor = database.query(DBHelper.TABLE_RESERVATION,
				allColumnsReservation, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Reservation reservation = cursorToReservation(cursor);
			reservations.add(reservation);
			cursor.moveToNext();
		}
		cursor.close();
		return reservations;
	}

	public Cursor getReservationsCursor() {
		return database.query(DBHelper.TABLE_RESERVATION,
				allColumnsReservation, null, null, null, null, DBHelper.RESERVATION_DATE);
	}

	public Reservation getReservation(long id) {

		Cursor cursor = database.query(DBHelper.TABLE_RESERVATION,
				allColumnsReservation, DBHelper.RESERVATION_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);

		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			return cursorToReservation(cursor);
		} else {
			return null;
		}
	}

	/**
	 * Insert a new reservation in the database.
	 * @param reservation
	 */
	public void insertReservation(Reservation reservation) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.RESERVATION_ID, reservation.getIdReservation());
		values.put(DBHelper.RESERVATION_SESSION_ID, reservation.getIdSession()
				.getIdSession());
		values.put(DBHelper.RESERVATION_PLACES, reservation.getPlaces());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		values.put(DBHelper.RESERVATION_DATE, df.format(reservation.getDate()));
		database.insertOrThrow(DBHelper.TABLE_RESERVATION, null, values);
	}

	/**
	 * Delete a reservation from the database.
	 * @param reservation
	 */
	public void deleteReservation(Reservation reservation) {
		long id = reservation.getIdReservation();
		database.delete(DBHelper.TABLE_RESERVATION, DBHelper.RESERVATION_ID
				+ " = " + id, null);
	}

	/**
	 * Clean all reservations from the database.
	 */
	public void cleanAllReservations() {
		database.delete(DBHelper.TABLE_RESERVATION, null, null);
	}

	/**
	 * Convert a reservation cursor in a cursor object.
	 * @param cursor
	 * @return
	 */
	private Reservation cursorToReservation(Cursor cursor) {
		Reservation reservation = new Reservation();

		reservation.setIdReservation((int) cursor.getLong(0));
		reservation.setPlaces(cursor.getString(2));
		reservation.setIdSession(getSession(cursor.getLong(1)));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			reservation.setDate(df.parse(cursor.getString(3)));
		} catch (ParseException e) {
		}

		return reservation;
	}

	/**
	 * Get all sessions
	 * @return
	 */
	public List<Session> getAllSession() {
		List<Session> sessions = new ArrayList<Session>();

		Cursor cursor = database.query(DBHelper.TABLE_SESSION,
				allColumnsSession, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Session session = cursorToSession(cursor);
			sessions.add(session);
			cursor.moveToNext();
		}
		cursor.close();
		return sessions;
	}

	/**
	 * Get all the reservations for a movie.
	 * @param id Movie id
	 * @return
	 */
	public List<Session> getSessionByMovie(long id) {
		List<Session> sessions = new ArrayList<Session>();

		Cursor cursor = getSessionByMovieCursor(id);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Session session = cursorToSession(cursor);
			sessions.add(session);
			cursor.moveToNext();
		}
		cursor.close();
		return sessions;
	}

	/**
	 * Get a database cursor to the sessions of a movie.
	 * @param id Movie id
	 * @return
	 */
	public Cursor getSessionByMovieCursor(long id) {
		return database.query(DBHelper.TABLE_SESSION, allColumnsSession,
				DBHelper.SESSION_MOVIE_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
	}

	/**
	 * Get the information from a specific session.
	 * @param id
	 * @return
	 */
	public Session getSession(long id) {

		Cursor cursor = database.query(DBHelper.TABLE_SESSION,
				allColumnsSession, DBHelper.SESSION_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);

		cursor.moveToFirst();

		if (!cursor.isAfterLast()) {
			return cursorToSession(cursor);
		} else {
			return null;
		}
	}

	/**
	 * Insert a new session in the database
	 * @param session
	 */
	public void insertSession(Session session) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.SESSION_ID, session.getIdSession());
		values.put(DBHelper.SESSION_MOVIE_ID, session.getIdMovie().getIdMovie());
		values.put(DBHelper.SESSION_ROOM, session.getRoom());

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		values.put(DBHelper.SESSION_TIME, df.format(session.getTime()));

		database.insertOrThrow(DBHelper.TABLE_SESSION, null, values);
	}

	/**
	 * Delete a session from the database.
	 * @param session
	 */
	public void deleteSession(Session session) {
		long id = session.getIdSession();
		database.delete(DBHelper.TABLE_SESSION, DBHelper.SESSION_ID + " = "
				+ id, null);
	}

	/**
	 * Clean all sessions from the database.
	 */
	public void cleanAllSessions() {
		database.delete(DBHelper.TABLE_SESSION, null, null);
	}

	/**
	 * Convert a cursor to a session in a session object.
	 * @param cursor
	 * @return
	 */
	private Session cursorToSession(Cursor cursor) {
		Session session = new Session();

		session.setIdSession((int) cursor.getLong(0));
		session.setIdMovie(getMovie(cursor.getLong(1)));
		session.setRoom(cursor.getString(3));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			session.setTime(df.parse(cursor.getString(2)));
		} catch (ParseException e) {
		}

		return session;
	}

}
