package fr.socialtouch.android.activity;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.utils.SessionStore;

import fr.socialtouch.android.R;
import fr.socialtouch.android.model.FacebookUser;

public class SyncActivity extends Activity {
    
    private static final String LOG_TAG = SyncActivity.class.getName();

    
    
	// max byte size
	public static final int TAG_SIZE = 1504;
	private static final String PROFILE_1 = "bhart|Bret Hart|0|12-04-83|75015|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_2 = "bhart|#|#|#|#|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_3 = "#|Bret Hart|0|#|#|92340||World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_4 = "bhart|#|1|#|#|#|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";
	private static final String PROFILE_5 = "#|#|0|#|75015|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";

	private static List<FacebookUser> facebookUserList;

	static {
		facebookUserList = new ArrayList<FacebookUser>();
	}
	
	EditText mTagEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matching);
		
		mTagEditText = (EditText) findViewById(R.id.ed_hashtag);
	}
	
	private void tagSynchronization (Intent intent) {
//	       Intent intent = getIntent();
	        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	        boolean alreadyTag = false;

	        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
	            // tag d�tect�
	            MifareClassic mifare = MifareClassic.get(tag);
	            // on peut lire et �crire ici
	            try {

	                //clearTag(mifare);
	                
	                String prof_string = "";
	                String data = readFromTag(mifare);

	                String[] profiles;
	                profiles = data.split("(\\|\\|)");

	                if (profiles.length == 0) {
	                    prof_string = SessionStore.getFBProfileFormatted(this) + "||";
	                    clearTag(mifare);
	                    writeToTag(mifare, prof_string, false);
	                    
	                    Toast.makeText(this, "Tu es le premier !", Toast.LENGTH_LONG).show();
	                    
	                } else {
	                    synchronized (facebookUserList) {
	                        facebookUserList.clear();
	                        for (int i = 0; i < profiles.length - 1; i++) {
	                            // retrieve and store user
	                            FacebookUser user = FacebookUser.readObject(this, profiles[i]);
	                            
                                // check if current user is already in list
	                            if (!user.mUsername.equalsIgnoreCase(SessionStore.getUserName(this))) {
	                                facebookUserList.add(user);
                                } else {
                                    alreadyTag = true;
                                }
	                            
	                            // TODO CHECK
	                            prof_string += profiles[i] + "||";
	                            Log.e("SocialTouch", "Read Profile data = " + profiles[i]);
	                        }
	                    }

	                    if (!alreadyTag
	                            && SessionStore.getFBProfileFormatted(this).length()
	                                    + prof_string.length() < TAG_SIZE) {
	                        prof_string += SessionStore.getFBProfileFormatted(this) + "||";
	                        clearTag(mifare);
	                        writeToTag(mifare, prof_string, false);
	                    }
	                    
	                    if (mTagEditText != null && mTagEditText.getText().toString().length() > 0) {
                            SessionStore.setSOcialTouchTag(this, mTagEditText.getText().toString());
                        }
	                    
	                    Intent listIntent = new Intent(this, ContactListActivity.class);
	                    startActivity(listIntent);
	                }

	                // FacebookUser user = FacebookUser.readObject(this,
	                // profiles[0]);
	                // FacebookUser user2 = FacebookUser.readObject(this,
	                // profiles[1]);

//	              resultFacebook.setText(facebookUserList.toString());

	                Log.e("SocialTouch", "Tag data = " + data);
	            } catch (IOException e) {
	                // oh no !
	                e.printStackTrace();
	            }
	        }
	}

	@Override
	public void onResume() {
		super.onResume();

		tagSynchronization(getIntent());
		

	}

	public void clearTag(MifareClassic mifare) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TAG_SIZE; i++) {
			sb.append(" ");
		}
		writeToTag(mifare, sb.toString(), false);
	}

	// TODO append not implemented
	public void writeToTag(MifareClassic mifare, String inData, boolean append) throws IOException {
		mifare.connect();
		boolean auth = false;
		boolean writeEnd = false;
		int sector = 0;
		int block = 0;
		byte[] value = inData.getBytes(Charset.forName("ASCII"));
		int end = value.length
				+ (MifareClassic.BLOCK_SIZE - value.length % MifareClassic.BLOCK_SIZE);
		byte[] toWrite = new byte[MifareClassic.BLOCK_SIZE];

		for (int i = 0; i < end; i++) {
			// Change sector every 3 blocks, start at sector 1
			if (i % (3 * MifareClassic.BLOCK_SIZE) == 0) {
				sector++;
				auth = mifare.authenticateSectorWithKeyB(sector, MifareClassic.KEY_DEFAULT); // A
																								// =
																								// NFC
																								// FORUM
																								// B
																								// =
																								// DEFAULT
				if (!auth)
					throw new IOException("Cannot authenticate sector");
				block = 0;
			}

			if (i >= value.length) {
				writeEnd = true;
				toWrite[i % MifareClassic.BLOCK_SIZE] = 0;
			} else {
				toWrite[i % MifareClassic.BLOCK_SIZE] = value[i];
			}
			if ((i + 1) % MifareClassic.BLOCK_SIZE == 0) {
				BigInteger bi = new BigInteger(toWrite);
				String hexrepresentation = bi.toString(16);
				Log.e("NFC WRITER", "Write " + hexrepresentation + " to sector " + sector
						+ " block " + block + " aka " + (block + mifare.sectorToBlock(sector)));
				mifare.writeBlock(block + mifare.sectorToBlock(sector), toWrite);
				block++;
			}
		}
		mifare.close();
	}

	public String readFromTag(MifareClassic mifare) throws IOException {
		mifare.connect();

		StringBuilder sb = new StringBuilder();
		int sector = 0;
		boolean auth = false;
		for (int i = 4; i < TAG_SIZE / MifareClassic.BLOCK_SIZE; i++) {
			if (i % 4 == 0) {
				sector++;
				auth = mifare.authenticateSectorWithKeyB(sector, MifareClassic.KEY_DEFAULT);
				if (!auth)
					throw new IOException("Cannot authenticate to sector " + sector);
			}

			if (i > 4 && (i % 4) == 3)
				continue;

			// Log.e("TEST","i:"+i+" bloc:"+ new
			// String(mifare.readBlock(i),Charset.forName("ASCII")));
			sb.append(new String(mifare.readBlock(i), Charset.forName("ASCII")));
		}
		mifare.close();
		return sb.toString();
	}

	@Override
	public void onNewIntent(Intent intent) {
	    Log.v(LOG_TAG, "onNewIntent");
        setIntent(intent);

        tagSynchronization(intent);
	}

	public static List<FacebookUser> getFacebookUsersList() {
		return facebookUserList;
	}
}
