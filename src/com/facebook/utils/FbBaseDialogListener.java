package com.facebook.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

/**
 * @author Francois Rouault
 * @since 18 aout 2012
 */
public abstract class FbBaseDialogListener implements DialogListener {

	private final String LOG = this.getClass().getName();
	private String mErrorMessage;
	private Handler mHandler;
	private Context mContext;
	
	public FbBaseDialogListener(Context context, String errorMessage) {
		mErrorMessage = errorMessage;
		mHandler = new Handler();
		mContext = context;
	}
	
	public void onFacebookError(FacebookError e) {
		Log.e(LOG, "onFacebookError("+e.getMessage()+")");
		e.printStackTrace();
		toastThis();
	}

	public void onError(DialogError e) {
		Log.e(LOG, "onError("+e.getMessage()+")");
		e.printStackTrace();
		toastThis();
	}

	public void onCancel() {
		Log.w(LOG, "onCancel()");
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
