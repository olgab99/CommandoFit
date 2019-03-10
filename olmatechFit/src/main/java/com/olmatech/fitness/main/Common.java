package com.olmatech.fitness.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.music.PlayMusicActivity;
import com.olmatech.fitness.music.PlaylistActivity;
import com.olmatech.fitness.ui.AdvancedSettingsActivity;
import com.olmatech.fitness.ui.CalcMenuActivity;
import com.olmatech.fitness.ui.DateSelectActivity;
import com.olmatech.fitness.ui.HelpActivity;
import com.olmatech.fitness.ui.HrCalculatorActivity;
import com.olmatech.fitness.ui.InterviewActivity;
import com.olmatech.fitness.ui.LogCalendarActivity;
import com.olmatech.fitness.ui.MaxrepActivity;
import com.olmatech.fitness.ui.MenuActivity;
import com.olmatech.fitness.ui.SettingsActivity;
import com.olmatech.fitness.ui.UnitConvertActivity;

public class Common {
	
	public final static boolean DEBUG = false;
	
	//dialogs
	public final static int DLG_ERROR=1;
	public final static int DLG_EX_LIST =2;
	public final static int DLG_YESNO=3;
	public final static int DLG_MSG=4;
	public final static int DLG_YESNOCANCEL=5;
	
	public final static int BG_COLOR = Color.rgb(42, 43, 43);
	
	//Loader IDs
    public final static int LOADER_EX_LIST=-1;
    public final static int LOADER_WK_LIST=-2;
    public final static int LOADER_EX_LIST_DLG=-3;
    
    //results
    public final static int RESULT_OK = 100;
    public final static int RESULT_ERROR =101;
    public final static int RESULT_CANCEL=102;
    public final static int RESULT_DONOTHING=103;
    public final static int RESULT_RESET=104;
    public final static int RESULT_BACK=105;
    
    //reasons for dialogs
    public final static int REASON_NOT_IMPORTANT=1000;
    public final static int REASON_FATAL_ERROR=1001;
    
    //activities
   
    public final static int ACT_InterviewActivity=501;
    public final static int ACT_CalcMenuActivity=502;
    public final static int ACT_FirstActivityPhone=503;  //FirstActivity
    public final static int ACT_MainActivityPhone=504;
    public final static int ACT_ExListActivity=505;
    public final static int ACT_ExerciseActivityPhone=506;
    public final static int ACT_MenuActivity=507; // list of menu options
    public final static int ACT_HELP=508;
    public final static int ACT_HrCalculatorActivity = 509;
    public final static int ACT_MaxrepActivity = 510;
    public final static int ACT_Settings=511;
    public final static int ACT_LogListDlgActivity =512;
    public final static int ACT_CardioZonesActivity=513;
    public final static int ACT_TackticalActivity=514;
    public final static int ACT_ExImageViewActivity=515;
    public final static int ACT_LogCalendarActivity=516;
    public final static int ACT_LogViewActivity=517;
    public final static int ACT_PopupDlgActivity=518;
    public final static int ACT_FacebookActivity=519;
    public final static int ACT_DateSelectActivity=520;
    public final static int ACT_SplashActivityPhone = 521;
    public final static int ACT_AdvancedSettingsActivity = 522;
    public final static int ACT_PlaylistActivity=523;
    public final static int ACT_PlayMusicActivity=524;
    public final static int ACT_UnitConvertActivity=525;
    
    //Fragments
    public final static int ID_WK_FRAGMENT=1;
    public final static int ID_EX_FRRAGMENT=2;
    public final static int ID_EX_DLG_FRAGMENT=3;
    
    //cardio equipment
    public final static int[] CARDIO_EQUIPMENT = new int[]{R.drawable.cardio1, R.drawable.cardio2, R.drawable.cardio3, 
    	R.drawable.cardio4, R.drawable.cardio5, R.drawable.cardio6};
    
    //timer
    public final static int DEF_REST_TIME=60; //secs
    
    public final static String NEWLINE = System.getProperty("line.separator");
    
    public final static int EMPTY_VALUE=-1;   
    public final static int ZERO_ID=0;  //id for non-program week and day
    
    public static final int DEFAULT_WEIGHT_INTERVAL =1;
    public static final int DEFAULT_WEIGHT_BREAK = 500;
    
    private static boolean ISTABLET = false;
    private static boolean ISTABLETSET = false;
    
    //Music
    private static boolean canStartMusicService = true;
    private static boolean canStartMusicServiceSet= false;
    
    //public static final double MAXREP_ROUND= 0.0;
    
            
    //calculators
    public enum Calculator
    {
    	General,
    	Karvonen,
    	Unknown;
    	
