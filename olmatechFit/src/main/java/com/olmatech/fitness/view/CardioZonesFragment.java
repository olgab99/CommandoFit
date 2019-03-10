package com.olmatech.fitness.view;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

//bottom of cardio screen and bot. of Cardio Calculator screen
public class CardioZonesFragment  extends BaseFragment{
	
	private TextView[] tvZones = new TextView[5];
	private TextView tvMsg;
	
	private ICardioZonesListener mListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cardio_zones, container, false);
		
		tvZones[0] = (TextView)view.findViewById(R.id.hr_maxZone);
		tvZones[1] = (TextView)view.findViewById(R.id.hr_hardcoreZone);
		tvZones[2] = (TextView)view.findViewById(R.id.hr_endurZone);
		tvZones[3] = (TextView)view.findViewById(R.id.hr_fitnessZone);
		tvZones[4] = (TextView)view.findViewById(R.id.hr_warmupZone);
		
		TextView[] tvZonesTitle = new TextView[5];
		tvZonesTitle[0] = (TextView)view.findViewById(R.id.hr_maxZoneTitle);
		tvZonesTitle[1] = (TextView)view.findViewById(R.id.hr_hardcoreZoneTitle);
		tvZonesTitle[2] = (TextView)view.findViewById(R.id.hr_endurZoneTitle);
		tvZonesTitle[3] = (TextView)view.findViewById(R.id.hr_fitnessZoneTitle);
		tvZonesTitle[4] = (TextView)view.findViewById(R.id.hr_warmupZoneTitle);
		
		for(int i=0; i < 5; i++)
		{
			setZoneOnClick(tvZonesTitle[i], i);
			setZoneOnClick(tvZones[i], i);
		}
		
		View zoneCont = view.findViewById(R.id.hr_container_zones);
		if(zoneCont != null) zoneCont.setVisibility(View.GONE);
		
		tvMsg = (TextView)view.findViewById(R.id.hr_msg);
		return view;
	}
	
	private void setZoneOnClick(TextView tv, final int ind)
	{
		tv.setClickable(true);
		tv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mListener != null)
				{					
					String s = (String) tvZones[ind].getText();
					mListener.onCardioZoneClick(s);
				}				
			}			
		});
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (ICardioZonesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ICardioZonesListener");
        }
		if(mListener != null)
		{
			mListener.onCardioZonesActivityAttached();
		}
	}



	public void showDefaultImage(final boolean show)
	{
		Activity act = this.getActivity();
		if(act == null) return;
		View imgView = act.findViewById(R.id.img_zones_default);
		if(imgView == null) return;
		if(show){
			imgView.setVisibility(View.VISIBLE);
			View zoneCont = act.findViewById(R.id.hr_container_zones);
			if(zoneCont != null) zoneCont.setVisibility(View.GONE);
		}
		else {
			imgView.setVisibility(View.GONE);
		}
	}
	
	public void showZones(final int ageVal, final int hrRest, final Common.Calculator calcType)
	{
		int age;
		if(ageVal <15)
		{
			age = 15;			
		}
		else if(ageVal >80) age = 80;
		else age = ageVal;
		
		int maxHR= 220 - age;	
		int val_90 = Common.getCardioZoneHR(maxHR, hrRest, 0.9, calcType);
		int val_100 = Common.getCardioZoneHR(maxHR, hrRest, 1.0, calcType);	
		int val_80 = Common.getCardioZoneHR(maxHR, hrRest, 0.8, calcType);	
		int val_70= Common.getCardioZoneHR(maxHR, hrRest, 0.7, calcType);	
		int val_60=Common.getCardioZoneHR(maxHR, hrRest, 0.6, calcType);
		int val_50=Common.getCardioZoneHR(maxHR, hrRest, 0.5, calcType);	
		
		int val_85 = Common.getCardioZoneHR(maxHR, hrRest, 0.85, calcType);
		
		//estimated 50-85%		
		String str = String.format(Locale.US, "Your estimated target heart rate zone %d - %d beats per minute", 
				val_50, val_85);
		
		tvMsg.setText(str);
		tvMsg.setVisibility(View.VISIBLE);		
		
		setZoneText(0, val_90, val_100);
		setZoneText(1, val_80, val_90);
		setZoneText(2, val_70, val_80);
		setZoneText(3, val_60, val_70);
		setZoneText(4, val_50, val_60);		
		setZonesVisible(true);
	}
	
	private void setZoneText(final int index, final int from, final int to)
	{			
		String tvStr = String.format(Locale.US, "%d - %d", from, to);		
		tvZones[index].setText(tvStr);
		//tv.setVisibility(View.VISIBLE);
	}
	
	public void setZonesVisible(final boolean show)
	{
		Activity act = this.getActivity();
		if(act == null) return;
		View view = act.findViewById(R.id.hr_container_zones);
		if(view == null) return;
		if(show) view.setVisibility(View.VISIBLE);
		else view.setVisibility(View.GONE);
	}
	
	//////////// Interface
	
	public interface ICardioZonesListener
	{
		public void onCardioZonesActivityAttached();
		public void onCardioZoneClick(final String txt);
	}

}
