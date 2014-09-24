package pt.feup.cmov.cinema.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import pt.feup.cmov.cinema.R;
import pt.feup.cmov.cinema.R.drawable;
import pt.feup.cmov.cinema.R.id;
import pt.feup.cmov.cinema.R.layout;
import pt.feup.cmov.cinema.serverAccess.ServerAction;
import pt.feup.cmov.cinema.serverAccess.ServerActions;
import pt.feup.cmov.cinema.serverAccess.ServerConnection;
import pt.feup.cmov.cinema.serverAccess.ServerResultHandler;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Show the building plant with the given seats location to user. If internet
 * connection is available, also shows the seats unavailable.
 * 
 * @author diogo
 * 
 */
// Codigo adaptado da internet:
// http://blahti.wordpress.com/2012/07/23/three-variations-of-image-squares/
public class ShowPlant extends Activity {

	final int numColsSeats = 32;
	final int numRowsSeats = 7;

	private int mImageThumbSize;
	private int mImageThumbSpacing;
	private int mNumColumns;
	private View mFrameView;
	private GridView mGridView;
	private ImageAdapter mAdapter;
	private int[] mImageIds = { R.drawable.chair, R.drawable.chair_chosen,
			R.drawable.chair_unavailable };

	ProgressBar loadingProgressBar;

