package com.olmatech.fitness.ui;

import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;

import com.olmatech.fitness.R;

public abstract class ExpandableListBase extends BaseFragmentActivity{

		
	protected ExpandableListView getExpandableListView()
	{
		View v = this.findViewById(R.id.log_list);
		if(v == null || !(v instanceof ExpandableListView)) return null;
		
		return (ExpandableListView)v;
	}
	
	protected void setListAdapter(SimpleCursorTreeAdapter adp)
	{
		View v = this.findViewById(R.id.log_list);
		if(v == null) return;
		ExpandableListView list = (ExpandableListView)v;
		list.setAdapter(adp);
	}
	
	protected  ExpandableListAdapter getExpandableListAdapter()
	{
		View v = this.findViewById(R.id.log_list);
		if(v == null) return null;
		ExpandableListView list;
		try
		{
			list = (ExpandableListView)v;
		}
		catch(Exception ex)
		{
			list = null;
		}
		
		if(list != null) return list.getExpandableListAdapter();
		else return null;
	}
	

}
