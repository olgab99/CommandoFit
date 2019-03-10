package com.olmatech.fitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

//dialog
public class CalcMenuActivity extends BaseFragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_calc_menu);
				
	}
	
	public void onButtonClick(View view)
	{
		Intent intent;
		switch(view.getId())
		{
		case R.id.calc_menu_but_onemaxrep:
			// display calculator 			
			intent = new Intent(this, MaxrepActivity.class);
			intent.putExtra("next", Common.ACT_MaxrepActivity);
			this.setResult(Common.RESULT_OK, intent);
			finish();
			break;
		case R.id.calc_menu_but_hr:
			intent = new Intent(this, HrCalculatorActivity.class);			
			intent.putExtra("calculator", 1);
			intent.putExtra("next", Common.ACT_HrCalculatorActivity);
			this.setResult(Common.RESULT_OK, intent);
			finish();
			break;
		case R.id.calc_menu_but_kar:
			intent = new Intent(this, HrCalculatorActivity.class);
			intent.putExtra("calculator", 2);
			intent.putExtra("next", Common.ACT_HrCalculatorActivity);
			this.setResult(Common.RESULT_OK, intent);
			finish();
			break;
		case R.id.calc_but_close:	
			this.setResult(Common.RESULT_CANCEL);
			finish();
			break;
		case R.id.calc_but_convert:
			intent = new Intent(this,UnitConvertActivity.class);			
			intent.putExtra("next", Common.ACT_UnitConvertActivity);
			this.setResult(Common.RESULT_OK, intent);
			finish();
			break;
		default:
			super.onButtonClick(view);
			break;
		}		
			
	}
	
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);				
	}

	@Override
	public int getId() {		
		return Common.ACT_CalcMenuActivity;
	}

	

	
}
