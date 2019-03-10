package com.olmatech.fitness.main;

import java.util.Locale;


public class User {
	
	public enum Gender
	{
		Male,
		Female,
		Unknown;
		public static String getGenderLetter(Gender g)
		{
			if(g == Female) return "F";
			else if(g == Male) return "M";
			else return "U";
		}
		
		public static Gender getGenderFromStr(final String s)
		{
			if(s == null) return Unknown;
			String str = s.toUpperCase(Locale.US);
			if(str.startsWith("F")) return Female;
			else if(str.startsWith("M")) return Male;
			else return Unknown;
		}
	}
	
	public enum FitnessLevel
	{
		Low,
		Average,
		High;
		
		public static int getId(final FitnessLevel fl)
		{
			switch(fl)
			{
			case Low: return 1;
			case High: return 3;
			default: return 2;
			}
		}
		
		public static FitnessLevel getFitnessLevelFromId(final int id)
		{
			switch(id)
			{
			case 1: return FitnessLevel.Low;
			case 3: return FitnessLevel.High;
			default: return FitnessLevel.Average;
			}
		}
	}
	
	private static User refUser;
	private Gender gender = Gender.Male;
	private String name;
	private int age=0;
	private int id=1; //TODO
	private float weight;
	private boolean isWeightLbs= true;
	private boolean isDistanceKm = true;
	private FitnessLevel fitnessLevel = FitnessLevel.Average;
	private int restHR = -1; // resting hr
	
	private User(){}
	
	public static User getUser()
	{
		if(refUser==null) refUser = new User();
		return refUser;
	}
	
	public Gender getGender()
	{
		return gender;
	}
	
	public void setGender(final Gender g)
	{
		gender=g;
	}
	
	
	public void setId(final int val)
	{
		id=val;
	}
	
	public int getId(){ return id; }
	
	public void setName(final String val){ name=val;}
	
	public String getName(){ return name; }
	
	public void setAge(final int val){ age=val; }
	
	public int getAge(){ return age; }
	
	public void setIsWeightLbs(final boolean islbs){ isWeightLbs= islbs; }
	
	public boolean getIsWeightLbs(){ return isWeightLbs; }
	
	public void setDistanceKm(final boolean val){ isDistanceKm = val; }
	
	public boolean getDistanceKm(){ return isDistanceKm; }
	
	public void setFitnessLevel(final FitnessLevel val){ fitnessLevel=val; }
	
	public FitnessLevel getFitnessLevel(){ return fitnessLevel; }
	
	//if fit level - low - ret Average for calculations
	public FitnessLevel getAdjuatedFitnessLevel(){
		return (fitnessLevel==FitnessLevel.Low)? FitnessLevel.Average : fitnessLevel;
	}

	public float getWeight() {
		// TODO Auto-generated method stub
		return weight;
	}
	
	public void setWeight(final float val){
		weight=val;
	}
	
	public void setRestHR(final int val){ restHR=val; }
	public int getRestHR(){ return restHR; }
	
	


}
