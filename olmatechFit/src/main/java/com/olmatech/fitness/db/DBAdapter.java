package com.olmatech.fitness.db;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.DayData;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.ExData;
import com.olmatech.fitness.main.Exercise;
import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.LogData;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout;
import com.olmatech.fitness.main.Workout.EntryType;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.main.WorkoutInfo;
import com.olmatech.fitness.main.WorkoutLog;


public class DBAdapter {
	
	private final static String TAG = "SQL";
	
	//public final static String DB_DIR = "/data/data/com.olmatech.fitness/databases";	
	
	//public static String DB_DIR;	
	//public final static String DB_DESTINATION = DB_DIR +"/fit";	
	
	public static final String DATABASE_NAME ="fit";	
	 private static final int DATABASE_VERSION = 1;
	 private static DBAdapter refAdapter;
	    
	 public DatabaseHelper DBHelper;
	 private static SQLiteDatabase mDb;
	 
	 private static final String TB_Complex="Complex";
	 private static final String TB_Program="Program";
	 private static final String TB_Week = "Week";
	 private static final String TB_Day="Day";	 
	 private static final String TB_Workout="Workout";
	 private static final String TB_Workout_entry="Workout_entry";
	 private static final String TB_Exercise="Exercise";	 
	 private static final String TB_Alternatives="Aleternatives";
	 private static final String TB_Day_complete="Day_complete";
	 
	 
	 private static final String TB_Workout_log="Workout_log";
	 private static final String TB_Workout_log_data_w="Workout_log_data_w";
	 private static final String TB_Workout_log_data_c="Workout_log_data_c";
	 private static final String TB_Workout_log_data_t="Workout_log_data_t";
	 
//	 private static final String TB_User="User";
	// private static final String TB_User_ex_preset="User_ex_preset";
	 //private static final String TB_Recommended="Recommended";
	 //private static final String TB_Settings="Settings";
	 
	 //columns
	 public static final String COL_ID="_id";
	 public static final String COL_TITLE="title";
	 public static final String COL_DESCRIPTION="description";
	 public static final String COL_COMPLEX_ID="complex_id";
	 public static final String COL_WEEK_ID="week_id";
	 public static final String COL_PROGR_ID="progr_id";
	 public static final String COL_WEEK_ORDER="week_order";
	 public static final String COL_WORKOUT_ID="workout_id";
	 public static final String COL_MODE="mode";
	 public static final String COL_TIME="time";
	 public static final String COL_ENTRY_ID="entry_id";
	 public static final String COL_ENTRY_TYPE="entry_type";
	 public static final String COL_SETS="sets";
	 public static final String COL_ENTRY_ORDER="entry_order";
	 public static final String COL_GENDER="gender";
	 public static final String COL_IMG_MALE="img_male";
	 public static final String COL_IMG_FEMALE="img_female";
	 public static final String COL_LEVEL="level";
	 public static final String COL_LAPS="laps";
	 public static final String COL_WORKOUT_ENTRY_ID="workout_entry_id";
	 public static final String COL_ALT_ID="alt_id";
	 
	 public static final String COL_NAME="name";
	 public static final String COL_AGE="age";
	 public static final String COL_WEIGHT="weight";
	 public static final String COL_USER_ID="user_id";
	 public static final String COL_DATETIME="datetime";
	 public static final String COL_DAY_ID="day_id";
	 public static final String COL_SET_NUM="set_num";
	 public static final String COL_REPS="reps";
	 public static final String COL_LOG_ID="log_id";
	 public static final String COL_EX_ID="ex_id";	
//	 public static final String COL_EN_WEIGHT="en_weight";	
	 public static final String COL_MAXREP="maxrep";	/////////////////////////////////////was bb_weight
	 public static final String COL_DAY_ORDER="day_order";
	 public static final String COL_COMPLETE="complete";
	 public static final String COL_DISTANCE="distance";
	 public static final String COL_EQUIPMENT = "equipment";
	 public static final String COL_UNITS="units";
	 
	 public static final String COL_EN_REPS = "en_reps";
	 public static final String COL_EN_WT_ACTUAL ="en_wt_actual";
	 public static final String COL_BB_REPS="bb_reps";
	 public static final String COL_BB_WT_ACTUAL= "bb_wt_actual";
	 public static final String COL_SHOW_ACTUAL ="show_actual";
		 
	 //strings
	 private final static String WHERE_WK_LOG = COL_USER_ID + "=%d and " + COL_PROGR_ID +"=%d and " + 
				COL_WEEK_ID +"=%d and " + COL_DAY_ID + "=%d and " +
				COL_WORKOUT_ID + "=%d and " + COL_DATETIME + "=%d and " +
				COL_ENTRY_ID + "=%d and " + COL_EX_ID + "=%d"; 
	
	 private final static String WHERE_WK_LOG_W = COL_LOG_ID + "=%d and " + COL_SET_NUM + "=%d";
	 
	 
	 private final static String LOG_DATA_SELECT ="select log._id as " + COL_ID + ",log." + COL_SET_NUM + " as " + COL_SET_NUM +
				",log." + COL_REPS + " as " + COL_REPS +
				",log." + COL_WEIGHT + " as " + COL_WEIGHT +
				",(CASE(log." + COL_UNITS + ") WHEN 1 THEN  'kg' ELSE 'lb' END) as " + COL_UNITS +
				" from " + TB_Workout_log_data_w  + " as log where log." + COL_LOG_ID + "=%d";
	 
	 
	 private DBAdapter(Context cxt) 
    {	
		 //DB_DIR = cxt.getFilesDir().getPath();
        DBHelper = new DatabaseHelper(cxt);
    }
	
	public static DBAdapter getAdapter(Context cxt)
	{
		if(refAdapter == null) refAdapter = new DBAdapter(cxt);
		return refAdapter;
	}
	
	private final static String SQL_WORKOUT_LIST = "select tbone." + COL_ID + " as " + COL_ID + ",tbone." + COL_ENTRY_ID + " as " + COL_ENTRY_ID +
			",tbone." + COL_ENTRY_TYPE  + " as " + COL_ENTRY_TYPE + 
			",tbone."+COL_ENTRY_ORDER+ " as " + COL_ENTRY_ORDER +
			",tbone." + COL_TITLE + " as " + COL_TITLE+
			",tbone." + COL_DESCRIPTION + " as " + COL_DESCRIPTION +
			",tbone." + COL_SETS + " as " + COL_SETS +
			",tbone." + COL_TIME + " as " + COL_TIME +
			",tbone." +COL_LEVEL + " as " + COL_LEVEL +
			",tbone." + COL_IMG_MALE + " as " + COL_IMG_MALE +
			",tbone." + COL_IMG_FEMALE + " as " + COL_IMG_FEMALE +
			",tbone." + COL_MODE + " as " + COL_MODE +    			
			//",tbone." + COL_EN_WEIGHT + " as " + COL_EN_WEIGHT + 			
			",tbone." +COL_EN_REPS + " as " + COL_EN_REPS +
			",tbone." + COL_EN_WT_ACTUAL + " as " + COL_EN_WT_ACTUAL + 
			",tbone." + COL_BB_REPS + " as " + COL_BB_REPS +
			",tbone." + COL_BB_WT_ACTUAL + " as " + COL_BB_WT_ACTUAL +	
			",tbone." + COL_SHOW_ACTUAL + " as " + COL_SHOW_ACTUAL +			
			",tbone." + COL_MAXREP + " as " + COL_MAXREP +    			   			
			",log." + COL_COMPLETE + " as " + COL_COMPLETE +
			" from (select ent._id as " + COL_ID + ",ent.entry_id as " + COL_ENTRY_ID + 
			",ent.entry_type as "+ COL_ENTRY_TYPE +
			", ent.entry_order as " + COL_ENTRY_ORDER +
			",ent.sets as " + COL_SETS +
			",ent.time as " + COL_TIME +
			", ex.title as "+COL_TITLE+ 
			", ex.description as " + COL_DESCRIPTION + 
			", ex.img_male as "+ COL_IMG_MALE +
			", ex.img_female as " + COL_IMG_FEMALE +
			", ex.mode as " + COL_MODE +
			",ex.level as " + COL_LEVEL +
			//",ex.en_weight as " + COL_EN_WEIGHT + 
			",ex.en_reps as " + COL_EN_REPS +
			",ex.en_wt_actual as " + COL_EN_WT_ACTUAL +
			",ex.bb_reps as " + COL_BB_REPS +
			",ex.bb_wt_actual as " + COL_BB_WT_ACTUAL +	
			",ex.show_actual as " + COL_SHOW_ACTUAL +
			",ex.maxrep as " + COL_MAXREP +    			
			" from " + TB_Workout_entry + " as ent left join " + TB_Exercise +
			" as ex on ent.entry_id=ex._id where ent.workout_id=%d order by ent.entry_order) as tbone left join "+
			" (select entry_id, complete from Workout_log where progr_id=%d and week_id=%d and day_id=%d and workout_id=%d) as log on  tbone." + COL_ENTRY_ID+"=log.entry_id;";
	
	public static String getWorkoutListStr(final DayInfo dayInfo)
	{
		
		return String.format(Locale.US,SQL_WORKOUT_LIST, 
				dayInfo.workoutId, dayInfo.progrId, dayInfo.weekId,dayInfo.dayId,dayInfo.workoutId);
		
		//TODO - optimize    	
//    	return "select tbone." + COL_ID + " as " + COL_ID + ",tbone." + COL_ENTRY_ID + " as " + COL_ENTRY_ID +
//    			",tbone." + COL_ENTRY_TYPE  + " as " + COL_ENTRY_TYPE + 
//    			",tbone."+COL_ENTRY_ORDER+ " as " + COL_ENTRY_ORDER +
//    			",tbone." + COL_TITLE + " as " + COL_TITLE+
//    			",tbone." + COL_DESCRIPTION + " as " + COL_DESCRIPTION +
//    			",tbone." + COL_SETS + " as " + COL_SETS +
//    			",tbone." + COL_TIME + " as " + COL_TIME +
//    			",tbone." +COL_LEVEL + " as " + COL_LEVEL +
//    			",tbone." + COL_IMG_MALE + " as " + COL_IMG_MALE +
//    			",tbone." + COL_IMG_FEMALE + " as " + COL_IMG_FEMALE +
//    			",tbone." + COL_MODE + " as " + COL_MODE +    			
//    			",tbone." + COL_EN_WEIGHT + " as " + COL_EN_WEIGHT +    			
//    			",tbone." + COL_BB_WEIGHT + " as " + COL_BB_WEIGHT +    			   			
//    			",log." + COL_COMPLETE + " as " + COL_COMPLETE +
//    			" from (select ent._id as " + COL_ID + ",ent.entry_id as " + COL_ENTRY_ID + 
//    			",ent.entry_type as "+ COL_ENTRY_TYPE +
//    			", ent.entry_order as " + COL_ENTRY_ORDER +
//    			",ent.sets as " + COL_SETS +
//    			",ent.time as " + COL_TIME +
//    			", ex.title as "+COL_TITLE+ 
//    			", ex.description as " + COL_DESCRIPTION + 
//    			", ex.img_male as "+ COL_IMG_MALE +
//    			", ex.img_female as " + COL_IMG_FEMALE +
//    			", ex.mode as " + COL_MODE +
//    			",ex.level as " + COL_LEVEL +
//    			",ex.en_weight as " + COL_EN_WEIGHT +    			
//    			",ex.bb_weight as " + COL_BB_WEIGHT +    			
//    			" from " + TB_Workout_entry + " as ent left join " + TB_Exercise +
//    			" as ex on ent.entry_id=ex._id where ent.workout_id=" + dayInfo.workoutId + " order by ent.entry_order) as tbone left join "+
//    			" (select entry_id, complete from Workout_log where progr_id=" + dayInfo.progrId +
//    			" and week_id=" + dayInfo.weekId +
//    			" and day_id=" + dayInfo.dayId +
//    			" and workout_id=" + dayInfo.workoutId +
//    			") as log on  tbone." + COL_ENTRY_ID+"=log.entry_id;";
	}
	
	private static String getWorkoutLetterForMode(final WorkoutMode md)
	{
		switch(md)
		{
		case Tactical:	return "T"; 					
		case Endurance:				
		case Bodybuilding:	return "W"; 	
		case Cardio: return "C"; 
		default: return "W";			
		}
	}
	
	private final static String SQL_WORKOUT_LIST_FORMODE ="select tbone." + COL_ID + " as " + COL_ID + ",tbone." + COL_ENTRY_ID + " as " + COL_ENTRY_ID +
    			",tbone." + COL_ENTRY_TYPE  + " as " + COL_ENTRY_TYPE + 
    			",tbone."+COL_ENTRY_ORDER+ " as " + COL_ENTRY_ORDER +
    			",tbone." + COL_TITLE + " as " + COL_TITLE+
    			",tbone." + COL_DESCRIPTION + " as " + COL_DESCRIPTION +
    			",tbone." + COL_SETS + " as " + COL_SETS +
    			",tbone." + COL_TIME + " as " + COL_TIME +
    			",tbone." +COL_LEVEL + " as " + COL_LEVEL +
    			",tbone." + COL_IMG_MALE + " as " + COL_IMG_MALE +
    			",tbone." + COL_IMG_FEMALE + " as " + COL_IMG_FEMALE +
    			",tbone." + COL_MODE + " as " + COL_MODE +    			
    			//",tbone." + COL_EN_WEIGHT + " as " + COL_EN_WEIGHT +    	
    			",tbone." +COL_EN_REPS + " as " + COL_EN_REPS +
				",tbone." + COL_EN_WT_ACTUAL + " as " + COL_EN_WT_ACTUAL + 
				",tbone." + COL_BB_REPS + " as " + COL_BB_REPS +
				",tbone." + COL_BB_WT_ACTUAL + " as " + COL_BB_WT_ACTUAL +	
				",tbone." + COL_SHOW_ACTUAL + " as " + COL_SHOW_ACTUAL +
    			",tbone." + COL_MAXREP + " as " + COL_MAXREP +    			   			
    			",log." + COL_COMPLETE + " as " + COL_COMPLETE +
    			" from (select ent._id as " + COL_ID + ",ent.entry_id as " + COL_ENTRY_ID + 
    			",ent.entry_type as "+ COL_ENTRY_TYPE +
    			", ent.entry_order as " + COL_ENTRY_ORDER +
    			",ent.sets as " + COL_SETS +
    			",ent.time as " + COL_TIME +
    			", ex.title as "+COL_TITLE+ 
    			", ex.description as " + COL_DESCRIPTION + 
    			", ex.img_male as "+ COL_IMG_MALE +
    			", ex.img_female as " + COL_IMG_FEMALE +
    			", ex.mode as " + COL_MODE +
    			",ex.level as " + COL_LEVEL +
    			//",ex.en_weight as " + COL_EN_WEIGHT +    	
    			",ex.en_reps as " + COL_EN_REPS +
				",ex.en_wt_actual as " + COL_EN_WT_ACTUAL +
				",ex.bb_reps as " + COL_BB_REPS +
				",ex.bb_wt_actual as " + COL_BB_WT_ACTUAL +		  
				",ex.show_actual as " + COL_SHOW_ACTUAL +	
    			",ex.maxrep as " + COL_MAXREP +    			
    			" from " + TB_Workout_entry + " as ent left join " + TB_Exercise +
    			" as ex on ent.entry_id=ex._id where ent.workout_id=%d order by ent.entry_order) as tbone left join "+
    			" (select entry_id, complete from Workout_log where "+    			 
    			 "mode='%s'%s and workout_id=%d) as log on  tbone." + COL_ENTRY_ID+"=log.entry_id;";
	
	//sql to select workout for mode data
	public static String getWorkoutListStrForMode(final WorkoutMode md,  final int workoutId, final long log_datetime)
	{
		final String modeStr =getWorkoutLetterForMode(md);
		
		return String.format(Locale.US, SQL_WORKOUT_LIST_FORMODE, 
				workoutId, modeStr,getDatetimeClause(log_datetime),workoutId);
		
		//TODO - optimize    	
//    	return "select tbone." + COL_ID + " as " + COL_ID + ",tbone." + COL_ENTRY_ID + " as " + COL_ENTRY_ID +
//    			",tbone." + COL_ENTRY_TYPE  + " as " + COL_ENTRY_TYPE + 
//    			",tbone."+COL_ENTRY_ORDER+ " as " + COL_ENTRY_ORDER +
//    			",tbone." + COL_TITLE + " as " + COL_TITLE+
//    			",tbone." + COL_DESCRIPTION + " as " + COL_DESCRIPTION +
//    			",tbone." + COL_SETS + " as " + COL_SETS +
//    			",tbone." + COL_TIME + " as " + COL_TIME +
//    			",tbone." +COL_LEVEL + " as " + COL_LEVEL +
//    			",tbone." + COL_IMG_MALE + " as " + COL_IMG_MALE +
//    			",tbone." + COL_IMG_FEMALE + " as " + COL_IMG_FEMALE +
//    			",tbone." + COL_MODE + " as " + COL_MODE +    			
//    			",tbone." + COL_EN_WEIGHT + " as " + COL_EN_WEIGHT +    			
//    			",tbone." + COL_BB_WEIGHT + " as " + COL_BB_WEIGHT +    			   			
//    			",log." + COL_COMPLETE + " as " + COL_COMPLETE +
//    			" from (select ent._id as " + COL_ID + ",ent.entry_id as " + COL_ENTRY_ID + 
//    			",ent.entry_type as "+ COL_ENTRY_TYPE +
//    			", ent.entry_order as " + COL_ENTRY_ORDER +
//    			",ent.sets as " + COL_SETS +
//    			",ent.time as " + COL_TIME +
//    			", ex.title as "+COL_TITLE+ 
//    			", ex.description as " + COL_DESCRIPTION + 
//    			", ex.img_male as "+ COL_IMG_MALE +
//    			", ex.img_female as " + COL_IMG_FEMALE +
//    			", ex.mode as " + COL_MODE +
//    			",ex.level as " + COL_LEVEL +
//    			",ex.en_weight as " + COL_EN_WEIGHT +    			
//    			",ex.bb_weight as " + COL_BB_WEIGHT +    			
//    			" from " + TB_Workout_entry + " as ent left join " + TB_Exercise +
//    			" as ex on ent.entry_id=ex._id where ent.workout_id=" + workoutId + " order by ent.entry_order) as tbone left join "+
//    			" (select entry_id, complete from Workout_log where "+    			 
//    			 "mode='" +modeStr +"'" +   
//    			 getDatetimeClause(log_datetime) +
//    			" and workout_id=" + workoutId +
//    			") as log on  tbone." + COL_ENTRY_ID+"=log.entry_id;";
	}
	
