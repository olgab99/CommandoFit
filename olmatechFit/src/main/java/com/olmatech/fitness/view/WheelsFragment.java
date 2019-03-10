package com.olmatech.fitness.view;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.adapters.ValueNumericAdapter;
import com.olmatech.fitness.adapters.WeightLbAdapter;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.Exercise;

public class WheelsFragment extends BaseFragment{
	
	//private final static String TAG = "WheelsFragment";	
	private IWheelsListener mListener;
	
	//private WheelView wheelSets;
	private WheelView wheelWeight;
	private WheelView wheelReps;
	private TextView txtSetsTitle;	
	
	private View wheelWeightTitle;
	private View circleMark;
	
//	private final static String HTML_HEAD = "<!DOCTYPE html><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body>";
//	private final static String HTML_END = "</body></html>";
//	private final static String encoding = "UTF-8";
//	private final static String mimeType = "text/html";
	
	private Activity parentActivity;
	private View  butLogEdit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View view = getLayoutView(inflater, container,R.layout.fragment_wheels, R.layout.fragment_wheels_tab);
					
		//wheelSets = (WheelView) view.findViewById(R.id.wheel_sets);
		wheelWeight = (WheelView) view.findViewById(R.id.wheel_weight);
		wheelReps = (WheelView) view.findViewById(R.id.wheel_reps);	
		txtSetsTitle = (TextView)view.findViewById(R.id.setsTitle);
		wheelWeightTitle = view.findViewById(R.id.wheel_wt_title);
		circleMark =  view.findViewById(R.id.circle_mark);
		//set save event
		View butSave = view.findViewById(R.id.butSave);
		if(butSave != null)
		{
			butSave.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					if(ev.getAction() == MotionEvent.ACTION_DOWN)
					{
						saveLog();
						return true;
					}
					return true;
				}				
			});
		}
		
		butLogEdit = view.findViewById(R.id.but_log_edit);
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		parentActivity = activity;
		try {
			mListener = (IWheelsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IWheelsListener");           
        }  
		
	}
	
	
	//we have to separate initing wheels and setting adapters (data)
	
	public void fillData()
	{
		if(parentActivity == null || wheelReps == null || wheelWeight == null)
		{
			return; // TODO - handle
		}			
		
               
	}
	
	///////////// LOG /////////////////
	
	private void saveLog()
	{
		//save to DB and display in webview
		//save weight, reps
		int index;//index of cur item
		int weight =0;
	
		if(wheelWeight != null && wheelWeight.getVisibility() == View.VISIBLE)
		{
			index = wheelWeight.getCurrentItem();//index of cur item
			weight = WeightLbAdapter.getItemValueFromIndex(index);
		}		
		
		index = wheelReps.getCurrentItem(); //index
		int reps = index+1;
		if(mListener != null)
		{
			mListener.onSaveLogClick(weight, reps);
		}	
		
	}
	
	//// Display saved log in list
	public void displaySavedLog(final int curSet)
	{
		//for now - display i list
		if(parentActivity == null) return;
		ListView lvLog = (ListView)parentActivity.findViewById(R.id.listLog);
		ArrayList<String> arrLog = new ArrayList<String>();
		for(int i=1; i <= curSet; i++)
		{
			arrLog.add(Integer.toString(i));
		}		
		lvLog.setAdapter(new LogAdapter(parentActivity,
				((Common.isTabletDevice(parentActivity))? R.layout.list_item_shortlog_tab : R.layout.list_item_shortlog), 
						R.id.txtSet, arrLog ));
		
		if(butLogEdit != null) butLogEdit.setVisibility(View.VISIBLE);
		
	}
	
	public void clearLogDisplay()
	{
		ListView lvLog = (ListView)parentActivity.findViewById(R.id.listLog);
		lvLog.setAdapter(null);
		if(butLogEdit != null) butLogEdit.setVisibility(View.GONE);
		
	}
	
	private class LogAdapter extends ArrayAdapter<String>
	{

		public LogAdapter(Context context, int resource,int textViewResourceId, List<String> objects) {
			super(context, resource, textViewResourceId, objects);
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view =super.getView(position, convertView, parent);
			TextView tv = (TextView)view.findViewById( R.id.txtSet);
			tv.setTypeface(Typeface.MONOSPACE);
			return view;
		}
		
	}
	
	
	/////////////////// Set wheels data for a given exercise///////////////////////////
	
	public void setSetsTitle(final int maxVal, final int curVal)
	{
		if(txtSetsTitle != null)
		{
			txtSetsTitle.setText("SET " + curVal + " OF " + maxVal);
		}
	}	
	
	public void setSetsTitle(final String str)
	{
		if(txtSetsTitle != null)
		{
			txtSetsTitle.setText(str);
		}
	}
	
	
	public void setReps(final int minVal, final int maxVal, final int curVal)
	{
		 wheelReps.setViewAdapter(new ValueNumericAdapter(parentActivity, minVal, maxVal, curVal));
		 setCurReps(curVal);		 
	}
	
	public void setCurReps(final int curVal)
	{
		wheelReps.setCurrentItem(curVal);		
		//wheelReps.scroll(curVal-1, 50);
	}
	//set highlits
	public void clearRepsHighligts()
	{
		WheelViewAdapter adap = wheelReps.getViewAdapter();
		if(adap == null) return;
		adap.clearHighlightIndexes();
	}
	
	public void setRepsHighlight(final int start, final int end)
	{
		//Log.d(TAG, "setRepsHighlight start=" + start + "  end=" + end);
		WheelViewAdapter adap = wheelReps.getViewAdapter();
		if(adap == null) return;
		adap.setIndexesToHighlight(start, end);
	}
	
	public void clearWeigtHighlights()
	{
		WheelViewAdapter adap = wheelWeight.getViewAdapter();
		if(adap == null) return;
		adap.clearHighlightIndexes();
	}
	
	//we will set indexes based on weight values 
	//will need to adjust
	public void setWeigtHighlights(final int[] ranges)
	{
		if(ranges == null || ranges.length<3) return;
		WheelViewAdapter adap = wheelWeight.getViewAdapter();
		if(adap == null) return;
		adap.setIndexesToHighlight(ranges[0], ranges[2]);////////////// TODO
		wheelWeight.invalidateWheel(true);
	}
	
	public void setWeight( final int curVal) //final int minVal, final int maxVal,
	{
		if(wheelWeight == null || parentActivity == null) return;
		if(wheelWeight.getViewAdapter() == null)
		{			
			resetWeightAdapter();
		}	
		//wheelWeight.setViewAdapter(new WeightLbAdapter(parentActivity, minVal, maxVal, curVal));
		setCurWeight(curVal);
	}
	
	private void setCurWeight(final int curVal)
	{
		final int ind = WeightLbAdapter.getItemIndexFromValue(curVal);		
		WheelViewAdapter adap = wheelWeight.getViewAdapter();
		if(adap != null)
		{
			try
			{
				WeightLbAdapter wtAdap = (WeightLbAdapter)adap;
				wtAdap.setCurValue(curVal);
				wtAdap.setDoHighlight((curVal>0)? true : false);
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return;
			}
			wheelWeight.setCurrentItem(ind);
		}
		
		
		//wheelWeight.scroll(ind,50);
	}
	
