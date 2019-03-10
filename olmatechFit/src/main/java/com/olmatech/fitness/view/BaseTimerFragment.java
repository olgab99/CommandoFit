package com.olmatech.fitness.view;

import java.lang.ref.WeakReference;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.interfaces.IHaveTimer;
import com.olmatech.fitness.main.Common;


public abstract class BaseTimerFragment extends BasePlaySoundFragment{
	protected final static String TAG = "BaseTimerFragment";
	//Timer
//	protected Timer timer;
//	private final static long TIMER_INTERVAL = 1000; //ms
	
	protected boolean isTimerOn = false;
	private TimeHandler timeHandler= new TimeHandler(this);
	
	protected long time=0;  //secs
	protected long totalTime=0;
	protected ProgressBar progrBar;
	protected int progressValue=0;
	
	protected Button butStart;
	protected Button butStop;  // can be null
	
	protected TextView txtTime;
	
	protected boolean showHours = false; //true to show 00:00:00, false 00:00	
	
	protected IHaveTimer timerListener=null;
	
	protected boolean toggleStopButton = true;	


	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(butStart != null)
		{
			outState.putBoolean("start_visible", (butStart.getVisibility() == View.VISIBLE)? true : false);
		}
		if(butStop != null)
		{
			outState.putBoolean("stop_visible", (butStop.getVisibility() == View.VISIBLE)? true : false);
		}
		
		outState.putLong("total_time", totalTime);   
		outState.putLong("time", time);   
		outState.putInt("progress", progressValue);				
		outState.putBoolean("timer_on", isTimerOn); 
		
		if(txtTime != null)
		{
			outState.putString("txt_time", (String) txtTime.getText());
		}
		
		super.onSaveInstanceState(outState);
	}

	//set time in secs
	public void setTime(final long tm, final boolean resetTotal, final boolean resetUI)
	{
		time = tm;
		if(resetTotal) totalTime = tm;
		if(resetUI) resetView();
	}
	
	public void setProgressVal(final int val)
	{
		progressValue = val;
	}
	
	public void calcProgress()
	{
		if(time == 0)
		{
			setProgress(100);
		}
		else
		{
			//%
			if(totalTime >0)
			{				
				setProgress((int) (((double)(totalTime-time) / (double)totalTime)*100.0));						
			}					
		}
	}
	
	public void setTotalTime(final long val)
	{
		totalTime = val;
	}
	
	protected  void resetView(){}
	
	public void displayTimeOnUI(final boolean updateProgress)
	{
		displayTime(updateProgress);
	}
	
	
	public void setTimeToTotal(final boolean updateProgress)
	{
		time = totalTime;
		displayTime(updateProgress);
	}
	
	public void setShowHours(final boolean val){ showHours=val; }
	public boolean getShowHours(){ return showHours; }
		
	protected void setStartButTitle(final int resId, final boolean showStart)
	{
		Activity act = this.getActivity();
		if(act != null){
			act.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					if(butStart != null)
					{
						butStart.setText(resId);
						if(showStart) butStart.setVisibility(View.VISIBLE);
						else butStart.setVisibility(View.GONE);
					}					
				}
				
			});
		}
	}
	
		
	// START button clicked - start timer or paused
	protected void butTimerStartClick()
	{
		if(isTimerOn)
		{
			detachTimer();			
			setStartButTitle(R.string.start, true);	
			if(toggleStopButton) setStopButVisible(true); /////////////////////////////////////////////////
		}
		else
		{
			attachTimer();			
			setStartButTitle(R.string.pause, true);			
			if(toggleStopButton) setStopButVisible(false);
		}
	}
	
//	public void setPaused()
//	{
//		stopTimer();
//		setStartButTitle(R.string.start, true,false);
//	}
	
	protected void butTimerStopClick()
	{
		detachTimer();
		setStopButVisible(false);
	}
	
	public void setStopButVisible(final boolean show)
	{
		if(butStop == null ) return;
		Activity act = this.getActivity();
		if(act != null){
			act.runOnUiThread(new Runnable(){

				@Override
				public void run() {					
					if(show) butStop.setVisibility(View.VISIBLE);
					else butStop.setVisibility(View.GONE);
				}
				
			});
		}
	}
	
	
	protected void setStartButVisible(final boolean show)
	{
		if(butStart == null) return;
		Activity act = this.getActivity();
		if(act != null){
			act.runOnUiThread(new Runnable(){

				@Override
				public void run() {					
					if(show) butStart.setVisibility(View.VISIBLE);
					else butStart.setVisibility(View.GONE);
				}
				
			});
		}
	}
	
	protected void attachTimer()
	{
		if(Common.DEBUG) Log.d(TAG, "attachTimer isTimerOn = true" );
		if(timerListener != null){
			timerListener.setHandler(timeHandler);			
		}
		isTimerOn = true;
		startTimerIfNotOn();
	}
	
	protected void detachTimer()
	{
		if(Common.DEBUG) Log.d(TAG, "detachTimer isTimerOn = false" );
		if(timerListener != null) timerListener.removeHandler(timeHandler);
		isTimerOn = false;
		
	}
	
	
	
	private void doOnTimer()
	{
		if(time > 0)
		{
			time--;
			showTime();
		}
		else
		{
			time =0;
			showTime();
			onTimeComplete();
			if(this.playSoundOnTimeUp) this.playSound();
		}
	}
	
	//done, time = 0
	protected void onTimeComplete()
	{
		detachTimer();
		
	}
	
	protected void showTime()  ////// HERE  ///////////////////
	{
		Activity act = getActivity();
		if(act == null || !this.isVisible()) return;
		act.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				displayTime(true);				
			}
			
		});
	}
	
	protected String getTimeStr()
	{
		if(time >=3600 || showHours)
		{
			return (time >0)? String.format(Locale.US, "%d:%02d:%02d", time/3600, (time%3600)/60, (time%60)) : "00:00";
		}
		else
		{
			return (time >0)? String.format(Locale.US, "%02d:%02d", (time%3600)/60, (time%60)) : "00:00";
		}
		
	}
	
