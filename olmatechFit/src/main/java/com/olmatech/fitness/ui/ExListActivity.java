package com.olmatech.fitness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.interfaces.IProcessLoaded;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.view.ExListFragment;

//Dialog activity showing list of exercises
public class ExListActivity extends BaseFragmentActivity implements IProcessLoaded{
	
	final String TAG ="ExListActivity";  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ex_list);
		initActivity();
	}

	@Override
	public void onFragmentLoadFinished(int id) {
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showProgress(false);					
			}				
		});			
		
	}

	private	void initActivity() {
		Intent intent = this.getIntent();
		Bundle b = intent.getExtras();
		if(b == null || !b.containsKey("progr_id") || !b.containsKey("wk_id") || !b.containsKey("week_index") || 
				! b.containsKey("day_index") )
		{
			showErrLoading();
			return;
		}
		//get data - progrid, workout id, week index, dayindex
		final int progrId = b.getInt("progr_id");
		final int wk_id = b.getInt("wk_id");
		final int week_index= b.getInt("week_index");
		final int day_index= b.getInt("day_index");
		final int mode_id = b.getInt("mode");
		
		WorkoutMode md = WorkoutMode.getModeFromId(mode_id);
		
		if(b.containsKey("title"))
		{
			String s = b.getString("title");
			TextView tv = (TextView)this.findViewById(R.id.txtTitle);
			tv.setText(s);
		}
		
		ExListFragment fragment = (ExListFragment) getSupportFragmentManager().findFragmentById(R.id.exListFragment);	
		View butClose = this.findViewById(R.id.butExListClose);
		butClose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				close();				
			}
			
		});
		
		fragment.setDoItemClick(false);
		fragment.setLoaderId(Common.LOADER_EX_LIST_DLG);
		fragment.setFragmentId(Common.ID_EX_DLG_FRAGMENT);
		fragment.setShowContentMenu(false);
		//fragment.setImageDirectory(progr.getProgrDir());
		//fragment.setUserGender(progr.getGender());
		fragment.setListDayInfo(progrId, wk_id, -1, week_index, day_index, md);
		fragment.fillData();
		
	}
	
	private void showProgress(final boolean show)
	{
		View progrView = this.findViewById(R.id.progrBar);
		if(progrView != null)
		{
			if(show) progrView.setVisibility(View.VISIBLE);
			else progrView.setVisibility(View.GONE);
		}
	}
	
	private void showErrLoading()
	{
		showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);		
	}
	
	private void close()
	{
		finish();
	}
	
	

	

	@Override
	public int getId() {		
		return Common.ACT_ExListActivity;
	}	

	@Override
	public void onExerciseListItemClick(Object tag, int gragmentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListSelectedItemIdSet(int selecetdId, int gragmentId) {
		//do nothing
		
	}

	@Override
	public void onStartButtonClick(int gragmentId, final Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onViewButtonClick(int gragmentId, Object data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetComplete(Object tag, boolean isComplete, 
			int gragmentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processShowDayWorkout(int workoutId, int weekId, int dayId,
			String title, WorkoutMode md) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processDoDayWorkout(int workoutId, String title, WorkoutMode md) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectedItem(int selItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetDayCurrent(DayInfo dayInfo) {
		// TODO Auto-generated method stub
		
	}

}
