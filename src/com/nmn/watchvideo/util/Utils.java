package com.nmn.watchvideo.util;

import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class Utils {
	public static String secondsToString(int seconds) {
		String s = String.format("%02d", seconds / 60) + ":";
		int t = seconds % 60;
		s += t < 10 ? "0" + t : t;
		return s;
	}
	
	public static int getWidthScreen(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int width = metric.widthPixels;
		return width;
	}

	public static int getHeightScreen(Context context) {
		DisplayMetrics metric = context.getResources().getDisplayMetrics();
		int height = metric.heightPixels;
		return height;
	}
	
	public static String generateRandomString(int len){
		String str = "0123456789abcdefghijklmnopqrstuvwxyz";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		   for( int i = 0; i < len; i++ ) {
			   sb.append(str.charAt( rnd.nextInt(str.length()))); 
		   }
		return sb.toString();
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

}
