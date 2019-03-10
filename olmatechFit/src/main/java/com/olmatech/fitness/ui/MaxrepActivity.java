package com.olmatech.fitness.ui;

import java.text.DecimalFormat;

import kankan.wheel.widget.WheelView;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.adapters.ValueNumericAdapter;
import com.olmatech.fitness.adapters.WeightLbAdapter;
import com.olmatech.fitness.main.Common;
//dialog activity
public class MaxrepActivity extends BaseFragmentActivity{
	
	//private final static String TAG="MaxrepActivity";
	
	//private String dlgMsg="Error reading data.";
	//private boolean finishOnErr = false;
		
	TextView[] tvTableLbs; //array of views in teh table with values from 100 to 35 % in order

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_calculator);
				
		//set adapters
		WheelView wheelWeight = (WheelView)this.findViewById(R.id.maxrep_wheel_weight);		
		wheelWeight.setViewAdapter(new WeightLbAdapter(this, 1, 300, 0, false));
		
		WheelView wheelReps = (WheelView)this.findViewById(R.id.maxrep_wheel_reps);	
		wheelReps.setViewAdapter(new ValueNumericAdapter(this, 1, 50, 7));
		
		if(savedInstanceState != null)
		{			
			if(savedInstanceState.containsKey("wheight_ind"))
			{
				int ind = savedInstanceState.getInt("wheight_ind");
				
				wheelWeight.setCurrentItem(ind);
			}
			if(savedInstanceState.containsKey("reps_ind"))
			{
				int ind = savedInstanceState.getInt("reps_ind");
				
				wheelReps.setCurrentItem(ind);
			}
			this.initTextViewArray();
			//draw table
		}
		else
		{
			new InitTask().execute();
		}		
		
	}
	
	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.maxrep_but_calculate:
			doCalculate();
			break;
		case R.id.maxrep_but_close:
			doClose();	
			break;
		default:
			super.onButtonClick(view);
			break;
		}
		
	}	
		
	private void doClose()
	{		
		this.setResult(Common.RESULT_OK);
		finish();
	}
	
	private void doCalculate()
	{
		new SaveTask().execute();		
		
	}
	
	private boolean calculate()
	{
		//get data
		WheelView wheelWeight = (WheelView)this.findViewById(R.id.maxrep_wheel_weight);	
		WheelView wheelReps = (WheelView)this.findViewById(R.id.maxrep_wheel_reps);		
		
		
		int weight = WeightLbAdapter.getItemValueFromIndex(wheelWeight.getCurrentItem());
		int repVal =  wheelReps.getCurrentItem() +1;
		//final double wt =weight;
		//final double reps = repVal;
		
///////////////// REMOVE
		//calculate
//		double epley = wt*reps / 30f + wt;
//		double brzycki = wt *(36f /(37f -reps));
//		
//		double lander = (100f*wt) /(101.3 - 2.67123*reps);
//		
//		double lombardi = Math.pow(reps, 0.1) * wt;
//		
//		double mayhew = (100.0 * wt) / (52.2 + Math.pow(2.71828, -0.055*reps)*41.9);
//		
//		double oconner = wt*(1+0.025*reps);
//		
//		double wathan = (100.0* wt) /(48.8 + Math.pow(2.71828, -0.075*reps));
//		
//		DecimalFormat df = new DecimalFormat("#.##");
//		String testStr = "epley=" + df.format(epley) + "    -- brzycki= " + df.format(brzycki) +
//				" --lander=" + df.format(lander) +  "  -- lombardi=" + df.format(lombardi) +
//				"  - mayhew=" + df.format(mayhew) + "  -- oconner=" + df.format(oconner)  +
//				"  -- wathan=" + df.format(wathan);
//						
//		showTest(testStr);
		///////////// REMOVE END /////////////////
		
		final double maxRep = Common.calculateMaxRep(weight, repVal);  		
		
		//save to db
//		DBAdapter dbAdapter = DBAdapter.getAdapter(this.getApplicationContext());
//		User user = User.getUser();
//		CurProgram progr = CurProgram.getProgram();
//		final boolean ok = dbAdapter.saveOneMaxRep(user.getId(), progr.getCurExId(user.getGender()), maxRep, wt, reps);
//		if(ok)
//		{
//			this.drawRepsTable(maxRep);
//		}
		
		this.drawRepsTable(maxRep);		
		return true;
	}	
	
