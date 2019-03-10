package com.olmatech.fitness.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.StrictMode;

public class FitApplication extends Application{

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		if (Common.DEBUG) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedSqlLiteObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());
	     }
		super.onCreate();
	}
	
	

}
