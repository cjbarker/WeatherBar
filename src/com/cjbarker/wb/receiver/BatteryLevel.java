package com.cjbarker.wb.receiver;

import com.cjbarker.wb.Prefs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryLevel extends BroadcastReceiver {
	
	private static final String TAG = "BatteryLevel";
	
	private boolean isBatteryLow;
	private boolean isCharging;
	private boolean usbCharge;
	private boolean acCharge;

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.i(TAG, "Intent received: " + intent.getAction());
		
		if (intent.getAction().equals(Intent.ACTION_BATTERY_OKAY) ||
			intent.getAction().equals(Intent.ACTION_BATTERY_LOW) ) {
			this.getBatteryState(intent);
		}
		else {
			this.getChargingState(intent);
		}
		
		Prefs.getInstance().setBatteryOkay(isBatteryOkay());
	}
	
	/**
	 * Denotes if battery power is low via Android broadcast
	 * @return	True if battery is low 
	 */
	public boolean isBatteryLow() {
		return isBatteryLow;
	}
	
	/**
	 * Okay to run/use application b/c battery is okay.
	 * Meaning that it has a full charge, is charing, or in okay state via Android.
	 * 
	 * @return	True if battery is okay else false
	 */
	public boolean isBatteryOkay() {
		return (isBatteryLow == false || isCharging || usbCharge || acCharge);
	}
	
	/**
	 * Handles battery power intent for determining the charge state
	 * 
	 * @param intent	Broadcast Intent
	 */
	private void getChargingState(Intent intent) {
		if (intent == null) {
			return;
		}
		Log.d(TAG, "Loading charging state...");
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
	}
	
	/**
	 * Handles battery state intent 
	 * 
	 * @param intent	Broadcast Intent
	 */
	private void getBatteryState(Intent intent) {
		if (intent == null) {
			return;
		}
		Log.d(TAG, "Loading battery state...");
		isBatteryLow = (intent.getAction().equals(Intent.ACTION_BATTERY_LOW) ? true : false);
	}
} 