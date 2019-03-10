package com.olmatech.fitness.main;

//struct to hold basic log data
public class LogData {
	private int seqNum;
	private int weight;
	private int reps;
	
	public LogData(final int seq, final int w, final int r)
	{
		seqNum = seq;
		weight = w;
		reps = r;
	}
	
	public int getSeqNum(){ return seqNum; }
	
	public int getWeight(){ return weight; }
	
	public int getReps(){ return reps; }

}
