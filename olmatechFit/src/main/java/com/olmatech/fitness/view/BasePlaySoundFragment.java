package com.olmatech.fitness.view;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasePlaySoundFragment extends BaseFragment{
	
	//sounds	
	protected boolean playSoundOnTimeUp = false;
	protected Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    protected Ringtone ringTone;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void setPlaySoundOnTimeUp(final boolean val)
	{
		if(playSoundOnTimeUp != val)
		{
			if(val)
			{
				if(ringTone == null)
				{
					setRingtone();
				}
				
			}
			else
			{
				if(ringTone != null)
				{
					if(ringTone.isPlaying()) ringTone.stop();
					ringTone=null;
				}
			}
			playSoundOnTimeUp=val;
		}
	}
	
	protected void playSound()
	{
		if(ringTone == null && playSoundOnTimeUp) setRingtone();
		if(ringTone != null)
		{
			try{				
				ringTone.play();
			}catch (Exception e) {}
			
		}
	}
	
	private void setRingtone()
	{
		Activity act = getActivity();
		if(act == null) return;
		ringTone = RingtoneManager.getRingtone(act.getApplicationContext(), notification);
	}

	@Override
	public void onDestroy() {
		if(ringTone != null)
		{
			if(ringTone.isPlaying()) ringTone.stop();
			ringTone=null;
		}
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		if(ringTone != null)
		{
			if(ringTone.isPlaying()) ringTone.stop();
			ringTone=null;
		}
		super.onDetach();
	}

	@Override
	public void onPause() {
		if(ringTone != null)
		{
			if(ringTone.isPlaying()) ringTone.stop();
			ringTone=null;
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		if(playSoundOnTimeUp && ringTone==null)
		{
			setRingtone();
		}
		super.onResume();
	}

	@Override
	public void onStart() {
		if(playSoundOnTimeUp && ringTone==null)
		{
			setRingtone();
		}
		super.onStart();
	}

	@Override
	public void onStop() {
		if(ringTone != null)
		{
			if(ringTone.isPlaying()) ringTone.stop();
			ringTone=null;
		}
		super.onStop();
	}
	
//	public void playSound() throws IllegalArgumentException, SecurityException, IllegalStateException,  IOException {
//		
//		Activity act = getActivity();
//		if(act == null) return;
//		 
//		mMediaPlayer.setDataSource(act, soundUri);
//		final AudioManager audioManager = (AudioManager) act.getSystemService(Context.AUDIO_SERVICE);
//		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//		    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//		    mMediaPlayer.setLooping(true);
//		    mMediaPlayer.prepare();
//		    mMediaPlayer.start();
//		}
//}
	
	
	

}
