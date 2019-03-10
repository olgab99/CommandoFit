package com.olmatech.fitness.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.view.CardioZonesFragment;
import com.olmatech.fitness.view.CardioZonesFragment.ICardioZonesListener;

// Target heart rate calculator Max HR = 220 - age
public class HrCalculatorActivity extends BaseFragmentActivity implements ICardioZonesListener{

	private Common.Calculator calc_type=Common.Calculator.General; //0 - general, 1 - karvonen
	private CardioZonesFragment frCardioZones;
	private int userAge =0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_heart_rate);
		//DataStore ds = new DataStore(this);
		User user = User.getUser();
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("calculator"))
			{
				int calc = savedInstanceState.getInt("calculator", 1);
				calc_type = Common.Calculator.getCalcFromId(calc);
				if(calc_type == Common.Calculator.Unknown) calc_type = Common.Calculator.General;
			}
			if(calc_type == Common.Calculator.Karvonen)
			{
				showRestHRInput(true, user.getRestHR());
			}
			else{
				showRestHRInput(false, -1);
			}
			
			if(savedInstanceState.containsKey("age"))
			{
				String val = savedInstanceState.getString("age");
				EditText txt = (EditText)this.findViewById(R.id.hr_age);
				txt.setText(val);	
								
				try
				{
					userAge = Integer.parseInt(val);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					userAge=0;
				}
//				if(userAge > 0)
//				{
//					doCalculate();
//				}
//				else setZonesVisible(false);
			}
		}
		else
		{
			Intent intent = this.getIntent();
			if(intent.hasExtra("calculator"))
			{
				int calc = intent.getIntExtra("calculator", 1);
				calc_type = Common.Calculator.getCalcFromId(calc);
				if(calc_type == Common.Calculator.Unknown) calc_type = Common.Calculator.General;
			}
			
			if(calc_type == Common.Calculator.Karvonen)
			{
				showRestHRInput(true, user.getRestHR());
			}
			else showRestHRInput(false, -1);
			
			//age
			userAge = user.getAge();
			if(userAge > 0)
			{
				EditText txt = (EditText)this.findViewById(R.id.hr_age);
				txt.setText(String.format("%d", userAge));
				
				if(calc_type == Common.Calculator.Karvonen)
				{
					final int userHR = user.getRestHR();
					if(userHR>0)
					{
						txt = (EditText)this.findViewById(R.id.hr_rest_hr);
						txt.setText(String.format("%d", userHR));
					}
				
				}
				
				
				
//				if(calc_type == Common.Calculator.General)
//				{
//					showZones(userAge, -1);
//				}
//				else
//				{
//					final int userHR = user.getRestHR();
//					if(userHR >0) showZones(userAge, userHR);
//					else setZonesVisible(false);
//				}				
			}
			else
			{
				setZonesVisible(false);
			}
		}
		
		setTitles();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		EditText txt = (EditText)this.findViewById(R.id.hr_age);
		String val = txt.getText().toString();
		if(val != null && val.length() > 0)
		{
			outState.putString("age", val);
		}
		outState.putInt("calculator", Common.Calculator.getId(calc_type));
		super.onSaveInstanceState(outState);
	}
	
	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.hr_but_close:
			finish();	
			break;	
		case R.id.hr_but_calculate:
			doCalculate(true);
			break;
		default:
			super.onButtonClick(view);
			break;
		}
				
	}
	
	
	private boolean doCalculate(final boolean showErr)
	{	
		
		//close kbrd
		InputMethodManager inputManager = (InputMethodManager)            
		this.getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
		InputMethodManager.HIDE_NOT_ALWAYS);
		
		int age=getIntValFromEdit(R.id.hr_age);
		if(age <=0)
		{
			if(showErr) showErrorDlg(R.string.err_input_age, Common.REASON_NOT_IMPORTANT);			
			return false;
		}		
		
		//Karvonen
		if(calc_type == Common.Calculator.Karvonen)
		{
			int hrVal = getIntValFromEdit(R.id.hr_rest_hr);			
			if(hrVal <= 0)
			{
				if(showErr) this.showErrorDlg(R.string.err_input_hr, Common.REASON_NOT_IMPORTANT);				
				return false;
			}
//			DataStore ds = new DataStore(this);
//			ds.setRestHR(hrVal);
//			User user = User.getUser();
//			user.setRestHR(hrVal);
			showZones(age, hrVal);
		}
		else showZones(age, -1);
		
		return true;
		
	}
	
	
	
	// pre - age is valid
	private void showZones(final int ageVal, final int hrRest)
	{		
		if(frCardioZones == null) return;
		frCardioZones.showZones(ageVal, hrRest, this.calc_type);
		
//		if(calc_type == Common.Calculator.Karvonen && hrRest <= 0)
//		{
//			showErrorDlg(R.string.err_input_hr, false);			
//			return;
//		}
//		
//		int age;
//		if(ageVal <15)
//		{
//			age = 15;			
//		}
//		else if(ageVal >80) age = 80;
//		else age = ageVal;
//		
//		float maxHR= 220 - age;	
//		
//		//Karvonen THR = ((HRmax − HRrest) × % intensity) + HRrest
//		
//		//estimated 50-85%
//		TextView tv = (TextView)this.findViewById(R.id.hr_msg);
//		String str = String.format(Locale.US, "Your estimated target heart rate zone %d - %d beats per minute", 
//				(int)(maxHR*0.5), (int)(maxHR*0.85));
//		
//		tv.setText(str);
//		tv.setVisibility(View.VISIBLE);
//		
//		setZoneText(R.id.hr_maxZone, 0.9f, 1f, maxHR);
//		setZoneText(R.id.hr_hardcoreZone, 0.8f, 0.9f, maxHR);
//		setZoneText(R.id.hr_endurZone, 0.7f, 0.8f, maxHR);
//		setZoneText(R.id.hr_fitnessZone, 0.6f, 0.7f, maxHR);
//		setZoneText(R.id.hr_warmupZone, 0.5f, 0.6f, maxHR);
//		
//		setZonesVisible(true);		
	}
	
	private void showRestHRInput(final boolean show, final int val)
	{
		EditText view = (EditText)this.findViewById(R.id.hr_rest_hr);
		View viewLbl = findViewById(R.id.hr_rest_hr_title);
		
		if(show)
		{
			if(view != null){
				view.setVisibility(View.VISIBLE);
				if(val >0)
				{
					view.setText(Integer.toString(val));
				}
				else view.setText("");
			}
			if(viewLbl != null) viewLbl.setVisibility(View.VISIBLE);
		}
		else
		{
			if(view != null) view.setVisibility(View.GONE);
			if(viewLbl != null) viewLbl.setVisibility(View.GONE);
		}
	}
	
