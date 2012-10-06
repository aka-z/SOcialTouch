package com.facebook.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

import fr.socialtouch.android.SocialTouchApp;

public class FBUserInfoRetrievalService extends Service {

	private static final int REQUIRED_NUMBER_OF_LIKES = 10;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		retrieveUserInfo();
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private void retrieveUserInfo() {
		SessionStore.restore(SocialTouchApp.getFacebook(), this);
		if (SocialTouchApp.getFacebook().isSessionValid()) {
			Bundle params = new Bundle();
			// "name,username,updated_time,birthday"
			// id,name,gender,birthday,religion,username,hometown,location,likes.fields(likes,name)
			params.putString("fields",
					"name,updated_time,gender,birthday,religion,username,hometown,location,likes.fields(likes,name)");
			// get information about the currently logged in user
			SocialTouchApp.getAsyncFacebookRunner().request("me", params,
					new UserInfoRequestListener());
		} else{
			Log.e(getClass().getSimpleName(), "retrieveUserInfo FAILED");
		}
	}

	private class UserInfoRequestListener implements RequestListener {

		/**
		 * Request informations about user. If user is under age to connect
		 * Heineken Plus on Facebook, he is automatically logout.
		 */
		@Override
		public void onComplete(String response, Object state) {
			Log.e(getClass().getSimpleName(), "onComplete");
			storeFBInformations(response);
		}

		@Override
		public void onIOException(IOException e, Object state) {
			Log.e(getClass().getSimpleName(), "onIOException");
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e, Object state) {
			Log.e(getClass().getSimpleName(), "onFileNotFoundException");
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object state) {
			Log.e(getClass().getSimpleName(), "onMalformedURLException");
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Log.e(getClass().getSimpleName(), "onFacebookError");
		}
	}

	public void fbDisconnect() {
		if (SocialTouchApp.getFacebook().isSessionValid()) {
			SocialTouchApp.getAsyncFacebookRunner().logout(this, new RequestListener() {
				@Override
				public void onComplete(String response, Object state) {
					SessionStore.clear(getApplicationContext());

				}

				@Override
				public void onIOException(IOException e, Object state) {
				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e, Object state) {
				}

				@Override
				public void onMalformedURLException(MalformedURLException e, Object state) {
				}

				@Override
				public void onFacebookError(FacebookError e, Object state) {
				}
			});
		} else {
			// We just clear the local information
			SessionStore.clear(getApplicationContext());
		}
	}

	private void storeFBInformations(String response) {
		Log.e(getClass().getSimpleName(), "RESPONSE FROM FB OK : " + response);
		// SessionStore.displayInfo(getApplicationContext());
		String fbUpdatedTime = null;
		try {

			JSONObject jsonObject = new JSONObject(response);

			fbUpdatedTime = getStringfFromJSONObject(jsonObject, "updated_time");
			String localInfoTime = SessionStore.getUpdatedTime(FBUserInfoRetrievalService.this);
			if (localInfoTime == null || !fbUpdatedTime.equals(localInfoTime)) {
				SessionStore.setName(this, getStringfFromJSONObject(jsonObject, "name"));
				SessionStore.setUserName(this, getStringfFromJSONObject(jsonObject, "username"));
				SessionStore.setGender(this, getStringfFromJSONObject(jsonObject, "gender"));
				// birthday format MM/dd/yyyy
				SessionStore.setBirthday(this, getStringfFromJSONObject(jsonObject, "birthday"));
				SessionStore.setTown(this, getStringfFromJSONObject(jsonObject, "town"));
				SessionStore.setHometown(this, getStringfFromJSONObject(jsonObject, "hometown"));
				SessionStore.setReligion(this, getStringfFromJSONObject(jsonObject, "religion"));
				SessionStore.setLikes(this, getLikesFromJSONObject(jsonObject));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e(getClass().getSimpleName(), SessionStore.getFBProfileFormatted(this));
	}

	private String getStringfFromJSONObject(JSONObject jsonObject, String key) {
		String value = null;
		try {
			value = jsonObject.getString(key);
		} catch (JSONException e) {
		}
		//Log.i(getClass().getSimpleName(), "KEY = " + key + " VALUE = " + value);
		return value;
	}

	private List<String> getLikesFromJSONObject(JSONObject jsonObject) {
		List<String> listLikes = null;
		ArrayList<String> arrayListLikes = null;
		TreeMap<String, Integer> sortedMap = null;
		Map<String, Integer> likesNameWithLikesNumber = new HashMap<String, Integer>();
		try {
			JSONObject like;
			// retrieve likes from data
			JSONArray likesArray = jsonObject.getJSONObject("likes").getJSONArray("data");
			for (int i = 0; i < likesArray.length(); i++) {
				like = likesArray.getJSONObject(i);
				// retrieve name and likes from likes
				likesNameWithLikesNumber.put(like.getString("name"), like.getInt("likes"));
			}
			// sort likes from the most to the lowest number of likes
			ValueComparator bvc = new ValueComparator(likesNameWithLikesNumber);
			sortedMap = new TreeMap<String, Integer>(bvc);
			sortedMap.putAll(likesNameWithLikesNumber);
			arrayListLikes = new ArrayList<String>(sortedMap.keySet());
			// add padding with # to have exactly REQUIRED_NUMBER_OF_LIKES elements
			if (arrayListLikes.size() < REQUIRED_NUMBER_OF_LIKES) {
				for (int i = arrayListLikes.size(); i < REQUIRED_NUMBER_OF_LIKES; i++) {
					arrayListLikes.add("#");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (sortedMap != null) {
			arrayListLikes = new ArrayList<String>(sortedMap.keySet());
		} else {
			// create an array and add padding
			arrayListLikes = new ArrayList<String>(REQUIRED_NUMBER_OF_LIKES);
			for (int i = 0; i < arrayListLikes.size(); i++) {
				arrayListLikes.add("#");
			}
		}
		// remove excessive likes if too many are present
		if (arrayListLikes.size() > REQUIRED_NUMBER_OF_LIKES) {
			listLikes = arrayListLikes.subList(0, REQUIRED_NUMBER_OF_LIKES);
		}
		Log.e(getClass().getSimpleName(), "likesFormated = " + listLikes);
		return listLikes;
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;

		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
