package com.olmatech.fitness.ui;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.ExData;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.Workout;
import com.olmatech.fitness.view.DescriptionFragment;
import com.olmatech.fitness.view.DescriptionFragment.IDescriptionListener;

public class ExImageViewActivity extends BaseFragmentActivity
	implements IDescriptionListener{
	
	final static String TAG ="ExImageViewActivity";
	private int ex_index=-1;
	private String imgName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_ex_pic_view);
		//display img and description for current exercise
		
		if(savedInstanceState!=null)
		{
			if(savedInstanceState.containsKey("ex_index"))
			{
				ex_index = savedInstanceState.getInt("ex_index");				
			}
			if(savedInstanceState.containsKey("img_name"))
			{
				imgName= savedInstanceState.getString("img_name");
			}
		}
		else
		{
			Intent intent= getIntent();
			ex_index = intent.getIntExtra("ex_index", -1);
			imgName = intent.getStringExtra("img_name");
		}
		if(ex_index <0)
		{
			this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
			return;
		}
		CurProgram progr = CurProgram.getProgram();
		Workout workout = progr.getWorkout();
		ExData exData = workout.getExerciseAt(ex_index).getCurExData(User.getUser().getGender());
		
		FragmentManager frManager = this.getSupportFragmentManager();
		DescriptionFragment frDescription = (DescriptionFragment)frManager.findFragmentById(R.id.frag_descrip);
		frDescription.setDescription(exData.getDescription());
		frDescription.setTitle(exData.getTitle());
		frDescription.showCloseButton(false);
		
		ImageView imgEx = (ImageView)this.findViewById(R.id.eximg);
		if(imgName != null)
		{
			Bitmap bm = getBitmapFromAssets(imgName, CurProgram.getProgrDir()+ File.separator);
			if(bm != null)
			{
				imgEx.setImageBitmap(bm);
				imgEx.setTag(Boolean.TRUE);
			}
			else{
				imgEx.setImageResource(R.drawable.generic_ex_img); // TEMP - TODO
				imgEx.setTag(null);
			}
		}
		
	}
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("ex_index", ex_index);
		outState.putString("img_name", imgName);
		super.onSaveInstanceState(outState);
	}



	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.but_close:
			finish();
			break;
		default:
			super.onButtonClick(view);
			break;
		}
		
	}

	@Override
	public int getId() {		
		return Common.ACT_ExImageViewActivity;
	}



	@Override
	public void onDescriptionClose() {
		//nothing		
	}

	@Override
	protected void onStop() {
		recycleBitmapFromImageViewIfTagNotNull(R.id.eximg);
		super.onStop();
	}
	

}
