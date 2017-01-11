package com.mobile.android.sttarterdemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Shahbaz on 26-10-2016.
 */

public class MessagingService extends Service
{
    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * Service onStartCommand
     * Handles the action passed via the Intent
     *
     * @return START_REDELIVER_INTENT
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sendBroadCast();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){

        sendBroadCast();

        super.onTaskRemoved(rootIntent);
    }

    void sendBroadCast(){
        Log.e("Send BroadCast",">>>>>>>>>>");
        Intent broadcastIntent = new Intent("com.nps.android.services.RestartIntent");
        sendBroadcast(broadcastIntent);
    }

}