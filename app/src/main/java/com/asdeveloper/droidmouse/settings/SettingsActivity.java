package com.asdeveloper.droidmouse.settings;

import com.asdeveloper.droidmouse.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_main);
		PreferenceManager.setDefaultValues(this, R.xml.preference_main, false);
	}	
}
