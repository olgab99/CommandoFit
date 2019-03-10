package com.olmatech.fitness.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.interfaces.IHaveId;
import com.olmatech.fitness.main.Common;

public class BottomFragment extends BaseFragment{

	//private final static String TAG = "BottomFragment";		
	private View viewBack;
	private ImageView butBack;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = getLayoutView(inflater, container,R.layout.fragment_bottom,R.layout.fragment_bottom_tab );		
			
		viewBack = view.findViewById(R.id.bgMenuBack);
		butBack = (ImageView)view.findViewById(R.id.butBack);
		viewBack.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_UP)
				{
					if(parentActivity != null)
					{
						parentActivity.onBackPressed();
					}
				}
				
				return true;
			}
			
		});
		
		View viewCalc = view.findViewById(R.id.bgMenuCalc);
		viewCalc.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_UP)
				{
					Common.showCalculatorMenu(parentActivity);					
				}
				
				return true;
			}
			
		});
		
		View viewHelp = view.findViewById(R.id.bgMenuHelp);
		viewHelp.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_UP)
				{					
					final int parentId = getParentId();
					if(parentId< 0)
					{
						Common.showHelp(parentActivity, 0);
					}
					else Common.showHelp(parentActivity, parentId);
								
				}
				return true;
			}
			
		});
		
//		View viewTimer = view.findViewById(R.id.bgMenuTimer);
//		viewTimer.setOnTouchListener(new OnTouchListener(){
//
//			@Override
//			public boolean onTouch(View v, MotionEvent e) {
//				if(e.getAction() == MotionEvent.ACTION_UP)
//				{
//					Log.d(TAG, "viewTimer");
//					if(mListener != null)
//					{
//						mListener.onClockClick();
//					}
//				}
//				return true;
//			}
//			
//		});
		
		View viewOpt = view.findViewById(R.id.bgMenuOpt);
		viewOpt.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_UP)
				{					
					Common.showMenuActivity(parentActivity, getParentId());
				}
				return true;
			}
			
		});
		
		return view;		
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parentActivity = activity;
	}
	
////HELPERS
	/**
	 * 
	 * @return parent id or -1
	 */
	private int getParentId()
	{
		if(parentActivity != null && parentActivity instanceof IHaveId )
		{
			IHaveId idAct=null;
			try
			{
				idAct = (IHaveId)parentActivity;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				idAct = null;
			}
			if(idAct != null)
			{
				return idAct.getId();
			}			
		}
		return -1;	
	}
	
	public void setBackButtonEnabled(final boolean enabled)
	{
		if(enabled) {			
			butBack.setImageResource(R.drawable.back);
		}
		else{
			butBack.setImageResource(R.drawable.back_dis);	
		}
		viewBack.setEnabled(enabled);
	}
	
	

}
