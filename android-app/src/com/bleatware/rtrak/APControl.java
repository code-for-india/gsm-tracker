package com.bleatware.rtrak;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * RescueTracker
 * User: vasuman
 * Date: 10/5/14
 * Time: 1:55 PM
 */
public class APControl {
    WifiManager wifi;
    BackgroundService bgs;
    static String AP_NAME = "DISASTER_AP";
    static String SYNC_AP = "COMM_CTRL_SRV";

    static WifiConfiguration apConfig = new WifiConfiguration();
    static
    {
        apConfig.SSID = AP_NAME;
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    }

    public APControl(BackgroundService ctx) {
        this.bgs = ctx;
        this.wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    }

    private boolean tryConnect(String SSID) {
        WifiInfo info = wifi.getConnectionInfo();
        if(info != null && info.getSSID().equals(SSID)) {
            return true;
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + SSID + "\"";
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        int id = wifi.addNetwork(config);
        return wifi.enableNetwork(id, true);
    }

    public List<ClientScanResult> scan() {
        BufferedReader br = null;
        List<ClientScanResult> result = new ArrayList<ClientScanResult>();
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if ((splitted != null) && (splitted.length >= 4)) {
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], true));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            Log.e(this.getClass().toString(), e.toString());
        }
        return null;
    }
    public void syncMode(boolean state) {
        if(state) {
            wifi.setWifiEnabled(true);
            if(tryConnect(SYNC_AP)) {
                bgs.doSync();
            }
        } else {
            wifi.setWifiEnabled(false);
        }
    }

    public boolean switchAP(boolean state) {
        try {
            wifi.setWifiEnabled(false);
            Method setConfig = WifiManager.class.getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfig.invoke(wifi, apConfig);
            Method start = WifiManager.class.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            start.invoke(wifi, apConfig, state);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(bgs, "ERROR w/ HotSpot", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
