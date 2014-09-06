package pt.feup.cmov.cinema.dataStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class DBDataSource {

	Context context;
	DownloadImages downloader;

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

	public DBDataSource(Context context) {
		this.context = context;
		downloader = new DownloadImages(context);
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void cleanOld() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();

		Cursor cursor = database.query(DBHelper.TABLE_MOVIE,
				allColumnsMovie,
				DBHelper.MOVIE_DATE_UNTIL + " < Datetime(?)",
				new String[] { sdfDate.format(now) }, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Movie movie = cursorToMovie(cursor);
			MovieImages.removeImage(context, movie.getIdMovie());
			cursor.moveToNext();
		}
		cursor.close();

		database.execSQL("DELETE FROM " + DBHelper.TABLE_MOVIE + " WHERE "
				+ DBHelper.MOVIE_DATE_UNTIL + " < Datetime('"
				+ sdfDate.format(now) + "')");

		database.execSQL("DELETE FROM " + DBHelper.TABLE_RESERVATION
				+ " WHERE " + DBHelper.RESERVATION_DATE + " < Datetime('"
				+ sdfDate.format(now) + "')");
	}

	public void cleanAll() {
		cleanAllMovies();
		cleanAllReservations();
		cleanAllSessions();
		close();
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
			downloader.execute(movie.getCover(), movie.getIdMovie().toString());
		}

		return movie;
	}

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
			downloader.execute(movie.getCover(), movie.getIdMovie().toString());
		}
	}

	public void deleteMovie(Movie movie) {
		long id = movie.getIdMovie();
		database.delete(DBHelper.TABLE_MOVIE, DBHelper.MOVIE_ID + " = " + id,
				null);
		MovieImages.removeImage(context, movie.getIdMovie());
	}

	public void cleanAllMovies() {

		for (Movie movie : getAllMovie()) {
			MovieImages.removeImage(context, movie.getIdMovie());
		}

		database.delete(DBHelper.TABLE_MOVIE, null, null);
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

	public void deleteReservation(Reservation reservation) {
		long id = reservation.getIdReservation();
		database.delete(DBHelper.TABLE_RESERVATION, DBHelper.RESERVATION_ID
				+ " = " + id, null);
	}

	public void cleanAllReservations() {
		database.delete(DBHelper.TABLE_RESERVATION, null, null);
	}

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

	public Cursor getSessionByMovieCursor(long id) {
		return database.query(DBHelper.TABLE_SESSION, allColumnsSession,
				DBHelper.SESSION_MOVIE_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
	}

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

	public void insertSession(Session session) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.SESSION_ID, session.getIdSession());
		values.put(DBHelper.SESSION_MOVIE_ID, session.getIdMovie().getIdMovie());
		values.put(DBHelper.SESSION_ROOM, session.getRoom());

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		values.put(DBHelper.SESSION_TIME, df.format(session.getTime()));

		database.insertOrThrow(DBHelper.TABLE_SESSION, null, values);
	}

	public void deleteSession(Session session) {
		long id = session.getIdSession();
		database.delete(DBHelper.TABLE_SESSION, DBHelper.SESSION_ID + " = "
				+ id, null);
	}

	public void cleanAllSessions() {
		database.delete(DBHelper.TABLE_SESSION, null, null);
	}

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
