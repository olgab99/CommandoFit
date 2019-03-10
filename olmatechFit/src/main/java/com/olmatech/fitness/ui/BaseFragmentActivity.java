package com.olmatech.fitness.ui;

import java.io.IOException;
import java.io.InputStream;

import kankan.wheel.widget.adapters.WeightLbWheelAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.interfaces.IHaveId;
import com.olmatech.fitness.interfaces.IShowDialogs;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.music.MusicPlayerService;

//activity with Error Dialog, YesNo Dialog and has ID
public abstract class BaseFragmentActivity extends FragmentActivity implements IHaveId, IShowDialogs{
	
	private final static String TAG="BaseFragmentActivity";
	protected int dlgMsg;
	private int dlgOkMsg = R.string.ok_but;
	private int dlgCancelMsg=R.string.cancel_but;
	private int dlgDoNothingMsg= R.string.cancel_but;
	
	protected static int SCREEN_WIDTH=0;;
		
	//protected boolean closeOnError = false;
	//private int dlgReason=Common.REASON_NOT_IMPORTANT; //up to der. class to set
	
 @Override
	protected void onCreate(Bundle b) {		
		super.onCreate(b);
		
		dlgMsg = R.string.app_name; //default
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.setVolumeControlStream(AudioManager.STREAM_RING);  
		
		if(SCREEN_WIDTH <=0)
		{			
			readScreenWidth();
		}		
	}
 
 	protected void readScreenWidth()
 	{
 		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		SCREEN_WIDTH = metrics.widthPixels;
 	}
 
 
// protected void setCloseOnError(final boolean val)
// {
//	 closeOnError = val;
// }
 
 
    @Override
	public abstract int getId();
 
// @Override
//public int getId() {
//	// TODO Auto-generated method stub
//	return 0;
//}
    
    public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.common_small_close:
			this.setResult(Common.RESULT_CANCEL);
			finish();
			return;
		default: break;
		}
	}

protected void resetSettings(){}
protected void resetView(){} //called if we ret RESULT_RESET from menu
protected void resetWeightWheel(){}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch(requestCode)
	{	
	case Common.ACT_HELP:	
	case Common.ACT_MaxrepActivity:
	case Common.ACT_CardioZonesActivity:
	case Common.ACT_HrCalculatorActivity:
	case Common.ACT_UnitConvertActivity:
		return; //do nothing		
		
	case Common.ACT_MenuActivity:
		if(resultCode == Common.RESULT_RESET)
		{
			resetView();
		}
		else if(resultCode == Common.RESULT_BACK)
		{
			this.onBackPressed(); //the same as go back
		}
		else if(resultCode == Common.RESULT_OK && data != null) Common.showActivityFromMenu(this, data.getExtras());	
		break;
	case Common.ACT_CalcMenuActivity:	
		if(resultCode == Common.RESULT_OK && data != null) Common.showActivityFromMenu(this, data.getExtras());	
		break;
	case Common.ACT_Settings:
		if(resultCode == Common.RESULT_OK)
		{
			resetSettings();
		}		
		break;
	case Common.ACT_PopupDlgActivity:
		if(data != null)
		{
			int dlgType = data.getIntExtra("dlg_type", -1);
			int dlgReason = data.getIntExtra("dlg_reason", Common.REASON_NOT_IMPORTANT);
			if(dlgType >0)
			{
				switch(dlgType)
				{
				case Common.DLG_ERROR:
					processErrDlgClose(dlgReason);					
					break;
				case Common.DLG_YESNO:
					if(resultCode ==Common.RESULT_OK) processDlgYes(dlgReason);
					else processDlgNo(dlgReason);
					break;
				case Common.DLG_YESNOCANCEL:
					if(resultCode !=Common.RESULT_DONOTHING)
					{
						if(resultCode ==Common.RESULT_OK) processDlgYes(dlgReason);
						else processDlgNo(dlgReason);
					}
					break;
				case Common.DLG_MSG:
					processMsgDlgClosed(dlgReason);
					break;
				default: break;
				}
			}
		}
		
		break;
	case Common.ACT_AdvancedSettingsActivity:
		if(resultCode == Common.RESULT_OK && data != null)
		{
			final int weightInterval = data.getIntExtra("interval", Common.EMPTY_VALUE);
			final int weightBreak = data.getIntExtra("break",Common.EMPTY_VALUE);
			DataStore ds = new DataStore(this);
			if(ds.getWeightWheelInterval() != weightInterval ||
					ds.getWeightWheelBreak() != weightBreak)
			{
				//changing
				if(weightInterval != Common.EMPTY_VALUE)
				{
					ds.setWeightWheelInterval(weightInterval);
					WeightLbWheelAdapter.setDeltaValue(weightInterval);
				}
				if(weightBreak != Common.EMPTY_VALUE)
				{
					ds.setWeightWheelBreak(weightBreak);
					WeightLbWheelAdapter.setBreakValue(weightBreak);
				}	
				
				resetWeightWheel();
			}
		}
		break;
	case Common.ACT_PlayMusicActivity:
		if(resultCode == Common.RESULT_OK)
		{
//			boolean startPlaying = false;
//			if(data != null)
//			{
//				startPlaying = data.getBooleanExtra("play", false);
//			}
			
			
		}
		break;
	case Common.ACT_FacebookActivity:
		
		break;
	default: super.onActivityResult(requestCode, resultCode, data);
		break;
	}
	
}

