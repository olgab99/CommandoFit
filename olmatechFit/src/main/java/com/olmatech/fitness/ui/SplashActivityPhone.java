package com.olmatech.fitness.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.User.FitnessLevel;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.tools.licensing.ILicensingParent;
import com.olmatech.tools.licensing.Licenser;


public class SplashActivityPhone extends BaseFragmentActivity implements ILicensingParent{
	
	/**
	 * MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0C8fDCdx29ekB122Z43BYJysUdBByCNH7ICrqvWNG1+hnrl2uC4tPV1J0l1q9oWDQn3EGwxZYaIm7UWkYtA/Wxb5/ZKj/gJMj+1vIwWOSOgtEBKbWzhUI7SHZJIvnnXEGQN0UNr2vW80apQ+Av1Amyy1zHn1eoMKkxHEA9rnxeO3IMhIFe/uQnsj1TjiYTgPTWFI48evGUle1QLtMI19auyMhwk/Z5k6E8gwPL/aZCItZhxBaDEKhcj+Gmh6ftv+BoGc5iyoD8x6/EdqS4mSZtJH3Nwjyz+jLxBDCJ8xqVK7hQgIVwwtFGtksAvfYaMFJp2X7D+1ast+L+7cTDGg1wIDAQAB
      The thread to process splash screen events
     */
	private Thread mSplashThread;
	private boolean initDone = false;
	private static final int DLG_INSTALL_ERROR = 10;
    private int dlg_install_err_msg = R.string.installerror;
    private static final String BASE64_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0C8fDCdx29ekB122Z43BYJysUdBByCNH7ICrqvWNG1+hnrl2uC4tPV1J0l1q9oWDQn3EGwxZYaIm7UWkYtA/Wxb5/ZKj/gJMj+1vIwWOSOgtEBKbWzhUI7SHZJIvnnXEGQN0UNr2vW80apQ+Av1Amyy1zHn1eoMKkxHEA9rnxeO3IMhIFe/uQnsj1TjiYTgPTWFI48evGUle1QLtMI19auyMhwk/Z5k6E8gwPL/aZCItZhxBaDEKhcj+Gmh6ftv+BoGc5iyoD8x6/EdqS4mSZtJH3Nwjyz+jLxBDCJ8xqVK7hQgIVwwtFGtksAvfYaMFJp2X7D+1ast+L+7cTDGg1wIDAQAB";
    private int dlgToShow =0;   
    
    private static Licenser licenser;
	
    private static String pkgName;
	private static int numAppUsed=0;

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState); 
	        pkgName = getPackageName();
	        setContentView(R.layout.activity_splash);	
	        
	        ImageView iv = (ImageView)this.findViewById(R.id.img_splash);
	        Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
	        iv.setImageBitmap(bmp1);
	        
	        iv = (ImageView)findViewById(R.id.splash_title);
	        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.splash_text);
	        iv.setImageBitmap(bmp2);
	        
	        iv = (ImageView)findViewById(R.id.img_logo);
	        Bitmap bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo);
	        iv.setImageBitmap(bmp3);
	        
	        runActivity();      
	       
	    }
	 
	 
