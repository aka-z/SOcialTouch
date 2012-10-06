package fr.socialtouch.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import fr.socialtouch.android.SocialTouchApp;

public class TextViewST extends TextView {
	
	public TextViewST(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initFont();
	}
	
	public TextViewST(Context context) {
	    super(context);
	    initFont();
	}
	
	public TextViewST(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDefStyle = defStyle;
		initFont();
	}
	
	private int mDefStyle = 1;
	
	public void initFont(){
	    setTypeface(SocialTouchApp.mFont, mDefStyle);
	}
	
//	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//	private Typeface mFace;
//	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		mFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/JosefinSlab-Regular.ttf");
//		mPaint.setTypeface(mFace);
//		//canvas.drawText(name, 30, y, mPaint);
//		
//	}

}
