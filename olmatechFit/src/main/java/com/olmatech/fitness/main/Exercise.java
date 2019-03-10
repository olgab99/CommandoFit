package com.olmatech.fitness.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout.WorkoutMode;

public class Exercise {	
	
	public enum ExType
	{
		Weights,
		BodyWeight,
		Cardio,
		Tactical,
		Unknown;
		
		public static String getShortString(ExType tp)
		{
			if(tp == Cardio) return "C";
			else if(tp == BodyWeight) return "B";
			else if(tp == Weights) return "W";
			else if(tp==Tactical) return "T";
			else return "U";
		}
		
		public static ExType getExTypeFromStr(final String s)
		{
			if(s == null) return Unknown;
			String str = s.toUpperCase(Locale.US);
			if(str.startsWith("W"))
			{
				return Weights;
			}
			else if(str.startsWith("B"))
			{
				return BodyWeight;
			}
			else if(str.startsWith("C"))
			{
				return Cardio;
			}
			else if(str.startsWith("T"))
			{
				return Tactical;
			}
			else return Unknown;
		}
		
	}
	
	
	private ExData exercise;	
	private int index;
	private boolean completed=false;	
	private List<ExData> listAltMale;
	private List<ExData> listAltFemale;	
	private int curAltExIndex = -1;
	
	public final static String MALE="M";
	public final static String FEMALE="F";
	
	public final static int SAVE_REC_FOR_SET=1;  // we always re-calculate and save max rep for rep 1
	public final static int BODY_WEIGHT=0;
	
	public final static int MAX_WEIGHT = 500;
	//public final static int LAST_SET = 3; //after this exercise marked as completed
	
	//for Cardio - time in mins
	//private int cardio_time=0;
	
	
	public Exercise (final int ex_id)
	{
		exercise = new ExData(ex_id);
	}
	
	public Exercise (final ExData exData)
	{
		exercise = exData;
	}
	
	public ExData getExerciseData()
	{
		return exercise;
	}
	
	public ExData getCurExData(Gender g)
	{
		//ret main or alt
		if(curAltExIndex >=0)
		{
			if(g == Gender.Female)
			{
				if(listAltFemale != null && curAltExIndex < listAltFemale.size()) return listAltFemale.get(curAltExIndex);
				
			}
			else
			{
				if(listAltMale != null && curAltExIndex < listAltMale.size()) return listAltMale.get(curAltExIndex);
			}			
		}
		return exercise;
	}
	
	/**
	 * 
	 * @return id of Main or Alt exercise (what we are currently doing)
	 */
	public int getCurExId(Gender g)
	{
		if(curAltExIndex >=0)
		{
			if(g == Gender.Female)
			{
				if(listAltFemale != null && curAltExIndex < listAltFemale.size()) return listAltFemale.get(curAltExIndex).getExId();
				
			}
			else
			{
				if(listAltMale != null && curAltExIndex < listAltMale.size()) return listAltMale.get(curAltExIndex).getExId();
			}
		}
		return exercise.getExId();
	}
	
	public int getId()
	{
		if(exercise == null) return -1;
		return exercise.getExId();
	}
	
	public void setLogId(final long logId)
	{
		if(exercise == null) return;
		exercise.setLogId(logId);
	}
	
	public long getLogId()
	{
		if(exercise == null) return -1L;
		return exercise.getLogId();
	}
	
	/**
	 * 
	 * @param setVal - set
	 * @param exId - main exercise or alternative, what we really did
	 * @param g - gender
	 */
	public void setMaxSetAnLogId( final int setVal, final int exId, Gender g, final int logId)
	{
		final int entryId = exercise.getExId();
		if(entryId == exId || exId <0)
		{
			exercise.setCurSet(setVal);
			exercise.setLogId(logId);
		}
		else
		{
			//alternative - we have to find it and in case if we changed gender and not find it  - just set complete to false
			List<ExData> dataList = (g == Gender.Female)? listAltFemale : listAltMale;			
			if(dataList != null)
			{
				int i=0;
				for(ExData data: dataList)
				{
					if(data.getExId() == exId)
					{
						this.curAltExIndex =i;
						data.setCurSet(setVal);
						data.setLogId(logId);
						break;
					}
					i++;
				}//for
				if(curAltExIndex <0) this.completed = false;
			}
			else this.completed = false;
		}
	}
	
