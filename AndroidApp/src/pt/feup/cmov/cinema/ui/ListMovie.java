package pt.feup.cmov.cinema.ui;

import pt.feup.cmov.cinema.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListMovie extends SimpleCursorAdapter {

	private final int layout;
	private final LayoutInflater inflater;
	private final Context context;

	public ListMovie(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.layout = layout;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(layout, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);

		View rowView = inflater.inflate(layout, null, true);

		TextView movieTitle = (TextView) rowView
				.findViewById(R.id.movie_info_name);
		ImageView movieImage = (ImageView) rowView
				.findViewById(R.id.movie_info_img);
		

		movieTitle.setText(cursor.getString(1));

		movieImage.setImageResource(R.drawable.default_movie_img);

	}

}