public void onHelpClick(View view)
{
	Common.showHelp(this, this.getId());
}


///**
//  * the user selected to see another dayOfMonth / week from Program
//  * @param workoutId
//  */
// 	public void processShowDayWorkout(final int workoutId, final int weekIndex, final int dayIndex,
// 			final String title, final WorkoutMode md){}
 	
// 	/**
// 	 * user selected first exercise to srarrt workout
// 	 * @param curExOrder
// 	 */
// 	public void startWorkout(){}
 	
 
	public void showMsgDlg(final int dlgMessage, final int reason)
	{
		dlgMsg = dlgMessage;
		dlgOkMsg = R.string.ok_but;
		showPopup(Common.DLG_MSG, reason);
	}
		
	public void showErrorDlg(final int msgResourceId,  final int reason)
	{			
			setDlgMsgRes(msgResourceId);
			dlgOkMsg = R.string.ok_but;
			//closeOnError = finishOnErr;
			showPopup(Common.DLG_ERROR, reason);					
	}
	 
	 
	 public void showYesNoDlg(final int dlgMessage, final int okMsg, final int cancelMsg, final int reason)
	 {
		 dlgMsg = dlgMessage;
		 dlgOkMsg = okMsg;
		 dlgCancelMsg=cancelMsg;
		 showPopup(Common.DLG_YESNO, reason);
	 }
	 
	 public void showYesNoCancelDlg(final int dlgMessage, final int okMsg, final int cancelMsg, 
			 final int doNothingMsg, final int reason)
	 {
		 dlgMsg = dlgMessage;
		 dlgOkMsg = okMsg;
		 dlgCancelMsg=cancelMsg;
		 dlgDoNothingMsg=doNothingMsg;
		 showPopup(Common.DLG_YESNOCANCEL, reason);
	 }
	 
		
	 public void processDlgNo(final int reason){}
	 public void processDlgYes(final int reason){}
	 public void processErrDlgClose(final int reason){
		 if(reason == Common.REASON_FATAL_ERROR)
		 {
			 goBackOnError();
		 }
	 }
	 
	 /**
	  * close activity on Common.REASON_FATAL_ERROR
	  */
	 public void goBackOnError(){
		 this.setResult(Common.RESULT_ERROR);
		 finish();
	 }
	 
	 public void processMsgDlgClosed(final int reason){}

	
	// ////////////////////////DIALOG ////////////////////////////
	public void setDlgMsgRes(final int id) {
		dlgMsg = id;
	}
	
	private void showPopup(final int id, final int dlgReason)
	{
		Intent intent = new Intent(this, PopupDlgActivity.class);	
		intent.putExtra("dlg_type", id);
		intent.putExtra("dlg_reason", dlgReason);
		if(id == Common.DLG_YESNO) intent.putExtra("buttons", 2);
		else if(id == Common.DLG_YESNOCANCEL)
		{
			intent.putExtra("buttons", 3);
		}
		intent.putExtra("msg", dlgMsg);
		intent.putExtra("okbut", dlgOkMsg);
		intent.putExtra("nobut", dlgCancelMsg);		
		intent.putExtra("cancelbut", dlgDoNothingMsg);
		startActivityForResult(intent, Common.ACT_PopupDlgActivity);
	}
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch(id)
//		{
//		case Common.DLG_ERROR:
//			//Show dlg with Ok button
//			 AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
//				builder2.setMessage(dlgMsg)
//					.setCancelable(true)					
//					.setPositiveButton(R.string.ok_but, new DialogInterface.OnClickListener(){
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//							if(closeOnError){
//								closeOnError=false;
//								finish(); 
//							}
//							else processErrDlgClose();
//						}		
//					});	
//					
//					return builder2.create();	
//		case Common.DLG_YESNO:
//			//Show dlg with Ok button
//			 AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
//				builder3.setMessage(dlgMsg)
//					.setCancelable(true)	
//					.setNegativeButton(dlgCancelMsg, new DialogInterface.OnClickListener(){
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//							processDlgNo();
//						}		
//					})
//					.setPositiveButton(dlgOkMsg, new DialogInterface.OnClickListener(){
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//							processDlgYes();
//						}		
//					});	
//					
//					return builder3.create();	
//		default: return null;
//		}		
//	}	
	
	
	
	
	
	
	//Exercise list dialog
	
	//images
