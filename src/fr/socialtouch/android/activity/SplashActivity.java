package fr.socialtouch.android.activity;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.utils.FbLoginButton;
import com.facebook.utils.FbSessionStore;

import fr.socialtouch.android.R;
import fr.socialtouch.android.SocialTouchApp;

public class SplashActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SocialTouchApp.mFacebook = new Facebook(SocialTouchApp.mFacebookAppId);
        SocialTouchApp.mFacebookAsync = new AsyncFacebookRunner(SocialTouchApp.mFacebook);
        
        FbSessionStore.restore(SocialTouchApp.mFacebook, this);
        
        initUI();
    } 
    
    
    private void initUI(){
    	((FbLoginButton) findViewById(R.id.splash_facebook_button)).init(this, SocialTouchApp.mFacebook, SocialTouchApp.mFacebookPermissions);
    }
}
