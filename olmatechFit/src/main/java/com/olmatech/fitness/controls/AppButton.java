package com.olmatech.fitness.controls;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class AppButton extends Button{

	public AppButton(Context context) {
		super(context);
		if(!isInEditMode()) setAppearance();
	}
	
	public AppButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) setAppearance();
	}
	
	public AppButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) setAppearance();
	}
	
	private void setAppearance()
	{
		boolean isTablet = Common.getIsTablet();
		
		//<item name="android:paddingTop">6dp</item>
      	//<item name="android:paddingBottom">6dp</item>
		if(isTablet)
		{
			this.setTextAppearance(this.getContext(), R.style.ListButtonTextTab);
			this.setPadding(20, this.getPaddingTop(), 20, this.getPaddingBottom());
		}
		else
		{
			this.setTextAppearance(this.getContext(), R.style.ListButtonText);			
		}
	}

}
