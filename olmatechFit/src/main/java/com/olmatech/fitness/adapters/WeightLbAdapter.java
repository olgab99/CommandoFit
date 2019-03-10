package com.olmatech.fitness.adapters;

import kankan.wheel.widget.adapters.WeightLbWheelAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * Weight - lb
 * 
 */
public class WeightLbAdapter extends WeightLbWheelAdapter {
	// Index of current item
	int currentItem;
	// Index of item to be highlighted
	int currentValue;
	
	private boolean doHighlight = true;
	
	private final static int TEXT_HLIGHT_COLOR = 0xFF0000F0;

	public WeightLbAdapter(Context context, int minValue, int maxValue,
			int cur) {
		super(context, minValue, maxValue);
		this.currentValue = cur;
		setTextSize(24);
	}
	
	public WeightLbAdapter(Context context, int minValue, int maxValue,
			int cur, final boolean doHighlightVal) {
		this(context, minValue, maxValue,cur);
		doHighlight = doHighlightVal;
	}

	@Override
	protected void configureTextView(TextView view) {
		super.configureTextView(view);
		if (doHighlight && currentItem == currentValue) {
			view.setTextColor(TEXT_HLIGHT_COLOR);
		}
		view.setTypeface(Typeface.SANS_SERIF);
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		currentItem = index;
		return super.getItem(index, cachedView, parent);
	}
	
	public void setDoHighlight(final boolean val)
	{
		doHighlight = val;
	}
	
	public void setCurValue(final int cur)
	{
		this.currentValue = cur;
	}
}
