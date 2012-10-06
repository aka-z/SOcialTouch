package fr.socialtouch.android.activity;

import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

import fr.socialtouch.android.ListAdapter;
import fr.socialtouch.android.R;
import fr.socialtouch.android.model.FacebookLike;
import fr.socialtouch.android.model.FacebookUser;

public class ContactListActivity extends SherlockActivity {

	private final String TAG = this.getClass().getName();
	
	private ListView mListView;
	private FacebookUser me;
	private ArrayList<FacebookUser> mListProfiles = new ArrayList<FacebookUser>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_list);
		
		initUI();
		
		createMe();
		createProfiles();
		
		computeCompat();
		
		mListView.setAdapter(new ListAdapter(this, mListProfiles));
	}
	
	private void initUI(){
		mListView = (ListView) findViewById(R.id.activity_liste_listview);
	}
	
	private void createMe(){
		me = new FacebookUser(this);
		me.mBirthday = "05/07/1988";
		me.mHomeTown.mZipcode = "59600";
		me.mGender=GENDER[0];
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
	
	private String[] RELIGION = {"Catho","Boudiste","Cassouletiste","Choucroutiste","Androïdiste","BeMyAppiste", "Athé"};
	private String[] GENDER = {"Male", "Female", "Asexué", "Bisexuel"};
	//private String[] LOCATION = {"Maubeuge", "Paris", "Cergy", "Lyon", "Lille", "Strasbourg", "Nantes", "Bordeaux", "Boulogne-Billancourt"};
	private String[] ZIPCODE = {"62352","75015","75001","59600","62500","59000","59611","01234","05689","75013"};
}
