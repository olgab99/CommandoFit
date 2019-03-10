package com.olmatech.fitness.ui;

//Main app menu - Complete program, endurance, mass .....
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.view.BottomFragment;

public class FirstActivityPhone extends BaseFragmentActivity {
	
	//private final static String TAG ="FirstActivityPhone";
	
	//dialogs
		private final static int DLG_REASON_QUIT = 101;
	@Override
	protected void onCreate(Bundle b) {		
		super.onCreate(b);			
		this.setContentView(R.layout.first_activity);
		
//		ImageView img =(ImageView)this.findViewById(R.id.img_target);
//		Bitmap  bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.target);
//		img.setImageBitmap(bmp1);		
		
		Button butAll = (Button)findViewById(R.id.butAll);
		butAll.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showProgram(WorkoutMode.All);
				
			}
			
		});
		Button butTactical = (Button)findViewById(R.id.butTact);
		butTactical.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showProgram(WorkoutMode.Tactical);
				
			}
			
		});
		Button butEndur = (Button)findViewById(R.id.butEndur);
		butEndur.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showProgram(WorkoutMode.Endurance);
				
			}
			
		});
		Button butMass = (Button)findViewById(R.id.butMass);
		butMass.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showProgram(WorkoutMode.Bodybuilding);				
			}
			
		});
		
		//for Samsung - nothing
		
		//for others 
//		FragmentManager frManager = this.getSupportFragmentManager();
//		BottomFragment frBottom = (BottomFragment) frManager.findFragmentById(R.id.botFragment);
//		frBottom.setBackButtonEnabled(false);
	}
	
		
