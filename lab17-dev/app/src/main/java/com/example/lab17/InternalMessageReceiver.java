package com.example.lab17;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class InternalMessageReceiver extends BroadcastReceiver {

    private static final String TAG = "InternalMsgReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.lab17.ACTION_PING_INTERNAL".equals(intent.getAction())) {
            String payload = intent.getStringExtra("payload");
            Log.i(TAG, "Message interne intercepté : " + payload);
            Toast.makeText(context, "Intercepté : " + payload, Toast.LENGTH_LONG).show();
        }
    }
}
