package com.olmatech.fitness.music;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.olmatech.fitness.R;
import com.olmatech.fitness.db.DataStore;
import com.olmatech.fitness.main.Common;

public class PlayMusicActivity extends BaseMusicPlayerActivity{
	private final static String TAG = "PlayMusicActivity";
	
	//private final static int DLG_REASON_PLAYLIST=1;
	
	private boolean msgShown= false;
	boolean playing = false;
	private SeekBar seeker;
	private int musicVol;
	private boolean canStart = true;

	@Override
	protected void onCreate(Bundle b) {		
		super.onCreate(b);
		setContentView(R.layout.dlg_music);		
		if(b != null)
		{
			if(b.containsKey("msg"))
			{
				msgShown = b.getBoolean("msg");
			}
		}
		seeker = (SeekBar)this.findViewById(R.id.music_vol);
		AudioManager audioManager = (AudioManager) getSystemService( Context.AUDIO_SERVICE );
		int maxInd = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		seeker.setMax(maxInd);
		
		DataStore ds = new DataStore(this);
		musicVol = ds.getMusicVol();
		if(musicVol <0)
		{
			musicVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			ds.setMusicVolume(musicVol);
		}
		else
		{
			audioManager.setStreamVolume( AudioManager.STREAM_MUSIC, musicVol, 0 );
		}
		seeker.setProgress(musicVol);
		seeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seek, int progr, boolean user) {					
				setMusicVol(progr,true);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
		ImageView imgPlay = (ImageView)this.findViewById(R.id.img_play);
		
		if(MusicPlayerService.isInstanceCreated() && MusicPlayerService.isPalying())
		{
			imgPlay.setImageResource(R.drawable.pause);
		}
		
		imgPlay.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if(ev.getAction() != MotionEvent.ACTION_UP) return true;				
				togglePlayingMusic();
				return true;				
			}
			
		});
		
		
		if(canStart)
		{
			try
			{
				doStartMusicService();	
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
		}
		
	}
	
	
	//Volume
		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			int action = event.getAction();
		    int keyCode = event.getKeyCode();
		    switch (keyCode) {
	        case KeyEvent.KEYCODE_VOLUME_UP:
	            if (action == KeyEvent.ACTION_DOWN) {
	            	processVolKey(true);   
	            }
	            return true;
	        case KeyEvent.KEYCODE_VOLUME_DOWN:
	            if (action == KeyEvent.ACTION_DOWN) {
	            	processVolKey(false);
	            }
	            return true;
	        default:
	            return super.dispatchKeyEvent(event);
		    }    
		    
		}
		
		private void processVolKey(final boolean up)
		{
			if(up)
			{
				setMusicVol(musicVol+1, true);
			}
			else
			{
				setMusicVol(musicVol-1,true);
			}		
			
		}
		
