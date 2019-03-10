package com.olmatech.fitness.main;

import com.olmatech.fitness.main.Workout.WorkoutMode;

public class DayInfo {
	public int weekId=Common.ZERO_ID;
	public int dayId = Common.ZERO_ID;
	public int workoutId=Common.EMPTY_VALUE;
	public int progrId=Common.EMPTY_VALUE;
	public int curExOrder=Common.EMPTY_VALUE; //exercise orde - starting at 1
	public WorkoutMode workoutMode = WorkoutMode.Unknown;
	public int dayOrder;
	public DayInfo(){}
	
	public DayInfo (final int progId, final int weekInd, final int dayInd, final int wkId,  final int curExInd, WorkoutMode md)
	{
		weekId = weekInd;
		dayId = dayInd;
		workoutId = wkId;
		progrId = progId;
		curExOrder = curExInd;
		workoutMode = md;
	}
	
	public DayInfo(final DayInfo dayInfo)
	{
		weekId = dayInfo.weekId;
		dayId = dayInfo.dayId;
		workoutId = dayInfo.workoutId;
		progrId = dayInfo.progrId;
		curExOrder = dayInfo.curExOrder;
		workoutMode = dayInfo.workoutMode;
		dayOrder = dayInfo.dayOrder;
	}
	
	public boolean isInit()
	{
		if(weekId < 0 || dayId < 0 || workoutId < 0 || progrId < 0 || curExOrder < 0) return false;
		return true;
	}

}
