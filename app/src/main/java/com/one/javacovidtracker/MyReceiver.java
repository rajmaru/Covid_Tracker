package com.one.javacovidtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!CheckNetworkConnection.check(context)) {
            Toast.makeText(context, "Internet Not Connected", Toast.LENGTH_SHORT).show();
        }
    }
}