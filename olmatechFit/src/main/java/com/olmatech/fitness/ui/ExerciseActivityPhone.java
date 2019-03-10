package com.olmatech.fitness.ui;

//Workout view
import java.io.File;
import java.util.Date;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.ExData;
import com.olmatech.fitness.main.Exercise;
import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.User.FitnessLevel;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.main.WorkoutLog;
import com.olmatech.fitness.ui.MainGestureDetector.IDetectGestures;
import com.olmatech.fitness.ui.MainGestureDetector.SwipeDirection;
import com.olmatech.fitness.view.CardioBottomFragment;
import com.olmatech.fitness.view.CardioBottomFragment.ICardioBottomListener;
import com.olmatech.fitness.view.CardioImgFragment;
import com.olmatech.fitness.view.CardioImgFragment.ICardioListener;
import com.olmatech.fitness.view.DescriptionFragment;
import com.olmatech.fitness.view.DescriptionFragment.IDescriptionListener;
import com.olmatech.fitness.view.ExImgFragment;
import com.olmatech.fitness.view.ExImgFragment.IExImgFragmentListener;
import com.olmatech.fitness.view.WheelsFragment;
import com.olmatech.fitness.view.WheelsFragment.IWheelsListener;

public class ExerciseActivityPhone extends BaseTimeFragmentActivity implements IExImgFragmentListener, IDetectGestures, 
				IWheelsListener,ICardioListener,ICardioBottomListener, IDescriptionListener{
	
	private final static String TAG ="ExerciseActivityPhone";
	private GestureDetector gestureDetector;
	private CurProgram progr;
	
	//data
	private Gender gender;
	private String dayTitle; // type of the current dayOfMonth
	
	//fragments
	private ExImgFragment frExImg;
	private WheelsFragment frWeels;
	private DescriptionFragment frDescription;
	private CardioImgFragment frCardioTop;	
	private CardioBottomFragment frCardioBottom;	
	
	//timer
	private boolean startTimerOnSave= false;
	private int timerInterval = Common.DEF_REST_TIME;
	
	private WorkoutMode startMode = WorkoutMode.Unknown;
	
	private boolean advanceOnExComplete= true;
	
	//dialogs
	private final static int DLG_REASON_CHECK_WK_COMPLETE=101;
	private final static int DLG_REASON_NOTIFY=102;
	private final static int DLG_REASON_CARDIOON=103;
	private final static int DLG_REASON_CARDIO_TIME_NOT_COMPLETE = 104;
	
	//saved values
		
	@Override
	protected void onCreate(Bundle b) {
		// TODO Auto-generated method stub
		super.onCreate(b);	
		//we already set Workout id, dayOfMonth and week for thsi pogram
		progr = CurProgram.getProgram();
		this.setContentView(R.layout.activity_exercise);				
		if(b == null)
		{
			Intent intent = this.getIntent();
			Bundle data = intent.getExtras();
			if(data != null)
			{
				if(!data.containsKey("mode"))
				{
					this.setResult(Common.RESULT_ERROR);
					finish();
					return;
				}
				final int mdId = data.getInt("mode");
				startMode = WorkoutMode.getModeFromId(mdId);
			}
			else //error 
			{
				this.setResult(Common.RESULT_ERROR);
				finish();
			}			
		}
		
		doInitOnTask();
		finishInit(b);
		
		//new ActivityInitTask().execute(b);	
		
	}	
	
	private void doInitOnTask()
	{
		User user = User.getUser();
		gender = user.getGender();
		if(gender == Gender.Unknown) gender = Gender.Male;
		user.setGender(gender);
		
		DataStore ds = new DataStore(this);
		startTimerOnSave = ds.getStartTimerOnSave();
		timerInterval = ds.getRestTime();
		final boolean playSound = ds.getPlaySoundOnTimeUp();
		
		advanceOnExComplete = ds.getAdvanceOnComplete();
		
		FragmentManager frManager = this.getSupportFragmentManager();
        frWeels = (WheelsFragment) frManager.findFragmentById(R.id.frag_wheels);
		
		frExImg = (ExImgFragment) frManager.findFragmentById(R.id.frag_eximg);
		frExImg.setTimeInterval(timerInterval);
		frExImg.setPlaySoundOnTimeUp(playSound); 
		
		frDescription = (DescriptionFragment)frManager.findFragmentById(R.id.frag_descrip);
		
		frCardioTop = (CardioImgFragment)frManager.findFragmentById(R.id.frag_cardio_top);
		frCardioTop.setPlaySoundOnTimeUp(playSound);
		frCardioTop.setToggleStopButton(false); //always show till clicked
				
		frCardioBottom = (CardioBottomFragment)frManager.findFragmentById(R.id.frag_cardio_bot);
	}
	
	private void finishInit(Bundle b)
	{		
		DataStore ds = new DataStore(this);
        Common.setKeepScreenOn(this, ds.getKeepAwake());		
		
		initGestures();
		final boolean iscardio = processInitResult(b);		
		
		if(iscardio && b != null && b.containsKey("cur_cardio") && b.getBoolean("cur_cardio")==true)
		{
			//check if we pressed DONE button before 
			boolean stop_visible = frCardioTop.isStopButVisible();
			boolean start_visible = frCardioTop.isStartButVisible();
			
			if(start_visible)
			{
				if(stop_visible)
				{
					//started 
					long savedTime=-1;
					long time = frCardioTop.getTime();
					boolean timer_on = frCardioTop.isTimerOn();
					
					if(time >0 && timer_on)
					{
						if(b.containsKey("time_now")) savedTime = b.getLong("time_now");
						isTimerWasOn= timer_on;
						if(isTimerWasOn){
							time_saved = savedTime;
						}
						frCardioTop.displayTimeOnUI(true);
					}
				}
				else
				{
					time_saved=0;
				}				
			}			
			else //if(!stop_visible)				
			{
				//both hidden - DONE clicked
				time_saved=0;
			}			
			
		}		
	}
	
	
	@Override
	protected void onDestroy() {
		ImageView imgEx = (ImageView)this.findViewById(R.id.eximg);
		if(imgEx.getTag() != null)
		{
			Bitmap bmp1 = null;
			 try
			 {
				 bmp1 = ((BitmapDrawable)imgEx.getDrawable()).getBitmap();
			 }
			 catch(Exception ex)
			 {
				 bmp1 = null;
			 }
			 imgEx.setImageBitmap(null);
			 if(bmp1 != null)
			 {
				 bmp1.recycle();
			 }
		}
		
		super.onDestroy();
	}




	@Override
	protected void onResume() {
		if(time_saved >0 && frCardioTop != null)
		{
			if(isCurExCardio())
			{
				setCardioOnResume(frCardioTop);
			}
		}
		super.onResume();
	}


	private boolean isCurExCardio()
	{
		Workout workout = progr.getWorkout();
		if(workout != null && workout.getCurExercise().getExType()== ExType.Cardio) 
		{
			return true;
		}
		return false;
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {		
		final Workout workout = progr.getWorkout();	
		if(workout == null)
		{
			goBackOnError();
			return;
		}
		boolean curCardio = false;
		if(frCardioTop != null)
		{			
			if(isCurExCardio())
			{
				curCardio=true;
				Date d = new Date();			
				outState.putLong("time_now", d.getTime());							
			}
		}	
		
		outState.putBoolean("cur_cardio", curCardio);
		
		//add UI data
		TextView tvTitle = (TextView)this.findViewById(R.id.txt_title);
		outState.putString("title", (String) tvTitle.getText());
		tvTitle = (TextView)this.findViewById(R.id.txt_title2);
		outState.putString("title2", (String) tvTitle.getText());
		outState.putString("day_title", dayTitle);
		
		final int modeId = workout.getMode().getId();
			
		outState.putInt("wk_mode", modeId);	
		View imgTick = (curCardio)? this.findViewById(R.id.img_ex_done_cardio) : this.findViewById(R.id.img_ex_done);
		if(imgTick != null)
		{
			outState.putInt("complete_visisble", imgTick.getVisibility());
		}		
		
		super.onSaveInstanceState(outState);
	}




	private void initWheelFragment()
	{
		frWeels = (WheelsFragment) this.getSupportFragmentManager().findFragmentById(R.id.frag_wheels);
		
	}
	private void initImageFragment()
	{
		frExImg = (ExImgFragment) this.getSupportFragmentManager().findFragmentById(R.id.frag_eximg);
		frExImg.setTimeInterval(timerInterval);
	}
	
	public void initDescriptionFragment()
	{
		 frDescription = (DescriptionFragment)this.getSupportFragmentManager()
				.findFragmentById(R.id.frag_descrip);
	}
	
	public void initCardioFragment()
	{
		frCardioTop = (CardioImgFragment)this.getSupportFragmentManager().findFragmentById(R.id.frag_cardio_top);
	}
	
	//called for displayExercise and displayCardio
	/**
	 * shows Weight or Cardio, hides Description
	 * @param showex
	 * @param doFillData
	 */
	private void showExOrCardio(final boolean showex, final boolean doFillData)
	//we shoe either: Exersice top + wheels  or  Cardio top + cardio bottom
	{
		if(frExImg == null) this.initImageFragment();
		if(frCardioTop == null) this.initCardioFragment();
		if(frWeels == null) initWheelFragment();
		
		if(frExImg == null || frCardioTop==null)
		{
			goBackOnError();
			return;
		}
		
		if(showex)
		{	
			if(doFillData) frWeels.fillData();
				FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
				//2 choices for top
				tr.show(frExImg);						
				tr.hide(frCardioTop);
				//bottom			
				tr.show(frWeels);				
				tr.hide(frDescription);
				tr.hide(frCardioBottom);
				tr.commit();
			frExImg.toggleDescriptionButton(true);
			
		}
		else //hide
		{	
				FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
				//top
				tr.show(frCardioTop);					
				tr.hide(frExImg);				
				//bottom
				tr.show(frCardioBottom);
				tr.hide(frDescription);
				tr.hide(frWeels);
				tr.commit();
			
			frCardioTop.toggleDescriptionButton(true);
		}
		
	}
	

	
	private void initGestures() {		
		// gestures
		float scale = this.getResources().getDisplayMetrics().densityDpi;
		gestureDetector = new GestureDetector(this, new MainGestureDetector(this, scale));

	}

	
	private void processLogSaveResult(final long logId, final int curSet, final boolean isCardio)
	{
		if(logId <=0)
		{
			this.showErrorDlg(R.string.err_save_log, DLG_REASON_NOTIFY);
			return;
		}
		Workout workout = progr.getWorkout();
		workout.increaseCurSet(); //cur set num 
		User user = User.getUser();
		progr.setCurExLogId(logId, user.getGender());
		
		Common.showToast("Log entry saved.", this);
		
		if(isCardio)
		{
			if(advanceOnExComplete)
			{
				//show next Excercesi or finish
				if(!showNext(true))
				{
					checkCompleteAndFinish(false);
				}
			}
			else
			{
				setCompletedImg(true, true);
				this.frCardioBottom.setSaveButtonVisible(false);
			}			
		}
		else
		{
			int sets = workout.getCurExercise().getRecommendedSets();
			if(sets < 1)		
			{
				sets = Workout.AVER_MAX_SETS;
			}
			final boolean completed = workout.getCurExCompleted();
			
			//check if completed
			if(advanceOnExComplete && completed)
			{
				showNext(true);
			}
			else
			{
				showSetsAndLog(workout.getCurSet(), sets, completed);
			}			
		}		
	}
		
	private void showSetsAndLog(int curSet, int sets, final boolean completed)
	{
		//display saved
		if(this.frWeels != null)
		{
			frWeels.displaySavedLog(curSet);
			if(this.startMode == WorkoutMode.All)
			{
				if(curSet+1 <= sets) frWeels.setSetsTitle(sets, curSet+1);
				else  frWeels.setSetsTitle("Completed");
			}
			else frWeels.setSetsTitle(sets, curSet+1);			
		}
		setCompletedImg(completed, false);	
	}

	//show weight or cardio
	//ret true if cardio is displayed, false if exerc. or error - need for resetting time
	private boolean processInitResult(final Bundle b)
	{	
		//if b == null - we init, other wise re-init
		boolean iscardio = false;
		if(b != null)
		{
			//re-init
			TextView tvTitle = (TextView)this.findViewById(R.id.txt_title);
			if(b.containsKey("title"))
			{
				tvTitle.setText(b.getString("title"));
			}
			else
			{
				tvTitle.setText(progr.getCurDayTitle());
			}
			tvTitle = (TextView)this.findViewById(R.id.txt_title2);
			if(b.containsKey("title2"))
			{
				tvTitle.setText(b.getString("title2"));
			}
			
			if(b.containsKey("day_title")) dayTitle = b.getString("day_title");
			
			ImageView icon = (ImageView)findViewById(R.id.title_icon);  
			if(b.containsKey("wk_mode"))
			{
				final int md = b.getInt("wk_mode");
				WorkoutMode wkMode = WorkoutMode.getModeFromId(md);
				switch(wkMode)
				{
				case Endurance:
					icon.setImageResource(R.drawable.endurance_gray);
					break;				
				case Cardio:
					icon.setImageResource(R.drawable.cardio);
					break;
				default:  //Bodybuilding:
					icon.setImageResource(R.drawable.weight_gray);
					break;
				}
				
			}
			
			if(b.containsKey("is_endurance") && b.getBoolean("is_endurance"))
			{
				icon.setImageResource(R.drawable.endurance_gray);
			}
			else
			{
				icon.setImageResource(R.drawable.weight_gray);
			}
			iscardio = (b.containsKey("cur_cardio"))? b.getBoolean("cur_cardio") : false;
			
			if(iscardio)
			{			
				displayCardio(false);					
			}
			else
			{			
				displayExercise();			
			}	
			View imgTick = (iscardio)? this.findViewById(R.id.img_ex_done_cardio) : this.findViewById(R.id.img_ex_done);
			if(b.containsKey("complete_visisble") && imgTick != null)
			{
				imgTick.setVisibility(b.getInt("complete_visisble"));
			}
		}
		else
		{
			//init
//			String s = progr.getCurDayTitle();		
//			if(s != null)
//			{
//				TextView tvTitle = (TextView)this.findViewById(R.id.txt_title);
//				tvTitle.setText(s);
//			}
			//display workout
			final Workout workout = progr.getWorkout();			
			if(workout==null)
			{
				this.setResult(Common.RESULT_ERROR);
				finish();
				return false;
			}
			Resources mng = this.getResources();	
			
			switch(workout.getMode())
			{
			case Endurance:
				dayTitle=mng.getString(R.string.but_endur);
				break;
			case Bodybuilding:
				dayTitle=mng.getString(R.string.but_mass);
				break;
			case Cardio:
				dayTitle=mng.getString(R.string.cardio);
				break;
			default:
				dayTitle=mng.getString(R.string.but_mass);
				break;
			}
		 
			//dayTitle = progr.getDayTypeStr();
			Exercise curEx = workout.getCurExercise();
			if(curEx == null)
			{
				this.setResult(Common.RESULT_ERROR);
				finish();
				return false;
			}	
			
			//icon
			ImageView icon = (ImageView)findViewById(R.id.title_icon);
			WorkoutMode md = workout.getMode();
			if(md == WorkoutMode.Endurance)
			{
				icon.setImageResource(R.drawable.endurance_gray);
			}
			else if(md == WorkoutMode.Cardio)
			{
				icon.setImageResource(R.drawable.cardio);
			}
			else
			{
				icon.setImageResource(R.drawable.weight_gray);
			}		
				
			
			if(curEx.getExType() == ExType.Cardio)
			{			
				displayCardio(true);	
				iscardio = true;
				
			}
			else
			{			
				displayExercise();			
			}	
		}
		
		
		showProgress(false);
		return iscardio;
	}	
	
	//display Weight lifting exercise
	private void displayExercise() {
		stopTimer();
		Workout workout = progr.getWorkout();
		if(workout == null)
		{
			goBackOnError();
			return;
		}
		
		Exercise curEx = workout.getCurExercise(); 
		ExData exData = curEx.getCurExData(gender);
		final int totalAlts = curEx.getTotalAlts(gender);
		final int curAltIndex = (totalAlts >0)? curEx.getCurAltIndex() : -1;
		// images  
		String imgName;
		if (gender == Gender.Female) {
			imgName = exData.getImgFemale();
			if (imgName == null)
				imgName = exData.getImgMale();
		} else {
			imgName = exData.getImgMale();
			if (imgName == null)
				imgName = exData.getImgFemale();
		}		
		
		ImageView imgEx = (ImageView)this.findViewById(R.id.eximg);		
		//get "old" bitmap		
		Bitmap bmp1 = null;
		Object tag = imgEx.getTag();
		if(tag != null)
		{
			try
			 {
				 bmp1 = ((BitmapDrawable)imgEx.getDrawable()).getBitmap();
			 }
			 catch(Exception ex)
			 {
				 bmp1 = null;
			 }		
		}
		 

		if (imgName == null) {
			//no image - TODO
			imgEx.setImageBitmap(null);
			imgEx.setTag(null);

		} else {
			Bitmap bm = getBitmapFromAssets(imgName, CurProgram.getProgrDir()+ File.separator);
			if(bm != null)
			{
				imgEx.setImageBitmap(bm);
				imgEx.setTag(Boolean.TRUE);
			}
			else{
				imgEx.setImageResource(R.drawable.generic_ex_img); // TEMP - TODO
				imgEx.setTag(null);
			}

		}
		
		 if(bmp1 != null)
		 {
			 bmp1.recycle();
		 }
		
		if(frWeels == null) this.initWheelFragment();
		//set wheels to values
		if(frWeels == null)
		{
			this.setResult(Common.RESULT_ERROR);
			finish();
		}
		
		//see if we have to check if we need actual - saved the last in range
		//we always save the last valid actual
		
		WorkoutMode md = workout.getMode();	
		int repsActual;
		int wtActual;
		
		ExType exType = exData.getExType();
		if(exType == ExType.Weights)
		{
			repsActual = exData.getRepsActual(md); ///////////////////////////
			wtActual = exData.getWtActual(md);
			frWeels.showWeightWheel(true);
		}
		else //bodyweight
		{
			repsActual=exData.getRepsActual(md);
			wtActual=0;
			frWeels.showWeightWheel(false);
		}		
				
		//weight - based on maxrep
//		final double wtRecommendedWeightRaw =curEx.getRecommendedWeight(md);
//		final int wtRecommendedWeightRounded = (int)wtRecommendedWeightRaw;
		
		
		final double curMaxRep = exData.getMaxRep();
		int[] weightRange = ExData.getRecommendedWeightRange(curMaxRep, md);
		
		final FitnessLevel adjustedFitLevel = User.getUser().getAdjuatedFitnessLevel();
		final int wtRecommendedWeightRounded = getRecWeightForUser(weightRange,adjustedFitLevel);
				
		//see if we need to show wtRecommendedWeight or actual
		//highlt - if we are on set 1 - display recommended based on saved maxrep
		//if set > 1 - if we have actual  weight / reps - calculate based on them 
		//and h-lite based on this maxrep
		
		
		final int curSet = curEx.getCurSet();
		if(exType == ExType.Weights)
		{
			if((curSet <1) || (wtActual<=0 || repsActual <=0))
			{
				//we are on first set - display based on maxrep			
				if(wtRecommendedWeightRounded >0)
				{
					adjustWeightRange(weightRange, wtRecommendedWeightRounded);
					
					frWeels.setWeight(wtRecommendedWeightRounded);
					frWeels.setWeigtHighlights(weightRange);
				}
				else
				{					
					frWeels.setWeight(0);
					frWeels.clearWeigtHighlights();
				}			
			}
			else if(wtActual >0 && repsActual >0)
			{	
				adjustWeightRange(weightRange, wtActual);
				//we are on set > 1 and were in range on prev. set - should display h-lt the same as on prev. set based on saved maxrep
				frWeels.setWeight(wtActual);
				frWeels.setWeigtHighlights(weightRange);
			}		
		}		
		
		//reps
		final int repsMax = workout.getMaxReps(exType);
		if(exType == ExType.BodyWeight)
		{		
			
			if(repsActual >0) frWeels.setReps(1, repsMax, repsActual-1);
			else frWeels.setReps(1, repsMax, 0);
			
			frWeels.clearRepsHighligts();  //no highligting
		}
		else
		{
			//see if we are on the first set
			if(curSet <1 || repsActual <=0  || wtActual <= 0)
			{
				//display average
				//reps  - average		
				int userReps = workout.getRepsForUser();
				if(userReps ==0) userReps =workout.getAverReps();
				if(repsMax >0)
				{
					frWeels.setReps(1, repsMax, userReps); 
				}
				else
				{
					frWeels.setReps(1, Workout.MAX_REPS, userReps);
				}
			}			
			else
			{
				frWeels.setReps(1, repsMax, repsActual-1);
			}
			
			//reps h-lites
			int[] repsRange = workout.getRepsRange();
			frWeels.setRepsHighlight(repsRange[0]-1, repsRange[1]-1); 
		}
						
		//exerc number
		final boolean completed = curEx.getCompleted();
		TextView tvNumber = (TextView)this.findViewById(R.id.txt_ex_number);
		tvNumber.setText(Integer.toString(workout.getCurIndex() +1));
		
		setCompletedImg(completed, false);	
		
		//arrows
		if(frExImg == null)
		{
			initImageFragment();
		}
		if(frExImg != null)
		{
			frExImg.showArrows((workout.getCurIndex() ==0)? false : true, 
					(curAltIndex >=0)? true : false, 
					(workout.isLast())? false : true, 
					(totalAlts >0 && curAltIndex < (totalAlts-1) )? true : false);
		}			
		
		//log
		//set # title
		int maxSets = curEx.getRecommendedSets();
		final int nextSet = curSet+1;
		if(maxSets <=1) maxSets = Workout.AVER_MAX_SETS;
		if(this.startMode == WorkoutMode.All)
		{
			if(nextSet <= maxSets) frWeels.setSetsTitle(maxSets, nextSet);
			else  frWeels.setSetsTitle("Completed");
		}
		else frWeels.setSetsTitle(maxSets, nextSet);	
		
		//see if we have log id
		long logId = exData.getLogId();
		if(logId >0)
		{
			//we have saved log data
			if(curSet < 0)
			{
				//TODO - error - we don't have set, but saved log...
				//for now: 
				workout.setCurSet(0);
				frWeels.clearLogDisplay();
			}
			else
			{
				//display saved log data 
				this.frWeels.displaySavedLog(curSet);    
			}
			
		}
		else
		{
			workout.setCurSet(0);
			frWeels.clearLogDisplay();
		}
			
		showExOrCardio(true, false);
		this.displayCommonData(exData);
		
	}// displayExercise
	
	private int getRecWeightForUser(final int[] weightRange, final FitnessLevel fl)
	{
		if(weightRange != null)
		{
			if(fl == FitnessLevel.High)
			{
				return weightRange[2];
			}
			else if(fl == FitnessLevel.Low)
			{
				return weightRange[0];
			}
			else
			{
				return weightRange[1];
			}
		}
		return -1;
	}
	
	private void displayCommonData(ExData exData)
	{
		//title
		TextView tvTitle = (TextView)this.findViewById(R.id.txt_title);
		TextView tvTitle2 = (TextView)this.findViewById(R.id.txt_title2);
		if(tvTitle != null)
		{
			tvTitle.setText(dayTitle);			
		}
		if(tvTitle2 != null)
		{
			tvTitle2.setText(exData.getTitle());
		}
				
		//description
		if(this.frDescription == null) this.initDescriptionFragment();
		if(this.frDescription != null)
		{
			this.frDescription.setDescription(exData.getDescription());
			frDescription.setTitle(exData.getTitle());
		}		
		
	}
	
	private void displayCardio(final boolean doinit)
	{
		if(this.frCardioTop == null) this.initCardioFragment();
		//init cardion fragment
		if(frCardioTop == null)
		{
			goBackOnError();
			return;
		}
		
		if(doinit)
		{
			Workout workout = progr.getWorkout();
			//workout.setCurSet(1);
			Exercise curEx = workout.getCurExercise();
			int m = curEx.getCardioMinutes(); //cardio minutes
			if(m <=0) m = 5;  //?? TODO
			
			frCardioTop.setTime(m*60, true, true);
			if(frCardioTop.isVisible())
			{
				frCardioTop.displayTimeOnUI(true);
			}
			frCardioTop.setStopButVisible(false);/////////////////////////////////////////////////////
					
			frCardioTop.showArrows((workout.getCurIndex() ==0)? false : true, 
					(workout.isLast())? false : true);
			this.displayCommonData(curEx.getExerciseData());
			
			if(frCardioBottom != null)
			{
				frCardioBottom.setSaveButtonVisible(false);
				frCardioBottom.setDistance(0);
			}
			showExOrCardio(false, false);
			setCompletedImg(curEx.getCompleted(), true);	
		}
		else
		{
			showExOrCardio(false, false);
		}	
	}
	
	//completed img
	private void setCompletedImg(final boolean completed, final boolean isCardio)
	{
		View imgTick = (isCardio)? this.findViewById(R.id.img_ex_done_cardio) : this.findViewById(R.id.img_ex_done);
		if(imgTick != null)
		{
			if(completed) imgTick.setVisibility(View.VISIBLE);
			else imgTick.setVisibility(View.GONE);
		}	
	}
	


//	@Override
//	public void onCloseClick() {
//		
//		Fragment frDescription = this.getSupportFragmentManager().findFragmentById(R.id.frag_descrip);		
//		if(frDescription != null && !frDescription.isHidden())
//		{
//			FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
//			tr.hide(frDescription);
//			tr.commit();
//		}		
//		
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector != null && gestureDetector.onTouchEvent(event))
		{				
			return true;
		}
		else
		{
			return false;
		}		
	}



	
	private void toggleDescription() {
		if(frDescription ==null) this.initDescriptionFragment();
		if(frDescription ==null) return;
		final boolean isDescHidden = frDescription.isHidden();
		
		this.showDescription(isDescHidden);	
			
	}
	
	//called from toggleDescruiption
		private void showDescription(final boolean show)
		{
			if(frDescription== null) this.initDescriptionFragment();			
			if(show)
			{
				if(frDescription.isHidden())
				{
					FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
					tr.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
					if(frWeels != null && frWeels.isVisible()) tr.hide(frWeels);
					if(this.frCardioBottom != null && frCardioBottom.isVisible()) tr.hide(frCardioBottom);
					tr.show(frDescription);			
					tr.commit();
				}		
			}
			else
			{				
				if (frDescription.isVisible()) {
					FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
					tr.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
					tr.hide(frDescription);
					//see if we are on Cardio
					if(this.frCardioTop != null && this.frCardioTop.isVisible() && this.frCardioBottom != null) tr.show(frCardioBottom);
					else if(frWeels != null) tr.show(frWeels);
					tr.commit();
				}
				
			}
						
			if(frExImg != null && frExImg.isVisible()) this.frExImg.toggleDescriptionButton(!show);
			if(this.frCardioTop != null && this.frCardioTop.isVisible()) frCardioTop.toggleDescriptionButton(!show);
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

	
	

///////////////   GESTURES /////////////////////////////
	@Override
	public void onSwipe(SwipeDirection dir) {
		
		if(dir == SwipeDirection.LEFT_SWIPE)
		{
			showNext(false);
		}
		else if(dir == SwipeDirection.RIGHT_SWIPE)
		{
			showPrev();
		}
		else if(dir == SwipeDirection.DOWN_SWIPE)
		{
			showUp();
		}
		else if(dir == SwipeDirection.UP_SWIPE)
		{
			showDown();
		}
	}

/////// Fragment methods
	//ExImgFragment
	@Override
	public void onDescriptionButClick() {
		toggleDescription();
		
	}


//////////// WHEEL Fragment - weight excersise 	
	public void onSaveLogClick(final int weight, final int reps) {
		
		if(startTimerOnSave) onClockClick();
		
//		Exercise.ExType exMode = progr.getCurExType();
		//to show message when weight is 0
//		if(weight <= Exercise.BODY_WEIGHT && exMode == Exercise.ExType.Weights)
//		{
//			//error - we need weight
//			this.showErrorDlg(R.string.err_save_log_weight, DLG_REASON_NOTIFY);
//			return;
//		}
		
		final int curSet = progr.getWorkout().getCurSet();
				
		new SaveLogTask().execute(Integer.valueOf(1), 
				Integer.valueOf(weight), Integer.valueOf(reps), Integer.valueOf(curSet+1));	
	}
	
	//ret _id from Workout_log table or -1
	private long doSaveLogCardio(final long time, final int dist, final int equipment)
	{
		DBAdapter dbAdapter = DBAdapter.getAdapter(this.getApplicationContext());
		Exercise exer = progr.getWorkout().getCurExercise();
		if(exer == null)
		{
			return -1;
		}
		WorkoutLog wkLog = new WorkoutLog(progr.getCurWorkoutId(), progr.getDayInfo(), progr.getCurExCompleted());	
		Exercise.ExType exMode = progr.getCurExType();
		wkLog.setLogType(exMode);// exerc mode
		final  WorkoutMode wkMode = progr.getWorkout().getMode();
		wkLog.setWorkoutMode(wkMode);
		
		wkLog.setEntryAndExerciseId(exer.getId(), exer.getCurExId(gender));
		
		wkLog.setDistance(dist);
		
		wkLog.setCardioEquipment(equipment);
		wkLog.setCompleted(true);
		exer.setCompleted(true);
		
		long dt = Common.getDateTimeForToday();

		wkLog.setDateTime(dt);
		wkLog.setTime(time);
		wkLog.setUnitsWeight(User.getUser().getIsWeightLbs());
		wkLog.setUnitsDist(User.getUser().getDistanceKm());
		
		long logId = exer.getLogId();
		if(logId >0)
		{
			wkLog.setId(logId);
		}
		else wkLog.setId(-1L);
		
		return dbAdapter.addLogData(wkLog);	
	}
	
	//Saving exer log and maxrep if needed
	private long doSaveLogWeight(final int wt, final int reps, final int curSet)
	{
		DBAdapter dbAdapter = DBAdapter.getAdapter(this.getApplicationContext());
		
		Exercise exer = progr.getWorkout().getCurExercise();
		if(exer == null) {
			return -1;
		}
		
		WorkoutLog wkLog = new WorkoutLog(progr.getCurWorkoutId(), progr.getDayInfo(), exer.getCompleted());	
		Exercise.ExType exType = exer.getExType(); // progr.getCurExType();
		wkLog.setLogType(exType);// exerc mode
		final  WorkoutMode wkMode = progr.getWorkout().getMode();
		wkLog.setWorkoutMode(wkMode);
				
		wkLog.setEntryAndExerciseId(exer.getId(), exer.getCurExId(gender));		
		
		wkLog.setWeight(wt);
		wkLog.setReps(reps);
		wkLog.setSet(curSet);
		wkLog.setUnitsWeight(User.getUser().getIsWeightLbs());
		
		long dt = Common.getDateTimeForToday();
//		if(dt ==0)
//		{
//			Log.d(TAG, "doSaveLogWeight - DAtetime not set");
//			return -1;
//		}		
		wkLog.setDateTime(dt);	
		
		if(exType == Exercise.ExType.Weights)
		{
			final int[] repsRange = progr.getWorkout().getRepsRange();
			final boolean repsNotInRange = (reps < (repsRange[0]+1) || reps > repsRange[1])? true : false;
			
			wkLog.setIsInRange(!repsNotInRange);				
			
			final double maxrep = Common.calculateMaxRep(wt, reps);	
			ExData exData = exer.getExerciseData();
			final double curMaxRep = exData.getMaxRep();	
			
			if(repsNotInRange) exData.setActual(0, 0, wkMode);				
			else exData.setActual(wt,reps, wkMode);
					
			if(maxrep > curMaxRep)
			{
				exData.setMaxrep(maxrep);
				wkLog.setMaxRep(maxrep);		
				
				if(Common.DEBUG) Log.d(TAG, "maxrep=" + maxrep);
			}				
			
			boolean updateWheels = true;			
			if(advanceOnExComplete) // && this.startMode == WorkoutMode.All)
			{
				int recSets = exer.getRecommendedSets();
				if(curSet >= recSets)
				{
					updateWheels=false;
				}
			}			
			
			if(updateWheels)
			{	
				final FitnessLevel adjustedFitLevel = User.getUser().getAdjuatedFitnessLevel();
				int[] weightRange = ExData.getRecommendedWeightRange(maxrep, wkMode);								
				int recWtRounded=getRecWeightForUser(weightRange,adjustedFitLevel);				
				//wew are still on the same screen
				//firts time - if in range - keep the same weight and h-light wheel
				if(repsNotInRange) 
				{		
					//get new values based on maxrep for this set
					//or we need to highligt if not h-lighted		
					setWeightReps(recWtRounded, (repsRange[0] + repsRange[1])/2, weightRange);  //? - should we show curent reps instead?		
					
				}
				else
				{
					//if recommended > upper range value or < low range value - update range
					adjustWeightRange(weightRange, wt);
					if(curMaxRep <=0)
					{					
						
						setWeightReps(wt, -1, weightRange);
					}
					else
					{
						//h-ligt weight wheel
						setWeightReps(-1,-1, weightRange);
					}
				}
				
				
//				else if(recWeight > wt)  //do not update - we are in range
//				{
//					setWeightReps(recWeight, -1);
//				}							
			}			
		}	
		else if(exType == Exercise.ExType.BodyWeight)
		{
			ExData exData = exer.getExerciseData();
			exData.setActual(wt,reps, wkMode);
			wkLog.setIsInRange(true);
			exData.setMaxrep(0);
			wkLog.setMaxRep(0);		
		}
		
		final int maxSets = exer.getRecommendedSets();
		
		if(curSet >= maxSets){			
			exer.setCompleted(true);
			wkLog.setCompleted(true);
		}
		wkLog.setUnitsDist(User.getUser().getDistanceKm());				
		return dbAdapter.addLogData(wkLog);		
	}
	
	private void adjustWeightRange(int[] weightRange, final int wt)
	{
		if(weightRange != null)
		{
			if(wt > weightRange[2])
			{
				weightRange[2] = wt;
			}
			else if(wt < weightRange[0])
			{
				weightRange[0]=wt;
			}
		}
	}
	
	private void setWeightReps(final int wt, final int rep, final int[] weightHighlightRange)
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				if(frWeels != null)
				{
					if(wt >=0){
						frWeels.setWeight(wt);
					}
					if(weightHighlightRange != null)
					{
						frWeels.setWeigtHighlights(weightHighlightRange);
					}
					else
					{
						frWeels.clearWeigtHighlights();
					}
					if(rep >0)
					{
						frWeels.setCurReps(rep);
					}
				}
				
			}
			
		});
	}
	
	//////////////// THREADS ///////////////////////	
	
	private class ActivityInitTask extends AsyncTask<Bundle, Void, Bundle>
	{
		protected void onPreExecute() { 
			showProgress(true);
	   }  

		@Override
		protected Bundle doInBackground(Bundle... params) {
			doInitOnTask();
			if(params == null || params.length ==0) return null;
			return params[0];
		}

		@Override
		protected void onPostExecute(Bundle result) {
			ExerciseActivityPhone.this.finishInit(result);
			showProgress(false);
		}
		
		
		
	}
	
	//Cardio DONE button clicked - save log entry marked as Complete
	//in - time, distance, equipment
	private class CardioDoneClickTask extends AsyncTask<Long, Void, long[]>
	{		

		@Override
		protected void onPreExecute() {
			showProgress(true);
		}

		@Override
		protected long[] doInBackground(Long... args) {
			if(args == null || args.length <3)
			{
				return null;
			}
			
			long tm;
			int dist, equipment;
			try
			{
				tm = args[0].longValue();
				dist = (int)(args[1].longValue());
				equipment = (int)(args[2].longValue());
			}
			catch(Exception ex)
			{
				return null;
			}
			long[] res = new long[1];
			res[0] = doSaveLogCardio(tm, dist, equipment);
			return res;
		}
		
		@Override
		protected void onPostExecute(long[] result) {
			showProgress(false);
			ExerciseActivityPhone.this.processRetFromCardioDoneClick((result != null && result.length>0)? result[0] : -1);
		}
		
	}
	
	//send in - weigt, reps, curset, cardio equipment
	private class SaveLogTask extends AsyncTask<Integer, Void, long[]>
	{
		// can use UI thread here
		protected void onPreExecute() { 
				showProgress(true);
		 }  

		//params - for weight: exType=1, weight, reps, curSet
		//for cardio - exType=2; secs, dist-meters
		@Override
		protected long[] doInBackground(Integer... args) {			
			long[] res = new long[3]; 
			
			if(args == null || args.length <4)
			{
				res[0]=0;
				return res;
			}
			
			int wt_or_mins=0, reps_or_dist=0, curSet=0, exType=1, cardio_equipment=-1;
			try
			{
				exType = args[0].intValue(); //Integer.parseInt(args[0]);
				wt_or_mins = args[1].intValue(); //Integer.parseInt(args[1]);
				reps_or_dist = args[2].intValue(); //Integer.parseInt(args[2]);
				if(args[3] != null) curSet = args[3].intValue(); //Integer.parseInt(args[3]);
				if(args.length >= 5 && args[4] != null) cardio_equipment=args[4].intValue(); //Integer.parseInt(args[4]);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();	
				wt_or_mins =0;
				reps_or_dist=0;
				curSet=0;
			}
			if(exType==1 && (wt_or_mins < 0 || reps_or_dist <=0))
			{
				res[0]=0;
				return res;
			}
			res[0]= (exType==1)? doSaveLogWeight(wt_or_mins, reps_or_dist, curSet) : doSaveLogCardio(wt_or_mins,reps_or_dist,cardio_equipment);  // log Id or -1
			res[1] = curSet;
			res[2] = exType;
			return res;
		}
		
		protected void onPostExecute(final long[] result) 
		{
			showProgress(false);
			ExerciseActivityPhone.this.processLogSaveResult(result[0], (int)result[1], (result[2]==1)? false : true);
		}
		
	}
	
	
	private class SetWorkoutCompleteTask extends AsyncTask<Boolean, Void, Boolean[]>
	{
		// can use UI thread here
		protected void onPreExecute() { 
				showProgress(true);
		 }
		
		@Override
		protected Boolean[] doInBackground(Boolean... params) {
			
			Boolean[] res = new Boolean[2];
			res[0]= params[0];
			
			if(setAllDayComplete(params[0].booleanValue())) res[1]= Boolean.TRUE;
			else res[1]= Boolean.FALSE;
			
			return res;
		}
		
		protected void onPostExecute(final Boolean[] res) 
		{
			showProgress(false);
			ExerciseActivityPhone.this.processSetComplete(res[0].booleanValue(), res[1].booleanValue());
		}

		
		
	}
	
