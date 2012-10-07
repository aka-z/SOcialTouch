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
import fr.socialtouch.android.model.FacebookUser;

public class FBConnectActivity extends SherlockActivity implements OnClickListener {

	private Button btnFBConnect;
	
	private static final String PROFILE_1 = "bhart|Bret Hart|0|dev|12-04-83|75015|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_2 = "bhart|#|#|#|#|#|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_3 = "#|Bret Hart|0|#|#|#|92340||World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_4 = "bhart|#|1|crea|#|#|#|islam|World taekwondo family|SPAMM|American Dad|#|#|#|#|#|#|#";
	private static final String PROFILE_5 = "#|#|0|#|#|75015|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|#|#|#";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		btnFBConnect = (Button)findViewById(R.id.btn_fb_connect);
		btnFBConnect.setText("Vas-y, connectes-toi !");
		// TEST
		//FacebookUser.readObject(this, PROFILE_1);
		//FacebookUser.readObject(this, PROFILE_2);
		//FacebookUser.readObject(this, PROFILE_3);
		FacebookUser.readObject(this, PROFILE_4);
		FacebookUser.readObject(this, PROFILE_5);
		
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

}
