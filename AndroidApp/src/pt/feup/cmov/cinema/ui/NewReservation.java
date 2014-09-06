package pt.feup.cmov.cinema.ui;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTimeComparator;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.commonModels.Reservation;
import pt.feup.cmov.cinema.commonModels.Session;
import pt.feup.cmov.cinema.commonModels.Useracount;
import pt.feup.cmov.cinema.dataStorage.DBDataSource;
import pt.feup.cmov.cinema.dataStorage.Preferences;
import pt.feup.cmov.cinema.serverAccess.ServerAction;
import pt.feup.cmov.cinema.serverAccess.ServerActions;
import pt.feup.cmov.cinema.serverAccess.ServerConnection;
import pt.feup.cmov.cinema.serverAccess.ServerResultHandler;
import pt.feup.cmov.cinema.utils.MovieImages;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

public class NewReservation extends Activity {

	static final int MAX_SEATS = 6;

	private DBDataSource dataSource;
	private Session session;
	private EditText dateEditText;
	private Context context;

	static final int DATE_PICKER_ID = 1111;
	private String chosenDateString;
	private int year = 0;
	private int month = 0;
	private int day = 0;

	private int totalSeats;

	private LinearLayout placesGrid;
	private LinearLayout SpecifySeatsBlock;
	private LinearLayout FinishReservationBlock;
	private ProgressBar placesLoader;
	private SeekBar nPlacesChooser;
	private TextView seatsGiven;
	private Button getSeatsButton;
	private Button finishReservationButton;

	private List<String> availablePlaces;
	private List<Map<String, String>> places;

