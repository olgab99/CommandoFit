package com.olmatech.fitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;

public class AdvancedSettingsActivity extends BaseFragmentActivity{	
	
	private final static Integer[] wt_breaks = new Integer[]{Integer.valueOf(1),
			Integer.valueOf(15),
			Integer.valueOf(30),
			Integer.valueOf(50)};

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dlg_advanced_settings);
		
		DataStore ds = new DataStore(this);
		Spinner spInterval = (Spinner)this.findViewById(R.id.sp_weight_inc_select);
		Spinner spBreak = (Spinner)this.findViewById(R.id.sp_break_weight_select);
		
		
		 ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, wt_breaks);
		 dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 spBreak.setAdapter(dataAdapter);
		
		final int interval = ds.getWeightWheelInterval();
		final int br = ds.getWeightWheelBreak();
		
		int br_pos=-1;
		
		for(int i=0; i < wt_breaks.length; i++)
		{
			if(br == wt_breaks[i].intValue())
			{
				br_pos = i;
				break;
			}
		}
		if(br_pos <0) br_pos =0;
		
		//set
		spInterval.setSelection(interval -1);
		spBreak.setSelection(br_pos);
		
		
	}
	
	public void onButtonClick(View view) 
	{
		switch(view.getId())
		{
		case R.id.but_default:
			setToDefault();
			break;
		case R.id.but_cancel:
			this.setResult(Common.RESULT_CANCEL);
			finish();
			break;
		case R.id.but_save:
			saveValues();
			break;
		default: break;			
		}
	}
	
	private void saveValues()
	{
		Spinner spInterval = (Spinner)this.findViewById(R.id.sp_weight_inc_select);
		int wt_pos = spInterval.getSelectedItemPosition(); //start with 0, we have weight 1 - 5
		
		Spinner spBreak = (Spinner)this.findViewById(R.id.sp_break_weight_select);
		int br_pos = spBreak.getSelectedItemPosition();
		
		if(wt_pos == Spinner.INVALID_POSITION || br_pos == Spinner.INVALID_POSITION ||
				br_pos <0 || br_pos >= wt_breaks.length)
		{
			this.showMsgDlg(R.string.msg_input, Common.REASON_NOT_IMPORTANT);
			return;
		}
		
		int breakVal = wt_breaks[br_pos].intValue();
		
		//get values and save
//		DataStore ds = new DataStore(this);
//		ds.setWeightWheelInterval(wt_pos +1);
//		ds.setWeightWheelBreak(breakVal);
		
		Intent intent = this.getIntent();
		intent.putExtra("interval", wt_pos +1);
		intent.putExtra("break", breakVal);		
		this.setResult(Common.RESULT_OK, intent);
		finish();
	}
	
	private void setToDefault()
	{
		Spinner spInterval = (Spinner)this.findViewById(R.id.sp_weight_inc_select);
		Spinner spBreak = (Spinner)this.findViewById(R.id.sp_break_weight_select);
		
		spInterval.setSelection(0);
		spBreak.setSelection(0);
	}

	@Override
	public int getId() {		
		return Common.ACT_AdvancedSettingsActivity;
	}

}
