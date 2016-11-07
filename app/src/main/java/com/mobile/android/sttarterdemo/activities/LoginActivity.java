package com.mobile.android.sttarterdemo.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.gcm.GCMClientManager;
import com.mobile.android.sttarterdemo.services.MessagingService;
import com.mobile.android.sttarterdemo.utils.Constants;
import com.spurtreetech.sttarter.lib.helper.STTKeys;
import com.spurtreetech.sttarter.lib.helper.STTGeneralRoutines;
import com.spurtreetech.sttarter.lib.helper.STTarter;
import com.spurtreetech.sttarter.lib.helper.models.LoginOTPResponse;
import com.spurtreetech.sttarter.lib.helper.models.LoginResponse;

/**
 * Created by Shahbaz on 10/18/2016.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton;
    EditText phoneNumberEditText, otpCodeEditText;
    ImageView imageViewlolo;

    private GCMClientManager pushClientManager;


    IntentFilter smsIntentFilter;
    SmsListener smsListener;

    private static final String TAG = "LoginResponse User";
    Dialog otpDialog;

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    Context ctx;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;

        loginButton = (Button) findViewById(R.id.loginButton);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        imageViewlolo = (ImageView) findViewById(R.id.lolo);
        loginButton.setOnClickListener(this);

        smsListener = new SmsListener();
        smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsIntentFilter.setPriority(2147483647);
        this.registerReceiver(smsListener, smsIntentFilter);

        sp = getSharedPreferences(STTKeys.STTARTER_PREFERENCES, Context.MODE_PRIVATE);
        spEditor = sp.edit();

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        if (!sp.getString(STTKeys.USER_ID, "").equals("")) {
            //User has already logged in. So we can go ahead and take him to the new page.

            STTarter.getInstance().init(
                    sp.getString(STTKeys.APP_KEY, ""),
                    sp.getString(STTKeys.APP_SECRET, ""),
                    sp.getString(STTKeys.USER_ID, ""), sp.getString(STTKeys.USER_TOKEN, ""),
                    sp.getString(STTKeys.AUTH_TOKEN, ""), getApplicationContext(), AppController.getInstance().getNotificationHelperListener());
            startService(new Intent(this, MessagingService.class));
            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(loginIntent);
            finish();

        }

        /*imageViewlolo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle(getResources().getString(R.string.change_Server_Url));
                //alertDialog.setMessage("Enter Password");

                //alertDialog.setView(input);
                //alertDialog.setIcon(R.drawable.key);

                View input = getLayoutInflater().inflate(R.layout.otp, null);

               final EditText serverEdit = (EditText) input.findViewById(R.id.otpCodeEditText);
                serverEdit.setHint("Enter server url");

                alertDialog.setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                STTarter.getInstance().setServerUrl(LoginActivity.this,serverEdit.getText().toString());
                                dialog.dismiss();
                            }
                        });

                alertDialog.setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                alertDialog.setView(input);
                alertDialog.setCancelable(false);

                alertDialog.show();

                return false;
            }
        });*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        removeTimer();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton) {

            if (phoneNumberEditText.getText().toString().equals("")) {
                showToast("Please enter Phone Number");
            } else {
                showTimer("Requesting OTP...");
                STTarter.getInstance().loginUserRequestForOTP(Constants.GET_OTP, getRegisterSuccessListener(), getLoginResponseListener(), phoneNumberEditText.getText().toString().trim(), "8", getApplicationContext());

            }
        }/* else if (v.getId() == R.id.confirmButton) {
            confirmOTPWithServer();
        } else if (v.getId() == R.id.dismissButton) {
            otpDialog.dismiss();
        }*/
    }


    private Response.Listener<LoginResponse> getRegisterSuccessListener() {

        return new Response.Listener<LoginResponse>() {
            @Override
            public void onResponse(LoginResponse responseFromLogin) {
                removeTimer();
                String message = "";
                if (responseFromLogin.isValidUser()) {
                    showConfirmationDialog();
                } else if (responseFromLogin.getStatus() == STTKeys.USER_NOT_FOUND || responseFromLogin.getStatus() == STTKeys.ORGANISATION_NOT_FOUND) {
                    message = "Invalid User or Organization chosen. Please check the entries and try again.";
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginActivity.this, SignUpActivity.class).putExtra("phone_number", phoneNumberEditText.getText().toString()));
                    finish();

                } else {
                    message = "An unknown error occurred. Please try again and if the problem persist, please contact your administrator.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public Response.ErrorListener getLoginResponseListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                removeTimer();
                Log.e(TAG, "status code: " + error.toString());
                String message = "An unknown error occurred. Please try again and if the problem persist, please contact your administrator.";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        };
    }


    //PART 2 - Get the OTP and then continue

    private void showConfirmationDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.otp_will_appear_in_box_below));
        //alertDialog.setMessage("Enter Password");

        //alertDialog.setView(input);
        //alertDialog.setIcon(R.drawable.key);

        View input = getLayoutInflater().inflate(R.layout.otp, null);

        otpCodeEditText = (EditText) input.findViewById(R.id.otpCodeEditText);

        alertDialog.setPositiveButton(getResources().getString(R.string.confirm_OTP),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        confirmOTPWithServer();
                    }
                });

        alertDialog.setNegativeButton(getResources().getString(R.string.dismiss),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        alertDialog.setView(input);
        alertDialog.setCancelable(false);

        alertDialog.show();

        /*otpDialog = new Dialog(this);

        otpDialog.setContentView(R.layout.otp);
        otpDialog.setTitle("Confirming OTP");

        // OTP code EditText
        otpCodeEditText = (EditText) otpDialog.findViewById(R.id.otpCodeEditText);

        //adding button click event
        Button dismissButton = (Button) otpDialog.findViewById(R.id.dismissButton);
        Button confirmButton = (Button) otpDialog.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        dismissButton.setOnClickListener(this);
        otpDialog.show();*/

    }


    // On click on SUBMIT BUTTON
    public void confirmOTPWithServer() {
        if (otpCodeEditText.getText().toString() != null && otpCodeEditText.getText().toString().trim().trim() != "") {
            showTimer("Please wait..");
            STTarter.getInstance().confirmOTPWithServer(Constants.OTP_LOGIN, otpCodeEditText.getText().toString().trim(), getOtpSuccessListener(), getOTPResponseListener(), phoneNumberEditText.getText().toString().trim(), "8", getApplicationContext());
        } else {
            String message = "Please enter a valid OTP in order to continue.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

    }

    private Response.Listener<LoginOTPResponse> getOtpSuccessListener() {

        return new Response.Listener<LoginOTPResponse>() {
            @Override
            public void onResponse(final LoginOTPResponse response) {
                //removeTimer();
                if (response.hasUserLoggedIn()) {
                    //We will receive the APP Key and APP Secret here. We will need to set this up.
                    pushClientManager = new GCMClientManager(LoginActivity.this, Constants.GOOGLE_PROJECT_NUMBER);
                    pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                        @Override
                        public void onSuccess(String registrationId, boolean isNewRegistration) {
                            /*Toast.makeText(OpeningActivity.this, registrationId,
                                    Toast.LENGTH_SHORT).show();*/

                            Log.d("RegId", registrationId);

                            // SEND async device registration to your back-end server
                            // linking user with device registration id
                            // POST https://my-back-end.com/devices/register?user_id=123&device_id=abc
                            loginIntoSttarter(response, registrationId);
                        }

                        @Override
                        public void onFailure(String ex) {
                            super.onFailure(ex);
                            // If there is an error registering, don't just keep trying to register.
                            // Require the user to click a button again, or perform
                            // exponential back-off when retrying.
                            Toast.makeText(LoginActivity.this, "GCM registration failed", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }
        };
    }

    public Response.ErrorListener getOTPResponseListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                removeTimer();
                Log.e(TAG, "status code: " + error.toString());
                String message = "An unknown error occurred. Please try again and if the problem persist, please contact your administrator.";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(smsListener);
    }


    // Automatically reading SMS from device and logging in.

    public class SmsListener extends BroadcastReceiver {

        //private SharedPreferences preferences;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();            //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msg_from;
                String[] msg_sender;
                if (bundle != null) {
                    //---retrieve the SMS message received---
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            msg_sender = msg_from.split("-");
                            String msgBody = msgs[i].getMessageBody();
                            Log.d("MainActivity", "message from - " + msgs[i].getOriginatingAddress() + ", messageBody - " + msgs[i].getMessageBody());
                            //if(msgs[i].getOriginatingAddress().equals(Constants.SMS_SENDER1) || msgs[i].getOriginatingAddress().equals(Constants.SMS_SENDER2)) {
                            if (msg_sender[msg_sender.length - 1].equals("STTART")) {
                                String[] messageSplit = msgs[i].getMessageBody().split(" ");
                                loginWithOTP(messageSplit[messageSplit.length - 1]);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception caught", e.getMessage());
                    }
                }
            }
        }


    }

    private void loginWithOTP(String otpCode) {
        if (otpCodeEditText != null)
            otpCodeEditText.setText(otpCode);
        //confirmOTPWithServer(otpCode);
        if (LoginActivity.this != null)
            STTarter.getInstance().confirmOTPWithServer(Constants.OTP_LOGIN, otpCode, getOtpSuccessListener(), getOTPResponseListener(), phoneNumberEditText.getText().toString().trim(), "8", getApplicationContext());
    }

    private void loginIntoSttarter(LoginOTPResponse response, String gcmRegId) {

        Log.d("OpeningActivity", "stt_token : " + response.getStt_token() + ", ie_token : " + response.getIe_token());

        //STTarter Intialize - needs app key, secret, user and user token
        STTarter.getInstance().init(response.getApp_key(), response.getApp_secret(),
                response.getUsername(), response.getUser_token(), response.getStt_token(),
                getApplicationContext(), AppController.getInstance().getNotificationHelperListener());
        startService(new Intent(this, MessagingService.class));
        // TODO redirect user to login screen
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);


        STTGeneralRoutines sttGeneralRoutines = new STTGeneralRoutines();
        sttGeneralRoutines.registerApp(response.getApp_key(), response.getUsername(), STTarter.getInstance().getClientId(), Build.MANUFACTURER, Build.MODEL, Build.VERSION.SDK_INT + "", gcmRegId);

        finish();
    }

    private void showTimer(String message) {
        try {
            progress.setMessage(message);
            progress.show();
        } catch (Exception e) {

        }
    }

    private void removeTimer() {
        if (progress.isShowing())
            progress.dismiss();
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
