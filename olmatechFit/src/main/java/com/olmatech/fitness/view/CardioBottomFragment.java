package com.olmatech.fitness.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.olmatech.fitness.R;
import com.olmatech.fitness.controls.ImageViewCardioEquipment;
import com.olmatech.fitness.controls.NumberPickerTwo;

public class CardioBottomFragment extends BaseFragment{
	//private final static String TAG = "CardioBottomFragment";
	
	private ICardioBottomListener mListener;
	
	//distance picker
		private NumberPickerTwo milesPicker;
		private NumberPickerTwo fractionPicker;  //HERE
		//cardio equipment
		private final static int NUM_OPTIONS =6;
		private ImageViewCardioEquipment[] vEquipment = new ImageViewCardioEquipment[NUM_OPTIONS]; 
		
		private View saveBut;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = getLayoutView(inflater, container, R.layout.fragment_cardio_bottom, R.layout.fragment_cardio_bottom_tab);
			milesPicker = (NumberPickerTwo)view.findViewById(R.id.milePicker);
			fractionPicker = (NumberPickerTwo)view.findViewById(R.id.fractionPicker);  //HERE
			
			milesPicker.setRange(0, 50);/////////////////
			
			String[] vals = new String[20];
			 float val = 0;
			 for(int i=0; i < 20; i++)
			 {
				 vals[i] = String.format("%.2f", val);
				 vals[i] = vals[i].replace("0.", ".");
				 val += 0.05;
			 }
			 
			 fractionPicker.setRange(0, 20, vals);	//HERE 
			 
			 //do not show kboard		
//			 fractionPicker.setAllowKBoardInput(false);  //HERE
//			 milesPicker.setAllowKBoardInput(false);
			 
			 //equipment
			 vEquipment[0] = (ImageViewCardioEquipment)view.findViewById(R.id.cardio1);			 
			 vEquipment[1] = (ImageViewCardioEquipment)view.findViewById(R.id.cardio2);
			 vEquipment[2] = (ImageViewCardioEquipment)view.findViewById(R.id.cardio3);
			 vEquipment[3] = (ImageViewCardioEquipment)view.findViewById(R.id.cardio4);
			 vEquipment[4] = (ImageViewCardioEquipment)view.findViewById(R.id.cardio5);
			 vEquipment[5] = (ImageViewCardioEquipment)view.findViewById(R.id.cardio6);
			 
			 setSelected(-1);
			 
			 for(int i=0; i < NUM_OPTIONS; i++)
			 {
				 vEquipment[i].setId(i);
				 vEquipment[i].setOnTouchListener(new OnTouchListener(){

						@Override
						public boolean onTouch(View v, MotionEvent e) {
							if(e.getAction() != MotionEvent.ACTION_UP) return true;	
							//setselected
							ImageViewCardioEquipment but =(ImageViewCardioEquipment)v;
							setSelected(but.getId());
							return true;						
						}				
					});
			 }
			 
			 Button butShowZones = (Button)view.findViewById(R.id.but_show_zones);
			 
