package com.mobile.android.sttarterdemo.application_controller;

import android.app.Application;
import android.app.NotificationManager;
import android.support.v7.appcompat.BuildConfig;

import com.android.volley.RequestQueue;
import com.sttarter.helper.utils.NotificationHelperListener;

/**
 * Created by kevalprabhu on 17/10/16.
 */

public class AppController extends Application {

    private static AppController instance = null;
    private RequestQueue mRequestQueue;

    private static NotificationManager notificationManager;
    NotificationHelperListener notificationHelperListener;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            //Fabric.with(this, new Crashlytics());
        }
        instance = this;
    }

    public static synchronized AppController getInstance() {
        if(instance == null) {
            instance = new AppController();
        }

        return instance;
    }


    public NotificationHelperListener getNotificationHelperListener() {

        return STTarterUtil.getSTTarterNotificationLister(getApplicationContext());

    }

}
