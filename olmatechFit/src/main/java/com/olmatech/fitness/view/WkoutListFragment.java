package com.olmatech.fitness.view;

// Block - Day or Rest list 
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.ui.BaseFragmentActivity;

public class WkoutListFragment extends BaseLoaderFragment{
	
	final String TAG ="WkoutListFragment";    
	private int progrId=-1;
	private int curWeekOrder=-1;
	private int curDayOrder=-1;
	private ResourceCursorAdapter listAdapter;	
	
	private boolean showContenetMenu= true;
		
	private int curSelWorkoutId = -1; // for mode view		
	private boolean showStartButton = false; //button Start or View
	private boolean showCompleteTick = false;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LOADER_ID=Common.LOADER_WK_LIST;
		
		setHaveProgress();

		mManager = parentActivity.getSupportLoaderManager();
		
		View v = inflater.inflate(R.layout.fragment_workout_list, container, false);		
		list = (ListView)v.findViewById(R.id.wkList);
		
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey("sel_workout"))
			{
				curSelWorkoutId = savedInstanceState.getInt("sel_workout");
			}
		}
				
		return v;
	}
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("sel_workout", curSelWorkoutId);
		super.onSaveInstanceState(outState);
	}



	//public void setCurrentDayId(final int val){ currentDayId=val; }
	public void setCurDayOrder(final int val) { curDayOrder=val; }
	//public void setCurWeekId(final int val){ currentWeekId = val; }
	public void setCurWeekOrder(final int val){ curWeekOrder=val; }
	public void setProgrId(final int val){ progrId = val; }
	
	public void setShowCompleteTick(final boolean val){ showCompleteTick=val; }
	
