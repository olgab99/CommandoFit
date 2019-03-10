package com.olmatech.fitness.view;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

public class ExImgFragment extends BasePlaySoundFragment{
	//private final static String TAG = "ExImgFragment";
	private IExImgFragmentListener mListener;
	private View arr_left;
	private View arr_up;
	private View arr_right;
	private View arr_down;
	
	//Timer
	private Timer timer;
	private TextView txtTimer;
	private View viewTimer;
	private final static long TIMER_INTERVAL = 1000; //ms
	//private final static long START_TIME = Common.DEF_REST_TIME; //sec
	private long time=0;
	private int time_interval = Common.DEF_REST_TIME;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ex_img, container, false);
		
		View viewDescription = view.findViewById(R.id.but_descript);
		if(viewDescription != null)
		{
			viewDescription.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					if(ev.getAction() != MotionEvent.ACTION_UP) return true;						
					mListener.onDescriptionButClick();
					return true;					
				}
				
			});
		}
		
		//init buttons
		viewTimer = view.findViewById(R.id.butTimer);
		viewTimer.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_UP)
				{					
					if(mListener != null)
					{
						mListener.onClockClick();
					}
				}
				return true;
			}
			
		});
		
		arr_left = view.findViewById(R.id.img_arr_left);
		arr_up = view.findViewById(R.id.img_arr_up);
		arr_right = view.findViewById(R.id.img_arr_right);
		arr_down= view.findViewById(R.id.img_arr_down);
		
		if(arr_left != null)
		{
			arr_left.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					if(e.getAction() != MotionEvent.ACTION_UP) return true;	
					if(mListener != null) mListener.showPrev();
					return true;						
				}				
			});
		}
		if(arr_up != null)
		{
			arr_up.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					if(e.getAction() != MotionEvent.ACTION_UP) return true;	
					if(mListener != null) mListener.showUp();
					return true;
					
				}				
			});
		}
		
		if(arr_right != null)
		{
			arr_right.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					if(e.getAction() != MotionEvent.ACTION_UP) return true;						
					if(mListener != null) mListener.showNext(false);
					return true;
					
				}				
			});
		}
		
		if(arr_down != null)
		{
			arr_down.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					if(e.getAction() != MotionEvent.ACTION_UP) return true;	
					if(mListener != null) mListener.showDown();
					return true;						
				}				
			});
		}
			
		txtTimer = (TextView)view.findViewById(R.id.txt_timer);
		if(txtTimer !=null)
		{
			txtTimer.setVisibility(View.INVISIBLE);
		}
	
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);		
		try {
			mListener = (IExImgFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IExImgFragmentListener");
        }
//		if(mListener != null)
//		{
//			mListener.onActivityAttached();
//		}
	}
	
	//toggle description close button
	public void toggleDescriptionButton(final boolean showOpenButton)
	{
		Activity act = this.getActivity();
		if(act == null) return;
		ImageView viewClose = (ImageView)act.findViewById(R.id.but_descript);
		if(viewClose !=null)
		{
			if(showOpenButton)
			{
				viewClose.setImageResource(R.drawable.corner_open);
			}
			else
			{
				viewClose.setImageResource(R.drawable.corner_close);
			}
		}
	}
	
	/////////////////////// ARROWS ////////////////////
	
	public void showArrows(final boolean left,final boolean top, final boolean right, final boolean bottom)
	{
		if(arr_left != null)
		{
			if(!left)
			{
				//first
				arr_left.setVisibility(View.GONE);
			}
			else arr_left.setVisibility(View.VISIBLE);
		}
		
		
		if(arr_up != null)
		{
			if(top) arr_up.setVisibility(View.VISIBLE);
			else arr_up.setVisibility(View.GONE);			
		}
		if(arr_right != null)
		{
			if(!right) arr_right.setVisibility(View.GONE);	
			else arr_right.setVisibility(View.VISIBLE);	
		}
		if(arr_down != null)
		{
			if(bottom) arr_down.setVisibility(View.VISIBLE);
			else arr_down.setVisibility(View.GONE);
		}	
	}
	
	/////////////////// TIMER ////////////////////////
	
	public void setTimeInterval(final int val)
	{
		time_interval = val;
	}
	
	public void startTimer()
	{
		if(timer != null)
		{
			timer.cancel();			
		}
		timer = new Timer();
		time =time_interval; //
		//display string
		
		String val = getTimeString(time_interval);	
		showTime(val); ///////////////////////////////////////////////////////
		showTimerCtrl(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				showTime();
			}

		}, TIMER_INTERVAL, TIMER_INTERVAL);   	
	}
	
	//show time in seconds
	private String getTimeString(final long t)
	{
//		int min = (int) (t / 60);
//		int secs = (int) (t - min*60);		
//		if(min >0) return String.format(Locale.US, "%d:%02d", min, secs);		
//		else return String.format(Locale.US, "%02d",secs);	
		
		return String.format(Locale.US, "%02d",t);	
	}
	
	public void stopTimer()
	{
		if(timer != null)
		{
			timer.cancel();	
			timer=null;
		}
		time =-1;
		showTimerCtrl(false);
	}
	
	private void showTimerCtrl(final boolean show)
	{
		Activity parent = this.getActivity();
		if(parent == null) return;
		parent.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showTimer(show);			
			}
			
		});
	}
	
	private void showTime()
	{
		Activity parent = this.getActivity();
		if(parent == null) return;
		time--;
		parent.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				if(time >=0)
				{
					String val = getTimeString(time);	
					showTime(val);
				}
				else
				{
					stopTimer();
					if(playSoundOnTimeUp) playSound();
				}				
			}
			
		});
		
	}
	
	public void showTimer(final boolean show)
	{		
		if(txtTimer == null || viewTimer == null) return;
		if(show){
			txtTimer.setVisibility(View.VISIBLE);
			viewTimer.setVisibility(View.GONE);		
		}
		else {
			txtTimer.setVisibility(View.INVISIBLE);	
			viewTimer.setVisibility(View.VISIBLE);
			viewTimer.bringToFront();
		}
	}
	
	public void showTime(final String tm)
	{
		if(txtTimer == null) return;
		txtTimer.setText(tm);
	}
	
	public boolean isTimerOn()
	{
		if(txtTimer == null) return false;
		return (time >= 0 && (txtTimer.getVisibility() == View.VISIBLE))? true : false;
	}
	
	///////////// INTERFACE ///////////////////////
	
	public interface IExImgFragmentListener{
		public void onDescriptionButClick();  //show / hide description
		public void onActivityAttached();
		public boolean showNext(final boolean skipCompleted);
		public void showUp();
		public boolean showPrev();
		public void showDown();
		public void onClockClick();
		
	}	
	
	

}
