package com.olmatech.fitness.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.interfaces.IHaveId;
import com.olmatech.fitness.main.Common;

public class PopupDlgActivity extends Activity implements IHaveId{
	
	private int numButtons=1;
	private int dlgType;
	private int dlgReason= Common.REASON_NOT_IMPORTANT; //set by caller

	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.dlg_popup);
			TextView tv = (TextView)findViewById(R.id.txt_title);
			Button butone = (Button)findViewById(R.id.but_one);
			Button buttwo = (Button)findViewById(R.id.but_two);
			Button butthree = (Button)findViewById(R.id.but_three);			
					
			if(savedInstanceState != null)
			{
				tv.setText(savedInstanceState.getString("msg"));
				numButtons = savedInstanceState.getInt("buttons");
				if(numButtons ==1)
				{					
					butone.setText(savedInstanceState.getString("okbut"));
					buttwo.setVisibility(View.GONE);
					butthree.setVisibility(View.GONE);
				}
				else 
				{					
					butone.setText(savedInstanceState.getString("okbut"));
					buttwo.setText(savedInstanceState.getString("nobut"));	
					if(numButtons==3)
					{
						butthree.setText(savedInstanceState.getString("cancelbut"));
					}
					else{
						butthree.setVisibility(View.GONE);
					}
				}
				
				dlgType = savedInstanceState.getInt("dlgType");
				dlgReason = savedInstanceState.getInt("dlg_reason");
			}
			else
			{
				Intent intent = getIntent();
				dlgType = intent.getIntExtra("dlg_type", -1);
				dlgReason = intent.getIntExtra("dlg_reason", Common.REASON_NOT_IMPORTANT);
				if(dlgType <0)
				{
					processOnClick(dlgType);
					return;
				}
				int msg = intent.getIntExtra("msg", -1);
				if(msg >0) tv.setText(msg);	
				numButtons = intent.getIntExtra("buttons", 1);	
				if(numButtons ==1)
				{
					msg = intent.getIntExtra("okbut", -1);
					if(msg >0)  butone.setText(msg);
				}
				else
				{
					msg = intent.getIntExtra("okbut", -1);
					if(msg >0)  butone.setText(msg);
					msg = intent.getIntExtra("nobut", -1);
					if(msg >0)  buttwo.setText(msg);
					if(numButtons==3)
					{
						msg = intent.getIntExtra("cancelbut", -1);
						if(msg >0)  butthree.setText(msg);
					}
				}
				
			}
			if(numButtons ==1)
			{
				buttwo.setVisibility(View.GONE);
				butthree.setVisibility(View.GONE);	
				
				buttwo.setEnabled(false);
				butthree.setEnabled(false);				
			}			
			else
			{					
								
				if(numButtons==2)
				{
					butthree.setVisibility(View.GONE);	
					butthree.setEnabled(false);		
				}				
			}			
	}
	
	public void onButtonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.but_one:
			processOnClick(Common.RESULT_OK);		
			break;
		case R.id.but_two:
			processOnClick(Common.RESULT_CANCEL);	
			break;
		case R.id.but_three:
			processOnClick(Common.RESULT_DONOTHING);
			break;
		default: break;
		}
	}
	
	private void processOnClick(final int res)
	{
		Intent data = this.getIntent();
		data.putExtra("dlg_type", dlgType);
		data.putExtra("dlg_reason", dlgReason);
		this.setResult(res, data);
		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("buttons", numButtons);
		TextView tv = (TextView)findViewById(R.id.txt_title);
		outState.putString("msg", tv.getText().toString());
		if(numButtons == 1)
		{
			Button onebut = (Button)findViewById(R.id.but_one);
			outState.putString("okbut", onebut.getText().toString());
		}
		else
		{
			Button yesbut = (Button)findViewById(R.id.but_one);
			Button nobut = (Button)findViewById(R.id.but_two);
			outState.putString("okbut", yesbut.getText().toString());
			outState.putString("nobut", nobut.getText().toString());
			if(numButtons == 3)
			{
				Button cancelbut = (Button)findViewById(R.id.but_three);
				outState.putString("cancelbut", cancelbut.getText().toString());
			}
		}
		outState.putInt("dlgType", dlgType);
		outState.putInt("dlg_reason", dlgReason);
		super.onSaveInstanceState(outState);
	}

	@Override
	public int getId() {
		return Common.ACT_PopupDlgActivity;
	}

	@Override
	public void onBackPressed() {
		if(numButtons == 3)
		{
			processOnClick(Common.RESULT_DONOTHING);
		}
	}
	
	

}
