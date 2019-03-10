package com.olmatech.fitness.music;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.main.Common;

public class PlaylistActivity extends BaseMusicPlayerActivity implements
	android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
	final private static String TAG="PlaylistActivity";
	
	//private final static int DLG_REASON_NOPALYLIST=1;
	private final static int DLG_REASON_ERROR=2;
	
	private SimpleCursorAdapter listAdapter;
	
	protected LoaderManager mManager;
	protected int LOADER_ID=-1;
	 protected ListView list;	 
	 private long playListId=-1;
	 private String playListTitle;
	 
	 final static String[] PROJ = new String[]{MediaStore.Audio.Playlists.NAME};

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_playlist);
		
		boolean canStart = true;
		try
		{
			doBindService();	
		}
		catch(Exception ex)
		{
			canStart=false;
		}
		if(!canStart)
		{
			this.showErrorDlg(R.string.err_start_music_server, Common.REASON_FATAL_ERROR);
			return;
		}
		Log.d(TAG,"playListId="+ playListId);
        
        mManager = this.getSupportLoaderManager();
        list = (ListView)this.findViewById(R.id.list_playlist);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        listAdapter = new SimpleCursorAdapter(
                this,
                //R.layout.list_item_playlist,
                android.R.layout.simple_list_item_checked, 
                null,
                PROJ, 
                new int[] {android.R.id.text1},
               //new int[]{R.id.txt},
                0)
        {

			@Override
			public void bindView(View view, Context context, Cursor cursor) {				
				super.bindView(view, context, cursor);
				//check if we have id in the list - and set checked
				
				int ind = cursor.getColumnIndex(MediaStore.Audio.Playlists._ID);
				
				long songId = cursor.getLong(ind);			
				CheckedTextView tv = (CheckedTextView)view;
				//Log.d(TAG, "bindView songId="+ songId + "  playListId="+playListId);
				int position = cursor.getPosition();
				if(playListId == songId)
				{
					tv.setChecked(true);					
					list.setItemChecked(position, true);
					//Log.d(TAG, "bindView songId - checked");
				}
				else{
					tv.setChecked(false);					
					list.setItemChecked(position, false);
				}
			
				view.setTag(Long.valueOf(songId));
			}
        	
        }; 
        
        
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				CheckedTextView tv = (CheckedTextView)view; //.findViewById(R.id.txt);
				
				Long song;
				try
				{
					song = (Long)view.getTag();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();					
					return;
				}		
				
				//Log.d(TAG, "onItemClick song="+song.longValue()+ " checked="+tv.isChecked());
							
				if(tv.isChecked())
				{
					playListId=-1;	
					playListTitle=null;
					list.setItemChecked(position, false);
					tv.setChecked(false);
				}
				else
				{
					playListId = song.longValue();
					playListTitle = (String) tv.getText();
					list.setItemChecked(position, true);
					tv.setChecked(true);
				}					
			}        	
        });      
        
	}
	
	private void showFatalErr()
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showErrorDlg(R.string.err_processing, Common.REASON_FATAL_ERROR);				
			}			
		});
	}
	
	@Override
	protected void processOnServiceConencted() {
		if(mBoundService == null)
		{
			showFatalErr();
			return;
		}
		
		playListId = mBoundService.getPlayListId();
		fillData();
	}



	//Fill list with data - Sevice connected
	private void fillData()
	{
		if(mManager == null || list == null)
		{
			this.showErrorDlg(R.string.err_processing, DLG_REASON_ERROR);
			return;
		}		
		
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
				this.showErrorDlg(R.string.err_processing, DLG_REASON_ERROR);
				return;
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
				this.showErrorDlg(R.string.err_processing, DLG_REASON_ERROR);
				return;
			}		
		}
		
		list.setAdapter(listAdapter);
	}
	
	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.but_close:
			saveAndClose();
			break;
		case R.id.but_cancel:
			this.setResult(Common.RESULT_CANCEL);
			finish();
			break;
