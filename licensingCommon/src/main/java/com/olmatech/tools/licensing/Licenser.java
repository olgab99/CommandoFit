package com.olmatech.tools.licensing;

import android.os.Handler;

public abstract class Licenser {
	
	public final static int DIALOG_RETRY=1;
	public final static int DIALOG_GOTOMARKET=2;
	public final static int DIALOG_DEV_ERR=5;
	public final static int DIALOG_SAMSUNG_NOT_LICENSED=6;
	
	//common
	 protected boolean firstTime= true;
	 public final static int MAX_NOTLICENSED = 5;
	 protected int errIndex = -1;
	 protected ILicensingParent parent;
	 
	   // A handler on the UI thread.
		private Handler mCheckHandler;		
	 
	 public Licenser(final ILicensingParent p)
	 {
		 parent = p;
		 mCheckHandler = new Handler();
	 }
	 
	 abstract public void doDestroy();
	 abstract public boolean checkAppLicense();
	 abstract public String getRetryDlgMsg(final int err);
	 
	 public void processLicensed(boolean showPage) {
		 mCheckHandler.post(new Runnable() {
             public void run() {      
            	 parent.setLicensed();  //set only here
            	 parent.StartApp();
             }
         });
		
	}
	 
	 public int getErrIndex()
	 {
		 return errIndex;
	 }

}