//	protected void setProgress()
//	{
//		setProgress(progressValue);
//	}
	protected void setProgress(final int val)
	{
		progressValue = val;
		if(progrBar != null)
		{
			progrBar.setProgress(progressValue);
		}
	}
	
	//time and progress
		protected void displayTime(final boolean updateProgress)
		{
			//mm:ss
			String str =getTimeStr();
			
			if(txtTime != null) txtTime.setText(str);
			
			if(updateProgress && progrBar != null)
			{
				if(time == 0)
				{
					setProgress(100);
				}
				else
				{
					//%
					if(totalTime >0)
					{				
						setProgress((int) (((double)(totalTime-time) / (double)totalTime)*100.0));						
					}					
				}
			}
			
		}
		
		/**
		 * Get current time settings
		 * @return
		 */
		public long getTime()
		{
			return time;
		}
		
		public long getTotalTime()
		{
			return totalTime;
		}
		
		public boolean isTimerOn()
		{
			return isTimerOn;
		}
		
		public void setIsTimerOn(final boolean val)
		{
			isTimerOn = val;
		}
		
		public int getProgressVal()
		{
			return progressValue;
		}
		
		public String getStartButTitle()
		{
			return  (butStart != null)? (String)butStart.getText() : null;
		}
		
		public boolean isStartButVisible()
		{
			if(butStart != null)
			{
				return (butStart.getVisibility() == View.VISIBLE)? true : false;
			}
			return false;
		}
		
		public boolean isStopButVisible()
		{
			if(butStop != null)
			{
				return (butStop.getVisibility() == View.VISIBLE)? true : false;
			}
			return false;
		}
		
		//Time handler
		static class TimeHandler extends Handler{
			WeakReference<BaseTimerFragment> mFrag;
			
			TimeHandler(BaseTimerFragment fr)
			{
				mFrag = new WeakReference<BaseTimerFragment>(fr);
			}

			@Override
			public void handleMessage(Message msg) {
				BaseTimerFragment theFrag = mFrag.get();
				theFrag.doOnTimer();				
			}			
		}
		
		public void setPaused()
		{
			if(Common.DEBUG) Log.d(TAG, "setPaused detachTimer");
			this.detachTimer();
			if(butStart != null) butStart.setText(R.string.start);
		}
		
		public void setStarted()
		{
			if(Common.DEBUG) Log.d(TAG, "setStarted attachTimer");
			this.attachTimer();
			if(butStart != null) butStart.setText(R.string.pause);
			
		}

		
		@Override
		public void onDestroy() {
			removeTimeHandler();
			timeHandler = null;
			super.onDestroy();
		}
		
		
		
		@Override
		public void onAttach(Activity activity) {			
			super.onAttach(activity);
			try {
				timerListener = (IHaveTimer) activity;				
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement IHaveTimer");
	        }
		}

		private void removeTimeHandler()
		{
			if(timerListener != null) timerListener.removeHandler(timeHandler);			
		}
		
		protected void startTimerIfNotOn()
		{
			if(this.timerListener != null)
			{
				if(!timerListener.isTimerOn()) timerListener.startTimer();
			}
		}	
		
		public void setToggleStopButton(final boolean val)
		{
			toggleStopButton= val;
		}

		
//		@Override
//		public void onSaveInstanceState(Bundle outState) {
//			
//			outState.putLong("total_time", totalTime);
//			outState.putLong("time", time);
//			outState.putInt("progress", progressValue);
//			outState.putBoolean("show_hours", showHours);
//			if(butStart != null) {
//				outState.putString("but_start", (String) butStart.getText());
//				outState.putInt("but_start_visible", butStart.getVisibility());
//			}
//			
//			if(butStop != null) {
//				outState.putString("but_stop", (String) butStop.getText());
//				outState.putInt("but_stop_visible", butStop.getVisibility());
//			}
//			
//			outState.putBoolean("timer", (timer != null)? true : false);
//			
//			super.onSaveInstanceState(outState);
//		}

		

}
