package com.olmatech.fitness.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DBAdapter;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.CurProgram;
import com.olmatech.fitness.main.DayInfo;
import com.olmatech.fitness.main.User;
import com.olmatech.fitness.main.User.Gender;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.ui.BaseFragmentActivity;
//Fragment showing list of exercises - picture + name + order#
public class ExListFragment extends BaseLoaderFragment{
	
	final String TAG ="ExListFragment";
	
	//protected static DBAdapter dbAdapter;
	private ExListAdapter  listAdapter;	
	
	//settings
	private boolean showTitles=true;		
	//list data
	
	private DayInfo dayInfo; //dayOfMonth data
	
	private final static int MAX_IMG_HEIGHT=80; 
	private int maxImgH;	
	private AssetManager assetManager;
	//bmp data
	private int bmpInSampleSize = 0;
	private int bmpWidth =0;
	private int bmpHeight =0;
	
	//helper data
	private String imgDirectory;
	private Gender userGender = Gender.Unknown;

	
//ExampleFragment fragment = (ExampleFragment) getFragmentManager().findFragmentById(R.id.example_fragment);	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//progr = CurProgram.getProgram();
		//dayInfo = new DayInfo();
		setHaveProgress();
		//curExerciseInd= progr.getCurExerciseInd();
		LOADER_ID=-Common.LOADER_EX_LIST;
		mManager = parentActivity.getSupportLoaderManager();
		imgDirectory = CurProgram.getProgrDir() + File.separator;
		userGender = User.getUser().getGender();
		