//	public void setHLightWeightValue(final boolean val)
//	{
//		WheelViewAdapter adap = wheelWeight.getViewAdapter();
//		if(adap != null)
//		{
//			try
//			{
//				WeightLbAdapter wtAdap = (WeightLbAdapter)adap;
//				wtAdap.setDoHighlight(val);
//				
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//				return;
//			}
//		}
//	}
	
	//reset weigh adapter
	public void resetWeightAdapter()
	{
		if(wheelWeight == null || parentActivity == null) return;		
		wheelWeight.setViewAdapter(new WeightLbAdapter(parentActivity, 0, Exercise.MAX_WEIGHT, 0));		
	}
	
	public void showWeightWheel(final boolean show)
	{
		if(show)
		{
			if(wheelWeight != null && wheelWeight.getVisibility() != View.VISIBLE)
			{
				wheelWeight.setVisibility(View.VISIBLE);
			}
			if(wheelWeightTitle != null && wheelWeightTitle.getVisibility() != View.VISIBLE)
			{
				wheelWeightTitle.setVisibility(View.VISIBLE);
			}
			if(circleMark != null && circleMark.getVisibility() != View.VISIBLE)
			{
				circleMark.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			if(wheelWeight != null && wheelWeight.getVisibility() == View.VISIBLE)
			{
				wheelWeight.setVisibility(View.INVISIBLE);
			}
			if(wheelWeightTitle != null && wheelWeightTitle.getVisibility() == View.VISIBLE)
			{
				wheelWeightTitle.setVisibility(View.INVISIBLE);
			}
			if(circleMark != null && circleMark.getVisibility() == View.VISIBLE)
			{
				circleMark.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	////////////// wheels //////////////////////////
	
		
//	private void updateLogSets(final WheelView wheelSets)
//	{
//		int ind = wheelSets.getCurrentItem();		
//	}
	
//	private void updateLogReps(final WheelView wheelReps)
//	{
//		int ind = wheelReps.getCurrentItem();
//		Log.d(TAG, "wheelReps val=" + ind);
//	}
//	
//	private void updateLogWeight(final WheelView wheelWeight)
//	{
//		int ind = wheelWeight.getCurrentItem();
//		Log.d(TAG, "wheelSets val=" + ind);
//	}
	
//	private void setLogWeight(final WheelView wheelWeight)
//	{
//		int curSet = wheelWeight.getCurrentItem() + 1;
//		wheelWeight.setCurrentItem(curSet - 1, true);
//	}
	
////////////////////ADAPTERS /////////////////////////////
	


	

	// /////////// INTERFACE ///////////////////////

	public interface IWheelsListener {
		public void onSaveLogClick(final int weight, final int reps);
		
	}
	
	

}
