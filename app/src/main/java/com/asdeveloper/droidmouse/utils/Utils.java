package com.asdeveloper.droidmouse.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.asdeveloper.droidmouse.R;
import com.asdeveloper.droidmouse.connection.Klient;

public class Utils {
	public static final String TIPS_PREF_FILE = "tips";
	public static final int DEFAUTL_PORT = 4444;
	public static final String ADDRESS_IP="ADDRESS IP";
	public static final String PORT_NUMBER="PORT_NUMBER";
	
	public static void showTipOnFailedConnection(Activity activity) {
		buildAlert(activity, R.string.ERROR_KEY, R.string.alert_title_error, R.string.alert_msg_error);
	}
	
	public static void showTipOnMainActivity(Activity activity) {
		buildAlert(activity, R.string.MAIN_KEY, R.string.alert_title_tip, R.string.alert_msg_mainTip);
		
	}
	
	public static void showTipForKeyboard(Activity activity) {
		buildAlert(activity, R.string.KEYBOARD_KEY, R.string.alert_title_tip, R.string.alert_msg_keyboardTip);
	}
	
	public static boolean showTipForCloseButton(Activity activity) {
		return buildAlert(activity, R.string.CLOSE_KEY, R.string.alert_title_tip, R.string.alert_msg_closeTip);
	}
	
	private static boolean buildAlert(Activity activity, int key, int idTitle, int idMessage) {
		SharedPreferences prefs = activity.getSharedPreferences(TIPS_PREF_FILE, 0);
		boolean show = prefs.getBoolean(activity.getString(key), true);
		if (show) {
			AlertDialog.Builder alert = new AlertDialog.Builder(activity);
			alert.setTitle(idTitle);
			alert.setMessage(idMessage);
			alert.setNeutralButton("OK", null);
			alert.show();
			if(key != R.string.ERROR_KEY ) {
				Editor editor = prefs.edit();
				editor.putBoolean(activity.getString(key), false);
				editor.commit();
			}
		}
	return show;
	}
	
	public static void sendMessageWithDelay(final Klient klient, final String keyCode, int ms) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				klient.akcja(keyCode);
			}
		}, ms);
	}
	
	
}
