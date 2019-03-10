package com.olmatech.fitness.ui;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.Workout.WorkoutMode;

public class LogViewActivity extends ExpandableListBase 
	implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
	
	private final static String TAG="LogViewActivity";
	
	private LoaderManager mManager;
	private final int LOADER_LOG=-5;
	private final int LOADER_LOG_CARDIO=-6;
	private final static String SQL ="sql";
	private DBAdapter dbAdapter;
	private SimpleCursorTreeAdapter logAdapter;
	
	private SimpleCursorAdapter cardioAdapter;
	
	//data
	private long datetime=-1;
	private int workoutId=-1;
	private int weekId=-1;
	private WorkoutMode selectMode=WorkoutMode.All;  // values can be All, Weight, Tactical
	
	private WorkoutMode dayMode = WorkoutMode.Unknown;
	
	//email data
	private StringBuilder emailBodyStr;
	private StringBuilder attachmentStr;
	private String title;
	private static File TEMP_FILE=null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_log_view);	
		final ExpandableListView list = getExpandableListView();
		if(list != null)
		{
			list.setOnGroupClickListener(new OnGroupClickListener(){

				@Override
				public boolean onGroupClick(ExpandableListView parent, View v,
						int groupPosition, long id) {
					redrawMe();				
					return false;
				}
				
			});
		}
		TextView tvTitle = (TextView)this.findViewById(R.id.log_title);
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("datetime")) datetime=savedInstanceState.getLong("datetime");
			if(savedInstanceState.containsKey("workout_id")) workoutId= savedInstanceState.getInt("workout_id"); 
			if(savedInstanceState.containsKey("week_id")) weekId= savedInstanceState.getInt("week_id");
			if(savedInstanceState.containsKey("title")) tvTitle.setText(savedInstanceState.getString("title"));
			else tvTitle.setText("Exercise Log");
			
			if(savedInstanceState.containsKey("formode")) selectMode =WorkoutMode.getModeFromId(savedInstanceState.getInt("formode"));
			if(savedInstanceState.containsKey("day_mode")) dayMode =WorkoutMode.getModeFromId(savedInstanceState.getInt("day_mode"));
		}
		else
		{
			Intent intent = this.getIntent();
			int year =intent.getIntExtra("year",-1);
			int month = intent.getIntExtra("month", -1);
			int day = intent.getIntExtra("day", -1);
			
			if(year <=0 || month <0 || day <0)
			{
				this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
				return;
			}			
			datetime=Common.getDateTimeForDay(year, month, day);
			workoutId=intent.getIntExtra("workout_id", -1);
			weekId=intent.getIntExtra("week_id", -1);
			
			selectMode = WorkoutMode.getModeFromId(intent.getIntExtra("formode", WorkoutMode.All.getId()));
			dayMode = WorkoutMode.getModeFromId(intent.getIntExtra("day_mode", WorkoutMode.Unknown.getId()));
			
			//int logOrLapsCnt = intent.getIntExtra("log_or_laps_count", 0);
			String modeTitle =  intent.getStringExtra("wk_type");
			
			title = (modeTitle!= null)? modeTitle+ ": " + Common.getShortMonthName(month) + " " + day + ", " + year
					: "Workout log for " + Common.getShortMonthName(month) + " " + day + ", " + year;
			tvTitle.setText(title);
		}
		if(datetime <0)
		{
			this.showErrorDlg(R.string.err_reading_data, Common.REASON_FATAL_ERROR);
		}
		else
		{
			if(selectMode == WorkoutMode.Tactical)
			{
				View header = this.findViewById(R.id.list_header);
				if(header != null)
				{
					header.setVisibility(View.GONE);					
				}
			}
			mManager = this.getSupportLoaderManager();
			dbAdapter = DBAdapter.getAdapter(this.getApplicationContext());
			fillData();
		}
	}
		
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong("datetime", datetime);
		outState.putInt("workout_id", workoutId);
		outState.putInt("week_id", weekId);
		//TextView tvTitle = (TextView)this.findViewById(R.id.log_title);
		outState.putString("title", title);
		outState.putInt("formode", selectMode.getId());
		outState.putInt("day_mode", dayMode.getId());
		super.onSaveInstanceState(outState);
	}

	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.log_but_close:
			finish();
			break;
		case R.id.log_but_email:
			new EmailWorkoutTask().execute();
			break;
		default: break;
		}
		
	}

	private void fillData()
	{
		//final ExpandableListView list = this.getExpandableListView();
		if(mManager.getLoader(LOADER_LOG) != null && !mManager.getLoader(LOADER_LOG).isReset())
		{
			mManager.restartLoader(LOADER_LOG, null, this);
		}
		else
		{
			mManager.initLoader(LOADER_LOG, null, this);
		}
		
		if(selectMode != WorkoutMode.Tactical)
		{
			if(mManager.getLoader(LOADER_LOG_CARDIO) != null && !mManager.getLoader(LOADER_LOG_CARDIO).isReset())
			{
				mManager.restartLoader(LOADER_LOG_CARDIO, null, this);
			}
			else
			{
				mManager.initLoader(LOADER_LOG_CARDIO, null, this);
			}
		}
		
		boolean ok = true;
		try
		{
			if(selectMode == WorkoutMode.Tactical || this.dayMode ==WorkoutMode.Tactical)
			{
				logAdapter = new LogAdapter(this, null, // mGroupCursor,
						R.layout.log_ex_name,
						new String[]{DBAdapter.COL_TITLE},
						new int[]{R.id.ex_name},
						R.layout.log_tacktical_details,				
						new String[]{DBAdapter.COL_TITLE, DBAdapter.COL_LAPS},
						new int[]{R.id.txt_name,R.id.txt_laps});	
				
				//Hide cardio
				hideCardio();
			}
			else  //weigh + cardio, only weight; only cardio; 
			{				
				logAdapter = new LogAdapter(this, null, // mGroupCursor,
						R.layout.log_ex_name,
						new String[]{DBAdapter.COL_TITLE},
						new int[]{R.id.ex_name},
						R.layout.log_ex_details,				
						new String[]{DBAdapter.COL_SET_NUM, DBAdapter.COL_REPS,DBAdapter.COL_WEIGHT,DBAdapter.COL_UNITS},
						new int[]{R.id.txt_set,R.id.txt_reps,R.id.txt_wt,R.id.txt_units});
				
				cardioAdapter = new SimpleCursorAdapter(this, R.layout.log_item_cardio,null,
						new String[]{ DBAdapter.COL_DISTANCE, DBAdapter.COL_UNITS, DBAdapter.COL_EQUIPMENT,DBAdapter.COL_TIME},
						new int[]{R.id.txt_cardio_dist,R.id.txt_cardio_units,R.id.img_cardio,R.id.txt_cardio_time}, 
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				
				ListView cardioList = (ListView)findViewById(R.id.list_log_cardio);
				cardioList.setAdapter(cardioAdapter);							
				
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			ok = false;
		}		
		
		if(ok){
			setListAdapter(logAdapter);
//			final int cnt =logAdapter.getGroupCount();
//			for(int i=0; i < cnt; i++)
//			{
//				list.expandGroup(i);
//			}
		}
		//getExpandableListView().setGroupIndicator(getResources().getDrawable(R.drawable.group_selector));
	}
	
	private void hideCardio()
	{
		View cardioCont = this.findViewById(R.id.cardio_container);
		cardioCont.setVisibility(View.GONE);		
		
		View exerCont = this.findViewById(R.id.exer_container);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
		exerCont.setLayoutParams(param);
		
	}
	
	private void hideExerciseList()
	{
		View exerCont = this.findViewById(R.id.exer_container);
		exerCont.setVisibility(View.GONE);	
		
		View cardioCont = this.findViewById(R.id.cardio_container);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1.0f);
		cardioCont.setLayoutParams(param);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		SQLiteCursorLoader loader = null;
		if(id == LOADER_LOG)
		{
			String sql;
			if(selectMode == WorkoutMode.All)
			{
				if(dayMode == WorkoutMode.Tactical)
				{
					sql=DBAdapter.getTackticalLogSQLForView(workoutId, datetime, CurProgram.getProgram().getId());
				}
				else
				{
					sql= DBAdapter.getLogExerciseNamesSql(datetime,workoutId,weekId);
				}
				
			}
			else if(selectMode == WorkoutMode.Tactical)
			{
				sql=DBAdapter.getTackticalLogSQLForView(-1, datetime,CurProgram.getProgram().getId());/////////////////////////////////////////////
			}
			else // module - we do not know workout id (can have few workouts in the same day)
			{
				sql = DBAdapter.getLogExerciseNamesSqlForModule(datetime); //,workoutId);
			}
			loader= new SQLiteCursorLoader(this, dbAdapter.DBHelper, sql, null);
			return(loader);
		}
		if(id == LOADER_LOG_CARDIO)
		{
			String sql;
			if(selectMode == WorkoutMode.All)
			{
				sql = DBAdapter.getCardioLogSqlForAll(workoutId,weekId,datetime);
			}
			else 
			{
				sql = DBAdapter.getCardioLogSqlForModule(datetime);
			}
			loader= new SQLiteCursorLoader(this, dbAdapter.DBHelper, sql, null);
			return(loader);
		}
		if(bundle != null )
		{
			if(bundle.containsKey(SQL))
			{
				String qry = bundle.getString(SQL);
				//String qry= DBAdapter.getLogExerciseNamesSql(datetime,workoutId,weekId);
				//populate child
				loader= new SQLiteCursorLoader(this, dbAdapter.DBHelper, qry, null);
				return(loader);
			}
		}
		return null;
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> ld, Cursor cursor) {
		final int id = ld.getId();
		if(id == LOADER_LOG)
		{
			if(logAdapter != null)
			{
				try
				{
					logAdapter.changeCursor(cursor);	
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				//expand 
				
				if(cursor != null && cursor.getCount() >0)
				{
					final ExpandableListView list = this.getExpandableListView();	
					if(list == null)
					{
						Log.e(TAG, "ExpandableListView is null");
						return;
					}
					final int cnt = cursor.getCount();
					for(int i =0; i < cnt; i++)
					{
						list.expandGroup(i);
					}
					list.invalidateViews();
				}
				else
				{
					hideExerciseList();
				}
				
			}
		}
		else if(id==LOADER_LOG_CARDIO)
		{
			if(cursor == null || cursor.getCount()== 0)
			{
				mManager.destroyLoader(LOADER_LOG_CARDIO);
				cardioAdapter = null;
				hideCardio();
				
			}			
			else if(cardioAdapter != null)
			{				
				try
				{
					cardioAdapter.changeCursor(cursor);	
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}			
				
			}
		}
		else 
		{
			if(logAdapter != null)
			{
				try
				{
					logAdapter.setChildrenCursor(id, cursor);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}				
			}
		}
		
		
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> ld) {
		final int id = ld.getId();
		if(id == LOADER_LOG)
		{
			try
			{
				if(logAdapter != null) logAdapter.changeCursor(null);
			}
			catch(Exception ex)
			{
				//nothing
			}
			
		}
		else
		{
			try
			{
				if(logAdapter != null)
				{
					logAdapter.setChildrenCursor(id, null); //was NullPointerExeption
				}
			}
			catch(Exception ex)
			{
				//nothing
			}
			
		}		
		
	}



	@Override
	public int getId() {
		return Common.ACT_LogViewActivity;
	}
	
	private class LogAdapter extends SimpleCursorTreeAdapter
	{
		public LogAdapter(Context context, Cursor cursor, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom,
				int[] childTo) {
				super(context, cursor, groupLayout, groupFrom, groupTo,
		                childLayout, childFrom, childTo);
		}

//		public LogAdapter(Context context, Cursor cursor, int groupLayout,
//				String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom,
//				int[] childTo) {
//				super(context, cursor, groupLayout, groupFrom, groupTo,
//		                childLayout, childFrom, childTo);
//		}

		// returns cursor with subitems for given group cursor
		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			// Given the group, we return a cursor for all the children within that group 
			if(selectMode == WorkoutMode.Tactical ||
					(selectMode == WorkoutMode.All && dayMode == WorkoutMode.Tactical))
			{
				int wkId=-1;
				int colWkId;
				try
				{
					colWkId =  groupCursor.getColumnIndexOrThrow(DBAdapter.COL_ID);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					return null;
				}
				wkId = groupCursor.getInt(colWkId);
				Bundle bundle = new Bundle();
				String sql = DBAdapter.getTackticalLogSQLForSubView(wkId, datetime, (selectMode == WorkoutMode.All)? true : false);
				bundle.putString(SQL, sql);	
				
				int groupPos = groupCursor.getPosition();
				
				if(mManager.getLoader(groupPos) != null && !mManager.getLoader(groupPos).isReset())
				{
					mManager.restartLoader(groupPos, bundle, LogViewActivity.this);
				}
				else
				{
					 mManager.initLoader(groupPos, bundle, LogViewActivity.this);
				}
			}
			else
			{
				int logId = -1;
				int colLogId;
				try
				{
					colLogId =  groupCursor.getColumnIndexOrThrow(DBAdapter.COL_ID);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					return null;
				}
				logId = groupCursor.getInt(colLogId);
				
				Bundle bundle = new Bundle();
				String sql = DBAdapter.getWeightLogsByLogIdSql(logId);
				bundle.putString(SQL, sql);	
				
				int groupPos = groupCursor.getPosition();
				
				if(mManager.getLoader(groupPos) != null && !mManager.getLoader(groupPos).isReset())
				{
					mManager.restartLoader(groupPos, bundle, LogViewActivity.this);
				}
				else
				{
					 mManager.initLoader(groupPos, bundle, LogViewActivity.this);
				}
			}
			
			
			
			return null;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
			View v = super.getGroupView( groupPosition, isExpanded, convertView, parent);
			View indView = v.findViewById( R.id.group_indicator);
			if( indView != null ) 
			{	
				ImageView img = (ImageView)indView;				
					if(isExpanded)
					{
						img.setImageResource(R.drawable.group_open);
					}
					else
					{
						img.setImageResource(R.drawable.group_close);
					}			
			}
			return v;
			
		}

		@Override
		public void setViewBinder(ViewBinder viewBinder) {
			// TODO Auto-generated method stub
			super.setViewBinder(viewBinder);
		}

		@Override
		public ViewBinder getViewBinder() {
			// TODO Auto-generated method stub
			return super.getViewBinder();
		}	
		
		

		
		
//		@Override
//		protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
//			if(cursor == null)
//			{
//				return;
//			}
//			TextView txtSets = (TextView)view.findViewById(R.id.txt_set);
//			TextView txtReps = (TextView)view.findViewById(R.id.txt_reps);
//			TextView txtWt = (TextView)view.findViewById(R.id.txt_wt);
//			
//			 int colSet = cursor.getColumnIndex(DBAdapter.COL_SET_NUM);
//			 
//			int sets = cursor.getInt(colSet);
//			 Log.d(TAG, "set=" + sets);
//			 txtSets.setText(Integer.toString(sets));
//			
//		}
	
		
	}
	
	private boolean getEmail()
	{
		DBAdapter adapter = DBAdapter.getAdapter(this.getApplicationContext());
		attachmentStr = new StringBuilder();
			
		attachmentStr.insert(0, Common.NEWLINE);		
		attachmentStr.insert(0, title.replace(',', '-')); 		
		
		WorkoutMode logMode = (selectMode == WorkoutMode.All && dayMode == WorkoutMode.Tactical)? dayMode : selectMode;
		
		this.emailBodyStr = adapter.getLogTextToEmailAll(datetime, workoutId, weekId, logMode, attachmentStr, CurProgram.getProgram().getId());		
		
		if(emailBodyStr == null) return false;
		if(TEMP_FILE != null)		
		{
			try
			{
				TEMP_FILE.delete();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		String outputDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		try {
//			if(!outputDir.exists())
//			{
//				File.createTempFile(prefix, suffix)
//			}
			TEMP_FILE = new File(outputDir, "logdata.csv");
		} catch (Exception e) {			
			e.printStackTrace();
			attachmentStr = null;
		}
		if(attachmentStr != null && TEMP_FILE != null)
		{
			if(!Common.writeToFile(TEMP_FILE, attachmentStr.toString()))
			{
				attachmentStr = null;
				try
				{
					TEMP_FILE.delete();
					TEMP_FILE= null;
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}		
		return true;
	}
	
	private void processGetLogEmailDone(final boolean res)
	{
		if(res && emailBodyStr != null)
		{
			String appName = this.getString(R.string.app_name);
			emailBodyStr.append(Common.NEWLINE);
			emailBodyStr.append(Common.NEWLINE);
			emailBodyStr.append("Thank you for using ");
			emailBodyStr.append(appName);
			emailBodyStr.append(".");
			emailBodyStr.append(Common.NEWLINE);
			emailBodyStr.append("Olmatech Development Team,");
			emailBodyStr.append(Common.NEWLINE);
			emailBodyStr.append("www.olmatech.com");
			
			emailBodyStr.insert(0, Common.NEWLINE);
			emailBodyStr.insert(0,selectMode.getTitle());
			
			String subject =appName + " - " + title;	
			if(attachmentStr != null && TEMP_FILE != null)
			{
				Common.sendMail(this, null, subject, emailBodyStr.toString(), TEMP_FILE);
			}
			else  Common.sendMail(this, null, subject, emailBodyStr.toString(), null);
			
			attachmentStr = null;
			emailBodyStr = null;
		}
		else
		{
			this.showErrorDlg(R.string.err_processing, Common.REASON_NOT_IMPORTANT);
		}
		
	}
	
	
	
	protected class EmailWorkoutTask extends AsyncTask<String, Void, long[]>
	{
		private ProgressDialog dialog = new ProgressDialog(LogViewActivity.this);  
		// can use UI thread here
		 protected void onPreExecute() { 
			 this.dialog.setMessage(getString(R.string.processing));  
 			 this.dialog.show();  
		  }  

		@Override
		protected long[] doInBackground(String... arg0) {
			boolean ok = getEmail();
			long[] res = new long[1];
			res[0] =(ok)? 1 : 0;
			return res;
		}
		
		protected void onPostExecute(final long[] result) 
		{
			try
			{
				if(dialog != null)
				{
					dialog.dismiss();
					dialog = null;
				}
			}
			catch(Exception e)
			{
				//nothing
			}	         
			LogViewActivity.this.processGetLogEmailDone((result[0]==1)? true : false);
		}
		
	}

	
	private void redrawMe() {
		View mainCont = this.findViewById(R.id.main_cont);
		if(mainCont != null) mainCont.invalidate();
		
	}

}

