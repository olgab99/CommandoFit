package com.olmatech.fitness.main;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.User.Gender;

public class Workout {
	public enum EntryType
	{
		Exercise,
		Workout,
		Unknown;
		
		public static String getShortTitle(EntryType tp)
		{
			if(tp == Exercise)
			{
				return "E";
			}
			else if(tp == Workout)
			{
				return "W";
			}
			else return "U";
		}
		
		public static EntryType getEntryTypeFromStr(final String s)
		{
			if(s == null) return Unknown;
			String str = s.toUpperCase(Locale.US);
			if(str.startsWith("E")) return Exercise;
			else if(str.startsWith("W")) return Workout;
			else return Unknown;
		}
		
	}
	
	public enum WorkoutMode
	{
		Endurance("Endurance",1),
		Bodybuilding("Bodybuilding", 2),		
		Tactical ("Tactical",3),
		Cardio ("Cardio", 4),
		Rest ("Rest", 5),
		Weight("Weight", 6),  //to distinguish btw weights and Tactical
		All("Complete Program", 7),
		Unknown("Unknown",8);
		
		private String title;
		private int id;
		
		WorkoutMode (String name, int modeId){
			title = name;
			id = modeId;
		}
		public String getTitle() { return title; }
		public int getId() { return id; }
		
		public static String getShortTitle2(WorkoutMode m)
		{
			if(m == Endurance) return "E";
			else if(m==Bodybuilding) return "B";
			else if(m==Tactical) return "T";
			else if(m==Cardio) return "C";
			else if(m==Rest) return "R";
			else if(m==Weight) return "W";
			else if(m==All) return "A";
			return "U";
		}
			
		
		public static String getShortTitle(final int md_id)
		{
			switch(md_id)
			{
			case 1: return "E";
			case 2: return "B";
			case 3: return "T";
			case 4: return "C";
			case 5: return "R";
			case 6: return "W";
			case 7: return "A";
			default: return "U";
			}
		}
		
		public static WorkoutMode getModeFromId(final int mode_id)
		{
			switch(mode_id)
			{
			case 1: return Endurance;
			case 2: return Bodybuilding;
			case 3: return Tactical;
			case 4: return Cardio;
			case 5: return Rest;
			case 6: return Weight;
			case 7: return All;
			default: return Unknown;
			}
		}
		
		public static WorkoutMode getMode(final String m)
		{
			if(m == null || m.length() ==0) return Unknown;
			
			if(m.startsWith("E"))
			{
				return Endurance;
			}
			else if(m.startsWith("B"))
			{
				return Bodybuilding;
			}
			else if(m.startsWith("T"))
			{
				return Tactical;
			}
			else if(m.startsWith("C"))
			{
				return Cardio;
			}
			else if(m.startsWith("R"))
			{
				return Rest;
			}
			else if(m.startsWith("W"))
			{
				return Weight;
			}
			else if(m.startsWith("A"))
			{
				return All;
			}
			else
			{
				return Unknown;
			}
		}
	}
	
	private WorkoutMode mode;
	private int id;
	private String title;
	private String description;
	private List<Exercise> listExs;
	
	private int curExIndex;
	//private int curSet = 1; //set we are on
	//private long datetime;
	
	//defaults
	public static final int AVER_MAX_SETS=3;
	public static final int MAX_REPS=30;
	public static final int MAX_REPS_BW=150; // for exerc. without weigts
	//private static final int DEF_MAX_WEIGHT = 500;
	
	//for tacktical workout
	private int laps=0;
	
	public Workout(){
		listExs = new LinkedList<Exercise>();
	//	datetime = Common.getDateTimeForToday();
	}
		
	public Workout(final int wkid)
	{
		id=wkid;
		listExs = new LinkedList<Exercise>();
		//datetime = Common.getDateTimeForToday();
	}
	
	public boolean getWorkoutCompleted()
	{
		if(listExs == null || listExs.size() == 0) return false;		
		
		for(Exercise exer: listExs)
		{
			if(exer != null && !exer.getCompleted())
			{
				return false;				
			}
		}
		return true;
	}
	
	//public long getDateTime(){ return datetime; }
	
	public Exercise getCurExercise()
	{
		if(curExIndex <0 || curExIndex >= listExs.size()) return null;
		return listExs.get(curExIndex);
	}
	
	public Exercise getExerciseAt(final int index)
	{
		if(index <0 || index >= listExs.size()) return null;
		return listExs.get(index);
	}
	
	public ExType getCurExType()
	{
		if(curExIndex <0 || curExIndex >= listExs.size()) return ExType.Unknown;
		return listExs.get(curExIndex).getExType();
	}
	
	public boolean getCurExCompleted()
	{
		if(curExIndex <0 || curExIndex >= listExs.size()) return false;
		return listExs.get(curExIndex).getCompleted();
	}
	
	public boolean getExCompleted(final int ind)
	{
		if(ind <0 || ind >= listExs.size()) return false;
		return listExs.get(ind).getCompleted();
	}
	
	public void setExCompleted(final int ind, final boolean val)
	{
		if(ind <0 || ind >= listExs.size()) return;
		listExs.get(ind).setCompleted(val);
	}
	
	public void setExLogId(final int ind, final long logId)
	{
		if(ind <0 || ind >= listExs.size()) return;
		listExs.get(ind).setLogId(logId);
	}
	
