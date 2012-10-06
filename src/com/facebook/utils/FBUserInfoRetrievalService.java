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

	private static final int MAX_LIKES_NUMBER = 10;

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
					"id,name,updated_time,gender,birthday,religion,username,hometown,location,likes.fields(likes,name)");
			// get information about the currently logged in user
			SocialTouchApp.getAsyncFacebookRunner().request("me", params,
					new UserInfoRequestListener());
		}
	}

	private class UserInfoRequestListener implements RequestListener {

		/**
		 * Request informations about user. If user is under age to connect
		 * Heineken Plus on Facebook, he is automatically logout.
		 */
		@Override
		public void onComplete(String response, Object state) {
			storeFBInformations(response);
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
				SessionStore.setId(this, getStringfFromJSONObject(jsonObject, "id"));
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
	}

	private String getStringfFromJSONObject(JSONObject jsonObject, String key) {
		String value = null;
		try {
			value = jsonObject.getString(key);
		} catch (JSONException e) {
		}
		Log.i(getClass().getSimpleName(), "KEY = " + key + " VALUE = " + value);
		return value;
	}

	private List<String> getLikesFromJSONObject(JSONObject jsonObject) {
		List<String> listLikes = null;
		Map<String, Integer> likesNameWithLikesNumber = new HashMap<String, Integer>();
		try {
			JSONObject like;
			JSONArray likesArray = jsonObject.getJSONObject("likes").getJSONArray("data");
			for (int i = 0; i < likesArray.length(); i++) {
				like = likesArray.getJSONObject(i);
				likesNameWithLikesNumber.put(like.getString("name"), like.getInt("likes"));
			}
			ValueComparator bvc = new ValueComparator(likesNameWithLikesNumber);
			TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
			sortedMap.putAll(likesNameWithLikesNumber);
			ArrayList<String> arrayListLikes = new ArrayList<String>(sortedMap.keySet());
			if (arrayListLikes.size() > MAX_LIKES_NUMBER) {
				listLikes = arrayListLikes.subList(0, MAX_LIKES_NUMBER);
			}
			Log.e(getClass().getSimpleName(), "likesFormated = " + listLikes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
