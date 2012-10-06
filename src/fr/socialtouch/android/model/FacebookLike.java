package fr.socialtouch.android.model;

import java.util.Hashtable;

public class FacebookLike {
	public String mName = "";
	
	@Override
	public String toString() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		table.put("name", mName);
		return table.toString();
	}
}
