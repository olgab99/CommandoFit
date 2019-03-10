package com.olmatech.fitness.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.Workout;
import com.olmatech.fitness.main.Workout.WorkoutMode;

public abstract class BaseLoadWorkoutFragmentActivity extends BaseFragmentActivity{
	//private final static String TAG ="FirstActivityPhone";
	//protected int erroDlgAction= -1;
	protected CurProgram progr;
	protected WorkoutMode startMode = WorkoutMode.Unknown;
	
	protected WorkoutMode workoutToStartMode = WorkoutMode.Unknown;  //mode of workout we wont to do - can be Cardio
	
	protected final static int DLG_REASON_WORKOUT=2;
	protected final static int DLG_REASON_SET_DAY_CURRENT=3;
	protected final static int DLG_REASON_PROGR_INIT_DONE=4;
	
	//protected int dlgReason=0;
	
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		progr = CurProgram.getProgram();
	}

	abstract protected void initActivity();
	abstract protected void progrInitProcessDone();
		
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	//We choose not to continue with current logs - delete them
	@Override
	public void processDlgNo(final int reason) {
		if(reason == DLG_REASON_PROGR_INIT_DONE)
		{
			//start "clean" workout for today - clear log data if any		
			new DeleteLogTask().execute();
		}
		super.processDlgNo(reason);		
	}

	@Override
	public void processDlgYes(final int reason) {
		super.processDlgYes(reason);
		if(reason == DLG_REASON_PROGR_INIT_DONE)
		{
			//read the logs and set Complete data on task
			if(startMode == WorkoutMode.All) new SetWorkoutLogDataTask().execute();
			else
			{
				//for module - load logs
				new SetWorkoutLogDataTask().execute();
			}
		}			
	}

		/////////// Init program for ALL Button //////////////////////
	//init based on current dayOfMonth and week for our workout and get this particular workout
		private void initProgramForDay(boolean[] result)
		{			
			DataStore ds = new DataStore(this);
			int progrId = ds.getCurProgramId();
			final int weekId = ds.getCurWeekId();
			final int dayId = ds.getCurDayId();
			final int dayOrder = ds.getCurDayOrder();
			final int weekOrder = ds.getCurWeekOrder();		
			
			//For now - we have 1 program only
			boolean forWeekDayId=true;
			if(progrId <0)
			{
				//new program
				progrId=1;
				ds.setCurProgrId(1);
				
				progr.setId(progrId);
				progr.setDayOrder(1);
				progr.setWeekOrder(1);
				ds.setCurDayOrder(1);
				ds.setCurWeekOrder(1);
				forWeekDayId = false;
			}
			else
			{
				if(weekId >0 && dayId >0){
					progr.setWeek(weekId);
					progr.setDay(dayId);
				}
				else if(weekOrder >0 && dayOrder>0){
					progr.setDayOrder(dayOrder);
					progr.setWeekOrder(weekOrder);
					forWeekDayId = false;
				}
				else
				{
					//invalid data - can't init program?
					result[0] = false;
					return;
				}				
			}				
			
			DBAdapter dbAdap = DBAdapter.getAdapter(this.getApplicationContext());
			result[0] = dbAdap.getProgramFromDb(progr,forWeekDayId);	 //we are getting program based on Week / Day and setting Workout Id and mode for this dayOfMonth	
			//checking for today's logs - we are initing program 
			if(!result[0]) return;
			
			//now get workout
			Workout workout = progr.getWorkout();
			result[0] = dbAdap.getWorkoutForDay(progr.getDayInfo(), workout);
			
			WorkoutMode md = progr.getCurWorkoutMode();
			if(md == WorkoutMode.Tactical)
			{
				result[1] = (startMode != WorkoutMode.All)? 
						dbAdap.checkIfHaveTactLogs(User.getUser().getId(), progr.getId(), progr.getCurWorkoutId(), 
						Common.getDateTimeForToday(), progr.gettWeek(), progr.getDayOrder()) :
					dbAdap.checkIfHaveTactLogs(User.getUser().getId(), progr.getId(), progr.getCurWorkoutId(), 
									Common.getDateTimeForToday(), 0,0);
							
			}
			else
			{
				result[1] = dbAdap.checkIfHaveLogsForDay(User.getUser().getId(), progr.getDayInfo(), Common.getDateTimeForToday());
			}				
		}
		
		
		//////////////////////////////////////////////////////////////////////////////////////////
		//for init All prpgram
		private void processProgrInitDone(final boolean res, final boolean haveLogsForToday)
		{
			if(res)
			{
				//check if we have log for today - and ask the user start a new  workout or continue
				if(haveLogsForToday)
				{
					this.showYesNoDlg(R.string.msg_logs, R.string.yes, R.string.no, DLG_REASON_PROGR_INIT_DONE);
					return;
				}
							
				//do it after init		
				progrInitProcessDone();
			}
			else
			{
				//error
				this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
			}
			
		}
		
		/////////////////////// DELETING LOGS //////////////////////////////////
		////delete log for current workout - with workout ID for today - we will do it again
		protected boolean deleteLogsForToday()
		{
			DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());		
			Workout workout = progr.getWorkout();
			if(workout == null) return false;
			
			DayInfo dayInfo=  progr.getDayInfo();
			
			final boolean ok = adapter.deleteLogForWorkoutOnDate(User.getUser().getId(), dayInfo.workoutId, 
					dayInfo.progrId, dayInfo.workoutMode, Common.getDateTimeForToday());		
			
			if(ok)
			{
				progr.resetAllLogs();
			}
			return ok;
		}
		
		protected void processDeleteLogsResult(final boolean res)
		{
			if(res)
			{
				progrInitProcessDone();
			}
			else
			{
				
				this.showErrorDlg(R.string.err_processing, Common.ACT_MainActivityPhone);
			}
		}
		
		//////////////// LOADING WORKOUT FOR MODE //////////////////////////////
		//PRE: Id must be set
		//ret null ot haveLogs
		private Boolean getWorkoutForMode(final WorkoutMode md)
		{
			final int wkId = progr.getCurWorkoutId();
			final int curExIndex = progr.getCurExerciseInd();
			if(wkId <0) return null;
			
			progr.setCurWorkoutId(wkId, true);
			
			DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
			
			Workout workout = progr.getWorkout();
			if(workout == null) return null;			
			
			workout.setMode(md);
			workout.setCurIndex(curExIndex, false);
			final long dtToday = Common.getDateTimeForToday();
			if(!adapter.getWorkoutForMode(workout, wkId, md, dtToday)) return null;  //CHANGE
			
			boolean haveLogs = false;
			if(md == WorkoutMode.Tactical)
			{
				haveLogs = adapter.checkIfHaveTactLogs(User.getUser().getId(), progr.getId(), wkId, 
						dtToday, 0, 0);
			}
			else if(md == WorkoutMode.Cardio)
			{
				haveLogs = false;
			}
			else
			{
				haveLogs = adapter.checkIfHaveLogsForModule(User.getUser().getId(), progr.getId(), dtToday,
						wkId, md);//CHANGE
			}
			
			return (haveLogs)? Boolean.TRUE : Boolean.FALSE;
			
		}
		
		protected void processWorkoutLoadForModeDone(final boolean res, final boolean haveLogs){}
		
		////////////////////// SETTING WORKOUT  LOG DATA////////////////////////
		private boolean setWorkoutCompleteData()
		{
			DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
			
			Workout workout = progr.getWorkout();
			if(workout == null) return false;
			WorkoutMode md = progr.getCurWorkoutMode();
			User user = User.getUser();
			if(md == WorkoutMode.Tactical)
			{
				return adapter.setLapsForTackticalWorkout(user.getId(), progr.getId(), workout, Common.getDateTimeForToday());
			}
			else if(md == WorkoutMode.Cardio)
			{
				return true;
			}
			else 
			{
				return adapter.setCompleteStatusForWorkout(user.getId(), progr.getDayInfo(), workout, 
						Common.getDateTimeForToday(),user.getGender(), startMode);
			}		
		}
		
		private void processSetWorkoutResult(final boolean res)
		{
			if(res)
			{
				progrInitProcessDone();
			}
			else
			{
				this.showErrorDlg(R.string.err_processing, Common.ACT_MainActivityPhone);
			}
		}
		
		///////////////// ---------- FOR TACTICAL ------------------
