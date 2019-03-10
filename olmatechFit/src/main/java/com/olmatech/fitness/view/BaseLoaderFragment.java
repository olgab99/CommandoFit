package com.olmatech.fitness.view;

//base class for all fragments that are using Loader
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.olmatech.fitness.interfaces.IProcessLoaded;
import com.olmatech.fitness.main.Common;
import com.olmatech.fitness.main.Workout.WorkoutMode;
import com.olmatech.fitness.ui.BaseFragmentActivity;

public abstract class BaseLoaderFragment extends BaseFragment 
	implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
	
	private final static String TAG = "BaseLoaderFragment";
	protected IProcessLoaded mListener;
	protected LoaderManager mManager;
	protected int LOADER_ID;
	
    //protected CurProgram progr;
    protected BaseFragmentActivity parentActivity;
    
    protected int fragmentId;
    protected boolean haveProgress = false;
    protected boolean showProgress = false;
    protected int progressBarId=-1;
    
    protected ListView list;
    
    protected WorkoutMode viewMode = WorkoutMode.Unknown; //either All or by type
    
    protected boolean loadDone = false;
    protected boolean showContenetMenu= false;
    protected boolean doItemClick = true;
    protected PopupWindow pw;	
        
   public void setFragmentId(final int id)
   {
	   fragmentId=id;
   }
   
   public void setViewMode(final WorkoutMode md)
	{
		viewMode = md;
	}
   
   abstract void setHaveProgress();
   
   public void setShowLoadProgess(final boolean val){ showProgress = val; }
   
   public void setShowContentMenu(final boolean val) { 
	   if(showContenetMenu != val)
	   {
		   if(showContenetMenu)
		   {
			   //we hav to de-register
			   this.unregisterForContextMenu(list);
			   list.setLongClickable(false);
		   }
		   else
		   {
			   list.setLongClickable(true);
			   this.registerForContextMenu(list);			   
		   }
		   showContenetMenu = val; 
	   }
	   
   }	
	public void setDoItemClick(final boolean show) { doItemClick = show; }
   
   public int getFragmentId(){ return fragmentId;}
   
   public void setLoaderId(final int id){ LOADER_ID=id; }
   
   protected void showErrorDlg(final int msgResourceId)
	{
		if(parentActivity != null ){
			parentActivity.showErrorDlg(msgResourceId, Common.REASON_NOT_IMPORTANT);							
		}		
	}

@Override
public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
	loadDone = true;
	
}

public boolean getDoneLoading(){ return loadDone; }

@Override
public void onLoaderReset(Loader<Cursor> arg0) {
	// TODO Auto-generated method stub
	loadDone = false;
}

public void reloadData()
{
	if(mManager == null) return;
	loadDone=false;	
	mManager.restartLoader(LOADER_ID, null, this);	
}

protected void startLoader()
{
	if(mManager == null) return;
	loadDone=false;
	if(mManager.getLoader(LOADER_ID) != null && !mManager.getLoader(LOADER_ID).isReset())
	{
		try
		{
			mManager.restartLoader(LOADER_ID, null, this);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Error startLoader" );
		}
		
	}
	else 
	{
		try
		{
			mManager.initLoader(LOADER_ID, null, this);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Error startLoader 2" );
		}		
	}
}

protected void stopLoader()
{
	if(mManager == null) return;
	loadDone = false;
	if(mManager.getLoader(LOADER_ID) != null && mManager.getLoader(LOADER_ID).isStarted())
	{
		try
		{
			mManager.getLoader(LOADER_ID).stopLoading();	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Error stopLoader" );
		}			
	}
}

protected void abandonLoader()
{
	if(mManager == null) return;
	loadDone = false;
	if(mManager.getLoader(LOADER_ID) != null)
	{
		try
		{
			if(mManager.getLoader(LOADER_ID).isStarted()) mManager.getLoader(LOADER_ID).stopLoading();		
			mManager.getLoader(LOADER_ID).abandon();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Error abandonLoader" );
		}		
	}
}


@Override
public void onAttach(Activity activity) {	
	super.onAttach(activity);
	try {
		mListener = (IProcessLoaded)activity;
    } catch (ClassCastException e) {
        throw new ClassCastException(activity.toString() + " must implement IProcessLoaded");
    }
}

protected void showProgress(final boolean show)
{
	if(parentActivity == null || !haveProgress || progressBarId <0) return;
	parentActivity.runOnUiThread(new Runnable(){

		@Override
		public void run() {
			View progrView = parentActivity.findViewById(progressBarId);
			if(progrView != null)
			{
				if(show) progrView.setVisibility(View.VISIBLE);
				else progrView.setVisibility(View.GONE);
			}
			
		}
		
	});
	
}// progress

//fragment removed / replaced
	@Override
	public void onPause() {
		//cancel loader
		stopLoader();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		abandonLoader();
		super.onDestroy();
	}   

}
