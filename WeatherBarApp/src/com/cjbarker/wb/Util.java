package com.cjbarker.wb;

public class Util {
	
	public static final boolean isEmpty(String s) {
		if (s == null || s.trim().equals("")) {
			return true;
		}
		else {
			return false;
		}
	}

}