	public ExType getExType()
	{
		if(exercise == null) return ExType.Unknown;
		return exercise.getExType();
	}
	
	//Recommended
	public double getRecommendedWeight(final WorkoutMode wk_mode)
	{
		if(exercise == null) return -1;
		return exercise.getRecommendedWeight(wk_mode, User.getUser().getFitnessLevel());
	}
	
	public int getRepsActual(final WorkoutMode md)
	{
		if(exercise == null) return -1;
		return exercise.getRepsActual(md);
	}
	
	public int getWtActual(final WorkoutMode md)
	{
		if(exercise == null) return -1;
		return exercise.getWtActual(md);
	}
	
	
	public int getCardioMinutes()
	{
		if(exercise == null) return -1;
		return exercise.getMinutes();
	}
	
	public void setCurSet(final int val){ if(exercise != null) exercise.setCurSet(val); }
	
	public void increaseCurSet(){ if(exercise != null) exercise.increaseCurSet(); }
	public void decreaseCurSet(){ if(exercise != null) exercise.decreaseCurSet(); }
	
	public int getCurSet(){ 
		if(exercise == null) return -1;
		return exercise.getCurSet(); 
	}
	
//	public int getMaxWeight()
//	{
//		return 200; // TODO
//	}
	
	public int getRecommendedSets()
	{
		return exercise.getRecommendedSets();
	}
	
	public int getLevel()
	{
		return exercise.getLevel();
	}
	
	public void setTitle(final String val)
	{
		exercise.setTitle(val);
	}
	
	public String getTitle()
	{
		return exercise.getTitle();
	}
	
	public String getCurTitle(Gender g)
	{
		ExData data = this.getCurExData(g);
		return data.getTitle();
	}
	
	public void setDescription(final String d)
	{
		exercise.setDescription(d);
	}
	
	/**
	 * Get description of shown exercice - cur or alt
	 * @param g
	 * @return
	 */
	public String getCurDescription(Gender g)  ////////////////////////////
	{
		ExData data = this.getCurExData(g);
		return data.getDescription();
	}
	
	public void setMaleImage(final String val)
	{
		exercise.setImgMale(val);
	}
	
	public String getImageMale()
	{
		return exercise.getImgMale();
	}
	
	public void setFemaleImage(final String val)
	{
		exercise.setImgFemale(val);
	}
	
	public String getImagefemale()
	{
		return exercise.getImgFemale();
	}
	
	public String getCurImage(Gender g)
	{
		if(g== Gender.Female)
		{
			if(curAltExIndex >=0 )
			{
				if(listAltFemale != null && curAltExIndex < listAltFemale.size()) return listAltFemale.get(curAltExIndex).getImgFemale();
				else return null;
			}
			else return exercise.getImgFemale();
		}
		else
		{
			if(curAltExIndex >=0 )
			{
				if(listAltMale != null && curAltExIndex < listAltMale.size()) return listAltMale.get(curAltExIndex).getImgFemale();
				else return null;
			}
			else return exercise.getImgFemale();
		}
	}
	
	public void addAltMale(final int id, final String t, final String d, final String imgM, final ExType mode)
	{
		if(listAltMale == null) listAltMale = new ArrayList<ExData>();
		listAltMale.add(new ExData(id, t, d, imgM, null, mode));
	}
	
	public List<ExData> getAltListMale()
	{
		return listAltMale;
	}
	
	public void addAltFemale(final int id, final String t, final String d, final String imgF,final ExType mode)
	{
		if(listAltFemale == null) listAltFemale = new ArrayList<ExData>();
		listAltFemale.add(new ExData(id, t, d, null, imgF,mode));
	}
	
