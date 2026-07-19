package com.example.lab17;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SystemStartupListener extends BroadcastReceiver {
    
    private static final String TAG = "SystemStartupListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i(TAG, "Le système a terminé son amorçage. Initialisation des services...");
            Toast.makeText(context, "Démarrage Système Complet - Lab 17 Actif", Toast.LENGTH_LONG).show();
        }
    }
}
