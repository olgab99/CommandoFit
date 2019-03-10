package com.olmatech.fitness.main;

import com.olmatech.fitness.main.Workout.WorkoutMode;

public class DayData
{
	public int dayOfMonth=0;// of month
	public int numOfLogs=0;	
	public int weekId;
	public String blockTxt;
	public int dayOrder=Common.EMPTY_VALUE;  //days are in order in program
	public int workoutId=Common.EMPTY_VALUE;
	public WorkoutMode workoutMode = WorkoutMode.Unknown;
			
	public DayData(){}
	public DayData(final int d){ dayOfMonth=d; }
}