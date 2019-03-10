package com.olmatech.fitness.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

public class UnitConvertActivity extends BaseFragmentActivity{	

	@Override
	protected void onCreate(Bundle b) {		
		super.onCreate(b);
		this.setContentView(R.layout.activity_wt_converter);
		setClosable();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setClosable(){
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			this.setFinishOnTouchOutside(false);
		}
	}

	@Override
	public int getId() {		
		return Common.ACT_UnitConvertActivity;
	}
	
	public void onButtonClick(View view){
		switch(view.getId()){
		case R.id.but_close:
			finish();
			break;
		default: break;
		}
	}

}
