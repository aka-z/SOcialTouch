package fr.socialtouch.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.socialtouch.android.model.FacebookUser;
import fr.socialtouch.android.utils.ComparatorCompatibility;

public class ListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<FacebookUser> mList;
	private LayoutInflater mLayoutInflater;
	
	public ListAdapter(Context context, ArrayList<FacebookUser> list){
		mContext = context;
		mList = list;
		Collections.sort(list, new ComparatorCompatibility());
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
			
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
	
		FacebookUser fb = getItem(position);
		
		holder.mTVpseudo.setText(fb.mUsername);
		holder.mTVsttag.setText(fb.mSocialTouchTag);
		holder.mTVrate.setText((int)fb.getCompatibility()+"%");
		
		String text;
		int pointCommunNumber = fb.getPointsCommumNumber();
		if(pointCommunNumber!=0)
			text = mContext.getString(R.string.cell_liste_pointcommun).replace("@r", ""+pointCommunNumber).replace("@s", pointCommunNumber>1?"s":"");
		else
			text = mContext.getString(R.string.cell_liste_pointcommun_no);
		holder.mTVpointcommun.setText(text);
		
		if(pointCommunNumber>=1){
			Collections.shuffle(fb.getPointsCommunsListe());
			holder.mTVpoint0.setText(fb.getPointsCommunsListe().get(0));
			holder.mTVpoint0.setVisibility(View.VISIBLE);
		}
		else
			holder.mTVpoint0.setVisibility(View.INVISIBLE);
		
		if(pointCommunNumber>=2){
			holder.mTVpoint1.setText(fb.getPointsCommunsListe().get(1));
			holder.mTVpoint1.setVisibility(View.VISIBLE);
		}
		else
			holder.mTVpoint1.setVisibility(View.INVISIBLE);
		
		if(pointCommunNumber>=3){
			holder.mTVpoint2.setText(fb.getPointsCommunsListe().get(2));
			holder.mTVpoint2.setVisibility(View.VISIBLE);
		}
		else
			holder.mTVpoint2.setVisibility(View.INVISIBLE);
			
		
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
	}
}