	public void addAltFemale(final ExData data)
	{
		if(listAltFemale == null) listAltFemale = new ArrayList<ExData>();
		listAltFemale.add(data);
	}
	
	public void addAltMale(final ExData data)
	{
		if(listAltMale == null) listAltMale = new ArrayList<ExData>();
		listAltMale.add(data);
	}
	
	public List<ExData> getAltListFemale()
	{
		return listAltFemale;
	}
	
	public void setCurAltIndex(final int val){	
		curAltExIndex=val;	
		}
	public void clearAltIndex(){ curAltExIndex=-1; }	
	public int getCurAltIndex(){ return curAltExIndex; }
	
	//set prev Alt or this exercise
	public boolean setPrevAlt(Gender g)
	{
		if(g == Gender.Female)
		{
			if(curAltExIndex <0) return false;
			if(curAltExIndex==0)
			{
				curAltExIndex =-1; //set to main exercise
				return true;
			}
			if(listAltFemale != null && listAltFemale.size()>0)  
			{				
				curAltExIndex--;
				return true;				
			}
			return false;		
		}
		else
		{
			if(curAltExIndex <0) return false;
			if(curAltExIndex==0)
			{
				curAltExIndex =-1; //set to main exercise
				return true;
			}
			if(listAltMale != null && listAltMale.size()>0)  
			{				
				curAltExIndex--;
				return true;				
			}
			return false;		
		}
	}
	
	public boolean setNextAlt(Gender g)
	{
		if(g == Gender.Female)
		{
			if(listAltFemale == null || listAltFemale.size()==0) return false;
			final int nextInd = curAltExIndex+1;
			if(nextInd >=  listAltFemale.size()) return false;
			curAltExIndex = nextInd;
			return true;	
		}
		else
		{
			if(listAltMale == null || listAltMale.size()==0) return false;
			final int nextInd = curAltExIndex+1;
			if(nextInd >=  listAltMale.size()) return false;
			curAltExIndex = nextInd;
			return true;	
		}
	}
	
	public int getTotalAlts(Gender g)
	{
		if(g == Gender.Female)
		{
			return (listAltFemale == null)? 0 :  listAltFemale.size();
		}
		else return (listAltMale == null)? 0 :  listAltMale.size();
	}
	
	public ExData getCurAlt(Gender g)
	{
		if(curAltExIndex <0) return null;
		if(g == Gender.Female)
		{
			if(listAltFemale == null || curAltExIndex >= listAltFemale.size()) return null;
			return listAltFemale.get(curAltExIndex);
		}
		else
		{
			if(listAltMale == null || curAltExIndex >= listAltMale.size()) return null;
			return listAltMale.get(curAltExIndex);
		}
	}
	
	public boolean isHaveAlts(Gender g)
	{
		if(g == Gender.Female)
		{
			if(listAltFemale != null && listAltFemale.size()>0) return true;
		}
		else if(listAltMale != null && listAltMale.size()>0) return true;
		
		return false;
	}
	
	public void setIndex(final int val)
	{
		index = val;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public void setCompleted(final boolean val)
	{
		completed =val;
	}
	
	public boolean getCompleted()
	{
		return completed;
	}
	
	public void resetAllLogs()
	{
		if(exercise != null) exercise.resetLog();
		resetLogs(listAltMale);
		resetLogs(listAltFemale);
		this.setCompleted(false);
		
	}
	
	private void resetLogs(List<ExData> data)
	{
		if(data == null) return;
		for(ExData d: data)
		{
			if(d != null) d.resetLog();
		}
		completed = false;
	}
	
	public void clearRecommended()
	{
		if(exercise != null) exercise.clearRecommended();		
		clearRecomVals(listAltMale);
		clearRecomVals(listAltFemale);
	}
	
	private void clearRecomVals(List<ExData> data)
	{
		if(data == null) return;
		for(ExData d: data)
		{
			if(d != null) d.clearRecommended();
		}
	}
	
	
//	public int getCardioTime(){ return cardio_time; }
//	public void setCardioTime(final int val){ cardio_time=val; }
	
	

}
