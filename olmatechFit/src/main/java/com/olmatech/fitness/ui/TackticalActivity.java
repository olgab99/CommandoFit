package com.olmatech.fitness.ui;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.controls.AppTickImageView;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.interfaces.ITackticalListener;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.ExData;
import com.olmatech.fitness.main.Exercise;
import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.view.TackticalCardioFragment;

public class TackticalActivity extends BaseTimeFragmentActivity implements ITackticalListener{
	
	//private final static String TAG ="TackticalActivity";
	
	private static int LIST_ITEM_H=80;
	//unique id in the List
	static int next_id = 1;
	
	private LinearLayout exList;	
	private List<TackticalCardioFragment> listCardioFr;	
	private int cur_index=0;
	
	private Workout workout;	
	private boolean playSound = false;
	private boolean doingCardio=false;
	
	private final static int DLG_REASON_WK_DONE=10;
	private final static int DLG_REASON_BACK_BUTTON=11;
	//private final static int DLG_REASON_WK_DONE_PROGR=12;
	private final static int DLG_REASON_DAY_COMPLETE=14;
	
	private boolean isTablet;	
	
	private int maxImgSize =0;
	
	private WorkoutMode progrMode = WorkoutMode.Unknown;
	private WorkoutMode workoutMode = WorkoutMode.Unknown;
		
	@Override
	protected void onCreate(Bundle b) {		
	 	super.onCreate(b);	 	
	 	this.setContentView(R.layout.activity_tacktical);
	 	CurProgram progr = CurProgram.getProgram();
	 	if(b!= null)
	 	{
	 		if(b.containsKey("cur_index")) cur_index=b.getInt("cur_index");	 		
	 	}	 	
	 	workout = progr.getWorkout();
	 	if(workout == null)
	 	{
	 		//HERE err on htc
	 		this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR); //
	 		return;
	 	}
	 	
	 	String str = workout.getTitle();
	 	if(str != null)
	 	{
	 		TextView tv = (TextView)this.findViewById(R.id.txt_title);
	 		tv.setText(str);
	 	}
	 	if(workout.getLaps() >0)
	 	{
	 		showLaps();
	 	}
	 	
	 	isTablet = Common.isTabletDevice(this);
	 	LIST_ITEM_H = (isTablet)? 120 : 80;
	 	
	 	TextView tv = (TextView)this.findViewById(R.id.txt_laps);	 	
	 	tv.setTextAppearance(this, (isTablet)? R.style.timerTextTab : R.style.timerText);
	 	
	 	exList = (LinearLayout)findViewById(R.id.container_ex_list);
	 		 	
