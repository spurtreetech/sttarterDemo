package com.mobile.android.sttarterdemo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.eclipse.paho.android.service.MqttService;

/**
 * Created by Shahbaz on 27-10-2016.
 */

public class RestarterBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        try {
            context.startService(new Intent(context, MessagingService.class));
            context.startService(new Intent(context, MqttService.class));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}