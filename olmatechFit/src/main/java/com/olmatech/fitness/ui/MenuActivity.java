package com.olmatech.fitness.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.Workout;
import com.olmatech.fitness.music.BaseMusicPlayerActivity;
import com.olmatech.fitness.music.MusicPlayerService;

//dialog activity
public class MenuActivity extends BaseMusicPlayerActivity{	
	private final static String TAG ="MenuActivity";
	
	private final static int DLG_REASON_RESET_CUR_LOG=1;
	private final static int DLG_REASON_PROGR=2;
	private final static int DLG_REASON_DELETE_ALL_LOGS=3;
	private final static int DLG_REASON_RESET_MAXREP=4;
	private final static int DLG_REASON_RESET_MAXREP_CUR=5;
	
	private boolean needReset = false;
		
	private int caller_id=-1;
	/*	
	 *   ACT_ExerciseActivityPhone			
			ACT_TackticalActivity			
			ACT_MainActivityPhone			
			ACT_LogCalendarActivity
			ACT_FirstActivityPhone
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_menu_ver2);	
		//we have to figure out if we have active workout
		CurProgram progr = CurProgram.getProgram();
		final boolean haveWk=progr.haveWorkout();
		if(!haveWk)
		{
			View butDelLog = this.findViewById(R.id.menu_but_resetlog);
			if(butDelLog != null) butDelLog.setVisibility(View.GONE);
		}

		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("caller_id")) caller_id=savedInstanceState.getInt("caller_id");
			
		}
		else
		{
			Intent intent = getIntent();			
			caller_id = intent.getIntExtra("caller_id", -1);
		}
		
		//int to have View.VISIBLE or View.Gone
		int menu_but_interview;
		int menu_but_settings;
		int menu_but_logs;
		int menu_but_resetlog;
		int menu_but_resetprogr;
		int menu_but_dellogs;
//		int menu_but_facebook;
		int menu_but_finish_workout;
		int menu_but_maxrep_current=View.GONE;
//		int menu_but_wheel;  //advance settings
		
		//close visible always
		//reset recomemnded weights visible always
		
		switch(caller_id)
		{
		case Common.ACT_FirstActivityPhone:
			menu_but_interview = View.VISIBLE;
			menu_but_settings= View.VISIBLE;
			menu_but_logs= View.VISIBLE;
			menu_but_resetlog= View.GONE;
			menu_but_resetprogr= View.VISIBLE;
			menu_but_dellogs= View.VISIBLE;
//			menu_but_facebook= View.VISIBLE;
			menu_but_finish_workout= View.GONE;
//			menu_but_wheel= View.GONE;
			break;		
		case Common.ACT_LogCalendarActivity:			
			menu_but_interview = View.VISIBLE;
			menu_but_settings= View.VISIBLE;
			menu_but_logs= View.GONE;
			menu_but_resetlog= View.GONE;
			menu_but_resetprogr= View.VISIBLE;
			menu_but_dellogs= View.VISIBLE;
//			menu_but_facebook= View.VISIBLE;
			menu_but_finish_workout= View.GONE;
//			menu_but_wheel= View.GONE;
			break;
		case Common.ACT_ExerciseActivityPhone:
			menu_but_interview = View.GONE;
			menu_but_settings= View.VISIBLE;
			menu_but_logs= View.GONE;
			menu_but_resetlog= View.VISIBLE;
			menu_but_resetprogr= View.GONE;
			menu_but_dellogs= View.GONE;
//			menu_but_facebook= View.GONE;
			menu_but_finish_workout= View.VISIBLE;
			
			if(haveWk)
			{
				Workout workout = progr.getWorkout();
				if(workout != null)
				{
					if(workout.getCurExercise().getExType()== ExType.Weights) menu_but_maxrep_current=View.VISIBLE;
					
				}
						
			}	
			
//			menu_but_wheel= View.VISIBLE;
			break;
		case Common.ACT_TackticalActivity:
			menu_but_interview = View.GONE;
			menu_but_settings= View.VISIBLE;
			menu_but_logs= View.GONE;
			menu_but_resetlog= View.VISIBLE;
			menu_but_resetprogr= View.GONE;
			menu_but_dellogs= View.GONE;
//			menu_but_facebook= View.GONE;
			menu_but_finish_workout= View.GONE;
//			menu_but_wheel= View.GONE;
			break;
		case Common.ACT_MainActivityPhone:
			menu_but_interview = View.GONE;
			menu_but_settings= View.VISIBLE;
			menu_but_logs= View.GONE;
			menu_but_resetlog= View.VISIBLE;
			menu_but_resetprogr= View.GONE;
			menu_but_dellogs= View.GONE;
//			menu_but_facebook= View.GONE;
			menu_but_finish_workout= View.GONE;
//			menu_but_wheel= View.GONE;
			break;
		default:			
			menu_but_interview = View.GONE;
			menu_but_settings= View.VISIBLE;
			menu_but_logs= View.GONE;
			menu_but_resetlog= View.VISIBLE;
			menu_but_resetprogr= View.GONE;
			menu_but_dellogs= View.GONE;
//			menu_but_facebook= View.GONE;
			menu_but_finish_workout= View.GONE;
//			menu_but_wheel= View.GONE;
			break;
		}
		
		//hide / show
		
		View but = findViewById(R.id.menu_but_interview);
		if(but != null) but.setVisibility(menu_but_interview);
		
		but = findViewById(R.id.menu_but_settings);
		if(but != null) but.setVisibility(menu_but_settings);
		
		but = findViewById(R.id.menu_but_logs);
		if(but != null) but.setVisibility(menu_but_logs);
		
		but = findViewById(R.id.menu_but_resetlog);
		if(but != null) but.setVisibility(menu_but_resetlog);
		
		but = findViewById(R.id.menu_but_resetprogr);
		if(but != null) but.setVisibility(menu_but_resetprogr);
		
		but = findViewById(R.id.menu_but_dellogs);
		if(but != null) but.setVisibility(menu_but_dellogs);
		
//		but = findViewById(R.id.menu_but_facebook);
//		if(but != null) but.setVisibility(menu_but_facebook);
		
		but = findViewById(R.id.menu_but_finish_workout);
		if(but != null) but.setVisibility(menu_but_finish_workout);		
		
		but = findViewById(R.id.menu_but_resetmaxrep_cur);
		if(but != null) but.setVisibility(menu_but_maxrep_current);			

//		but = this.findViewById(R.id.menu_but_wheel);
//		if(but != null) but.setVisibility(menu_but_wheel);
		
		//Music
		//check if we can play music	
		if(!Common.getCanPlayMusicSet())
		{
			boolean canStart=true;
			try
			{
				doStartMusicService();	
			}
			catch(Exception ex)
			{
				canStart=false;
			}
			if(canStart)
			{
				try
				{
					this.stopService(new Intent(this, MusicPlayerService.class));
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				Common.setCanPlayMusic(true);
			}
			else
			{
				Common.setCanPlayMusic(false);
			}
		}
		
//		if(Common.getCanPlayMusic())
//		{
//			but = this.findViewById(R.id.menu_but_sound);
//			if(but != null)
//			{
//				if(!MusicPlayerService.isInstanceCreated() || !MusicPlayerService.isPalying())
//				{
//					but.setEnabled(false);
//				}			
//			}	
//		}
//		else
//		{
//			View v = this.findViewById(R.id.cont_music);
//			if(v != null)
//			{
//				v.setVisibility(View.GONE);
//			}
//		}	
		mIsBound= false;
		showProgress(false);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {		
		outState.putInt("caller_id", caller_id);		
		super.onSaveInstanceState(outState);
	}
	
	


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	public void onButtonClick(View view) {
		switch(view.getId())
		{
		case R.id.menu_but_interview:
			showInterview();	
			break;
		case R.id.menu_but_settings:
			showSettings();
			break;
		case R.id.menu_but_resetlog:
			resetLog(); 
			break;
		case R.id.menu_but_close:
			this.setResult((needReset)? Common.RESULT_RESET : Common.RESULT_CANCEL);
			finish();
			break;
		case R.id.menu_but_resetprogr:
			resetProgram();
			break;
		case R.id.menu_but_resetmaxrep:
			resetMaxrep();
			needReset=true;
			break;
		case R.id.menu_but_resetmaxrep_cur:
			resetCurrentMaxrep();
			needReset=true;
			break;
		case R.id.menu_but_logs:
			showLogs();
			break;
//		case R.id.menu_but_facebook:
//			postToFacebook();
//			break;
		case R.id.menu_but_dellogs:
			doDeleteAllLogs();
			needReset=true;
			break;	
		case R.id.menu_but_finish_workout:
			doFinishWorkout();
			break;
//		case R.id.menu_but_wheel:
//			sowAdvancedSettings();
//			break;
		case R.id.menu_but_music:
			try{
				Intent myint = new Intent("android.intent.action.MUSIC_PLAYER");
                startActivity(myint);
			}
			catch(Exception e)
			{
				Log.d(TAG, "Cannot open music player");
			}
//			chooseMusic();
			break;
//		case R.id.menu_but_sound:
//			stopPlayingMusic();
//			break;
		default:
			super.onButtonClick(view);
			break;
		}
	}
	
//	private void stopPlayingMusic()
//	{
//		boolean result=true;
//		try
//		{
//			stopService(new Intent(this, MusicPlayerService.class));
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//			result = false;			
//		}
//		if(result)
//		{
//			View but = this.findViewById(R.id.menu_but_sound);
//			but.setEnabled(false);
//		}
//		else
//		{
//			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
//		}		
//	}
	
	private void chooseMusic()
	{
		Intent data = this.getIntent();
		data.putExtra("next", Common.ACT_PlayMusicActivity);		
		setResult(Common.RESULT_OK, data);
		finish();
	}
	
	private void postToFacebook()
	{
		Intent data = this.getIntent();
		data.putExtra("next", Common.ACT_FacebookActivity);		
		setResult(Common.RESULT_OK, data);
		finish();
	}
	
	private void showInterview()
	{
		Intent data = this.getIntent();
		data.putExtra("next", Common.ACT_InterviewActivity);		
		setResult(Common.RESULT_OK, data);
		finish();
	}
	
	private void showSettings()
	{
		Intent data = this.getIntent();
		data.putExtra("next", Common.ACT_Settings);		
		setResult(Common.RESULT_OK, data);
		finish();
	}
	
	private void sowAdvancedSettings()
	{
		Intent data = this.getIntent();
		data.putExtra("next", Common.ACT_AdvancedSettingsActivity);
		setResult(Common.RESULT_OK, data);
		finish();
	}
	
	private void showLogs()
	{
		Intent data = this.getIntent();
		data.putExtra("next", Common.ACT_LogCalendarActivity);		
		setResult(Common.RESULT_OK, data);
		finish();
	}
	
	private void doFinishWorkout()
	{
		Intent data = this.getIntent();
		this.setResult(Common.RESULT_BACK, data);
		finish();
	}
	
	private void resetLog()
	{
		//we have to figure out if we have active workout		
		//show yes/no dlg		
		this.showYesNoDlg(R.string.clear_log_msg,R.string.yes, R.string.no,DLG_REASON_RESET_CUR_LOG);
	}
	
	private void doDeleteAllLogs()
	{
		this.showYesNoDlg(R.string.del_all_logs_msg,R.string.yes, R.string.no,DLG_REASON_DELETE_ALL_LOGS);
	}
	
	private void resetProgram()
	{
		//reset current program - remove all logs and set workout to Block 1 Day 1
		this.showYesNoDlg(R.string.msg_reset_progr, R.string.yes, R.string.no,DLG_REASON_PROGR);
	}
	
	private void resetMaxrep()
	{
		this.showYesNoDlg(R.string.msg_reset_maxrep, R.string.yes, R.string.cancel_but,DLG_REASON_RESET_MAXREP);
	}
	
	private void resetCurrentMaxrep()
	{
		this.showYesNoDlg(R.string.msg_reset_maxrep_cur, R.string.yes, R.string.cancel_but,DLG_REASON_RESET_MAXREP_CUR);
	}
	
	private boolean doDeleteRecommendedWeights(final int exId)
	{
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		boolean ok =adapter.deleteRecommendedWeights(exId);		
		if(!ok) return false;		
		CurProgram progr = CurProgram.getProgram();
		progr.clearRecommended();		
		return ok;
		
	}
	
	private void processDeleteRecWeights(final boolean res)
	{
		if(!res)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
		}
	}
		
	@Override
	public void processDlgNo(final int reason) {		
			// msg to remove log entries
			//close this dlg
		super.processDlgNo(reason);
//		if(reason== DLG_REASON_RESET_CUR_LOG)
//		{			
//			//nothing
//		}
//		else if(reason == DLG_REASON_DELETE_ALL_LOGS)
//		{			
//			//nothing
//		}
//		else if(reason == DLG_REASON_RESET_MAXREP)
//		{
//			//nothing
//		}
		
	}

	@Override
	public void processDlgYes(final int reason) {	
		super.processDlgYes(reason);
		if(reason== DLG_REASON_RESET_CUR_LOG)
		{			
			new DeleteLogTask().execute();	
		}
		else if(reason== DLG_REASON_PROGR)
		{		
			//reset progr
			new ResetProgrTask().execute();
		}
		else if(reason == DLG_REASON_DELETE_ALL_LOGS)
		{
			new DeleteAllLogsTask().execute();
		}
		else if(reason == DLG_REASON_RESET_MAXREP)
		{
			new DeleteRecommendedWeightsTask().execute(new Integer[]{});
		}
		else if(reason==DLG_REASON_RESET_MAXREP_CUR)
		{
			CurProgram progr=CurProgram.getProgram();
			Workout workout = progr.getWorkout();
			if(workout != null)
			{
				final int exId=workout.getCurExId();
				new DeleteRecommendedWeightsTask().execute(new Integer[]{Integer.valueOf(exId)});
			}			
		}
		
	}
	
	private boolean resetProgrInDb()
	{
		CurProgram progr = CurProgram.getProgram();
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		boolean ok = adapter.deleteAllProgrLogs(progr.getId(), User.getUser().getId());
		
		if(ok)
		{
			DayInfo dayInfo = new DayInfo();
			dayInfo.progrId = progr.getId();
			ok = adapter.getFirstProgrDayInfo(dayInfo);
			if(ok)
			{
				DataStore ds = new DataStore(this);
				ds.setCurWeekId(dayInfo.weekId);
				ds.setCurDayId(dayInfo.dayId);
				ds.setCurDayOrder(1);
				progr.resetProgram();
			}
		}
		return ok;
	}
	
	private void processResetProgrResult(final boolean res)
	{
		if(!res)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
		}
		else
		{
			Common.showToast(this.getString(R.string.msg_progr_reset_ok), this);	
		}
	}
	
	//delete log for current workout - with workout ID for today
	private boolean deleteLogs()
	{
		CurProgram progr = CurProgram.getProgram();
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		
		Workout workout = progr.getWorkout();
		if(workout == null) return false;
		
		final boolean ok = adapter.deleteLogForWorkoutOnDate(User.getUser().getId(), workout.getId(), 
				progr.getId(), workout.getMode(), Common.getDateTimeForToday());		
		
		if(ok)
		{
			CurProgram.getProgram().resetAllLogs();
		}
		return ok;
		
	}
	
	private boolean deleteAllLogs()
	{
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		if(!adapter.deleteAllLogs()) return false;
		
		//clear complete exercise statuses from current workout
		CurProgram.getProgram().resetAllLogs();
		return true;
	}
	
	//we deleted current log
	public void processDelLogResult(final boolean res)
	{
		if(!res)
		{
			this.showErrorDlg(R.string.log_del_witherrors, Common.REASON_NOT_IMPORTANT);
		}
		else{				
			this.showMsgDlg(R.string.logs_removed, DLG_REASON_RESET_CUR_LOG);			
						
		}
	}
	
	
	
	@Override
	public void processMsgDlgClosed(final int reason) {
		super.processMsgDlgClosed(reason);
		if(reason == DLG_REASON_RESET_CUR_LOG)
		{
			this.setResult(Common.RESULT_RESET);
			finish();
		}
		
	}



	private void processDelAllLogsResult(final boolean res)
	{
		if(!res)
		{
			this.showErrorDlg(R.string.logs_del_witherrors, Common.REASON_NOT_IMPORTANT);
		}
		else{			
			Common.showToast(this.getString(R.string.logs_deleted), this);			
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case Common.ACT_InterviewActivity:
		case Common.ACT_Settings:
			this.setResult(Common.RESULT_OK);
			finish();
			return;	
		default: super.onActivityResult(requestCode, resultCode, data);
		break;
		}
		
	}

	@Override
	public int getId() {		
		return Common.ACT_MenuActivity;
	}
	
	private void showProgress(final boolean show)
	{
		View progrView = this.findViewById(R.id.progrBar);
		if(progrView != null)
		{
			if(show) progrView.setVisibility(View.VISIBLE);
			else progrView.setVisibility(View.GONE);
		}
	}

	
	//delete current log
	private class DeleteLogTask extends AsyncTask<String, Void, Boolean>
	{
		// can use UI thread here
		 protected void onPreExecute() { 
			showProgress(true);
		  }  

		@Override
		protected Boolean doInBackground(String... arg0) {
			boolean ok = deleteLogs();
			return (ok)? Boolean.TRUE : Boolean.FALSE;
		}
		
		protected void onPostExecute(Boolean result) 
		{
			showProgress(false);
			MenuActivity.this.processDelLogResult((result==Boolean.TRUE)? true : false);
		}
		
	}
	
	private class DeleteAllLogsTask extends AsyncTask<Void, Void, Boolean>
	{
		// can use UI thread here
		 protected void onPreExecute() { 
			showProgress(true);
		  }  

		
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			return (deleteAllLogs())? Boolean.TRUE : Boolean.FALSE;
		}



		protected void onPostExecute(Boolean result) 
		{
			showProgress(false);
			MenuActivity.this.processDelAllLogsResult((result==Boolean.TRUE)? true : false);
		}
		
	}
	
	private class ResetProgrTask extends AsyncTask<String, Void, long[]>
	{
		// can use UI thread here
		 protected void onPreExecute() { 
			showProgress(true);
		  }  

		@Override
		protected long[] doInBackground(String... arg0) {
			boolean ok = resetProgrInDb();
			long[] res = new long[1];
			res[0] =(ok)? 1 : 0;
			return res;
		}
		
		protected void onPostExecute(final long[] result) 
		{
			showProgress(false);
			MenuActivity.this.processResetProgrResult((result[0]==1)? true : false);
		}
		
	}
	
	private class DeleteRecommendedWeightsTask extends AsyncTask<Integer, Void, Boolean>
	{
		// can use UI thread here
		 protected void onPreExecute() { 
			showProgress(true);
	 }  	
		
		@Override
		protected Boolean doInBackground(Integer... args) {
			final int exId=(args!=null && args.length>0)? args[0].intValue() : -1;
			return (doDeleteRecommendedWeights(exId))? Boolean.TRUE : Boolean.FALSE;
			 
		}

		protected void onPostExecute(final Boolean result) 
		{
			showProgress(false);
			MenuActivity.this.processDeleteRecWeights((result != null)? result.booleanValue() : false);
		}	
		
		
	}

}
