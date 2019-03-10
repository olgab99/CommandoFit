package com.olmatech.fitness.music;

import android.content.Context;
import android.content.Intent;

public class MusicIntentReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
      // signal your service to stop playback
      // (via an Intent, for instance)
			try
			{
				ctx.stopService(new Intent(ctx, MusicPlayerService.class));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
					
			}
			
  }
		
	}

}
