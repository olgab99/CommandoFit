package com.olmatech.fitness.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.olmatech.fitness.interfaces.IHaveTimer;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.view.BaseTimerFragment;

public abstract class BaseTimeFragmentActivity extends BaseFragmentActivity implements IHaveTimer{
	
	private final static String TAG = "BaseTimeFragmentActivity";
	
	//Timer
    private Timer timer;
    private final static long TIMER_INTERVAL = 1000; //ms
    private Handler timerHandler;
    protected boolean isTimerWasOn = false;
    protected long time_saved = 0;    

	@Override
	protected void onCreate(Bundle b) {		
		super.onCreate(b);
		
		if(b != null)
		{
			if(b.containsKey("timer_was_on")) isTimerWasOn = b.getBoolean("timer_was_on");
		}
	}

	@Override
	public void startTimer() {
		if(timer != null) return;    		
			
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				doOnTimer();
			}

		}, TIMER_INTERVAL, TIMER_INTERVAL); 		
		
	}

	@Override
	public void stopTimer() {
		if(timer != null)
		{
			timer.cancel();	
			timer=null;
		}
	}

	@Override
	public void setHandler(Handler hnd) {
		timerHandler = hnd;   	
		if(Common.DEBUG) Log.d(TAG, "setHandler");
	}

	@Override
	public void deleteHandler() {
		timerHandler = null;
		if(Common.DEBUG) Log.d(TAG, "deleteHandler");
	}

	@Override
	public void removeHandler(Handler hnd) {
		if(hnd != null && hnd == timerHandler) {
			timerHandler = null;
			if(Common.DEBUG) Log.d(TAG, "removeHandler");
		}
		
		
	}

	@Override
	public boolean isTimerOn() {
		return (timer != null)? true : false;
	}
	
	private void doOnTimer()
	{
		if(timerHandler != null)
		{
			timerHandler.sendEmptyMessage(0);
		}
		
		
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void onDestroy() {
		stopTimer();
		this.deleteHandler();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if(isTimerOn())
		{
			stopTimer();
			isTimerWasOn = true;
			Date d = new Date();
			time_saved = d.getTime();
			
		}
		else isTimerWasOn = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(isTimerWasOn)
		{
			if(Common.DEBUG) Log.d(TAG, "onResume  - startTimer");
			startTimer();
		}
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("timer_was_on", isTimerOn());
		if(Common.DEBUG) Log.d(TAG, "onSaveInstanceState outState=" + outState);
		super.onSaveInstanceState(outState);
	}
	
	protected long getTimeDiff(final long time)
	{
		Date d = new Date();
		long diff = (d.getTime() - time_saved)/1000;  //secs
		return (time - diff);
	}

	protected void setCardioOnResume(BaseTimerFragment frCardio)
	{
		if(frCardio.isTimerOn())
		{
			long time = frCardio.getTime();							
				long t = getTimeDiff(time);
				if(t >0)
				{
					frCardio.setTime(t, false, false);	 						
				}
				else
				{
					//we are done - set time to 1 sec
					frCardio.setTime(1, false, false);	 							
				}
				frCardio.calcProgress();
				if(this.isTimerWasOn)
				{
					frCardio.setStarted();
				}
				else
				{
					frCardio.setPaused();
				}
		}
		else
		{
			frCardio.setPaused();
		}
	}

}
