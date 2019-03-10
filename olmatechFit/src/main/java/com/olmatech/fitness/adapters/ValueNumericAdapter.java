package com.olmatech.fitness.adapters;

import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
* Adapter for numeric wheels. Highlights the current value.
*/
	public class ValueNumericAdapter extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;
		
		//private final static int TEXT_HLIGHT_COLOR = 0xFF0000F0;

		/**
		 * Constructor
		 */
		public ValueNumericAdapter(Context context, int minValue, int maxValue, int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(24);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
//			if (currentItem == currentValue) {
//				view.setTextColor(TEXT_HLIGHT_COLOR);
//			}
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}