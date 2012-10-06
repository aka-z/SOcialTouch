package fr.socialtouch.android;

import android.app.Application;
import android.graphics.Typeface;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class SocialTouchApp extends Application {

	public static final String[] FACEBOOK_PERMISSIONS = new String[] { "user_birthday",
			"user_hometown", "user_location", "user_likes" };

	private static final String TAG = SocialTouchApp.class.getName();
	private static Facebook mFacebook;
	private static AsyncFacebookRunner mFacebookAsync;
	private static final String FACEBOOK_APP_ID = "475398122492638";

    public static Typeface mFont;

	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		super.onCreate();
		
	      mFont = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");

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
