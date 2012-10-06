package com.facebook.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.utils.FbSessionEvents.AuthListener;
import com.facebook.utils.FbSessionEvents.LogoutListener;

import fr.socialtouch.android.R;

/**
 * @author Francois Rouault
 * @since 18 aout 2012
 */
public class FbLoginButton extends ImageButton {

	private final String LOG = this.getClass().getName(); 
	
	public static final String MESSAGE_LOGIN_CANCELED = "Action canceled";
	private Facebook mFb;
	private Handler mHandler;
	private SessionListener mSessionListener = new SessionListener();
	private String[] mPermissions;
	private Activity mActivity;

	public FbLoginButton(Context context) {
		super(context);
	}

	public FbLoginButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FbLoginButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(final Activity activity, final Facebook fb) {
		init(activity, fb, new String[] {});
	}

	public void init(final Activity activity, final Facebook fb, final String[] permissions) {
		mActivity = activity;
		mFb = fb;
		mPermissions = permissions;
		mHandler = new Handler();

		setBackgroundColor(Color.TRANSPARENT);
		setAdjustViewBounds(true);
		setImageResource(fb.isSessionValid() ? R.drawable.selector_facebook_logout : R.drawable.selector_facebook_login);
		drawableStateChanged();

		FbSessionEvents.addAuthListener(mSessionListener);
		FbSessionEvents.addLogoutListener(mSessionListener);
		setOnClickListener(new ButtonOnClickListener());
	}

	private final class ButtonOnClickListener implements OnClickListener {
		public void onClick(View v) {
			if (mFb.isSessionValid()) {
				FbSessionEvents.onLogoutBegin();
				AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
				asyncRunner.logout(getContext(), new LogoutRequestListener());
			} else {
				mFb.authorize(mActivity, mPermissions, new LoginDialogListener());
			}
		}
	}
	
	private void toastThis(final String errorMessage){
		if(errorMessage!=null){
			mHandler.post(new Runnable() {
				public void run() {
					Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT);
				}
			});
		}
	}
	
	////////////////////////////////////////////////////
	/////////       LOG IN/OUT LISTENERS      //////////
	////////////////////////////////////////////////////
	private final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			FbSessionEvents.onLoginSuccess();
		}

		public void onFacebookError(FacebookError error) {
			FbSessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			FbSessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			FbSessionEvents.onLoginError(MESSAGE_LOGIN_CANCELED);
		}
	}
	
	private class LogoutRequestListener implements RequestListener {

		public void onComplete(String response, final Object state) {
			// callback should be run in the original thread,
			// not the background thread
			mHandler.post(new Runnable() {
				public void run() {
					FbSessionEvents.onLogoutFinish();
				}
			});
		}

		@Override
		public void onIOException(IOException e, Object state) {
			e.printStackTrace();
			Log.e(LOG, e.getMessage());
			toastThis(mActivity.getString(R.string.message_facebook_logout_failed));
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			e.printStackTrace();
			Log.e(LOG, e.getMessage());
			toastThis(mActivity.getString(R.string.message_facebook_logout_failed));
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			e.printStackTrace();
			Log.e(LOG, e.getMessage());
			toastThis(mActivity.getString(R.string.message_facebook_logout_failed));			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			e.printStackTrace();
			Log.e(LOG, e.getMessage());
			toastThis(mActivity.getString(R.string.message_facebook_logout_failed));
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	///////////       SESSION  LISTENER        /////////////////////
	////////////////////////////////////////////////////////////////
	private class SessionListener implements AuthListener, LogoutListener {

		public void onAuthSucceed() {
			setImageResource(R.drawable.selector_facebook_logout);
			FbSessionStore.save(mFb, getContext());
		}

		public void onAuthFail(String error) {
			if(!error.equals(MESSAGE_LOGIN_CANCELED))
				Toast.makeText(mActivity, R.string.message_facebook_login_failed, Toast.LENGTH_SHORT).show();
		}

		public void onLogoutBegin() {}

		public void onLogoutFinish() {
			FbSessionStore.clear(getContext());
			setImageResource(R.drawable.selector_facebook_login);
		}
	}
}
