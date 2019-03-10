package com.olmatech.fitness.controls;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class AppMainButton extends Button{
	public AppMainButton(Context context) {
		super(context);
		if(!isInEditMode()) setAppearance();
	}
	
	public AppMainButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) setAppearance();
	}
	
	public AppMainButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) setAppearance();
	}
	
	private void setAppearance()
	{
		boolean isTablet = Common.getIsTablet();
		
		if(isTablet)
		{
			this.setTextAppearance(this.getContext(), R.style.mainButtonTextTab);
			this.setPadding(20, 10, 20, 10);
		}
		else
		{
			this.setTextAppearance(this.getContext(), R.style.mainButtonText);			
		}
	}

}