		View v = inflater.inflate(R.layout.ex_list, container, false);
		list = (ListView)v.findViewById(R.id.exList);
		
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long rowId) {
				//set cur exercise to selected and start workout
				if(!doItemClick || mListener == null) return;
				
				//for all modes
				if(dayInfo == null || dayInfo.workoutId <0)
				{
					showErrorDlg(R.string.err_reading_data);
					return;
				}
				View txtOrder= v.findViewById(R.id.txtExNumList);
				if(txtOrder == null) 
				{
					showErrorDlg(R.string.err_reading_data);
					return;
				}
				Object tag = txtOrder.getTag();
				if(tag == null)
				{
					showErrorDlg(R.string.err_reading_data);
					return;
				}				
				mListener.onExerciseListItemClick(tag, fragmentId);	
			}
			
		});
		
		list.setRecyclerListener(new RecyclerListener(){

			@Override
			public void onMovedToScrapHeap(View view) {
				ImageView imgView = (ImageView)view.findViewById(R.id.imgList);
				Object tag = imgView.getTag();
				if(tag != null)
				{
					Bitmap bmp1 = null;
					 try
					 {
						 bmp1 = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
					 }
					 catch(Exception ex)
					 {
						 bmp1 = null;
					 }
					// Release strong reference when a view is recycled
					 imgView.setImageBitmap(null);
					 if(bmp1 != null)
					 {
						 bmp1.recycle();
					 }
				}
				
			}
			
		});
		
		maxImgH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_IMG_HEIGHT, getResources().getDisplayMetrics());	
		
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		bmpInSampleSize  = Common.calculateInSampleSize(options, maxImgH, maxImgH);
		
		return v;
	}
		
	public void setListDayInfo(final int progId, final int wkId, final int curExIndex, 
			final int weekIndex, final int dayIndex, WorkoutMode md)
	{
		if(dayInfo == null) dayInfo = new DayInfo();
		dayInfo.progrId=progId;
		dayInfo.workoutId=wkId;
		dayInfo.curExOrder = curExIndex;
		dayInfo.weekId = weekIndex;
		dayInfo.dayId= dayIndex;
		dayInfo.workoutMode = md;
	}
	
	public void setListDayShortInfo(final int progId, final int wkId, final WorkoutMode md)
	{
		setListDayInfo(progId, wkId, 0, -1, -1, md);
	}
	
	public void setProgramId(final int val) { if(dayInfo == null) dayInfo = new DayInfo(); dayInfo.progrId=val; }
	public void setWorkoutId(final int val) { if(dayInfo == null) dayInfo = new DayInfo(); dayInfo.workoutId=val; }
	public int getWorkoutId(){ return (dayInfo!=null)? dayInfo.workoutId : -1; }
	public void setCurExIndex(final int val) { if(dayInfo == null) dayInfo = new DayInfo(); dayInfo.curExOrder=val; }
	//public void setImageDirectory(final String progrDir){ imgDirectory = progrDir + File.separator; }
	//public void setUserGender(final Gender g){ userGender = g; }
	
	public void fillData()
	{
		System.gc();
		if(dayInfo == null || dayInfo.progrId < 0 || dayInfo.workoutId <0)
		{
			if(mListener != null)
			{
				mListener.onFragmentLoadFinished(fragmentId);
			}
			if(parentActivity != null ){
				parentActivity.showErrorDlg(R.string.err_reading_workout, Common.REASON_NOT_IMPORTANT);							
			}
			return;
		}
		if(haveProgress && showProgress)
		{
			showProgress(true);
			//Log.d(TAG, "showProgress(true)");
		}
		
		startLoader();
		
		listAdapter = new ExListAdapter(parentActivity, 
				R.layout.ex_list_item,
				null,
				ResourceCursorAdapter.NO_SELECTION
				);
		
		
		if(list != null)
		{			
			list.setAdapter(listAdapter);

		}
		else
		{
			Common.showToast("Error reading data", parentActivity);
		}
		
	}	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(!showContenetMenu || (dayInfo != null && dayInfo.workoutMode == WorkoutMode.Tactical)) return;
		try
		{
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			if(info != null && info.targetView != null)
			{				
					Object tag = info.targetView.getTag();
					if(tag != null)
					{
						int[] loc = new int[2];
						info.targetView.getLocationOnScreen(loc); 					
						View imgExComplete = info.targetView.findViewById(R.id.imgCompleteOverlay);
						boolean isComplete;
						if(imgExComplete != null)
						{
							isComplete = (imgExComplete.getVisibility() == View.VISIBLE)? true : false;
						}
						else isComplete=false;
						showContextPopup(tag, loc[0], loc[1], isComplete);	 //info.targetView.getTop()	
					}								
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void showContextPopup(final Object tag, final int left, final int top, final boolean isComplete)
	{
		try
		{
			View dialog = this.parentActivity.getLayoutInflater().inflate(R.layout.ex_list_content_menu, null);
			
			Button butComplete = (Button)dialog.findViewById(R.id.butComplete);
			butComplete.setText((isComplete)? R.string.reset : R.string.complete);
			butComplete.setTag(tag);
			butComplete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					pw.dismiss();
					if(mListener != null)
					{
						mListener.onSetComplete(v.getTag(), isComplete, fragmentId);
					}
				}
				
			});
			
			pw = new PopupWindow(dialog, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			RectShape rect = new RectShape();
			PaintDrawable p = new PaintDrawable(Color.TRANSPARENT);
			p.setShape(rect);
	       
	        pw.setBackgroundDrawable((Drawable)p);
	        pw.setOutsideTouchable(true);
	        pw.showAtLocation(dialog, Gravity.TOP, 40, top+20); //Gravity.TOP
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
//	private void processContextMenuButClick(final Object tag, final boolean isComplete)
//	{
//		
//	}
	
	
	

	//settings
	public void setShowTitles(final boolean val)
	{
		showTitles=val;
	}
	

	@Override
	public void onAttach(Activity activity) {			
		parentActivity = (BaseFragmentActivity)activity;			
		assetManager = parentActivity.getAssets();
		super.onAttach(activity);
	}



	
	
	/////////////// HELPERS //////////////////////////
	
//	private Bitmap getBmpFromAssets(final String name)
//	{
//		if(parentActivity == null || progr== null) return null;
//		Bitmap bm;
//		String strName = progr.getProgrDir() + File.separator + name;
//		//Log.d(TAG,"getBmpFromAssets strName=" + strName );
//		try {
//		    AssetManager assetManager = parentActivity.getAssets();
//		    InputStream istr = assetManager.open(strName);
//		    bm = BitmapFactory.decodeStream(istr);			
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//			bm=null;
//		}
//		return bm;
//	}
	
	
	
	private Bitmap getBmpFromAssets2(final String name)
	{			
		
		if(parentActivity == null || imgDirectory== null) return null;
		String strName = imgDirectory + name;
		
		Bitmap bm = null;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		  //////////////////////////////
		InputStream istr = null;
		try
		{
			istr = assetManager.open(strName);
			BitmapFactory.decodeStream(istr, null, options);
			try {
			    istr.reset();
			} catch (IOException e) {
			    Log.e(TAG, e.getMessage());
			}	
			// Calculate inSampleSize			
		   if(bmpInSampleSize <=0 || options.outWidth != bmpWidth || options.outHeight != bmpHeight) 
		   {
			   bmpWidth = options.outWidth;
			   bmpHeight = options.outHeight;
			   bmpInSampleSize = Common.calculateInSampleSize(bmpWidth, bmpHeight, maxImgH, maxImgH); 
		   }
		 			
			options.inSampleSize = bmpInSampleSize; 
		 // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    bm = BitmapFactory.decodeStream(istr, new Rect(), options);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			bm = null;
		}
		finally
		{
			try
			{
				if(istr != null) istr.close();
			}
			catch(Exception ex2)
			{
				
			}
		}
		
		return bm;
			
		
	}
	
	
	
	
//	private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
//
//	    // First decode with inJustDecodeBounds=true to check dimensions
//	    final BitmapFactory.Options options = new BitmapFactory.Options();
//	    options.inJustDecodeBounds = true;
//	    BitmapFactory.decodeResource(res, resId, options);
//
//	    // Calculate inSampleSize
//	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//	    // Decode bitmap with inSampleSize set
//	    options.inJustDecodeBounds = false;
//	    return BitmapFactory.decodeResource(res, resId, options);
//	}
	
	
	
	
	
	/*
	 * private Bitmap decodeFile(File f){
    Bitmap b = null;

        //Decode image size
    BitmapFactory.Options o = new BitmapFactory.Options();
    o.inJustDecodeBounds = true;

    FileInputStream fis = new FileInputStream(f);
    BitmapFactory.decodeStream(fis, null, o);
    fis.close();

    int scale = 1;
    if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
        scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / 
           (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
    }

    //Decode with inSampleSize
    BitmapFactory.Options o2 = new BitmapFactory.Options();
    o2.inSampleSize = scale;
    fis = new FileInputStream(f);
    b = BitmapFactory.decodeStream(fis, null, o2);
    fis.close();

    return b;
}
	 */
	
	
	///////////// ADAPTER
	
	/*
	 * COL_ENTRY_ID
COL_TITLE
COL_IMG_MALE
COL_IMG_FEMALE
COL_ENTRY_ORDER
COL_COMPLETE
	 */
	private class ExListAdapter extends ResourceCursorAdapter
	{
		public ExListAdapter(Context context, int layout, Cursor c, int flags) {
			super(context, layout, c, flags);
			
		}

		@Override
		public void bindView(View v, Context ctx, Cursor cur) {
			ImageView imgView = (ImageView)v.findViewById(R.id.imgList);
			TextView txtView = (TextView)v.findViewById(R.id.txtList); //title			
			//order block
			TextView txtOrder= (TextView)v.findViewById(R.id.txtExNumList); //order
						
			View imgArrow = v.findViewById(R.id.imgArrow);
			View imgExComplete = v.findViewById(R.id.imgCompleteOverlay);
						
			int ind;
			try
			{
				ind = cur.getColumnIndexOrThrow(DBAdapter.COL_ENTRY_ID);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ind = -1;
			}
			
			//debug
//			int tmpTag=0;
//			String tmpTitle="";
			
			if(ind >=0)
			{
				final int tag = cur.getInt(ind);
				v.setTag(Integer.valueOf(tag));				
			}
			else v.setTag(null);
			
			if(showTitles)
			{
				try
				{
					ind = cur.getColumnIndexOrThrow(DBAdapter.COL_TITLE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ind = -1;
				}
				if(ind <0 ) 
				{

					Log.e(TAG, "no ex title'");
					return;
				}
				String s = cur.getString(ind);
				txtView.setText(s);				
			}
			else
			{
				txtView.setVisibility(View.GONE);
			}			
			//image
			int ind_img;
			if(userGender == User.Gender.Male)
			{
				try
				{
					ind_img = cur.getColumnIndexOrThrow(DBAdapter.COL_IMG_MALE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ind_img = -1;
				}
			}
			else
			{
				try
				{
					ind_img = cur.getColumnIndexOrThrow(DBAdapter.COL_IMG_FEMALE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ind_img = -1;
				}
			}
						
			if(ind_img <0) 
			{
				//no image - display default
				Log.e(TAG, "no image link'");		
				imgView.setImageResource(R.drawable.cardio); // Assume cardio
				imgView.setTag(null);
			}
			else
			{
				String imgMName;				
				try
				{
					imgMName = cur.getString(ind_img);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					imgMName=null;
				}
				//image
				if(imgMName != null)
				{
					//Log.d(TAG, "img lnk=" + imgMName);
					Bitmap bm = getBmpFromAssets2(imgMName); ////////////////////////////////////////////////////////////////////////////////////////////////////
					if(bm != null)
					{
						imgView.setImageBitmap(bm); 
						imgView.setTag(Boolean.TRUE);
						bm=null;
					}
					else{
						if(Common.DEBUG) Log.d(TAG,"bmp is null");
						imgView.setImageResource(R.drawable.cardio); // Assume cardio
						imgView.setTag(null);
					}
				}
				else
				{
					imgView.setImageResource(R.drawable.cardio); // Assume cardio
					imgView.setTag(null);
				}
				
			}
			
			//order
			
			try
			{
				ind = cur.getColumnIndexOrThrow(DBAdapter.COL_ENTRY_ORDER);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ind = -1;
			}
			if(ind >=0) //have ex order
			{
				final int exOrder = cur.getInt(ind);				
				txtOrder.setText(Integer.toString(exOrder));
				txtOrder.setTag(Integer.valueOf(exOrder));  //TAG	
				
				if(imgArrow != null)
				{
					if(dayInfo != null && exOrder == dayInfo.curExOrder)
					{
						imgArrow.setVisibility(View.VISIBLE);
						imgArrow.bringToFront();
					}
					else imgArrow.setVisibility(View.GONE);
				}
					
			}			
			else if(imgArrow != null) imgArrow.setVisibility(View.GONE);
			
			//complete overlay
			if(imgExComplete != null)
			{
				try
				{
					ind = cur.getColumnIndexOrThrow(DBAdapter.COL_COMPLETE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					ind = -1;
				}
				if(ind >=0)
				{
					if(cur.isNull(ind))
					{
						imgExComplete.setVisibility(View.GONE);
						//Log.d(TAG, "NOT COMPLETED - NULL " + txtOrder.getText());
					}
					else
					{
						if(cur.getInt(ind) == 1)
						{
							imgExComplete.setVisibility(View.VISIBLE);
							//Log.d(TAG, "COMPLETED -  VISIBLE " + txtOrder.getText());
						}
						else 
						{
							imgExComplete.setVisibility(View.GONE);
							//Log.d(TAG, "NOT COMPLETED -  " + txtOrder.getText());
						}
					}
					
					
				}
				else
				{
					imgExComplete.setVisibility(View.GONE); //TODO - verify?
				}
			}
		
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {						
			if(parentActivity == null)
			{
				if(Common.DEBUG) Log.d(TAG, "newView - No activity");
				return null;
			}
			
			LayoutInflater li = (LayoutInflater)parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.ex_list_item, parent, false);
		}

		
	}
	
////////////////////////////LOADERS/////////////////////////////////////////
	@Override
	public  android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle)
	{
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
				String listStr;//final DayInfo dayInfo
				if(dayInfo.workoutMode == WorkoutMode.All)
				{
					listStr = dbAdapter.getExListSQL(dayInfo);//final DayInfo dayInfo
				}
				else{
					listStr = DBAdapter.getWorkoutListStrForMode(dayInfo.workoutMode, dayInfo.workoutId, Common.getDateTimeForToday());
				}
				if(listStr == null)
				{
					if(Common.DEBUG)  Log.d(TAG, "Error getting getExListSQL");
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
		//load into cursor
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
	
	
	//THREADS
//  	static class ContentLoadTask extends AsyncTask<String, Void, long[]>
//  	{
//
//		@Override
//		protected long[] doInBackground(String... arg0) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//  		
//  	}

	@Override
	void setHaveProgress() {
		haveProgress = true;
		progressBarId=R.id.progrBarExList;
		showProgress= true;
	}	

}
/*
 * select tbone._id as _id,tbone.entry_id as entry_id,tbone.entry_type as entry_type,tbone.entry_order as entry_order,tbone.title as title,tbone.description as description,tbone.sets as sets,tbone.time as time,tbone.level as level,tbone.img_male as img_male,tbone.img_female as img_female,tbone.mode as mode,tbone.en_weight as en_weight,tbone.en_reps as en_reps,tbone.bb_weight as bb_weight,tbone.bb_reps as bb_reps,log.complete as complete 
from 
(select ent._id as _id,ent.entry_id as entry_id,ent.entry_type as entry_type, ent.entry_order as entry_order,ent.sets as sets,ent.time as time, ex.title as title, ex.description as description, ex.img_male as img_male, ex.img_female as img_female, ex.mode as mode,ex.level as level,ex.en_weight as en_weight,ex.en_reps as en_reps,ex.bb_weight as bb_weight,ex.bb_reps as bb_reps from Workout_entry as ent left join Exercise as ex on ent.entry_id=ex._id where ent.workout_id=1 order by ent.entry_order) as tbone 
left join  
(select entry_id, complete from Workout_log where mode='W' and datetime=1378364400000 and workout_id=1) as log on  tbone.entry_id=log.entry_id;
*/
