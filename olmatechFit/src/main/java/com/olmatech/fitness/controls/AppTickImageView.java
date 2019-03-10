package com.olmatech.fitness.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.olmatech.fitness.main.Common;

public class AppTickImageView extends ImageView{

	public AppTickImageView(Context context) {
		super(context);
		//if(!isInEditMode()) setAppearance(getContext());
	}
	
	public AppTickImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//if(!isInEditMode()) setAppearance(getContext());
	}
	
	public AppTickImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//if(!isInEditMode()) setAppearance(getContext());
	}
	
//	private void setAppearance(final Context cont)
//	{
//		int sz;
//		boolean isTablet = Common.getIsTablet();
//		if(isTablet)
//		{
//			sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, cont.getResources().getDisplayMetrics());
//		}
//		else sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, cont.getResources().getDisplayMetrics());
//		
//		LayoutParams params = this.getLayoutParams();
//		params.width = sz;
//		params.height = sz;
//		this.setLayoutParams(params);
//		
//	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int sz;
		boolean isTablet = Common.getIsTablet();
		if(isTablet)
		{
			sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, this.getContext().getResources().getDisplayMetrics());
		}
		else sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getContext().getResources().getDisplayMetrics());
		
		setMeasuredDimension(sz,sz); 
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}
	
	

}