//	public void setHandleListItemClick(final boolean val)
//	{
//		if(list == null) return;
//		if(val)
//		{
//			list.setOnItemClickListener(new OnItemClickListener(){
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View v, int position, long rowId) {
//					//we will hide this list and show workout in ExerciseList
//					if(parentActivity == null || isIHidden()) return;
//					Object objTag = v.getTag();
//					processItemClick(objTag);				
//				}
//				
//			});		
//		}
//		else if(list.getOnItemClickListener() != null)
//		{
//			list.setOnItemClickListener(null);
//		}
//		
//	}
	
	
	
	public int getCurSelWorkoutId()
	{
		return curSelWorkoutId;
	}
	
	public void setCurSelWorkoutId(final int val)
	{
		if(curSelWorkoutId != val)
		{
			curSelWorkoutId = val;
			refreshViews();
		}
	}
	
	public void setShowStartButton(final boolean show)
	{
		showStartButton = show;
		refreshViews();
	}
	
	public void refreshViews()
	{
		if(list != null && list.getChildCount() >0)
		{
			list.invalidateViews();
		}
	}
	
	public void scrollListToSelected(final int selectedItem)
	{
		if(list != null)
		{
			try
			{
				list.setSelection(selectedItem);				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
	}
	
	//Item click by mode
	private void processContentMenuItemClick(final DayInfo tag, final int butId)
	{
		switch(viewMode)
		{
		case All:		
			String title = "Workout - Block " + tag.weekId + " Day " + tag.dayId;
			if(mListener != null){
				switch(butId)
				{
				case R.id.butView:
					mListener.processShowDayWorkout(tag.workoutId, tag.weekId, tag.dayId, title, tag.workoutMode);
					break;
//				case R.id.butDo:
//					mListener.processDoDayWorkout(tag.workoutId, title, tag.workoutMode);
//					break;
				case R.id.butMakeCurrent:
					mListener.onSetDayCurrent(tag);
					break;
					default: break;				
				}				
			}
			break;		
		default:
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {		
		parentActivity = (BaseFragmentActivity)activity;
		super.onAttach(activity);		
	}
	
	public void fillData()
	{
		if(haveProgress && showProgress)
		{
			showProgress(true);
		}
		if(mManager.getLoader(LOADER_ID) != null && !mManager.getLoader(LOADER_ID).isReset())
		{
			mManager.restartLoader(LOADER_ID, null, this);
		}
		else
		{
			mManager.initLoader(LOADER_ID, null, this);
		}
		
		if(listAdapter == null)
		{
			switch(viewMode)
			{
			case All:
				listAdapter = new WkListAdapter(parentActivity, 
						R.layout.wk_list_item,
						null,
						ResourceCursorAdapter.NO_SELECTION);
				break;
			case Tactical:				
			case Endurance:			
			case Bodybuilding:
				//WorkoutModeAdapter
				listAdapter = new WorkoutModeAdapter(parentActivity, 
						R.layout.wk_module_list_item,
						null,
						ResourceCursorAdapter.NO_SELECTION);
				break;
			default:
				break;
			}			
			
			
			if(list != null)
			{
				list.setAdapter(listAdapter);
				
			}
			else
			{
				Common.showToast("Error reading data", parentActivity);
			}
		}			
		
	}
	
	private void processSelectedIdSet()
	{
		if(mListener != null)
		{
			mListener.onListSelectedItemIdSet(this.curSelWorkoutId, this.fragmentId);
		}
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		stopLoader();
		super.onStop();
	}
	
	///////////////// MENU ///////////////
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(!showContenetMenu) return;
		try
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			if(info != null && info.targetView != null)
			{
				Object tag = info.targetView.getTag();
				if(tag == null) return;  //Rest dayOfMonth - no menu
				
				int[] loc = new int[2];
				info.targetView.getLocationOnScreen(loc); 					
				showContextPopup(tag, loc[0], loc[1]);	 //info.targetView.getTop()	
								
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void showContextPopup(Object objTag,final int left, final int top)
	{
		View dialog = this.parentActivity.getLayoutInflater().inflate(R.layout.wk_list_content_menu, null);
		if(objTag == null) return;
		final DayInfo dayInfo;
		try
		{
			dayInfo = (DayInfo)objTag;
		}
		catch(Exception ex)
		{
			return;
		}		
		
		Button but = (Button)dialog.findViewById(R.id.butView);		
		but.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				pw.dismiss();				
				processContentMenuItemClick(dayInfo, v.getId());
			}			
		});
		
//		but = (Button)dialog.findViewById(R.id.butDo);		
//		but.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				pw.dismiss();				
//				processContentMenuItemClick(dayInfo, v.getId());
//			}			
//		});
		but = (Button)dialog.findViewById(R.id.butMakeCurrent);
		if(dayInfo.dayOrder >= curDayOrder)
		{
			but.setVisibility(View.GONE);
		}
		else
		{
			but.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					pw.dismiss();				
					processContentMenuItemClick(dayInfo, v.getId());
				}			
			});
		}
		
		pw = new PopupWindow(dialog, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		RectShape rect = new RectShape();
		PaintDrawable p = new PaintDrawable(Color.TRANSPARENT);
		p.setShape(rect);
       
        pw.setBackgroundDrawable((Drawable)p);
        pw.setOutsideTouchable(true);
        pw.showAtLocation(dialog, Gravity.TOP, 40, top+20); //Gravity.TOP
	}

	//////////// LOADERS ////////////////////////////
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle b) {
		SQLiteCursorLoader loader = null;
		if(id==LOADER_ID)
		{
			if(parentActivity == null)
			{
				if(Common.DEBUG) Log.d(TAG, "No activity");
				return null;
			}
			try
			{
				DBAdapter dbAdapter= DBAdapter.getAdapter(parentActivity);
				String listStr=null;
				switch(viewMode)
				{
				case All:
					listStr = dbAdapter.getProgramWeeksDaysSQL(progrId);
					break;
				case Tactical:					
				case Endurance:				
				case Bodybuilding:
					listStr = dbAdapter.getWorkoutTitlesForMode(viewMode);
					break;
				default:
					break;
				}			
				
				if(listStr == null)
				{
					if(Common.DEBUG) Log.d(TAG, "Error getting getExListSQL");
				}
				else
				{
					loader= new SQLiteCursorLoader(parentActivity, dbAdapter.DBHelper, listStr, null);
				}				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return(loader);
		}
		
		return null;
	}

	

	@Override
	public void onLoadFinished(Loader<Cursor> ld, Cursor cursor) {
		final int id = ld.getId();
		if(id==LOADER_ID)
		{
			if(listAdapter != null)
			{
				try
				{					
					listAdapter.changeCursor(cursor);						
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(mListener != null)
			{
				mListener.onFragmentLoadFinished(fragmentId);
			}
			if(haveProgress && showProgress)
			{
				showProgress(false);
			}
			
		}		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> ld) {
		final int id = ld.getId();
		//set adapter cursor to null
		if(id==LOADER_ID)
		{
			try
			{
				if(listAdapter != null) listAdapter.changeCursor(null);
			}
			catch(Exception ex)
			{
				//nothing
			}
		}
		
	}
	
	private class WkListAdapter extends ResourceCursorAdapter
	{		
		//private int curWeek=1;

		public WkListAdapter(Context context, int layout, Cursor c, int flags) {
			super(context, layout, c, flags);			
		}

		@Override
		public void bindView(View v, Context ctx, Cursor cur) {
			TextView tvBlock = (TextView)v.findViewById(R.id.txtBlock);
			TextView tvDay= (TextView)v.findViewById(R.id.txtDay);
			View imgArrow = v.findViewById(R.id.imgArrow);
			View bgView = v.findViewById(R.id.itemBg);
			ImageView completeTick = (ImageView)v.findViewById(R.id.img_day_done);//complete tick or workout type icon
			View completeOverlay = v.findViewById(R.id.imgCompleteOverlay);
			
			int ind;
			int ind2, ind3;
			try
			{
				ind = cur.getColumnIndexOrThrow(DBAdapter.COL_MODE);				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ind = -1;				
			}
			
			try
			{
				ind3 = cur.getColumnIndexOrThrow(DBAdapter.COL_DAY_ORDER);				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ind3 = -1;				
			}
			
			String strDay;
			int dayNum=-1;
			if(ind3 >=0)
			{
				dayNum = cur.getInt(ind3);
				int dayOrder = dayNum%7;	
				if(dayOrder == 0) dayOrder=7;
				strDay = "Day " + dayOrder;
			}
			else strDay="Day";
			
			//get week
			try
			{
				ind2 = cur.getColumnIndexOrThrow(DBAdapter.COL_WEEK_ORDER);
			}
			catch(Exception e)
			{
				e.printStackTrace();				
				ind2=-1;
			}
			WorkoutMode wk_mode = WorkoutMode.Unknown;
			if(ind >=0)
			{
				String mode = cur.getString(ind);
				wk_mode = WorkoutMode.getMode(mode);
				if(wk_mode == WorkoutMode.Rest)
				{
					String modeStr = wk_mode.getTitle();					
					tvDay.setText(strDay + " - " + modeStr);
					
				}//Rest	
				else  //show week / dayOfMonth
				{
					tvDay.setText(strDay);					
				}				
			}	
			int weekOrder=-1;
			
			if(ind2 >=0)
			{
				weekOrder= cur.getInt(ind2);
				//Log.d(TAG, "week_order=" + week_order + " currentWeek=" + currentWeek + " dayId=" + dayIndex + " currentDay=" + currentDay);
				if(weekOrder == curWeekOrder)
				{					
					if(bgView != null)
					{
						bgView.setBackgroundResource(R.drawable.wk_list_item_active_selector);
					}
					
					if(dayNum <=0 || (dayNum >0 && dayNum!=curDayOrder))
					{
						if(imgArrow != null) imgArrow.setVisibility(View.GONE);						
					}
					else{						
						if(imgArrow != null) {
							imgArrow.setVisibility(View.VISIBLE);
							imgArrow.bringToFront();
							
							final int selectedItem = cur.getPosition();							
							if(mListener != null)
							{
								mListener.onSelectedItem(selectedItem);
							}
						}
					}
									
				}
				else
				{
					if(bgView != null)
					{
						if(wk_mode == WorkoutMode.Rest) bgView.setBackgroundResource(R.drawable.workout_bg_list);
						else bgView.setBackgroundResource(R.drawable.wk_list_item_selector);
					}
					if(imgArrow != null)
					{
						imgArrow.setVisibility(View.GONE);
					}
				}
			}
			
			if(weekOrder >0)
			{
				tvBlock.setText("Block " + weekOrder);
			}
			
			if(showCompleteTick)
			{
				if((weekOrder < curWeekOrder) || (weekOrder ==curWeekOrder && dayNum < curDayOrder)){
					completeTick.setImageResource(R.drawable.tic);
					completeTick.setVisibility(View.VISIBLE);
					completeOverlay.setVisibility(View.VISIBLE);
				}
				else{
					//see if we are on rest day or workout
					if(wk_mode == WorkoutMode.Rest)
					{
						completeTick.setVisibility(View.GONE);
						completeOverlay.setVisibility(View.GONE);
					}
					else if(wk_mode == WorkoutMode.Endurance)
					{
						completeTick.setImageResource(R.drawable.endurance);
						completeTick.setVisibility(View.VISIBLE);
						completeOverlay.setVisibility(View.GONE);
					}
					else if(wk_mode == WorkoutMode.Tactical)
					{
						completeTick.setImageResource(R.drawable.tactical);
						completeTick.setVisibility(View.VISIBLE);
						completeOverlay.setVisibility(View.GONE);
					}
					else if(wk_mode == WorkoutMode.Cardio)
					{
						completeTick.setImageResource(R.drawable.cardio_icon);
						completeTick.setVisibility(View.VISIBLE);
						completeOverlay.setVisibility(View.GONE);
					}
					else{ //weight
						completeTick.setImageResource(R.drawable.weight);
						completeTick.setVisibility(View.VISIBLE);
						completeOverlay.setVisibility(View.GONE);
					}			
				}
			}
			else{
				completeTick.setVisibility(View.GONE);
				completeOverlay.setVisibility(View.GONE);
			}
					
			
			//tag
			if(wk_mode != WorkoutMode.Rest && wk_mode != WorkoutMode.Unknown)
			{
				//COL_WORKOUT_ID
				int colWeekId = 0, colDayId;
				try
				{
					ind = cur.getColumnIndexOrThrow(DBAdapter.COL_WORKOUT_ID);	
					colWeekId = cur.getColumnIndexOrThrow(DBAdapter.COL_WEEK_ID);
					colDayId = cur.getColumnIndexOrThrow(DBAdapter.COL_ID);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ind = -1;	
					colWeekId=-1;
					colDayId=-1;
				}
				
				if(ind >=0 && colWeekId >=0 && colDayId >=0)
				{
					DayInfo dayInfo = new DayInfo();
					dayInfo.workoutId = cur.getInt(ind);
					dayInfo.weekId = cur.getInt(colWeekId);
					dayInfo.dayId = cur.getInt(colDayId);
					dayInfo.workoutMode= wk_mode;
					dayInfo.dayOrder = dayNum;
								
					//set tag
					v.setTag(dayInfo);
					
				}
//				if(showStartButton)
//				{
//					butStart.setOnClickListener(new OnClickListener(){
//
//						@Override
//						public void onClick(View v) {
//							if(mListener != null)
//							{
//								mListener.onStartButtonClick(fragmentId, v.getTag());
//							}
//						}						
//					});
//				}
//				else
//				{
//					butStart.setVisibility(View.GONE);
//					butStart.setEnabled(false);
//				}
			}	
//			else
//			{
//				butStart.setVisibility(View.GONE);
//				butStart.setEnabled(false);
//			}
			
		} //bindView
		
	}
	
	//adapter for Mode
	private class WorkoutModeAdapter extends ResourceCursorAdapter
	{
		private int itemId=0;

		public WorkoutModeAdapter(Context context, int layout, Cursor c, int flags) {
			super(context, layout, c, flags);
			
		}
		
		
		@Override
		public void bindView(View v, Context ctx, Cursor cur) {
			//TextView tvBlock = (TextView)v.findViewById(R.id.txtBlock);
			TextView tvDay= (TextView)v.findViewById(R.id.txtDay);
			View imgArrow = v.findViewById(R.id.imgArrow);
			Button butStart = (Button)v.findViewById(R.id.wk_but_start);
			View completeTick = v.findViewById(R.id.img_day_done);
			if(completeTick != null) completeTick.setVisibility(View.GONE);
			int ind, ind2;
			
			try
			{
				ind = cur.getColumnIndexOrThrow(DBAdapter.COL_ID);
				ind2 = cur.getColumnIndexOrThrow(DBAdapter.COL_TITLE);				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ind = -1;
				ind2=-1;
			}
			final int id = (ind >=0)? cur.getInt(ind) : -1;
			
			if(ind2>=0)
			{
				if(!cur.isNull(ind2))
				{
					tvDay.setText(cur.getString(ind2));					
				}
			}
			else if(id>=0)
			{
				tvDay.setText("#" + id);
			}			
			
     		if(itemId==0 || id == curSelWorkoutId)
			{
				imgArrow.setVisibility(View.VISIBLE);
				if(itemId==0 && curSelWorkoutId <=0){
					curSelWorkoutId = id; //first selected
				}
			}
			else imgArrow.setVisibility(View.INVISIBLE);
			
			itemId =id;			
			v.setTag(Integer.valueOf(id)); // workout id
			
			final boolean isSelected = (curSelWorkoutId == id)? true : false;
			
			if(showStartButton)
			{
				if(isSelected)
				{
					butStart.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							if(mListener != null)
							{
								mListener.onStartButtonClick(fragmentId, v.getTag());
							}
						}
						
					});
					
					butStart.setText(R.string.start);
					butStart.setEnabled(true);
				}
				else
				{
					butStart.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							if(mListener != null)
							{
								mListener.onViewButtonClick(fragmentId, v.getTag());
							}
						}
						
					});
					
					butStart.setText(R.string.select_title);
					butStart.setEnabled(true);
				}
				butStart.setTag(Integer.valueOf(id));
			}//show
			else
			{
				butStart.setVisibility(View.GONE);
				butStart.setEnabled(false);
			}		
			//background
			View bgView = v.findViewById(R.id.itemBg);
			if(bgView != null) {
				if(isSelected) bgView.setBackgroundResource(R.drawable.workout_bg_list_on);
				else bgView.setBackgroundResource(R.drawable.wk_list_item_selector);
			}
			
			if(curSelWorkoutId>=0){
				//can populate Exercise List
				processSelectedIdSet();
			}
		}
		
	}

	@Override
	void setHaveProgress() {
		haveProgress = true;
		progressBarId=R.id.progrBarWkList;
		showProgress= true;
	}
	
		

}
