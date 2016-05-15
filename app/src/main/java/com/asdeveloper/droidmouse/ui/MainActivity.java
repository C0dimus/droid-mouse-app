package com.asdeveloper.droidmouse.ui;

import com.asdeveloper.droidmouse.R;
import com.asdeveloper.droidmouse.connection.Klient;
import com.asdeveloper.droidmouse.settings.Preferences;
import com.asdeveloper.droidmouse.settings.SettingsActivity;
import com.asdeveloper.droidmouse.utils.KeyboardDroid;
import com.asdeveloper.droidmouse.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
	implements OnClickListener, OnTouchListener {
	
	private RelativeLayout layout;
	private EditText editTextSend;
	private CheckBox checkBoxLive;
	private Button buttonSend;
	private Button buttonWheel;
	private Button buttonLeft;
	private Button buttonRight;
	private ImageView scrollImageView;
	private KeyboardDroid keyboardDroid;
	private Klient klient;
	private String ip;
	private int port;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		layout = (RelativeLayout) findViewById(R.id.mainLayout);
		layout.setOnTouchListener(this);
		editTextSend = (EditText) findViewById(R.id.editTextSend);
		checkBoxLive = (CheckBox) findViewById(R.id.checkBoxLive);
		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonLeft = (Button) findViewById(R.id.buttonLeft);
		buttonWheel = (Button) findViewById(R.id.buttonWheel);
		buttonRight = (Button) findViewById(R.id.buttonRight);
		scrollImageView = (ImageView) findViewById(R.id.iv_main_scroll);
		buttonLeft.setOnClickListener(this);
		buttonWheel.setOnClickListener(this);
		buttonRight.setOnClickListener(this);
		scrollImageView.setOnTouchListener(this);
	
		Preferences.loadMainActivityPreferences(this, buttonLeft, scrollImageView);
		Intent intent = getIntent();
	    ip = intent.getStringExtra(Utils.ADDRESS_IP);
		port = intent.getIntExtra(Utils.PORT_NUMBER, Utils.DEFAUTL_PORT);
		klient = new Klient(ip, port, getApplicationContext());
		keyboardDroid = new KeyboardDroid(this, klient, editTextSend, checkBoxLive, buttonSend);
		keyboardDroid.addRelativeButtons(buttonLeft, buttonWheel, buttonRight);
		Utils.showTipOnMainActivity(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_keyboard:
			Utils.showTipForKeyboard(this);
			keyboardDroid.show(true);
			return true;
		case R.id.action_accelerometer:
			Intent i = new Intent(this, AccelerometerActivity.class);
			i.putExtra(Utils.ADDRESS_IP, ip);
			i.putExtra(Utils.PORT_NUMBER, port);
			startActivity(i);
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivityForResult(intent, 0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		Preferences.loadMainActivityPreferences(this, buttonLeft, scrollImageView);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonLeft:
			klient.akcja("!<");
			break;
		case R.id.buttonWheel:
			klient.akcja("!^");
			break;
		case R.id.buttonRight:
			klient.akcja("!>");
			break;	
		}
	}

	private int x, y, offx, offy;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		keyboardDroid.show(false);
		int action = event.getActionMasked();
		switch (v.getId()) {
		case R.id.mainLayout:
			return mouseOnTouch(action, event);
		case R.id.iv_main_scroll:
			return scrollOnTouch(action, event);
		}
		return true;
	}

	private boolean mouseOnTouch(int action, MotionEvent event) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			offx = 0; offy = 0;
			x = (int) event.getX();
			y = (int) event.getY();
			Log.d("mouse", "action down: " + x + " " + y);
			return true;
		case MotionEvent.ACTION_MOVE:
			offx = (int) event.getX() - x;
			offy = (int) event.getY() - y;
			klient.akcja("!;" + offx/Preferences.MOUSE_SENSITIVITY + " " + offy/Preferences.MOUSE_SENSITIVITY);
			return true;
		case MotionEvent.ACTION_UP:
			int pointSize = 5;
			if ((offx >= -pointSize && offx <= pointSize) &&
					(offy >= -pointSize && offy <= pointSize)) {
				Utils.sendMessageWithDelay(klient, "!<", 50);
				Log.d("mouse", "left pressed");
			}
			return true;
		default:
			return true;
		}
	}
	
	private int temp;
	private boolean scrollOnTouch(int action, MotionEvent event) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = (int) event.getY();
			temp = Preferences.SCROLL_SENSITIVITY;
			return true;
		case MotionEvent.ACTION_MOVE:
			temp--;
			if(temp <= 0) {
				if (event.getY() - y > 0)
					klient.akcja("!:1");
				else 
					klient.akcja("!:-1");
				temp = Preferences.SCROLL_SENSITIVITY;
			}
			return true;
		default:
			return true;
		}
	}

	
	@Override
	public void onBackPressed() {
		if( !Utils.showTipForCloseButton(this) )
			super.onBackPressed();
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	        klient.akcja("#close");
	        finish();
	        return true;
	    }
	    return super.onKeyLongPress(keyCode, event);
	}
}

/////////////////////////////////