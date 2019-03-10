package com.olmatech.fitness.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.User.FitnessLevel;
import com.olmatech.fitness.main.User.Gender;

//dialog showing interview
public class InterviewActivity extends BaseFragmentActivity{
	
	private static User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_interview);
		DataStore ds = new DataStore(this);
		user = User.getUser();
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("age"))
			{
				String val = savedInstanceState.getString("age");
				EditText txt = (EditText)this.findViewById(R.id.interview_age);
				txt.setText(val);
			}
			if(savedInstanceState.containsKey("weight"))
			{
				String val = savedInstanceState.getString("weight");
				EditText txt = (EditText)this.findViewById(R.id.interview_weight);
				txt.setText(val);
			}
			if(savedInstanceState.containsKey("name"))
			{
				String val = savedInstanceState.getString("name");
				EditText txt = (EditText)this.findViewById(R.id.interview_name);
				txt.setText(val);
			}
			if(savedInstanceState.containsKey("hr"))
			{
				String val = savedInstanceState.getString("hr");
				EditText txt = (EditText)this.findViewById(R.id.interview_rest_hr);
				txt.setText(val);
			}
		}
		else
		{
			//age
			int age = user.getAge();
			if(age > 0)
			{
				EditText txt = (EditText)this.findViewById(R.id.interview_age);
				txt.setText(String.format("%d", age));
			}
			//weight
			final float weight = user.getWeight();
			if(weight > 1.0f)
			{
				EditText txt = (EditText)this.findViewById(R.id.interview_weight);
				txt.setText(String.format("%.1f", weight));
			}
			//nmae
			String n = user.getName();
			if(n!= null)
			{
				EditText txt = (EditText)this.findViewById(R.id.interview_name);
				txt.setText(n);
			}
			final int userHR = user.getRestHR();
			if(userHR>0)
			{
				EditText txt = (EditText)this.findViewById(R.id.interview_rest_hr);
				txt.setText(String.format("%d", userHR));
			}
		}
		//display saved data if any	
		//the rest is saved on input
		// M/F
		Gender g = user.getGender();
		boolean isMale = (g == Gender.Male || g== Gender.Unknown)? true : false ;
		RadioButton radio;
		if(isMale)
		{
			radio = (RadioButton)this.findViewById(R.id.radio_male);
			radio.setChecked(true);
			ds.setIsMale(true);
		}
		else
		{
			radio = (RadioButton)this.findViewById(R.id.radio_female);
			radio.setChecked(true);
			ds.setIsMale(false);
		}		
		
		//units
		if(user.getIsWeightLbs())
		{
			radio = (RadioButton)this.findViewById(R.id.radio_lbs);
			radio.setChecked(true);
			ds.setUserWeightUnits(true);
		}
		else
		{
			radio = (RadioButton)this.findViewById(R.id.radio_kg);
			radio.setChecked(true);
			ds.setUserWeightUnits(false);
		}
		//fitness level
		FitnessLevel lev = user.getFitnessLevel();		
		if(lev == FitnessLevel.Low)
		{
			radio = (RadioButton)this.findViewById(R.id.radio_low);			
		}
		else if(lev == FitnessLevel.Average)
		{
			radio = (RadioButton)this.findViewById(R.id.radio_average);			
		}
		else 
		{
			radio = (RadioButton)this.findViewById(R.id.radio_high);			
		}
		radio.setChecked(true);
		
//		Button butSave = (Button)this.findViewById(R.id.interview_but_save);
//		butSave.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View arg0) {
//				saveData();			
//			}
//			
//		});
		
		
	}
	
	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.common_small_close:
		case R.id.interview_but_save:
			saveData(true);	
			break;
		case R.id.interview_but_cancel:
			saveData(false);
			break;
		default:
			super.onButtonClick(view);
			break;
		}	
				
	}
	
	
	private void saveData(final boolean save)
	{
		if(save)
		{
			//save
			DataStore ds = new DataStore(this);
			ds.setInterviewDone();
			
			EditText txt = (EditText)this.findViewById(R.id.interview_age);
			String val = txt.getText().toString();
			if(val != null && val.length() > 0)
			{
				int age=0;
				try
				{
					age = Integer.parseInt(val);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					age= 0;
				}
				ds.setAge(age);
				user.setAge(age);
			}
			else{
				ds.setAge(0);
				user.setAge(0);
			}
			
			//name
			txt = (EditText)this.findViewById(R.id.interview_name);
			val = txt.getText().toString();
			user.setName(val);
			ds.setUserName(val);
					
			txt = (EditText)this.findViewById(R.id.interview_weight);
			val = txt.getText().toString();
			if(val != null && val.length() > 0)
			{
				float wt=0f;
				try
				{
					wt = Float.parseFloat(val);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					wt =0f;
				}
				ds.setWeight(wt);
				user.setWeight(wt);
					
			}
			else {
				ds.setWeight(0);
				user.setWeight(0);
			}
			
			int hrVal = getIntValFromEdit(R.id.interview_rest_hr);	
			if(hrVal >0)
			{
				ds.setRestHR(hrVal);				
				user.setRestHR(hrVal);
			}
			
		}
		
		this.setResult(Common.RESULT_OK);
		finish();		
		
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    DataStore ds = new DataStore(this);
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_female:
	            if (checked)
	            {
	            	ds.setIsMale(false);
	            	user.setGender(Gender.Female);
	            }	               
	            break;
	        case R.id.radio_male:
	        	if (checked)
	            {
	            	ds.setIsMale(true);
	            	user.setGender(Gender.Male);
	            }                
	            break;
	        case R.id.radio_kg:
	        	if (checked)
	            {
	            	ds.setUserWeightUnits(false);
	            	user.setIsWeightLbs(false);
	            }	
	        	break;
	        case R.id.radio_lbs:
	        	if (checked)
	            {
	            	ds.setUserWeightUnits(true);
	            	user.setIsWeightLbs(true);
	            }	
	        	break;	      
	        case R.id.radio_low:
	        	if (checked)
	            {
	        		ds.setFitnessLevel(FitnessLevel.getId(FitnessLevel.Low));
	        		 user.setFitnessLevel(FitnessLevel.Low);
	            }	
	        	break;
	        case R.id.radio_average:
	        	if (checked)
	            {
	        		ds.setFitnessLevel(FitnessLevel.getId(FitnessLevel.Average));
	        		user.setFitnessLevel(FitnessLevel.Average); 		
	            }	
	        	break;
	        case R.id.radio_high:
	        	if (checked)
	            {
	        		ds.setFitnessLevel(FitnessLevel.getId(FitnessLevel.High));
	        		user.setFitnessLevel(FitnessLevel.High);      
	            }	
	        	break;
	    }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		EditText txt = (EditText)this.findViewById(R.id.interview_age);
		String val = txt.getText().toString();
		if(val != null && val.length() > 0)
		{
			outState.putString("age", val);
		}
		txt = (EditText)this.findViewById(R.id.interview_weight);
		val = txt.getText().toString();
		if(val != null && val.length() > 0)
		{
			outState.putString("weight", val);
		}
		txt = (EditText)this.findViewById(R.id.interview_name);
		val = txt.getText().toString();
		if(val != null && val.length() > 0)
		{
			outState.putString("name", val);
		}
		txt = (EditText)this.findViewById(R.id.hr_rest_hr);
		val = txt.getText().toString();
		if(val != null && val.length() > 0)
		{
			outState.putString("hr", val);
		}
		
		super.onSaveInstanceState(outState);
	}

	@Override
	public int getId() {
		return Common.ACT_InterviewActivity;
	}

	
	
	

}
