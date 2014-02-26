package com.cjbarker.wb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {
	
	public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm zzz yyyy";
	
	public static final boolean isEmpty(String s) {
		if (s == null || s.trim().equals("")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String getLocalTime(long utc) {
		return getLocalTime(utc, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Converts the UTC to the local device's time zone.
	 * 
	 * @param utc		Epoch UTC time in milliseconds
	 * @param timeFmt	Date/Time formatter
	 * @return			Local time formatted in string 
	 */
	public static String getLocalTime(long utc, String timeFmt) {
		timeFmt = (Util.isEmpty(timeFmt)) ? DEFAULT_DATE_FORMAT : timeFmt;
		Date utcDate = new Date(utc);
        SimpleDateFormat sdf = new SimpleDateFormat(timeFmt);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(utcDate);
	}
}
