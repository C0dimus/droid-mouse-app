package com.asdeveloper.droidmouse.ui;

import com.asdeveloper.droidmouse.R;
import com.asdeveloper.droidmouse.connection.Klient;
import com.asdeveloper.droidmouse.settings.Preferences;
import com.asdeveloper.droidmouse.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class StartActivity extends Activity {
	
	private TextView ipEditText;
	private Button connectButton;
	private String ip;
	private int port;
	private Klient klient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		ipEditText= (TextView) findViewById(R.id.et_start_ip);
		connectButton= (Button) findViewById(R.id.bt_start_connect);
		connectButton.setOnClickListener(buttonConnectClicked);
		ip = Preferences.loadIP(this);
		ipEditText.setText(ip);
		ipEditText.setOnEditorActionListener(ipTextViewListener);
	
	}

	OnClickListener buttonConnectClicked = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			String s = ipEditText.getText().toString();
			if(s.indexOf(":") > 0) {
				ip= s.substring(0, s.indexOf(":"));
				port = Integer.valueOf(s.substring(s.indexOf(":")+1));
			} else {
				ip = s;
				port = Utils.DEFAUTL_PORT;
			}
			
			klient= new Klient(ip,port,getApplicationContext());
			if( klient.akcja()==true) {
				Intent intent= new Intent(StartActivity.this,MainActivity.class);
				intent.putExtra(Utils.ADDRESS_IP, ip);
				intent.putExtra(Utils.PORT_NUMBER, port);
				startActivity(intent);
				Preferences.saveIP(StartActivity.this, ip);
				finish();
			}else {
				Utils.showTipOnFailedConnection(StartActivity.this);
			}			
		}
	};
	
	OnEditorActionListener ipTextViewListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_GO) {
				connectButton.performClick();
				return true;
			}
			return false;
		}
	};
}
