package com.mobile.android.sttarterdemo.utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Shahbaz on 08-12-2016.
 */

public class CommonFuntions {

    public static void onErrorResponse(Context context,VolleyError error) {
        String json = null;

        NetworkResponse response = error.networkResponse;
        if(response != null && response.data != null){
            if (response.statusCode!=200){
                json = new String(response.data);
                json = trimMessage(json, "msg");
                if(json != null) displayMessage(context,json);
            }
        }
    }


    public static String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public static void displayMessage(Context context, String toastString){
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }

    public static String convertTimeStamp(String dateTime){
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        f.setTimeZone(utc);
        GregorianCalendar cal = new GregorianCalendar(utc);
        try {
            cal.setTime(f.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a, dd MMM yyyy");
        System.out.println(cal.getTime());
        return sdf.format(cal.getTime());
    }

}
