package com.olmatech.fitness.view;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.olmatech.fitness.main.Common;

public class BaseFragment extends Fragment{
	
	protected Activity parentActivity;
	
	protected View getLayoutView(LayoutInflater inflater, ViewGroup container, 
			final int phoneView, final int tabView)
	{
		View view;
		
		if(Common.isTabletSet())
		{
			if(Common.getIsTablet())
			{
				view = inflater.inflate(tabView, container, false);				
			}
			else
			{
				view =inflater.inflate(phoneView, container, false);
			}
		}
		else if(parentActivity != null)
		{
				if(Common.isTabletDevice(parentActivity))
				{
					view = inflater.inflate(tabView, container, false);
					
				}
				else
				{
					view =inflater.inflate(phoneView, container, false);
				}
		}
		else
		{
			view =inflater.inflate(phoneView, container, false);
		}	
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parentActivity = activity;
	}
	
	

}
