package fr.socialtouch.android;

import android.app.Application;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class SocialTouchApp extends Application {

	public static final String[] FACEBOOK_PERMISSIONS = new String[] { "user_birthday",
			"publish_stream", "publish_actions" };

	private static final String TAG = SocialTouchApp.class.getName();
	private static Facebook mFacebook;
	private static AsyncFacebookRunner mFacebookAsync;
	private static final String FACEBOOK_APP_ID = "475398122492638";

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		super.onCreate();
	}

	public static Facebook getFacebook() {
		if (mFacebook == null) {
			mFacebook = new Facebook(FACEBOOK_APP_ID);
		}
		return mFacebook;
	}

	public static AsyncFacebookRunner getAsyncFacebookRunner() {
		if (mFacebookAsync == null) {
			mFacebookAsync = new AsyncFacebookRunner(getFacebook());
		}
		return mFacebookAsync;
	}

}
