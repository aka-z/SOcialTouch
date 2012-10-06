package fr.socialtouch.android;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;

public class SocialTouchApp extends Application {
	
	private final String TAG = this.getClass().getName();
	
	public static Facebook mFacebook;
	public static AsyncFacebookRunner mFacebookAsync;
	public static String mFacebookAppId = "475398122492638";
	public static String[] mFacebookPermissions = new String[] {"publish_stream", "publish_actions"};
	
	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		super.onCreate();
	}
	
	public static void requestUserData(RequestListener requestListener) {
        Bundle params = new Bundle();
        params.putString("fields", "name, picture");
        mFacebookAsync.request("me", params, requestListener);
    }
	
		
	
}
