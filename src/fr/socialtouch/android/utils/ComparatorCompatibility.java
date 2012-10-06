package fr.socialtouch.android.utils;

import java.util.Comparator;

import fr.socialtouch.android.model.FacebookUser;

public class ComparatorCompatibility implements Comparator<FacebookUser> {

	@Override
	public int compare(FacebookUser lhs, FacebookUser rhs) {
		return lhs.getCompatibility()>rhs.getCompatibility()?-1:1;
	}

}