	  	Button but = (Button)this.findViewById(R.id.but_workout_done);
	 	but.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				processWorkoutDone();				
			}
	 		
	 	});
	 	
	 	calcMaxImgSize();
	 	
	 	DataStore ds = new DataStore(this);
		playSound = ds.getPlaySoundOnTimeUp();//////////////////////////////
	 		
		//now we have to get list of ExData for a given tacktical dayOfMonth	
	 	LinearLayout exList = (LinearLayout)findViewById(R.id.container_ex_list);
	 	if(exList.getChildCount() == 0){
	 		processInitDone(true);
	 	}
	 	
	 	if(b != null)
	 	{	 
	 		if(b.containsKey("progr_mode")) this.progrMode = WorkoutMode.getModeFromId(b.getInt("progr_mode"));
	 		if(b.containsKey("workout_mode")) this.workoutMode = WorkoutMode.getModeFromId(b.getInt("workout_mode"));
	 		
		 	if( b.containsKey("cur_cardio"))
		 	{
		 		
		 		boolean cur_cardio = b.getBoolean("cur_cardio");	
		 		if(cur_cardio && listCardioFr != null)
		 		{
		 			long savedTime=-1, time=-1; 			
		 			boolean start_visible = false;
		 			
		 			if(b.containsKey("time")) time = b.getLong("time");	 			
		 			if(b.containsKey("start_visible")) start_visible = b.getBoolean("start_visible");		 			
		 			
		 			if(time >0 && start_visible)  //we have time left - set this in  fragment
		 			{
		 				int ind =getCardioIndexFromListIndex(cur_index);
		 				if(ind >=0)
		 				{	 					
		 					TackticalCardioFragment frCardio =listCardioFr.get(ind);			 				
		 					//String butTitle = null;
		 					boolean timer_on=false;
		 					//if(b.containsKey("start_title")) butTitle = b.getString("start_title");
		 					if(b.containsKey("time_now")) savedTime = b.getLong("time_now");
		 					if(b.containsKey("timer_on")) timer_on = b.getBoolean("timer_on");
		 					
		 					isTimerWasOn= timer_on;
		 					if(isTimerWasOn) time_saved = savedTime;
		 					
		 					frCardio.setTime(time, false, false);		 					
		 					frCardio.setIsTimerOn(true);		 					
		 						 					
		 				}
		 			}	 			
		 		}
		 	}
	 	}
	 	else
	 	{
	 		Intent intent = this.getIntent();
	 		progrMode =WorkoutMode.getModeFromId(intent.getIntExtra("progr_mode", WorkoutMode.Unknown.getId()));
	 		workoutMode = WorkoutMode.getModeFromId(intent.getIntExtra("workout_mode", WorkoutMode.Unknown.getId()));
	 	}	 	
	 	
	 	Common.setKeepScreenOn(this, ds.getKeepAwake());
	}
	
	private void calcMaxImgSize()
	{
		maxImgSize= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());	
	}
	
	
	@Override
	protected void onResume() {		
		if(time_saved >0)
		{
			//update cardio with time
			Exercise exer = workout.getExerciseAt(cur_index);
			if(exer != null)
			{
				if(exer.getExType() == ExType.Cardio && listCardioFr != null)
				{
					int ind =getCardioIndexFromListIndex(cur_index);
					if(ind >=0)
					{
						TackticalCardioFragment frCardio =listCardioFr.get(ind);
						
						setCardioOnResume(frCardio);
						
//						if(frCardio.isTimerOn())
//						{
//							long time = frCardio.getTime();							
//	 						long t = getTimeDiff(time);
//	 						if(t >0)
//	 						{
//	 							frCardio.setTime(t, false, false);	 						
//	 						}
//	 						else
//	 						{
//	 							//we are done - set time to 1 sec
//	 							frCardio.setTime(1, false, false);	 							
//	 						}
//	 						frCardio.calcProgress();
//	 						if(this.isTimerWasOn)
//	 						{
//	 							frCardio.setStarted();
//	 						}
//	 						else
//	 						{
//	 							frCardio.setPaused();
//	 						}
//						}						
						
					}
					
				}
			}			
			time_saved = 0;
		}
		
		super.onResume();
	}




	private void processWorkoutDone()
	{
		if(workout.getLaps() <=0)
		{
			CurProgram progr = CurProgram.getProgram();
			if(progr.gettWeek() != Common.ZERO_ID && progr.getDay() != Common.ZERO_ID)
			{
				this.showYesNoCancelDlg(R.string.msg_complete, R.string.set_complete, 
						R.string.set_not_complete, R.string.cancel_but, DLG_REASON_DAY_COMPLETE);
			}
			else //module
			{
				this.showYesNoDlg(R.string.err_zero_laps, R.string.finish_workout, R.string.contin, DLG_REASON_WK_DONE);
			}						
			return;
		}	
		
		stopCardio();
		//save Log data on Task
		new SaveLogTask().execute();
	
	}
	
	private void stopCardio()
	{
		//need to stop cardio if on
		if(doingCardio)
		{
			int cardioIndex =getCardioIndexFromListIndex(cur_index);
			if(cardioIndex >=0)
			{
				TackticalCardioFragment frCardio =listCardioFr.get(cardioIndex);
				frCardio.stopCardio();
			}
		}
	}
	
	
	@Override
	public void processDlgYes(final int reason) {
		super.processDlgYes(reason);
		if(reason == DLG_REASON_WK_DONE)
		{
			stopCardio();
			close(false);
		}
		else if(reason == DLG_REASON_DAY_COMPLETE)
		{
			//no laps - but wont to complete the day
			stopCardio();
			//save log
			new SaveLogTask().execute(Boolean.TRUE);
		}		
		else if(reason == DLG_REASON_BACK_BUTTON)
		{
			stopCardio();
			if(workout.getLaps() <=0)
			{
				close(false);
			}
			else
			{
				//save log
				new SaveLogTask().execute();
			}		
		}		
	}
	
	
	private void close(final boolean setDayCompleted)
	{
		if(setDayCompleted)
		{
			Intent intent = this.getIntent();		
			intent.putExtra("progr_mode",progrMode.getId());
			intent.putExtra("wk_mode", WorkoutMode.Tactical.getId());
			intent.putExtra("completed", true);
			intent.putExtra("laps", workout.getLaps());
			setResult(Common.RESULT_OK, intent);
			finish();		
		}
		else
		{
			this.setResult(Common.RESULT_OK);
			finish();
		}		
	}
	

	@Override
	public void processDlgNo(int reason) {
		if(reason == DLG_REASON_BACK_BUTTON)
		{
			//close without saving log
			stopCardio();
			close(false);
			
		}
		else if(reason == DLG_REASON_DAY_COMPLETE)
		{
			stopCardio();
			close(false);
		}		
		else super.processDlgNo(reason);
	}

	//ret resulty from adding log and setting day complete
	//if onlySetComplete=true - dio not save log, but mark day as complete
	private boolean[] saveLog(final boolean onlySetComplete)
	{
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		CurProgram progr = CurProgram.getProgram();
		
		final long dt = Common.getDateTimeForToday();
		
		boolean[] res = new boolean[2];
		
		final int week = progr.gettWeek();
		
		res[0] = (onlySetComplete)? true : adapter.addTackticalLogData(User.getUser().getId(),dt, progr.getId(), 
				workout.getId(), workout.getLaps(), week, progr.getDayOrder());
		
		if(!res[0])
		{
			res[1] = false;
			return res;
		}
		
		if(week != Common.ZERO_ID && progr.getDay() != Common.ZERO_ID)
		{
			res[1] = adapter.setDayComplete(true, progr.getId(), week, progr.getDay(), dt);
		}
		else res[1] = false;
		
		return res;
	}
	
	private void processSaveLogDone(final boolean res, final boolean setDayCompleted)
	{
		if(res)
		{
			close(setDayCompleted);
		}
		else {
			this.showYesNoDlg(R.string.err_saving_log_yesno, R.string.finish_workout, R.string.contin, DLG_REASON_WK_DONE);
		}
	}
	
	private void showLaps()
	{
		TextView tv = (TextView)this.findViewById(R.id.txt_laps);
 		tv.setText(Integer.toString(workout.getLaps()));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		if(workout == null)
		{
			workout = CurProgram.getProgram().getWorkout();
			if(workout == null) return;
		}
		outState.putInt("cur_index", cur_index);	
		outState.putInt("progr_mode", progrMode.getId());
		outState.putInt("workout_mode", workoutMode.getId());
		
		Date d = new Date();			
		outState.putLong("time_now", d.getTime());		
		
		Exercise exer = workout.getExerciseAt(cur_index);
		if(exer != null)
		{
			if(exer.getExType() == ExType.Cardio)
			{
				outState.putBoolean("cur_cardio", true);
				if( listCardioFr != null)
				{
					int ind =getCardioIndexFromListIndex(cur_index);
					if(ind >=0)
					{
						TackticalCardioFragment frCardio =listCardioFr.get(ind);
						if(frCardio != null)
						{
							outState.putLong("total_time", frCardio.getTotalTime());
							outState.putLong("time", frCardio.getTime());
							outState.putInt("progress", frCardio.getProgressVal());
//							String s = frCardio.getStartButTitle();
//							if(s != null) outState.putString("start_title", s);
							outState.putBoolean("start_visible", frCardio.isStartButVisible());
							outState.putBoolean("timer_on", frCardio.isTimerOn());						
							
						}
					}
				}				
			}
			else outState.putBoolean("cur_cardio", false);
		}	
		
		super.onSaveInstanceState(outState);
	}



