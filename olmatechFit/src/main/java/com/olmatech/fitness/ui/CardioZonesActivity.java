package com.olmatech.fitness.ui;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.view.CardioZonesFragment;
import com.olmatech.fitness.view.CardioZonesFragment.ICardioZonesListener;

public class CardioZonesActivity extends BaseFragmentActivity implements ICardioZonesListener{
	
	private CardioZonesFragment frCardioZones;
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		this.setContentView(R.layout.activity_cardio_zones);
		
		frCardioZones = (CardioZonesFragment) this.getSupportFragmentManager().findFragmentById(R.id.frag_cardio_zones);
		
		User user = User.getUser();
		int age = user.getAge();
		
		if(age >0)
		{			
			int hr = user.getRestHR();
			String s;
			Common.Calculator calc;
			if(hr > 0){
				s = this.getString(R.string.title_cardiohr);
				s = String.format(Locale.US,  s, age, hr);
				calc = Common.Calculator.Karvonen;
			}
			else
			{				
				hr = -1;	
				s = this.getString(R.string.title_cardio_zones);
				s = String.format(Locale.US,  s, age);
				calc = Common.Calculator.General;
			}
			TextView tv = (TextView) this.findViewById(R.id.title_cardio_zones);
			tv.setText(this.getString(R.string.cardio_zones) + s);
			
			frCardioZones.showZones(age, hr, calc);
		}
		else
		{
			frCardioZones.showDefaultImage(true);
		}
	}
	
	public void onCloseClick(View view)
	{
		finish();			
	}
	
	
	@Override
	public void onCardioZonesActivityAttached() {
		// nothing	
	}

	@Override
	public int getId() {
		return Common.ACT_CardioZonesActivity;
	}

	@Override
	public void onCardioZoneClick(String txt) {
		if(txt != null)
		{
			Intent intent = this.getIntent();
			intent.putExtra("hr", txt);
			this.setResult(Common.RESULT_OK, intent);
			finish();
		}
		
	}

}
