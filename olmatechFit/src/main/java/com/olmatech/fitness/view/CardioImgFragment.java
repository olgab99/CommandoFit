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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;


//top of cardio screen
public class CardioImgFragment extends BaseTimerFragment{
	private View arr_left;
	private View arr_right;
	private ImageView cardio_img;
	private int cardio_img_index=0;
	private TextView cardio_hr_text;
	
	//listener
	private ICardioListener mListener;	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view;
		
		if(Common.getIsTablet())
		{
			view = inflater.inflate(R.layout.fragment_cardio_img_tab, container, false);
		}
		else
		{
			view = inflater.inflate(R.layout.fragment_cardio_img, container, false);
		}
		
		txtTime = (TextView)view.findViewById(R.id.txtTime);
		progrBar = (ProgressBar)view.findViewById(R.id.progrBarCardio);
		arr_left = view.findViewById(R.id.img_arr_left);
		arr_right = view.findViewById(R.id.img_arr_right);
		
		cardio_img = (ImageView)view.findViewById(R.id.cardio_img);		
		cardio_hr_text = (TextView)view.findViewById(R.id.hr_msg);
		cardio_hr_text.setVisibility(View.GONE);
		
		Button butMinus = (Button)view.findViewById(R.id.butMinus);
		butMinus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				changeTime(false);		
			}
			
		});
		
		Button butPlus = (Button)view.findViewById(R.id.butPlus);
		butPlus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				changeTime(true);		
			}
			
		});
		
		butStart = (Button)view.findViewById(R.id.butStart);
		butStop = (Button)view.findViewById(R.id.butStop);
		
		butStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				doStartButClick();
				
			}
			
		});
		
		butStop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				butTimerStopClick();
			}
			
		});
			
		
		if(arr_left != null)
		{
			arr_left.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					if(e.getAction() != MotionEvent.ACTION_UP) return true;	
					if(mListener != null) {
						mListener.showPrevCardio();
					}
					return true;						
				}				
			});
		}
		
		if(arr_right != null)
		{
			arr_right.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					if(e.getAction() != MotionEvent.ACTION_UP) return true;						
					if(mListener != null){
						mListener.showNextCardio();
					}
					return true;
					
				}				
			});
		}
		
		View viewDescription = view.findViewById(R.id.but_descript_cardio);
		if(viewDescription != null)
		{
			viewDescription.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					if(ev.getAction() != MotionEvent.ACTION_UP) return true;						
					mListener.onDescriptionCardioButClick();
					return true;					
				}
				
			});
		}
		
		//visibility / data
		if(savedInstanceState != null)
		{
			if(butStart != null)
			{
				if(savedInstanceState.containsKey("start_visible"))
				{
					if(savedInstanceState.getBoolean("start_visible"))
					{
						butStart.setVisibility(View.VISIBLE);
					}
					else butStart.setVisibility(View.INVISIBLE);
				}
			}
			if(butStop != null)
			{
				if(savedInstanceState.containsKey("stop_visible"))
				{
					if(savedInstanceState.getBoolean("stop_visible"))
					{
						butStop.setVisibility(View.VISIBLE);
					}
					else butStop.setVisibility(View.INVISIBLE);
				}
			}
			if(savedInstanceState.containsKey("total_time"))
			{
				totalTime = savedInstanceState.getLong("total_time");
			}
			if(savedInstanceState.containsKey("time"))
			{
				time = savedInstanceState.getLong("time");
			}
			if(savedInstanceState.containsKey("progress"))
			{
				progressValue = savedInstanceState.getInt("progress");
			}
			if(savedInstanceState.containsKey("timer_on"))
			{
				isTimerOn=savedInstanceState.getBoolean("timer_on");
			}
			if(savedInstanceState.containsKey("left_arrow") && arr_left != null)
			{
				arr_left.setVisibility(savedInstanceState.getInt("left_arrow"));
			}
			if(savedInstanceState.containsKey("right_arrow") && arr_right != null)
			{
				arr_right.setVisibility(savedInstanceState.getInt("right_arrow"));
			}
			if(savedInstanceState.containsKey("txt_time") && txtTime != null)
			{
				String s = savedInstanceState.getString("txt_time");
				if(s != null) txtTime.setText(s);
			}
			//image / text
			if(savedInstanceState.containsKey("cardio_img_index"))
			{				
				setCadioEquipmentImg(savedInstanceState.getInt("cardio_img_index"));
			}
			if(savedInstanceState.containsKey("cardio_hr_text"))
			{
				setCardioText(savedInstanceState.getString("cardio_hr_text"));
			}
			
		} //savedInstanceState
		else
		{
			//set Start visible, DONE invisible
			if(butStart != null) butStart.setVisibility(View.VISIBLE);
			if(butStop != null) butStop.setVisibility(View.INVISIBLE);
			
		}
		
		return view;
	}
	
	public void doStartButClick()
	{
		butTimerStartClick();
		setStopButVisible(true); 
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		//arrows
		if(arr_left != null)
		{
			outState.putInt("left_arrow", arr_left.getVisibility());
		}
		if(arr_right != null)
		{
			outState.putInt("right_arrow", arr_right.getVisibility());
		}
		//image on top of progress bar
		outState.putInt("cardio_img_index", cardio_img_index);
		if(cardio_hr_text != null && cardio_hr_text.getVisibility() == View.VISIBLE)
		{
			outState.putString("cardio_hr_text", (String) cardio_hr_text.getText());
		}
		
		super.onSaveInstanceState(outState);
	}



	@Override
	protected void resetView()
	{
		this.detachTimer();
		displayTimeOnUI(true);
		setStartButTitle(R.string.start, true);
		//setStopButVisible(false);
	}
	
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
		try {
			mListener = (ICardioListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ICardioListener");
        }
	}


	//////////// TIMER 
	
	@Override
	protected void butTimerStopClick()
	{
		super.butTimerStopClick();	
		setStartButVisible(false);
		if(mListener != null)
		{
			mListener.onCardioDoneClick();
		}
	}
	
	public long getCardioTime()
	{
		return totalTime - time;
	}
	
	
	

	private void changeTime(final boolean add)
	{
		if(add){
			time +=60;
			totalTime += 60;
			showTime();
		}
		else if(time > 60){
			time -=60;
			totalTime -=60;
			showTime();
		}		
	}
	

	public void showArrows(final boolean left, final boolean right)
	{
		if(arr_left != null)
		{
			if(!left)
			{
				//first
				arr_left.setVisibility(View.INVISIBLE);
			}
			else arr_left.setVisibility(View.VISIBLE);
		}		
		
		if(arr_right != null)
		{
			if(!right) arr_right.setVisibility(View.INVISIBLE);	
			else arr_right.setVisibility(View.VISIBLE);	
		}
		
	}
	
	//toggle description close button
		public void toggleDescriptionButton(final boolean showOpenButton)
		{
			Activity act = this.getActivity();
			if(act == null) return;
			ImageView viewClose = (ImageView)act.findViewById(R.id.but_descript_cardio);
			if(viewClose !=null)
			{
				if(showOpenButton)
				{
					viewClose.setImageResource(R.drawable.corner_open);
				}
				else
				{
					viewClose.setImageResource(R.drawable.corner_close);
				}
			}
		}
	
	
		@Override
		protected void onTimeComplete() {
			super.onTimeComplete();
			setStartButVisible(false);
			
		}
		
		//set cardio img
		public void setCadioEquipmentImg(final int ind)
		{
			if(cardio_img != null && ind >= 0 && ind < Common.CARDIO_EQUIPMENT.length)
			{
				cardio_img_index = ind;				
				if(cardio_img.getVisibility() == View.VISIBLE) cardio_img.setImageResource(Common.CARDIO_EQUIPMENT[cardio_img_index]);
			}
		}
		
		public void setCardioText(final String txt)
		{
			if(txt== null)
			{
				//hide text - show icon
				cardio_hr_text.setText("");
				cardio_hr_text.setVisibility(View.GONE);
				cardio_img.setVisibility(View.VISIBLE);
				cardio_img.setImageResource(Common.CARDIO_EQUIPMENT[cardio_img_index]);
			}
			else
			{
				if(!Common.isLargeSize(parentActivity))
				{
					if(cardio_img.getVisibility() == View.VISIBLE) 
					{
						cardio_img.setVisibility(View.GONE);
					}
				}
				
				cardio_hr_text.setText(txt);
				if(cardio_hr_text.getVisibility() != View.VISIBLE) 
				{
					cardio_hr_text.setVisibility(View.VISIBLE);
				}
			}
		}


	///////////// INTERFACE ///////////////////////
	public interface ICardioListener {		
		public void showNextCardio();	
		public void showPrevCardio();
		public void onCardioDoneClick(); 
		public void onDescriptionCardioButClick();
	}

}
