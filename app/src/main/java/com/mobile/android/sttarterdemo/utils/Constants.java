package com.mobile.android.sttarterdemo.utils;

/**
 * Created by Shahbaz on 10/20/2016.
 */

public class Constants {

    private static String BASE_HOST = "http://sttarter.com";
    public static final String DEFAULT_MOBILE_URL = BASE_HOST + ":3030/mobile/";
    public static final String REGISTER_USER = DEFAULT_MOBILE_URL + "register";
    public static final String VERIFY_OTP = DEFAULT_MOBILE_URL + "verifyotp";
    public static final String LOGIN = DEFAULT_MOBILE_URL + "login";
    public static final String GET_OTP = DEFAULT_MOBILE_URL + "getotp";
    public static final String OTP_LOGIN = DEFAULT_MOBILE_URL + "otplogin";
    public static final String QUICK_LOGIN = DEFAULT_MOBILE_URL + "quicklogin";

    /** Constants for user info **/
    //public static final String APP_key
    public static String DEFAULT_PREF = "default_app_prefs";
    public static String USER_ID = "user_id";
    public static String USER_TOKEN = "user_token";
    public static String USER_PHONE = "user_phone";
    public static String IE_TOKEN = "ie_token";
    public static String STTARTER_TOKEN = "sttarter_token";
    public static String STTARTER_APP_KEY = "sttarter_app_key";
    public static String STTARTER_APP_SECRET = "sttarter_secret";

    public static String GOOGLE_PROJECT_NUMBER = "617292272419";
    public static String DEVICE_ID ="device_id";

}