//	private boolean initWorkout()
//	{
//		CurProgram progr = CurProgram.getProgram();
//		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());			
//		return false; //adapter.getTackticalWorkout(workout,  progr.getId());		
//	}
	
	private void processInitDone(final boolean res)
	{
		if(!res)
		{
			this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
	 		return;
		}
		LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		final int cnt = (workout != null)? workout.getExerciseCount() : 0;
		if(cnt == 0)
		{
			this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
	 		return;
		}
		ExData exData;
		Exercise exer;
		Gender g = User.getUser().getGender();
				
		AssetManager assetMng = this.getAssets();
		String progrDir = CurProgram.getProgrDir() + File.separator;
		for(int i =0; i < cnt; i++)
		{
			exer = workout.getExerciseAt(i);
			if(exer == null) continue;
			
			exData = exer.getCurExData(g);
			if(exData == null) continue;
			
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LIST_ITEM_H, getResources().getDisplayMetrics());	
			if(exer.getExType() == ExType.Cardio)
			{
				addCardioToList(exData, i, height+80, playSound);
			}
			else
			{
				addExToList(exData, false, g, progrDir, li, assetMng, i, height);
			}
		}
		
		//set the first item 
		if(exList.getChildCount() == 0)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_FATAL_ERROR);
			return;
		}
		
		//TEST
		//cur_index =1;
		
		exer = workout.getExerciseAt(cur_index);
		exer.setCompleted(false);
		if(exer.getExType() == ExType.Cardio)
		{
			if(listCardioFr != null)
			{				
				int ind =getCardioIndexFromListIndex(cur_index);
				if(ind >=0)
				{					
					setCardioItemStart(ind, true, true);
				}
				else
				{
					//error
				}				
			}
			else
			{
				//error
				this.showErrorDlg(R.string.err_processing, Common.REASON_FATAL_ERROR);
				return;
			}
			
		}
		else
		{			
			setExViewStatus(cur_index, false, true);
		}
		
	}
	
	private int getCardioIndexFromListIndex(final int listInd)
	{
		int ind =0;
		for(TackticalCardioFragment frCardio: listCardioFr)
		{
			if(frCardio.getIndex() == cur_index)
			{
				return ind;
			}
			ind++;
		}
		return -1;
	}
	
	//we are starting Cardio exercise first time
	private void setCardioItemStart(final int cardioIndex, final boolean showActive,final boolean setStart)
	{
		if(listCardioFr == null) return;
		TackticalCardioFragment frCardio =listCardioFr.get(cardioIndex);		
		if(showActive)
		{
			if(setStart)
			{
				frCardio.setStart();  // we are starting cardio
			}
			else
			{
				frCardio.showActive(true);
			}			
		}
		else
		{
			frCardio.showActive(false);			
		}
		if(doingCardio != showActive)
		{
			setDoneButtonsEnabled(!showActive);
			doingCardio = showActive;
		}		
	}
	
		
	private boolean addCardioToList(final ExData exData, final int listIndex, int height,
			final boolean playSound)
	{	
		
		FragmentManager frManager = this.getSupportFragmentManager();
		
		if(listCardioFr == null) listCardioFr=new LinkedList<TackticalCardioFragment>();
		TackticalCardioFragment frCardio = new TackticalCardioFragment();
		
		//create View to add 
		LinearLayout innerLayout1 = new LinearLayout(this);
       // innerLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        innerLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final int id = findId(exList);
        innerLayout1.setId(id);
		FragmentTransaction ft = frManager.beginTransaction();	
		ft.add(id, frCardio);
		ft.commit();
		
		exList.addView(innerLayout1);
		
		frCardio.setTime(exData.getMinutes()*60, true,false);
		//TEST
		//frCardio.setTime(5, true, false);
		
		//frCardio.setMessage(exData.getDescription());  //to set message from Description, we will use generic for now
//		frCardio.setContainingViewId(id);
		frCardio.setIndex(listIndex);
		frCardio.setPlaySoundOnTimeUp(playSound);
		
		listCardioFr.add(frCardio);
		
		return true;
	}
	
	// Returns a valid id that isn't in use
	private int findId(View parent){  
		View v = parent.findViewById(next_id);  
	    while (v != null){  
	        v = parent.findViewById(++next_id);  
	    }  
	    return next_id++;  
	}
	
		
	//add exercise 
	/**
	 * 
	 * @param exData - data of exercise
	 * @param isComplete
	 * @param gender
	 * @param programDir
	 * @param li  - Inflater
	 * @param exList - LinearLayout to add to
	 * @param assetMng
	 * @param listIndex - index in the Workout
	 * @param height - list item height
	 * @return
	 */
	private boolean addExToList(final ExData exData, final boolean isComplete, Gender gender, final String programDir, 
			LayoutInflater li, AssetManager assetMng, final int listIndex, int height) 
	{		
		String imgName=null;		
		View view= li.inflate(R.layout.ex_view_item, null); // = (isTablet)? li.inflate(R.layout.ex_view_item_tab, null) : li.inflate(R.layout.ex_view_item, null);		
		ImageView imgEx = (ImageView)view.findViewById(R.id.img_ex);
		boolean imgAdded= false;
		if (imgEx != null){
			//image
			if (gender == Gender.Female) {
				imgName = exData.getImgFemale();
				if (imgName == null)
					imgName = exData.getImgMale();
			} else {
				imgName = exData.getImgMale();
				if (imgName == null)
					imgName = exData.getImgFemale();
			}
			if (imgName == null)
			{
				imgEx.setImageResource(R.drawable.generic_ex_img); // TEMP - TODO
				
			}
			else
			{
				if(maxImgSize <=0)
				{
					calcMaxImgSize();
				}
				Bitmap bm = Common.getBmpFromAssets2(imgName, programDir, assetMng, maxImgSize, maxImgSize);
				if(bm != null)
				{
					imgEx.setImageBitmap(bm);
					imgAdded=true;
				}
				else{					
					imgEx.setImageResource(R.drawable.generic_ex_img); // TEMP - TODO					
				}
			}
			imgEx.setTag(Integer.valueOf(listIndex));
			
			//click event
			final String exImgName =imgName;
			imgEx.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					if(ev.getAction() != MotionEvent.ACTION_UP) return true;
					Object obj = v.getTag();
					if(obj == null) return true;
					int ind = -1;
					try
					{
						ind = ((Integer)obj).intValue();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						return true;
					}
					showExImgView(ind, exImgName);
					return true;
				}
				
			});
		}//image
		
		
		//text
		TextView tvName = (TextView)view.findViewById(R.id.txt_ex_name);
		tvName.setText(exData.getTitle());
		
		//overlay
		final View viewComplete = view.findViewById(R.id.imgCompleteOverlay);
		if(isComplete) viewComplete.setVisibility(View.VISIBLE);
		else viewComplete.setVisibility(View.GONE);
		
		AppTickImageView imgTick = (AppTickImageView) view.findViewById(R.id.imgArrow);
		imgTick.setBackgroundResource(R.drawable.arrow_right_anim);
