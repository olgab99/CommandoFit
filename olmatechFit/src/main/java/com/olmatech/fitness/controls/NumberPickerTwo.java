package com.olmatech.fitness.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

public class NumberPickerTwo extends LinearLayout{
	
//	private final static String TAG = "NumberPickerTwo";	
	
	private static final int DEFAULT_MAX = 200;
	private static final int DEFAULT_MIN = 0;	
	
	private TextView mText;
	private Button mIncrementButton;
	private Button mDecrementButton;
	private String[] mDisplayedValues;
	protected int mStart;
	protected int mEnd;
	protected int mCurrent=0;
	private Formatter mFormatter;
	
	public interface Formatter
	{
		String toString(int value);
	}
	
	public static final NumberPickerTwo.Formatter TWO_DIGIT_FORMATTER = new NumberPickerTwo.Formatter()
	{
		final StringBuilder mBuilder = new StringBuilder();
		final java.util.Formatter mFmt = new java.util.Formatter(mBuilder);
		final Object[] mArgs = new Object[1];

		public String toString(int value)
		{
			mArgs[0] = value;
			mBuilder.delete(0, mBuilder.length());
			mFmt.format("%02d", mArgs);
			return mFmt.toString();
		}
	};
	
	public NumberPickerTwo(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
//	public NumberPickerTwo(Context context, AttributeSet attrs) {
//		this(context, attrs,0);
//		// TODO Auto-generated constructor stub
//	}
	
	
	public NumberPickerTwo(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(Common.getIsTablet())
		{
			inflater.inflate(R.layout.number_picker_two_tab, this, true);
		}
		else
		{
			inflater.inflate(R.layout.number_picker_two, this, true);
		}
		if(isInEditMode()) return;
		mText = (TextView)findViewById(R.id.timepicker_input);
		mIncrementButton = (Button)findViewById(R.id.increment);
		mDecrementButton = (Button)findViewById(R.id.decrement);
		
		mIncrementButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				changeCurrent(mCurrent+1);
				
			}			
		});
		
		mDecrementButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				changeCurrent(mCurrent-1);
				
			}			
		});
		
		mStart = DEFAULT_MIN;
		mEnd = DEFAULT_MAX;
	}
	
	private void changeCurrent(final int newCurrent)
	{
		int current = newCurrent;
		// Wrap around the values if we go past the start or end
				if (current > mEnd)
				{
					current = mStart;
				}
				else if (current < mStart)
				{
					current = mEnd;
				}
				
				mCurrent = current;

				updateView();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mIncrementButton.setEnabled(enabled);
		mDecrementButton.setEnabled(enabled);
		mText.setEnabled(enabled);
	}
	
	public void setFormatter(Formatter formatter)
	{
		mFormatter = formatter;
	}
	
	/**
	 * Set the range of numbers allowed for the number picker. The current value
	 * will be automatically set to the start.
	 * 
	 * @param start
	 *            the start of the range (inclusive)
	 * @param end
	 *            the end of the range (inclusive)
	 */
	public void setRange(int start, int end)
	{
		mStart = start;
		mEnd = end;
		mCurrent = start;
		updateView();
	}
	
	/**
	 * Set the range of numbers allowed for the number picker. The current value
	 * will be automatically set to the start. Also provide a mapping for values
	 * used to display to the user.
	 * 
	 * @param start
	 *            the start of the range (inclusive)
	 * @param end
	 *            the end of the range (inclusive)
	 * @param displayedValues
	 *            the values displayed to the user.
	 */
	public void setRange(int start, int end, String[] displayedValues)
	{
		mDisplayedValues = displayedValues;
		mStart = start;
		mEnd = end;
		mCurrent = start;
		updateView();
	}
	
	public void setCurrent(int current)
	{
		mCurrent = current;
		updateView();
	}
	
	/**
	 * @return the current value.
	 */
	public int getCurrent()
	{
		return mCurrent;
	}
	
	private String formatNumber(int value)
	{
		return (mFormatter != null) ? mFormatter.toString(value) : String.valueOf(value);
	}
	
	private void updateView()
	{
		/*
		 * If we don't have displayed values then use the current number else
		 * find the correct value in the displayed values for the current
		 * number.
		 */
		if (mDisplayedValues == null)
		{
			mText.setText(formatNumber(mCurrent));			
		}
		else
		{			
			int ind = mCurrent - mStart;
			if(ind < 0) ind = 0;
			else if(ind >= mDisplayedValues.length) ind = 0;
			mText.setText(mDisplayedValues[ind]); //////////////////
		}
	}

}
