package pt.feup.cmov.cinema.dataStorage;

import java.util.ArrayList;
import java.util.List;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Movie;
import pt.feup.cmov.cinema.serverAccess.ServerAction;
import pt.feup.cmov.cinema.serverAccess.ServerActions;
import pt.feup.cmov.cinema.serverAccess.ServerConnection;
import pt.feup.cmov.cinema.serverAccess.ServerResultHandler;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

public class CinemaData {

	private DBDataSource dataSource;
	private Context context;
	private SimpleCursorAdapter moviesAdapter;
	private SimpleCursorAdapter reservationsAdapter;

	public CinemaData(DBDataSource dataSource, Context context, SimpleCursorAdapter moviesAdapter, SimpleCursorAdapter reservationsAdapter) {
		this.dataSource = dataSource;
		dataSource.cleanOld();
		this.context = context;
		this.moviesAdapter = moviesAdapter;
		this.reservationsAdapter = reservationsAdapter;
	}

	public void updateFromServer() {
		dataSource.cleanAllMovies();
		ServerConnection<ArrayList<Movie>> serverConnection = new ServerConnection<ArrayList<Movie>>(
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
						moviesAdapter.changeCursor(dataSource.getMoviesCursor());
					}

					@Override
					public void onServerResultFailure(Exception exception) {
					}
				}, new TypeToken<List<Movie>>() {
				}.getType());
		
		serverConnection.execute(new ServerAction(ServerActions.MoviesGet,
				Preferences.getLastUpdateDate()));
	}

}
