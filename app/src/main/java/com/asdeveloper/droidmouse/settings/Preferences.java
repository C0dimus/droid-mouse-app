package com.asdeveloper.droidmouse.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.asdeveloper.droidmouse.R;
import com.asdeveloper.droidmouse.utils.Utils;

public class Preferences {
	public static int MOUSE_SENSITIVITY = 30;
	public static int SCROLL_SENSITIVITY = 2;
	public static int ACCELEROMETER_DELAY = 0;
	
	public static void loadMainActivityPreferences(Activity activity, Button button, ImageView scroll) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String buttonsPosition = sharedPreferences.getString(activity.getString(R.string.BUTTONS_KEY), 
				activity.getString(R.string.BUTTONS_BOTTOM));
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
		if(buttonsPosition.equals(activity.getString(R.string.BUTTONS_TOP))) {
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}
		button.setLayoutParams(params);
		
		String scrollPosition = sharedPreferences.getString(activity.getString(R.string.SCROLL_KEY),
				activity.getString(R.string.SCROLL_RIGHT));
		params = (RelativeLayout.LayoutParams) scroll.getLayoutParams();
		if(scrollPosition.equals(activity.getString(R.string.SCROLL_LEFT))) {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		}
		scroll.setLayoutParams(params);
		
		boolean restoreTips = sharedPreferences.getBoolean(activity.getString(R.string.RESTORE_TIPS_KEY), false);
		if (restoreTips) {
			SharedPreferences.Editor edit = activity.getSharedPreferences(Utils.TIPS_PREF_FILE, 0).edit();
			edit.putBoolean(activity.getString(R.string.MAIN_KEY), true);
			edit.putBoolean(activity.getString(R.string.KEYBOARD_KEY), true);
			edit.putBoolean(activity.getString(R.string.CLOSE_KEY), true);
			edit.commit();
			
			Editor editDefault = sharedPreferences.edit();
			editDefault.putBoolean(activity.getString(R.string.RESTORE_TIPS_KEY), false);
			editDefault.commit();
		}
	
		MOUSE_SENSITIVITY = sharedPreferences.getInt(activity.getString(R.string.MOUSE_SENSITIVE_KEY), 
				Integer.valueOf(activity.getString(R.string.default_mouse_sen)));
		if(MOUSE_SENSITIVITY <= 0) MOUSE_SENSITIVITY = 1;
		SCROLL_SENSITIVITY = sharedPreferences.getInt(activity.getString(R.string.SCROLL_SENSITIVE_KEY), 
				Integer.valueOf(activity.getString(R.string.default_scroll_sen)));
		if(SCROLL_SENSITIVITY < 0) SCROLL_SENSITIVITY = 0;
		ACCELEROMETER_DELAY = sharedPreferences.getInt(activity.getString(R.string.ACCELETOMETER_KEY), 
				Integer.valueOf(activity.getString(R.string.default_accelerometer)));
		if(ACCELEROMETER_DELAY < 0) ACCELEROMETER_DELAY = 0;
	}
	
	private static final String IP_KEY = "IP_KEY";	
	
	public static String loadIP(Activity activity) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		return sharedPreferences.getString(IP_KEY, "");
	}
	
	public static void saveIP(Activity activity, String ip) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
		editor.putString(IP_KEY, ip);
		editor.commit();
	}
	
}
