package com.olmatech.fitness.main;

import com.olmatech.fitness.main.Exercise.ExType;
import com.olmatech.fitness.main.User.FitnessLevel;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout.WorkoutMode;

public class ExData{
	final static String TAG ="ExData";
	private int id;
	private String title;
	private String description;
	private String imgMale;		
	private String imgFemale;
	private ExType mode = ExType.Weights;  //default, or C - cardio
	private int level=0; //for cardio
	private int recommendedSets=0; // comes from workout, for cardio - minutes - recommended value
	private Gender gender = Gender.Unknown; // for alternatives
	
	//recommended	
	private double maxrep_weigt=0;
	private int en_reps =0;
	private int en_wt_actual=0;
	private int bb_reps =0;
	private int bb_wt_actual =0;
	
	//log id
	private long logId = -1;
	
	//cur set for log data
	private int curSet = 0; //will indicate saved in Log set
	
	//cardio minutes
	private int cardio_time=0;
	
	private final static double[] MAXREP_COEFF_ENDUR = new double[]{0.4,0.535, 0.67};
	private final static double[] MAXREP_COEFF_MASS= new double[]{0.67,0.76, 0.85};
		
	
	public ExData(final int exid)
	{
		id=exid;			
	}
	
	public ExData(final int exid, final String t, final String d, final String imgM, final String imgF, 
			final ExType md)
	{
		this(exid);
		title = t;
		description=d;
		imgMale=imgM;
		imgFemale=imgF;
		mode = md;
	}
	
	public int getExId()
	{
		return id;
	}
	
