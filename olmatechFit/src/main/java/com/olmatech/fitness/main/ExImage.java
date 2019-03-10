package com.olmatech.fitness.main;

public class ExImage {
	
	public enum Img_type
	{
		Male,
		Female,
		All
	}
	
	private Img_type imgType;
	private String link;
	
	public ExImage(final Img_type tp, final String lk)
	{
		imgType=tp;
		link=lk;
	}
	
	public Img_type getType()
	{
		return imgType;
	}
	
	public String getLink()
	{
		return link;
	}

}