//		private int getMusicVol()
//		{
//			AudioManager audioManager = (AudioManager) getSystemService( Context.AUDIO_SERVICE );
//			return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		}
		
		private void setMusicVol(final int vol, final boolean setSeeker)
		{	
			AudioManager audioManager = (AudioManager) getSystemService( Context.AUDIO_SERVICE );
			final int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			musicVol = vol;
			if(musicVol > maxVol)
			{
				musicVol= maxVol;
			}
			else if(musicVol <0)
			{
				musicVol =0;
			}						
			
			audioManager.setStreamVolume( AudioManager.STREAM_MUSIC, musicVol, 0 );
			
			if(setSeeker && seeker != null)
			{
				seeker.setProgress(musicVol);
			}
			 DataStore ds = new DataStore(this);
			 ds.setMusicVolume(musicVol);
		}
	
	
	



	private void togglePlayingMusic()
	{
		if(mBoundService == null || !mBoundService.havePlaylist()){
			
			return;
		}
		ImageView imgPlay = (ImageView)this.findViewById(R.id.img_play);
		
		if(playing)
		{
			mBoundService.pause();
			playing = false;
			imgPlay.setImageResource(R.drawable.play);
		}
		else
		{
			mBoundService.play();
			playing = true;
			imgPlay.setImageResource(R.drawable.pause);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("msg", msgShown);
		super.onSaveInstanceState(outState);
	}
	
	public void onButtonClick(View view)
	{
		switch(view.getId())
		{
		case R.id.but_load:
			showPlaylist();
			break;
		case R.id.but_close:
			this.setResult(Common.RESULT_OK);
			finish();
			break;
		}
	}
	
	
	@Override
	protected void processOnServiceConencted() {
		if(mBoundService == null)
		{				
			this.showErrorDlg(R.string.msg_no_music, Common.REASON_FATAL_ERROR);
			//this.showMsgDlg(R.string.msg_no_music, Common.REASON_NOT_IMPORTANT);
			return;
		}
		
		playing =MusicPlayerService.isPalying();
		ImageView imgPlay = (ImageView)this.findViewById(R.id.img_play);
		if(!mBoundService.havePlaylist())
  		{	
			//check
			DataStore ds = new DataStore(this);
			long listId = ds.getPlaylistId();
			boolean haveList = false;
			String listTitle=null;
			if(listId >=0)
			{
				listTitle = ds.getPlaylistTitle();
				haveList = setPalylist(listId, listTitle, false);				
			}
			
			if(haveList)
			{
				imgPlay.setVisibility(View.VISIBLE);
				setPlaylistTitle(listTitle);
			}
			else
			{
				imgPlay.setVisibility(View.INVISIBLE);
	  			if(!msgShown) showNoPlayListMsg();
	  			return;
			}			
  		}
		else
		{
			setPlaylistTitle(mBoundService.getPlaylistTitle());
			imgPlay.setVisibility(View.VISIBLE);
		}
	}
	
	private void setPlaylistTitle(final String tlt)
	{
		TextView tv = (TextView)this.findViewById(R.id.music_subtitle);
		if(tlt != null) tv.setText(tlt);
		else tv.setText(R.string.msg_playlist);
	}

//	@Override
//	public void processErrDlgClose(int reason) {
//		if(reason == DLG_REASON_PLAYLIST)
//		{
//			showPlaylist();
//			return;
//		}
//		super.processErrDlgClose(reason);
//	}

	private void showPlaylist()
	{	
		//stop music if playing
		if(playing)
		{
			if(mBoundService != null && this.mIsBound) mBoundService.stop();
			playing = false;
			ImageView imgPlay = (ImageView)this.findViewById(R.id.img_play);
			imgPlay.setImageResource(R.drawable.play);
		}
		Intent intent = new Intent(this, PlaylistActivity.class);
		this.startActivityForResult(intent, Common.ACT_PlaylistActivity);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case Common.ACT_PlaylistActivity:
			switch(resultCode)
			{
			case Common.RESULT_OK:
				//ok
				//TODO - check if we have playlist or cleared it
				DataStore ds = new DataStore(this);
				long listId = ds.getPlaylistId();
				ImageView imgPlay = (ImageView)this.findViewById(R.id.img_play);
				if(listId>0){
					imgPlay.setVisibility(View.VISIBLE);
					String listTitle= ds.getPlaylistTitle();
					setPlaylistTitle(listTitle);
				}
				else{
					imgPlay.setVisibility(View.INVISIBLE);
					setPlaylistTitle(null);
		  			if(!msgShown) showNoPlayListMsg();		  			
				}
				
				break;
			case Common.RESULT_ERROR:
				this.showErrorDlg(R.string.err_load_playlist, Common.REASON_NOT_IMPORTANT);
				break;
			default: break;
			}
			
//			if(mBoundService != null && !mBoundService.havePlaylist())
//			{
//				this.showMsgDlg(R.string.msg_no_music, Common.REASON_NOT_IMPORTANT);
//			}
//			else
//			{
//				
//			}
			break;
		default: super.onActivityResult(requestCode, resultCode, data);
			break;
		}
		
	}



	@Override
	protected void onPause() {
		doUnbindService();
		super.onPause();
	}

	@Override
	protected void onResume() {	
		super.onResume();
		if(canStart)
		{
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
		}	
		
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!playing)
		{
			try
			{
				stopService(new Intent(this, MusicPlayerService.class));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
	}

	@Override
	public int getId() {		
		return Common.ACT_PlayMusicActivity;
	}
	
	
	private void showNoPlayListMsg()
	{
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				msgShown = true;
				showMsgDlg(R.string.msg_no_music, Common.REASON_NOT_IMPORTANT);			
			}
			
		});
		
	}


}
