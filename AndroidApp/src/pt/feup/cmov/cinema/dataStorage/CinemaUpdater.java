package pt.feup.cmov.cinema.dataStorage;

import java.util.ArrayList;
import java.util.List;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Movie;
import pt.feup.cmov.cinema.commonModels.Reservation;
import pt.feup.cmov.cinema.commonModels.Session;
import pt.feup.cmov.cinema.serverAccess.ServerAction;
import pt.feup.cmov.cinema.serverAccess.ServerActions;
import pt.feup.cmov.cinema.serverAccess.ServerConnection;
import pt.feup.cmov.cinema.serverAccess.ServerResultHandler;
import pt.feup.cmov.cinema.ui.MenuMain;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;
import pt.feup.cmov.cinema.utils.*;

import com.google.gson.reflect.TypeToken;

/**
 * Synchronization with the server.
 * @author diogo
 *
 */
public class CinemaUpdater {

	private DBDataSource dataSource;
	private MenuMain context;
	private boolean moviesUpdateFailed;

	public CinemaUpdater(DBDataSource dataSource, MenuMain context) {
		this.dataSource = dataSource;
		dataSource.cleanOld();
		this.context = context;
	}

	/**
	 * Update all elements in the database.
	 * The synchronization follows a sequence: movies, sessions and then reservation.
	 */
	public void updateFromServer() {
		try {
			updateMovies();
		} catch (Exception e) {
			Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT)
					.show();
			context.finishUpdateMovieData();
		}
	}

	/**
	 * Update the movies list from the server, with the ones newer than the last sync.
	 * Store them in the database.
	 * If update success, calls the update of sessions. Otherwise, only calls the reservations update.
	 */
	@SuppressWarnings("unchecked")
	private void updateMovies() {

		ServerConnection<ArrayList<Movie>> serverConnectionMovies = new ServerConnection<ArrayList<Movie>>(
				new ServerResultHandler<ArrayList<Movie>>() {

					@Override
					public void onServerResultSucess(ArrayList<Movie> response,
							int httpStatusCode) {

						for (Movie movie : response) {
							try {
								dataSource.insertMovie(movie);
							} catch (SQLiteConstraintException e) {
								dataSource.deleteMovie(movie);
								dataSource.insertMovie(movie);
							}
						}
						updateSessions();
					}

					@Override
					public void onServerResultFailure(Exception exception) {
						Toast.makeText(context, R.string.update_failed_movies,
								Toast.LENGTH_SHORT).show();
						moviesUpdateFailed = true;
						updateReservations();
						context.finishUpdateMovieData();
					}
				}, new TypeToken<List<Movie>>() {
				}.getType());

		serverConnectionMovies.execute(new ServerAction<ArrayList<Movie>>(
				ServerActions.MoviesGet, Preferences.getLastUpdateDate()));

	}

	/**
	 * Update the sessions list from the server, with the ones newer than the last sync.
	 * Store them in the database.
	 * Calls the reservations update.
	 */
	@SuppressWarnings("unchecked")
	private void updateSessions() {

		ServerConnection<ArrayList<Session>> serverConnectionMovies = new ServerConnection<ArrayList<Session>>(
				new ServerResultHandler<ArrayList<Session>>() {

					@Override
					public void onServerResultSucess(
							ArrayList<Session> response, int httpStatusCode) {

						for (Session session : response) {
							try {
								dataSource.insertSession(session);
							} catch (SQLiteConstraintException e) {
								try {
									dataSource.deleteSession(session);
									dataSource.insertSession(session);
								} catch (Exception ee) {
								}
							}
						}
						context.finishUpdateMovieData();
						moviesUpdateFailed = false;
						updateReservations();
					}

					@Override
					public void onServerResultFailure(Exception exception) {
						Toast.makeText(context, R.string.update_failed_movies,
								Toast.LENGTH_SHORT).show();
						moviesUpdateFailed = true;
						updateReservations();
						context.finishUpdateMovieData();
					}
				}, new TypeToken<List<Session>>() {
				}.getType());

		serverConnectionMovies.execute(new ServerAction<ArrayList<Session>>(
				ServerActions.SessionsGet, Preferences.getLastUpdateDate()));
	}

	/**
	 * Update the reservations list from the server, with the ones newer than the last sync.
	 * Store them in the database.
	 */
	@SuppressWarnings("unchecked")
	private void updateReservations() {

		ServerConnection<ArrayList<Reservation>> serverConnectionReservations = new ServerConnection<ArrayList<Reservation>>(
				new ServerResultHandler<ArrayList<Reservation>>() {

					@Override
					public void onServerResultSucess(
							ArrayList<Reservation> response, int httpStatusCode) {

						for (Reservation reservation : response) {
							try {
								dataSource.insertReservation(reservation);
							} catch (SQLiteConstraintException e) {
								try {
									dataSource.deleteReservation(reservation);
									dataSource.insertReservation(reservation);
								} catch (Exception ee) {
								}
							}

						}
						context.finishUpdateReservationData();

						if (!moviesUpdateFailed) {
							Preferences.updateLastUpdateDate();
						}
					}

					@Override
					public void onServerResultFailure(Exception exception) {
						Toast.makeText(context,
								R.string.update_failed_reservations,
								Toast.LENGTH_SHORT).show();

						context.finishUpdateReservationData();
					}
				}, new TypeToken<List<Reservation>>() {
				}.getType());

		serverConnectionReservations
				.execute(new ServerAction<ArrayList<Reservation>>(
						ServerActions.ReservationGetByUserUpdateDate,
						Preferences.getUserId(), Preferences
								.getLastUpdateDate()));

	}

}
