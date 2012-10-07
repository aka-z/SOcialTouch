package fr.socialtouch.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.utils.FBUserInfoRetrievalService;
import com.facebook.utils.SessionStore;

import fr.socialtouch.android.R;
import fr.socialtouch.android.SocialTouchApp;

public class FBConnectActivity extends SherlockFragmentActivity implements OnClickListener {

	private Button btnFBConnect;
	private ViewPager mTutoViewPager;
	private TutoAdapter mTutoAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		btnFBConnect = (Button)findViewById(R.id.btn_fb_connect);
		btnFBConnect.setText(R.string.splash_connecte_toi);
		
		Log.e(getClass().getSimpleName(), SessionStore.getFBProfileFormatted(this));
		
		// init view pager
		List<TutoFragment> mFragments = new ArrayList<TutoFragment>();
		mFragments.add(new TutoFragment("Connecte-toi avec Facebook, l'application récupère ton profil."));
		mFragments.add(new TutoFragment("Pour encore plus de fun, choisis le tag qui te correspond sur l'affiche."));
		mFragments.add(new TutoFragment("Pose ton télephone contre l'affiche."));
		mFragments.add(new TutoFragment("Regarde les profils qui matchent avec le tiens."));
		mFragments.add(new TutoFragment("Créé le contact pour en savoir plus ..."));
		mTutoAdapter = new TutoAdapter(getSupportFragmentManager(), mFragments);
		
        mTutoViewPager = (ViewPager) findViewById(R.id.vp_tutorial);
		mTutoViewPager.setAdapter(mTutoAdapter);
		mTutoViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int position) {
                refreshPageIndicatorUI(position);
            }
            
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }
            
            @Override
            public void onPageScrollStateChanged(int state) {
                
            }
        });
		refreshPageIndicatorUI(0);
	}
	
    private void refreshPageIndicatorUI(int position) {
        
        LinearLayout pageIndicators = (LinearLayout) findViewById(R.id.ll_pageindicator);
        for (int i = 0; i < pageIndicators.getChildCount(); i++) {
            View child = pageIndicators.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                if (i == position) {
                    tv.setSelected(true);
                    tv.setTextColor(Color.WHITE);
                } else {
                    tv.setSelected(false);
                    tv.setTextColor(Color.BLACK);
                }
            }
        }
    }

	@Override
	public void onClick(View v) {
		if (v == btnFBConnect) {
			btnFBConnect.setEnabled(false);
			fbConnect();
		}
	}

	private void fbConnect() {
		SocialTouchApp.getFacebook().authorize(this, SocialTouchApp.FACEBOOK_PERMISSIONS,
				new DialogListener() {
					@Override
					public void onComplete(Bundle values) {
						//SessionStore.displayInfo(SplashActivity.this);
						SessionStore.save(SocialTouchApp.getFacebook(), FBConnectActivity.this);

						// retrieve user info asynchronously
						Intent intent = new Intent();
						intent.setClass(FBConnectActivity.this, FBUserInfoRetrievalService.class);
						FBConnectActivity.this.startService(intent);
						
						btnFBConnect.setEnabled(false);
						
						// TODO launch next screen
						Toast.makeText(FBConnectActivity.this, "Connection OK !", Toast.LENGTH_LONG)
								.show();
						
					}

					@Override
					public void onFacebookError(FacebookError error) {
						displayErrorDialog(error.getMessage());
					}

					@Override
					public void onError(DialogError e) {
						displayErrorDialog(null);
					}

					@Override
					public void onCancel() {
						btnFBConnect.setEnabled(true);
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SocialTouchApp.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}

	private void displayErrorDialog(String msgError) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FBConnectActivity.this);
		String btnTxt = "OK";
		if (msgError == null) {
			msgError = "Ah la la..., je crois que Facebook ne vaut pas de toi...";
			btnTxt = "Dommage";
		}
		dialogBuilder.setMessage(msgError);
		dialogBuilder.setNeutralButton(btnTxt, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				btnFBConnect.setEnabled(true);
				dialog.cancel();
			}
		});
		dialogBuilder.create().show();
	}
	
	private class TutoAdapter extends FragmentPagerAdapter {

	    private final List<TutoFragment> mFragments;

	    public TutoAdapter(FragmentManager fm, List<TutoFragment> fragments) {
	        super(fm);
	        mFragments = fragments;
	    }

	    @Override
	    public TutoFragment getItem(int position) {
	        return mFragments.get(position);
	    }

	    @Override
	    public int getCount() {
	        return mFragments.size();
	    }
	}
	
}
