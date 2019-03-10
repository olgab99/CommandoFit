package com.olmatech.fitness.ui;

//activity with Lists of Blocks / days and Excersises

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.interfaces.IProcessLoaded;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.main.WorkoutLog;
import com.olmatech.fitness.view.ExListFragment;
import com.olmatech.fitness.view.WkoutListFragment;

public class MainActivityPhone extends BaseLoadWorkoutFragmentActivity implements IProcessLoaded{
	
	final String TAG ="MainActivityPhone";    
		
	private ExListFragment frExList;
	private WkoutListFragment frWkList;	
	
	//to display start workout from program like just workout
	
	private int selectedWorkoutId=-1;
	private WorkoutMode selectedWorkoutMode = WorkoutMode.Unknown;
	String selectedWorkoutTitle;
	
	//temp data to set day current
	private DayInfo tempDayInfo=null;
	
	@Override
	protected void onCreate(Bundle b) {		
		super.onCreate(b);
		
		setContentView(R.layout.activity_wk_ex_list);
		//get init mode data
		int modeId;
		if(b == null)
		{
			Intent intent = this.getIntent();
			modeId = intent.getIntExtra("mode",-1);
			if(modeId >=0)
			{
				startMode = WorkoutMode.getModeFromId(modeId);
			}
		}
		else
		{
			if(b.containsKey("mode"))
			{
				startMode = WorkoutMode.getModeFromId(b.getInt("mode"));
			}
		}
		
		if(startMode == WorkoutMode.Unknown)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_FATAL_ERROR);
			return;
		}	
		
		
		frExList = (ExListFragment) getSupportFragmentManager().findFragmentById(R.id.exListFragment);
		frExList.setFragmentId(Common.ID_EX_FRRAGMENT);
		frWkList = (WkoutListFragment)getSupportFragmentManager().findFragmentById(R.id.wkListFragment);
		frWkList.setFragmentId(Common.ID_WK_FRAGMENT);
		frWkList.setViewMode(startMode);
		
		initActivity();
	}

	@Override
	protected void initActivity() {
		//init based on startMode
		int tlt = getTitleResource();
		TextView tvTitle = (TextView)this.findViewById(R.id.txtTitle);
		tvTitle.setText(tlt);
		switch(startMode)
		{
		case All:
			frWkList.setShowStartButton(false);
			frWkList.setShowContentMenu(true);
			new ProgramInitTask().execute();
			break;
		case Tactical:		
		case Endurance:		
		case Bodybuilding:
			frWkList.setShowStartButton(true);
			frWkList.setShowContentMenu(false);
			frExList.setShowContentMenu(false);
			frExList.setDoItemClick(true);
			frWkList.fillData();
			showProgress(false);
			break;
		default:
			this.showErrorDlg(R.string.err_invalid_mode, Common.REASON_FATAL_ERROR);
			break;
		}	
	}
	
	private int getTitleResource()
	{
		switch(startMode)
		{
		case All: return R.string.title_all;
		case Tactical:	return R.string.title_tact;	
		case Endurance:	return R.string.title_endur;	
		case Bodybuilding: return R.string.title_mass;
		default: return R.string.title_generic;
		}
	}
	
	//we are ready to display data - this called for All mode only
	@Override
	protected void progrInitProcessDone() {
		switch(startMode)
		{
		case All:
			displayAll();
			break;
		case Tactical:			
		case Endurance:		
		case Bodybuilding:	
			//we were initializing logs for current module with logs
			startWorkoutForModule();
			break;
		default:
			this.showErrorDlg(R.string.err_invalid_mode, Common.REASON_FATAL_ERROR);
			break;
		}
		
	}
	
	private void displayAll()
	{				
		frExList.setShowContentMenu(true);
		frExList.setDoItemClick(true);
		//frExList.setImageDirectory(progr.getProgrDir());
		//frExList.setUserGender(progr.getGender());		
		frExList.setListDayInfo(progr.getId(), progr.getCurWorkoutId(), progr.getCurExerciseInd(), 
				progr.gettWeek(), progr.getDay(), this.startMode);
		frExList.fillData();
		
		frWkList.setProgrId(progr.getId());
		//frWkList.setCurrentDayId(progr.getDay());	
		frWkList.setCurDayOrder(progr.getDayOrder());
		//frWkList.setCurWeekId(progr.gettWeek());
		frWkList.setCurWeekOrder(progr.getWeekOrder());
		frWkList.setShowCompleteTick(true);
		frWkList.fillData();		
		
		showProgress(false);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("mode", startMode.getId());
		super.onSaveInstanceState(outState);
	}

	////////// init activity process
	@Override
	public  void processDlgNo(final int reason) {
		if(reason == DLG_REASON_WORKOUT || reason == DLG_REASON_SET_DAY_CURRENT)
		{
			//do nothing
		}
		else
		{
			super.processDlgNo(reason);
		}
		
	}


	@Override
	public void processDlgYes(final int reason) {
		if(reason == DLG_REASON_WORKOUT)
		{
			//workoutToStartMode = selectedWorkoutMode;  //we setting mode to Bodybuilding, Endurance or Cardio or Tacktical
			//show workout like separate workout based on temp data
			processWorkoutForModeStart(selectedWorkoutId, selectedWorkoutTitle, selectedWorkoutMode,0);
			selectedWorkoutId=-1;
			selectedWorkoutTitle=null;
			selectedWorkoutMode = WorkoutMode.Unknown;
		}
		else if(reason == DLG_REASON_SET_DAY_CURRENT)
		{
			setDayCurrent();
			
		}
		
		super.processDlgYes(reason);		
	}
	
	
	
	@Override
	public void processErrDlgClose(final int reason) {
		if(reason == Common.ACT_MainActivityPhone)
		{
			progrInitProcessDone();
		}
		super.processErrDlgClose(reason);
	}	
	
	//////////// end init activiry process
	