	public void setTitle(final String val)
	{
		title = val;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setDescription(final String val)
	{
		description = val;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setImgMale(final String val)
	{
		imgMale=val;
	}
	
	public void setImgFemale(final String val)
	{
		imgFemale=val;
	}
	
	public String getImgMale()
	{
		return imgMale;
	}
	
	public String getImgFemale()
	{
		return imgFemale;
	}
	
	public void setSets(final int val)
	{
		recommendedSets = val;
	}
	
	public int getRecommendedSets()
	{
		return recommendedSets;
	}
	
	public void setMinutes(final int val)
	{
		cardio_time = val;
	}
	
	public int getMinutes()
	{
		return cardio_time;
	}
	
	public void setExType(final ExType tp)
	{
		mode = tp;
	}
	
	public void setTypeFromStr(final String s)
	{
		mode = ExType.getExTypeFromStr(s);
	}
	
	public ExType getExType()
	{
		return mode;
	}
	
	public void setGender(Gender g)
	{
		gender = g;
	}
	
	public Gender getGender()
	{
		return gender;
	}
	
	public void setLevel(final int val)
	{
		level = val;
	}
	
	public int getLevel(){ return level; }
	
	//Recommended
	
	public void setMaxrep(final double wt)
	{
		maxrep_weigt=wt;				
	}
	
//	public int getRecommendedReps(final WorkoutMode wk_mode, FitnessLevel fitLevel)
//	{
//		if(recommended == null) return -1;
//		if(wk_mode == WorkoutMode.Endurance)
//		{
//			double reps;
//			
//			return recommended.rec_reps_en;			
//		}
//		else if(wk_mode == WorkoutMode.Bodybuilding)
//		{
//			return recommended.rec_reps_bb;
//		}
//		return -1;
//	}
	public double getMaxRep()
	{
		return maxrep_weigt;
		
	}
	
	//Ret int[3] - low, med, high - vbased on % for mode, the last val rounded to ceiling
	public static int[] getRecommendedWeightRange(final double maxrep,final WorkoutMode wk_mode)
	{
		if(maxrep <=0) return null;
		if(wk_mode == WorkoutMode.Endurance)
		{			
			return new int[]{(int)(maxrep*MAXREP_COEFF_ENDUR[0]),
					(int)(maxrep*MAXREP_COEFF_ENDUR[1]),
					(int)(maxrep*MAXREP_COEFF_ENDUR[2] +1.0)					
				};
		}
		else if(wk_mode == WorkoutMode.Bodybuilding)
		{
			return new int[]{(int)(maxrep*MAXREP_COEFF_MASS[0] ),
					(int)(maxrep*MAXREP_COEFF_MASS[1] ),
					(int)(maxrep*MAXREP_COEFF_MASS[2] +1.0)					
				};
		}
		return null;
		
	}
	
	public static double getRecommendedWeight(final double maxrep, final WorkoutMode wk_mode, FitnessLevel fitLevel)
	{
		if(maxrep <=0) return -1.0;
		
		if(wk_mode == WorkoutMode.Endurance)
		{
			double coeff;
			switch(fitLevel)
			{
			case Low: 
				coeff=MAXREP_COEFF_ENDUR[0];
				break;			
			case High:
				coeff=MAXREP_COEFF_ENDUR[2];
				break;
			default:
				coeff=MAXREP_COEFF_ENDUR[1];
				break;
			}
			return (maxrep*coeff);						
		}
		else if(wk_mode == WorkoutMode.Bodybuilding)
		{
			double coeff;
			switch(fitLevel)
			{
			case Low: 
				coeff=MAXREP_COEFF_MASS[0];
				break;			
			case High:
				coeff=MAXREP_COEFF_MASS[2];
				break;
			default:
				coeff=MAXREP_COEFF_MASS[1];
				break;
			}
			return (maxrep*coeff);	
				
		}
		
		return -1;
		
	}
	
	
	
	public double getRecommendedWeight(final WorkoutMode wk_mode, FitnessLevel fitLevel)
	{
		if(maxrep_weigt <=0) return -1;
		if(wk_mode == WorkoutMode.Endurance)
		{
			double coeff;
			switch(fitLevel)
			{
			case Low: 
				coeff=MAXREP_COEFF_ENDUR[0];
				break;			
			case High:
				coeff=MAXREP_COEFF_ENDUR[2];
				break;
			default:
				coeff=MAXREP_COEFF_ENDUR[1];
				break;
			}
			return (maxrep_weigt*coeff);						
		}
		else if(wk_mode == WorkoutMode.Bodybuilding)
		{
			double coeff;
			switch(fitLevel)
			{
			case Low: 
				coeff=MAXREP_COEFF_MASS[0];
				break;			
			case High:
				coeff=MAXREP_COEFF_MASS[2];
				break;
			default:
				coeff=MAXREP_COEFF_MASS[1];
				break;
			}
			return (maxrep_weigt*coeff);	
				
		}
		
		return -1;
		
	}
	
	public void setLogId(final long val){ logId=val; }
	
	public long getLogId() { return logId; }
	
	public void setCurSet(final int val){ curSet=val;  }	
	public void increaseCurSet(){ curSet++;  }
	public void decreaseCurSet(){ if(curSet >0) curSet--; }	
	public int getCurSet(){ return curSet; }
	
	public void resetLog()
	{
		logId=-1;
		curSet=0;
	}
	
	public void setActual(final int wt, final int reps, final WorkoutMode md)
	{
		if(md== WorkoutMode.Endurance)
		{
			en_reps = reps;
			en_wt_actual = wt;
		}
		else
		{
			bb_reps = reps;
			bb_wt_actual = wt;
		}
		
	}	
	
	public int getRepsActual(final WorkoutMode md)
	{
		return (md== WorkoutMode.Endurance)? en_reps : bb_reps;
	}
	
	public int getWtActual(final WorkoutMode md)
	{
		return (md== WorkoutMode.Endurance)? en_wt_actual : bb_wt_actual;
	}
	
	public void clearRecommended()
	{
		maxrep_weigt=0.0;
		en_reps =0;
		en_wt_actual=0;
		bb_reps =0;
		bb_wt_actual =0;
	}

	
}