    	public static int getId(final Calculator calc)
    	{
    		if(calc == General) return 1;
    		else if(calc == Karvonen) return 2;    		
    		else return -1;
    	}
    	public static Calculator getCalcFromId(final int id)
    	{
    		switch(id)
    		{
    		case 1: return General;
    		case 2: return Karvonen;    		
        	default: return Unknown;
    		}
    	}
    	
    	public static String getCalcName(Calculator calc)
    	{
    		if(calc == General) return "General";
    		else if(calc == Karvonen) return "Karvonen";    		
    		else return null;
    	}
    }
	
	public static void showToast(final String msg, Context context)
	{		
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, msg, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CLIP_HORIZONTAL, 0, 0);
		toast.show();
	}
	
	public static void sendMail(final Activity act, final String addr, final String subject, 
			final String body, final File attachmentFile)
	{
		try {
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);			
			emailIntent.setType("plain/text");
			
			final PackageManager pm = act.getPackageManager();
			final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
			ResolveInfo best = null;
			for (final ResolveInfo info : matches) {
			    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
			        best = info;
			        break;
			    }
			}
			if (best != null) {
				emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
			}
			
			if(attachmentFile != null){
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachmentFile));				
			}	       
	       if(addr != null) emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{addr});  
	        if(subject != null) emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
	        else emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Email from Commando training"); //TODO
	        if(body != null) emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
	        	        	       
	        try
	        {
	        	//act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	        	act.startActivity(emailIntent);
	        }
	        catch(android.content.ActivityNotFoundException ex)
	        {
	        	showToast("There are no email clients installed.", act);
	        }	        
	        
		} catch (ActivityNotFoundException e) {
	        // cannot send email for some reason
			showToast("Error sending email.", act);
	    }
	}
	