	Reservation currentReservation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_reservation);
		Bundle b = getIntent().getExtras();
		dataSource = new DBDataSource(this);
		session = dataSource.getSession(b.getLong("id"));
		context = this;
		currentReservation = new Reservation();
		currentReservation.setIdUser(new Useracount(Integer.decode(Preferences
				.getUserId())));
		currentReservation.setIdSession(session);

		places = new ArrayList<Map<String, String>>();
		Map<String, String> mss = new HashMap<String, String>(3);
		mss.put("idButton", String.valueOf(R.id.place_front_left));
		mss.put("textButton", String.valueOf(R.string.column_left));
		mss.put("place", "FrontLeft");
		places.add(mss);
		mss = new HashMap<String, String>(3);
		mss.put("idButton", String.valueOf(R.id.place_front_center));
		mss.put("textButton", String.valueOf(R.string.column_center));
		mss.put("place", "FrontCenter");
		places.add(mss);
		mss = new HashMap<String, String>(3);
		mss.put("idButton", String.valueOf(R.id.place_front_right));
		mss.put("textButton", String.valueOf(R.string.column_right));
		mss.put("place", "FrontRight");
		places.add(mss);
		mss = new HashMap<String, String>(3);
		mss.put("idButton", String.valueOf(R.id.place_back_left));
		mss.put("textButton", String.valueOf(R.string.column_left));
		mss.put("place", "BackLeft");
		places.add(mss);
		mss = new HashMap<String, String>(3);
		mss.put("idButton", String.valueOf(R.id.place_back_center));
		mss.put("textButton", String.valueOf(R.string.column_center));
		mss.put("place", "BackCenter");
		places.add(mss);
		mss = new HashMap<String, String>(3);
		mss.put("idButton", String.valueOf(R.id.place_back_right));
		mss.put("textButton", String.valueOf(R.string.column_right));
		mss.put("place", "BackRight");
		places.add(mss);

		// ////////////////////////////////
		try {
			ImageView img = (ImageView) this.findViewById(R.id.movie_cover);
			img.setImageBitmap(MovieImages.getBitmap(this, session.getIdMovie()
					.getIdMovie()));
		} catch (IOException e) {
		}

		// ////////////////////////////////
		TextView movie_name = (TextView) this.findViewById(R.id.movie_name);
		movie_name.setText(session.getIdMovie().getName());

		// ////////////////////////////////
		SimpleDateFormat dff = new SimpleDateFormat("dd/MM");
		String dateFrom = dff.format(session.getIdMovie().getDateFrom());
		String dateTo = dff.format(session.getIdMovie().getDateUntil());
		TextView dates = (TextView) this.findViewById(R.id.movie_dates);
		dates.setText(dateFrom + " - " + dateTo);

		// ////////////////////////////////
		SimpleDateFormat dfh = new SimpleDateFormat("HH:mm");
		TextView reservation_time = (TextView) this
				.findViewById(R.id.reservation_time);
		reservation_time.setText(dfh.format(session.getTime()));

		dateEditText = (EditText) this.findViewById(R.id.new_reservation_date);
		dateEditText.setInputType(InputType.TYPE_NULL);

		dateEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_PICKER_ID);
			}

		});
		placesGrid = (LinearLayout) this.findViewById(R.id.places_grid);
		placesGrid.setVisibility(View.GONE);
		placesLoader = (ProgressBar) this.findViewById(R.id.places_loader);
		placesLoader.setVisibility(View.GONE);
		placesLoader = (ProgressBar) this.findViewById(R.id.places_loader);
		SpecifySeatsBlock = (LinearLayout) this
				.findViewById(R.id.specify_seats_block);
		SpecifySeatsBlock.setVisibility(View.GONE);
		FinishReservationBlock = (LinearLayout) this
				.findViewById(R.id.finish_reservation_block);
		FinishReservationBlock.setVisibility(View.GONE);
		nPlacesChooser = (SeekBar) this.findViewById(R.id.n_places_chooser);
		seatsGiven = (TextView) this.findViewById(R.id.seats_given);
		getSeatsButton = (Button) this.findViewById(R.id.get_seats);
		finishReservationButton = (Button) this
				.findViewById(R.id.finish_reservation_button);
		finishReservationButton
				.setOnClickListener(new FinishReservationButtonOnClickListener());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reservation_info, menu);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:
			if (year == 0 && month == 0 && day == 0) {
				Calendar rightNow = Calendar.getInstance();
				return new DatePickerDialog(this, pickerListener,
						rightNow.get(Calendar.YEAR),
						rightNow.get(Calendar.MONTH),
						rightNow.get(Calendar.DAY_OF_MONTH));
			} else {
				return new DatePickerDialog(this, pickerListener, year, month,
						day);
			}
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			String dateText = new StringBuilder().append(selectedYear)
					.append("-").append(selectedMonth + 1).append("-")
					.append(selectedDay).toString();

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			currentReservation.setDate(null);
			try {
				currentReservation.setDate(df.parse(dateText));
			} catch (ParseException e) {
			}

			if (currentReservation.getDate() != null
					&& DateTimeComparator.getDateOnlyInstance().compare(
							session.getIdMovie().getDateFrom(),
							currentReservation.getDate()) <= 0
					&& DateTimeComparator.getDateOnlyInstance().compare(
							session.getIdMovie().getDateUntil(),
							currentReservation.getDate()) >= 0
					&& DateTimeComparator.getDateOnlyInstance().compare(
							new Date(), currentReservation.getDate()) <= 0
					&& (year != selectedYear || month != selectedMonth || day != selectedDay)) {
				year = selectedYear;
				month = selectedMonth;
				day = selectedDay;
				chosenDateString = dateText;
				dateEditText.setText(dateText);
				hasSeats();
			} else if (year == selectedYear && month == selectedMonth
					&& day == selectedDay) {

			} else {
				Toast.makeText(context, R.string.invalid_date,
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	@SuppressWarnings("unchecked")
	private void hasSeats() {

		placesLoader.setVisibility(View.VISIBLE);
		SpecifySeatsBlock.setVisibility(View.GONE);
		FinishReservationBlock.setVisibility(View.GONE);
		placesGrid.setVisibility(View.GONE);

		ServerConnection<String> serverConnection = new ServerConnection<String>(
				new ServerResultHandler<String>() {

					@Override
					public void onServerResultSucess(String response,
							int httpStatusCode) {
						totalSeats = Integer.parseInt(response);
						if (totalSeats > 0) {
							chooseLocationAndSeats();
						} else {
							Toast.makeText(context, R.string.no_seats,
									Toast.LENGTH_SHORT).show();
							placesLoader.setVisibility(View.GONE);
						}
					}

					@Override
					public void onServerResultFailure(Exception exception) {
					}
				}, new TypeToken<String>() {
				}.getType());

		serverConnection.execute(new ServerAction<String>(
				ServerActions.SessionGetTotalSeatsCount, session.getIdSession()
						.toString(), chosenDateString));

	}

	@SuppressWarnings("unchecked")
	private void chooseLocationAndSeats() {
		ServerConnection<List<String>> serverConnection = new ServerConnection<List<String>>(
				new ServerResultHandler<List<String>>() {

					@Override
					public void onServerResultSucess(List<String> response,
							int httpStatusCode) {
						availablePlaces = response;
						completeButtons();
						placesLoader.setVisibility(View.GONE);
						placesGrid.setVisibility(View.VISIBLE);
					}

					@Override
					public void onServerResultFailure(Exception exception) {
					}
				}, new TypeToken<List<String>>() {
				}.getType());

		serverConnection.execute(new ServerAction<List<String>>(
				ServerActions.SessionGetAvailableSeatsCountList, session
						.getIdSession().toString(), chosenDateString));
	}

	private void completeButtons() {

		for (int i = 0; i < places.size(); i++) {
			Button button = (Button) this.findViewById(Integer.parseInt(places
					.get(i).get("idButton")));
			button.setText(getString(Integer.parseInt(places.get(i).get(
					"textButton")))
					+ " (" + availablePlaces.get(i) + ")");

			button.setOnClickListener(new PlaceButtonOnClickListener(Integer
					.parseInt(availablePlaces.get(i)), places.get(i).get(
					"place")));

		}
	}

	public class PlaceButtonOnClickListener implements OnClickListener {

		int availableSeats;
		String place;

		public PlaceButtonOnClickListener(int availableSeats, String place) {
			this.availableSeats = availableSeats;
			this.place = place;

		}

		@Override
		public void onClick(View v) {
			SpecifySeatsBlock.setVisibility(View.VISIBLE);
			FinishReservationBlock.setVisibility(View.GONE);
			getSeatsButton
					.setOnClickListener(new GetSeatsButtonOnClickListener(place));
			nPlacesChooser.setMax(Math.min(availableSeats + 1, MAX_SEATS));
			nPlacesChooser
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {

							getSeatsButton.setText(getResources().getString(
									R.string.get_seats_1)
									+ " "
									+ String.valueOf(progress)
									+ " "
									+ getResources().getString(
											R.string.get_seats_2));
						}
					});
			nPlacesChooser.setProgress(1);

		}

	};

	public class GetSeatsButtonOnClickListener implements OnClickListener {

		String place;

		public GetSeatsButtonOnClickListener(String place) {
			this.place = place;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {

			FinishReservationBlock.setVisibility(View.GONE);
			ServerConnection<List<String>> serverConnectionReservations = new ServerConnection<List<String>>(
					new ServerResultHandler<List<String>>() {

						@Override
						public void onServerResultSucess(List<String> response,
								int httpStatusCode) {

							chooseSeats(nPlacesChooser.getProgress(), response);
							FinishReservationBlock.setVisibility(View.VISIBLE);
						}

						@Override
						public void onServerResultFailure(Exception exception) {
							Toast.makeText(context, R.string.no_seats,
									Toast.LENGTH_SHORT).show();
						}
					}, new TypeToken<List<String>>() {
					}.getType());

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			serverConnectionReservations
					.execute(new ServerAction<List<String>>(
							ServerActions.SessionGetAvailableSeats, String
									.valueOf(session.getIdSession()), df
									.format(currentReservation.getDate()),
							place));
		}

	};

	public class FinishReservationButtonOnClickListener implements
			OnClickListener {

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			currentReservation.setUpdateDate(new Date());

			ServerConnection<String> serverConnectionReservations = new ServerConnection<String>(
					new ServerResultHandler<String>() {

						@Override
						public void onServerResultSucess(String response,
								int httpStatusCode) {

							currentReservation.setIdReservation(Integer
									.parseInt(response));
							dataSource.insertReservation(currentReservation);
							MenuMain.reservationsAdapter.changeCursor(dataSource
									.getReservationsCursor());
							finish();
						}

						@Override
						public void onServerResultFailure(Exception exception) {
							Toast.makeText(context,
									R.string.error_finish_reservation,
									Toast.LENGTH_SHORT).show();
						}
					}, new TypeToken<String>() {
					}.getType());

			serverConnectionReservations.execute(new ServerAction<String>(
					ServerActions.ReservationPost, currentReservation));
		}

	}

	private void chooseSeats(int nSeats, List<String> available) {

		HashMap<String, ArrayList<String>> rows = new HashMap<String, ArrayList<String>>();
		String seats = "";

		for (String seat : available) {

			if (!rows.containsKey(seat.substring(0, 1))) {
				rows.put(seat.substring(0, 1), new ArrayList<String>());
			}
			rows.get(seat.substring(0, 1)).add(seat);
		}

		boolean hasSufficient = false;

		for (String key : rows.keySet()) {
			if (rows.get(key).size() >= nSeats) {
				for (int i = 0; i < nSeats; i++) {
					seats += rows.get(key).get(i);
					if (i < nSeats - 1) {
						seats += ",";
					}
				}
				hasSufficient = true;
				break;
			}

		}

		if (!hasSufficient) {
			for (String key : rows.keySet()) {
				for (String seat : rows.get(key)) {
					if (nSeats > 0) {
						seats += seat;
						if (nSeats > 1) {
							seats += ",";
						}
						nSeats--;
					} else {
						break;
					}
				}
			}
		}

		seatsGiven.setText(seats);
		currentReservation.setPlaces(seats);
	}

	@Override
	protected void onDestroy() {
		dataSource.close();
		super.onDestroy();
	}
}
