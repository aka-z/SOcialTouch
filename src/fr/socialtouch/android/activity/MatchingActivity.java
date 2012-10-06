package fr.socialtouch.android.activity;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.util.Log;

public class MatchingActivity extends Activity {
	
	@Override
	public void onResume() {
	    super.onResume();

	    if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
	        Log.d("MatchingActivity", "tag Detected");
	    }
	    //process the msgs array
	}
}
