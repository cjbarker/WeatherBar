package com.cjbarker.wb;

import com.cjbarker.wb.receiver.Connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class Prefs {
	
	private static Prefs SINGLETON = null;
	private static final String TAG = "Prefs";
	
	private Context ctx;
	private SharedPreferences prefs;
	
	private Prefs() {}
	
	/**
	 * Constructor initializes shared preferences
	 * @param ctx	Activity's Context
	 */
	private Prefs(Context ctx) {
		Log.d(TAG, "Initializing SharedPreferences for " + ctx.getString(R.string.pref_file_key));
		this.prefs = ctx.getSharedPreferences(
				ctx.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
		this.ctx = ctx;
	}

	/**
	 * @return		Singleton previously instantiated
	 * @exception	NullPointerException if did not call <code>getInstance(Context ctx)</code>
	 */
	public static Prefs getInstance() {
		if (SINGLETON == null) {
			throw new NullPointerException("Must initialize with Context before invoking");
		}
		return SINGLETON;
	}
	
	/**
	 * Initializes the SharedPrefence singleton. Is thread safe.
	 * 
	 * @param ctx	Activity's Context
	 * @return		Singleton Prefs
	 */
	public static Prefs getInstance(Context ctx) {
		if (ctx == null) {
			return null;
		}
		synchronized (SINGLETON) {
			if (SINGLETON == null) {
				SINGLETON = new Prefs(ctx);
			}
		}
		return SINGLETON;
	}
	
	/**
	 * Gets shared preference value based on the requested key.
	 * If no value is found the defaultValue is returned.
	 * 
	 * @param key		Object key to look-up shared preference
	 * @param defValue	Default value to return when no key found
	 * @return			SharedPreference value or default for key
	 */
	public Object getValue(String key, Object defValue) {
		Object obj = null;
		
		if (key == null || key.trim().equals("") || defValue == null) { 
			return obj;
		}

		if (defValue instanceof String) {
			obj = prefs.getString(key, (String)defValue);
		}
		else if (defValue instanceof Integer) {
			obj = prefs.getInt(key, (Integer)defValue);
		}
		else if (defValue instanceof Boolean) {
			obj = prefs.getBoolean(key, (Boolean)defValue);
		}
		else if (defValue instanceof Float) {
			obj = prefs.getFloat(key, (Float)defValue);
		}
		else if (defValue instanceof Long) {
			obj = prefs.getFloat(key, (Long)defValue);
		}
		else {
			Log.w(TAG, "Unsupported SharedPrefs value type: " + defValue.getClass().getCanonicalName());
		}
		
		Log.d(TAG, "SharedPrefs Get " + key + ":" + obj);
		
		return obj;
	}
	
	/**
	 * Puts the shared preference value based on the requested key.
	 * 
	 * @param key		Object key to store
	 * @param value		Value to store
	 */
	public void putValue(String key, Object value) {
		if (key == null || key.trim().equals("") || value == null) { 
			return;
		}
		
		SharedPreferences.Editor editor = prefs.edit();

		if (value instanceof String) {
			editor.putString(key, (String)value);
		}
		else if (value instanceof Integer) {
			editor.putInt(key, (Integer)value);
		}
		else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean)value);
		}
		else if (value instanceof Float) {
			editor.putFloat(key, (Float)value);
		}
		else if (value instanceof Long) {
			editor.putLong(key, (Long)value);
		}
		else {
			Log.w(TAG, "Unsupported SharedPrefs Editor value type: " + value.getClass().getCanonicalName());
			return;
		}
		
		Log.d(TAG, "SharedPrefs Put " + key + ":" + value);
		
		// A slight performance improvement via async apply() vs. blocking on commit().
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else { 
			editor.apply();
		}
	}
	
	/**
	 * Setter for shared preference
	 * 
	 * @param value	Denotes battery state is good/healthy/okay
	 */
	public void setBatteryOkay(boolean value) {
		this.putValue(ctx.getString(R.string.pref_battery_okay), value);
	}
	
	/**
	 * @return	Boolean denoting whether or not battery state is good/healthy/okay
	 */
	public boolean isBatteryOkay() {
		return (Boolean)this.getValue(ctx.getString(R.string.pref_battery_okay), Boolean.TRUE);
	}
	
	/**
	 * @return	Boolean denoting whether or not okay to query weather based on device state
	 */
	public boolean isOkayToQuery() {
		boolean okay = (Boolean)this.getValue(ctx.getString(R.string.pref_okay_to_query), Boolean.TRUE);
		return (okay && isBatteryOkay() && Connection.isNetworkConnected(this.ctx));
	}
	
	/**
	 * Setter for shared preference 
	 * 
	 * @param value	Denotes whether or not okay to query weather based on device state
	 */
	public void setOkayToQuery(boolean value) {
		this.putValue(ctx.getString(R.string.pref_okay_to_query), value);
	}
	
	/**
	 * Setter for shared preference 
	 * 
	 * @param freq	Denotes update frequency in milliseconds for how often to check weather
	 */
	public void setUpdateFrequency(long freq) {
		this.putValue(ctx.getString(R.string.pref_update_freq), freq);
	}
	
	/**
	 * Setter for shared preference
	 * 
	 * @param lastUpdate	Denotes datetime last successful weather update occurred
	 */
	public void setLastWeatherUpdate(long lastUpdate) {
		if (lastUpdate <= 0) {
			return;
		}
		this.putValue(ctx.getString(R.string.pref_last_weather_update), lastUpdate);
	}
	
	/**
	 * Denotes whether or not the last successful weather update has expired based on
	 * the update frequency.
	 * 
	 * @return	True if expired and weather should refresh via query
	 */
	public boolean isUpdateExpired() {
		long curr = System.currentTimeMillis();
		long lastUpdate = (Long)this.getValue(ctx.getString(R.string.pref_last_weather_update), 0);
		long diff = curr - lastUpdate;
		long freq = (Long)this.getValue(ctx.getString(R.string.pref_update_freq), 0);
		return (diff >= freq);
	}
}
