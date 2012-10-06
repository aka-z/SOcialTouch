package fr.socialtouch.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.utils.FBUserInfoRetrievalService;
import com.facebook.utils.SessionStore;

import fr.socialtouch.android.R;
import fr.socialtouch.android.SocialTouchApp;

public class SplashActivity extends SherlockActivity implements OnClickListener {

	private Button btnFBConnect;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		btnFBConnect = (Button)findViewById(R.id.btn_fb_connect);
		btnFBConnect.setText("Vas-y, connectes-toi !");
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
						SessionStore.displayInfo(SplashActivity.this);
						SessionStore.save(SocialTouchApp.getFacebook(), SplashActivity.this);

						// retrieve user info asynchronously
						Intent intent = new Intent();
						intent.setClass(SplashActivity.this, FBUserInfoRetrievalService.class);
						SplashActivity.this.startService(intent);
						// TODO launch next screen
						Toast.makeText(SplashActivity.this, "Connection OK !", Toast.LENGTH_LONG)
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
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SplashActivity.this);
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

}
