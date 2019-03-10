package com.olmatech.fitness.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.olmatech.fitness.main.Common;


public class MusicPlayerService extends Service implements 
	MediaPlayer.OnPreparedListener,
	MediaPlayer.OnErrorListener,
	MediaPlayer.OnCompletionListener,
	AudioManager.OnAudioFocusChangeListener{
	
	private final static String TAG="MusicPlayerService";
	
	private static MusicPlayerService instance = null;
	
	private static List<String> playList = new ArrayList<String>();
	private static long playListId=-1;
	private static MediaPlayer player = null;
	private int currentSong =0;
	private String playListTitle;
	
	//phone call listener
//	private PhoneListener phListener;
//	private OutgoingReceiver phReceiver;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		 // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
	}
	
	@Override
	public void onCreate() {		
		super.onCreate();
//		phListener = new PhoneListener();
//		try
//		{
//			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//	        telephonyManager.listen(phListener, PhoneStateListener.LISTEN_CALL_STATE);
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//			phListener=null;
//		}
		
        
//        phReceiver = new OutgoingReceiver();
//        phReceiver.setService(this);
//        try
//		{
//        	  IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
//              this.registerReceiver(phReceiver, intentFilter);
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//			phReceiver=null;
//		}
      
        
	}




	@Override
	public void onLowMemory() {
		releasePlayer();
		super.onLowMemory();
	}


	private void initPlayer(String path){
		player = new MediaPlayer();
		player.setOnPreparedListener(this);
		player.setOnErrorListener(this);
		player.setOnCompletionListener(this);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		boolean ready = false;
		try {
			//player.setDataSource(getApplicationContext(), contentUri);
			player.setDataSource(path);
			ready = true;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //TODO
		if(ready)
		{
			player.prepareAsync(); 
		}
		else
		{
			releasePlayer();
		}
	}


	@Override
	public void onPrepared(MediaPlayer mp) {
		if(mp != null)
		{
			mp.start();
		}
	}
	
//	public void setSongsList(final List<Long> list)
//	{
//		boolean playing;
//		if(isPalying())
//		{
//			player.stop();
//			playing = true;
//		}
//		else
//		{
//			playing = false;
//		}
//		if(playList != null)
//		{
//			playList.clear();
//			playList = null;
//		}
//		if(list == null || list.size() ==0)
//		{
//			return;
//		}
//		playList = new ArrayList<Long>(list.size());
//		for(Long val: list)
//		{
//			playList.add(val);
//		}		
//		currentSong = 0;
//		if(playing)
//		{
//			play();
//		}
//	}
	
	public void play()
	{
		if(player != null )
		{
			if(!player.isPlaying()) player.start();
		}
		else
		{
			if(playList != null && playList.size() >0)
			{
				this.currentSong =0;
				this.initPlayer(playList.get(0));
			}
			
		}
			
	}
	
	public void playSongFromPlaylist()
	{		
		if(playList == null) return;	
		int sz = playList.size();
		if(sz == 0) return;
		if(currentSong <0 || currentSong >= sz)
		{
			currentSong = 0;
		}
		String path =playList.get(currentSong);
		
		
		if(path != null)
		{
			try
			{
							
				if(player == null)
				{
					initPlayer(path);
				}				
				else
				{
					if(isPalying())
					{
						player.stop();
					}
					boolean ready = false;
					try {
						//player.setDataSource(getApplicationContext(), contentUri);
						player.setDataSource(path);
						ready = true;
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  //TODO
					if(ready)
					{
						player.prepareAsync(); 
					}
					else
					{
						releasePlayer();
						initPlayer(path);
					}
								     
				}
				
			}
			catch(Exception ex)
			{				
				ex.printStackTrace();
				
			}
		}			
	}
	
	
	
	public static boolean isPalying()
	{
		if( player == null)
		{
			return false;
		}
		else
		{
			boolean playing = false;
			try
			{
				playing = player.isPlaying();
			}
			catch(Exception ex)
			{
				playing = false;
			}
			return playing;
		}
	}
	
//	public void playSong(final String path)
//	{	
////		Uri contentUri = ContentUris.withAppendedId(
////		        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
//		if(player != null)
//		{
//			if( isPalying()) player.stop();
//			try
//			{
//				
//				player.setDataSource(getApplicationContext(), contentUri);
//			}
//			catch(Exception ex)
//			{
//				
//			}
//		}
//		else
//		{
//			initPlayer(contentUri);
//		}
//		
//	}
//	
	public void pause() {
		if(isPalying())
		{
			player.pause();
		}		
				
	}
	
	public void stop() {
		if(isPalying())
		{
			player.stop();
		}		
	}	
	
	private void playNextSong()
	{		
		currentSong++;
		playSongFromPlaylist();
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		playNextSong();
		
	}
	
	@Override
	public void onDestroy() {
		releasePlayer();
		if(playList != null)
		{
			playList.clear();
			playList=null;
		}	
		
//		if(phListener != null)
//		{
//			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//			telephonyManager.listen(phListener, PhoneStateListener.LISTEN_NONE);
//		}
		
//		if(phReceiver != null)
//		{
//			this.unregisterReceiver(phReceiver);
//		}
		
		instance = null;
		super.onDestroy();
	}
	
	private void releasePlayer()
	{
		if (player != null){
			try
			{
				if(isPalying())
				{
					player.stop();
				}
				player.release();
			}
			catch(Exception ex)
			{
				
			}			
			player = null;
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if(Common.DEBUG) Log.d(TAG, "error " + what + " extra " + extra);
		return false;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN:
            // resume playback
            if (player == null){
            	if(playList != null && playList.size() >0)
            	{
            		if(currentSong < 0 || currentSong >= playList.size())
                	{
                		this.currentSong =0;
                	}            	
                	initPlayer(playList.get(currentSong));
            	}            	
            }
            else if (!player.isPlaying()){
            	player.start();  // TODO
            }
//            player.setVolume(1.0f, 1.0f);
            break;

        case AudioManager.AUDIOFOCUS_LOSS:
            // Lost focus for an unbounded amount of time: stop playback and release media player
            if (player.isPlaying()) player.stop();
            player.release();
            player = null;
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            // Lost focus for a short time, but we have to stop
            // playback. We don't release the media player because playback
            // is likely to resume
            if (player.isPlaying()) player.pause();
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            // Lost focus for a short time, but it's ok to keep playing
            // at an attenuated level
            if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
            break;
		}
		
	}
	
	

	/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	public MusicPlayerService getService() {
    		if(instance == null)
    		{
    			instance = MusicPlayerService.this;
    		}
            return instance;
        }
    }
    
    public static boolean isInstanceCreated()
    {
    	return instance != null; 
    }


    private final IBinder mBinder = new LocalBinder();
    
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	


		//ADDING SONGS
	//Music
//		public List<Long> getPalyList()
//		{
//			return  playList;
//		}
		
		public long getPlayListId()
		{
			return playListId;
		}
		public void setPlayListId(final long val)
		{
			playListId = val;
		}
		
		public boolean havePlaylist()
		{
			return (playList != null && playList.size() >0)? true : false;
		}
		
		public void addToPlayList(final String song)
		{
			if(playList == null)
			{
				playList = new ArrayList<String>();
			}
			
			if(!playList.contains(song))
			{
				playList.add(song);
			}
		}
		
		public void setPlayListTitle(final String t)
		{
			playListTitle=t;
		}
		
		public String getPlaylistTitle()
		{
			return playListTitle;
		}
		
		
		public void removeFromPlayList(final String song)
		{		
			if(playList !=  null && playList.contains(song))
			{
				playList.remove(song);
			}
		}
		
		public void clearPlayList()
		{
			if(playList !=  null)
			{
				playList.clear();
				playList = null;
			}
			this.playListTitle= null;
			playListId=-1;
		}
		
		public boolean haveSong(final String song)
		{
			if(playList == null) return false;		
			return (playList.contains(song));
		}
		
		public String getFirstSongPath()
		{
			if(playList == null || playList.size() ==0) return null;
			
			return playList.get(0);
		}

		///// PHONE calls
	private class PhoneListener extends PhoneStateListener
	{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state)
			{
			case TelephonyManager.CALL_STATE_RINGING:
		          // called when someone is ringing to this phone
				pause();
			break;
			case TelephonyManager.CALL_STATE_IDLE:
				play();
				break;
			}
		}		
	}
	
	

}
