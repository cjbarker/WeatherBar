package com.cjbarker.wb.receiver;

import com.cjbarker.wb.Prefs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Connection extends BroadcastReceiver {
	
	private static final String TAG = "Connection";
	
	private boolean isConnected;
	private boolean isWiFi;

	public static boolean isNetworkConnected(Context ctx) {
		if (ctx == null) {
			return false;
		}
		ConnectivityManager cm =
		        (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return ( activeNetwork != null && activeNetwork.isConnectedOrConnecting() );
	}
	
	public boolean isWiFiConnection() {
		return isWiFi;
	}
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.i(TAG, "Intent received: " + intent.getAction());
		
		isConnected = isNetworkConnected(ctx);
		
		if (isConnected) {
			ConnectivityManager cm =
			        (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		}
		else {
			isWiFi = false;
		}
		
		// okay - no battery drain searching for radio
		if (isWiFi) {
			Prefs.getInstance().setOkayToQuery(true);
		}
	}
}
