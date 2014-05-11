package com.bleatware.rtrak;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * RescueTracker
 * User: vasuman
 * Date: 10/5/14
 * Time: 1:20 PM
 */
public class BackgroundService extends Service {
    public static final int TOGGLE_MODE = 1;
    public static final int STOP_SERVICE = 2;
    static final int SERVER_PORT = 8990;
    public static final int START_SERVICE = 3;
    private static final int POLL_CONNECTED = 4;
    MessageHandler mHandler;
    HandlerThread hThread;
    APControl apControl;
    InfoServer infoServer;

    static final String HOST = "http://192.168.12.1/add";

    public void doSync() {
        HttpClient client = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(HOST);
            List<NameValuePair> postArgs = new ArrayList<NameValuePair>();
            postArgs.add(new BasicNameValuePair("data", infoServer.getJSONArray()));
            post.setEntity(new UrlEncodedFormEntity(postArgs));
            HttpResponse response = client.execute(post);
            infoServer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static enum State {
        AP_STARTED, STOPPED, SYNCING
    }
    State state = State.STOPPED;
    public BackgroundService() {
        hThread = new HandlerThread("BackgroundService");
        hThread.start();
        mHandler = new MessageHandler(hThread.getLooper());
    }

    @Override
    public void onCreate() {
        apControl = new APControl(this);
        infoServer = new InfoServer(SERVER_PORT, this);
        try {
            infoServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageHandler extends Handler {
        private static final long POLL_INTERVAL = 1000;

        public MessageHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == TOGGLE_MODE) {
                if(state == State.AP_STARTED) {
                    state = State.SYNCING;
                } else {
                    state = State.AP_STARTED;
                }
            } else if(msg.what == START_SERVICE) {
                state = State.AP_STARTED;
                Message m = obtainMessage();
                m.what = POLL_CONNECTED;
                sendMessageDelayed(m, POLL_INTERVAL);
            } else if(msg.what == STOP_SERVICE) {
                state = State.STOPPED;
            } else if(msg.what == POLL_CONNECTED) {
                // TODO: poll clients
                sendMessageDelayed(msg, POLL_INTERVAL);
                if(state == State.SYNCING) {
                    apControl.syncMode(true);
                }
            }
            updateState();
        }
    }

    private void updateState() {
        if(state == State.STOPPED) {
            apControl.switchAP(false);
        } else if(state == State.AP_STARTED) {
            apControl.switchAP(true);
        } else if(state == State.SYNCING) {
            apControl.switchAP(false);
            apControl.syncMode(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(this.getClass().toString(), "Bound");
        return new Messenger(mHandler).getBinder();
    }
}
