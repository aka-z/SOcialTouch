package fr.socialtouch.android.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.widget.ImageView;

public class ImageDownloader {

	public static void downloadBitmap(final ImageView imgView, final String imageUrl) {
		// download the file
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream is = getInputStreamFromUrl(imageUrl);
				if (is != null) {
					postBitmapToImgView(imgView, is);
				}
			}
		}).start();
	}

	private static InputStream getInputStreamFromUrl(String url) {
		InputStream in = null;
		AndroidHttpClient httpclient = AndroidHttpClient.newInstance(null);
		 HttpClientParams.setRedirecting(httpclient.getParams(), true);
		HttpUriRequest request = new HttpGet(url.toString());
		HttpResponse response;
		try {
			response = httpclient.execute(request);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				in = new ByteArrayInputStream(EntityUtils.toByteArray(response.getEntity()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.close();
		}
		return in;
	}

	private static void postBitmapToImgView(final ImageView imgView,
			final InputStream bitmapInputStream) {
		if (bitmapInputStream != null) {
			final Bitmap bitmap = BitmapFactory.decodeStream(bitmapInputStream);
			if (bitmap != null && imgView != null) {
				imgView.post(new Runnable() {

					@Override
					public void run() {
						imgView.setImageBitmap(bitmap);
					}
				});
			}
		}
	}

}