//		case R.id.but_clear:
//			clearPlaylist();
//			break;
		default: break;
		}
	}
	
	//TODO - move to task
	private void saveAndClose()
	{
		if(mBoundService == null)
		{
			showFatalErr();
			return;
		}	
		
		if(playListId<=0){
			//No selected playlist - we will clear palylist and exit
			setPalylist(playListId, null, true);
			this.setResult(Common.RESULT_OK);
			finish();
			return;
			
		}
		
		
		final boolean haveoldList = mBoundService.havePlaylist();
		final long oldListId = (haveoldList)? mBoundService.getPlayListId() : -1;
		final String oldListTitle = (haveoldList)? mBoundService.getPlaylistTitle() : null;
		
		mBoundService.clearPlayList();		
		boolean result = setPalylist(playListId, playListTitle, true);//No play list id=-1 - TODO	
		if(!result)
		{
			//we tried to save valid playlist and could not
			mBoundService.clearPlayList();
			boolean res2 = (haveoldList)? setPalylist(oldListId, oldListTitle, true) : false;
			showErrorDlg(R.string.err_load_playlist, Common.REASON_NOT_IMPORTANT);			
		}
		else
		{
			if(!mBoundService.havePlaylist())
			{
				mBoundService.clearPlayList(); //we will load the "old one" if cancel
				this.showErrorDlg(R.string.msg_no_songs, Common.REASON_NOT_IMPORTANT);///////////////////////////////
				
			}
			else
			{
				this.setResult(Common.RESULT_OK);
				finish();
			}			
		}			
		
	}
	
//	private void clearPlaylist()
//	{
//		if(mBoundService != null) mBoundService.clearPlayList();
//		
//		list.invalidateViews();
//	}
		
	private void showError()
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				showErrorDlg(R.string.err_processing, DLG_REASON_ERROR);
			}
			
		});
	}
	/**
	 * We have null or empty cursor - check for SD card
	 */
	private void processNoPlaylist()
	{
		boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		TextView tv = (TextView)this.findViewById(R.id.txt_subtitle);
		if(isSDPresent)
		{
			tv.setText(R.string.msg_no_playlist);
		}
		else
		{
			tv.setText(R.string.msg_no_card);			
		}
	}

	@Override
	public void processErrDlgClose(int reason) {
		if(reason == DLG_REASON_ERROR)
		{
			 this.setResult(Common.RESULT_CANCEL);
	         finish();
	         return;
		}
		super.processErrDlgClose(reason);
	}

	@Override
	protected void onDestroy() {
		abandonLoader();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		stopLoader();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public int getId() {		
		return Common.ACT_PlaylistActivity;
	}


	//////////// LOADER
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle b) {
		
		Loader<Cursor> ld;
		try
		{
			ld = new CursorLoader(
	                this,
	                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,  
	                new String[]{MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME},
	                null,
	                null,
	                null);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		return ld;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> ld, Cursor cursor) {
		final int id = ld.getId();
		if(id==LOADER_ID)
		{
			if(listAdapter != null)
			{
				if(cursor == null || cursor.getCount()==0)
				{
					showProgress(false);	
					processNoPlaylist();
					return;
				}
				else
				{
					try
					{
						listAdapter.changeCursor(cursor);	
					}
					catch(Exception e)
					{
						e.printStackTrace();
						showError();
					}					
				}
				
			}
			else
			{
				showError();
			}
			
		}
		showProgress(false);	
		
		
	}
	
	private void showProgress(final boolean show)
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				View progr = findViewById(R.id.progrBar);
				if(progr == null) return;
				if(show)
				{
					if(progr.getVisibility() != View.VISIBLE) progr.setVisibility(View.VISIBLE);					
				}
				else
				{
					if(progr.getVisibility() == View.VISIBLE) progr.setVisibility(View.GONE);	
				}
				
			}
			
		});
	}




	@Override
	public void onLoaderReset(Loader<Cursor> ld) {
		final int id = ld.getId();
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
	
	private void stopLoader()
	{
		if(mManager == null) return;		
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

	private void abandonLoader()
	{
		if(mManager == null) return;		
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
	
	
	
	

}
