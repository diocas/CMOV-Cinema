package pt.feup.cmov.cinema.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MovieImages {

	private static String DEFAULT_IMG = "default_movie_img.jpg";
	private static String IMG_PATH = "cover_";

	/**
	 * Get the cover of a movie
	 * @param context 
	 * @param idMovie
	 * @return The image of the movie
	 * @throws IOException
	 */
	public static Bitmap getBitmap(Context context, int  idMovie)
			throws IOException {
		
		try {
			FileInputStream fis = context.openFileInput(IMG_PATH + idMovie);
			Bitmap b = BitmapFactory.decodeStream(fis);
			fis.close();
			return b;

		} catch (FileNotFoundException e) {
			AssetManager assetManager = context.getAssets();
			InputStream istr = assetManager.open(DEFAULT_IMG);
			return BitmapFactory.decodeStream(istr);
		}
	}

	/**
	 * Store an image on the application
	 * @param context
	 * @param b
	 * @param idMovie
	 * @throws IOException
	 */
	public static void importImage(Context context, Bitmap b, int  idMovie) throws IOException {
		FileOutputStream fos; 
	    try { 
	        fos = context.openFileOutput(IMG_PATH + idMovie, Context.MODE_PRIVATE); 
	        b.compress(Bitmap.CompressFormat.PNG, 100, fos); 
	        fos.close(); 
	    }  
	    catch (FileNotFoundException e) {
	    }
	}

	/**
	 * Remove an image file from the application
	 * @param context
	 * @param idMovie
	 */
	public static void removeImage(Context context, int  idMovie) {
		File dir = context.getFilesDir();
		File file = new File(dir, IMG_PATH + idMovie);
		file.delete();
	}
}