//	 
//	 @Override
//	protected void onResume() {
//		
//		super.onResume();
//	}



	@Override
	protected void onStop() {		 
		 recycleBitmapFromImageView(R.id.img_splash);
		 recycleBitmapFromImageView(R.id.splash_title);
		 recycleBitmapFromImageView(R.id.img_logo);
		 
		 super.onStop();
	}
	 
	 


	private void runActivity()
	  {
	    	//check for license
	    	if(checkAppLicense())
	        {
			   	runApp();
	        }		    	
	   }
	 
	    
	    private void done()
		{
	    	DataStore ds = new DataStore(this);
	    	boolean interview = ds.getInterviewDone();
	    	if(interview)
	    	{
	    		startMainActivity();				
	    	}
	    	else
	    	{
	    		this.showMsgDlg(R.string.disclimer, Common.REASON_NOT_IMPORTANT);	    		
	    		
	    	}			
		}    
	    
	    
	    
	    @Override
		public void processMsgDlgClosed(int reason) {
			super.processMsgDlgClosed(reason);
			Bundle b = new Bundle();
	    	b.putInt("next", Common.ACT_InterviewActivity);
	    	Common.showActivityFromMenu(this, b);
		}

		
		private void startMainActivity()
	    {
	    	Intent intent = new Intent(this, FirstActivityPhone.class);
    		startActivity(intent);
			finish();
	    }
		

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch(requestCode)
			{
			case Common.ACT_InterviewActivity:
				startMainActivity();
				return;
			default: super.onActivityResult(requestCode, resultCode, data);
			break;
			}
			
		}

		@Override
		public int getId() {
			return Common.ACT_SplashActivityPhone;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {	
			if(!initDone) return true; 
			if(event.getAction() == MotionEvent.ACTION_DOWN)
	        {
	            synchronized(mSplashThread){
	             mSplashThread.notifyAll();
	            }
	        }
	        return true;
			 
		}  

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.splash, menu);
			return true;
		}
		
		 private void runApp()
		 {
			 //init user
			 User user = User.getUser();
			 DataStore ds = new DataStore(this);
			 user.setName(ds.getUserName());
			 user.setAge(ds.getAge());
			 user.setGender((ds.getIsMale())? Gender.Male : Gender.Female);
			 final FitnessLevel fl = FitnessLevel.getFitnessLevelFromId(ds.getFitnessLevel());
			 user.setFitnessLevel(fl);		
			 
			 user.setRestHR(ds.getRestHR());
			 
			 user.setWeight(ds.getWeight());
			 
			 user.setDistanceKm(ds.getDistanceUnitsKm());
			 user.setIsWeightLbs(ds.getIsUserWeightUnitsLbs());		 
			 
			// Check if the database exists before copying
			
			    final String db_destination = getDbDestination();
				File f = new File(db_destination);
				boolean initialiseDatabase = f.exists();
				if (initialiseDatabase == false)
				{
					new DbLoadTask().execute();
				}
				else
				{
					 // The thread to wait for splash screen events
			        mSplashThread =  new Thread(){
			            @Override
			            public void run(){
			                try {
			                    synchronized(this){
			                        // Wait given period of time or exit on touch
			                        wait(5000);  
			                    }
			                }
			                catch(InterruptedException ex){                    
			                }   		                
			                // Run next activity                
			                done();
			            }
			        };
			        
			        mSplashThread.start(); 
			        initDone = true;
				}
		 }
		 
		 private void processInstallDone(final boolean res)
		{
			if(res)
			{
				initDone = true;
				done();
			}
			else
			{
				//Common.showToast("Error installing database.", this); ///////////////////////
				//this.showDialog(DLG_INSTALL_ERROR);
				
				this.showErrorDlg(R.string.installerror, Common.REASON_NOT_IMPORTANT);
			}
		}
		 
		
		  private String getDbDir()
		 {		
			String dir= this.getDatabasePath(DBAdapter.DATABASE_NAME).getAbsolutePath();
			dir = dir.substring(0, dir.lastIndexOf("/"));
			 return dir; //this.getFilesDir().getPath() +"/databases";
		 }
		 
		 private String getDbDestination()
		 {
			 return this.getDatabasePath(DBAdapter.DATABASE_NAME).getAbsolutePath();
		 }
		 
		 private boolean copyDb() 
			{
			 final String db_dir = getDbDir();
			 final String db_destination = db_dir + "/" + DBAdapter.DATABASE_NAME;
				File f = new File(db_destination);
				if(!f.exists())
				{
					File dir = new File(db_dir);
					if(!dir.exists())
					{
						dir.mkdirs();
					}
					try {				
						f.createNewFile();
					} catch (IOException e) {  ///////////////////////////////////////////////////////
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				boolean result = true;
				 InputStream is = null;		 
				 // Copy the database into the destination
				 OutputStream os = null;
				try {
					is = getBaseContext().getAssets().open("fit.db");
					os = new FileOutputStream(f);		
				    byte[] buffer = new byte[1024];
				    int length;
					 while ((length = is.read(buffer)) > 0){
			 	        os.write(buffer, 0, length);
					 }
					} 
				    catch (FileNotFoundException e) 
				    {	
				    	result = false;
						e.printStackTrace();				
					}
					catch (IOException e) 
				    {	
						result = false;
						e.printStackTrace();
					}
				finally
				{
					if(os != null)
					{
						try {
							os.flush();
							os.close();
						 	is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}	 	   
				}
				 
		 	    return result;
				
			}
		 
	//////////////////LICENSING ////////////////
		public void StartApp()
		{
			runApp();
		}
		private boolean checkAppLicense()
		{
			//For other app stores 
			return true;
			
			//For Google & Samsung
//			if(getStoredLicensed()) return true;		
			
			//For Google    	
//			licenser = new GoogleLicenser(this);
//			return licenser.checkAppLicense();
			
			//Samsung
//			licenser = new SamsungLicenser(this);
//			return licenser.checkAppLicense();	
		
		}	  
		
		public boolean getStoredLicensed()
		{
			DataStore ds = new DataStore(this);
			final boolean haveLicence = ds.getIsLicensed();
			return haveLicence;
		}
		
		public void setTimesUsed(final int timesUsed)
		{
			DataStore ds = new DataStore(this);	    	
			ds.setTimesUsed(timesUsed);
			//Log.d("FIT", "timesUsed=" + timesUsed);
		}
		
		public void setLicensed()
		{
			DataStore ds = new DataStore(this);	    	
			//Log.d("FIT", "setLicensed");	    	
			ds.setLicensed(); /////////////////////////////////////////////////////////////////////
			ds.setTimesUsed(0);
		}
		
		public void useAppWithoutLicense(final int timesUsed)
		{
			setTimesUsed(timesUsed+1);
			StartApp();
		}
		
		public void displayDlg(final int id)
		{
			dlgToShow = id;
			showDialog();
		}
		
		
		 
		public int getNumUsed()
	  	{
	  		DataStore ds = new DataStore(this);
		    return ds.getTimesUsed();
	  	}
	  	
	  	@Override
		protected void onDestroy() {	
			super.onDestroy();
			if(licenser != null) licenser.doDestroy();
		}


		//////////////////////////DIALOG ////////////////////////////
		public void removeDlg()
		{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	  	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	  	    if (prev != null) {  	    	
	  	        ft.remove(prev);
	  	    }
	  	    ft.addToBackStack(null);
	  	    ft.commit();
		}
		void showDialog()
		{
			removeDlg();
	  		switch(dlgToShow)
	  		{
	  		case DLG_INSTALL_ERROR:
	  			DialogFragment newFragment = DlgAlert.newInstance(dlg_install_err_msg);
	  		    newFragment.show(this.getSupportFragmentManager(), "dialog");
	  			break;
	  		case Licenser.DIALOG_RETRY:
	  			numAppUsed = this.getNumUsed();
	  			DialogFragment newFragment4 = DlgRetry.newInstance(R.string.lisensing_error);
	  			newFragment4.show(this.getSupportFragmentManager(), "dialog");
	  			break;
	  		case Licenser.DIALOG_GOTOMARKET:
	  			DialogFragment newFragment2 = DlgGoToMarket.newInstance(R.string.lisensing_error);
	  			newFragment2.show(this.getSupportFragmentManager(), "dialog");
	  			break;
	  		default:
	  			DialogFragment newFragment3 = DlgAlert.newInstance(R.string.errstart);
	  		    newFragment3.show(this.getSupportFragmentManager(), "dialog");
	  			break;
	  		}
		}
		
		public void doPositiveClick() {
			//nothing
		}

		public void doNegativeClick() {
		//nothing
		}
		
		public static class DlgAlert extends DialogFragment
		{
			  public static DlgAlert newInstance(int title) {
				    DlgAlert frag = new DlgAlert();
			        Bundle args = new Bundle();
			        args.putInt("title", title);
			        frag.setArguments(args);
			        return frag;
			    }
			  @Override
			  public Dialog onCreateDialog(Bundle savedInstanceState) {
			        int title = getArguments().getInt("title");

			        return new AlertDialog.Builder(getActivity())
			                //.setIcon(android.R.drawable.alert_dialog_icon)
			                .setTitle(title)
			                .setPositiveButton(R.string.ok_but,
			                    new DialogInterface.OnClickListener() {
			                        public void onClick(DialogInterface dialog, int whichButton) {
			                            ((SplashActivityPhone)getActivity()).doPositiveClick();
			                        }
			                    }
			                )		                
			                .create();
			    }
		}//DlgAlert
		
		public static class DlgGoToMarket extends DialogFragment
		{
			public static DlgGoToMarket newInstance(int title)
			{
				DlgGoToMarket frag = new DlgGoToMarket();
				Bundle args = new Bundle();
		        args.putInt("title", title);
		        frag.setArguments(args);
		        return frag;			
			}
			 @Override
			  public Dialog onCreateDialog(Bundle savedInstanceState) {
			        int title = getArguments().getInt("title");

			        return new AlertDialog.Builder(getActivity())
			                //.setIcon(android.R.drawable.alert_dialog_icon)
			                .setTitle(title)
			                .setMessage(R.string.unlicensed_dialog_title)
			                .setPositiveButton(R.string.buy_button, new DialogInterface.OnClickListener() {
			            
				            public void onClick(DialogInterface dialog, int which) {			              
				                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
				                            "http://market.android.com/details?id=" + pkgName));
				                        startActivity(marketIntent);    
				                        dialog.dismiss();	
				               
			            }
			        })  
			        .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            	 dialog.dismiss();	
			            }
			        })
			        .create();
			    }
		}//DlgGoToMarket
		
		public static class DlgRetry extends DialogFragment
		{
			public DlgRetry()
			{
				
			}
			
			static DlgRetry newInstance(int title)
			{
				DlgRetry dlg = new DlgRetry();
				return dlg;
			}
			
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState)
			{
				View dlg = inflater.inflate(R.layout.license_dlg, container);	
			   
				getDialog().setTitle(this.getString(R.string.app_name));
			    Button butRetry = (Button) dlg.findViewById(R.id.butRetry);
			    Button butUse = (Button) dlg.findViewById(R.id.butUse);
			    Button butQuit = (Button) dlg.findViewById(R.id.butQuit);
			    TextView txtView = (TextView)dlg.findViewById(R.id.txtMsg);
			    if(txtView != null && licenser != null)
			    {
			    	String msg;
					final int errIndex = licenser.getErrIndex();
					msg = licenser.getRetryDlgMsg(errIndex);		
			    	if(msg != null) txtView.setText(msg);
			    }
			    
			    
			   
			    if(numAppUsed >= Licenser.MAX_NOTLICENSED)
			    {
			    	butUse.setVisibility(View.GONE);
			    }
			    else
			    {
			    	butUse.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {						
							getDialog().dismiss();
							((SplashActivityPhone)getActivity()).useAppWithoutLicense(numAppUsed);	
							
						}
					});
			    }
			    
			    butRetry.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						 //Log.e("LIC","butRetry call doCheck");					
						getDialog().dismiss();
						((SplashActivityPhone)getActivity()).checkAppLicense();					
						
					}
				});
			    
			    butQuit.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {					
						getDialog().dismiss();
						((SplashActivityPhone)getActivity()).finish();
						
					}
				});
			    
			    return dlg;
			}
		}
		
		//Interface methods
			public Activity getActivity() {		
				return this;
			}

			public String getPublicKey() {
				return BASE64_PUBLIC_KEY;
			}
		 
	//////////////////////////////////ASYNC TASK
			private class DbLoadTask extends AsyncTask<String, Void, long[]>
		  	{
		  		private ProgressDialog dialog = new ProgressDialog(SplashActivityPhone.this);  
		  		
		  		
		  		// can use UI thread here
		 		 protected void onPreExecute() { 
		 			 this.dialog.setMessage(getString(R.string.installing));  
		 			 this.dialog.show();  
		 		  }  

				@Override
				protected long[] doInBackground(String... arg0) {			
					   long[] result = new long[1];
					   if(copyDb())
					   {
						   result[0] = 1;
					   }
					   else
					   {
						   result[0] = 0;
						   
					   }			
						return result;
					
				}
				
				protected void onPostExecute(final long[] result) {  
					try
					{
						if(dialog != null)
						{
							dialog.dismiss();
							dialog = null;
						}
					}
					catch(Exception e)
					{
						//nothing
					}	         
			          
			          if(result != null && result.length >0)
			          {
			        	  SplashActivityPhone.this.processInstallDone((result[0] == 1)? true : false);
			          }	          
			        
					}     		
		  		
		  	}
		
	    
	    
}
