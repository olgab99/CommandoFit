package com.olmatech.fitness.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.User.FitnessLevel;

public class SettingsActivity extends BaseFragmentActivity{
	
	private boolean isWtLb = true;
	private boolean isKm = true;
	private FitnessLevel fitLevel = FitnessLevel.Average;	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);		
		
		int tm=60;
		boolean start = false, sound=false, advance = true, awake=true;
		DataStore ds = new DataStore(this);
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("time"))
			{
				tm = savedInstanceState.getInt("time");
			}
			if(savedInstanceState.containsKey("start"))
			{
				start = savedInstanceState.getBoolean("start");
			}
			if(savedInstanceState.containsKey("sound"))
			{
				sound = savedInstanceState.getBoolean("sound");
			}	
			if(savedInstanceState.containsKey("advance"))
			{
				advance = savedInstanceState.getBoolean("advance");
			}	
			if(savedInstanceState.containsKey("awake"))
			{
				awake = savedInstanceState.getBoolean("awake");
			}	
			
			if(savedInstanceState.containsKey("unit_weight"))
			{
				isWtLb = savedInstanceState.getBoolean("unit_weight");
			}	
			if(savedInstanceState.containsKey("unitdist"))
			{
				isKm = savedInstanceState.getBoolean("unitdist");
			}	
			
			if(savedInstanceState.containsKey("fit_level"))
			{
				int val = savedInstanceState.getInt("fit_level");
				fitLevel = FitnessLevel.getFitnessLevelFromId(val);
			}	
		}
		else
		{			
			tm = ds.getRestTime();	
			start = ds.getStartTimerOnSave();
			sound = ds.getPlaySoundOnTimeUp();
			advance = ds.getAdvanceOnComplete();
			awake = ds.getKeepAwake();
		}		
			
		RadioButton rb;
		if(tm == 30)
		{
			rb = (RadioButton)this.findViewById(R.id.radio_30);
		}
		else if(tm==45)
		{
			rb = (RadioButton)this.findViewById(R.id.radio_45);
		}
		else
		{
			rb = (RadioButton)this.findViewById(R.id.radio_60);
		}
		rb.setChecked(true);		
		CheckBox cb = (CheckBox)this.findViewById(R.id.cb_start);
		cb.setChecked(start);
		
		cb = (CheckBox)this.findViewById(R.id.cb_sound);
		cb.setChecked(sound);
		
		cb = (CheckBox)this.findViewById(R.id.cb_advance);
		cb.setChecked(advance);
		
		cb = (CheckBox)this.findViewById(R.id.cb_awake);
		cb.setChecked(awake);
				
		if(ds.getIsUserWeightUnitsLbs())
		{
			rb = (RadioButton)this.findViewById(R.id.radio_lbs);	
			isWtLb= true;
		}
		else
		{
			rb = (RadioButton)this.findViewById(R.id.radio_kg);	
			isWtLb = false;
		}
		rb.setChecked(true);	
		if(ds.getDistanceUnitsKm())
		{
			rb = (RadioButton)this.findViewById(R.id.radio_km);
			isKm = true;
		}
		else{
			rb = (RadioButton)this.findViewById(R.id.radio_ml);
			isKm = false;
		}
		rb.setChecked(true);	
		
		//fitness level
		fitLevel = User.getUser().getFitnessLevel();
		RadioButton radio;
		if(fitLevel == FitnessLevel.Low)
		{
			radio = (RadioButton)this.findViewById(R.id.radio_low);			
		}
		else if(fitLevel == FitnessLevel.Average)
		{
			radio = (RadioButton)this.findViewById(R.id.radio_average);			
		}
		else 
		{
			radio = (RadioButton)this.findViewById(R.id.radio_high);			
		}
		radio.setChecked(true);
	}
	
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		RadioButton rb = (RadioButton)this.findViewById(R.id.radio_30);
		if(rb.isChecked()) outState.putInt("time", 30);
		else{
			rb = (RadioButton)this.findViewById(R.id.radio_30);
			if(rb.isChecked()) outState.putInt("time", 45);
			else if(rb.isChecked()) outState.putInt("time", 60);
		}
		
		CheckBox cb = (CheckBox)this.findViewById(R.id.cb_start);
		outState.putBoolean("start", cb.isChecked());
		
		cb = (CheckBox)this.findViewById(R.id.cb_sound);
		outState.putBoolean("sound", cb.isChecked());
		
		cb= (CheckBox)this.findViewById(R.id.cb_advance);
		outState.putBoolean("advance", cb.isChecked());
		
		cb= (CheckBox)this.findViewById(R.id.cb_awake);
		outState.putBoolean("awake", cb.isChecked());
		
		outState.putBoolean("unit_weight", isWtLb);
		outState.putBoolean("unitdist", isKm);
		outState.putInt("fit_level", FitnessLevel.getId(fitLevel));
		
		super.onSaveInstanceState(outState);
	}	
	
	
	public void onButtonClick(View view)
	{
		switch(view.getId()) {
		case R.id.settings_but_save:
			doClose(true);
			break;
		case R.id.settings_but_cancel:
			doClose(false);
			break;		
		default:
			super.onButtonClick(view);
			break;
		}		
	}
	
	
	
	
	
	



	private void doClose(final boolean save)
	{
		if(save)
		{
			//save data
			CheckBox cb = (CheckBox)this.findViewById(R.id.cb_start);
			DataStore ds = new DataStore(this);
			ds.setStartTimerOnSave(cb.isChecked());
			cb = (CheckBox)this.findViewById(R.id.cb_sound);
			ds.setPlaySoundOnTimeUp(cb.isChecked());
			
			cb = (CheckBox)this.findViewById(R.id.cb_advance);
			ds.setAdvanceOnComplete(cb.isChecked());
			
			cb = (CheckBox)this.findViewById(R.id.cb_awake);
			ds.setKeepAwake(cb.isChecked());
			
			RadioButton rb = (RadioButton)this.findViewById(R.id.radio_30);
			if(rb.isChecked()) ds.setRestTime(30);
			else{
				rb = (RadioButton)this.findViewById(R.id.radio_45);
				if(rb.isChecked()) ds.setRestTime(45);
				else ds.setRestTime(60);
			}
			
			ds.setUserWeightUnits(isWtLb);
			ds.setDistanceUnits(isKm);	
			ds.setFitnessLevel(FitnessLevel.getId(fitLevel));	
			
			User user = User.getUser();
			user.setDistanceKm(isKm);
			user.setIsWeightLbs(isWtLb);
			user.setFitnessLevel(fitLevel);
			
			this.setResult(Common.RESULT_OK);
			
		}	
		else
		{
			this.setResult(Common.RESULT_CANCEL);
		}
		
		
		finish();
	}



	@Override
	public int getId() {
		return Common.ACT_Settings;
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    //DataStore ds = new DataStore(this);
	    // Check which radio button was clicked
	    switch(view.getId()) {	       
	        case R.id.radio_kg:
	        	if (checked)
	            {
	        		isWtLb=false;            	
	            }	
	        	break;
	        case R.id.radio_lbs:
	        	if (checked)
	            {
	        		isWtLb=true;	            	
	            }	
	        	break;
	        case R.id.radio_km:
	        	if (checked)
	        	{
	        		isKm=true;      		
	        	}
	        	break;
	        case R.id.radio_ml:
	        	if (checked)
	        	{
	        		isKm=false;        		
	        	}
	        	break;	 
	        case R.id.radio_low:
	        	if (checked)
	        	{
	        		fitLevel=FitnessLevel.Low;	        		
	        	}
	        	break;
	        case R.id.radio_average:
	        	if (checked)
	        	{
	        		fitLevel =FitnessLevel.Average;
	        		 		
	        	}
	        	break;
	        case R.id.radio_high:
	        	if (checked)
	        	{
	        		fitLevel=FitnessLevel.High;	        		    		
	        	}
	        	break;
	        	
	        default: break;
	    }
	}
	
	

}