//	private void showTest(final String s)
//	{
//		this.runOnUiThread(new Runnable(){
//
//			@Override
//			public void run() {
//				showTestStr(s);
//				
//			}
//			
//		});
//	}
//	
//	private void showTestStr(final String s)
//	{
//		TextView tv = (TextView)this.findViewById(R.id.maxrep_msg);
//		tv.setText(s);
//		
//	}
	
	private void processSaveDone(final boolean res)
	{
		this.showRepsTable(res);
		if(!res)
		{
			showErrorDlg(R.string.err_save_data, Common.REASON_NOT_IMPORTANT);
			
		}
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		WheelView wheelWeight = (WheelView)this.findViewById(R.id.maxrep_wheel_weight);	
		outState.putInt("wheight_ind", wheelWeight.getCurrentItem());
		WheelView wheelReps = (WheelView)this.findViewById(R.id.maxrep_wheel_reps);		
		outState.putInt("reps_ind", wheelReps.getCurrentItem());
		super.onSaveInstanceState(outState);
	}


	private void initTextViewArray()
	{
		//init arary
			tvTableLbs = new TextView[15];
			tvTableLbs[0] =(TextView)this.findViewById(R.id.maxrep_100);
			tvTableLbs[1] =(TextView)this.findViewById(R.id.maxrep_95);
			tvTableLbs[2] =(TextView)this.findViewById(R.id.maxrep_90);
			tvTableLbs[3] =(TextView)this.findViewById(R.id.maxrep_85);
			tvTableLbs[4] =(TextView)this.findViewById(R.id.maxrep_80);
			tvTableLbs[5] =(TextView)this.findViewById(R.id.maxrep_75);
			tvTableLbs[6] =(TextView)this.findViewById(R.id.maxrep_70);
			tvTableLbs[7] =(TextView)this.findViewById(R.id.maxrep_65);
			tvTableLbs[8] =(TextView)this.findViewById(R.id.maxrep_60);
			tvTableLbs[9] =(TextView)this.findViewById(R.id.maxrep_55);
			tvTableLbs[10] =(TextView)this.findViewById(R.id.maxrep_50);
			tvTableLbs[11] =(TextView)this.findViewById(R.id.maxrep_45);
			tvTableLbs[12] =(TextView)this.findViewById(R.id.maxrep_40);
			tvTableLbs[13] =(TextView)this.findViewById(R.id.maxrep_35);
			tvTableLbs[14] =(TextView)this.findViewById(R.id.maxrep_30);	
	}
	

	//ret One max rep or -1
	private void initData()
	{
		initTextViewArray();
//		DBAdapter dbAdapter = DBAdapter.getAdapter(this.getApplicationContext());
//				
//		User user = User.getUser();
//		
//		int[] data = dbAdapter.getOneMaxRep(exId, user.getId());
//		
//		
//		if(data == null)
//		{
//			setWheelsVals(20, 7);
//			return -1;
//		}
		//set adapters
		
		setWheelsVals(0,0);				
		//return data[0];
	}
	
	private void setWheelsVals(final int wt, final int reps)
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				setWheelsValsUI(wt, reps);
				
			}
			
		});
	}
	
	private void setWheelsValsUI(final int wt, final int reps)
	{
		WheelView wheelWeight = (WheelView)this.findViewById(R.id.maxrep_wheel_weight);	
		WheelView wheelReps = (WheelView)this.findViewById(R.id.maxrep_wheel_reps);		
		if(wt >0)
		{
			final int ind = WeightLbAdapter.getItemIndexFromValue(wt);
			wheelWeight.setCurrentItem(ind);
		}
		else
		{
			wheelWeight.setCurrentItem(10);
		}		
		if(reps > 0) wheelReps.setCurrentItem(reps-1);
	}
	
	private void drawRepsTable(final double maxRep)
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				drawTable(maxRep);				
			}
			
		});
	}
	
	private void showRepsTable(final boolean show)
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showTable(show);				
			}
			
		});
	}
	
	private void showTable(final boolean show)
	{
		View tb = this.findViewById(R.id.maxrep_table);
		if(show) tb.setVisibility(View.VISIBLE);
		else tb.setVisibility(View.GONE);
	}
	
	private void drawTable(final double maxRepVal)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		
		TextView tv = (TextView)this.findViewById(R.id.maxrep_number);
		tv.setText(df.format(maxRepVal));
		
		if(tvTableLbs == null) return;
		final int cnt = tvTableLbs.length;
		double coeff = 1.0;
		final double diff = 0.05;
				
		for(int i=0; i < cnt; i++)
		{			
			tvTableLbs[i].setText(df.format(coeff*maxRepVal));
			coeff -=diff;
		}
		
	}
	
	//TODO - define
	private void processInitDone(final boolean res)
	{
		if(!res)
		{
			//no max rep in db - will calculate
//			dlgMsg = "Error accessing database.";
//			this.showDialog(Common.DLG_ERROR);
		}
	}
	

	
	
	/////////////////////// TASKS 
	
	private class SaveTask extends AsyncTask<String, Void, long[]>
	{
		private ProgressDialog dialog = new ProgressDialog(MaxrepActivity.this);  
		// can use UI thread here
		 protected void onPreExecute() { 
			 this.dialog.setMessage(getString(R.string.processing));  
			 this.dialog.show();  
		  }  


		@Override
		protected long[] doInBackground(String... params) {
			final boolean ok = calculate();
			
			return (ok)? new long[]{1L} : new long[]{0L};
		}
		
		protected void onPostExecute(final long[] result) {  
			try
			{
				if(dialog != null)
				{
					dialog.dismiss();
					dialog = null;
				}
			}
			catch(Exception e)
			{
				//nothing
			}	         
	          
	          if(result != null && result.length >0)
	          {
	        	  MaxrepActivity.this.processSaveDone((result[0] == 1)? true : false);
	          }	          
	        
		}     		
		
	}
	
	private class InitTask extends AsyncTask<String, Void, long[]>
	{
		private ProgressDialog dialog = new ProgressDialog(MaxrepActivity.this);  
		// can use UI thread here
		 protected void onPreExecute() { 
			 this.dialog.setMessage(getString(R.string.processing));  
			 this.dialog.show();  
		  }  

		@Override
		protected long[] doInBackground(String... arg0) {
			long[] res = new long[1];
//			int maxRep = initData();
//			if(maxRep >0)
//			{
//				res[0]=1;
//				drawRepsTable(maxRep);
//				showRepsTable(true);
//			}
//			else
//			{
//				res[0]=0;
//				showRepsTable(false);
//			}	
			
			initData();
			showRepsTable(false);
			return res;
		}
		
		protected void onPostExecute(final long[] result) {  
			try
			{
				if(dialog != null)
				{
					dialog.dismiss();
					dialog = null;
				}
			}
			catch(Exception e)
			{
				//nothing
			}	         
	          
	          if(result != null && result.length >0)
	          {
	        	  MaxrepActivity.this.processInitDone((result[0] == 1)? true : false);
	          }	          
	        
		}     		
		
	}

	@Override
	public int getId() {
		return Common.ACT_MaxrepActivity;
	}

}
/*

TableLayout tb = (TableLayout)this.findViewById(R.id.maxrep_table);
tb.removeAllViews();

TableRow row;
TextView tv;
LinearLayout cell;
TableLayout.LayoutParams tableRowParams;
LinearLayout.LayoutParams tvParams;

tableRowParams =  new TableLayout.LayoutParams (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
tableRowParams.setMargins(1,1,1,1);		
row = new TableRow (this);
row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
row.setBackgroundResource(R.color.txt_light);
cell = new LinearLayout(this);
cell.setLayoutParams(tableRowParams);
cell.setBackgroundResource(R.color.txt_light);
		
tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
tvParams.setMargins(1, 1, 1, 1);		

tv = new TextView(this);
tv.setLayoutParams(tvParams);
tv.setBackgroundResource(R.color.bgcolor);
tv.setSingleLine(true);
tv.setMaxLines(1);
tv.setPadding(6, 6, 6, 6);
tv.setText("95% 1 Max");

cell.addView(tv);

tv = new TextView(this);
tv.setLayoutParams(tvParams);
tv.setBackgroundResource(R.color.bgcolor);
tv.setSingleLine(true);
tv.setMaxLines(1);
tv.setPadding(6, 6, 6, 6);
tv.setText("500");

cell.addView(tv);
row.addView(cell);
tb.addView(row);
*/