package com.olmatech.fitness.interfaces;

import android.os.Handler;

public interface IHaveTimer {
	
	public void startTimer();
	public void stopTimer();
	public void setHandler(Handler hnd);
	public void deleteHandler();
	public void removeHandler(Handler hnd);
	public boolean isTimerOn();
	 

}
