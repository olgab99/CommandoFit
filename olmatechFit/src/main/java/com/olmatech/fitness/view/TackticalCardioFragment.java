package com.olmatech.fitness.view;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.interfaces.ITackticalListener;

public class TackticalCardioFragment extends BaseTimerFragment{
	
	private ITackticalListener mListener;
	private TextView txtMessage;
	private ImageView imgTick;
	
	private View containerView;
	
	private String message;
	private int index =-1;
	private boolean showTick = false;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
		
		View view = getLayoutView(inflater, container,R.layout.fragment_cardio_item, R.layout.fragment_cardio_item_tab);				
				
		txtTime= (TextView)view.findViewById(R.id.txt_time);
		if(this.totalTime > 0) txtTime.setText(getTimeStr());
	
		butStart = (Button)view.findViewById(R.id.but_start);
		butStop = null;
		progrBar= (ProgressBar)view.findViewById(R.id.progrBarTacktical);
		setProgress(0);
		
		containerView = view.findViewById(R.id.ex_view_container);
		
		txtMessage = (TextView)view.findViewById(R.id.title_speed);
		if(message != null ) txtMessage.setText(message);
		imgTick = (ImageView)view.findViewById(R.id.imgArrow);
		imgTick.setBackgroundResource(R.drawable.arrow_right_anim);
		showActive(showTick);
		
		butStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				butTimerStartClick();		
			}			
		});			
		
//		if(savedInstanceState != null)
//		{
//			if(savedInstanceState.containsKey("but_start"))
//			{
//				butStart.setText(savedInstanceState.getString("but_start"));				
//			}
//			if(savedInstanceState.containsKey("but_stop_visible"))
//			{
//				butStart.setVisibility(savedInstanceState.getInt("but_start_visible"));
//			}
//			
//			if(savedInstanceState.containsKey("but_stop"))
//			{
//				butStop.setText(savedInstanceState.getString("but_stop"));				
//			}
//			if(savedInstanceState.containsKey("but_stop_visible"))
//			{
//				butStop.setVisibility(savedInstanceState.getInt("but_stop_visible"));
//			}
//			
//			
//		}
		return view;
	}
	
//	public void setMessage(final String str)
//	{
//		message = str;
//		if(txtMessage != null) txtMessage.setText(message);
//	}
	
	
	public void setIndex(final int val)
	{
		index=val;
	}
	
	public int getIndex(){ return index; }
	
		
	public void showActive(final boolean show)
	{
		showTick = show;
		if(imgTick ==null) return;		
		AnimationDrawable tickAnimation = (AnimationDrawable) imgTick.getBackground();
		if(show)
		{
			//background
			containerView.setBackgroundResource(R.color.ex_active);
			//Start button
			this.butStart.setText(R.string.start);
			if(butStart.getVisibility() != View.VISIBLE) butStart.setVisibility(View.VISIBLE);
			
			tickAnimation.setOneShot(false);
			tickAnimation.start();
			imgTick.setVisibility(View.VISIBLE);
		}
		else{
			containerView.setBackgroundResource(R.color.transparent);
			tickAnimation.stop();
			imgTick.setVisibility(View.INVISIBLE);
		}		
	}
	
	public void showStartButton(final boolean show)
	{
		if(show)
		{
			if(butStart.getVisibility() != View.VISIBLE) butStart.setVisibility(View.VISIBLE);
		}
		else
		{
			if(butStart.getVisibility() == View.VISIBLE) butStart.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setComplete()
	{
		Activity act = getActivity();
		if(act == null)
		{
			return;
		}
		act.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showActive(false);
				if(butStart != null) butStart.setVisibility(View.INVISIBLE);		
				setProgress(100);			
			}
			
		});		
	}	
	
	
	public void setStart()
	{
		Activity act = getActivity();
		if(act == null)
		{
			//we are not in activity yet
			showActive(true);
			setProgress(0);
			setTimeToTotal(false);	
		}
		else
		{
			act.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					showActive(true);
					setProgress(0);
					setTimeToTotal(false);	
				}
				
			});		
		}		
	}
	
	@Override
	protected void onTimeComplete() {		
		super.onTimeComplete();
		setComplete();
		if(mListener != null) mListener.onTimeComplete(index);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (ITackticalListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ITackticalListener");
        }
	}
	
	public void stopCardio()
	{
		detachTimer();
	}
	
	public void startCardioTimer()
	{
		attachTimer();		
	}


}
