/*
 * TutoFragment.java
 * Oct 6, 2012 9:42 AM - Jean-Élie Le Corre
 *
 * Copyright RedShift, 2012.
 */
package fr.socialtouch.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.socialtouch.android.R;

/**
 * @author jean-elie.l
 *
 */
public class TutoFragment extends Fragment {
    
    private String mText;
    private Context mContext;
    
    // UI
    private TextView mTutoTextView;
    
    public TutoFragment(String text) {
        mText = text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tuto, null);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mContext = getActivity();
        
        initUI();
        setContent();
    }

    private void initUI() {
        
        mTutoTextView = (TextView) getView().findViewById(R.id.tv_text);
        
    }
    
    private void setContent() {
        
        if (!TextUtils.isEmpty(mText)) {
            mTutoTextView.setText(mText);
        }
        
    }
    
}
