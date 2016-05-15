package com.asdeveloper.droidmouse.connection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import com.asdeveloper.droidmouse.R;



import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class Klient {
	private Socket serwer = null;
	private int port;
	private String ip;
	private PrintWriter pw;
	private String msg;
	private ConnectionDetector cd;
	private Context context;
	private boolean sukces = false;

	public Klient(String ip, int port, Context context) {
		this.ip = ip;
		this.port = port;
		this.context= context;
		cd = new ConnectionDetector(this.context);
	}

	public void akcja(String msg) {
		if (cd.isWiFiEnabled()) {
			this.msg = msg;
			DroidServer droidServer = new DroidServer();				
			droidServer.execute();
		} else 
			wifiDisabledToast();
	}
	
	private Toast toast = null;
	private void wifiDisabledToast() {
		if(toast == null) {
			toast = Toast.makeText(context, context.getString(R.string.toast_wifi_disabled), Toast.LENGTH_SHORT);
			toast.show();
		}else {
			toast.cancel();
			toast = null; 
		}
	}
	
	public boolean akcja() {
		if(cd.isWiFiEnabled()) {
			this.msg="connected";
			DroidServer g= new DroidServer();
			g.execute();
			try {
				sukces=g.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return sukces;
		} else {
			wifiDisabledToast();
			return false;
		}			
	}

	private class DroidServer extends AsyncTask<Void, Void, Boolean> {
		
		public DroidServer() {
			// TODO Auto-generated constructor stub
		}

		@Override
	protected Boolean doInBackground(Void... params) {

		if (otworz() == true) {
			wyslij();	
			sukces= true;
		} else {
			sukces= false;
		}
		zamknij();
		return sukces;
	}

	private boolean otworz() {
		if (serwer == null) {
			try {
				serwer = new Socket();
				serwer.connect(new InetSocketAddress(ip,port),500);
				return true;
			} catch (IOException e) {
				return false;
			}
		} else
			return true;
	}

	private String wyslij() {
		if (!msg.isEmpty()) {
			try {
				pw = new PrintWriter(serwer.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			pw.write(msg); 
			pw.flush();
			pw.close();
		}
		return msg;
	}

	private boolean zamknij() {
		if (serwer != null) {
			try {
				serwer.close();
				serwer = null;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else
			return true;
	}
	} // end of Class DroidServer

}
