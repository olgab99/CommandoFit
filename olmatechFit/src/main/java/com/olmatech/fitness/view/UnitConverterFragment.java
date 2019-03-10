package com.olmatech.fitness.view;

import java.util.Locale;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.olmatech.fitness.R;

public class UnitConverterFragment extends BaseFragment{
	
	private String title;
	//private String subtitle;
	private String radio1Title;
	private String radio2Title;
	
	private RadioButton rb1;
	
	private String radio1ResultUnits;
	private String radio2ResultUnits;
	
	private EditText editFrom;
	private TextView txtTo;
	
	private float coeff1;
	private float coeff2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wt_converter, container, false);	
		
		TextView tv;
		if(title!=null){
			tv= (TextView)view.findViewById(R.id.txt_title);
			tv.setText(title);
		}		
		
		rb1 =(RadioButton)view.findViewById(R.id.radio1);
		if(radio1Title!=null){
			
			rb1.setText(radio1Title);
		}
		final RadioButton rb2 =(RadioButton)view.findViewById(R.id.radio2);
		if(radio2Title!=null){
			
			rb2.setText(radio2Title);
		}
		
		editFrom = (EditText)view.findViewById(R.id.edit_input);
		txtTo = (TextView)view.findViewById(R.id.txt_result);
		
		if(savedInstanceState!= null)
		{
			if(savedInstanceState.containsKey("edit")){
				editFrom.setText(savedInstanceState.getString("edit"));
			}
			if(savedInstanceState.containsKey("radio")){
				boolean val = savedInstanceState.getBoolean("radio");
				if(val){
					rb1.setChecked(true);
				}
				else{
					rb2.setChecked(true);
				}
			}
			else{
				rb1.setChecked(true);
			}
		}
		else{
			rb1.setChecked(true);
		}
		
//		rb1.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				
//				
//			}
//			
//		});
//		
		
		Button calcBut = (Button)view.findViewById(R.id.but_calc);
		calcBut.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Editable ed = editFrom.getEditableText();
				if(ed==null || ed.length()==0) return;
				float from;
				float res;
				String txt = ed.toString().trim();
				String unt;
				if(txt.length()==0) return;
				try{
					from = Float.parseFloat(txt);
				}
				catch(Exception e){
					e.printStackTrace();
					return;
				}
				if(rb1.isChecked()){
					if(coeff1<0.01) return;
					res = from*coeff1;
					unt=radio1ResultUnits;
					
				}
				else{
					if(coeff2<0.01) return;
					res = from*coeff2;
					unt=radio2ResultUnits;
				}
				String txtRes = String.format(Locale.US, "%.2f", res) +((unt!=null)? unt : "");
				txtTo.setText(txtRes);
			}
			
		});
		return view;
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(rb1!= null) outState.putBoolean("radio", rb1.isSelected());
		if(editFrom != null && editFrom.getEditableText()!=null) outState.putString("edit", editFrom.getEditableText().toString());
		if(txtTo != null && txtTo.getText()!= null) outState.putString("txt", (String) txtTo.getText());	
		super.onSaveInstanceState(outState);
	}



	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		
		super.onInflate(activity, attrs, savedInstanceState);
		TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.UnitConverterFragment);
        if (a != null) {
             // this is where we can get out attributes.
                // we can "parse" them into class fields
               
        	title= a.getString(R.styleable.UnitConverterFragment_title);
        	//subtitle=a.getString(R.styleable.UnitConverterFragment_subtitle);
        	radio1Title= a.getString(R.styleable.UnitConverterFragment_radio1);
        	radio2Title= a.getString(R.styleable.UnitConverterFragment_radio2);
        	coeff1=a.getFloat(R.styleable.UnitConverterFragment_coeff1, (float) 0.0);
        	coeff2=a.getFloat(R.styleable.UnitConverterFragment_coeff2, (float) 0.0);
        	radio1ResultUnits= a.getString(R.styleable.UnitConverterFragment_radio1ResUnits);
        	radio2ResultUnits= a.getString(R.styleable.UnitConverterFragment_radio2ResUnits);
        	a.recycle();
        	//isRetainInstance = typedArray.getBoolean(R.styleable.SpinnerFragment_retain, true);
        	//Log.d(TAG, "onInflate isRetainInstance=" + isRetainInstance + "  fragmentTag="+fragmentTag);
              
       }
	}
	
	

}
