package com.olmatech.fitness.music;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.ui.BaseFragmentActivity;

public abstract class BaseMusicPlayerActivity extends BaseFragmentActivity{
	
//	private final static String TAG="BaseMusicPlayerActivity";
	
	protected MusicPlayerService mBoundService;	
	protected boolean mIsBound = false;
	
	
	@Override
	protected void onDestroy() {
		doUnbindService();
		super.onDestroy();
		
	}
	
	protected void processOnServiceConencted(){}
	
	//SERVICE
		private ServiceConnection mConnection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className, IBinder service) {
		          mBoundService = ((MusicPlayerService.LocalBinder)service).getService();
		          processOnServiceConencted();
		          //check for music
		      	
		    }

		    public void onServiceDisconnected(ComponentName className) {
		        // This is called when the connection with the service has been
		        // unexpectedly disconnected -- that is, its process crashed.
		        // Because it is running in our same process, we should never
		        // see this happen.
		        mBoundService = null;
		       
		    }    
		    
		};
		
		protected void doBindService() throws SecurityException {
		    // Establish a connection with the service.  We use an explicit
		    // class name because we want a specific service implementation that
		    // we know will be running in our own process (and thus won't be
		    // supporting component replacement by other applications).
			mIsBound = bindService(new Intent(BaseMusicPlayerActivity.this, MusicPlayerService.class), 
		    		mConnection, Context.BIND_AUTO_CREATE);							
			
		}
		
		protected void doStartMusicService() throws SecurityException{
			Intent intent = new Intent(BaseMusicPlayerActivity.this, MusicPlayerService.class);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			ComponentName  cmp = startService(intent);
			
			if(cmp == null)
			{
				mIsBound = false;
			}
			else{
				mIsBound = true;				
			}
		}

		protected void doUnbindService() {
		    if (mIsBound && mConnection != null) 
		    {
		    	try
		    	{
		    		unbindService(mConnection);
		    	}
		    	catch(Exception ex)
		    	{
		    		ex.printStackTrace();
		    	}
		        // Detach our existing connection.		    	
		        
		        mIsBound = false;        
		       
		    }
		}
	
		//add data
		protected boolean setPalylist(final long listId, final String listTitle, final boolean saveToDs)
		{
			boolean result = true;
			if(listId >=0)
			{
				//get all songs for the palylist
				
				final ContentResolver resolver = this.getContentResolver();
		        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", listId);
		        final String dataKey = MediaStore.Audio.Media.DATA;
		        Cursor tracks = resolver.query(uri, new String[] { dataKey }, null, null, null);
		        if (tracks != null)
		        {
		        	final int dataIndex = tracks.getColumnIndex(dataKey);		        	
		        	while(tracks.moveToNext())
		        	{
		        		final String dataPath = tracks.getString(dataIndex);
		        		mBoundService.addToPlayList(dataPath);
		        	}
		        	tracks.close();
		        	
		        	mBoundService.setPlayListTitle(listTitle);
		        	mBoundService.setPlayListId(listId);
		        }
		        else
		        {
		        	mBoundService.clearPlayList();		   
		        	result = false;
		        }
				
			}
			else
			{
				mBoundService.clearPlayList();				
			}
			
			
			if(saveToDs && result)
			{
				DataStore ds = new DataStore(this);
				ds.setPlayListId(listId);
				ds.setPlaylistTitle(listTitle);
			}
			
			return result;
		}
	

}