	private static String getDatetimeClause(final long log_datetime)
	{
		return ((log_datetime >0)? " and datetime=" + log_datetime : "");
	}	
	
	//tacktical for Complete program for a Tacktical day
	private final static String SQL_TACTICAL_FORVIEW = "select wk." + COL_ID +" as " + COL_ID +
			", wk." + COL_TITLE + " as " + COL_TITLE +
			", log." + COL_ID + " as log_id from " + 
			TB_Workout_log_data_t + " as log inner join " +
			TB_Workout + " as wk on log." + COL_WORKOUT_ID +"=wk." + COL_ID + 
			" where wk." + COL_ID + "=%d and log." + COL_DATETIME +"=%d and log."+ COL_PROGR_ID + "=%d and log." +
			COL_WEEK_ID + ">0";
	
	
		
	public static String getTackticalLogSQLForView(final int workoutId, final long dt, final int progrId)
	{
		/*
		 * select wk._id as _id, wk.title as title, log._id as log_id from Workout_log_data_t as log inner join Workout as wk on log.workout_id=wk._id where wk._id=3;
		 */
		if(workoutId >0)
		{
			return String.format(Locale.US, SQL_TACTICAL_FORVIEW, 
					workoutId,dt, progrId);
		}
		else
		{
			//all tackticals for this day outside Program
			return "select log." + COL_WORKOUT_ID + " as " + COL_ID +	
					", wk." + COL_TITLE + " as " + COL_TITLE +
					" from " + TB_Workout_log_data_t + " as log inner join " +
					TB_Workout + " as wk on log." + COL_WORKOUT_ID +"=wk." + COL_ID + 					
					"  where log." + COL_DATETIME + "=" + dt +
					" and log." + COL_PROGR_ID + "=" + progrId +
					" and (log." + COL_WEEK_ID + " is null or log." + COL_WEEK_ID + "<=" + Common.ZERO_ID +")";
		}
		
	}
	
	
	//tacktical sub-view - can be for Complete progr Tacktical day (forCompleteProgr=true) or separate Tacktical (forCompleteProgr=false)
	public static String getTackticalLogSQLForSubView(final int workoutId, final long dt, final boolean forCompleteProgr)
	{
		/*
		 * select data.ex_id as _id, data.title as title,log.laps as laps from Workout_log_data_t as log inner join 
(select ex._id as ex_id, wk.workout_id as workoutid, wk.entry_order as entry_order, ex.title as title from Workout_entry as wk inner join Exercise as ex on wk.entry_id=ex._id where wk.workout_id=3) as data on log.workout_id=data.workoutid;
		 */
		//TODO - optimize   
		String where = (forCompleteProgr)?
				" and log." + COL_WEEK_ID + ">0" :
				" and (log." + COL_WEEK_ID + " is null or log." + COL_WEEK_ID + "<=0)";
		
    	return "select data.ex_id as " + COL_ID +
    			",data." + COL_TITLE + " as " + COL_TITLE+
    			",log." + COL_LAPS + " as " + COL_LAPS +
    			" from " + TB_Workout_log_data_t + " as log inner join (select ex." + COL_ID + " as ex_id"+
    			",wk." + COL_WORKOUT_ID +" as workoutid, wk." + COL_ENTRY_ORDER + " as " +COL_ENTRY_ORDER +
    			",ex." + COL_TITLE +" as " +COL_TITLE +
    			" from " + TB_Workout_entry +" as wk inner join " + TB_Exercise + " as ex on wk." + COL_ENTRY_ID +
    			"=ex." + COL_ID + " where wk." + COL_WORKOUT_ID + "=" + workoutId +") as data on log." + COL_WORKOUT_ID+ "=data.workoutid" +
    			" where log."+ COL_DATETIME+ "=" + dt + where;
	}
	
	private final static String SQL_GET_ALTS = "select alt.alt_id as " + COL_EX_ID +
			",alt.gender as " + COL_GENDER +
			",ex.title as " + COL_TITLE + 
			",ex.description as " + COL_DESCRIPTION +
			",ex.img_male as " + COL_IMG_MALE +
			",ex.img_female as " + COL_IMG_FEMALE +
			",ex.mode as " + COL_MODE +
			",ex.level as " + COL_LEVEL + 
			//",ex.en_weight as " + COL_EN_WEIGHT +		    
	    	",ex.maxrep as " + COL_MAXREP +		    	
			" from Alternatives as alt inner join Exercise as ex on alt.alt_id=ex._id where alt.workout_entry_id=%d and alt.workout_id=%d";

	
	public static String getAlts()  //final int exId, final int workoutId
	{
		/*
		 * select alt.alt_id as id, alt.gender as gender, ex.title as title, ex.description as description, ex.img_male as img_male, ex.img_female as img_femele,
				ex.mode as mode, ex.level as level from Alternatives as alt inner join Exercise as ex on alt.alt_id=ex._id where alt.workout_entry_id=5 and 
				alt.workout_id=1;
		 */		
		
		return SQL_GET_ALTS;
		
	}
	/////////// LOG VIEW ///////////////////////
	public static String getLogExerciseNamesSql(final long dt, final int workoutId, final int weekId)
	{
		/*
		 * select log._id as _id,ex.title as title, ex._id as exid from Workout_log as log inner join Exercise as ex on log.ex_id=ex._id where log.datetime=123456 and log.workout_id=1 and log.week_id=1;
		 */
		return "select log." + COL_ID + " as " + COL_ID +
				",ex." + COL_TITLE + " as " + COL_TITLE +
				" from " + TB_Workout_log + 
				" as log inner join " + TB_Exercise + 
				" as ex on log.ex_id=ex._id where log." + COL_DATETIME + "=" + dt +
				" and log." + COL_WORKOUT_ID + "=" + workoutId + " and log." + COL_WEEK_ID + "=" + weekId +
				" and (ex."+ COL_MODE + "='W' or ex."+ COL_MODE + "='B')";					
	}
	
		
	public static String getLogExerciseNamesSqlForModule(final long dt) //, final int workoutId)
	{
		/*
		 * select log._id as _id,ex.title as title, ex._id as exid from Workout_log as log inner join Exercise as ex on log.ex_id=ex._id where log.datetime=123456 and log.workout_id=1 and log.week_id=1;
		 */
		return "select log." + COL_ID + " as " + COL_ID +
				",ex." + COL_TITLE + " as " + COL_TITLE +
				" from " + TB_Workout_log + 
				" as log inner join " + TB_Exercise + 
				" as ex on log.ex_id=ex._id where log." + COL_DATETIME + "=" + dt +
				" and log." + COL_WEEK_ID + "=" + Common.ZERO_ID + " and log." + COL_DAY_ID +"=" + Common.ZERO_ID  +
				" and (ex."+ COL_MODE + "='W' or ex."+ COL_MODE + "='B')";					
	}
	
	public static String getWeightLogsByLogIdSql(final int logId)
	{
		/*
		 * select log.set_num as set_num, log,reps as reps,log.weight as weight,log.units as units from Workout_log_data_w as log
where log.log_id = logId;
		 */
		
		return String.format(Locale.US, LOG_DATA_SELECT, logId);		
		
	}
	//Cardio part of log for loader
	public static String getCardioLogSqlForAll(final int workoutId, final int weekId, final long dt)
	{
		/*select log.datetime as datetime, cardio._id as _id, cardio.time as time,cardio.distance as distance,cardio.equipment as equipment,cardio.units as units from Workout_log as log inner join Workout_log_data_c as cardio on log._id=cardio.log_id where log.datetime=1379660400000 and log.workout_id=1;
		 * 
		 */		
		return "select cardio." + COL_ID + " as " + COL_ID +
				//", cardio." + COL_TIME + " as " + COL_TIME +
				",strftime('%H:%M:%S', CAST(cardio." + COL_TIME +"/86400.0 AS DATETIME)- 0.5) as "+ COL_TIME +				
				",cardio." + COL_DISTANCE + "/1000.0 as " + COL_DISTANCE + 				
				",(CASE(cardio." + COL_EQUIPMENT + ") WHEN 0 THEN " +Common.CARDIO_EQUIPMENT[0] +
					" WHEN 1 THEN " + Common.CARDIO_EQUIPMENT[1] +
					" WHEN 2 THEN " + Common.CARDIO_EQUIPMENT[2] +
					" WHEN 3 THEN " + Common.CARDIO_EQUIPMENT[3] +
					" WHEN 4 THEN " + Common.CARDIO_EQUIPMENT[4] +
					" ELSE " + Common.CARDIO_EQUIPMENT[5] + " END) as " + COL_EQUIPMENT +				
				",(CASE(cardio." + COL_UNITS + ") WHEN 1 THEN  'mi' ELSE 'km' END) as " + COL_UNITS +
				" from " + TB_Workout_log + " as  log inner join " +
				TB_Workout_log_data_c + " as cardio on log." + COL_ID + "=cardio." + COL_LOG_ID +
				" where log." + COL_DATETIME + "=" + dt + 
				" and log." + COL_WORKOUT_ID + "=" + workoutId +
				" and log." + COL_WEEK_ID + "=" + weekId;
	}
	
	public static String getCardioLogSqlForModule(final long dt)
	{
		/*select log.datetime as datetime, cardio._id as _id, cardio.time as time,cardio.distance as distance,cardio.equipment as equipment,cardio.units as units from Workout_log as log inner join Workout_log_data_c as cardio on log._id=cardio.log_id where log.datetime=1379660400000 and log.workout_id=1;
		 * 
		 */		
		return "select cardio." + COL_ID + " as " + COL_ID +
				//", cardio." + COL_TIME + " as " + COL_TIME +
				",strftime('%H:%M:%S', CAST(cardio." + COL_TIME +"/86400.0 AS DATETIME)- 0.5) as "+ COL_TIME +
				",cardio." + COL_DISTANCE + "/1000.0 as " + COL_DISTANCE +				
				",(CASE(cardio." + COL_EQUIPMENT + ") WHEN 0 THEN " +Common.CARDIO_EQUIPMENT[0] +
					" WHEN 1 THEN " + Common.CARDIO_EQUIPMENT[1] +
					" WHEN 2 THEN " + Common.CARDIO_EQUIPMENT[2] +
					" WHEN 3 THEN " + Common.CARDIO_EQUIPMENT[3] +
					" WHEN 4 THEN " + Common.CARDIO_EQUIPMENT[4] +
					" ELSE " + Common.CARDIO_EQUIPMENT[5] + " END) as " + COL_EQUIPMENT +				
				",(CASE(cardio." + COL_UNITS + ") WHEN 1 THEN  'mi' ELSE 'km' END) as " + COL_UNITS +
				" from " + TB_Workout_log + " as  log inner join " +
				TB_Workout_log_data_c + " as cardio on log." + COL_ID + "=cardio." + COL_LOG_ID +
				" where log." + COL_DATETIME + "=" + dt + 
				//" and log." + COL_WORKOUT_ID + "=" + workoutId +
				" and log." + COL_WEEK_ID + "=" + Common.ZERO_ID + 
				" and log." + COL_MODE + "='E' or log." + COL_MODE + "='B'";

	}
	
	private final static String SQL_EX_LIST ="select tbone." + COL_ID + " as " + COL_ID + ",tbone." + COL_ENTRY_ID + " as " + COL_ENTRY_ID +
			",tbone."+COL_ENTRY_ORDER+ " as " + COL_ENTRY_ORDER +
			",tbone." + COL_TITLE + " as " + COL_TITLE+
			",tbone." + COL_IMG_MALE + " as " + COL_IMG_MALE +
			",tbone." + COL_IMG_FEMALE + " as " + COL_IMG_FEMALE +    			
			",log." + COL_COMPLETE + " as " + COL_COMPLETE +
			" from (select ent._id as " + COL_ID + ",ent.entry_id as " + COL_ENTRY_ID +     			
			", ent.entry_order as " + COL_ENTRY_ORDER +
			", ex.title as "+COL_TITLE+ 
			", ex.img_male as "+ COL_IMG_MALE +
			", ex.img_female as " + COL_IMG_FEMALE +    			
			" from " + TB_Workout_entry + " as ent left join " + TB_Exercise +
			" as ex on ent.entry_id=ex._id where ent.workout_id=%d order by ent.entry_order) as tbone left join "+
			" (select entry_id, complete from Workout_log where progr_id=%d and week_id=%d and day_id=%d and workout_id=%d) as log on  tbone." + COL_ENTRY_ID+"=log.entry_id;";	
	
	
	public String getExListSQL(final DayInfo dayInfo)
	{	    
		return String.format(Locale.US,  SQL_EX_LIST,
				dayInfo.workoutId, dayInfo.progrId,dayInfo.weekId,dayInfo.dayId, dayInfo.workoutId);
    	
//    	return "select tbone." + COL_ID + " as " + COL_ID + ",tbone." + COL_ENTRY_ID + " as " + COL_ENTRY_ID +
//    			",tbone."+COL_ENTRY_ORDER+ " as " + COL_ENTRY_ORDER +
//    			",tbone." + COL_TITLE + " as " + COL_TITLE+
//    			",tbone." + COL_IMG_MALE + " as " + COL_IMG_MALE +
//    			",tbone." + COL_IMG_FEMALE + " as " + COL_IMG_FEMALE +    			
//    			",log." + COL_COMPLETE + " as " + COL_COMPLETE +
//    			" from (select ent._id as " + COL_ID + ",ent.entry_id as " + COL_ENTRY_ID +     			
//    			", ent.entry_order as " + COL_ENTRY_ORDER +
//    			", ex.title as "+COL_TITLE+ 
//    			", ex.img_male as "+ COL_IMG_MALE +
//    			", ex.img_female as " + COL_IMG_FEMALE +    			
//    			" from " + TB_Workout_entry + " as ent left join " + TB_Exercise +
//    			" as ex on ent.entry_id=ex._id where ent.workout_id=" + dayInfo.workoutId + " order by ent.entry_order) as tbone left join "+
//    			" (select entry_id, complete from Workout_log where progr_id=" + dayInfo.progrId +
//    			" and week_id=" + dayInfo.weekId +
//    			" and day_id=" + dayInfo.dayId +
//    			" and workout_id=" + dayInfo.workoutId +
//    			") as log on  tbone." + COL_ENTRY_ID+"=log.entry_id;";
    	
    	
	}
	
		
	public boolean getWorkoutForDay(final DayInfo dayInfo, Workout workout)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.getWorkoutForDay(dayInfo, workout, mDb);
	}
	
	public boolean getWorkoutForMode(Workout workout, final int workoutId, final WorkoutMode md,  
			final long log_datetime)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.getWorkoutForMode(workout, workoutId, md, log_datetime, mDb);
	}
	
	public List<WorkoutInfo> getWorkoutsInfoByMode(final WorkoutMode md)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getWorkoutsInfoByMode(md, mDb);
	}
	

	
	public boolean getProgramFromDb(CurProgram progr, final boolean forWeekDayId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.getProgramFromDb(progr,forWeekDayId, mDb);
	}	

	
	public LinkedList<LogData> getLogDataForWeight(final long logId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getLogDataForWeight(logId, mDb);
	}
	
	public boolean deleteWeightLogEntry(final long logId, final int setNum)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.deleteWeightLogEntry(logId, setNum, mDb);
	}
	
	public boolean checkIfHaveLogsForDay(final int userId, final DayInfo dayInfo, final long date_time)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.checkIfHaveLogs(userId, dayInfo, date_time, mDb);
	}
	
	public boolean checkIfHaveLogsForModule(final int userId, final int progrId, final long date_time, 
			final int workoutId, final WorkoutMode md)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.checkIfHaveLogsForModule(userId, progrId, date_time, workoutId, md, mDb);
	}
	
	public boolean checkIfHaveTactLogs(final int userId, final int progrId, final int wkId,
			final long date_time, final int weekId, final int dayOrder)
	{

		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.checkIfHaveTactLogs(userId, progrId,  wkId,	date_time,weekId, dayOrder, mDb);
	}
	
	//delete all workout log for  a dayOfMonth
