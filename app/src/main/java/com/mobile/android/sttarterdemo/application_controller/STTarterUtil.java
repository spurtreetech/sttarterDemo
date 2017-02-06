package com.mobile.android.sttarterdemo.application_controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.MainActivity;
import com.sttarter.communicator.models.GroupMeta;
import com.sttarter.helper.utils.NotificationHelperListener;
import com.sttarter.init.STTProviderHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kevalprabhu on 18/11/16.
 */

public class STTarterUtil {

    static NotificationManager notificationManager;

    public static NotificationHelperListener getSTTarterNotificationLister(final Context appContext){
        NotificationHelperListener notificationHelperListener;

        notificationHelperListener = new NotificationHelperListener() {
            @Override
            public void displayNotification(String notificationString) {

                Gson gson = new Gson();

                PendingIntent pIntent = null;
                Intent intent = new Intent(appContext, MainActivity.class);
                //intent.setClassName("com.nps.android", "com.nps.android.activities.MainActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                NotificationCompat.Builder builder = null;

                STTProviderHelper ph = new STTProviderHelper();
                Cursor tempCursor = ph.getUnreadMessageCount();

                tempCursor.moveToFirst();
                String bigText = "", mainText = "", topicMetaString = "";
                JSONObject intentJSON = new JSONObject();
                JSONArray topicsArray = new JSONArray();
                int messagesCount = 0, topicsCount = 0;
                topicsCount = tempCursor.getCount();
                while (!tempCursor.isAfterLast()) {
                    GroupMeta tm;
                    if(bigText.equals("")) {
                        topicMetaString = tempCursor.getString(2);
                        tm = gson.fromJson(topicMetaString, GroupMeta.class);
                        //bigText = tempCursor.getString(tempCursor.getColumnIndex("topic")) + " : " + "";
                        Log.d("NotificationHelper", "column index - " + tempCursor.getString(0) + ", " + tempCursor.getString(1) + ", " + tempCursor.getString(2) + ", TopicName " + ((tm == null || tm.getName()==null)?"":tm.getName()));
                        messagesCount = messagesCount + tempCursor.getInt(1);
                    } else {
                        topicMetaString = tempCursor.getString(2);
                        tm = gson.fromJson(topicMetaString, GroupMeta.class);
                        Log.d("NotificationHelper", "column index - " + tempCursor.getString(0) + ", " + tempCursor.getString(1) + ", " + tempCursor.getString(2) + ", TopicName "  + ((tm == null || tm.getName()==null)?"":tm.getName()));
                        messagesCount = messagesCount + tempCursor.getInt(1);
                    }
                    //JSONObject tempTopicJSON = new JSONObject();
                    //tempTopicJSON.p
                    topicsArray.put(tempCursor.getString(0));
                    tempCursor.moveToNext();
                }

                try {
                    intentJSON.put("count", topicsCount);
                    intentJSON.put("topic_name", topicsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("notification_data", intentJSON.toString());
                notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
                pIntent = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                mainText = "You have " + messagesCount + " unread " + ((messagesCount==1)? "message" : "messages") + " in " + topicsCount + ((topicsCount==1)? " topic" : " topics");
                Log.d("NotificationHelper", "messages count - " + messagesCount + ", topics count - " + topicsCount + ", intent string: " + intentJSON.toString());

                builder = new NotificationCompat.Builder(appContext);
                builder.setContentTitle(appContext.getResources().getString(R.string.app_name))
                        .setContentText(mainText)
                        .setContentIntent(pIntent)
                        //.addAction(android.R.drawable.stat_notify_chat, "Open", pIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setCategory(Notification.CATEGORY_EVENT)
                        .setGroupSummary(true)
                        .setGroup("sttarter")
                        .setAutoCancel(true);
                /*
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("IndusEdge")
                        .bigText("you have unread messages in " + groupsString))
                        //.addLine("you have unread messages in " + groupsString)
                        //.addLine("Line 2")
                        //.setSummaryText("Total unread messages")
                        //.setBigContentTitle("IndusEdge"))

                        //.setNumber(2)
                        .setContentIntent(pIntent)
                        .addAction(android.R.drawable.stat_notify_chat, "Open", pIntent)
                        .setSmallIcon(R.mipmap.nps_logo)
                        .setCategory(Notification.CATEGORY_EVENT)
                        .setGroupSummary(true)
                        .setGroup("sttarter")
                .setAutoCancel(true);
                */
                Notification notification = builder.build();

                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(123456, notification);
            }

        };


        return notificationHelperListener;
    }

}
