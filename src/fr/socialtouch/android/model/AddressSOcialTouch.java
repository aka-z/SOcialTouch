package fr.socialtouch.android.model;

import java.util.Hashtable;

public class AddressSOcialTouch {
	public String mZipcode = "";
	public String mTown = "";
	
	@Override
	public String toString() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		table.put("zipcode", mZipcode);
		table.put("town", mTown);
		return table.toString();
	}
}
