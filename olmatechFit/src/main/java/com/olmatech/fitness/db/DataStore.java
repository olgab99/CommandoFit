package com.olmatech.fitness.db;

import com.olmatech.fitness.main.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataStore {
	private Context context;
	private final static String STORE_NAME = "FIT";
	
		
	public DataStore(Context cnt)
	{
		context = cnt;
	}
	
	//Licensing
		public void setTimesUsed(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("times_used", val);
			editor.commit();
		}
		
		public int getTimesUsed()
		{	
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("times_used", 0);
			
		}
		
		public void setLicensed()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("haveit", true);
			editor.commit();
		}
		
		public boolean getIsLicensed()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("haveit", false);
		}
		
		public void setIsTablet(final boolean val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("istablet_set", true);
			editor.putBoolean("istablet_val", val);
			editor.commit();		
		}
		
		public boolean getIsTabletSet()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("istablet_set", false);
		}
		
		public boolean getIsTablet()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("istablet_val", false);
		}	
		
		//Program info
		public void setCurProgrId(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("program_id", val);
			editor.commit();
		}
		
		public int getCurProgramId()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("program_id", Common.EMPTY_VALUE);
		}
		
		public void setCurWeekId(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("week_index", val);
			editor.commit();
		}
		
		public int getCurWeekId()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("week_index", Common.EMPTY_VALUE);
		}
		
		public int getCurDayId()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("day_index", Common.EMPTY_VALUE);
		}
		
		public void setCurDayId(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("day_index", val);
			editor.commit();
		}
		
		public int getCurDayOrder()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("day_order", Common.EMPTY_VALUE);
		}
		
		public void setCurDayOrder(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("day_order", val);
			editor.commit();
		}
		
		public int getCurWeekOrder()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("week_order", Common.EMPTY_VALUE);
		}
		public void setCurWeekOrder(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("week_order", val);
			editor.commit();
		}
		
		/////////////////INTERVIEW
		
		public void setInterviewDone()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("interview_done", true);			
			editor.commit();		
		}
		
		public boolean getInterviewDone()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("interview_done", false);
		}
		
		public void setIsMale(final boolean val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("male", val);			
			editor.commit();		
		}
		
		public boolean getIsMale()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("male", true);
		}
		
		public void setUserName(final String val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putString("name", val);
			editor.commit();
		}
		
		public String getUserName()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getString("name", null);
		}
		
		public void setAge(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("age", val);
			editor.commit();
		}
		
		public int getAge()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("age", Common.EMPTY_VALUE);
		}
		
		public void setRestHR(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("hr", val);
			editor.commit();
		}
		
		public int getRestHR()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("hr", Common.EMPTY_VALUE);
		}
		
		public void setWeight(final float val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putFloat("weight", val);
			editor.commit();
		}
		
		public float getWeight()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getFloat("weight", 0f);
		}
		
		public void setUserWeightUnits(final boolean isLbs)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("user_lbs", isLbs);
			editor.commit();
		}
		
		public boolean getIsUserWeightUnitsLbs()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("user_lbs", true);
		}
		
		
		//Distance
		public void setDistanceUnits(final boolean isKm)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("km", isKm);
			editor.commit();
		}
		
		public boolean getDistanceUnitsKm()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("km", false);
		}
		
		//Fitness level 1- low, 2 - avg, 3 - high
		public void setFitnessLevel(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("fitness_level", val);
			editor.commit();
		}
		
		public int getFitnessLevel()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("fitness_level", 2);
		}
		
		///SETTINGS
		public void setRestTime(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("rest_time", val);
			editor.commit();
		}
		
		public int getRestTime()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("rest_time", Common.DEF_REST_TIME);
		}
		
		public void setStartTimerOnSave(final boolean val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("start_timer", val);			
			editor.commit();		
		}
		
		public boolean getStartTimerOnSave()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("start_timer", false);
		}
		
		public void setPlaySoundOnTimeUp(final boolean val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("play_sound", val);			
			editor.commit();	
		}
		
		public boolean getPlaySoundOnTimeUp()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("play_sound", false);
		}
		
		public void setAdvanceOnComplete(final boolean val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("advance", val);			
			editor.commit();	
		}
		
		public boolean getAdvanceOnComplete()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("advance", false);
		}
		
		public void setKeepAwake(final boolean val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putBoolean("keep_awake", val);			
			editor.commit();	
		}
		
		public boolean getKeepAwake()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getBoolean("keep_awake", true);  // we will keep awake by default
		}
		
		public void setAllWorkoutSelectedListItem(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("selected_list_item", val);
			editor.commit();
		}
		
		public int getAllWorkoutSelectedListItem()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("selected_list_item", Common.EMPTY_VALUE);
		}
		
		/////////////////// WHEELS SETTINGS 
		public void setWeightWheelInterval(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("weight_interval", val);
			editor.commit();
		}
		
		public int getWeightWheelInterval()
		{

			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("weight_interval", Common.DEFAULT_WEIGHT_INTERVAL);
		}
		
		public void setWeightWheelBreak(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("weight_break", val);
			editor.commit();
		}
		
		public int getWeightWheelBreak()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("weight_break", Common.DEFAULT_WEIGHT_BREAK);
		}
		
		//MUSIC
		
		public void setMusicVolume(final int val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putInt("music_vol", val);
			editor.commit();
		}
		
		public int getMusicVol()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getInt("music_vol", -1);
		}
		
		public void setPlayListId(final long val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putLong("music_playlist_id", val);
			editor.commit();
		}
		
		public long getPlaylistId()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getLong("music_playlist_id", -1);
		}
		
		public void setPlaylistTitle(final String val)
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();	
			editor.putString("music_playlist_title", ((val!=null)? val : ""));
			editor.commit();
		}
		
		public String getPlaylistTitle()
		{
			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
			return preferences.getString("music_playlist_title", null);
		}
		
		///////// SCREEN ///////////////////
		
//		public void setScreenWidth(final int val)
//		{
//			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
//			Editor editor = preferences.edit();	
//			editor.putLong("screen_width", val);
//			editor.commit();
//		}
//		
//		public int getScreenWidth()
//		{
//			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
//			return preferences.getInt("music_vol", 0);
//		}
		
		////////////// USER INFO
		
		//index on the wheel
//		public void setOneRepMaxWeightIndex(final int val)
//		{
//			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
//			Editor editor = preferences.edit();	
//			editor.putInt("onerepmax_weight", val);
//			editor.commit();
//		}
//		
//		public int getOneRepMaxWeightIndex()
//		{
//			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
//			return preferences.getInt("onerepmax_weight", 0);
//		}
//		
//		public void setOneRepMaxRepIndex(final int val)
//		{
//			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
//			Editor editor = preferences.edit();	
//			editor.putInt("onerepmax_rep", val);
//			editor.commit();
//		}
//		
//		public int getOneRepMaxRepIndex()
//		{
//			SharedPreferences preferences = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
//			return preferences.getInt("onerepmax_rep", 0);
//		}
		
	

}