	public void setExCompletedAndLogId(final int ind, final long logId, final boolean completed)
	{
		if(ind <0 || ind >= listExs.size()) return;
		Exercise ex = listExs.get(ind);
		if(ex == null) return;
		ex.setCompleted(completed);
		ex.setLogId(logId);
	}
	
	public void setExCompletedById(final int entryId, final boolean val)
	{
		if(listExs == null) return;
		for(Exercise exer: listExs)
		{
			if(exer.getId() == entryId)
			{
				exer.setCompleted(val);
				break;
			}
		}
	}
	
	public ExData getCurExData(Gender g)
	{
		if(curExIndex <0 || curExIndex >= listExs.size()) return null;
		return listExs.get(curExIndex).getCurExData(g);
	}
	
	
	public int getExerciseCount()
	{
		return (listExs != null)? listExs.size() : 0;
	}
	
	public boolean isLast()
	{
		return (curExIndex == (listExs.size()-1))? true : false;
	}
	
	public void addExercise(Exercise exer)
	{
		listExs.add(exer);
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(final int val)
	{
		id=val;
	}
	/**
	 * 
	 * @param ind
	 * @param clearData - if true reset Alt index
	 */
	public void setCurIndex(final int ind, final boolean clearAltData)
	{
		curExIndex = ind;
		if(clearAltData && curExIndex >=0 && listExs != null && listExs.size() > curExIndex) 
		{
			Exercise ex = listExs.get(curExIndex);
			ex.setCurAltIndex(-1);			
		}			
	}
	
	public boolean setPrevAlt(Gender g)
	{
		//if we have alt up - set to it
		Exercise exer = listExs.get(curExIndex);
		return (exer != null)? exer.setPrevAlt(g) : false;
		
	}
	
	public boolean setNextAlt(Gender g)
	{
		//if we have alt up - set to it
		Exercise exer = listExs.get(curExIndex);
		return (exer != null)? exer.setNextAlt(g) : false;
		
	}
	
	public int getCurIndex()
	{
		return curExIndex;
	}
	
	public void setTitle(final String t)
	{
		title=t;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setDescription(final String d)
	{
		description=d;
	}
	
	public String getDescriotion()
	{
		return description;
	}
	
	public int getCurExId()
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return -1;
		return listExs.get(curExIndex).getExerciseData().getExId();
	}
	
	public List<ExData> getCurAltsMale()
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return null;
		return listExs.get(curExIndex).getAltListMale();
	}
	
	public List<ExData> getCurAltsFemale()
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return null;
		return listExs.get(curExIndex).getAltListFemale();
	}
	
	public void setCurAltIndex(final int val){	
		if(curExIndex < 0 || curExIndex >= listExs.size()) return;
		listExs.get(curExIndex).setCurAltIndex(val);
	}
	public void clearAlt(){ 
		if(curExIndex < 0 || curExIndex >= listExs.size()) return;
		listExs.get(curExIndex).clearAltIndex();
	}
	
	public int getCurAltIndex(){ 
		if(curExIndex < 0 || curExIndex >= listExs.size()) return-1;
			return listExs.get(curExIndex).getCurAltIndex();
	 }
	
			
	public void setMode(final WorkoutMode m)
	{
		mode = m;
	}

	public WorkoutMode getMode()
	{
		return mode;
	}
	
	public void increaseCurSet()
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return;
		listExs.get(curExIndex).increaseCurSet();
	}
	
	public void decreaseCurSet()
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return;
		listExs.get(curExIndex).decreaseCurSet();
	}
	
	public void setCurSet(final int val)
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return;
		listExs.get(curExIndex).setCurSet(val);
	}
	
	public int getCurSet()
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return -1;
		return listExs.get(curExIndex).getCurSet();
	}
	
	public int getMaxReps(final ExType exType)
	{
		if(mode == WorkoutMode.Endurance || mode == WorkoutMode.Bodybuilding){
			
			return (exType == ExType.Weights)?  MAX_REPS : MAX_REPS_BW;
		}
		
		return 0;		
	}
	
	public int getRepsForUser()
	{
		//TODO - calculate
		if(mode == WorkoutMode.Endurance) return 15;
		else if(mode == WorkoutMode.Bodybuilding) return 8;
		
		return 0;
	}
	
	public int getAverReps()
	{
		if(mode == WorkoutMode.Endurance) return 15;
		else if(mode == WorkoutMode.Bodybuilding) return 8;
		
		return 0;
	}
	
	public int[] getRepsRange()
	{
		if(mode == WorkoutMode.Endurance) return new int[]{12,18};
		else if(mode == WorkoutMode.Bodybuilding) return new int[]{6,12};
		
		return new int[]{6,18};
	}
	
	/**
	 * 
	 * @return id of exercise that we are doing
	 */
	public int getCurExId(Gender g)
	{
		if(curExIndex < 0 || curExIndex >= listExs.size()) return -1;
		return listExs.get(curExIndex).getCurExId(g);
	}
	
	public void setLaps(final int val){ laps=val; }
	public int getLaps(){ return laps; }
	public void addLap(){ laps++; }
	
	public void resetAllLogs()
	{
		if(listExs == null) return;
		for(Exercise ex: listExs)
		{
			if(ex != null) ex.resetAllLogs();
		}
	}
	
	public void clearRecommended()
	{
		if(listExs == null) return;
		for(Exercise ex: listExs)
		{
			if(ex != null) ex.clearRecommended();
		}
	}
}
