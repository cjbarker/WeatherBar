package com.cjbarker.wb.receiver;

import com.cjbarker.wb.Prefs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Screen extends BroadcastReceiver {
	
	private static final String TAG = "Screen";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.i(TAG, "Intent received: " + intent.getAction());
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			screenOn();
		}
		else {
			screenOff();
		}
	}
	
	private void screenOff() {
		Log.d(TAG, "Screen turned off...");
	}
	
	private void screenOn() {
		Log.d(TAG, "Screen turned on - will refresh data accordingly");
		if (Prefs.getInstance().isOkayToQuery() && Prefs.getInstance().isUpdateExpired()) {
			;;	// TODO call someone to kickoff weather update and display 
		}
	}
}