//	private void setZoneText(final int viewId, final float from, final float to, final float maxHR)
//	{
//		TextView tv = (TextView)this.findViewById(viewId);
//		
//		String tvStr = String.format("%d - %d", (int)(maxHR*from), (int)(maxHR*to));		
//		tv.setText(tvStr);
//		//tv.setVisibility(View.VISIBLE);
//	}
	
	
	
	private void setZonesVisible(final boolean show)
	{
		if(frCardioZones == null) return;
		frCardioZones.setZonesVisible(show);
		
//		View view = this.findViewById(R.id.hr_container_zones);
//		if(view == null) return;
//		if(show) view.setVisibility(View.VISIBLE);
//		else view.setVisibility(View.GONE);
	}
	
	private void setTitles()
	{
		TextView tvTitle = (TextView)findViewById(R.id.hr_title);
		TextView tvSubTitle = (TextView)findViewById(R.id.hr_subtitle);
		if(calc_type== Common.Calculator.General)
		{
			tvTitle.setText(R.string.hr_title);
			tvSubTitle.setText(R.string.hr_subtitle);			
		}
		else
		{
			tvTitle.setText(R.string.hr_karvonen_title);
			tvSubTitle.setText(R.string.hr_karvonen_subtitle);		
		}
	}

	@Override
	public int getId() {
		return Common.ACT_HrCalculatorActivity;
	}

	@Override
	public void onCardioZonesActivityAttached() {
		frCardioZones = (CardioZonesFragment)this.getSupportFragmentManager().findFragmentById(R.id.frag_cardio_zones);
		if(frCardioZones == null) return;				
		if(calc_type == Common.Calculator.General)
		{
			if(userAge > 0)
			{
				showZones(userAge, -1);
			}
			else setZonesVisible(false);
		}
		else
		{
			User user = User.getUser();
			final int userHR = user.getRestHR();
			if(userHR >0) showZones(userAge, userHR);
			else setZonesVisible(false);
		}		
	}
	
	public void onZoneClick(View view){}

	@Override
	public void onCardioZoneClick(String txt) {
		// TODO Auto-generated method stub
		
	}

}
