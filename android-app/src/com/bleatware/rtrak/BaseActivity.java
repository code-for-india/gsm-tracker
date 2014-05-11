package com.bleatware.rtrak;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class BaseActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Messenger messenger;
    boolean bound = false;
    boolean running = false;
    boolean mode = false;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateState();
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        Log.d("sI", serviceIntent.toString());
        if(!bindService(serviceIntent, connection, BIND_AUTO_CREATE)) {
            Log.e(this.getClass().getName(), "Couldn't bind");
        }
    }
    public void updateState() {
        TextView txtLabel = (TextView) findViewById(R.id.textView);
        String msg = running ? (mode ? "Sync" : "Collect") : "Stopped";
        txtLabel.setText(msg);
    }

    public void toggleServiceState(View w) {
        Message message = Message.obtain();
        message.what = BackgroundService.TOGGLE_MODE;
        try {
            messenger.send(message);
            mode = !mode;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBG(View w) {
        running = !running;
        updateState();
        Message message = Message.obtain();
        message.what = running ? BackgroundService.START_SERVICE: BackgroundService.STOP_SERVICE;
        try {
            messenger.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Message message = Message.obtain();
        message.what = BackgroundService.STOP_SERVICE;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(connection);
        super.onDestroy();
    }
}