//	private class InitTask extends AsyncTask<String, Void, long[]>
//	{
//		// can use UI thread here
//		 protected void onPreExecute() { 
//			showProgress(true);
//		  }  
//
//
//		@Override
//		protected long[] doInBackground(String... arg0) {
//			long[] result = new long[1];
//			
//			
//			if(!initWorkout())
//			{
//				result[0] = 0;
//				return result;
//			}
//			displayUi();
//			result[0] = 1;
//			return result;
//		}
//		
//		protected void onPostExecute(final long[] result) 
//		{
//			showProgress(false);
//			ExerciseActivityPhone.this.processInitResult((result[0] == 1)? true : false);
//		}
//	}

	@Override
	public void onActivityAttached() { }
	
	/////////////// NAVIGATION /////////////////////////////////////
	@Override
	public boolean showPrev()
	{
		Workout workout = progr.getWorkout();
		if(workout == null) return false;		
		
		if(workout.getCurExercise().getExType()== ExType.Cardio)
		{
			if(frCardioTop != null && frCardioTop.isTimerOn())
			{
				showCardioOnMsg();
				return true; //we handled it
			}
		}		
		
		final int cutInd = workout.getCurIndex();
		if(cutInd >0)
		{
			workout.setCurIndex(cutInd -1, true);	
			if(workout.getCurExercise().getExType()== ExType.Cardio) displayCardio(true);
			else displayExercise();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean showNext(final boolean skipCompleted)
	{
		Workout workout = progr.getWorkout();
		if(workout == null ) return false;		//|| workout.isLast()
		if(workout.getCurExercise().getExType()== ExType.Cardio)
		{
			if(frCardioTop != null && frCardioTop.isTimerOn())
			{
				showCardioOnMsg();
				return true; //we handled it
			}
		}		
		
		final int curInd = workout.getCurIndex();		
		int nextInd = -1;		
		final int total = workout.getExerciseCount();
		
		
		
		if(advanceOnExComplete && skipCompleted)
		{			
			for(int i= curInd +1; i < total; i++)
			{
				if(!workout.getExCompleted(i)){
					nextInd =i;
					break;
				}
			}
			if(nextInd <0)
			{
				//start from beginning 
				for(int i= curInd-1; i >=0; i--)
				{
					if(!workout.getExCompleted(i)){
						nextInd =i;
						break;
					}
				}
				if(nextInd <0)
				{
					//we are done
					return false;					
				}
			}
		}
		else
		{
			nextInd = curInd +1;
			if(nextInd >= total) return false;
		}		
		workout.setCurIndex(nextInd, true);
		if(workout.getCurExercise().getExType()== ExType.Cardio) displayCardio(true);
		else displayExercise();	
		
		return true;
	}
	
	//called from Cardio fragment - use clicked Next button
	@Override
	public void showNextCardio()
	{
		showNext(false);
	}
	
	//called from Cardio fragment - use clicked prev button
	@Override
	public void showPrevCardio()
	{
		showPrev();
	}
	
	private void showCardioOnMsg()
	{
		this.showMsgDlg(R.string.msg_cardio_on, DLG_REASON_CARDIOON);
	}
	
	@Override
	public void showUp()
	{
		//this is only for weight, not for cardio
		Workout workout = progr.getWorkout();
		if(workout == null) return;
		if(workout.setPrevAlt(gender))
		{
			displayExercise();
		}
		
	}
	@Override
	public void showDown()
	{
		//this is only for weight, not for cardio
		Workout workout = progr.getWorkout();
		if(workout == null) return;
		if(workout.setNextAlt(gender))
		{
			displayExercise();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		switch(requestCode)
		{
		case Common.ACT_LogListDlgActivity:
			//re-load logs 
			if(data != null)
			{
				int setsDone = data.getIntExtra("num_sets", 0);
				Workout workout = progr.getWorkout();
				if(setsDone <=0)
				{
					//TODO - error - we don't have set, but saved log...
					//for now: 
					workout.setCurSet(0);
					if(frWeels != null) frWeels.clearLogDisplay();
				}
				else
				{
					workout.setCurSet(setsDone);
					//display saved log data 
					this.frWeels.displaySavedLog(setsDone);
					
				}
				Exercise curEx = workout.getCurExercise();
				final int sets = curEx.getRecommendedSets();
				if(sets > 1)
				{
					frWeels.setSetsTitle(sets, setsDone+1);
				}
				else
				{
					frWeels.setSetsTitle(Workout.AVER_MAX_SETS, setsDone+1);
				}
				if(setsDone < sets)
				{
					curEx.setCompleted(false);
					setCompletedImg(false, false);	
					
				}
			}
			break;
		case Common.ACT_CardioZonesActivity:
			if(resultCode == Common.RESULT_OK && data != null) 
			{
				String txt = data.getStringExtra("hr");
				if(txt == null) return;
				if(frCardioTop != null)// && frCardioTop.isVisible())
				{
					frCardioTop.setCardioText("Heart rate: "+txt + " bpm");
				}				
			}
			break;
		default: super.onActivityResult(requestCode, resultCode, data);
		break;
		}
		
	}
	
	@Override
	protected void resetSettings() {
		DataStore ds = new DataStore(this);
		timerInterval = ds.getRestTime();
		startTimerOnSave = ds.getStartTimerOnSave();
		advanceOnExComplete = ds.getAdvanceOnComplete();
		if(this.frExImg != null)
		{
			this.frExImg.setTimeInterval(timerInterval);
		}
		final boolean playSound = ds.getPlaySoundOnTimeUp();
		frExImg.setPlaySoundOnTimeUp(playSound); 
		frCardioTop.setPlaySoundOnTimeUp(playSound);
		
		Common.setKeepScreenOn(this, ds.getKeepAwake());		
	}
	
	
	//we changed intervals on the wheel
	@Override
	protected void resetWeightWheel() {
		if(this.frWeels != null)
		{
			frWeels.resetWeightAdapter();
			if(frWeels.isVisible())
			{
				Workout workout = progr.getWorkout();
				if(workout != null)
				{
					WorkoutMode md = workout.getMode();	
					int repsActual;
					int wtActual;
					ExData exData = workout.getCurExData(User.getUser().getGender());
					if(exData.getExType() == ExType.Weights)
					{
						repsActual = exData.getRepsActual(md); ///////////////////////////
						wtActual = exData.getWtActual(md);
					}
					else
					{
						repsActual=0;
						wtActual=0;
					}						
					if(repsActual >0 && wtActual >0)
					{
						frWeels.setWeight(wtActual); ////////////////////////////////////////
					}		
					else 
					{
						final double recommendedWt =workout.getCurExercise().getRecommendedWeight(md);						
						if(recommendedWt >0) frWeels.setWeight((int)recommendedWt);
					}				
					
				}
			}	
		}
	}

	//we deleted logs - reset current view
	@Override
	protected void resetView() {
		final Workout workout = progr.getWorkout();	
		Exercise curEx = workout.getCurExercise();
		final ExType extp= curEx.getExType();
		final boolean isCardio = (extp == ExType.Cardio)? true : false;
		if(!isCardio)
		{
			int maxSets = curEx.getRecommendedSets();
			frWeels.setSetsTitle(maxSets, 1);
			frWeels.clearLogDisplay();
			if(extp==ExType.Weights)
			{
				if(workout.getCurExercise().getExerciseData().getMaxRep() <0.1)
				{
					frWeels.setWeight(0);
					frWeels.clearWeigtHighlights();
				}
			}
		}
		setCompletedImg(false, isCardio);
	}

	////////////////// BOTTOM MENU ////////////////////////////////			


	public void onClockClick() {
		// start time if not started
		if(this.frExImg == null) this.initImageFragment();
		if(frExImg == null) return;
		
		if(!frExImg.isTimerOn())
		{
			frExImg.startTimer();
		}
		
	}
	
	//Log edit - show dialog with list of log entries allowing deleting 
	public void onEditLogClick(View view)
	{
		User user = User.getUser();
		Gender g = user.getGender();
		long logId =  progr.getWorkout().getCurExData(g).getLogId();
		if(logId <=0)
		{
			//this.showErrorDlg(R.string.err_reading_data, false);
			return;
		}
		//display dialog with Log Data List 
		Intent intent = new Intent(this, LogListDlgActivity.class);			
		intent.putExtra("logid", logId);
		startActivityForResult(intent, Common.ACT_LogListDlgActivity);
	}

	@Override
	public int getId() {		
		return Common.ACT_ExerciseActivityPhone;
	}

//////////// CARDIO 
	@Override
	public void onCardioDoneClick() {
		showDescription(false);		
		//check if time completed
		if(frCardioTop.getTime() >0)
		{
			this.showYesNoDlg(R.string.msg_time_notcomplete, R.string.done,
					R.string.contin, DLG_REASON_CARDIO_TIME_NOT_COMPLETE);
		}
		else
		{
			doCardioDone();
		}			
		
	}
	
	private void doCardioDone()
	{
		//set exercise as done 
				final long time = frCardioTop.getCardioTime();
				final long dist = frCardioBottom.getDistance(); 		
				final long equipment = this.frCardioBottom.getSelEquipmentIndex();

				new CardioDoneClickTask().execute(new Long[]{Long.valueOf(time),
							Long.valueOf(dist), Long.valueOf(equipment)});	
	}
	
	private void processRetFromCardioDoneClick(final long res)
	{
		if(res <0)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
			return;
		}
		Workout workout = progr.getWorkout();
		if(workout != null)
		{
			Exercise exer = workout.getCurExercise();
			exer.setCompleted(true);
			exer.setLogId(res); 
		}	
		
		if(this.frCardioBottom != null)
		{
			frCardioBottom.setSaveButtonVisible(true);
		}
		setCompletedImg(true, true);
	}
	
	@Override
	public void onCardioSaveClick() {
		
		if(this.frCardioTop == null)
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
			return;
		}
		final long time = frCardioTop.getCardioTime();
		// display next exercise or finish workout
		//save log - secs we run		
		//int dist_m=0;
		//get distance
		int dist = frCardioBottom.getDistance(); 		
		final int equipment = this.frCardioBottom.getSelEquipmentIndex();
//				new SaveLogTask().execute("2", Long.toString(time), Integer.toString(dist_m), null, 
//						(equipment>=0)? Integer.toString(equipment) : "-1");
		
		new SaveLogTask().execute(Integer.valueOf(2),  Integer.valueOf((int)time), 
				Integer.valueOf(dist), null, 
				(equipment>=0)? Integer.valueOf(equipment) : Integer.valueOf(-1));
		
		//reset cardio fragments
		frCardioBottom.setDistance(0);		
		
	}


	@Override
	public void onShowCardioZonesClick() {
		Intent intent = new Intent(this, CardioZonesActivity.class);		
		startActivityForResult(intent, Common.ACT_CardioZonesActivity);
		
	}


	@Override
	public void onDescriptionCardioButClick() {
		this.toggleDescription();
		
	}

///closing
	
	private void checkCompleteAndFinish(final boolean allowCancel)
	{
		final boolean done = progr.getCurWorkoutCompleted();
		if(startMode == WorkoutMode.All)
		{
			//we need to see if we completed the dayOfMonth and prompt the user to complete or not
			
			if(!done)
			{
				if(allowCancel)
				{
					//prompt the user if he wants to complete the dayOfMonth
					this.showYesNoCancelDlg(R.string.msg_finish, R.string.complete_workout, 
							R.string.not_complete_workout, R.string.cancel_but, DLG_REASON_CHECK_WK_COMPLETE);
					
				}
				else
				{
					//prompt the user if he wants to complete the dayOfMonth
					this.showYesNoDlg(R.string.msg_finish, R.string.complete, R.string.not_complete, 
							DLG_REASON_CHECK_WK_COMPLETE);
				}
				
			}
			else
			{
				finishWorkout(true);
			}		
		}
		else {	
			if(done)
			{
				processSetComplete(true, true);
			}
			else
			{
				//ask
				this.showYesNoDlg(R.string.msg_finish_module, R.string.close, R.string.cancel_but, DLG_REASON_CHECK_WK_COMPLETE);
				
			}
			
		}
		
	}
	
	private boolean setAllDayComplete(final boolean isComplete)
	{
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		
		return adapter.setDayComplete(isComplete, progr.getId(), progr.gettWeek(), progr.getDayOrder(), Common.getDateTimeForToday());
	}
	
	private void processSetComplete(final boolean isComplete, final boolean res)
	{
		if(res)
		{
			if(isComplete && startMode ==WorkoutMode.All)
			{
				this.showMsgDlg(R.string.msg_wk_complete, DLG_REASON_CHECK_WK_COMPLETE);
			}
			else
			{
				showHomeScreen(isComplete);
			}
						
		}
		else
		{
			this.showErrorDlg(R.string.err_completing_workout, DLG_REASON_NOTIFY);
		}
	}
	
	


	@Override
	public void processMsgDlgClosed(int reason) {
		if(reason == DLG_REASON_CHECK_WK_COMPLETE)
		{
			showHomeScreen(true);
		}
		else if(reason == DLG_REASON_CARDIOON)
		{
			//do nothing - we just notified the user
		}
	}
	
	private void showHomeScreen(final boolean isComplete)
	{
		Intent intent = this.getIntent();		
		intent.putExtra("workout_mode", startMode.getId());
		intent.putExtra("completed", isComplete);
		setResult(Common.RESULT_OK, intent);
		finish();		
	}

	@Override
	public void processDlgNo(final int reason) {
		super.processDlgNo(reason);
		if(reason == DLG_REASON_CHECK_WK_COMPLETE)
		{
			//we are quitting without completing workout
			if(startMode == WorkoutMode.All) finishWorkout(false);
			//else - do nothing
		}	
		else if(reason == DLG_REASON_CARDIO_TIME_NOT_COMPLETE)
		{
			//re-sart timer
			this.frCardioTop.doStartButClick();
		}
	}


	@Override
	public void processDlgYes(final int reason) {
		super.processDlgYes(reason);
		if(reason == DLG_REASON_CHECK_WK_COMPLETE)
		{
			//we are setting workout Complete
			if(startMode == WorkoutMode.All) finishWorkout(true);
			else
			{
				processSetComplete(true, true);
			}
		}
		else if(reason == DLG_REASON_CARDIO_TIME_NOT_COMPLETE)
		{
			doCardioDone();
		}
	}
	
			
	@Override
	public void processErrDlgClose(final int reason) {		
		if(reason == DLG_REASON_NOTIFY)
		{
			return;
		}		
		super.processErrDlgClose(reason); //finish with error
	}


	private void finishWorkout(final boolean isComplete)
	{
		new SetWorkoutCompleteTask().execute(Boolean.valueOf(isComplete));		
	}


	@Override
	public void onBackPressed() {
		checkCompleteAndFinish(true);
	}


	@Override
	public void onCardioEquipmentSelected(int selInd) {
		if(this.frCardioTop != null)
		{
			frCardioTop.setCadioEquipmentImg(selInd);
		}
		
	}

	@Override
	public void onDescriptionClose() {
		toggleDescription();		
	}

	
		
}
