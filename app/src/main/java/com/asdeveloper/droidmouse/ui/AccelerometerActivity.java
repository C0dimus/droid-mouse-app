package com.asdeveloper.droidmouse.ui;

import com.asdeveloper.droidmouse.R;
import com.asdeveloper.droidmouse.connection.Klient;
import com.asdeveloper.droidmouse.settings.Preferences;
import com.asdeveloper.droidmouse.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccelerometerActivity extends Activity implements SensorEventListener {
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private ImageButton leftImageButton;
	private ImageButton wheelImageButton;
	private ImageButton rightImageButton;
	private float x,y;
	private final int buttonDelay = 20;
	private long lastUpdate = 0;
	
	private Klient klient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometer_activity);
		sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		leftImageButton = (ImageButton) findViewById(R.id.ib_acc_left);
		wheelImageButton = (ImageButton) findViewById(R.id.ib_acc_wheel);
		rightImageButton = (ImageButton) findViewById(R.id.ib_acc_right);
		leftImageButton.setOnTouchListener(leftButtonTouched);
		rightImageButton.setOnTouchListener(rightButtonTouched);
		wheelImageButton.setOnTouchListener(wheelButtonTouched);
		String ip= getIntent().getStringExtra(Utils.ADDRESS_IP);
		int port = getIntent().getIntExtra(Utils.PORT_NUMBER, Utils.DEFAUTL_PORT);
		klient= new Klient(ip, port, getApplicationContext());
		
	}
	private int X=0, Y=0;
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {		
			long currTime = System.currentTimeMillis();		
			if(currTime - lastUpdate > Preferences.ACCELEROMETER_DELAY) {
				lastUpdate = currTime;
				x=event.values[0];
				y=event.values[1];
				X = (int)(-x);
				Y = (int)(y);
				klient.akcja("!;"+X + " " + Y);			
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	OnTouchListener leftButtonTouched = new OnTouchListener() {	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Utils.sendMessageWithDelay(klient, "!<p", buttonDelay);
				return true;
			case MotionEvent.ACTION_UP:
				Utils.sendMessageWithDelay(klient, "!<r", 0);
				return true;
			}
			return false;
		}
	};
	
	OnTouchListener rightButtonTouched = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				Utils.sendMessageWithDelay(klient, "!>", buttonDelay);
				return true;
			}
			return false;
		}
	};
	
	private int downY,upY, temp;
	OnTouchListener wheelButtonTouched = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = (int) event.getY();
				temp = Preferences.SCROLL_SENSITIVITY;
				return true;
			case MotionEvent.ACTION_MOVE:
				temp--;
				if(temp <= 0) {
					if(event.getY() - downY > 0)
						klient.akcja("!:1");
					else
						klient.akcja("!:-1");
					temp = Preferences.SCROLL_SENSITIVITY;
				}
				return true;
			case MotionEvent.ACTION_UP:
				int pointSize = 5;
				upY = (int)event.getY();
				if(upY - downY >= -pointSize && upY - downY <= pointSize) {
					Utils.sendMessageWithDelay(klient, "!^", buttonDelay);
				}
				return true;
			}
			return false;
		}
	};

}
