package com.olmatech.fitness.controls;

import android.util.TypedValue;
import android.widget.TextView;

import com.olmatech.fitness.main.Common;

public class WidgetsCommon {
	
	public static void setTextSize(TextView view, final int tabSz, final int phSz)
	{
		boolean isTablet = Common.getIsTablet();
		
		if(isTablet)
		{
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabSz);
		}
		else
		{
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, phSz);
		}
	}
	
	public static void setTextSizeToCommon(TextView view)
	{
		boolean isTablet = Common.getIsTablet();
		
		if(isTablet)
		{
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		}
		else
		{
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		}
	}

}
