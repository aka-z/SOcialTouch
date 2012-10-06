package fr.socialtouch.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.socialtouch.android.activity.ContactListActivity;
import fr.socialtouch.android.model.FacebookUser;
import fr.socialtouch.android.net.ImageDownloader;
import fr.socialtouch.android.utils.ComparatorCompatibility;

public class ListAdapter extends BaseAdapter {

	private final String TAG = this.getClass().getName();
	
	private Context mContext;
	private ArrayList<FacebookUser> mListAll;
	private ArrayList<FacebookUser> mList = new ArrayList<FacebookUser>();
	private LayoutInflater mLayoutInflater;
	
	public ListAdapter(Context context, List<FacebookUser> list){
		Log.v(TAG, "ListAdapter()");
		mContext = context;
		mListAll = new ArrayList<FacebookUser>(list);
		createListToDisplay(ContactListActivity.ALL_STT);
		mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public FacebookUser getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.cell_list, null);
			
			holder.mTVpseudo = (TextView) convertView.findViewById(R.id.cell_list_pseudo);
			holder.mTVsttag = (TextView) convertView.findViewById(R.id.cell_list_sttag);
			holder.mTVpointcommun = (TextView) convertView.findViewById(R.id.cell_list_pointcommun);
			holder.mTVpoint0 = (TextView) convertView.findViewById(R.id.cell_list_points0);
			holder.mTVpoint1 = (TextView) convertView.findViewById(R.id.cell_list_points1);
			holder.mTVpoint2 = (TextView) convertView.findViewById(R.id.cell_list_points2);
			holder.mTVrate = (TextView) convertView.findViewById(R.id.cell_list_rate);
			holder.mIVavatar = (ImageView) convertView.findViewById(R.id.cell_list_avatar);
			
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
	
		FacebookUser fb = getItem(position);
		
		holder.mTVpseudo.setText(fb.mName);
		holder.mTVsttag.setText(fb.mSocialTouchTag);
		holder.mTVrate.setText((int)fb.getCompatibility()+"%");

		// TODO : fix male / female
		if(fb.mGender.equalsIgnoreCase("Female")){
			holder.mIVavatar.setImageResource(R.drawable.profil_m);
			holder.mIVavatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
		else if(fb.mGender.equalsIgnoreCase("Male")){
			holder.mIVavatar.setImageResource(R.drawable.profil_f);
			holder.mIVavatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		}
		else
			holder.mIVavatar.setImageResource(R.drawable.profil_unknown_interro);
			
		ImageDownloader.downloadBitmap(holder.mIVavatar, "https://graph.facebook.com/" + fb.mUsername + "/picture?type=normal");
		
		String text;
		int pointCommunNumber = fb.getPointsCommumNumber();
		if(pointCommunNumber!=0)
			text = mContext.getString(R.string.cell_liste_pointcommun).replace("@r", ""+pointCommunNumber).replace("@s", pointCommunNumber>1?"s":"");
		else
			text = mContext.getString(R.string.cell_liste_pointcommun_no);
		holder.mTVpointcommun.setText(text);
		
		if(pointCommunNumber>=1){
			holder.mTVpoint0.setText(fb.getPointsCommunsListe().get(0));
			holder.mTVpoint0.setVisibility(View.VISIBLE);
		}
		else
			holder.mTVpoint0.setVisibility(View.GONE);
		
		if(pointCommunNumber>=2){
			holder.mTVpoint1.setText(fb.getPointsCommunsListe().get(1));
			holder.mTVpoint1.setVisibility(View.VISIBLE);
		}
		else
			holder.mTVpoint1.setVisibility(View.GONE);
		
		if(pointCommunNumber>=3){
			holder.mTVpoint2.setText(fb.getPointsCommunsListe().get(2));
			holder.mTVpoint2.setVisibility(View.VISIBLE);
		}
		else
			holder.mTVpoint2.setVisibility(View.GONE);
			
		
		return convertView;
	}

	public class ViewHolder{
		public TextView mTVpseudo;
		public TextView mTVsttag;
		public TextView mTVpointcommun;
		public TextView mTVpoint0;
		public TextView mTVpoint1;
		public TextView mTVpoint2;
		public TextView mTVrate;
		public ImageView mIVavatar;
	}

	public void createListToDisplay(String stt){
		Log.v(TAG, "createListToDisplay("+stt+")");
		mList.clear();
		if(stt.equalsIgnoreCase(ContactListActivity.ALL_STT)){
			mList = new ArrayList<FacebookUser>(mListAll);
		}
		else{
			for(FacebookUser fbProfile : mListAll){
				if(fbProfile.mSocialTouchTag.equalsIgnoreCase(stt)){
					mList.add(fbProfile);
				}
			}
		}
		Collections.sort(mList, new ComparatorCompatibility());
	}
	
	public void refresh(String stt) {
		Log.v(TAG, "refresh("+stt+")");
		createListToDisplay(stt);
		notifyDataSetChanged();
	}
}

