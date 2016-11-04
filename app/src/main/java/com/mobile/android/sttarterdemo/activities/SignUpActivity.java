package com.mobile.android.sttarterdemo.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.adapters.NothingSelectedSpinnerAdapter;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.gcm.GCMClientManager;
import com.mobile.android.sttarterdemo.services.MessagingService;
import com.mobile.android.sttarterdemo.utils.Constants;
import com.spurtreetech.sttarter.lib.helper.STTGeneralRoutines;
import com.spurtreetech.sttarter.lib.helper.STTarter;
import com.spurtreetech.sttarter.lib.helper.models.LoginOTPResponse;

/**
 * Created by Aishvarya on 10/21/2016.
 */

public class SignUpActivity extends AppCompatActivity {

    Button buttonSignUpSubmit;
    EditText editTextName, editTextEmail, phoneEditText;

    private GCMClientManager pushClientManager;

    EditText otpCodeEditText;

    IntentFilter smsIntentFilter;
    SmsListener smsListener;

    ProgressDialog progress;

    private static final String TAG = "LoginResponse User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        smsListener = new SmsListener();
        smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsIntentFilter.setPriority(2147483647);
        this.registerReceiver(smsListener, smsIntentFilter);

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        init();

        buttonSignUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().equals("")) {
                    showToast("Please enter your name");
                } else if (editTextEmail.getText().toString().equals("")) {
                    showToast("Please enter your Students Name");
                } else if (phoneEditText.getText().toString().equals("")) {
                    showToast("Please enter Students DOB");
                } else if (!isValidEmail(editTextEmail.getText().toString())) {
                    showToast("Please enter a valid Email");
                } else {
                    showTimer("Please wait..");
                    STTarter.getInstance().quickLogin(Constants.QUICK_LOGIN, new Response.Listener<LoginOTPResponse>() {
                                @Override
                                public void onResponse(final LoginOTPResponse response) {

                                    removeTimer();

                                    if (response.getStatus()==666) {
                                        //We will receive the APP Key and APP Secret here. We will need to set this up.
                                        /*pushClientManager = new GCMClientManager(SignUpActivity.this, Constants.GOOGLE_PROJECT_NUMBER);
                                        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                                            @Override
                                            public void onSuccess(String registrationId, boolean isNewRegistration) {
                                                *//*Toast.makeText(OpeningActivity.this, registrationId,
                                                Toast.LENGTH_SHORT).show();*//*

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
                                                Toast.makeText(SignUpActivity.this, "GCM registration failed", Toast.LENGTH_LONG).show();
                                            }
                                        });*/

                                        showConfirmationDialog();

                                    }
                                    else if (response.getStatus()==777){
                                        Toast.makeText(SignUpActivity.this,"Registered but OTP Not sent",Toast.LENGTH_LONG).show();
                                    }
                                    else if (response.getStatus()==888){
                                        Toast.makeText(SignUpActivity.this,"User already exists",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                            , new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    removeTimer();
                                    Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }, editTextName.getText().toString(), phoneEditText.getText().toString(), editTextEmail.getText().toString(), "8", SignUpActivity.this);
                }
                }

        });


    }

    private void init() {
        buttonSignUpSubmit = (Button) findViewById(R.id.buttonSignUpSubmit);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        if (getIntent().getExtras().containsKey("phone_number")){
            phoneEditText.setText(getIntent().getExtras().getString("phone_number"));
            phoneEditText.setSelection(phoneEditText.getText().length());
        }

    }

    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void showConfirmationDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
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
            STTarter.getInstance().confirmOTPWithServer(Constants.OTP_LOGIN,otpCodeEditText.getText().toString().trim(), getOtpSuccessListener(), getOTPResponseListener(), phoneEditText.getText().toString().trim(), "8", getApplicationContext());
        } else {
            String message = "Please enter a valid OTP in order to continue.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

    }

    private Response.Listener<LoginOTPResponse> getOtpSuccessListener() {

        return new Response.Listener<LoginOTPResponse>() {
            @Override
            public void onResponse(final LoginOTPResponse response) {
                removeTimer();
                if (response.hasUserLoggedIn()) {
                    //We will receive the APP Key and APP Secret here. We will need to set this up.
                    pushClientManager = new GCMClientManager(SignUpActivity.this, Constants.GOOGLE_PROJECT_NUMBER);
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
                            Toast.makeText(SignUpActivity.this, "GCM registration failed", Toast.LENGTH_LONG).show();
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


    private void loginIntoSttarter(LoginOTPResponse response, String gcmRegId) {

        Log.d("OpeningActivity", "stt_token : " + response.getStt_token() + ", ie_token : " + response.getIe_token());

        //STTarter Intialize - needs app key, secret, user and user token
        STTarter.getInstance().init(response.getApp_key(), response.getApp_secret(),
                response.getUsername(), response.getUser_token(), response.getStt_token(),
                getApplicationContext(), AppController.getInstance().getNotificationHelperListener());
        startService(new Intent(this, MessagingService.class));
        // TODO redirect user to login screen
        Intent loginIntent = new Intent(SignUpActivity.this, MainActivity.class);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);


        STTGeneralRoutines sttGeneralRoutines = new STTGeneralRoutines();
        sttGeneralRoutines.registerApp(response.getApp_key(), response.getUsername(), STTarter.getInstance().getClientId(), Build.MANUFACTURER, Build.MODEL, Build.VERSION.SDK_INT + "", gcmRegId);

        finish();
    }

    private void loginWithOTP(String otpCode) {
        if (otpCodeEditText != null)
            otpCodeEditText.setText(otpCode);
        //confirmOTPWithServer(otpCode);
        if (SignUpActivity.this != null)
            STTarter.getInstance().confirmOTPWithServer(Constants.OTP_LOGIN,otpCode, getOtpSuccessListener(), getOTPResponseListener(), phoneEditText.getText().toString().trim(), "8", getApplicationContext());
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

    private void showTimer(String message) {
        progress.setMessage(message);
        progress.show();
    }

    private void removeTimer() {
        if (progress.isShowing())
            progress.hide();
    }

}
