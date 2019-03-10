package com.olmatech.fitness.main;

import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout.WorkoutMode;

public final class CurProgram {
	
	private static CurProgram refProgram;
	private int programId=1;  //setting for now
	//private int curWorkoutId=1;
	private Gender gender;
	private User user;
	
	//properties
	private Workout workout;
	private int week =Common.ZERO_ID; //week id
	private int day=Common.ZERO_ID;  //dayId
	private int dayOrder=-1;
	private int weekOrder=-1;
	//private int curExerciseOrder=1;
	private final static String progrDir="progr1"; // for now
	
	public final static int PROGR_COMPLETE_DAYORDER=-5; //flag indic. completion of program
		
	private CurProgram()
	{
		user = User.getUser();
		gender = user.getGender();
	}
	
	public static CurProgram getProgram()
	{
		if(refProgram == null)
		{
			refProgram = new CurProgram();
		}
		return refProgram;
	}
	
	public void resetProgram()
	{
		workout = null;
		week =Common.ZERO_ID;
		day=Common.ZERO_ID;
		dayOrder=1;
		weekOrder=1;
		
	}
	
	
	//public DayInfo (final int progId, final int weekInd, final int dayInd, final int wkId,  final int curExInd)
	public DayInfo getDayInfo()
	{
		return new DayInfo (programId, week, day, (workout != null)? workout.getId() : Common.EMPTY_VALUE,  
				(workout != null)? workout.getCurIndex() : Common.EMPTY_VALUE, 
				(workout != null)? workout.getMode() : WorkoutMode.Unknown);
	}
	
	//ret. title for exercise workout  screen
	public String getCurDayTitle()
	{
		if(workout == null) return null;
//		WorkoutMode md = workout.getMode();
//		String t = md.getTitle();
		return workout.getMode().getTitle() + ": Block " + week + " dayOfMonth " + day; 
	}
	
	public String getDayTypeStr()
	{
		if(workout == null) return null;
		return workout.getMode().getTitle();
	}
	
	public void setCurWorkoutMode(final WorkoutMode md)
	{
		if(workout != null) workout.setMode(md);
	}
	
	public boolean getCurWorkoutCompleted()
	{
		return (workout != null)? workout.getWorkoutCompleted() : false;
	}
	public Workout getWorkout()
	{		
		return workout;
	}
	
	public void clearWorkout()
	{
		workout = null;		
	}
	
	public boolean haveWorkout()
	{
		return (workout==null)? false : true;
	}
	
	public void setCurExLogId(final long logId, Gender g)
	{
		if(workout != null)
		{
			workout.getCurExData(g).setLogId(logId);
		}
	}
	
	public WorkoutMode getCurWorkoutMode()
	{
		if(workout == null) return WorkoutMode.Unknown;
		return workout.getMode();
	}
	
	public void setWeek(final int val)
	{
		week = val;
	}
	
	public int gettWeek()
	{
		return week;
	}
	
	public void setDay(final int val)
	{
		day = val;
	}
	
	public int getDay()
	{
		return day;
	}
	
	public void setDayOrder(final int val){ dayOrder=val; }
	public int getDayOrder(){ return dayOrder; }
	public void setWeekOrder(final int val){ weekOrder=val; }
	public int getWeekOrder(){ return weekOrder; }
	
	/**
	 * @param id
	 */
	public void setCurWorkoutId(final int id, final boolean clearWorkout)
	{
		if(workout == null || clearWorkout) workout = new Workout(id);
		else workout.setId(id);
	}
	
	//workout
	public int getCurWorkoutId()
	{
		return (workout != null)? workout.getId() : -1;
	}
	
//	public long getCurWorkoutDateTime()
//	{
//		return (workout != null)? workout.getDateTime() : 0;
//	}
	
	public User.Gender getGender()
	{
		return gender;
	}
	
//	public void setProgrDir(final String dir)
//	{
//		progrDir =dir;
//	}
	
	public static String getProgrDir()
	{
		return progrDir;
	}
	
	public int getId()
	{
		return programId;
	}
	
	public void setId(final int id)
	{
		programId = id;
	}	
	
	public void setCurExerciseIndex(final int ind, final boolean clearData)
	{
		if(workout != null) workout.setCurIndex(ind, clearData);
	}
	
	public int getCurExerciseInd()
	{
		return (workout != null)? workout.getCurIndex() : -1;
	}
	
	public boolean getCurExCompleted()
	{
		return (workout != null)? workout.getCurExCompleted() : false;
	}
	
	public ExType getCurExType()
	{
		return (workout != null)? workout.getCurExType() : ExType.Unknown;
	}
	
	/**
	 * 
	 * @return id of exercise that we are doing
	 */
	public int getCurExId(Gender g)
	{
		if(workout==null) return -1;
		return workout.getCurExId(g);
	}
	
	public void resetAllLogs()
	{
		if(workout==null) return;
		workout.resetAllLogs();
	}
	
	public void clearRecommended()
	{
		if(workout==null) return;
		workout.clearRecommended();
	}

}