//	public boolean deleteLogForWorkout(final int userId, final int workoutId, final int progrId, final WorkoutMode md, 
//			final long date_time,  final int weekId, final int dayOrder,final boolean allLogsOnAndAfter)
//	{
//		if(mDb == null) openDb();
//    	if(mDb == null) return false;
//    	return DBHelper.deleteLogForWorkout(userId, workoutId,  progrId,  md, date_time,  weekId, dayOrder, allLogsOnAndAfter, mDb);
//	}
	
	public boolean deleteLogForWorkoutOnDate(final int userId, final int workoutId, final int progrId, 
			final WorkoutMode md,
			final long date_time)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.deleteLogForWorkoutOnDate(userId, workoutId, progrId, md, date_time, mDb);
	}
	
	public boolean deleteLogsOnAfterDate(final int userId, final int progrId, 				
			final long date_time, final int  weekId,final int dayOrder)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.deleteLogsOnAfterDate(userId, progrId, date_time, weekId, dayOrder, mDb);
	}
	
	public boolean setCompleteStatusForWorkout(final int userId, final DayInfo dayInfo, Workout workout,
			final long date_time,Gender g, final WorkoutMode forMode)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.setCompleteStatusForWorkout(userId, dayInfo, workout, date_time,g,forMode, mDb);
	}
	
	public boolean setLapsForTackticalWorkout(final int userId, final int progrId, Workout workout,
			final long date_time)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.setLapsForTackticalWorkout(userId, progrId, workout, date_time, mDb);
	}
	
	//number of workouts for a given mode
	public int getWorkoutForModeCount(final WorkoutMode md)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return -1;
    	return DBHelper.getWorkoutForModeCount(md, mDb);
	}
	
	
	public boolean getNextDayInfo(DayInfo dayInfo, final int dayOrder)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.getNextDayInfo(dayInfo, dayOrder, mDb);    
	}
	
	public boolean getFirstProgrDayInfo(DayInfo dayInfo)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.getFirstProgrDayInfo(dayInfo, mDb);
	}
	
	/**
	 * Delete all logs for this program
	 * @param progrId
	 * @param userId
	 * @return
	 */
	public boolean deleteAllProgrLogs(final int progrId, final int userId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.deleteAllProgrLogs(progrId, userId, mDb);
	}
	
	public Hashtable<Long, DayData> getLogsForAMonth(final int userId, final int progrId,
			final long startDate, 
			final long endDate)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getLogsForAMonth(userId, progrId,startDate, endDate, mDb);
	}
	
	//get ONLY tacktical logs - not for Complete program
	public Hashtable<Long, DayData> getTackticalLogsForAMonth(final int userId, final long startDate, 
			final long endDate)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getTackticalLogsForAMonth(userId, startDate, endDate, mDb);
	}
	
	public Hashtable<Long, DayData> getModulesLogsForAMonth(final int userId, final long startDate, 
			final long endDate)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getModulesLogsForAMonth(userId, startDate, endDate, mDb);
	}
	
	public StringBuilder getLogTextToEmailAll(final long dt, final int workoutId, final int weekId,
			final WorkoutMode mode, StringBuilder csvStr, final int progrId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getLogTextToEmailAll(dt, workoutId, weekId, mode, csvStr,mDb, progrId);
	}
	
	public boolean setDayComplete(final boolean isCompleted, final int progrId, final int weekId,
			final int dayId, final long dt)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.setDayComplete(isCompleted, progrId, weekId, dayId, dt, mDb);
	}
	
	public long getDatetimeDayWasLastCompleted(final int progrId, final int weekId, final int dayOrder)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return -1;
    	return DBHelper.getDatetimeDayWasLastCompleted(progrId, weekId, dayOrder, mDb);
	}
	
	public boolean deleteAllLogs()
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	DBHelper.deleteAllLogs(mDb);
    	return true;
	}
	//get days to display as scheduler 
	public List<DayData> getProgrForSchedule(final int progrId, final int dayTostart,
			final int maxDays)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getProgrForSchedule(progrId, dayTostart, maxDays, mDb);
	}
	
	//ret - last datetime and last day order
	public long[] getLastLogDatetime(final int progrId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getLastLogDatetime(progrId, mDb);
	}
	
	public long[] getLastCompletedDayorder(final int progrId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return null;
    	return DBHelper.getLastCompletedDayorder(progrId, mDb);
	}
	
	public long getDatetimeCompletedDay(final int progrId, final int dayOrder)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return -1;
    	return DBHelper.getDatetimeCompletedDay(progrId, dayOrder, mDb);
	}
	
	public int getHaveCompletedAtDate(final int progrId, final long dt)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return -1;
    	return DBHelper.getHaveCompletedAtDate(progrId, dt, mDb);
	}
	
	private final static String SQL_PROGR_WEEK_DAY ="select Day._id as " +COL_ID +
			",Week._id as " + COL_WEEK_ID +
			", Week.week_order as " + COL_WEEK_ORDER +
			", Day.workout_id as " + COL_WORKOUT_ID +
			", Day.mode as " + COL_MODE +
			",Day.day_order as " + COL_DAY_ORDER +
			" from " + TB_Week + " join " + TB_Day + " on Week._id=Day.week_id where Week.progr_id=%d order by "+COL_DAY_ORDER;

	
	public String getProgramWeeksDaysSQL(final int progrId)
	{
		//select Week.week_order as week_order, Day.workout_id as wkid, Day.mode as mode, Day.day_order as day_order from  Week join Day on Week._id=Day.week_id where Week.progr_id=1 order by Week.week_order, Day.day_order;
		
		return String.format(Locale.US, SQL_PROGR_WEEK_DAY, 
				progrId);
		
		
//		return "select Day._id as " +COL_ID +
//				",Week._id as " + COL_WEEK_ID +
//				", Week.week_order as " + COL_WEEK_ORDER +
//				", Day.workout_id as " + COL_WORKOUT_ID +
//				", Day.mode as " + COL_MODE +
//				",Day.day_order as " + COL_DAY_ORDER +
//				" from " + TB_Week + " join " + TB_Day + " on Week._id=Day.week_id where Week.progr_id=" + progrId + " order by "+COL_DAY_ORDER;
	}
	
	public int getDaysInProgram(final int progrId)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return 0;
    	return DBHelper.getDaysInProgram(progrId, mDb);
	}
	
	//called from WorkoutList - to get titles 
	public String getWorkoutTitlesForMode(final WorkoutMode md )
	{
		String where = COL_MODE +"='" + getWorkoutLetterForMode(md) + "'";
		
		
		return "select " + COL_ID + "," + COL_TITLE +"," + COL_DESCRIPTION + " from " + TB_Workout + 
				" where " + where +
				" order by " + COL_ID;
	}
	
	//Workout for a dayOfMonth - TODO
//	public boolean getWorkout(final DayInfo dayInfo)
//	{
//		final String sql = getExListSQL(dayInfo);		
//		
//		
//		return true;
//	}
	
	//ret _id from Workout_log table or -1
	public long addLogData(final WorkoutLog wkLog)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return -1;
    	return DBHelper.addLogData(wkLog, mDb);
	}
	
	//tacktical
	public boolean addTackticalLogData(final int userId, final long dt, final int progrId, 
			final int workoutId, final int laps,final int weekId, final int dayOrder)
	{
		if(mDb == null) openDb();
    	if(mDb == null) return false;
    	return DBHelper.addTackticalLogData(userId, dt, progrId, workoutId, laps, weekId, dayOrder, mDb);
	}
	
	//UPDATE LOG
	public boolean setExComplete(final WorkoutLog wkLog) //, final Handler msgHandler)
	{
		if(mDb == null) openDb();
		if(mDb == null) return false;
			
		final boolean result= DBHelper.setExComplete(wkLog, mDb);
		//if(result && msgHandler!= null) msgHandler.sendEmptyMessage(0);
		return result;
	}	
	
	//
	/**
	 * delete saved maxrep vals
	 * @param exId - if >0 - just for 1 exercise
	 * @return
	 */
	public boolean deleteRecommendedWeights(final int exId)
	{
		if(mDb == null) openDb();
		if(mDb == null) return false;
		return DBHelper.deleteRecommendedWeights(mDb,exId);
	}
	
	private boolean openDb()
	{
		 try {
	        	DBHelper.openDataBase();
	        } catch (SQLException sqle) {
	           sqle.printStackTrace();
	            mDb = null;
	            return false;
	        }
		 return true;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase dbase) {		
			
		}		
		