			 butShowZones.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					if(mListener != null)
					{
						mListener.onShowCardioZonesClick();
					}					
				}
				 
			 });
			 
			//save button
			saveBut = view.findViewById(R.id.but_save);
			if(saveBut != null)
			{
				saveBut.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_DOWN)
						{
							if(mListener != null)
							{
								//get time and optional distance			
								mListener.onCardioSaveClick();
							}
							return true;
						}
						return false;
					}					
				});
				
				
			}
			
			//current values
			if(savedInstanceState != null)
			{
				
				if(savedInstanceState.containsKey("distance"))
				{
					setDistance(savedInstanceState.getInt("distance"));
				}
				
				if(saveBut != null)
				{
					if(savedInstanceState.containsKey("save_but"))
					{
						if(savedInstanceState.getBoolean("save_but"))
						{
							saveBut.setVisibility(View.VISIBLE);
							saveBut.setEnabled(true);
						}
						else
						{
							saveBut.setVisibility(View.INVISIBLE);
							saveBut.setEnabled(false);
						}
					}
					else
					{
						//just in case
						saveBut.setVisibility(View.VISIBLE);
						saveBut.setEnabled(true);
					}
				}
				if(savedInstanceState.containsKey("sel_cardio"))
				{
					setSelected(savedInstanceState.getInt("sel_cardio"));
				}
				
			}
			else  //init values
			{
//				if(milesPicker != null) milesPicker.setCurrent(0); 		
//				if(fractionPicker != null) fractionPicker.setCurrent(0);
				if(saveBut != null)
				{
					saveBut.setVisibility(View.INVISIBLE);
					saveBut.setEnabled(false);
				}				
			}			
			 
			return view;
		}
		
		
		
				
		@Override
		public void onSaveInstanceState(Bundle outState) {
			if(saveBut != null)
			{
				outState.putBoolean("save_but", (saveBut.getVisibility()== View.VISIBLE)? true : false);
			}
			
			int dist = getDistance();
			outState.putInt("distance", dist);		
			
			if(vEquipment != null)
			{				
				for(int i =0; i < NUM_OPTIONS; i++)
				{
					if(vEquipment[i].isSelected())
					{
						outState.putInt("sel_cardio", i);
						break;
					}
				}
			}
			
			super.onSaveInstanceState(outState);
		}




		@Override
		public void onAttach(Activity activity) {			
			super.onAttach(activity);
			try {
				mListener = (ICardioBottomListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement ICardioBottomListener");
	        }
		}

		public void setSaveButtonVisible(final boolean show)
		{
			if(saveBut == null) return;
			final Activity act = this.getActivity();
			if(act == null) return;
			
			act.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					if(show)
					{
						if(saveBut.getVisibility() != View.VISIBLE) saveBut.setVisibility(View.VISIBLE);
						saveBut.setEnabled(show);
						
						
						
					}
					else
					{
						if(saveBut.getVisibility() == View.VISIBLE) saveBut.setVisibility(View.INVISIBLE);
						saveBut.setEnabled(show);
					}
					
					
				}
				
			});
		}

		//dist = miles*1000 + frac * 1000
		public void setDistance(final int dist)
		{
			if(dist <=0)
			{
				milesPicker.setCurrent(0);
				fractionPicker.setCurrent(0);  
			}
			else
			{
				int d = dist / 1000;//miles			
				int fr = (dist -d*1000)/50;				
				
				fractionPicker.setCurrent(fr);
				milesPicker.setCurrent(d);
				
			}
		}
		
		public int getDistance()
		{
			int m = milesPicker.getCurrent()*1000; //if km -> m
        	int frac = fractionPicker.getCurrent()* 50;  
        	return m + frac;
		}
		
		//Equipment buttons
		
		public int getSelEquipmentIndex()
		{
			for(int i=0; i < NUM_OPTIONS; i++)
			{
				if( vEquipment[i].isSelected()) return i;
			}
			return -1;
		}
		
		public void setSelectedEquipment(final int ind)
		{
			if(ind >=0 && ind < NUM_OPTIONS)
			{
				setSelected(ind);
			}
			else
			{
				//all not selected
				setSelected(-1);
			}
		}
		
		//equipment buttons manipulation		
		private void setSelected(final int selInd)
		{
			 for(int i=0; i < NUM_OPTIONS; i++)
			 {
				 if(i == selInd)
				 {
					 vEquipment[i].setButBackgroundOn();
				 }
				 else
				 {
					 vEquipment[i].setButBackground();
				 }
			 }
			 if(this.mListener != null)
			 {
				 mListener.onCardioEquipmentSelected(selInd);
			 }
		}
		
		public interface ICardioBottomListener
		{
			public void onShowCardioZonesClick();
			public void onCardioEquipmentSelected(final int selInd);
			public void onCardioSaveClick();
			
		}
		

}
