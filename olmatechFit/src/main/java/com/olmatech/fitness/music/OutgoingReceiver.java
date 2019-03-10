package com.olmatech.fitness.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OutgoingReceiver extends BroadcastReceiver {
	private MusicPlayerService serv;
	public OutgoingReceiver() {
    }

    public void setService(final MusicPlayerService srv) {
    	serv = srv;
    }
  
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if(serv != null) serv.pause();
		
	}
}
