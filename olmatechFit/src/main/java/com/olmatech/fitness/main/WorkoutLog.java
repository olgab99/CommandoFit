package com.olmatech.fitness.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.Workout.WorkoutMode;

//to hold WorkoutLog data for updates, inserts
//dayOfMonth index and week index combo ID for the given program / workout
//if we already have a log for this dayOfMonth - we will continue to add to it
public class WorkoutLog {
	
	private long _id;
	private int user_id=1;
	private long datetime=0;
	private DayInfo dayInfo;
	private int entry_id;
	private boolean isCompleted;
	private int exercise_id; // exercise we really did (if we did alternative)
	
	//log data
	private ExType logType = ExType.Unknown;
	//Weight log
	private int set=0;
	private int reps=0;
	private int weight=0;
	private boolean isInRange=true; // true if the weigt / reps are in range
	
	//Cardio log
	private long time=0;  //mins
	private int distance=0;  //meters
	private int cardio_equipment=-1; // unknown
	//tactical log
	private int laps=0;
	
	//maxrep or -1
	private double maxrepsweight=-1.0;
	
	private WorkoutMode wkMode = WorkoutMode.Unknown;
	
	private boolean isWeightLbs= true;
	private boolean isDistanceKm = true;
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("EE MMM d, yyyy");
	
	public WorkoutLog(final int wkout_id){ 
		dayInfo = new DayInfo();
		dayInfo.workoutId=wkout_id; 
	}
	
	public WorkoutLog(final int wkout_id, final int progrId, final int weekId, final int dayId, final boolean isCompl)
	{
		this(wkout_id);
		dayInfo.progrId= progrId;
		dayInfo.weekId= weekId;
		dayInfo.dayId= dayId;
		isCompleted = isCompl;
		
	}
	
	public WorkoutLog(final int wkout_id, final DayInfo di, final boolean isCompl)
	{
		this(wkout_id);
		dayInfo.progrId= di.progrId;
		dayInfo.weekId= di.weekId;
		dayInfo.dayId= di.dayId;
		isCompleted = isCompl;
	}
	
	public void setUnitsWeight(final boolean lb){isWeightLbs= lb; }
	public boolean getUnitsLbs(){ return isWeightLbs; }
	public void setUnitsDist(final boolean km){ this.isDistanceKm=km;	}
	public boolean getUnitsKm(){ return isDistanceKm; }
	
	
	public void setId(final long val)
	{
		_id=val;
	}
	
	public long getId(){ return _id; }
	
	public void setUserId(final int val) { 	user_id=val; }
	
	public int getUserId(){ return user_id;}
	
	public void setDateTime(final long val) { datetime=val; }
	
	//Datetime
//	public void setDateTimeForToday()
//	{
//		datetime = Common.getDateTimeForToday();
//		
//	}	
	public long getDatetime(){ return datetime; }
	
	public String getFormatetdDate()
	{
		if(datetime <=0) return null;
//		Calendar cal = new GregorianCalendar();
//		cal.setTimeInMillis(datetime);
		
		Date d = new Date(datetime);
		return sdf.format(d);
		
	}
	
	public void setProgrId(final int val){ dayInfo.progrId=val; }
	
	public int getProgrId(){ return dayInfo.progrId; }
	
	public void setWeekId(final int val) { dayInfo.weekId=val; }
	
	public int getWeekId() { return dayInfo.weekId; }
	
	public void setDayId(final int val) { dayInfo.dayId=val; }
	
	public int getDayId() { return dayInfo.dayId; }
	
	public void setWorkoutId(final int val) { dayInfo.workoutId=val; }
	
	public int getWorkoutId() { return dayInfo.workoutId; }
	
	public void setEntryAndExerciseId(final int entryId, final int exId) { entry_id=entryId; exercise_id=exId; }	
	public int getEntryId(){ return entry_id; }
	
	//what they actually did
	//public void setExerciseId(final int val){ exercise_id=val; }	
	public int getExerciseId(){ return exercise_id; }
	
	public void setCompleted(final boolean val) { isCompleted = val; }
	
	public boolean getCompleted() { return isCompleted; }
	
	public DayInfo getDayInfo(){ return dayInfo; }
	
	public void setLogType(ExType exType){ logType=exType; }
	
	public ExType getLogType(){ return logType; }
	
	public void setSet(final int val){ set=val; }
	
	public int getSet(){ return set; }
	
	public void setReps(final int val){ reps = val;}
	
	public int getReps(){ return reps; }
	
	public void setWeight(final int val){ weight = val; }
	
	public int getWeigt(){ return weight; }
	
	public void setTime(final long time2){ time = time2; }
	
	public long getTime(){ return time; }
	
	public void setLaps(final int val){ laps=val; }
	
	public int getLaps(){ return laps; }
	
	public void setWorkoutMode(final WorkoutMode m) { wkMode = m; }
	
	public WorkoutMode getWorkoutMode(){ return wkMode; }
	
	public void setMaxRep(final double val){ maxrepsweight=val; }
	
	public double getMaxRep(){ return maxrepsweight; }
	
	public int getDistance(){ return distance; }
	
	public void setDistance(final int d){ distance =d; }
	
	public void setCardioEquipment(final int val) { cardio_equipment = val; }
	
	public int getCardioEquipment(){ return cardio_equipment; }
	
	public void setIsInRange(final boolean val){ isInRange= val; }
	
	public boolean getIsInRange(){ return isInRange; }
				
}
	
	