//		protected boolean readWorkoutsByMode(final WorkoutMode md)
//		{
//			DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
//			List<WorkoutInfo> list = adapter.getWorkoutsInfoByMode(md);
//			if(list == null)
//			{
//				return false;
//			}
//			
//			return true;
//			
//		}

	/////////////////// TASKS //////////////////////////////
		protected class ProgramInitTask extends AsyncTask<String, Void, long[]>
		{
			private ProgressDialog dialog = new ProgressDialog(BaseLoadWorkoutFragmentActivity.this);   		
	  		
	  		// can use UI thread here
	 		 protected void onPreExecute() { 
	 			 this.dialog.setMessage(getString(R.string.processing));  
	 			 this.dialog.show();  
	 		  }  

			@Override
			protected long[] doInBackground(String... params) {
				
				boolean[] result = new boolean[2];
				initProgramForDay(result);
				
				long[] res = new long[2];
				res[0] = (result[0])? 1 : 0;  //result
				res[1] = (result[1])? 1 : 0; //true if have log for today
				
				return res;
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
		        	  BaseLoadWorkoutFragmentActivity.this.processProgrInitDone((result[0] == 1)? true : false, (result[1] == 1)? true : false);
		          }	          
		        
			}     		
			
		}
		
		///////////////////////// DELETE LOGS //////////////////////
		protected class DeleteLogTask extends AsyncTask<String, Void, long[]>
		{
			private ProgressDialog dialog = new ProgressDialog(BaseLoadWorkoutFragmentActivity.this);  
			// can use UI thread here
			 protected void onPreExecute() { 
				 this.dialog.setMessage(getString(R.string.processing));  
	 			 this.dialog.show();  
			  }  

			@Override
			protected long[] doInBackground(String... arg0) {
				boolean ok = deleteLogsForToday();
				long[] res = new long[1];
				res[0] =(ok)? 1 : 0;
				return res;
			}
			
			protected void onPostExecute(final long[] result) 
			{
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
				BaseLoadWorkoutFragmentActivity.this.processDeleteLogsResult((result[0]==1)? true : false);
			}
			
		}
		
		////////////////// LOAD WORKOUT //////////////////////
		protected class LoadWorkoutForModeTask extends AsyncTask<Void, Void, Boolean[]>
		{
			private ProgressDialog dialog = new ProgressDialog(BaseLoadWorkoutFragmentActivity.this);  
			// can use UI thread here
			 protected void onPreExecute() { 
				 this.dialog.setMessage(getString(R.string.processing));  
	 			 this.dialog.show();  
			  }
			 
			
			 
			@Override
			protected Boolean[] doInBackground(Void... params) {	
				
				Boolean[] ok = new Boolean[1];
				WorkoutMode md = progr.getCurWorkoutMode();
				ok[0] = getWorkoutForMode(md);  //CHANGE
				
				return ok;
			}
			protected void onPostExecute(final Boolean[] result) 
			{
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
				boolean [] res = new boolean[2];
				if(result == null || result.length == 0)
				{
					res[0] =false;
					res[1] = false;					
				}
				else
				{
					res[0] =true;
					res[1] = result[0].booleanValue();
					
				}
				BaseLoadWorkoutFragmentActivity.this.processWorkoutLoadForModeDone(res[0], res[1]);
			}

		
			
		}
		
		/////////////////////// SET LOG DATA ////////////////////////////////
		protected class SetWorkoutLogDataTask extends AsyncTask<String, Void, long[]>
		{
			private ProgressDialog dialog = new ProgressDialog(BaseLoadWorkoutFragmentActivity.this);  
			// can use UI thread here
			 protected void onPreExecute() { 
				 this.dialog.setMessage(getString(R.string.processing));  
	 			 this.dialog.show();  
			  }  

			@Override
			protected long[] doInBackground(String... arg0) {
				boolean ok = setWorkoutCompleteData();
				long[] res = new long[1];
				res[0] =(ok)? 1 : 0;
				return res;
			}
			
			protected void onPostExecute(final long[] result) 
			{
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
				BaseLoadWorkoutFragmentActivity.this.processSetWorkoutResult((result[0]==1)? true : false);
			}
			
		}
		
