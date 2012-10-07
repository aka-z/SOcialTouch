/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.facebook.android.Facebook;

public class SessionStore {

	private static final String KEY = "FbPreferencesName";

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";

	private static final String UPDATED_TIME = "updated_time";
	private static final String NAME = "name";
	private static final String USERNAME = "user_name";
	private static final String SOCIAL_TOUCH_TAG = "so_tag";
	private static final String GENDER = "gender";
	private static final String BIRTHDAY = "birthday";
	private static final String TOWN = "town";
	private static final String HOMETOWN = "hometown";
	private static final String RELIGION = "religion";
	private static final String LIKES = "likes";

	public static final byte MALE = 0x00;
	public static final byte FEMALE = 0x01;

	public static boolean save(Facebook session, Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, session.getAccessToken());
		editor.putLong(EXPIRES, session.getAccessExpires());
		return editor.commit();
	}

	public static boolean restore(Facebook session, Context context) {
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		session.setAccessToken(savedSession.getString(TOKEN, null));
		session.setAccessExpires(savedSession.getLong(EXPIRES, 0));
		return session.isSessionValid();
	}

	public static void clear(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static String getUpdatedTime(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.getString(UPDATED_TIME, null);
	}

	public static void setUpdatedTime(Context context, String updatedTime) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(UPDATED_TIME, updatedTime);
		editor.commit();
	}

	public static String getUserName(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(USERNAME, null);
	}

	public static void setUserName(Context context, String userName) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(USERNAME, userName);
		editor.commit();
	}

	public static String getName(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(NAME, null);
	}

	public static void setName(Context context, String name) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(NAME, name);
		editor.commit();
	}

	public static String getSOcialTouchTag(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(SOCIAL_TOUCH_TAG, null);
	}

	public static void setSOcialTouchTag(Context context, String tag) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(SOCIAL_TOUCH_TAG, tag);
		editor.commit();
	}

	public static String getBirthday(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(BIRTHDAY, null);
	}

	public static void setBirthday(Context context, String birthday) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(BIRTHDAY, birthday);
		editor.commit();
	}

	public static String getGender(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(GENDER, null);
	}

	public static void setGender(Context context, String gender) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(GENDER, gender);
		editor.commit();
	}

	public static String getTown(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(TOWN, null);
	}

	public static void setTown(Context context, String town) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOWN, town);
		editor.commit();
	}

	public static String getHometown(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(HOMETOWN, null);
	}

	public static void setHometown(Context context, String hometown) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(HOMETOWN, hometown);
		editor.commit();
	}

	public static String getReligion(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(RELIGION, null);
	}

	public static void setReligion(Context context, String religion) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(RELIGION, religion);
		editor.commit();
	}

	public static List<String> getLikesList(Context context) {
		String likes = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(LIKES,
				null);
		List<String> likesList = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(likes, "|");
		while (token.hasMoreTokens()) {
			likesList.add(token.nextToken());
		}
		return likesList;
	}

	public static void setLikes(Context context, List<String> likesList) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		if (likesList != null && !likesList.isEmpty()) {
			String likes = "";
			for (String s : likesList) {
				likes += s + "|";
			}
			// remove the last pipe
			likes = likes.substring(0, likes.length() - 1);
			editor.putString(LIKES, likes);
			editor.commit();
		}
	}

	public static String getLikesFormatted(Context context) {
		return context.getSharedPreferences(KEY, Context.MODE_PRIVATE).getString(LIKES, null);
	}

	// "516515302|bhart|Bret Hart|0|12-04-83|75015|92340|islam|World taekwondo family|SPAMM|American Dad|Mark The Ugly|GET SOME|Hoax-Slayer|PARIS IS BURNING|Cedric Ben Abdallah|Action Discru\00e8te|All United Drinks";

	public static String getFBProfileFormatted(Context context) {
		String res = "";
		// username
		if (getUserName(context) != null) {
			res += getUserName(context) + "|";
		} else {
			res += "#|";
		}
		// name
		if (getName(context) != null) {
			res += getName(context)+ "|";
		} else {
			res += "#|";
		}
		// gender
		if (getGender(context) != null) {
			res += getName(context) == "male" ? MALE + "|": FEMALE+ "|";
		} else {
			res += "#|";
		}
		// so tag
		if (getSOcialTouchTag(context) != null) {
			res += getSOcialTouchTag(context) + "|";
		} else {
			res += "#|";
		}
		// birthday
		if (getBirthday(context) != null) {
			res += getBirthday(context)+ "|";
		} else {
			res += "#|";
		}
		// town
		if (getTown(context) != null) {
			res += getTown(context)+ "|";
		} else {
			res += "#|";
		}
		// home town
		if (getHometown(context) != null) {
			res += getHometown(context)+ "|";
		} else {
			res += "#|";
		}
		// religion
		if (getReligion(context) != null) {
			res += getReligion(context)+ "|";
		} else {
			res += "#|";
		}
		// likes
		if (getLikesFormatted(context) != null) {
			res += getLikesFormatted(context);
		}
		return res;
	}
}