//	private void startWorkout() {
//		//show Workout Activity
//		Intent intent = new Intent(this, ExerciseActivityPhone.class);
//		Bundle b = new Bundle();
//		
//		b.putInt("workout", progr.getCurWorkoutId());
//		b.putInt("week", progr.gettWeek());
//		b.putInt("dayOfMonth", progr.getDay());
//		b.putInt("ex_index", progr.getCurExerciseInd()); //index in list
//		WorkoutMode md = WorkoutMode.All;
//		b.putInt("mode", md.getId());
//		
//		intent.putExtras(b);
//		
//		this.startActivityForResult(intent, Common.ACT_ExerciseActivityPhone);
//		
//	}

	
	
	private void showProgress(final boolean show)
	{
		View progrView = this.findViewById(R.id.progrBar);
		if(progrView != null)
		{
			if(show) progrView.setVisibility(View.VISIBLE);
			else progrView.setVisibility(View.GONE);
		}
	}
	
//	private void displayLeftPanel()
//	{
//		ExListFragment fragment = (ExListFragment) getSupportFragmentManager().findFragmentById(R.id.exListFragment);	
//		fragment.setShowContentMenu(true);
//		fragment.setFullScreenMode(false);
//		showListsDevider(true);
//		fragment.setListDayInfo(progr.getId(), progr.getCurWorkoutId(), progr.getCurExerciseInd(), progr.gettWeek(), progr.getDay());
//		fragment.fillData();
//		
//		//layout
//		View exView = fragment.getView();
//		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) exView.getLayoutParams();
//		params.weight = 0.65f;
//		exView.setLayoutParams(params);		
//		
//		WkoutListFragment fragmWk = (WkoutListFragment)getSupportFragmentManager().findFragmentById(R.id.wkListFragment);
//		if(fragmWk != null && fragmWk.isHidden())
//		{
//			FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
//			tr.show(fragmWk);
//			tr.commit();
//		}
//	}

	//the user selected to see workout for another dayOfMonth
	
	@Override
	public void processShowDayWorkout(int workoutId, final int weekIndex, final int dayIndex, 
			String title, WorkoutMode md) {  
		showProgress(true);
		//Log.d(TAG, "processShowDayWorkout workoutId"+ workoutId);
		Intent intent = new Intent(this, ExListActivity.class);
		Bundle b = new Bundle();
		b.putInt("progr_id", progr.getId());
		b.putInt("wk_id", workoutId);
		b.putInt("week_index", weekIndex);
		b.putInt("day_index", dayIndex);
		b.putString("title", title);
		b.putInt("mode", md.getId());
		intent.putExtras(b);
		
		this.startActivityForResult(intent, Common.ACT_ExListActivity);			
	}
	
	/**
	 * Do day worlkout oytside of all program
	 */
	@Override
	public void processDoDayWorkout(int workoutId, String title, WorkoutMode md) {
		// ask the user to start Workout just as workout, not part of program
		//save temp data
		selectedWorkoutId = workoutId;
		selectedWorkoutMode = md;
		selectedWorkoutTitle = title;
		
		this.showYesNoDlg(R.string.msg_start_workout, R.string.start, R.string.cancel_but, DLG_REASON_WORKOUT);		
	}
		

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		showProgress(false);		
		switch(requestCode)
		{
		case Common.ACT_ExListActivity:
			break;
		case Common.ACT_ExerciseActivityPhone:
			//see if we are just going Back or finished out workout (and need to go to the Menu screen)
			if(resultCode == Common.RESULT_OK)
			{
				//display current workout as it was with new cur. exercise
				Bundle b = data.getExtras();
				 if(b != null)
				 {
					 
				 }
			}
			else if(resultCode == Common.RESULT_ERROR)
			{
				this.showErrorDlg(R.string.err_reading_workout, Common.REASON_FATAL_ERROR);
				return;
			}
			break;
		default: super.onActivityResult(requestCode, resultCode, data);
		break;
		}
		
		 
	}

	
	
	@Override
	public int getId() {		
		return Common.ACT_MainActivityPhone;
	}
	
	//// FRAGMENTS INTERFACE 
	@Override
	public void onFragmentLoadFinished(final int id) {		
		if(id==Common.ID_WK_FRAGMENT)
		{
			//load exercise list based on mode
			switch(startMode)
			{
			case All: 
				 DataStore ds = new DataStore(this);
				final int selWkItem = ds.getAllWorkoutSelectedListItem();
				if(selWkItem > 3)  //3 item always will fit
				{
					this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							if(frWkList !=null)
		            		{
		            			frWkList.scrollListToSelected(selWkItem);
		            		}							
						}
						
					});
				}				
				return;	
			case Tactical:			
			case Endurance:		
			case Bodybuilding:			
			//get selecetd workout id
				final int workoutId =this.frWkList.getCurSelWorkoutId();				
				frExList.setListDayShortInfo(progr.getId(), workoutId, startMode);				
				if(workoutId >0)
				{
					frExList.fillData();
				}
				break;
			default:
				
				break;
			}			
		}
		//else if(id==ID_EX_FRRAGMENT) is_ex_loaded=true;
		