//	protected Bitmap getBmpFromAssets(final String name, final String dir)
//	{	
//		//TODO - remove and replace with call to Common
//		
//		return Common.getBmpFromAssets(name, dir, getAssets());
//		
//
//	}
	
	/**
	 * Gets bitmap from Assets with max size SCREEN_WIDTH X SCREEN_WIDTH
	 * @param name
	 * @param imgDirectory
	 * @param maxImgW
	 * @param maxImgH
	 * @return
	 */
	protected Bitmap getBitmapFromAssets(final String name,  final String imgDirectory)
	{			
		
		if(imgDirectory== null) return null;
		String strName = imgDirectory + name;
		
		Bitmap bm = null;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		  //////////////////////////////
		InputStream istr = null;
		AssetManager assetManager = this.getAssets();
		if(SCREEN_WIDTH <=0)
		{			
			readScreenWidth();
		}		
		try
		{
			//FIX
			istr = assetManager.open(strName);
			 options.inJustDecodeBounds=true;
			
			BitmapFactory.decodeStream(istr, null, options);
			try {
			    istr.reset();
			} catch (IOException e) {
			    Log.e(TAG, e.getMessage());
			}	
			
			// Calculate inSampleSize		 			
			options.inSampleSize = Common.calculateInSampleSize(options.outWidth, options.outHeight, SCREEN_WIDTH, SCREEN_WIDTH);
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

	@Override
	public void onLowMemory() {
		//kill the music player if running
		try
		{
			this.stopService(new Intent(this, MusicPlayerService.class));
		}
		catch(Exception ex)
		{
			
		}
		
		this.showMsgDlg(R.string.err_low_memory, Common.REASON_NOT_IMPORTANT);
		
		super.onLowMemory();
	}


	protected void recycleBitmapFromImageView(final int imgViewId)
	 {
		 ImageView iv = (ImageView)this.findViewById(imgViewId);		 
		 Bitmap bmp1 = null;
		 try
		 {
			 bmp1 = ((BitmapDrawable)iv.getDrawable()).getBitmap();
		 }
		 catch(Exception ex)
		 {
			 bmp1 = null;
		 }
		 iv.setImageBitmap(null);
		 if(bmp1 != null)
		 {
			 bmp1.recycle();
		 }
	 }
	
	protected void recycleBitmapFromImageViewIfTagNotNull(final int imgViewId)
	 {
		 ImageView iv = (ImageView)this.findViewById(imgViewId);		 
		 if(iv.getTag() == null) 
		 {
			 iv.setImageBitmap(null);
			 return;
		 }
		 Bitmap bmp1 = null;
		 try
		 {
			 bmp1 = ((BitmapDrawable)iv.getDrawable()).getBitmap();
		 }
		 catch(Exception ex)
		 {
			 bmp1 = null;
		 }
		 iv.setImageBitmap(null);
		 if(bmp1 != null)
		 {
			 bmp1.recycle();
		 }
	 }
	
	//ret -1 if error
		protected int getIntValFromEdit(final int viewId)
		{
			View v = this.findViewById(viewId);
			if(v == null) return -1;
			EditText txt = (EditText)v;
			Editable ed = txt.getText();
			if(ed == null || ed.length()==0) return -1;
			String val = ed.toString();
			if(val == null || val.length()==0)
			{			
				return -1;
			}
			int numVal=-1;
			try
			{
				numVal = Integer.parseInt(val);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				numVal=-1;
			}
			return numVal;
		}

}
