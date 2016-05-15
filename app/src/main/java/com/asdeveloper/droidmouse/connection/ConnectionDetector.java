package com.asdeveloper.droidmouse.connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class ConnectionDetector {
    
    private Context context;
    private WifiManager wifiManager;
     
    public ConnectionDetector(Context context){
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
 
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    public boolean isWiFiEnabled() {
    	if(wifiManager.isWifiEnabled())
    		return true;
    	else
    		return checkIfAPWorking() || true;
    }
    
    private boolean checkIfAPWorking() {
    	Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
    	for(Method method: wmMethods){
    	if(method.getName().equals("isWifiApEnabled")) {
	    	try {
		    	  return (boolean) method.invoke(wifiManager);
		    	} catch (IllegalArgumentException e) {
		    	  e.printStackTrace();
		    	} catch (IllegalAccessException e) {
		    	  e.printStackTrace();
		    	} catch (InvocationTargetException e) {
		    	  e.printStackTrace();
		    	}
	    	}
    	}
    	return false;
    }
}