//	public static void sendMail(final Activity act, final String addr, final String subject, 
//			final String body, final File attachmentFile)
//	{
//		try {
//			//final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//			final Intent emailIntent = new Intent(android.content.Intent.ACTION_VIEW);
//			emailIntent.setType("plain/text");
//			
//			if(attachmentFile != null){
//				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachmentFile));				
//			}	       
//	       if(addr != null) emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{addr});  
//	        if(subject != null) emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
//	        else emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Email from Commando training"); //TODO
//	        if(body != null) emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
//	        
//	        //gmail only
//	        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivity");
//	       
//	        try
//	        {
//	        	//act.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//	        	act.startActivity(emailIntent);
//	        }
//	        catch(android.content.ActivityNotFoundException ex)
//	        {
//	        	showToast("There are no email clients installed.", act);
//	        }	        
//	        
//		} catch (ActivityNotFoundException e) {
//	        // cannot send email for some reason
//			showToast("Error sending email.", act);
//	    }
//	}
	
	public static void setIsTablet(final boolean val)
	{
		ISTABLET = val;
		ISTABLETSET = true;
	}
	
	public static boolean getIsTablet()
	{
		return ISTABLET;
	}
	
	public static boolean isTabletSet()
	{
		return ISTABLETSET;
	}
	
	public static boolean isTabletDevice(Context activityContext) { 		
		DataStore ds = new DataStore(activityContext);       
        if(ds.getIsTabletSet())
        {
        	return ds.getIsTablet();
        }
	    try { 
	    	// Verifies if the Generalized Size of the device is XLARGE to be
	        // considered a Tablet
	    	 boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
	    	 boolean large = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	        
	    	 
	    	 // Compute screen size 
	        DisplayMetrics dm = activityContext.getResources().getDisplayMetrics(); 
	     // If XLarge, checks if the Generalized Density is at least MDPI
	        // (160dpi)
	         if(xlarge || large)
	         {
	        	 if (dm.densityDpi == DisplayMetrics.DENSITY_DEFAULT
	                     || dm.densityDpi == DisplayMetrics.DENSITY_HIGH
	                     || dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM   
	                     || dm.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

	                // Yes, this is a tablet!
	                return true;
	           }
	         }
	         return false;
	        
	        //check pixel size first
//	        final int wd = Math.min(dm.widthPixels, dm.heightPixels);        
//	        
//	        float screenWidth  = dm.widthPixels / dm.xdpi; 
//	        float screenHeight = dm.heightPixels / dm.ydpi; 
//	        double size = Math.sqrt(Math.pow(screenWidth, 2) + 
//	                                Math.pow(screenHeight, 2)); 
//	        // Tablet devices should have a screen size greater than 6 	inches 
//	        return (size >= 6 && wd >=600);   
	    } catch(Throwable t) { 
	        Log.e("Common", "Failed to compute screen size", t); 
	        return false; 
	    } 
	} 
	
	public static boolean isLargeSize(Context activityContext)
	{
		if(activityContext == null) return false;
		boolean yes= false;
		 try { 
		    	// Verifies if the Generalized Size of the device is XLARGE to be
		        // considered a Tablet
		    	 if(((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4) ||
		    			 ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE))
		    	 {
		    		 yes = true;
		    	 }		    	 
		 }
		 catch(Exception ex)
		 {
			 //nothing
		 }
		 return yes;
	}
	
	public static Bitmap getBmpFromAssets(final String name, final String dir, final Activity act)
	{
		if(act == null) return null;
		Bitmap bm;
		String strName = dir + File.separator + name;
		
		try {
		    AssetManager assetManager = act.getAssets();
		    InputStream istr = assetManager.open(strName);
		    bm = BitmapFactory.decodeStream(istr);			
			
		} catch (IOException e) {
			e.printStackTrace();
			bm=null;
		}
		return bm;
	}
	
	//calculator
	public static void showCalculatorMenu(final Activity act)
	{
		if(act == null) return;
		// display calculator menu
		Intent intent = new Intent(act, CalcMenuActivity.class);		
		act.startActivityForResult(intent, ACT_CalcMenuActivity);
	}
	
	public static void showDateSelectActivity(final Activity act)
	{
		if(act == null) return;
	    Intent intent = new Intent(act, DateSelectActivity.class);
	    act.startActivityForResult(intent, ACT_DateSelectActivity);
	}
	

	public static void showMenuActivity(final Activity act, final int actId)
	{
		if(act == null) return;
	    Intent intent = new Intent(act, MenuActivity.class);
	    //determin if we want to show Interview button
	   
	    intent.putExtra("caller_id", actId);
		act.startActivityForResult(intent, ACT_MenuActivity);
	}
	
	//show activity based on user selection in Menu
	public static void showActivityFromMenu(final Activity act, final Bundle data)
	{
		if(act == null || data == null || ! data.containsKey("next")) return;		
		int actToShow = data.getInt("next", 0);
		Intent intent;
		switch(actToShow)
		{
		case ACT_InterviewActivity:
			intent = new Intent(act, InterviewActivity.class);				
			act.startActivityForResult(intent, ACT_InterviewActivity);
			break;
		case ACT_Settings:
			intent = new Intent(act, SettingsActivity.class);				
			act.startActivityForResult(intent, ACT_Settings);
			break;
		case ACT_LogCalendarActivity:
			intent = new Intent(act, LogCalendarActivity.class);
		    final boolean future = (data.containsKey("future")? data.getBoolean("future") : false);			
		    intent.putExtra("future", future);
		    act.startActivityForResult(intent, ACT_LogCalendarActivity);
		    break;	
		case ACT_FacebookActivity:
//			intent = new Intent(act, FacebookActivity.class);
//			act.startActivityForResult(intent, ACT_FacebookActivity);
			break;
		case ACT_MaxrepActivity:
			intent = new Intent(act, MaxrepActivity.class);
		    act.startActivityForResult(intent, ACT_MaxrepActivity);
			break;
		case ACT_HrCalculatorActivity:
			intent = new Intent(act, HrCalculatorActivity.class);
			final int calc = (data.containsKey("calculator"))? data.getInt("calculator") : 1;
			intent.putExtra("calculator", calc);
			act.startActivityForResult(intent, ACT_HrCalculatorActivity);
			break;
		case ACT_AdvancedSettingsActivity:
			intent = new Intent(act, AdvancedSettingsActivity.class);	
			act.startActivityForResult(intent, ACT_AdvancedSettingsActivity);
			break;
		case ACT_PlaylistActivity:
			intent = new Intent(act, PlaylistActivity.class);	
			act.startActivityForResult(intent, ACT_PlaylistActivity);
			break;
		case ACT_PlayMusicActivity:
			intent = new Intent(act, PlayMusicActivity.class);	
			act.startActivityForResult(intent, ACT_PlayMusicActivity);
			break;
		case ACT_UnitConvertActivity:
			intent = new Intent(act, UnitConvertActivity.class);	
			act.startActivityForResult(intent, ACT_UnitConvertActivity);
			break;
		default: break;
		}
		
	}
	
	public static void showHelp(final Activity act, final int actId)
	{
		if(act == null) return;
		//show relevant help
		Intent intent = new Intent(act, HelpActivity.class);	
		intent.putExtra("caller", actId);
		act.startActivityForResult(intent, ACT_HELP);
	}
	
	public static double calculateMaxRep(final int weight, final int repsval)
	{
		if(repsval == 1)
		{
			return weight;
		}
		double wt = (double)weight;
		double reps = (double)repsval;
		//double epley = wt*reps / 30f + wt;
		double lander = (100.0*wt) /(101.3 - 2.67123*reps);
		double brzycki = wt *(36.0 /(37.0 -reps));
		//return ((lander+brzycki)/2 +1.0);  /////////////////////////// HERE -was before
		return ((lander+brzycki)/2);
	}
	
	public static int MilesToMeters(final double miles)
	{
		return (int)(miles*1609.34);
	}
	
	public static int getCardioZoneHR(final int hrMax, final int hrRest, final double intensity, 
			final Calculator calcType)
	{		
		switch(calcType)
		{
		case General:
			return (int)((double)hrMax*intensity + 0.5);
		case Karvonen:
			//THR = ((HRmax − HRrest) × % intensity) + HRrest
			if(hrRest <=0) return -1;
			return (int)((double)(hrMax-hrRest)*intensity +0.5 + (double)hrRest);			
		default:return -1;
		}
		
	}
	
