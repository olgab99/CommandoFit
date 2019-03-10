package com.olmatech.fitness.ui;

import kankan.wheel.widget.adapters.WeightLbWheelAdapter;

import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//app settings
		DataStore ds = new DataStore(this);
		boolean isTablet;
	    if(ds.getIsTabletSet())
	     {
	        	isTablet = ds.getIsTablet();
	     }
	     else
	     {
	        	isTablet = Common.isTabletDevice(this);
	        	ds.setIsTablet(isTablet);
	     }  
	    Common.setIsTablet(isTablet);
		final int weightInterval = ds.getWeightWheelInterval();
		final int weightBreak =ds.getWeightWheelBreak();
				
			//changing
		if(weightInterval != Common.EMPTY_VALUE)
		{			
			WeightLbWheelAdapter.setDeltaValue(weightInterval);
		}
		if(weightBreak != Common.EMPTY_VALUE)
		{			
			WeightLbWheelAdapter.setBreakValue(weightBreak);
		}				
	        
        Intent intent = new Intent(this, SplashActivityPhone.class);
		startActivity(intent);
        finish();
	}

}
