package com.facebook.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(getClass().getSimpleName(), "ON START ");
		retrieveUserInfo();
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private void retrieveUserInfo() {
		SessionStore.restore(SocialTouchApp.getFacebook(), this);
		if (SocialTouchApp.getFacebook().isSessionValid()) {
			Bundle params = new Bundle();
			params.putString("fields", "name,username,updated_time,birthday");
			// get information about the currently logged in user
			SocialTouchApp.getAsyncFacebookRunner().request("me", params, new UserInfoRequestListener());
		}
	}

	private class UserInfoRequestListener implements RequestListener {

		/**
		 * Request informations about user. If user is under age to connect
		 * Heineken Plus on Facebook, he is automatically logout.
		 */
		@Override
		public void onComplete(String response, Object state) {
			
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
	
	
	private void storeFBInformations(String response){
		SessionStore.displayInfo(getApplicationContext());
		String fbUpdatedTime = null;
		try {
			Log.i(getClass().getSimpleName(), "RESPONSE FROM FB OK : " + response);
			JSONObject jsonObject = new JSONObject(response);

			fbUpdatedTime = jsonObject.getString("updated_time");
			String localInfoTime = SessionStore.getUpdatedTime(FBUserInfoRetrievalService.this);
			if (localInfoTime == null || !fbUpdatedTime.equals(localInfoTime)) {
				SessionStore.setId(this, jsonObject.getString("id"));
				SessionStore.setName(this, jsonObject.getString("name"));
				SessionStore.setUserName(this, jsonObject.getString("username"));
				SessionStore.setGender(this, jsonObject.getString("gender"));
				SessionStore.setBirthday(this, jsonObject.getString("birthday"));
				SessionStore.setTown(this, jsonObject.getString("town"));
				SessionStore.setHometown(this, jsonObject.getString("hometown"));
				SessionStore.setReligion(this, jsonObject.getString("religion"));
				// retrieve LIKES
				// birthday format MM/dd/yyyy
				//birthday = jsonObject.getString("birthday");
				
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