//		if(is_ex_loaded && is_wk_loaded)
//		{
//			this.runOnUiThread(new Runnable(){
//
//				@Override
//				public void run() {
//					//showProgress(false);					
//				}				
//			});			
//		}
		
	}
	
	@Override
	public void onListSelectedItemIdSet(int selecetdId, int gragmentId) {
		if(selecetdId <=0)
		{
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					showErrorDlg(R.string.err_processing, Common.REASON_FATAL_ERROR);					
				}
				
			});
			return;
		}
		if(frExList.getWorkoutId() <=0)
		{
			frExList.setListDayShortInfo(progr.getId(), selecetdId, startMode);
			progr.setCurWorkoutId(selecetdId, true);
			frExList.fillData();
		}
		
	}

	
	@Override
	public void onExerciseListItemClick(Object tag, int fragmentId) {
		switch(startMode)
		{
		case All:
			processExListItemClickAll(tag);		
			break;
		case Tactical:			
		case Endurance:		
		case Bodybuilding:			
			int exIndex;
			if(tag == null) exIndex=0;
			else
			{					
				try
				{
					exIndex = ((Integer)tag).intValue()-1;
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					exIndex =0;
				}
			}			
			processWorkoutForModeStart(frExList.getWorkoutId(), "Workout - " + startMode.getTitle(), startMode,exIndex);
			break;
		default:
			
			break;
		}
		
	}
	
	private void processExListItemClickAll(final Object tag)
	{
		int exOrder;
		try
		{
			Integer intVal = (Integer)tag;
			exOrder = intVal.intValue();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			exOrder = -1;
		}
		if(exOrder <=0)
		{
			this.showErrorDlg(R.string.err_reading_data, Common.REASON_NOT_IMPORTANT);
			return;
		}
		progr.setCurExerciseIndex(exOrder-1, true);
				
		Intent intent = this.getIntent();		
		intent.putExtra("progr_mode", startMode.getId());
		final int wkModeId = progr.getWorkout().getMode().getId();
		intent.putExtra("wk_mode", wkModeId);
		setResult(Common.RESULT_OK, intent);
		finish();		
	}

	/**
	 * Click START button for workout - Mass or Endurance or Tacktical (no Cardio)
	 */
	@Override
	public void onStartButtonClick(int gragmentId, final Object data) {
		if(gragmentId==Common.ID_WK_FRAGMENT)
		{
			if(data != null)
			{
				int wkId;
				try
				{
					wkId = ((Integer)data).intValue();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					return;
				}				
				//start Workout Activity based on mode
				processWorkoutForModeStart(wkId, "Workout - " + startMode.getTitle(), startMode,0);			
				
			}
		}		
	}

	@Override
	public void onViewButtonClick(int gragmentId, Object data) {
		if(gragmentId==Common.ID_WK_FRAGMENT)
		{
			if(data != null)
			{
				int wkId;
				try
				{
					wkId = ((Integer)data).intValue();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					return;
				}
				//start Workout Activity based on mode
				
				switch(startMode)
				{
				case Tactical:						
				case Endurance:		
				case Bodybuilding:
					//load workout and finish activity
					//we will display activity based on workout mode from Main Activity
					// list item click-selected					
					frExList.setWorkoutId(wkId);
					frExList.fillData();					
					this.frWkList.setCurSelWorkoutId(wkId);
					break;
				default:
					
					break;
				}
				
			}
		}
		
				
		
	}	
	/**
	 * 
	 * @param wkId
	 * @param title
	 * @param md  - mode of workout - can be Cardio
	 * @param exIndex
	 */
	private void processWorkoutForModeStart(final int wkId, final String title, final WorkoutMode md, final int exIndex)
	{		
		int exerIndex = (md==WorkoutMode.Tactical)? 0 : exIndex;
		switch(md)
		{
		case Tactical:						
		case Endurance:		
		case Bodybuilding:
		case Cardio:
			//load workout and finish activity
			//we will display activity based on workout mode from Main Activity
			progr.setCurWorkoutId(wkId, true);	
			progr.setCurWorkoutMode(md);
			progr.setCurExerciseIndex(exerIndex, false);				
			
			new LoadWorkoutForModeTask().execute();
			break;
		default:
			
			break;
		}
		
	}
	
	
	
	@Override
	protected void processWorkoutLoadForModeDone(boolean res, final boolean haveLogs) {
		if(!res)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
			return;
		}
		
		if(haveLogs)
		{
			this.showYesNoDlg(R.string.msg_logs, R.string.yes, R.string.no, DLG_REASON_PROGR_INIT_DONE);
		}
		else
		{
			startWorkoutForModule();
		}	
	}
	
	//starting for Endurance or Mass
	private void startWorkoutForModule()
	{
		Intent intent = this.getIntent();		
		intent.putExtra("progr_mode", startMode.getId());
		final int wkModeId = progr.getWorkout().getMode().getId();
		intent.putExtra("wk_mode", wkModeId);
		setResult(Common.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onSetComplete(Object tag, boolean isComplete, int fragmentId) {
		if(fragmentId==Common.ID_EX_FRRAGMENT)
		{
			if(tag == null) 
			{
				//show error dialog
				showErrorDlg(R.string.err_reading_workout, Common.REASON_NOT_IMPORTANT);				
				return;
			}
			new SetExerciseCompleteTask().execute(tag.toString(), (!isComplete)? "t" : "f"); ////opposite
		}	
	}
	
	private boolean setExerciseComplete(final int entryId, final boolean isComplete)
	{
		DBAdapter dbAdapter = DBAdapter.getAdapter(this.getApplicationContext());
		
		//progr.getId(), progr.getCurWorkoutId(), progr.getCurExerciseInd(), 
		//progr.gettWeek(), progr.getDay(), this.startMode
			
		//tag == entry_id
		final WorkoutLog wkLog = new WorkoutLog(progr.getCurWorkoutId(), progr.getId(), progr.gettWeek(), 
				progr.getDay(), isComplete);
		wkLog.setCompleted(isComplete); //opposite
				
		wkLog.setEntryAndExerciseId(entryId, -1);
		
		final boolean ok = dbAdapter.setExComplete(wkLog); //, dataReloadHandler);
		if(ok){
			processDataReload();
			progr.getWorkout().setExCompletedById(entryId, isComplete);
		}
		return ok;
	}
	
	private void processSetCompleteResult(final boolean res)
	{
		if(!res)
		{
			//show error dialog
			showErrorDlg(R.string.err_reading_workout, Common.REASON_NOT_IMPORTANT);				
		}
	}
	
	
//	final Handler dataReloadHandler = new Handler()
//	{
//		@Override
//		public void handleMessage(Message msg) {
//			processDataReload();
//		}
//		
//	};
	
	private void processDataReload()
	{
		runOnUiThread(new Runnable(){
			@Override
			public void run() {				
				//restart loader
				frExList.reloadData();
			}
	    	   
	       });
    }

		
	
//	private void showListsDevider(final boolean show)
//	{
//		View v = findViewById(R.id.listsDevider);
//		if(v == null) return;
//		if(show) v.setVisibility(View.VISIBLE);
//		else v.setVisibility(View.INVISIBLE);
//	}
	
///////////////////////////////TASKS ///////////////////////////////////////
	protected class SetExerciseCompleteTask extends AsyncTask<String, Void, long[]>
	{
		private ProgressDialog dialog = new ProgressDialog(MainActivityPhone.this);  
		// can use UI thread here
		 protected void onPreExecute() { 
			 this.dialog.setMessage(getString(R.string.processing));  
 			 this.dialog.show();  
		  }  

		@Override
		protected long[] doInBackground(String... args) {
			int entryId;
			boolean isComplete;
			try
			{
				entryId = Integer.parseInt(args[0]);
				isComplete = (args[1].startsWith("t"))? true : false;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			
			boolean ok = setExerciseComplete(entryId, isComplete);
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
			MainActivityPhone.this.processSetCompleteResult((result != null && result[0]==1)? true : false);
		}
		
	}



	@Override
	public void onSelectedItem(int selItem) {
		DataStore ds = new DataStore(this);
		ds.setAllWorkoutSelectedListItem(selItem);		
		
	}

	@Override
	public void onSetDayCurrent(DayInfo dayInfo) {
		
		tempDayInfo = new DayInfo(dayInfo);
		this.showYesNoDlg(R.string.msg_set_day_current, R.string.yes, R.string.cancel_but, DLG_REASON_SET_DAY_CURRENT);		
	}
	
	private void setDayCurrent()
	{
		if(tempDayInfo==null)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
			return;
		}
		
		new DeleteLogsOnAndAfterTask().execute(tempDayInfo);		
	}

	//when setting day current - 
	@Override
	protected void processDeleteLogsOnAndAfterResult(boolean res) {
		DataStore ds = new DataStore(this);
		ds.setCurWeekId(tempDayInfo.weekId);
		ds.setCurDayId(tempDayInfo.dayId);
		ds.setCurDayOrder(tempDayInfo.dayOrder);
		
		progr.setCurWorkoutId(tempDayInfo.workoutId, true);
		progr.setCurWorkoutMode(tempDayInfo.workoutMode);
		progr.setDay(tempDayInfo.dayId);
		progr.setDayOrder(tempDayInfo.dayOrder);
		progr.setWeek(tempDayInfo.weekId);		
		tempDayInfo=null;
		initActivity();
	}	

}
