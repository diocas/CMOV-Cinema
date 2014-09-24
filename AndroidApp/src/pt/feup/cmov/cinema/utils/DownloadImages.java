package pt.feup.cmov.cinema.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Image downloader. Downloads images from the internet.
 * @author diogo
 *
 */
public class DownloadImages extends AsyncTask<String,Void,Bitmap> {

	Context context;
	
	public DownloadImages(Context context) {
		this.context = context;
	}
	
	/**
	 * Download an image from the internet and store it in the app
	 * @param url
	 * @param idMovie
	 * @return
	 */
    private Bitmap DownloadImageBitmap(String url, int idMovie){
        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            URL get_url = new URL(url);
            connection = (HttpURLConnection) get_url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            is = new BufferedInputStream(connection.getInputStream());
            final Bitmap bitmap = BitmapFactory.decodeStream(is);
            
            MovieImages.importImage(context, bitmap, idMovie);
            return bitmap;

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
//        finally {
//            connection.disconnect();
//            try {
//                is.close();
//            } catch (IOException e) {
//            }
//        }
        return null;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return DownloadImageBitmap(params[0], Integer.parseInt(params[1]));
    }

}