//	@Override
//	protected void onDestroy() {		
//		super.onDestroy();
//		try
//		{
//			this.stopService(new Intent(this, MusicPlayerService.class));
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		
//	}

	@Override
	protected void onResume() {
		ImageView img =(ImageView)this.findViewById(R.id.img_target);
		Bitmap  bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.target);
		img.setImageBitmap(bmp1);
		super.onResume();
	}


	@Override
	protected void onStop() {
		recycleBitmapFromImageView(R.id.img_target);
		super.onStop();
	}
		
		
	private void showProgram(final WorkoutMode md)
	{
		//check if progr is completed and need reset
		if(md == WorkoutMode.All)
		{
			DataStore ds = new DataStore(this);
			int dayOrder = ds.getCurDayOrder();
			if(dayOrder == CurProgram.PROGR_COMPLETE_DAYORDER)
			{
				this.showMsgDlg(R.string.msg_reset, Common.REASON_NOT_IMPORTANT);
				return;
			}			
		}
		
		
		Intent intent = new Intent(this, MainActivityPhone.class);
		//Mode - All, Endurance, Bodybuilding, Cardio, Tacktical
		intent.putExtra("mode", md.getId());
		this.startActivityForResult(intent, Common.ACT_MainActivityPhone);	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CurProgram progr = CurProgram.getProgram();
		
		switch(requestCode)
		{		
		case Common.ACT_MainActivityPhone:
			if((resultCode == Common.RESULT_OK || resultCode == Common.RESULT_OK) && data != null) 
			{
				//final int wkId = data.getIntExtra("workout_id", -1);
				final int mdId = data.getIntExtra("progr_mode", Common.EMPTY_VALUE);
				final int wkModeId = data.getIntExtra("wk_mode", Common.EMPTY_VALUE);
				if(mdId <0)
				{
					this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
					return;
				}
				final WorkoutMode md = WorkoutMode.getModeFromId(mdId);				
								
				switch(md)
				{
				case All:
					//we returned from All workout list - show this workout
					//we might have Tactical workout
					if(wkModeId== Common.EMPTY_VALUE)
					{
						this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
						return;
					}
					final WorkoutMode mdWorkout = WorkoutMode.getModeFromId(wkModeId);	
					if(mdWorkout ==WorkoutMode.Tactical)
					{
						Intent intent2 = new Intent(this, TackticalActivity.class);
						intent2.putExtra("progr_mode", md.getId());
						intent2.putExtra("workout_mode", WorkoutMode.Tactical.getId());
						startActivityForResult(intent2, Common.ACT_TackticalActivity);
					}
					else
					{
						Intent intent = new Intent(this, ExerciseActivityPhone.class);
						Bundle b = new Bundle();
						b.putInt("mode", md.getId());
						intent.putExtras(b);
						this.startActivityForResult(intent, Common.ACT_ExerciseActivityPhone);
					}					
					
					break;
				case Tactical:	
					progr.setDay(Common.ZERO_ID);
					progr.setWeek(Common.ZERO_ID);					
					Intent intent2 = new Intent(this, TackticalActivity.class);						
					intent2.putExtra("progr_mode", md.getId());
					intent2.putExtra("workout_mode", WorkoutMode.Tactical.getId());
					startActivityForResult(intent2, Common.ACT_TackticalActivity);
					break;
				case Endurance:		
				case Bodybuilding:
				case Cardio:
					progr.setDay(Common.ZERO_ID);
					progr.setWeek(Common.ZERO_ID);					
				
					Intent intent3 = new Intent(this, ExerciseActivityPhone.class);
					Bundle b3 = new Bundle();
					b3.putInt("mode", md.getId());
					intent3.putExtras(b3);
					this.startActivityForResult(intent3, Common.ACT_ExerciseActivityPhone);					
					break;
				default:					
					break;
				}
			}
			break;
		case Common.ACT_ExerciseActivityPhone:
			if(resultCode == Common.RESULT_ERROR)
			{
				this.showMsgDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
			}			
			//figure out if we returned from All workout and completed it or not and display ACT_MainActivityPhone
			else if(data != null)
			{
				final boolean completed = data.getBooleanExtra("completed", false);
				final int mdId = data.getIntExtra("workout_mode", Common.EMPTY_VALUE);
				WorkoutMode md;
				if(mdId >=0)
				{
					md = WorkoutMode.getModeFromId(mdId);							
				}
				else md =WorkoutMode.Unknown;
				
				if(completed)
				{					
					if(md == WorkoutMode.All)
					{
						Common.showToast("Workout completed", this);
						new GetNextWorkoutTask().execute();
					}					
				}	
//				else if(md == WorkoutMode.All)
//				{
//					
//					//display MainActivity with this workout data
//					showProgram(WorkoutMode.All);
//				}
			}			
			break;
		case Common.ACT_TackticalActivity:
			//see if we completed day
			if(resultCode == Common.RESULT_OK)
			{
				if(data != null)
				{
					final int mdId = data.getIntExtra("progr_mode", WorkoutMode.Unknown.getId());
					if(mdId == WorkoutMode.All.getId())
					{
						final boolean completed = data.getBooleanExtra("completed", false);
						if(completed)
						{
							final int laps = data.getIntExtra("laps", 0);
							if(laps >0) Common.showToast("Workout completed", this);
							new GetNextWorkoutTask().execute();
						}
					}				
				}
			}			
			break;
		default: 
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
		
	}


	@Override
	public int getId() {		
		return Common.ACT_FirstActivityPhone;
	}
	
	@Override
	public void onBackPressed() {
		//for Samsung
		super.onBackPressed();
		
		//for otheres - nothing
		
	}
	

//	@Override
//	public void processDlgYes(int reason) {
//		if(reason == DLG_REASON_QUIT)
//		{
//			super.onBackPressed();
//		}
//	}
	//SET NEXT WORKOUT
	/*
	 * res[0]= dayInfo.weekId;
			res[1] = dayInfo.dayId;
			res[2] = dayInfo.workoutId;
			res[3] = dayInfo.workoutMode.getId();
	 */
	private void processNextWorkoutResult(final DayInfo res)
	{
		if(res == null)
		{
			this.showErrorDlg(R.string.err_reading_next, Common.REASON_NOT_IMPORTANT);
			return;
		}
		DataStore ds = new DataStore(this.getApplicationContext());
		if(res.dayOrder == CurProgram.PROGR_COMPLETE_DAYORDER)
		{
			//we completed program
			CurProgram progr = CurProgram.getProgram();
			progr.clearWorkout();
			ds.setCurWeekId(-1);
			ds.setCurDayId(-1);
			ds.setCurDayOrder(CurProgram.PROGR_COMPLETE_DAYORDER);
			
			this.showMsgDlg(R.string.msg_progr_completed, Common.REASON_NOT_IMPORTANT);			
			return;
			
		}
		
		ds.setCurWeekId(res.weekId);
		ds.setCurDayId(res.dayId);
		ds.setCurDayOrder(res.dayOrder);
		//Log.d(TAG, "Next workout: week=" + res[0] + " dayOfMonth=" + res[1]  + "  workout=" + res[2]);
		CurProgram progr = CurProgram.getProgram();
		progr.clearWorkout();
		//now display MainActivity with new data
		//showProgram(WorkoutMode.All);
	}
	
	private DayInfo getNextWorkoutInfo()
	{
		DayInfo dayInfo = new DayInfo();
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		CurProgram progr = CurProgram.getProgram();
			
		dayInfo.dayOrder =0;
		if(adapter.getNextDayInfo(dayInfo, progr.getDayOrder()))
		{
			return dayInfo;
//			long[] res = new long[4];
//			res[0]= dayInfo.weekId;
//			res[1] = dayInfo.dayId;
//			res[2] = dayInfo.workoutId;
//			res[3] = dayInfo.workoutMode.getId();
//			return res;
		}
		else return null;
	}
	
	private class GetNextWorkoutTask extends AsyncTask<String, Void, DayInfo>
	{
		private ProgressDialog dialog = new ProgressDialog(FirstActivityPhone.this);  
		// can use UI thread here
		 protected void onPreExecute() { 
			 this.dialog.setMessage(getString(R.string.processing));  
 			 this.dialog.show();  
		  }  

		//params - for weight: exType=1, weight, reps, curSet
		//for cardio - exType=2; secs, dist-meters
		@Override
		protected DayInfo doInBackground(String... args) {			
			return getNextWorkoutInfo();
		}
		
		protected void onPostExecute(final DayInfo result) 
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
			FirstActivityPhone.this.processNextWorkoutResult(result);
		}
		
	}	

}