//	public static Bitmap getBmpFromAssets(final String name, final String dir, final  AssetManager assetManager)
//	{		
//		Bitmap bm;
//		String strName = dir + File.separator + name;
//		
//		try {
//		    InputStream istr = assetManager.open(strName);
//		    bm = BitmapFactory.decodeStream(istr);			
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//			bm=null;
//		}
//		return bm;
//	}
	
	
	
	public static long getDateTimeForToday()
	{
		Date d = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);		
		Calendar cal2 =  new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0); //to keep y/m/dayOfMonth
		return cal2.getTimeInMillis();
		
	}	
	
	public static long getDateTimeForDay(final int y, final int m, final int d)
	{
		Calendar cal = new GregorianCalendar(y,m,d,0,0,0);
		return cal.getTimeInMillis();
	}
	
	public static long getDateTimeForDate(final Calendar dt)
	{
		return getDateTimeForDay(dt.get(Calendar.YEAR), dt.get(Calendar.MONTH), dt.get(Calendar.DATE));
	}
	
	public static String getShortMonthName(final int m)
	{
		switch(m)
		{
		case Calendar.JANUARY: return "JAN";
		case Calendar.FEBRUARY: return "FEB";
		case Calendar.MARCH: return "MAR";
		case Calendar.APRIL: return "APR";
		case Calendar.MAY: return "MAY";
		case Calendar.JUNE: return "JUN";
		case Calendar.JULY: return "JUL";
		case Calendar.AUGUST: return "AUG";
		case Calendar.SEPTEMBER: return "SEP";
		case Calendar.OCTOBER: return "OCT";
		case Calendar.NOVEMBER: return "NOV";
		case Calendar.DECEMBER: return "DEC";
		default: return null;
		}
	}
	
	public static boolean writeToFile(File f, String data) {
		BufferedWriter writer = null;
		boolean ok = true;
		try
		{
		    writer = new BufferedWriter( new FileWriter( f));
		    writer.write( data);

		}
		catch ( IOException e)
		{
			e.printStackTrace();
			ok=false;
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
		return ok;
	}
	
	public static void setKeepScreenOn(Activity act, final boolean keepon)
	{
		if(act == null) return;
		ViewGroup view = (ViewGroup)act.getWindow().getDecorView();			
		if(keepon != view.getKeepScreenOn()) view.setKeepScreenOn(keepon);
	}
	
	
	public static void setCanPlayMusic(final boolean val)
	{
		canStartMusicService = val;
		canStartMusicServiceSet = true;
	}
	
	public static boolean getCanPlayMusic()
	{
		return canStartMusicService;
	}
	
	public static boolean getCanPlayMusicSet()
	{
		return canStartMusicServiceSet;
	}
	
	///IMAGES ////////////////////
	
	public static Bitmap getBmpFromAssets2(final String name, final String imgDirectory, final  AssetManager assetManager,
			 final int maxImgW, final int maxImgH)
	{
		String strName = imgDirectory + name;
		
		Bitmap bm = null;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		  //////////////////////////////
		InputStream istr = null;
		try
		{
			istr = assetManager.open(strName);
			BitmapFactory.decodeStream(istr, null, options);
			try {
			    istr.reset();
			} catch (IOException e) {
			    Log.e("Com", e.getMessage());
			}	
			// Calculate inSampleSize			
		   		 			
			options.inSampleSize = Common.calculateInSampleSize( options.outWidth, options.outHeight, maxImgW, maxImgH); 
		 // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    bm = BitmapFactory.decodeStream(istr, new Rect(), options);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			bm = null;
		}
		finally
		{
			try
			{
				if(istr != null) istr.close();
			}
			catch(Exception ex2)
			{
				
			}
		}
		
		return bm;
	}
	
	//calc the sample size
		public static int calculateInSampleSize(final int width, final int height, int reqWidth, int reqHeight) {
	    // Raw height and width of image
//		    final int height = options.outHeight;
//		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {
		
		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;
		
		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                && (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }
		
		    return inSampleSize;
		}
		
		
	
	
}