//		private boolean checkDataBase() {
//            SQLiteDatabase checkDB = null;
//            try {
//                String myPath = DB_DESTINATION;
//                checkDB = SQLiteDatabase.openDatabase(myPath, null,
//                        SQLiteDatabase.OPEN_READONLY);
//            } catch (SQLiteException e) {
//            }
//            if (checkDB != null) {
//                checkDB.close();
//            }
//            return checkDB != null ? true : false;
//        }
		
		public void openDataBase() throws SQLException {
            // Open the database
            mDb = this.getWritableDatabase();
        }

        @Override
        public synchronized void close() {

            if (mDb != null)
            {
                mDb.close();
                mDb = null;
            }

            super.close();

        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
		//remove all log data for this program
		public boolean deleteAllProgrLogs(final int progrId, final int userId, SQLiteDatabase db)
		{
			String where1 = COL_PROGR_ID + "=" + progrId + " and " + COL_USER_ID + "=" + userId;
			try
			{
				db.delete(TB_Workout_log_data_t, where1, null);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();				
			}				
			Cursor cur = null;
			try
			{
				cur = db.query(TB_Workout_log, new String[]{COL_ID}, where1, null, null, null, null);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				cur = null;
			}
			if(cur == null) return true;
			if(cur.getCount() ==0)
			{
				cur.close();
				return true;
			}
			final int colId;
			try
			{
				colId = cur.getColumnIndexOrThrow(COL_ID);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return false;
			}
			
			final String where2 = COL_LOG_ID +"=";
			boolean ok = true;
			String where;
			while(cur.moveToNext())
			{				
				where = where2 + cur.getInt(colId);
				try
				{
					db.delete(TB_Workout_log_data_w, where, null);
					db.delete(TB_Workout_log_data_c, where, null);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					ok= false;
				}				
			}
			cur.close();
			cur=null;
			
			if(ok)
			{
				try
				{
					db.delete(TB_Workout_log, where1, null);				
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					ok=false;
				}				
			}	
			
			//delete all day completed records
			try
			{
				db.delete(TB_Day_complete, COL_PROGR_ID + "=" + progrId, null);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();				
			}				
			
			return ok;
		}
		
		
		private final static String TACTICAL_WHERE = COL_DATETIME + "=%d and " + COL_USER_ID +
				"=%d and " + COL_PROGR_ID + "=%d and " + COL_WORKOUT_ID + "=%d and " +
				COL_WEEK_ID + "=%d and " + COL_DAY_ORDER +"=%d";
				
		public boolean addTackticalLogData(final int userId, final long dt, final int progrId, 
				final int workoutId, final int laps,
				final int weekId, final int dayOrder,
				SQLiteDatabase db)
		{
			ContentValues vals = new ContentValues();
			
			vals.put(COL_USER_ID, userId);
			vals.put(COL_DATETIME, dt);
			vals.put(COL_PROGR_ID, progrId);
			vals.put(COL_WORKOUT_ID, workoutId);
			vals.put(COL_LAPS, laps);			
			
			final String where = String.format(Locale.US, TACTICAL_WHERE, dt, userId, progrId, workoutId,weekId, dayOrder);
						
			boolean ok = true;
			int rows = db.update(TB_Workout_log_data_t, vals, where, null);
			if(rows <1)
			{
				vals.put(COL_WEEK_ID,  weekId);
				vals.put(COL_DAY_ORDER, dayOrder);
				try
				{
					db.insertOrThrow(TB_Workout_log_data_t, null, vals);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					ok=false;
				}
			}
			return ok;
		}
		
		/**
		 * 
		 * @param db
		 * @param exId if -1 - clear all
		 * @return
		 */
		public boolean deleteRecommendedWeights(SQLiteDatabase db, final int exId)
		{
			ContentValues vals = new ContentValues();
			vals.putNull(COL_MAXREP);			
			//remove all actual set values
			vals.putNull(COL_BB_REPS);
			vals.putNull(COL_EN_REPS);
			vals.putNull(COL_BB_WT_ACTUAL);
			vals.putNull(COL_EN_WT_ACTUAL);
			vals.putNull(COL_SHOW_ACTUAL);				
			try
			{
				if(exId>0)
				{
					db.update(TB_Exercise, vals, COL_ID+"="+exId, null);
				}
				else
				{
					db.update(TB_Exercise, vals, null, null);
				}
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			return true;
		}
		
		//add log data and MaxRep values if any
		//ret _id of the entry in the Workout_log table or -1
		//all exept tacktical
		public long addLogData(final WorkoutLog wkLog, SQLiteDatabase db)
		{	
			ExType logType = wkLog.getLogType();
			if(logType == ExType.Tactical)
			{
				return -1;
			}
			//Workout_log			
			//WHERE_WK_LOG
			String where = String.format(Locale.US, WHERE_WK_LOG, wkLog.getUserId(), wkLog.getProgrId(),  wkLog.getWeekId(),
					wkLog.getDayId(),wkLog.getWorkoutId(),wkLog.getDatetime(),wkLog.getEntryId(),wkLog.getExerciseId());
					
			ContentValues vals = new ContentValues();
			vals.put(COL_COMPLETE, (wkLog.getCompleted())? 1 : 0);					
			
			int rows = db.update(TB_Workout_log, vals, where, null);
			long rowId=-1;
			if(rows <1)
			{
				//need to insert
				vals.put(COL_USER_ID, wkLog.getUserId());
				vals.put(COL_PROGR_ID, wkLog.getProgrId());
				vals.put(COL_WEEK_ID, wkLog.getWeekId());
				vals.put(COL_DAY_ID, wkLog.getDayId());
				vals.put(COL_WORKOUT_ID, wkLog.getWorkoutId());
				vals.put(COL_DATETIME, wkLog.getDatetime());			
				vals.put(COL_ENTRY_ID, wkLog.getEntryId());
				vals.put(COL_EX_ID, wkLog.getExerciseId());
				vals.put(COL_MODE, WorkoutMode.getShortTitle2(wkLog.getWorkoutMode()));				
								
				try
				{
					rowId = db.insertOrThrow(TB_Workout_log, null, vals);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					rowId = -1;
				}
			}
			else
			{
				//get log id
				Cursor cur = null;
				try
				{
					cur = db.query(TB_Workout_log, new String[]{COL_ID}, where, null, null, null, COL_DATETIME + " DESC");
					if(cur != null)
					{
						if(cur.getCount() >0 && cur.moveToFirst())
						{
							rowId = cur.getLong(cur.getColumnIndexOrThrow(COL_ID));
						}
						
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					rowId = -1;
				}
				finally
				{
					if(cur != null)
					{
						cur.close();
					}
				}
				
			}
			
			if(rowId <0) return -1;
			
			//insert log entry
			//WHERE_WK_LOG_W = COL_LOG_ID + "=% and " + COL_SET_NUM + "=%d";
			
			if(logType == ExType.Weights || logType == ExType.BodyWeight)
			{
				where = String.format(Locale.US, WHERE_WK_LOG_W, rowId,  wkLog.getSet());
				vals = new ContentValues();			
				
				vals.put(COL_REPS, wkLog.getReps());
				vals.put(COL_WEIGHT,wkLog.getWeigt());				
				vals.put(COL_UNITS, DBAdapter.getWeightUnitsDBVal(wkLog.getUnitsLbs()));
				rows = db.update(TB_Workout_log_data_w, vals, where, null);
				if(rows <1)
				{
					//need to insert
					vals.put(COL_LOG_ID, rowId);
					vals.put(COL_SET_NUM, wkLog.getSet());
					try
					{
						db.insertOrThrow(TB_Workout_log_data_w, null, vals);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						return -1;
					}					
				}
				
				//add Max Rep to Exercise table
				double maxrepsweight = wkLog.getMaxRep();  //see if we need to save - only if it > 0
				final boolean isInRange = wkLog.getIsInRange();
				if((logType == ExType.Weights && (maxrepsweight >0 || isInRange))  ||
						logType == ExType.BodyWeight)
				{
					//add max rep to Exercise table
					WorkoutMode wkMode = wkLog.getWorkoutMode();
					if(wkMode == WorkoutMode.Bodybuilding || wkMode == WorkoutMode.Endurance)
					{
						String whereStr = COL_ID + "=" + wkLog.getExerciseId();
						ContentValues maxrepVals = new ContentValues();						
						if(maxrepsweight >0 && logType == ExType.Weights)
						{
							maxrepVals.put(COL_MAXREP, maxrepsweight);
						}						
						if(isInRange || logType == ExType.BodyWeight)
						{
							//save actual weiht / reps val if needed
							if(wkLog.getWorkoutMode() == WorkoutMode.Endurance)
							{
								maxrepVals.put(COL_EN_REPS, wkLog.getReps());
								maxrepVals.put(COL_EN_WT_ACTUAL, wkLog.getWeigt());
							}
							else
							{
								maxrepVals.put(COL_BB_REPS, wkLog.getReps());
								maxrepVals.put(COL_BB_WT_ACTUAL, wkLog.getWeigt());
							}	
							maxrepVals.put(COL_SHOW_ACTUAL, 1);
						}
						else
						{
							maxrepVals.put(COL_SHOW_ACTUAL, 0);
						}
						
						int cnt = db.update(TB_Exercise, maxrepVals, whereStr, null);
						if(cnt <=0)
						{
							if(Common.DEBUG) Log.d(TAG, "Error saving maxrep");
						} 
					}						
				}				
			}			
			else if(logType == ExType.Cardio)
			{				
				vals = new ContentValues();			
				vals.put(COL_TIME, wkLog.getTime());
				vals.put(COL_DISTANCE, wkLog.getDistance());
				vals.put(COL_EQUIPMENT, wkLog.getCardioEquipment());
				vals.put(COL_UNITS,  DBAdapter.getDistUnitsDBVal(wkLog.getUnitsKm()));				
				//we should create new record - in case we are doing cardio again				
				
				
				//check if we have log already saved when we pressed DONE button
				//and update values in the table
				long savedLogId = wkLog.getId();
				if(savedLogId>0)
				{
					if(savedLogId == rowId)
					{
						where = COL_LOG_ID +"=" + rowId;
						rows = db.update(TB_Workout_log_data_c, vals, where, null);
						
					}
					else
					{						
						if(Common.DEBUG) Log.d(TAG, "Cardio - wrong log id in log");
						savedLogId = -1L;
					}					
					
				}
				
				if(savedLogId <= 0)
				{
					vals.put(COL_LOG_ID, rowId);	
					try
					{
						db.insertOrThrow(TB_Workout_log_data_c, null, vals);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						return -1;
					}
				}			

			}
			else
			{
				Log.e(TAG, "addLogData invalid log type " + logType);
				return -1;
			}
			
			return rowId;			
		}
		
		public void UncompleteDaysOnAndAfterDay(final int progrId, final int dayOrder,SQLiteDatabase db)
		{
			String where = COL_PROGR_ID + "=" + progrId + " and " + COL_DAY_ORDER + ">=" + dayOrder;
			db.delete(TB_Day_complete, where, null);
		}
		
		public boolean setDayComplete(final boolean isCompleted, final int progrId, final int weekId,
				final int dayOrder, final long dt, SQLiteDatabase db)
		{
			boolean result = true;
			String where = COL_PROGR_ID + "=" + progrId + " AND " + COL_WEEK_ID + "=" + weekId +
					" AND " + COL_DAY_ORDER + "=" + dayOrder;
			if(isCompleted)
			{
				ContentValues vals = new ContentValues();
				vals.put(COL_DATETIME, dt);
				vals.put(COL_COMPLETE, 1);
				
				if( db.update(TB_Day_complete, vals, where, null) <1)
				{
					vals.put(COL_PROGR_ID, progrId);
					vals.put(COL_WEEK_ID, weekId);
					vals.put(COL_DAY_ORDER, dayOrder);
					try
					{
						db.insertOrThrow(TB_Day_complete, null, vals);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						result=false;
					}
				}			
				
			}
			else
			{
				db.delete(TB_Day_complete, where, null);
			}
			
			return result;			
		}
		
		private static final String SQL_EX_COMPLETE_WHERE=COL_PROGR_ID + "=%d and " + 
				COL_WEEK_ID + "=%d and " + COL_DAY_ID + "=%d and " + 
				COL_WORKOUT_ID + "=%d and " + COL_ENTRY_ID + "=%d";
		
		public boolean setExComplete(final WorkoutLog wkLog, SQLiteDatabase db)
		{
			//check if we have log entry
			String where = String.format(Locale.US, SQL_EX_COMPLETE_WHERE, 
					wkLog.getProgrId(),wkLog.getWeekId(),wkLog.getDayId(),wkLog.getWorkoutId(),wkLog.getEntryId());
			
//			String where =COL_PROGR_ID + "=" + wkLog.getProgrId() +
//					" and " + COL_WEEK_ID + "=" + wkLog.getWeekId() + " and " + COL_DAY_ID + "=" + wkLog.getDayId() + 
//					" and " + COL_WORKOUT_ID + "=" + wkLog.getWorkoutId() + " and " + COL_ENTRY_ID + "=" + wkLog.getEntryId(); 
		
			
			String sql= "select COUNT(*) as cnt from " +TB_Workout_log + " where " + where; 
			
			SQLiteStatement st = mDb.compileStatement(sql);
			long total = st.simpleQueryForLong(); 
			st.close();
			boolean result;
			if(total >0)
			{
				//update
				ContentValues vals = new ContentValues();
				vals.put(COL_COMPLETE, (wkLog.getCompleted())? 1 : 0);
				int rows = db.update(TB_Workout_log, vals, where, null);
				
				result = (rows >0)? true : false;	
				
			}
			else
			{
				//insert
				long res = insertWorkoutLog(wkLog, db);
				
				result = (res >=0)? true : false;				
			}	
			
			return result;
			
		}
		
		
		
		
		private final static String SQL_LAST_LOG_DT=COL_PROGR_ID + "=%d and " + COL_COMPLETE + "=1";
		
		//ret - last datetime and last day order
		public long[] getLastLogDatetime(final int progrId, SQLiteDatabase db)
		{
			String sql = String.format(Locale.US, SQL_LAST_LOG_DT, progrId);
			
//			String sql = COL_PROGR_ID + "=" + progrId +
//					" and " + COL_COMPLETE + "=1";
			
			
			Cursor cur = null;
			try
			{
				cur = db.query(TB_Day_complete, new String[]{COL_DAY_ORDER,COL_DATETIME}, 
						sql, null, null, null, COL_DAY_ORDER + " DESC");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				return null;
			}
			if(cur == null)
			{
				return null;
			}
			else
			{
				if(cur.getCount() == 0) 
				{
					cur.close();
					return null;
				}
				
			}
			long[] result = new long[2];
			
			if(cur.moveToFirst())
			{
				try
				{
					int colDayOrder = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
					int colDatetime = cur.getColumnIndexOrThrow(COL_DATETIME);
					result[1] = cur.getInt(colDayOrder);
					result[0] = cur.getLong(colDatetime);						
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					result = null;
				}
			}
			else result = null;
				
			if(cur != null) cur.close();
			
			return result;
			
			
		}
		
		private final static String SQL_DAYS_IN_PROGR="select  COUNT(day." + COL_ID + ") as total from " +
				TB_Day + " as day inner join " + TB_Week + " as week on week." +
				COL_ID + "=day." + COL_WEEK_ID +
				" where week." + COL_PROGR_ID + "=%d";
		
		public int getDaysInProgram(final int progrId, SQLiteDatabase db)
		{
			String sql = String.format(Locale.US, SQL_DAYS_IN_PROGR, progrId);
			
//			String sql = "select  COUNT(day." + COL_ID + ") as total from " +
//					TB_Day + " as day inner join " + TB_Week + " as week on week." +
//					COL_ID + "=day." + COL_WEEK_ID +
//					" where week." + COL_PROGR_ID + "="  + progrId;
			
			Cursor cur;
			try
			{
				cur = db.rawQuery(sql, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return 0;
			}
			if(cur == null) return 0;
			if(cur.getCount() == 0)
			{
				cur.close();
				return 0;
			}
			
			int res =0;
			if(cur.moveToFirst())
			{
				try
				{
					final int colTotal = cur.getColumnIndexOrThrow("total");
					res = cur.getInt(colTotal);				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					res = 0;
				}
			}
			cur.close();
			return res;
		}
		
		private final static String SQL_PROGR_FOR_CAL="select day." + COL_ID + " as " + COL_ID +
				",day." + COL_MODE + " as " + COL_MODE +
				",day." + COL_DAY_ORDER + " as " + COL_DAY_ORDER +
				",week." + COL_ID + " as " + COL_WEEK_ID +
				",week." + COL_TITLE + " as " + COL_TITLE +
				" from " + TB_Day + " as day inner join " + TB_Week + " as week on week." +
				COL_ID + "=day." + COL_WEEK_ID +
				" where week." + COL_PROGR_ID + "=%d%s order by day." + COL_DAY_ORDER;
		
		public List<DayData> getProgrForSchedule(final int progrId, final int dayTostart,
				 final int maxDays, SQLiteDatabase db)
		{
			//int firstDayOrder = Math.max(lastCompletedDayOrder, startDayOrder);
			
			String sql = String.format(Locale.US, SQL_PROGR_FOR_CAL, 
					progrId,
					((dayTostart >0)?  " and day." + COL_DAY_ORDER + ">=" + dayTostart : ""));
					
//			String sql ="select day." + COL_ID + " as " + COL_ID +
//					",day." + COL_MODE + " as " + COL_MODE +
//					",day." + COL_DAY_ORDER + " as " + COL_DAY_ORDER +
//					",week." + COL_ID + " as " + COL_WEEK_ID +
//					",week." + COL_TITLE + " as " + COL_TITLE +
//					" from " + TB_Day + " as day inner join " + TB_Week + " as week on week." +
//					COL_ID + "=day." + COL_WEEK_ID +
//					" where week." + COL_PROGR_ID + "="  + progrId +					
//					((firstDayOrder >0)?  " and day." + COL_DAY_ORDER + ">" + firstDayOrder : "") +													
//					" order by day." + COL_DAY_ORDER;
			
			Cursor cur;
			try
			{
				cur = db.rawQuery(sql, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			if(cur == null) return null;
			if(cur.getCount() == 0)
			{
				cur.close();
				return null;
			}
			//get data 
			DayData dayData;
			List<DayData> list = new LinkedList<DayData>();
			
			final int colDayMode;
			final int colDayOrder;
			final int colWeekId;
			final int colTitle;
			try
			{
				colDayMode = cur.getColumnIndexOrThrow(COL_MODE);
				colDayOrder = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
				colWeekId = cur.getColumnIndexOrThrow(COL_WEEK_ID);
				colTitle = cur.getColumnIndexOrThrow(COL_TITLE);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				return null;
			}
			int cnt =0;
			while(cur.moveToNext())
			{
				dayData = new DayData();
				if(!cur.isNull(colDayMode))
				{
					String md = cur.getString(colDayMode);
					dayData.workoutMode = WorkoutMode.getMode(md);
				}
				 dayData.dayOrder = cur.getInt(colDayOrder);
				 dayData.weekId = cur.getInt(colWeekId);
				 if(!cur.isNull(colTitle)){
					 dayData.blockTxt = cur.getString(colTitle);
				 }
				 
				 list.add(dayData);
				 cnt++;
				 if(maxDays >0 && cnt >= maxDays)
				 {
					 break;
				 }
			}
			cur.close();
			
			return list;
		}
		
		private final static String SQL_GET_PROGR_WITH_IDS="select d.workout_id as " + COL_WORKOUT_ID + ",d.mode as " + COL_MODE +
				",d._id as " + COL_DAY_ID +
				",wk._id as " + COL_WEEK_ID +
				",wk.week_order as " + COL_WEEK_ORDER + 
				",d.day_order as " + COL_DAY_ORDER +
				" from Week as wk inner join Day as d on d.week_id=wk._id where wk.progr_id=%d and wk._id=%d and d._id=%d order by wk.week_order,d.day_order;";
		
		private final static String SQL_GET_PROGR_WITHOUT_IDS="select d.workout_id as " + COL_WORKOUT_ID + ",d.mode as " + COL_MODE +
				",d._id as " + COL_DAY_ID +
				",wk._id as " + COL_WEEK_ID +
				",wk.week_order as " + COL_WEEK_ORDER + 
				",d.day_order as " + COL_DAY_ORDER +
				" from Week as wk inner join Day as d on d.week_id=wk._id where wk.progr_id=%d and wk.week_order=%d and d.day_order=%d order by wk.week_order,d.day_order;";
		//program data - week and dayOfMonth must be set
		public boolean getProgramFromDb(CurProgram progr, final boolean forWeekDayId, SQLiteDatabase db)
		{
			int progrId = progr.getId();
			if(progrId <0)
			{
				return false;
			}		
			
			
			String sql = (forWeekDayId)? String.format(Locale.US, SQL_GET_PROGR_WITH_IDS, 
					progrId,progr.gettWeek(),progr.getDay())
					: String.format(Locale.US, SQL_GET_PROGR_WITHOUT_IDS, 
							progrId,progr.getWeekOrder(),progr.getDayOrder());
			
//			String sql = (forWeekDayId)? "select d.workout_id as " + COL_WORKOUT_ID + ",d.mode as " + COL_MODE +
//					",d._id as " + COL_DAY_ID +
//					",wk._id as " + COL_WEEK_ID +
//					",wk.week_order as " + COL_WEEK_ORDER + 
//					",d.day_order as " + COL_DAY_ORDER +
//					" from Week as wk inner join Day as d on d.week_id=wk._id where wk.progr_id=" + progrId +
//					" and wk._id=" + progr.gettWeek() +
//					" and d._id=" + progr.getDay() +
//					" order by wk.week_order,d.day_order;" :						
//						"select d.workout_id as " + COL_WORKOUT_ID + ",d.mode as " + COL_MODE +
//						",d._id as " + COL_DAY_ID +
//						",wk._id as " + COL_WEEK_ID +
//						",wk.week_order as " + COL_WEEK_ORDER + 
//						",d.day_order as " + COL_DAY_ORDER +
//						" from Week as wk inner join Day as d on d.week_id=wk._id where wk.progr_id=" + progrId +
//						" and wk.week_order=" + progr.getWeekOrder() +
//						" and d.day_order=" + progr.getDayOrder() +
//						" order by wk.week_order,d.day_order;";
						
			Cursor cur;
			try
			{
				cur = db.rawQuery(sql, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			if(cur == null) return false;
			if(cur.getCount() == 0)
			{
				cur.close();
				return false;
			}
			
			boolean res = true;
			if(cur.moveToFirst())
			{
				try
				{
					final int colWkId = cur.getColumnIndexOrThrow(COL_WORKOUT_ID);
					final int colMode = cur.getColumnIndexOrThrow(COL_MODE);
					final int colDayId = cur.getColumnIndexOrThrow(COL_DAY_ID);
					final int colDayOrder = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
					final int colWeekId = cur.getColumnIndexOrThrow(COL_WEEK_ID);
					final int colWeekOrder= cur.getColumnIndexOrThrow(COL_WEEK_ORDER);
					if(!cur.isNull(colWkId))
					{
						progr.setCurWorkoutId(cur.getInt(colWkId), true);
					}
					else res = false;
					
					if(!cur.isNull(colMode))
					{
						progr.setCurWorkoutMode(WorkoutMode.getMode(cur.getString(colMode)));
					}
					else res = false;
					
					if(forWeekDayId)
					{
						progr.setDayOrder(cur.getInt(colDayOrder));
						progr.setWeekOrder(cur.getInt(colWeekOrder));
					}
					else
					{
						progr.setDay(cur.getInt(colDayId));
						progr.setWeek(cur.getInt(colWeekId));
					}
					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					res = false;
				}
			}
			else res = false;
			cur.close();
			
			return res;
			
		}
		
		private final static String SQL_LAPS_WHERE = COL_USER_ID + "=%d and " + 
				COL_DATETIME + "=%d and " + 
				COL_PROGR_ID + "=%d and " + 
				COL_WORKOUT_ID+"=%d";
		
		public boolean setLapsForTackticalWorkout(final int userId, final int progrId, Workout workout,
				final long date_time,final SQLiteDatabase db)
		{
			String where = String.format(Locale.US, SQL_LAPS_WHERE, 
					userId,date_time,progrId,workout.getId());
			
//			String where = COL_USER_ID + "=" + userId +
//					" and " + COL_DATETIME + "=" + date_time +
//					" and " + COL_PROGR_ID + "=" + progrId+
//					" and " + COL_WORKOUT_ID+"=" + workout.getId();
			
			Cursor cur = null;
			try
			{
				cur = db.query(TB_Workout_log_data_t, new String[]{COL_ID,COL_LAPS}, where, null, null, null, null);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				cur = null;
			}
			if(cur == null) return false;
			final int colLaps;
			try
			{
				colLaps = cur.getColumnIndexOrThrow(COL_LAPS);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				cur = null;
				return false;
			}
			if(cur.moveToFirst())
			{
				if(!cur.isNull(colLaps))
				{
					workout.setLaps(cur.getInt(colLaps));
				}
			}
			cur.close();
			cur=null;
			return true;
			
		}
		
		/**
		 * 
		 * @param userId
		 * @param dayInfo
		 * @param workout
		 * @param date_time
		 * @param db
		 * @return
		 * PRE: workout init 
		 */
		public boolean setCompleteStatusForWorkout(final int userId, final DayInfo dayInfo, Workout workout,
				final long date_time, Gender g,  final WorkoutMode forMode, final SQLiteDatabase db)
		{
			//TODO - set Log data for exercises
			//read log data and set status based on ids
			//we need to read ex_id from TB_Workout_log - what they actually did and
			//save it into exercese data 
			//also get number of sets in Workout_log_data_w they did and save it 
			String where = COL_USER_ID + "=" + userId +
					" and " + COL_DATETIME + "=" + date_time +
					" and " + COL_PROGR_ID + "=" + dayInfo.progrId+
					" and " + COL_WEEK_ID +"=" + dayInfo.weekId +
					" and " + COL_DAY_ID +"=" + dayInfo.dayId +
					" and " + COL_WORKOUT_ID+"=" + dayInfo.workoutId;
			
			String add = null;
			if(forMode == WorkoutMode.Endurance || forMode == WorkoutMode.Bodybuilding || forMode == WorkoutMode.Cardio)
			{
				add = " and " + COL_MODE + "='" + WorkoutMode.getShortTitle2(forMode) + "'";
				where = where + add;
			}
			
			Cursor cur = null;
			try
			{
				cur = db.query(TB_Workout_log, new String[]{COL_ID,COL_ENTRY_ID, COL_EX_ID,COL_COMPLETE}, where, null, null, null, null);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				cur = null;
			}
			if(cur == null) return false;
			final int colEntryId;
			final int colCompl;	
			final int colId;
			final int colExId2;
			try
			{
				colEntryId = cur.getColumnIndexOrThrow(COL_ENTRY_ID);
				colCompl = cur.getColumnIndexOrThrow(COL_COMPLETE);
				colId = cur.getColumnIndexOrThrow(COL_ID);
				colExId2 = cur.getColumnIndexOrThrow(COL_EX_ID);
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				cur = null;
				return false;
			}
			Hashtable<Integer,Boolean> ht = new Hashtable<Integer,Boolean>();
			Hashtable<Integer,Integer> htLogIds = new Hashtable<Integer,Integer>();
			int entry_id, tmp, logId=-1, ex_id2;
			boolean completed;
			while(cur.moveToNext())
			{
				logId = cur.getInt(colId);  //we get log id for this exercise ////////////////////////////////////////////////
				if(cur.isNull(colEntryId) || cur.isNull(colCompl))  //invalid or not completed
				{					
					continue;
				}
				entry_id = cur.getInt(colEntryId);	
				ex_id2 = cur.getInt(colExId2);
				tmp = cur.getInt(colCompl);
				completed = (tmp==1)? true : false;				
				ht.put(Integer.valueOf(entry_id), Boolean.valueOf(completed));
				htLogIds.put(Integer.valueOf(ex_id2), Integer.valueOf(logId));
			}
			cur.close();
			if(ht.size() ==0) return true;
			final int cnt = workout.getExerciseCount();
			Integer key;
			Hashtable<Integer, Integer> htExIds = new Hashtable<Integer, Integer>(); //entry_id = index in workout list
			//get completed status
			for(int i=0; i< cnt;i++)
			{
				entry_id=workout.getExerciseAt(i).getId();
				key = Integer.valueOf(entry_id);
				if(ht.containsKey(key))
				{
					completed = ((Boolean)ht.get(key)).booleanValue();					
					workout.setExCompleted(i, completed);
				}
				htExIds.put(key, Integer.valueOf(i));
				
			}
			ht.clear();
			ht=null;
			
			//get log data if any by logId
			//get Weight - we need max set number
			where = " having log." + COL_USER_ID + "=" + userId +
					" and log." + COL_DATETIME + "=" + date_time +
					" and log." + COL_PROGR_ID + "=" + dayInfo.progrId+
					" and log." + COL_WEEK_ID +"=" + dayInfo.weekId +
					" and log." + COL_DAY_ID +"=" + dayInfo.dayId +
					" and log." + COL_DATETIME +"=" + date_time;
			
			if(add != null)
			{
				where = where + add;
			}
			
			/*select max(d.set_num) as MAXSET, log.entry_id as entryid, log.ex_id as exid from Workout_log as log inner join Workout_log_data_w as d on log._id=d.log_id group by log.entry_id,log.ex_id having log.datetime=1379660400000;
			 * 
			 */
					
//			String qry ="select MAX(d."+ COL_SET_NUM +") as MAXSET, lg." + COL_ENTRY_ID + " as "+ COL_ENTRY_ID +
//					",lg." + COL_EX_ID +" as "  + COL_EX_ID +
//					" from " + TB_Workout_log + " as lg inner join " + TB_Workout_log_data_w + " as d on lg." +
//					COL_ID + "=d." + COL_LOG_ID + " where lg." + where;
			
			String qry = "select max(d.set_num) as MAXSET, log." + COL_ENTRY_ID + " as " + COL_ENTRY_ID+
					", log." + COL_EX_ID + " as " + COL_EX_ID +
					" from " + TB_Workout_log +" as log inner join " +
					TB_Workout_log_data_w + " as d on log." + COL_ID +"=d." + COL_LOG_ID +
					" group by log." + COL_ENTRY_ID +"," + 
					"log." + COL_EX_ID + where;
			
			try
			{
				cur = db.rawQuery(qry, null);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				cur = null;
			}
			if(cur != null)
			{
				boolean ok = true;
				if(cur.getCount() >0)
				{
					//get data
					int colEntry_id = 0;
					int colExId=0;
					int colSets=0;
					try
					{
						colEntry_id = cur.getColumnIndexOrThrow(COL_ENTRY_ID); 
						colExId = cur.getColumnIndexOrThrow(COL_EX_ID);
						colSets = cur.getColumnIndexOrThrow("MAXSET");
						
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						ok=false;				
					}
					if(ok)
					{
						int  ex_id, max_set, ind=0;
						Exercise exer;
						//htExIds						
						while(cur.moveToNext())
						{
							entry_id = cur.getInt(colEntry_id);  //0  ?????  ///////////////////////////
							ex_id = (cur.isNull(colExId))? -1 : cur.getInt(colExId);
							max_set= cur.getInt(colSets);
							
							key = Integer.valueOf(entry_id);
							if(htExIds.containsKey(key))
							{
								ind=htExIds.get(key).intValue(); 
								exer = workout.getExerciseAt(ind);
								
								key = Integer.valueOf(ex_id);
								if(htLogIds.containsKey(key))
								{
									logId = htLogIds.get(key).intValue();
								}
								else logId =-1; //invalid
								
								exer.setMaxSetAnLogId(max_set, ex_id, g, logId);
							}							
						}
					}
					
				}
//				else
//				{
//					//no records
//				}
				cur.close();
				htExIds = null;
			}
			
			return true;			
		}
		
		
		private final static String SQL_TAC_LOGS_CHECK_MODULE="select 1 from " + TB_Workout_log_data_t + " where " +
				COL_USER_ID + "=%d and " + 
				COL_DATETIME + "=%d and " + 
				COL_PROGR_ID + "=%d and " + 
				COL_WORKOUT_ID+"=%d and (" + COL_WEEK_ID + " is null or " + COL_WEEK_ID +"<=0)";
		
		private final static String SQL_TAC_LOGS_CHECK_PROGR="select 1 from " + TB_Workout_log_data_t + " where " +
				COL_USER_ID + "=%d and " + 
				COL_DATETIME + "=%d and " + 
				COL_PROGR_ID + "=%d and " + 
				COL_WORKOUT_ID+"=%d and " + COL_WEEK_ID + "=%d and " + COL_DAY_ORDER +"=%d";
				
		
		public boolean checkIfHaveTactLogs(final int userId, final int progrId, final int wkId,
				final long date_time, final int weekId, final int dayOrder,final SQLiteDatabase db)
		{
			String sql = (weekId <=0)? String.format(Locale.US, SQL_TAC_LOGS_CHECK_MODULE, 
					userId,date_time,progrId,wkId) :
						String.format(Locale.US, SQL_TAC_LOGS_CHECK_PROGR, 
								userId,date_time,progrId,wkId, weekId, dayOrder);
			
//			
//			String sql = "select 1 from " + TB_Workout_log_data_t + " where " +
//					COL_USER_ID + "=" + userId +
//					" and " + COL_DATETIME + "=" + date_time +
//					" and " + COL_PROGR_ID + "=" + progrId+
//					" and " + COL_WORKOUT_ID+"=" + wkId;
			return checkHaveLogs(sql, db);
		}
		
		
		private final static String SQL_HAVE_LOGS= "select 1 from " + TB_Workout_log + " where " + 
				COL_USER_ID + "=%d and " + 
				COL_DATETIME + "=%d and " + 
				COL_PROGR_ID + "=%d and " + 
				COL_WEEK_ID +"=%d and " + 
				COL_DAY_ID +"=%d and " + COL_WORKOUT_ID+"=%d";
		
		public boolean checkIfHaveLogs(final int userId, final DayInfo dayInfo, final long date_time,
				final SQLiteDatabase db)
		{
			String sql = String.format(Locale.US, SQL_HAVE_LOGS, 
					userId,date_time,dayInfo.progrId,dayInfo.weekId,dayInfo.dayId,dayInfo.workoutId);
			
//			String sql = "select 1 from " + TB_Workout_log + " where " + 
//						COL_USER_ID + "=" + userId +
//						" and " + COL_DATETIME + "=" + date_time +
//						" and " + COL_PROGR_ID + "=" + dayInfo.progrId+
//						" and " + COL_WEEK_ID +"=" + dayInfo.weekId +
//						" and " + COL_DAY_ID +"=" + dayInfo.dayId +
//						" and " + COL_WORKOUT_ID+"=" + dayInfo.workoutId;
			
			return checkHaveLogs(sql, db);
		}
		
		//For Endurance and Mass
		public boolean checkIfHaveLogsForModule(final int userId, final int progrId, final long date_time, 
				final int workoutId, final WorkoutMode md,final SQLiteDatabase db	)
		{
			String sql =  "select 1 from " + TB_Workout_log + " where " + 
					COL_USER_ID + "=" + userId +
					" and " + COL_DATETIME + "=" + date_time +
					" and " + COL_PROGR_ID + "=" + progrId+
					" and " + COL_WEEK_ID +"=" + Common.ZERO_ID +
					" and " + COL_DAY_ID +"=" + Common.ZERO_ID +
					" and " + COL_WORKOUT_ID+"=" + workoutId +
					" and " + COL_MODE + "='" + WorkoutMode.getShortTitle2(md)+"'";
			
			return checkHaveLogs(sql, db);
		}
		
		private boolean checkHaveLogs(final String sql, SQLiteDatabase db)
		{
			Cursor cur;
			try
			{
				cur = db.rawQuery(sql, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			if(cur == null) return false;
			boolean res = (cur.getCount() > 0)? true : false;
			cur.close();
			return res;
		}
		
		//delete one log entry
		public boolean deleteWeightLogEntry(final long logId, final int setNum, SQLiteDatabase db)
		{
			boolean ok = true;
			try
			{
				ok = (db.delete(TB_Workout_log_data_w, COL_LOG_ID+"=" +logId +" AND " + COL_SET_NUM + "=" + setNum, null) >0)?
						true : false;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				ok=false;
			}
			return ok;
		}
		
		public void deleteAllLogs(SQLiteDatabase db)
		{
			db.delete(TB_Workout_log_data_t, null, null);
			db.delete(TB_Workout_log_data_c, null, null);
			db.delete(TB_Workout_log_data_w, null, null);
			db.delete(TB_Workout_log, null, null);
		}
				
		public long getDatetimeDayWasLastCompleted(final int progrId, final int weekId, final int dayOrder,
				SQLiteDatabase db)
		{
			Cursor cur;
			try
			{
				cur = db.query(TB_Day_complete, 
						new String[]{COL_DATETIME}, 
						COL_PROGR_ID + "=" + progrId + 
						" and " + COL_WEEK_ID + "=" + weekId +
						" and " + COL_DAY_ORDER + "=" + dayOrder, 
						null, null, null, COL_DATETIME + " DESC");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return -1;
			}
			if(cur == null) return -1;
			if(cur.getCount() == 0)
			{
				if(cur != null)
				{
					cur.close();
				}
				return 0;
			}
			
			long res = 0;
			if(cur.moveToFirst())
			{
				try
				{
					final int colDt = cur.getColumnIndexOrThrow(COL_DATETIME);
					res = cur.getLong(colDt);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					res=0;
				}
			}
			cur.close();
			return res;
		}
		
		//for workout on a particular day and datetime
		//  delete log with given id for given date (workout id required)		
		public boolean deleteLogForWorkoutOnDate(final int userId, final int workoutId, final int progrId, 
				final WorkoutMode md,
				final long date_time,
				SQLiteDatabase db)
		{
			//have to dist. btw deleting log with workout ID and deleting logs for dates
			
			switch(md)
			{
			case Endurance:
			case Bodybuilding:
			case Cardio:
			case Weight:
			case All:
				boolean res =  deleteLogForWECWorkout(userId, workoutId,progrId, date_time, false, db);
				boolean res2 = deleteLogForTWorkout(userId, progrId, date_time, workoutId, false, db);
				return (res && res2);
			case Tactical:
				return deleteLogForTWorkout(userId, progrId, date_time, workoutId,  false,db);
			default: return false;
			}
		}
		
		
		//for WorkoutNode = All only
		//	    - delete all logs folloing given day with given day order or on and after date 
		public boolean deleteLogsOnAfterDate(final int userId, final int progrId, 				
				final long date_time,
				final int  weekId,final int dayOrder,
				SQLiteDatabase db)
		{
			//have to dist. btw deleting log with workout ID and deleting logs for dates			
			boolean res =  deleteLogForWECWorkout(userId, 0,progrId, date_time, true, db);
			boolean res2 = deleteLogForTWorkout(userId, progrId, date_time, 0, true, db);
			UncompleteDaysOnAndAfterDay(progrId, dayOrder, db);		
			return (res && res2);
		}
		
		//Tacktical- deletes log with given id for a given date OR logs on or after given date
		private boolean deleteLogForTWorkout(final int userId, final int progrId, 
				final long date_time,final int workoutId, final boolean allLogsOnAndAfter,
				SQLiteDatabase db)
		{	
			final String where = (allLogsOnAndAfter)? 
					// COL_WORKOUT_ID+"=" +workoutId + 
					    COL_DATETIME + ">=" + date_time+
						" and " + COL_USER_ID + "=" + userId + 
						" and " + COL_PROGR_ID+"=" + progrId +
						" and " + COL_WEEK_ID + ">0"
						
						
					: COL_DATETIME + "=" + date_time+
					" and " + COL_USER_ID + "=" + userId + 
					" and " + COL_PROGR_ID+"=" + progrId +
					" and " + COL_WORKOUT_ID + "=" + workoutId;
					
			try
			{
				db.delete(TB_Workout_log_data_t, where, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			return true;
		}
		
		//Weight, endurance, cardio
		//delete logs for this workout on this date OR deletes all logs on and following date
		private boolean deleteLogForWECWorkout(final int userId, final int workoutId, final int progrId, 
				final long date_time, 
				final boolean allLogsOnAndAfter, SQLiteDatabase db)
		{
			//get all logs for this workout
			Cursor cur = null;
			boolean ok = true;
			final String where = (allLogsOnAndAfter)?
					COL_DATETIME + ">=" + date_time+
					" and " + COL_USER_ID + "=" + userId + 
					" and " + COL_PROGR_ID+"=" + progrId +
					" and " + COL_WEEK_ID + ">" + Common.ZERO_ID
					
					: COL_WORKOUT_ID+"=" +workoutId + 
					" and " + COL_DATETIME + "=" + date_time+
					" and " + COL_USER_ID + "=" + userId + 
					" and " + COL_PROGR_ID+"=" + progrId;
			try
			{
				cur = db.query(TB_Workout_log, new String[]{COL_ID}, 
						where, null, null, null, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				ok=false;
			}
			if(cur == null) return false;
			if(!ok)
			{
				if(cur != null)
				{
					cur.close();
				}
				return false;
			}
			final int colId;
			try
			{
				colId = cur.getColumnIndexOrThrow(COL_ID);
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				return false;
			}	
			
			int id;
			String str;
			boolean grandResult = true;
			while(cur.moveToNext())
			{
				id = cur.getInt(colId);
				str = COL_LOG_ID+"=" +id;
				//delete from sub-tables
				try
				{
					db.delete(TB_Workout_log_data_w, str , null);
					db.delete(TB_Workout_log_data_c, str , null);					
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					ok=false;
				}
				if(!ok) grandResult = false;				
			}
			cur.close();
			cur = null;
			
			//delete main log
			try
			{
				db.delete(TB_Workout_log, where, null);
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				ok=false;
			}
			if(!ok) grandResult = false;
			return grandResult;
		}
		
		public LinkedList<LogData> getLogDataForWeight(final long logId, SQLiteDatabase db)
		{
			Cursor cur = null;
			boolean ok = true;
			try
			{
				cur = db.query(TB_Workout_log_data_w, new String[]{COL_SET_NUM, COL_REPS, COL_WEIGHT}, 
						COL_LOG_ID+"=" +logId, null, null, null, COL_SET_NUM);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				ok=false;
			}
			if(cur == null) return null; 
			if(!ok)
			{
				if(cur != null)
				{
					cur.close();
				}
				return null;
			}
			//int cnt = cur.getCount();
			if(cur.getCount() == 0)
			{
				if(cur != null)
				{
					cur.close();
				}
				return null;
			}
			
			final int colSet;
			final int colReps;
			final int colWeight;
			try
			{
				colSet = cur.getColumnIndexOrThrow(COL_SET_NUM);
				colReps = cur.getColumnIndexOrThrow(COL_REPS);
				colWeight = cur.getColumnIndexOrThrow(COL_WEIGHT);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				if(cur != null) cur.close();
				return null;
			}	
			int setNum, reps, wt;
			LogData data;
			LinkedList<LogData> list = new LinkedList<LogData>();
			while(cur.moveToNext())
			{
				if(!cur.isNull(colSet)){
					setNum = cur.getInt(colSet);					
				}
				else setNum =-1;
				
				if(!cur.isNull(colReps)) reps = cur.getInt(colReps);
				else reps =-1;
				
				if(!cur.isNull(colWeight)) wt = cur.getInt(colWeight);
				else wt =-1;
				
				data = new LogData(setNum, wt, reps);
				list.add(data);
			}
			if(cur != null) cur.close();
			
			return list;
		}
		
		////////// One Max Rep for exercise
		//retirn [0]= max_rep_weight, [1]=weight wheel, [2]=repos wheel
//		public int[] getOneMaxRep(final int exId, final int userId, SQLiteDatabase db)
//		{
//			Cursor cur = null;
//			int[] res = new int[3];
//			
//			try
//			{
//				cur = db.query(TB_User_ex_preset, new String[]{COL_MAX_REP_WEIGHT, COL_WEIGHT, COL_REPS}, 
//						COL_EX_ID+ "=" + exId + " and " + COL_USER_ID + "=" + userId, 
//						null, null, null, null);
//				if(cur == null) return null;
//				if(cur.getCount() ==0 || !cur.moveToFirst())
//				{
//					cur.close();
//					return null;
//				}
//				int ind = cur.getColumnIndexOrThrow(COL_MAX_REP_WEIGHT);
//				if(!cur.isNull(ind))
//				{
//					res[0] = cur.getInt(ind);
//				}
//				else
//				{
//					res[0]=-1;
//				}
//				ind = cur.getColumnIndexOrThrow(COL_WEIGHT);
//				if(!cur.isNull(ind))
//				{
//					res[1] = cur.getInt(ind);
//				}
//				else
//				{
//					res[1]=-1;
//				}
//				ind = cur.getColumnIndexOrThrow(COL_REPS);
//				if(!cur.isNull(ind))
//				{
//					res[2] = cur.getInt(ind);
//				}
//				else
//				{
//					res[2]=-1;
//				}
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//				res = null;
//			}
//			finally
//			{
//				if(cur != null)
//				{
//					cur.close();
//				}
//			}
//			if(cur == null) return null;
//			
//			return res;
//		}
		
		private final static String SQL_FIRST_PROGR_DAY="select w._id as WEEKID,d._id as DAYID from " + TB_Week +" as w inner join "+
				TB_Day + " as d on d.week_id=w._id where w.progr_id=%d and w.week_order=1 and d.day_order=1";
		
		private boolean getFirstProgrDayInfo(DayInfo dayInfo, SQLiteDatabase db)
		{
			Cursor cur;
			
			String qry = String.format(Locale.US, SQL_FIRST_PROGR_DAY, 
					dayInfo.progrId);			
			
//			String qry = "select w._id as WEEKID,d._id as DAYID from " + TB_Week +" as w inner join "+
//					TB_Day + " as d on d.week_id=w._id where w.progr_id=" + dayInfo.progrId +
//					" and w.week_order=1 and d.day_order=1";
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();				
				return false;
			}
			if(cur == null) return false;
			if(!cur.moveToFirst())
			{
				cur.close();
				return false;
			}
			
			final int colWeekId;
			final int colDayId;
			try
			{
				colWeekId = cur.getColumnIndexOrThrow("WEEKID");
				colDayId = cur.getColumnIndexOrThrow("DAYID");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();	
				cur.close();
				return false;
			}
			dayInfo.dayId = cur.getInt(colDayId);
			dayInfo.weekId = cur.getInt(colWeekId);
			cur.close();
			return true;
		}
		
		private boolean getNextDayInfo(DayInfo dayInfo, final int dayOrder,SQLiteDatabase db)
		{
			Cursor cur;
			
			//COL_WEEK_ID + ">=" + weekId + " and " + COL_WEEK_ID + "<=" + nextWeek
			final String qry = COL_DAY_ORDER +">" + dayOrder + " and " + COL_MODE +"!='R'";
			try
			{
				
				//TODO - add where for program
				cur = db.query(TB_Day, new String[]{COL_ID, COL_WEEK_ID, COL_WORKOUT_ID, COL_MODE,COL_DAY_ORDER}, 
						qry, 
						null, null, null, COL_DAY_ORDER);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();				
				return false;
			}
			if(cur == null)
			{
				return false;
			}
			if(cur.getCount() ==0)
			{
				cur.close();
				int total = this.getDaysInProgram(dayInfo.progrId, db);
				if(total == dayOrder)
				{
					//we completed the program
					dayInfo.dayOrder = CurProgram.PROGR_COMPLETE_DAYORDER;
					return true;
				}
				
				return false;
			}
			final int colDayId;
			final int colWeekId;
			final int colWkId;
			final int colMode;
			final int colDayOrd;
			
			try
			{
				colDayId = cur.getColumnIndexOrThrow(COL_ID);
				colWeekId = cur.getColumnIndexOrThrow(COL_WEEK_ID);
				colWkId = cur.getColumnIndexOrThrow(COL_WORKOUT_ID);
				colMode = cur.getColumnIndexOrThrow(COL_MODE);		
				colDayOrd = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();				
				cur.close();
				return false;
			}
			
			boolean found = false;
			if(cur.moveToFirst())
			{				
				dayInfo.weekId = cur.getInt(colWeekId);
				dayInfo.dayId = cur.getInt(colDayId);
				dayInfo.workoutId = cur.getInt(colWkId);
				String md = cur.getString(colMode);
				dayInfo.workoutMode = WorkoutMode.getMode(md);
				dayInfo.dayOrder = cur.getInt(colDayOrd);
				found = true;
			}			
			cur.close();
			return found;
			
		}
		
		//ret id, title and description
		//based on mode
		public List<WorkoutInfo> getWorkoutsInfoByMode(final WorkoutMode md, SQLiteDatabase db)
		{
			Cursor cur;
			String modeStr = WorkoutMode.getShortTitle2(md);
			try
			{
				//TODO - add where for program
				cur = db.query(TB_Workout, new String[]{COL_ID, COL_TITLE, COL_DESCRIPTION, COL_TIME}, 
						COL_MODE+ "='" + modeStr + "'", 
						null, null, null, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			if(cur == null)
			{
				return null;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return null;
			}
			
			final int colId;
			final int colTitle;
			final int colDescription;
		
			try
			{
				colId = cur.getColumnIndexOrThrow(COL_ID);
				colTitle = cur.getColumnIndexOrThrow(COL_TITLE);
				colDescription = cur.getColumnIndexOrThrow(COL_DESCRIPTION);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return null;
			}
			WorkoutInfo wkInfo;
			List<WorkoutInfo> list = new ArrayList<WorkoutInfo>();
			
			if(cur.moveToFirst())
			{
				wkInfo = new WorkoutInfo();
				if(!cur.isNull(colId)) wkInfo.id = cur.getInt(colId);
				if(!cur.isNull(colTitle)) wkInfo.title = cur.getString(colTitle);
				if(!cur.isNull(colDescription)) wkInfo.description = cur.getString(colDescription);
				
				//TODO - COL_TIME	
				
				list.add(wkInfo);
			}
			else{
				cur.close();
				return null;
			}
			cur.close();
			wkInfo=null;
			return list;
		}
		
		
		//
		//TODO - get all of them later
		/**
		 * get  Tacktical workout based on workout id
		 * @param workout 
		 * @param progrId
		 * @param db
		 * @return
		 */
//		public boolean getTackticalWorkout(Workout workout, final int progrId, SQLiteDatabase db)
//		{
//			//String qry="select _id, title,description,time from Workout where mode='T'";
//			Cursor cur;
//			
//			//we need to get exercises for this workout
//			//select ex._id as ex_id, ex.title as title, ex.description as description, ex.img_male as img_male, 
//			//ex.img_female as img_femail, ex.mode as mode, ex.level as level, ex.en_weight as en_weight, 
//			//ex.bb_weight as bb_weight from Workout_entry as wk inner join Exercise as ex on wk.entry_id=ex._id 
//			//where wk.workout_id=3 and wk.entry_type='E';
//			
////			String qry = "select ex." + COL_ID + " as " +COL_ID +", ex." + COL_TITLE + " as " + COL_TITLE +
////					",ex." + COL_DESCRIPTION + " as " +COL_DESCRIPTION +
////					", ex." + COL_IMG_MALE + " as " + COL_IMG_MALE +
////					",ex." + COL_IMG_FEMALE + " as " +  COL_IMG_FEMALE+
////					", ex." + COL_MODE +" as " +COL_MODE +
////					", ex." + COL_LEVEL + " as " + COL_LEVEL +
////					", ex." + COL_EN_WEIGHT +" as " + COL_EN_WEIGHT +
////					",ex." + COL_BB_WEIGHT + " as " + COL_BB_WEIGHT +
////					" from " + TB_Workout_entry + " as wk inner join " + TB_Exercise + " as ex on wk." + COL_ENTRY_ID +
////					"=ex." + COL_ID + " where wk." + COL_WORKOUT_ID + "=" + workout.getId() +
////					" and wk." + COL_ENTRY_TYPE + "='E' order by wk." + COL_ENTRY_ORDER;
////			
//
//			
//			//use getWorkout() to get workout
//			//need DayInfo 
//			//select d.day_order as dayorder, wk.week_order as weekorder from Day as d inner join Week as wk on d.week_id=wk._id 
//			//where d.workout_id=3;
//			String qry= "select d." + COL_DAY_ORDER + " as " + COL_DAY_ORDER+
//					", wk." + COL_WEEK_ORDER + " as " + COL_WEEK_ORDER +
//					" from " +TB_Day + " as d inner join " + TB_Week + " as wk on d." + COL_WEEK_ID +"=wk." + COL_ID +
//					" where d." + COL_WORKOUT_ID + "=" + workout.getId();
//			
//			try
//			{
//				cur = db.rawQuery(qry, null);
//			}
//			catch(Exception ex)
//			{
//				ex.printStackTrace();
//				return false;
//			}
//			if(cur == null) return false;
//			
//			DayInfo dayInfo = null;
//			
//			if(cur.moveToFirst())
//			{
//				dayInfo = new DayInfo();
//				dayInfo.progrId = progrId;
//				dayInfo.workoutId=workout.getId();
//				dayInfo.curExOrder = 0;
//				try
//				{
//					dayInfo.weekIndex = cur.getInt(cur.getColumnIndexOrThrow(COL_WEEK_ORDER));
//					dayInfo.dayIndex= cur.getInt(cur.getColumnIndexOrThrow(COL_DAY_ORDER));;
//				}
//				catch(Exception ex)
//				{
//					ex.printStackTrace();
//					dayInfo=null;
//				}
//				
//			}
//			cur.close();
//			if(dayInfo == null) return false;
//			return getWorkoutForDay(dayInfo, workout, db);
//			
//		}
		

		public boolean getWorkoutForMode(Workout workout, final int workoutId, final WorkoutMode md, final long log_datetime, SQLiteDatabase db)
		{
			final String sql = getWorkoutListStrForMode(md, workoutId, log_datetime);
			return getWorkout(sql,workout, workoutId, db);
		}
		
		public int getWorkoutForModeCount(final WorkoutMode md, SQLiteDatabase db)
		{
			String where = COL_MODE +"='" + WorkoutMode.getShortTitle2(md) + "'";
			String qry =  "select COUNT(" + COL_ID + ") as cnt from " + TB_Workout + 
					" where " + where;
			Cursor cur;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return -1;
			}
			 
			if(cur == null)
			{
				return -1;
			}
			
			int res = -1;
			final int colCnt;
			try
			{
				colCnt= cur.getColumnIndexOrThrow("cnt");
				res = cur.getInt(colCnt);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				res=-1;
			}
			cur.close();
			return res;
			
		}
		
		
		//Workout for a dayOfMonth
		public boolean getWorkoutForDay(final DayInfo dayInfo, Workout workout,SQLiteDatabase db)
		{
			final String sql = getWorkoutListStr(dayInfo);
			return getWorkout(sql,workout, dayInfo.workoutId, db);
		}
		
		
		private boolean getWorkout(final String sql, Workout workout, final int workoutId, SQLiteDatabase db)
		{			
			Cursor cur;
			try
			{
				cur = db.rawQuery(sql, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			 
			if(cur == null)
			{
				return false;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return false;
			}
			//columns
			final int colEntryId;
			final int colEntryType;
			final int colEntryOrder;
			final int colTitle;
			final int colDescription;
			final int colImgMale;
			final int colImgFemale;
			final int colMode;
			final int colCompleted;
			final int colSets;
			final int colLevel;
			final int colWeight;
			//final int colReps;
			final int colCardioTime;
			
			//actual
			final int colEnReps;
			final int colEnWt;
			final int colBBReps;
			final int colBBWt;
			final int colShowActual;
			
			final WorkoutMode mode = workout.getMode();
			try
			{
				colEntryId = cur.getColumnIndexOrThrow(COL_ENTRY_ID);
				colEntryType = cur.getColumnIndexOrThrow(COL_ENTRY_TYPE);
				
				colEntryOrder = cur.getColumnIndexOrThrow(COL_ENTRY_ORDER);
				colTitle = cur.getColumnIndexOrThrow(COL_TITLE);
				colDescription = cur.getColumnIndexOrThrow(COL_DESCRIPTION);
				colImgMale = cur.getColumnIndexOrThrow(COL_IMG_MALE);
				colImgFemale = cur.getColumnIndexOrThrow(COL_IMG_FEMALE);
				colMode = cur.getColumnIndexOrThrow(COL_MODE);
				colCompleted = cur.getColumnIndexOrThrow(COL_COMPLETE);	
				colSets = cur.getColumnIndexOrThrow(COL_SETS);
				colLevel = cur.getColumnIndexOrThrow(COL_LEVEL);
				
				colCardioTime = cur.getColumnIndexOrThrow(COL_TIME);
				
				//actual
				colEnReps = cur.getColumnIndexOrThrow(COL_EN_REPS);
				colEnWt = cur.getColumnIndexOrThrow(COL_EN_WT_ACTUAL);
				colBBReps = cur.getColumnIndexOrThrow(COL_BB_REPS);
				colBBWt = cur.getColumnIndexOrThrow(COL_BB_WT_ACTUAL);		
				colShowActual = cur.getColumnIndexOrThrow(COL_SHOW_ACTUAL);	
				
				if(mode == WorkoutMode.Bodybuilding || mode == WorkoutMode.Endurance)
				{
					colWeight = cur.getColumnIndexOrThrow(COL_MAXREP);
					//colReps = cur.getColumnIndexOrThrow(COL_BB_REPS);
				}				
				else
				{
					colWeight=-1;					
				}				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}			
			Exercise exer;
			int exId;
			ExData data;
			String md, entryTypeStr;
			EntryType entryType ;
			final String altStr = getAlts();
			while(cur.moveToNext())
			{
				if(!cur.isNull(colEntryType))
				{
					entryTypeStr = cur.getString(colEntryType);
					entryType = EntryType.getEntryTypeFromStr(entryTypeStr);
					
				}
				else
				{
					//ERROR
					entryType = EntryType.Unknown;
				}
				if(entryType != EntryType.Exercise)
				{					
					cur.close();
					return false; //for now  - TODO
				}
				exId = cur.getInt(colEntryId);
				data = new ExData(exId);
				data.setTitle(cur.getString(colTitle));
				data.setDescription(cur.getString(colDescription));
				if(!cur.isNull(colImgMale))
				{
					data.setImgMale(cur.getString(colImgMale));
				}
				if(!cur.isNull(colImgFemale))
				{
					data.setImgFemale(cur.getString(colImgFemale));
				}
				if(!cur.isNull(colMode))
				{
					md = cur.getString(colMode);
					data.setTypeFromStr(md);
				}
				else data.setExType(ExType.Unknown); // ERROR
				
				if(!cur.isNull(colSets))
				{
					data.setSets(cur.getInt(colSets));
				}
				if(!cur.isNull(colLevel))
				{
					data.setLevel(cur.getInt(colLevel));
				}
				
				//Recommended
				if(colWeight >=0)
				{
					double recWt=0.0;
					if(!cur.isNull(colWeight))
					{
						recWt = cur.getDouble(colWeight);
					}					
					data.setMaxrep(recWt);
				}
				//actual values
				if(!cur.isNull(colShowActual))
				{
					final int actual = cur.getInt(colShowActual);
					if(actual ==1)
					{
						int reps, wt;
						if(!cur.isNull(colEnReps)) 	reps = cur.getInt(colEnReps);						
						else reps =0;
						if(!cur.isNull(colEnWt))  wt = cur.getInt(colEnWt);
						else wt =0;						
						data.setActual(wt, reps, WorkoutMode.Endurance);
						
						if(!cur.isNull(colBBReps)) 	reps = cur.getInt(colBBReps);						
						else reps =0;
						if(!cur.isNull(colBBWt))  wt = cur.getInt(colBBWt);
						else wt =0;			
						data.setActual(wt, reps, WorkoutMode.Bodybuilding);						
					}
					else
					{
						data.setActual(0,0, WorkoutMode.Endurance);
						data.setActual(0,0, WorkoutMode.Bodybuilding);			
					}
				}
				else
				{
					data.setActual(0,0, WorkoutMode.Endurance);
					data.setActual(0,0, WorkoutMode.Bodybuilding);		
				}
				
				if(!cur.isNull(colCardioTime))
				{
					data.setMinutes(cur.getInt(colCardioTime));
				}
				
				//Init Exercise from ExDAta
				exer = new Exercise(data);			
				
				if(cur.isNull(colCompleted))
				{
					exer.setCompleted(false);
				}
				else
				{
					int val = cur.getInt(colCompleted);
					if(val == 1) exer.setCompleted(true);
					else exer.setCompleted(false);					
					
				}
				exer.setIndex(cur.getInt(colEntryOrder));
				
				//get alternatives if any
				String qryAlts = String.format(altStr, exer.getId(), workoutId);
				Cursor curAlts = db.rawQuery(qryAlts, null);
				if(curAlts != null)
				{
					if(curAlts.getCount() == 0)
					{
						curAlts.close();
					}
					else
					{
						//get alts
						boolean res = true;
						int colAltsExId = -1;
						int colAltsGender=-1;
						int colAltsTitle=-1;
						int colAltsDesc=-1;
						int colAltsImgM=-1;
						int colaltImgF=-1;
						int colAltsMode=-1;
						int colAltsLevel=-1;
						//int colAltsWeight=-1;
						//int colAltsReps = -1;
						try
						{
							colAltsExId = curAlts.getColumnIndexOrThrow(COL_EX_ID);
							
							colAltsGender = curAlts.getColumnIndexOrThrow(COL_GENDER);
							colAltsTitle = curAlts.getColumnIndexOrThrow(COL_TITLE);
							colAltsDesc = curAlts.getColumnIndexOrThrow(COL_DESCRIPTION);
							colAltsImgM = curAlts.getColumnIndexOrThrow(COL_IMG_MALE);
							colaltImgF = curAlts.getColumnIndexOrThrow(COL_IMG_FEMALE);
							colAltsMode = curAlts.getColumnIndexOrThrow(COL_MODE);
							colAltsLevel = curAlts.getColumnIndexOrThrow(COL_LEVEL);
//							if(mode == WorkoutMode.Bodybuilding)
//							{
//								colAltsWeight = curAlts.getColumnIndexOrThrow(COL_BB_WEIGHT);
//								//colAltsReps = curAlts.getColumnIndexOrThrow(COL_BB_REPS);
//							}
//							else if(mode == WorkoutMode.Endurance)
//							{
//								colAltsWeight = curAlts.getColumnIndexOrThrow(COL_EN_WEIGHT);
//								//colAltsReps = curAlts.getColumnIndexOrThrow(COL_EN_REPS);
//							}
//							else
//							{
//								colAltsWeight=-1;
//								//colAltsReps=-1;
//							}				
							
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
							res = false;
						}
						if(res)
						{
							ExData altData;
							while(curAlts.moveToNext())
							{
								int altId = curAlts.getInt(colAltsExId);
								altData = new ExData(altId);
								if(!curAlts.isNull(colAltsGender))
								{
									altData.setGender(Gender.getGenderFromStr(curAlts.getString(colAltsGender)));
								}
								if(!curAlts.isNull(colAltsTitle))
								{
									altData.setTitle(curAlts.getString(colAltsTitle));
								}
								if(!curAlts.isNull(colAltsDesc))
								{
									altData.setDescription(curAlts.getString(colAltsDesc));
								}
								if(!curAlts.isNull(colAltsImgM))
								{
									String s = curAlts.getString(colAltsImgM);
									altData.setImgMale(s);
								}
								if(!curAlts.isNull(colaltImgF))
								{
									altData.setImgFemale(curAlts.getString(colaltImgF));
								}
								if(!curAlts.isNull(colAltsMode))
								{
									md = curAlts.getString(colAltsMode);
									altData.setTypeFromStr(md);
								}
								if(!curAlts.isNull(colAltsLevel))
								{
									altData.setLevel(curAlts.getInt(colAltsLevel));
								}
								altData.setSets(data.getRecommendedSets());
								
								//Recommended
//								if(colAltsWeight >=0 )
//								{
//									int recWt=0, recReps=0;
//									if(!curAlts.isNull(colAltsWeight))
//									{
//										recWt = curAlts.getInt(colAltsWeight);
//									}
//									
//								}				
								
								//add to Exercise
								if(altData.getGender() == Gender.Female)
								{
									exer.addAltFemale(altData);
								}
								else exer.addAltMale(altData);
							}						
							
						}
						curAlts.close();
					}
				}//alternatives
				workout.addExercise(exer);
			}
			
			cur.close();			
			return true;
		}	

		/////////////////////////////// LOGS //////////////////////////////////////////////
		
		//get ONLY tacktical logs - not for Complete program
		public Hashtable<Long, DayData> getTackticalLogsForAMonth(final int userId, final long startDate, 
				final long endDate,SQLiteDatabase db)
		{
//			final String qry ="select log._id as " + COL_ID +
//					",log." +COL_DATETIME + " as " +COL_DATETIME +
//					",log.workout_id as "+ COL_WORKOUT_ID +
//					",log.laps as " + COL_LAPS +
//					" from " +TB_Workout_log_data_t + " as log where log.user_id="  + userId +
//					" and log." + COL_DATETIME + ">=" + startDate + " and log."  + COL_DATETIME + "<=" +endDate +
//					" group by " + COL_DATETIME;
			
			final String qry ="select COUNT(log." +COL_DATETIME + ") as numlogs" +		
					",log." +COL_DATETIME + " as " +COL_DATETIME +
					" from " +TB_Workout_log_data_t + " as log where log.user_id="  + userId +
					" and log." + COL_DATETIME + ">=" + startDate + " and log."  + COL_DATETIME + "<=" +endDate +
					" and (log." + COL_WEEK_ID+ " is null or log." + COL_WEEK_ID + "<="+ Common.ZERO_ID + ")" +
					" group by " + COL_DATETIME;
			
			Cursor cur;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			 
			if(cur == null)
			{
				return null;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return null;
			}
			final int colNumLogs;
			final int colDatetime;		
			
			try
			{
				colNumLogs= cur.getColumnIndexOrThrow("numlogs");
				colDatetime = cur.getColumnIndexOrThrow(COL_DATETIME);					
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return null;
			}			
			Hashtable<Long, DayData> htDays= new Hashtable<Long, DayData>(); 
			DayData dayData;
			long dt;
			while(cur.moveToNext())
			{
				dayData = new DayData();
				dayData.numOfLogs = cur.getInt(colNumLogs);
				dt = cur.getLong(colDatetime);
				//dayData.workoutId=cur.getInt(colWorkoutId);	
				dayData.workoutMode = WorkoutMode.Tactical;
				htDays.put(Long.valueOf(dt), dayData);
			}
			return htDays;
		}
		
		public Hashtable<Long, DayData> getModulesLogsForAMonth(final int userId, final long startDate, 
				final long endDate,SQLiteDatabase db)
		{
			/*
			 * 
			 * select COUNT(datetime) as numlogs,log.datetime as datetime from Workout_log as log 
			 * where log.user_id=1 and log.datetime>=1383289200000 and log.datetime<=1385798400000 and log.week_id=0 and log.day_id=0 group by datetime
			 */
			String qry="select COUNT(log." + COL_DATETIME +") as numlogs,log." +COL_DATETIME + " as " +COL_DATETIME +					
//					",log.workout_id as " + COL_WORKOUT_ID +
					",log.mode as " + COL_MODE +
					" from " + TB_Workout_log + 
					" as log where log." + COL_USER_ID + "=" + userId +
					" and log." + COL_DATETIME + ">=" + startDate + " and log."  + COL_DATETIME + "<=" +endDate +
					" and log.week_id=0 and log.day_id=0 group by "+ COL_MODE;
			
			Cursor cur;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			 
			if(cur == null)
			{
				return null;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return null;
			}
			final int colNumLogs;
			final int colDatetime;			
//			final int colWorkoutId;
			final int colMode;
			try
			{
				colNumLogs = cur.getColumnIndexOrThrow("numlogs");
				colDatetime = cur.getColumnIndexOrThrow(COL_DATETIME);				
//				colWorkoutId = cur.getColumnIndexOrThrow(COL_WORKOUT_ID);
				colMode = cur.getColumnIndexOrThrow(COL_MODE);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return null;
			}			
			Hashtable<Long, DayData> htDays= new Hashtable<Long, DayData>(); 
			DayData dayData;
			long dt;
			String md;
			Long key;
			
			while(cur.moveToNext())
			{
				//we might have multiple logs for diff modes 
				//in this case we will set mode to Unknown and add number of logs to total
				dt = cur.getLong(colDatetime);
				key = Long.valueOf(dt);
				if(htDays.containsKey(key))
				{
					dayData = htDays.get(key);
					dayData.numOfLogs += cur.getInt(colNumLogs);	
					dayData.workoutMode = WorkoutMode.Unknown;					
				}
				else
				{
					dayData = new DayData();
					dayData.numOfLogs = cur.getInt(colNumLogs);				
					//dayData.workoutId=cur.getInt(colWorkoutId);				
					md = cur.getString(colMode);
					dayData.workoutMode = WorkoutMode.getMode(md);	
					htDays.put(Long.valueOf(dt), dayData);
				}	
				
			}
			return htDays;
		}
		
		public int getHaveCompletedAtDate(final int progrId, final long dt, SQLiteDatabase db)
		{
			Cursor cur;
			String where = COL_PROGR_ID + "=" + progrId + " and " + COL_DATETIME +
					"=" + dt + " and " +  COL_COMPLETE + "=1";
			
			try
			{
				cur =db.query(TB_Day_complete, new String[]{COL_DAY_ORDER}, 
						where , null, null, null, 
						COL_DAY_ORDER + " DESC");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return -1;
			}
			 
			if(cur == null)
			{
				return -1;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return -1;
			}
			int res=-1;
			if(cur.moveToFirst())
			{				
				try
				{
					final int colDo = cur.getColumnIndexOrThrow(COL_DAY_ORDER);					
					res = cur.getInt(colDo);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					res=-1;
				}
			}
			cur.close();
			return res;
			
		}
		
		public long getDatetimeCompletedDay(final int progrId, final int dayOrder,SQLiteDatabase db)
		{
			Cursor cur;
			String where = COL_PROGR_ID + "=" + progrId + " and " + COL_DAY_ORDER +"=" + dayOrder + 
					" and " + COL_COMPLETE + "=1";
			try
			{
				cur =db.query(TB_Day_complete, new String[]{COL_DATETIME,COL_DAY_ORDER}, 
						where , null, null, null, 
						COL_DAY_ORDER + " DESC");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return -1;
			}
			 
			if(cur == null)
			{
				return -1;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return -1;
			}
			long res=-1;
			if(cur.moveToFirst())
			{				
				try
				{
					final int colDt = cur.getColumnIndexOrThrow(COL_DATETIME);					
					
					res = cur.getLong(colDt);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					res=-1;
				}
			}
			cur.close();
			return res;
		}
		
		//ret dayorder and datetime of last completed day for progr
		public long[] getLastCompletedDayorder(final int progrId, SQLiteDatabase db)
		{
			Cursor cur;
			try
			{
				cur =db.query(TB_Day_complete, new String[]{COL_DATETIME,COL_DAY_ORDER}, 
						COL_PROGR_ID + "=" + progrId, null, null, null, 
						COL_DATETIME + " DESC," + COL_DAY_ORDER + " DESC");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			 
			if(cur == null)
			{
				return null;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return null;
			}
			long[] data = null;
			if(cur.moveToFirst())
			{
				data = new long[2];
				try
				{
					final int colDt = cur.getColumnIndexOrThrow(COL_DATETIME);
					final int colOrd = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
					data[1] = (long)cur.getInt(colOrd);
					data[0] = cur.getLong(colDt);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					data=null;
				}
			}
			cur.close();
			return data;
			
		}
		
		//get logs info for a given dayOfMonth
		//get log id, dayOfMonth Id, week Id, dayOfMonth and week Order
		public Hashtable<Long, DayData> getLogsForAMonth(final int userId, final int progrId,final long startDate, 
				final long endDate,SQLiteDatabase db)
		{
			Hashtable<Long, DayData> htDays= new Hashtable<Long, DayData>(); 
			
			getCompleteDaysWithLogs(htDays, userId,progrId, startDate, endDate, db);
			getTckticalCompletedDays(htDays, userId,progrId, startDate, endDate, db);  //with logs
			getCompleteDaysWithoutLogs(htDays, userId,progrId, startDate, endDate, db);
			
			
			return (htDays.size() >0)? htDays : null;			
			
		}
		
		//get completed days for All program
		//get log data  
		//week_id and day_order columns should be > 0
		private boolean getTckticalCompletedDays(Hashtable<Long, DayData> htDays, final int userId, final int progrId,
				final long startDate, 
				final long endDate,SQLiteDatabase db)
		{
			String qry = "select day_week." + COL_WEEK_ID + " as " + COL_WEEK_ID +
					",day_week." + COL_PROGR_ID + " as " + COL_PROGR_ID +
					",day_week." + COL_DAY_ORDER + " as " + COL_DAY_ORDER +
					",day_week." + COL_DATETIME + " as " + COL_DATETIME +
					",day_week." + COL_TITLE + " as " + COL_TITLE +
					",log." + COL_WORKOUT_ID + " as " + COL_WORKOUT_ID +
					",log." + COL_LAPS + " as " + COL_LAPS +
					" from (select day." + COL_WEEK_ID + " as " + COL_WEEK_ID +
					",day." + COL_PROGR_ID + " as " + COL_PROGR_ID +
					",day." + COL_DAY_ORDER + " as " + COL_DAY_ORDER +
					",day." + COL_DATETIME + " as " + COL_DATETIME +					
					",week." + COL_TITLE + " as " + COL_TITLE +
					" from " + TB_Day_complete + " as day left join " +
					TB_Week + " as week on day." + COL_WEEK_ID + "=week." + COL_ID +
					" where day."+ COL_PROGR_ID + "=" + progrId +
					" and day." + COL_DATETIME + ">=" + startDate + 
					" and day." + COL_DATETIME + "<=" + endDate + ") as day_week inner join " +
					TB_Workout_log_data_t + " as log on day_week." + COL_DATETIME + 
					"=log." + COL_DATETIME + 
					" and day_week." + COL_PROGR_ID +"=log." + COL_PROGR_ID + 
					" where log." + COL_DAY_ORDER + ">0";	
			
			Cursor cur = null;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			 
			if(cur == null)
			{
				return false;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return false;
			}			
			
			final int colNumLogs;
			int colDatetime;
			int colWeekId;			
			int colDayOrder;
			int colWorkoutId;	
			int colTitle;
			try
			{
				colNumLogs = cur.getColumnIndexOrThrow(COL_LAPS);
				colDatetime = cur.getColumnIndexOrThrow(COL_DATETIME);
				colWeekId = cur.getColumnIndexOrThrow(COL_WEEK_ID);				
				colDayOrder = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
				colWorkoutId = cur.getColumnIndexOrThrow(COL_WORKOUT_ID);
				colTitle = cur.getColumnIndexOrThrow(COL_TITLE);	
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return false;
			}			
			
			DayData dayData;
			long dt;		
			Long key;
			
			while(cur.moveToNext())
			{
				dt = cur.getLong(colDatetime);
				key = Long.valueOf(dt);
				if(htDays.containsKey(key)) continue;
				
				dayData = new DayData();
				dayData.numOfLogs = cur.getInt(colNumLogs);
				
				if(!cur.isNull(colWeekId)){
					dayData.weekId = cur.getInt(colWeekId);					
				}				
				if(!cur.isNull(colDayOrder)){
					dayData.dayOrder = cur.getInt(colDayOrder);
				}
				if(!cur.isNull(colTitle))
				{
					dayData.blockTxt = cur.getString(colTitle);
				}
				dayData.workoutId=cur.getInt(colWorkoutId);						
				dayData.workoutMode = WorkoutMode.Tactical;
				
				htDays.put(Long.valueOf(dt), dayData);
			}
			
			cur.close();			
			
			return true;
					
		}
		
		private final static String SQL_DAYS_WITH_LOGS = "select COUNT(log." + COL_ID +") as numlogs,log." +
				COL_DATETIME + " as " +COL_DATETIME +
				",log.mode as " + COL_MODE +
				",weeks.weekid as "+COL_WEEK_ID +
				",weeks.weektitle as " + COL_TITLE +
				",weeks.dayodr as " + COL_DAY_ORDER + 
				//",weeks.dayid as " + COL_DAY_ID +  ////////////////////////
				",weeks.workoutid as " + COL_WORKOUT_ID + //////////////
				" from " + TB_Workout_log + 
				" as log inner join (select wk._id as weekid,wk.title as weektitle,wk.week_order as weekord,d._id as dayid,d.workout_id as workoutid,d.day_order as dayodr from Week as wk inner join Day as d on wk._id=d.week_id ) as weeks on log.week_id=weeks.weekid and log.day_id=weeks.dayid group by log." +
				COL_DATETIME +
				" having log." + COL_USER_ID + "=%d and log." + 
				COL_PROGR_ID + "=%d and log." + 
				COL_DATETIME + ">=%d and log."  + COL_DATETIME + 
				"<=%d and weeks.weekid>0 order by log." + COL_DATETIME;
		
		
		//kjeep days with higher day order
		private boolean getCompleteDaysWithLogs(Hashtable<Long, DayData> htDays, final int userId, final int progrId,
				final long startDate, 
				final long endDate,SQLiteDatabase db)		
		{	
			
			String qry = String.format(Locale.US, SQL_DAYS_WITH_LOGS, 
					userId,progrId,startDate,endDate);
			/*
			 * select COUNT(log._id) as numlogs,weeks.weekid as wkid,weeks.weektitle as title,weeks.dayodr as dayorder from Workout_log as log inner join (select wk._id as weekid,wk.title as weektitle,wk.week_order as weekord,d._id as dayid,d.workout_id as workoutid,d.day_order as dayodr from Week as wk inner join Day as d on wk._id=d.week_id where wk.progr_id=1) as weeks on log.week_id=weeks.weekid and log.day_id=weeks.dayid group by log.datetime having log.progr_id=1 and log.user_id=1 and log.datetime>=123456 and log.datetime<=123499;
			 */
			//days with logs
//			String qry="select COUNT(log." + COL_ID +") as numlogs,log." +COL_DATETIME + " as " +COL_DATETIME +
//					",log.mode as " + COL_MODE +
//					",weeks.weekid as "+COL_WEEK_ID +
//					",weeks.weektitle as " + COL_TITLE +
//					",weeks.dayodr as " + COL_DAY_ORDER + 
//					//",weeks.dayid as " + COL_DAY_ID +  ////////////////////////
//					",weeks.workoutid as " + COL_WORKOUT_ID + //////////////
//					" from " + TB_Workout_log + 
//					" as log inner join (select wk._id as weekid,wk.title as weektitle,wk.week_order as weekord,d._id as dayid,d.workout_id as workoutid,d.day_order as dayodr from Week as wk inner join Day as d on wk._id=d.week_id ) as weeks on log.week_id=weeks.weekid and log.day_id=weeks.dayid group by log." +
//					COL_DATETIME +
//					" having log." + COL_USER_ID + "=" + userId +
//					" and log." + COL_PROGR_ID + "=" + progrId +
//					" and log." + COL_DATETIME + ">=" + startDate + " and log."  + COL_DATETIME + "<=" +endDate+
//					" and weeks.weekid>0"+
//					" order by log." + COL_DATETIME;
			
			Cursor cur = null;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			 
			if(cur == null)
			{
				return false;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return false;
			}			
			
			final int colNumLogs;
			int colDatetime;
			int colWeekId;
			int colTitle;
			int colDayOrder;
			int colWorkoutId;
			int colMode;
			try
			{
				colNumLogs = cur.getColumnIndexOrThrow("numlogs");
				colDatetime = cur.getColumnIndexOrThrow(COL_DATETIME);
				colWeekId = cur.getColumnIndexOrThrow(COL_WEEK_ID);
				colTitle = cur.getColumnIndexOrThrow(COL_TITLE);
				colDayOrder = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
				colWorkoutId = cur.getColumnIndexOrThrow(COL_WORKOUT_ID);
				colMode = cur.getColumnIndexOrThrow(COL_MODE);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return false;
			}			
			
			DayData dayData;
			long dt;
			String md;
			Long key;
			while(cur.moveToNext())
			{
				dt = cur.getLong(colDatetime);
				key = Long.valueOf(dt);				
				dayData = new DayData();
				
				if(!cur.isNull(colDayOrder)){
					dayData.dayOrder = cur.getInt(colDayOrder);
				}			
				
				if(htDays.containsKey(key))
				{
					DayData d = htDays.get(key);
					if(d.dayOrder >= dayData.dayOrder)
					{
						continue; //got day
					}
					else
					{
						htDays.remove(key); //will replace
					}
				}				
				
				dayData.numOfLogs = cur.getInt(colNumLogs);
				
				if(!cur.isNull(colWeekId)){
					dayData.weekId = cur.getInt(colWeekId);
				}
				if(!cur.isNull(colTitle))
				{
					dayData.blockTxt = cur.getString(colTitle);
				}
				
				dayData.workoutId=cur.getInt(colWorkoutId);
				
				md = cur.getString(colMode);
				dayData.workoutMode = WorkoutMode.getMode(md);
				
				htDays.put(key, dayData);
			}
			
			cur.close();					
		    return true;			
		}
		
		
		
		
		private final static String SQL_DAYS_NO_LOGS="select log." +COL_DATETIME + " as " +COL_DATETIME +
				",weeks.weekid as "+COL_WEEK_ID +
				",weeks.weektitle as " + COL_TITLE +
				",weeks.dayodr as " + COL_DAY_ORDER + 
				",weeks.workoutid as " + COL_WORKOUT_ID + 
				" from " + TB_Day_complete + 
				" as log inner join (select wk._id as weekid,wk.title as weektitle,wk.week_order as weekord,d._id as dayid,d.workout_id as workoutid,d.day_order as dayodr from Week as wk inner join Day as d on wk._id=d.week_id ) as weeks on log.week_id=weeks.weekid and log.day_order=weeks.dayodr where log." +
				COL_DATETIME + ">=%d and log."  + 
				COL_DATETIME + "<=%d and weeks.weekid>0 and log." + 
				COL_PROGR_ID + "=%d and log." + COL_COMPLETE +"=1 order by log." + COL_DATETIME;
		
		private boolean getCompleteDaysWithoutLogs(Hashtable<Long, DayData> htDays,final int userId, 
				final int progrId,
				final long startDate, 
				final long endDate,SQLiteDatabase db)
		{
			String qry = String.format(Locale.US, SQL_DAYS_NO_LOGS, 
					startDate,endDate,progrId);
			
			//get days without logs
//			String qry="select log." +COL_DATETIME + " as " +COL_DATETIME +
//					",weeks.weekid as "+COL_WEEK_ID +
//					",weeks.weektitle as " + COL_TITLE +
//					",weeks.dayodr as " + COL_DAY_ORDER + 
//					",weeks.workoutid as " + COL_WORKOUT_ID + 
//					" from " + TB_Day_complete + 
//					" as log inner join (select wk._id as weekid,wk.title as weektitle,wk.week_order as weekord,d._id as dayid,d.workout_id as workoutid,d.day_order as dayodr from Week as wk inner join Day as d on wk._id=d.week_id ) as weeks on log.week_id=weeks.weekid and log.day_order=weeks.dayodr where log." +
//					COL_DATETIME + ">=" + startDate + " and log."  + COL_DATETIME + "<=" +endDate +
//					" and weeks.weekid>0" +
//					" and log." + COL_PROGR_ID + "=" + progrId +
//					" order by log." + COL_DATETIME;
			
			Cursor cur;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
			 
			if(cur == null)
			{
				return false;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return true;
			}
			
			final int colDatetime;
			final int colWeekId;
			final int colTitle;
			final int colDayOrder;
			final int colWorkoutId;	
			try
			{				
				colDatetime = cur.getColumnIndexOrThrow(COL_DATETIME);
				colWeekId = cur.getColumnIndexOrThrow(COL_WEEK_ID);
				colTitle = cur.getColumnIndexOrThrow(COL_TITLE);
				colDayOrder = cur.getColumnIndexOrThrow(COL_DAY_ORDER);
				colWorkoutId = cur.getColumnIndexOrThrow(COL_WORKOUT_ID);				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return false;
			}	
			
			Long key;
			long dt;
			DayData dayData;
			while(cur.moveToNext())
			{
				dt = cur.getLong(colDatetime);
				key = Long.valueOf(dt);
				if(htDays.containsKey(key)) continue;
				
				//add day
				dayData = new DayData();
				if(!cur.isNull(colWeekId)){
					dayData.weekId = cur.getInt(colWeekId);
				}
				if(!cur.isNull(colTitle))
				{
					dayData.blockTxt = cur.getString(colTitle);
				}
				if(!cur.isNull(colDayOrder)){
					dayData.dayOrder = cur.getInt(colDayOrder);
				}
				dayData.workoutId=cur.getInt(colWorkoutId);
				htDays.put(key, dayData);
			}
			cur.close();	
			return true;
		}
		
		////////////////////////////// EMAIL LOG ///////////////////////////////////
		/**
		 * We ned workoutId only for WorkoutMode.All
		 * @param dt
		 * @param workoutId
		 * @param weekId
		 * @param mode
		 * @param csvStr
		 * @param db
		 * @return
		 */
		public StringBuilder getLogTextToEmailAll(final long dt, final int workoutId, final int weekId,
				final WorkoutMode mode, StringBuilder csvStr, SQLiteDatabase db, final int progrId)
		{
			String qry;
			if(mode == WorkoutMode.All)
			{
				qry = getLogExerciseNamesSql(dt,workoutId,weekId);
			}
			else if(mode == WorkoutMode.Tactical)
			{
				qry=getTackticalLogSQLForView(workoutId, dt, progrId);
			}
			else //Weight
			{
				qry = getLogExerciseNamesSqlForModule(dt); 
			}
			Cursor cur;
			try
			{
				cur = db.rawQuery(qry, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
			 
			if(cur == null)
			{
				return null;
			}
			if(cur.getCount() == 0)
			{
				cur.close();
				return null;
			}
			final int colTitle;
			final int colId;
			final int colWkId;
			try
			{
				colId=cur.getColumnIndexOrThrow(DBAdapter.COL_ID);
				colTitle = cur.getColumnIndexOrThrow(DBAdapter.COL_TITLE);
				if(mode == WorkoutMode.Tactical) colWkId =  cur.getColumnIndexOrThrow(DBAdapter.COL_ID);
				else colWkId =-1;
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				cur.close();
				return null;
			}
			String tlt;
			int id, wkId;
			Cursor cur2 = null;
			StringBuilder str = new StringBuilder();
				
			final boolean saveFile = (csvStr!= null)? true : false;
			
			while(cur.moveToNext())
			{
				tlt = cur.getString(colTitle);
				
				if(mode == WorkoutMode.Tactical)
				{
					wkId = cur.getInt(colWkId);
					qry = getTackticalLogSQLForSubView(wkId, dt, true);
					try
					{
						cur2 = db.rawQuery(qry, null);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();						
						if(cur2 != null) cur2.close();
						continue;
					}
					if(cur2 == null)
					{						
						continue;
					}
					if(cur2.getCount() == 0)
					{
						cur2.close();
						continue;
					}
					int colLaps,colName;
					try
					{
						colLaps=cur2.getColumnIndexOrThrow(COL_LAPS);
						colName = cur2.getColumnIndexOrThrow(COL_TITLE);
						
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						cur2.close();						
						continue;
					}
					String name;
					int laps;
					//title
					if(saveFile)
					{
						csvStr.append(tlt);						
						csvStr.append(Common.NEWLINE);
						csvStr.append("Exercise,Laps");
						csvStr.append(Common.NEWLINE);
						
					}
					str.append(tlt);
					str.append(Common.NEWLINE);
					while(cur2.moveToNext())
					{
						name = cur2.getString(colName);
						if(!cur2.isNull(colLaps)) laps = cur2.getInt(colLaps);
						else laps = 0;
						
						str.append(name);
						str.append(" : laps : ");
						str.append(laps);
						str.append(Common.NEWLINE);			
						
						if(saveFile)
						{
							csvStr.append(name);
							csvStr.append(",");
							csvStr.append(laps);
							csvStr.append(Common.NEWLINE);
							
						}
					}
					cur2.close();
					cur2 = null;
					
				}  //Tactical end
				else
				{
					id = cur.getInt(colId);
					qry = getWeightLogsByLogIdSql(id);
					try
					{
						cur2 = db.rawQuery(qry, null);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();						
						if(cur2 != null) cur2.close();
						continue;
					}
					if(cur2 == null)
					{						
						continue;
					}
					if(cur2.getCount() == 0)
					{
						cur2.close();
						continue;
					}
					int colReps,colWeight,colUnits,colSet;
					try
					{
						colSet=cur2.getColumnIndexOrThrow(COL_SET_NUM);
						colReps=cur2.getColumnIndexOrThrow(COL_REPS);
						colWeight = cur2.getColumnIndexOrThrow(COL_WEIGHT);
						colUnits = cur2.getColumnIndexOrThrow(COL_UNITS);
						
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						cur2.close();						
						continue;
					}
					str.append(tlt);
					str.append(Common.NEWLINE);
					if(saveFile)
					{
						csvStr.append(tlt);
						csvStr.append(",,,,");
						csvStr.append(Common.NEWLINE);
						csvStr.append(",Set,Weight,Units,Reps");
						csvStr.append(Common.NEWLINE);
						
					}
					int setnum,reps,wt;
					String units;
					while(cur2.moveToNext())
					{
						
						if(!cur2.isNull(colSet))
						{
							setnum = cur2.getInt(colSet);							
						}
						else setnum = -1;
											
						if(!cur2.isNull(colReps))
						{							
							reps = cur2.getInt(colReps);
						}
						else reps = -1;
						
						if(!cur2.isNull(colWeight))
						{							
							wt = cur2.getInt(colWeight);
						}
						else wt = -1;
						if(!cur2.isNull(colUnits))
						{							
							units = cur2.getString(colUnits);
						}
						else units = null;
						
						
						if(setnum >0)
						{
							str.append("set: ");
							str.append(setnum);
						}
						if(reps >0)
						{
							str.append("  reps: ");	
							str.append(reps);
						}
						if(wt >0)
						{
							str.append("  weight: ");
							str.append(wt);
							if(units != null)
							{
								str.append(units);
							}
						}						
						str.append(Common.NEWLINE);
						
						if(saveFile)
						{
							csvStr.append(",");
							if(setnum >0)
							{
								csvStr.append(setnum);
							}
							csvStr.append(",");
							
							if(wt >0)
							{								
								csvStr.append(wt);								
							}	
							csvStr.append(",");
							if(units != null)
							{
								csvStr.append(units);
							}
							csvStr.append(",");
							if(reps >0)
							{								
								csvStr.append(reps);
							}
							csvStr.append(",");
							csvStr.append(Common.NEWLINE);
						}					
					}
					cur2.close();
					cur2=null;
				}//Weight
				
			}
			cur.close();
			cur=null;
			return str;
		}
		
		//return Workout_id
		public long insertWorkoutLog(final WorkoutLog wkLog, SQLiteDatabase db)
		{
			ContentValues vals = new ContentValues();
			vals.put(COL_COMPLETE, (wkLog.getCompleted())? 1 : 0);
			vals.put(COL_USER_ID, wkLog.getUserId());
			vals.put(COL_DATETIME, wkLog.getDatetime());
			vals.put(COL_PROGR_ID, wkLog.getProgrId());
			vals.put(COL_WEEK_ID, wkLog.getWeekId());
			vals.put(COL_DAY_ID, wkLog.getDayId());
			vals.put(COL_WORKOUT_ID, wkLog.getWorkoutId());
			vals.put(COL_ENTRY_ID, wkLog.getEntryId());
			
			long res;
	    	try
	    	 {
	    		 res = db.insertOrThrow(TB_Workout_log, null, vals);
	    	 }
	    	 catch (SQLException e)
	    	 {
	    		 e.printStackTrace();	
	    		 res = -1;
	    	 }
	    	
	    	return res;
		}
		
//		public boolean saveOneMaxRep(final int userId, final int exId, final int maxRep, final int wt, final int reps, SQLiteDatabase db)
//		{
//			int rows=0;
//			ContentValues vals = new ContentValues();
//			vals.put(COL_MAX_REP_WEIGHT, maxRep);
//			vals.put(COL_WEIGHT, wt);
//			vals.put(COL_REPS, reps);
//			
//			String where = COL_USER_ID + "=" + userId + " and " + COL_EX_ID + "=" + exId;
//			boolean ok =true;
//			try
//	    	 {
//	    		 rows = db.update(TB_User_ex_preset, vals, where, null);
//	    	 }
//	    	 catch (SQLException e)
//	    	 {
//	    		 e.printStackTrace();	
//	    		 rows = 0;
//	    	 }
//			
//			if(rows ==0)
//			{
//				vals.put(COL_USER_ID, userId);
//				vals.put(COL_EX_ID, exId);
//				try
//		    	 {
//		    		db.insertOrThrow(TB_User_ex_preset, null, vals);
//		    	 }
//		    	 catch (SQLException e)
//		    	 {
//		    		 e.printStackTrace();	
//		    		 ok=false;
//		    	 }
//			}
//			vals.clear();
//			vals=null;
//			return ok;
//		}
		
		
			//get hashtable of [ex_id] = link for male and female images
//			Cursor cimg;
//			
//			 try
//	    	 {
//				 cimg= mDb.rawQuery("select img.ex_id as " + COL_EX_ID +",img.gender as "+ COL_GENDER +", img.link as "+COL_LINK+" from Exercise_image as img join Workout_exercise as wk on img.ex_id=wk.ex_id where wk.workout_id="+workout.getId(), null);
//
//	    	 }
//	    	 catch (Exception ex)
//			 {
//	    		 Log.e(TAG, "err " + ex.getMessage());
//				 return false;
//			 }
//			 if(cimg == null)
//			 {
//				 Log.e(TAG, "No images found");
//				 return false;
//			 }
//			 if(cimg.getCount() == 0)
//			 {
//				 cimg.close();
//				 Log.e(TAG, "No images found");
//				 return false;
//			 }
//			 Hashtable<Integer,String> htImagesMale = new Hashtable<Integer,String>();
//			 Hashtable<Integer,String> htImagesFemale = new Hashtable<Integer,String>();
//			 String str;
//			 String str2;
//			 int exId;
//			 Integer key;
//			 while(cimg.moveToNext())
//			 {
//				 try
//				 {					 
//					 str=cimg.getString(cimg.getColumnIndex(COL_GENDER));
//					 str2 = cimg.getString(cimg.getColumnIndex(COL_LINK));
//					 exId=cimg.getInt(cimg.getColumnIndex(COL_EX_ID));
//					 key = Integer.valueOf(exId);
//					 if(str.equalsIgnoreCase(Exercise.FEMALE))
//					 {						
//						 if(!htImagesFemale.containsKey(key))
//						 {
//							 htImagesFemale.put(key, str2);
//						 }
//						 else
//						 {
//							 Log.e(TAG, "Multiple Female images for ex=" + exId);
//						 }
//					 }
//					 else
//					 {
//						 if(!htImagesMale.containsKey(key))
//						 {
//							 htImagesMale.put(key, str2);
//						 }
//						 else
//						 {
//							 Log.e(TAG, "Multiple Male images for ex=" + exId);
//						 }
//					 }
//				 }
//				 catch (Exception ex)
//				 {
//					 ex.printStackTrace();
//					 cimg.close();
//					 return false;
//				 }
//			 }
//			 cimg.close();
//			 cimg=null;
//			
//			//get exercises - title+description for workout
//			Cursor c;
// 			//select wk.ex_id as exid, ex.title as extitle, ex.description as exdescrip from Workout_exercise as wk inner join Exercise as ex on wk.ex_id=ex._id where wk.workout_id=1 order by wk.ex_order;
//			 try
//	    	 {
//				 c= mDb.rawQuery("select wk.ex_id as " + COL_EX_ID +", ex.title as " + COL_TITLE +", ex.description as " + COL_DESCRIPTION+" from Workout_exercise as wk inner join Exercise as ex on wk.ex_id=ex._id where wk.workout_id=1 order by wk.ex_order", null);
//				 
////	    		 c =  mDb.query(TB_Workout_exercise, new String[]{COL_ID, COL_EX_ID}, 
////	        			 "COL_WORKOUT_ID=" +workout.getId(),
////	        			 null, null, null, COL_EX_ORDER);
//	    	 }
//	    	 catch (Exception ex)
//			 {
//	    		 Log.e("SQL", "err " + ex.getMessage());
//				 return false;
//			 }
//			 if(c.getCount() == 0)
//			 {
//				 c.close();
//				 return false;
//			 }
//			 while(c.moveToNext())
//			 {
//				 //get exerc data
//				 int ex_id;
//				 String title;
//				 String description;
//				 try
//				 {
//					 ex_id = c.getInt(c.getColumnIndex(COL_EX_ID));
//					 title=c.getString(c.getColumnIndex(COL_TITLE));
//					 description = c.getString(c.getColumnIndex(COL_DESCRIPTION));
//				 }
//				 catch (Exception ex)
//				 {
//					 ex.printStackTrace();
//					 c.close();
//					 return false;
//				 }
//				
//				 //images
//				 
//				 Exercise exer = new Exercise(ex_id);
//				 exer.setTitle(title);
//				 exer.setDescription(description);			
// //images
//				 key = Integer.valueOf(ex_id);
//				 if(htImagesFemale.containsKey(key))
//				 {
//					 exer.setFemaleImage(htImagesFemale.get(key));
//				 }
//				 else if(htImagesMale.containsKey(key))
//				 {
//					 exer.setMaleImage(htImagesMale.get(key));
//				 }				
//				 
//				 //get alternatives
//				 Cursor calt;				 
//				 try
//		    	 {
//		    		 calt =  mDb.query(TB_Alternatives, new String[]{COL_ALT_ID, COL_GENDER}, 
//		        			 "COL_WORKOUT_ID=" +workout.getId() + " AND " + COL_EX_ID+"="+ ex_id,
//		        			 null, null, null, null);
//		    	 }
//		    	 catch (Exception ex)
//				 {
//		    		 Log.e("SQL", "err " + ex.getMessage());
//					 return false;
//				 }
//				 if(calt != null && calt.getCount() > 0)
//				 {
//					 String gender;
//					 int alt_id;
//					 while(calt.moveToNext())
//					 {
//						 try
//						 {
//							 alt_id = calt.getInt(calt.getColumnIndex(COL_ALT_ID));
//							 gender=calt.getString(calt.getColumnIndex(COL_GENDER));
//							 
//						 }
//						 catch (Exception ex)
//						 {
//							 ex.printStackTrace();
//							 continue;
//						 }
//						 
//					 }
//				 }
//				 
//			 }
//		}
		
	}
	 
	private static int getWeightUnitsDBVal(final boolean isLbs){
		return (isLbs)? 0 : 1;
	}
	private static int getDistUnitsDBVal(final boolean isKm)
	{
		return (isKm)? 0 : 1;
	}
	

}

/*
 *
			 * select log._id as logid,log.datetime as dt,log.week_id as logweekid,log.day_id as logdayid,log.entry_id as logentryid,log.ex_id as logexid,log.complete as complete,weeks.weektitle as title,weeks.weekord as weekorder,weeks.dayodr as dayorder from  Workout_log as log inner join (select wk._id as weekid,wk.title as weektitle,wk.week_order as weekord,d._id as dayid,d.workout_id as workoutid,d.day_order as dayodr from Week as wk inner join Day as d on wk._id=d.week_id where wk.progr_id=1) as weeks on log.week_id=weeks.weekid and log.day_id=weeks.dayid where log.progr_id=1 and log.user_id=1;
			
			String qry="select log."+ COL_ID + " as " + COL_LOG_ID +
					",log."+ COL_DATETIME + " as " + COL_DATETIME +
					",log." + COL_WEEK_ID + " as " +COL_WEEK_ID +
					",log." + COL_DAY_ID + " as " + COL_DAY_ID +
					",log." + COL_ENTRY_ID + " as "+ COL_ENTRY_ID +
					",log." + COL_EX_ID + " as " + COL_EX_ID +
					",log." + COL_COMPLETE + " as " +COL_COMPLETE +
					",weeks.weektitle as " +COL_TITLE+
					",weeks.weekord as " + COL_WEEK_ORDER +
					",weeks.dayodr as " + COL_DAY_ORDER +
					" from  " + TB_Workout_log + 
					" as log inner join (select wk." + COL_ID +" as weekid,wk." + COL_TITLE +
					" as weektitle,wk." + COL_WEEK_ORDER + 
					" as weekord,d." + COL_ID +" as dayid,d." +COL_DAY_ORDER +
					" as dayodr from " + TB_Week + " as wk inner join " + TB_Day + 
					" as d on wk." + COL_ID +"=d."+ COL_WEEK_ID +
					" where wk." + COL_PROGR_ID +"=" + progrId + ") as weeks on log." + COL_WEEK_ID +
					"=weeks.weekid and log." + COL_DAY_ID +"=weeks.dayid where log."+COL_PROGR_ID+"=" +progrId +
					" and log."+ COL_USER_ID +"=" + userId;
	*/