	private List<String> unAvailablePlaces;
	private List<String> chosenPlaces;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_plant);

		this.setTitle("Plant");

		loadingProgressBar = (ProgressBar) findViewById(R.id.loading);

		Bundle b = getIntent().getExtras();
		String[] passedChosenPlaces = b.getString("chosenPlaces").split(",");

		unAvailablePlaces = new ArrayList<String>();
		chosenPlaces = new ArrayList<String>();
		for (String place : passedChosenPlaces) {
			chosenPlaces.add(place);
		}

		//Get the unavailable seats
		ServerConnection<List<String>> serverConnection = new ServerConnection<List<String>>(
				new ServerResultHandler<List<String>>() {

					@Override
					public void onServerResultSucess(List<String> response,
							int httpStatusCode) {

						unAvailablePlaces = response;
						showPlaces();
					}

					@Override
					public void onServerResultFailure(Exception exception) {
						showPlaces();
					}
				}, new TypeToken<List<String>>() {
				}.getType());

		serverConnection.execute(new ServerAction<List<String>>(
				ServerActions.SessionGetUnavailableSeatsList, b
						.getString("idSession"), b.getString("date")));

	}

	/**
	 * Set the grid of seats
	 */
	private void showPlaces() {

		loadingProgressBar.setVisibility(View.GONE);

		View frame = (View) findViewById(R.id.CinemaSeatSGridWrapper);
		mGridView = (GridView) findViewById(R.id.CinemaSeatSGrid);

		mGridView.setNumColumns(numColsSeats);
		int numItems = numColsSeats * numRowsSeats;
		mNumColumns = numColsSeats;

		mFrameView = frame;
		mAdapter = new ImageAdapter(this, numItems, 0);
		mGridView.setAdapter(mAdapter);

		// This listener is used to get the final width of the GridView.
		// The column width is used to set the height
		// of each view so we get nice square thumbnails.
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (mAdapter.getNumColumns() == 0) {
							View f = mFrameView;
							int fh = f.getHeight() - f.getPaddingTop()
									- f.getPaddingBottom();
							int shortestWidth = fh;
							int fw = f.getWidth() - f.getPaddingLeft()
									- f.getPaddingRight();
							if (fw < shortestWidth)
								shortestWidth = fw;
							int usableWidth = shortestWidth - (0 + mNumColumns)
									* mImageThumbSpacing - f.getPaddingLeft()
									- f.getPaddingRight();
							usableWidth = shortestWidth - (0 + mNumColumns)
									* mImageThumbSpacing;

							int columnWidth = usableWidth / mNumColumns;
							mImageThumbSize = columnWidth;
							int gridWidth = shortestWidth;
							int estGridWidth = mNumColumns * columnWidth;
							int unusedSpace = (shortestWidth - estGridWidth);
							boolean addPadding = (unusedSpace * 2) > mImageThumbSize;
							if (addPadding) {
								int pad = unusedSpace / 3;
								if (pad > 0)
									mGridView.setPadding(pad, pad, pad, pad);
							}

							mGridView.setColumnWidth(columnWidth);

							LayoutParams lparams = new LinearLayout.LayoutParams(
									gridWidth, gridWidth);
							mGridView.setLayoutParams(lparams);

							mAdapter.setNumColumns(mNumColumns);
							mAdapter.setItemHeight(mImageThumbSize);
						}
					}
				});
	}

	/**
	 * The main adapter that backs the GridView.
	 */
	private class ImageAdapter extends BaseAdapter {

		private final Context mContext;
		private int mImageHeight = 0;
		private int mItemHeight = 0;
		private int mNumColumns = 0;
		private int mNumItems = 0;
		private GridView.LayoutParams mImageViewLayoutParams;

		public ImageAdapter(Context context, int numItems, int imageHeight) {
			super();
			mContext = context;
			mNumItems = numItems;
			mImageHeight = imageHeight;
			if (mImageHeight == 0) {
				mImageViewLayoutParams = new GridView.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			} else {
				mImageViewLayoutParams = new GridView.LayoutParams(
						mImageHeight, mImageHeight);
			}
		}

		@Override
		public int getCount() {
			return mNumItems;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		/**
		 * Create ImageView objects for the grid.
		 * The image that shows depends if the position is one of the passed seats (defined in the chosenPlaces list),
		 * if the seats are unavailable (defined in the unAvailablePlaces list, from the server) or if they are free.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			// Now handle the main ImageView thumbnails
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, instantiate and
										// initialize
				imageView = new ImageView(mContext);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setLayoutParams(mImageViewLayoutParams);
			} else { // Otherwise re-use the converted view
				imageView = (ImageView) convertView;
			}

			// Use the current layout params on the image view.
			// (Note that these change when the fragment finishes doing its
			// layout.
			// See resetImageDimensions and where that is called in
			// onCreateView.)
			imageView.setLayoutParams(mImageViewLayoutParams);

			String seatCode = getSeatCode(position);

			if (chosenPlaces.contains(seatCode)) {
				imageView.setImageResource(mImageIds[1]);
			} else if (unAvailablePlaces.contains(seatCode)) {
				imageView.setImageResource(mImageIds[2]);
			} else if (seatCode != "") {
				imageView.setImageResource(mImageIds[0]);
			}

			return imageView;
		}

		/**
		 * Sets the item height. Useful for when we know the column width so the
		 * height can be set to match.
		 * 
		 * @param height
		 */
		public void setItemHeight(int height) {
			if (height == mItemHeight) {
				return;
			}
			mItemHeight = height;
			mImageViewLayoutParams = new GridView.LayoutParams(mItemHeight, /*
																			 * LayoutParams
																			 * .
																			 * MATCH_PARENT
																			 * ,
																			 */
			mItemHeight);
			notifyDataSetChanged();
		}

		public void setNumColumns(int numColumns) {
			mNumColumns = numColumns;
		}

		public int getNumColumns() {
			return mNumColumns;
		}

	} // end class ImageAdapter

	/**
	 * Get the seat code given the position (linear list that simulates a matrix).
	 * @param pos
	 * @return
	 */
	String getSeatCode(int pos) {
		int column = pos % mNumColumns;
		int row = pos / mNumColumns;

		if (column == 10 || column == 21 || row == 3)
			return "";

		String rowName = "";
		switch (row) {
		case 0:
			rowName = "A";
			break;
		case 1:
			rowName = "B";
			break;
		case 2:
			rowName = "C";
			break;
		case 4:
			rowName = "D";
			break;
		case 5:
			rowName = "E";
			break;
		case 6:
			rowName = "F";
			break;
		}

		if (column >= 21)
			column -= 2;
		else if (column >= 10)
			column -= 1;

		return rowName + (column + 1);
	}
}
