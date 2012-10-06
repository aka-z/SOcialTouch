package fr.socialtouch.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.utils.SessionStore;

import fr.socialtouch.android.ListAdapter;
import fr.socialtouch.android.R;
import fr.socialtouch.android.model.FacebookLike;
import fr.socialtouch.android.model.FacebookUser;

public class ContactListActivity extends SherlockActivity implements ActionBar.OnNavigationListener {

	private final String TAG = this.getClass().getName();
	
	private ListView mListView;
	private FacebookUser me;
	private List<FacebookUser> mListProfiles = new ArrayList<FacebookUser>();
	private List<String> mListSTT = new ArrayList<String>();
	
	private ListAdapter mListAdapter;
	public static String ALL_STT = "All";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_list);
		
		initUI();
		
//		createMe();
//		createProfiles();
		
//		mListProfiles = new ArrayList<FacebookUser>();
		me = FacebookUser.readObject(this, SessionStore.getFBProfileFormatted(this));
		Log.v(TAG, "me = " + me.toString());
		
		mListProfiles = SyncActivity.getFacebookUsersList();
		
		computeCompat();
		
		mListAdapter = new ListAdapter(this, mListProfiles);
		mListView.setAdapter(mListAdapter);
		initActionBar();
	}
	
	private void initActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);		
		
		mListSTT.add(ALL_STT);
		for(FacebookUser fbProfile : mListProfiles){
			if(!fbProfile.mSocialTouchTag.equals("") && !mListSTT.contains(fbProfile.mSocialTouchTag)){
				mListSTT.add(fbProfile.mSocialTouchTag);
			}
		}
		
		Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = new ArrayAdapter(context, R.layout.sherlock_spinner_item, mListSTT);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
	}
	
	private void initUI(){
		mListView = (ListView) findViewById(R.id.activity_liste_listview);
	}
	
	private void createMe(){
		me = new FacebookUser(this);
		me.mBirthday = "05/07/1988";
		me.mHomeTown.mZipcode = "59600";
		me.mGender=GENDER[0];
		me.mSocialTouchTag=SST[1];
		me.mID = "48865453658";
		me.mListLike = generateListeLike();
		me.mReligion=RELIGION[0];
		me.mUsername="me pseudo";
		me.mLocation.mZipcode="75015";
		Log.v(TAG, "me: "+me.toString());
	}
	
	private void createProfiles(){
		int maxAge = 26;
		int minAge = 18;
		mListProfiles.add(me);
		for(int i=0; i<30; i++){
			FacebookUser profile = new FacebookUser(this);
			profile.mBirthday = new Random().nextInt(12)+"/"+(new Random().nextInt(32-1)+1)+"/"+(1977+new Random().nextInt(30));
			profile.mGender = GENDER[new Random().nextInt(GENDER.length)];
			profile.mID = ""+i;
			profile.mListLike = generateListeLike();
			profile.mReligion = RELIGION[new Random().nextInt(RELIGION.length)];
			profile.mUsername = "pseudo"+i;
			profile.mSocialTouchTag = SST[new Random().nextInt(SST.length)];
			profile.mLocation.mZipcode = ZIPCODE[new Random().nextInt(ZIPCODE.length)];
			profile.mHomeTown.mZipcode = ZIPCODE[new Random().nextInt(ZIPCODE.length)];
			Log.v(TAG, "profil"+i+": "+profile.toString());
			mListProfiles.add(profile);
		}
	}
	
	private ArrayList<FacebookLike> generateListeLike(){
		int maxLike = 10;
		ArrayList<FacebookLike> list = new ArrayList<FacebookLike>();
		for(int j=0; j<(new Random()).nextInt(maxLike)+1; j++){
			FacebookLike fblike = new FacebookLike();
			fblike.mName = LIKES[new Random().nextInt(LIKES.length)];
			list.add(fblike);
		}
		return list;
	}
	
	private void computeCompat(){
		for(FacebookUser fbProfile : mListProfiles)
			Log.v(TAG, "Compat with "+fbProfile.mUsername+": "+fbProfile.getCompatibilityWith(me));
		
	}
	
	private String[] LIKES = {
			 "Da Vinci Code",
	    	 "Alto Sax",
	         "Guitar",
	         "avast! antivirus",
	         "Facebook Graph API Explorer",
	         "Que Faire \u00e0 Paris ?",
	         "L'Or Rose",
	         "Photography",
	         "My Friend Map",
	         "Android",
	         "EADS",
	         "National Aeronautics and Space Administration - NASA",
	         "Raid 4L Trophy",
	         "Inception",
	         "The Big Blue",
	         "The Black Dahlia",
	         "The Da Vinci Code",
	         "Two Door Cinema Club",
	         "ENSEA",
	         "EISTI",
	         "Have sex",
	         "Big boobs",
	         "Batman: The Dark Knight",
	         "Illinois Institute of Technology",
	         "Radiohead",
	         "Phoenix",
	         "General Elektriks",
	         "The White Shoes",
	         "Muse",
	         "Tennis",
	         "Swimming",
	};
	
	private String[] RELIGION = {"Catho","Boudiste","Cassouletiste","Choucroutiste","Andro�diste","BeMyAppiste", "Ath�"};
	private String[] GENDER = {"Male", "Female", "Asexu�", "Bisexuel"};
	private String[] ZIPCODE = {"62352","75015","75001","59600","62500","59000","59611","01234","05689","75013"};
	private String[] SST = {"!mabite", "!patricsebastien", "!developer", "!designer"};
	
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Log.v(TAG, "onNavigationItemSelected("+itemPosition+") => sst="+mListSTT.get(itemPosition));
		mListAdapter.refresh(mListSTT.get(itemPosition));
		return false;
	}
}