/////////////////////// CHANGE CURRENT DAY - DELETE ALL LOGS AFTER and On ////////////////////////////////
	
		//delete logs following this day in the program
 protected boolean deleteLogsOnAndAfter(final DayInfo dayInfo)
 {
	 DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
	 
	 long date_time = adapter.getDatetimeDayWasLastCompleted(progr.getId(), dayInfo.weekId, dayInfo.dayOrder);
	 
	 if(date_time == -1) return false;
	 if(date_time == 0) return true;
	 
	 return adapter.deleteLogsOnAfterDate(User.getUser().getId(), progr.getId(), date_time, dayInfo.weekId, dayInfo.dayOrder);
	  
 }
 
 protected void processDeleteLogsOnAndAfterResult(final boolean res){}
		
protected class DeleteLogsOnAndAfterTask extends AsyncTask<DayInfo, Void, Boolean>
{
	private ProgressDialog dialog = new ProgressDialog(BaseLoadWorkoutFragmentActivity.this);  
	// can use UI thread here
	protected void onPreExecute() { 
		 this.dialog.setMessage(getString(R.string.processing));  
		 this.dialog.show();  
	}  
	
	//send - 
	@Override
	protected Boolean doInBackground(DayInfo... params) {
		if(params == null || params.length == 0) return Boolean.FALSE;
		
		return (deleteLogsOnAndAfter(params[0]))? Boolean.TRUE : Boolean.FALSE;
	}
	
	protected void onPostExecute(final Boolean result) 
	{
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
		BaseLoadWorkoutFragmentActivity.this.processDeleteLogsOnAndAfterResult((result == Boolean.TRUE)? true : false);
	}

	

}
		
		
}
