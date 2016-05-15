package com.asdeveloper.droidmouse.utils;

import com.asdeveloper.droidmouse.R;
import com.asdeveloper.droidmouse.connection.Klient;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class KeyboardDroid implements TextWatcher {
	private EditText editText;
	private CheckBox checkBox;
	private Button button;
	private Activity activity;
	private Button[] buttons;
	private InputMethodManager imm;
	private Klient klient;
	
	public KeyboardDroid(Activity activity, Klient klient, EditText editText, CheckBox checkBox, Button button){
		this.activity = activity;
		this.klient = klient;
		this.editText = editText;
		this.checkBox = checkBox;
		this.button = button;
		imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		editText.addTextChangedListener(this);
		editText.setText(" ");
		editText.setSelection(1);
		button.setOnClickListener(buttonClicked);
		checkBox.setOnClickListener(checkBoxClicked);
	}
	
	public void addRelativeButtons(Button... buttons) {
		this.buttons = buttons;
	}
	
	public void show(boolean show) {
		if (show) {
			editText.setVisibility(View.VISIBLE);
			checkBox.setVisibility(View.VISIBLE);
			button.setVisibility(View.VISIBLE);
			imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
			for(Button btn : buttons) {
				btn.setVisibility(View.INVISIBLE);
			}
		} else {
			editText.setVisibility(View.INVISIBLE);
			checkBox.setVisibility(View.INVISIBLE);
			button.setVisibility(View.INVISIBLE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			for(Button btn : buttons) {
				btn.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private OnClickListener buttonClicked = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			klient.akcja(getMessage());
			if(checkBox.isChecked()) {
				Utils.sendMessageWithDelay(klient, "#enter", 100);
			}
		}
	}; 
	
	public String getMessage() {
		String msg = editText.getText().toString().substring(1);
		editText.setText(" ");
		editText.setSelection(1);
		return  "@" + msg;
	}


	private OnClickListener checkBoxClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (checkBox.isChecked()) {
				editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
				editText.setText(" ");
				editText.setSelection(1);
				button.setText(R.string.enter);
			} else {
				editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | 
						InputType.TYPE_CLASS_TEXT);
				editText.setText(" ");
				editText.setSelection(1);
				button.setText(R.string.send);
			}
		}
	};

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0) {
			int ascii = s.charAt(s.length() - 1);
			Log.d("tag", "assci: " + ascii);
			if(ascii == 10) {
				if (!checkBox.isChecked()) {
					klient.akcja(getMessage());
					Utils.sendMessageWithDelay(klient, "#enter", 300);
				} else {
					editText.setText(" ");
					editText.setSelection(1);					
					Utils.sendMessageWithDelay(klient, "#enter", 100);
				}
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (checkBox.isChecked()) {
			editText.removeTextChangedListener(this);
			editText.setText(" ");
			editText.setSelection(1);
			editText.addTextChangedListener(this);
			if (s.length() > 1)
				klient.akcja("@" + s.toString().substring(1));	
		}
		if (count == 0) {
			Utils.sendMessageWithDelay(klient, "#backspace", 100);
			if(checkBox.isChecked() == false) {
				editText.removeTextChangedListener(this);
				editText.setText(" ");
				editText.setSelection(1);
				editText.addTextChangedListener(this);
			}
		}
	}
	
}
