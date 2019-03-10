package com.olmatech.fitness.interfaces;

import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.Workout.WorkoutMode;

public interface IProcessLoaded
{
	public void onFragmentLoadFinished(final int id);
	//public void onWorkoutListItemClick(final int workoutId);
	public void onExerciseListItemClick(final Object tag, final int gragmentId);
	public void onListSelectedItemIdSet(final int selecetdId, final int gragmentId);
	public void onStartButtonClick(final int gragmentId, final Object data);	
	public void onViewButtonClick(final int gragmentId, final Object data);
	public void onSetComplete(final Object tag, final boolean isComplete, final int gragmentId);
	public void processShowDayWorkout(final int workoutId, final int weekId, final int dayId,
 			final String title, final WorkoutMode md);
	public void processDoDayWorkout(final int workoutId,final String title, final WorkoutMode md);
	public void onSelectedItem(final int selItem);		
	public void onSetDayCurrent(final DayInfo dayInfo);
}
