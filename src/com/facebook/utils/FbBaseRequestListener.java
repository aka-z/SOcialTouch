package com.facebook.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

/**
 * @author Francois Rouault
 * @since 18 aout 2012
 */
public abstract class FbBaseRequestListener implements RequestListener {

	private final String LOG = this.getClass().getName();
	private String mErrorMessage;
	private Handler mHandler;
	private Context mContext;
	
	public FbBaseRequestListener(Context context, String errorMessage) {
		mErrorMessage = errorMessage;
		mHandler = new Handler();
		mContext = context;
	}
	
	public void onFacebookError(FacebookError e, final Object state) {
		Log.e(LOG, e.getMessage());
		e.printStackTrace();
		toastThis();
	}

	public void onFileNotFoundException(FileNotFoundException e, final Object state) {
		Log.e(LOG, e.getMessage());
		e.printStackTrace();
		toastThis();
	}

	public void onIOException(IOException e, final Object state) {
		Log.e(LOG, e.getMessage());
		e.printStackTrace();
		toastThis();
	}

	public void onMalformedURLException(MalformedURLException e, final Object state) {
		Log.e(LOG, e.getMessage());
		e.printStackTrace();
		toastThis();
	}

	private void toastThis(){
		if(mErrorMessage!=null){
			mHandler.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, mErrorMessage, Toast.LENGTH_SHORT);
				}
			});
		}
	}
}
