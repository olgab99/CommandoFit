package com.olmatech.fitness.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

public class DateSelectActivity extends BaseFragmentActivity{
	
	private final static int START_YEAR = 2013;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dlg_date_select);
		
		Spinner spYear = (Spinner)this.findViewById(R.id.log_year_select);
	    Integer[] years = new Integer[50];
	    int yr = START_YEAR;
	    for(int i=0; i< 50; i++,yr++)
	    {
	    	years[i] = Integer.valueOf(yr);
	    }
	    ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, years);
	    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spYear.setAdapter(dataAdapter);
	    
	    //set to current year
	    Calendar rightNow = GregorianCalendar.getInstance(); //today		
		int pos = rightNow.get(Calendar.YEAR) - START_YEAR;
	    
	    spYear.setSelection(pos, false);
	}
	
	public void onButtonClick(View view) 
	{
		switch(view.getId())
		{
		case R.id.but_set_date:
			setOtherDate();
			break;
		case R.id.but_cancel:
			this.setResult(Common.RESULT_CANCEL);
			finish();
			break;
		default: break;
		}
	}
	
	private void setOtherDate()
	{
		Spinner spYear = (Spinner)this.findViewById(R.id.log_year_select);
		Spinner spMonth = (Spinner)this.findViewById(R.id.log_month_select);
		int m = spMonth.getSelectedItemPosition();  // starts with 0 or 		
		Object obj =spYear.getSelectedItem();
		if(obj == null || m == Spinner.INVALID_POSITION)
		{
			this.showMsgDlg(R.string.mag_select_date, Common.REASON_NOT_IMPORTANT);
			return;
		}
		
		int yr = ((Integer)obj).intValue();
		//date = new GregorianCalendar(yr,m, 1);
		Intent data = this.getIntent();
		data.putExtra("year", yr);
		data.putExtra("month", m);
		this.setResult(Common.RESULT_OK, data);
		finish();
		
		
	}

	@Override
	public int getId() {		
		return Common.ACT_DateSelectActivity;
	}

}
