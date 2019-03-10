package com.olmatech.fitness.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.olmatech.fitness.R;

public class AppTextViewSizable extends TextView{
	public AppTextViewSizable(Context context) {
		super(context);
		if(!isInEditMode()) setAppearance(context, null);
	}
	
	public AppTextViewSizable(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) setAppearance(context,attrs);
	}
	
	public AppTextViewSizable(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) setAppearance(context, attrs);
	}

	private void setAppearance(Context context, AttributeSet attrs)
	{
		if(attrs == null) return;
		
		TypedArray a = context.obtainStyledAttributes(attrs,
			    R.styleable.AppTextViewSizable);		
		final int N = a.getIndexCount();
		int tabSize=-1, phoneSize=-1;
		for (int i = 0; i < N; ++i)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.AppTextViewSizable_tabTextSize:
				String txt = a.getString(attr);
				if(txt != null){
					try
					{
						tabSize= Integer.parseInt(txt);
					}
					catch(Exception ex)
					{
						tabSize=-1;
					}
				}				
				break;
			case R.styleable.AppTextViewSizable_phoneTextSize:
				String txt2 = a.getString(attr);
				if(txt2 != null){
					try
					{
						phoneSize= Integer.parseInt(txt2);
					}
					catch(Exception ex)
					{
						phoneSize=-1;
					}
				}				
				break;
			}
		}		
		a.recycle();
		if(tabSize <=0 || phoneSize <=0) return;
		
		WidgetsCommon.setTextSize(this, tabSize, phoneSize);		
	}

}