//		imgTick.setAppearance(this);
		imgTick.setVisibility(View.INVISIBLE);
		
		//TODO - get if this is completed during this Lap		
		//button
		Button but = (Button) view.findViewById(R.id.but_done);  /////////////////////////////////////////////
		but.setTag(Integer.valueOf(listIndex));
		but.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(viewComplete != null && viewComplete.getVisibility() != View.VISIBLE)
				{
					viewComplete.setVisibility(View.VISIBLE);
				}
				onExButtonDoneClick(v.getTag());
			}
			
		});			
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,height));	
		
		if(imgAdded) view.setTag(new Object()); //tag to mark exercise
		exList.addView(view);
		
		return true;
	}
	
	
	//set status exercise (not cardio)
	private void setExViewStatus(final int ind, final boolean showComplete, final boolean showActive )
	{
		//get View
		View view = exList.getChildAt(ind);
		//overlay
		final View viewComplete = view.findViewById(R.id.imgCompleteOverlay);
		if(viewComplete != null)
		{
			if(showComplete ) viewComplete.setVisibility(View.VISIBLE);
			else viewComplete.setVisibility(View.GONE);
		}	
		
		//background
		View container = view.findViewById(R.id.ex_view_container);
		if(container != null)
		{
			if(showActive) container.setBackgroundResource(R.color.ex_active);
			else container.setBackgroundResource(R.color.transparent);
		}
		
		//tick
		ImageView imgTick = (ImageView) view.findViewById(R.id.imgArrow);
		if(imgTick != null)
		{
			AnimationDrawable tickAnimation = (AnimationDrawable) imgTick.getBackground();
			if(showActive)
			{
				if(!tickAnimation.isRunning())
				{
					tickAnimation.setOneShot(false);
					tickAnimation.start();
				}				
				if(imgTick.getVisibility() != View.VISIBLE) imgTick.setVisibility(View.VISIBLE);
			}
			else{
				if(tickAnimation.isRunning()) tickAnimation.stop();
				if(imgTick.getVisibility() == View.VISIBLE) imgTick.setVisibility(View.INVISIBLE);
			}		
		}		
	}
	
	//button Done clicked in Exercise List item (not cardio)
	private void onExButtonDoneClick(final Object tag)
	{
		if(tag == null) return;
		int index=-1;  //index in the list
		try
		{
			Integer val = (Integer)tag;
			index = val.intValue();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return;
		}
		if(index < 0) return;
		
		//get view and set Complete Overlay on, set exercise as done
		processExerciseComplete(index);
	}
	

	@Override
	public int getId() { return Common.ACT_TackticalActivity; }

		


//cardio completed
	@Override
	public void onTimeComplete(final int listIndex) {
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				processExerciseComplete(listIndex);
			}
			
		});		
	}
	
	private void processExerciseComplete(final int listIndex)
	{
		//set this is done, activate next
		//if this is last - add lap, set first as active
		//figure out the next index
		Exercise exer = workout.getExerciseAt(listIndex);
		exer.setCompleted(true);
		
		int next_ind=-1;
		final int total = exList.getChildCount();
		for(int j=0; j < total; j++)
		{
			if(j == listIndex) continue;
			if(!workout.getExCompleted(j))
			{
				next_ind =j;
				break;
			}
		}
		final ScrollView sv = (ScrollView)this.findViewById(R.id.scroll_container_ex_list);
		if(next_ind >=0)
		{				
			if(exer.getExType() == ExType.Cardio)
			{
				int ind =getCardioIndexFromListIndex(cur_index);
				this.setCardioItemStart(ind, false, false);
			}
			else
			{
				this.setExViewStatus(cur_index, true, false);
			}
			//show next active
			cur_index = next_ind;
			exer = workout.getExerciseAt(cur_index);
			if(exer.getExType() == ExType.Cardio)
			{
				int ind =getCardioIndexFromListIndex(cur_index);
				this.setCardioItemStart(ind, true,true);// start
			}
			else
			{
				this.setExViewStatus(cur_index, false, true);
			}
			//scroll to view
			View v = exList.getChildAt(cur_index);
			final int top = v.getTop();
			
			new Handler().post(new Runnable() {
	            @Override
	            public void run() {
	                sv.smoothScrollTo(0, top);
	            }
	        });
			
			
		}
		else
		{
			//we done all			
			cur_index = 0;
			//complete lap
			workout.addLap();
			showLaps();
			//update exerc. status to not complete
			final int cnt = workout.getExerciseCount();			

			if(exer.getExType() == ExType.Cardio)
			{
				int ind =getCardioIndexFromListIndex(cur_index);
				this.setCardioItemStart(ind, false, false);
			}
			else
			{
				this.setExViewStatus(cur_index, true, false);
			}
			//update list and set the first ex to active			
			for(int i=1; i < cnt; i++)
			{
				exer = workout.getExerciseAt(i);
				exer.setCompleted(false);
				if(exer.getExType() != ExType.Cardio)
				{
					this.setExViewStatus(i, false, false);
				}
			}
			//deal with cardio
			for(TackticalCardioFragment fr: listCardioFr)
			{
				//set time to total time, progress to 0
				int ind = fr.getIndex();
				if(ind == 0) continue;
				fr.setTimeToTotal(true);
				if(ind == listIndex)
				{
					fr.showActive(false);
				}
				else fr.showStartButton(false);
			}	
			//deal with ex at index 0
			exer = workout.getExerciseAt(0);
			exer.setCompleted(false);
			if(exer.getExType() == ExType.Cardio)
			{
				setCardioItemStart(0, true, true);
//				TackticalCardioFragment fr = listCardioFr.get(0);
//				fr.setStart();
			}
			else
			{
				setExViewStatus(0,false, true);
			}
			cur_index =0;
			
			new Handler().post(new Runnable() {
	            @Override
	            public void run() {
	                sv.smoothScrollTo(0, 0);
	            }
	        });
		}	
		
	}
	
	/////// activities
	private void showExImgView(final int exIndex, final String exImgName)
	{
		if(exIndex <0) return;
		Intent intent = new Intent(this, ExImageViewActivity.class);
		intent.putExtra("ex_index", exIndex);
		if(exImgName != null) intent.putExtra("img_name", exImgName);
		this.startActivityForResult(intent, Common.ACT_ExImageViewActivity);			
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case Common.ACT_ExImageViewActivity: return;
		default: super.onActivityResult(requestCode, resultCode, data);
		break;
		}
		
	}

	@Override
	protected void resetSettings() {
		DataStore ds = new DataStore(this);
		final boolean sound = ds.getPlaySoundOnTimeUp();
		if(playSound != sound)
		{
			playSound = sound;
			for(TackticalCardioFragment frCardio: listCardioFr)
			{
				frCardio.setPlaySoundOnTimeUp(playSound);
			}
			
		}
		Common.setKeepScreenOn(this, ds.getKeepAwake());		
	}
	
	
	
	@Override
	protected void resetView() {
		//we deleted cur log in menu
		workout.setLaps(0);
		showLaps();
		
	}

	private void setDoneButtonsEnabled(final boolean enabled)
	{
		if(exList == null) return;
		int cnt = exList.getChildCount();
		View view;
		for(int i= 0; i < cnt; i++)
		{
			view = exList.getChildAt(i);
			if(view ==  null) continue;
			View butView = view.findViewById(R.id.but_done);
			if(butView == null) continue;
			Button but = (Button) butView;
			but.setEnabled(enabled);
			
		}
	}

		


	@Override
	protected void onDestroy() {
		if(exList != null)
		{
			int cnt = exList.getChildCount();
			View view;
			ImageView imgView;
			for(int i=0; i < cnt; i++)
			{
				view = exList.getChildAt(i);
				if(view != null && view.getTag() != null)
				{
					imgView = (ImageView)view.findViewById(R.id.img_ex);
					Bitmap bmp1 = null;
					 try
					 {
						 bmp1 = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
					 }
					 catch(Exception ex)
					 {
						 bmp1 = null;
					 }
					 imgView.setImageBitmap(null);
					 if(bmp1 != null)
					 {
						 bmp1.recycle();
					 }
				}
			}
		}
		
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if(workout==null) return;
		if(workout.getLaps() <=0)
		{
			processWorkoutDone();
		}
		else
		{
			this.showYesNoCancelDlg(R.string.msg_save, R.string.save_and_close, R.string.close_no_save, 
					R.string.cancel_but, DLG_REASON_BACK_BUTTON);
		}
			
	}

	/////Save log
	private class SaveLogTask extends AsyncTask<Boolean, Void, boolean[]>
	{
		private ProgressDialog dialog = new ProgressDialog(TackticalActivity.this);  
  		
  		
  		// can use UI thread here
 		 protected void onPreExecute() { 
 			 this.dialog.setMessage(getString(R.string.processing));  
 			 this.dialog.show();  
 		  }  

		@Override
		protected boolean[] doInBackground(Boolean... params) {		
			if(params == null || params.length == 0)
			{
				return saveLog(false);	
			}
			else
			{
				return saveLog(params[0].booleanValue());	
			}
						
		}
		
		protected void onPostExecute(final boolean[] result) {  
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
	        	  TackticalActivity.this.processSaveLogDone(result[0], result[1]);
	          }	          
	        
		}     		
		
	}
	
	
}
