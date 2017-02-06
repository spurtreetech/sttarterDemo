package com.mobile.android.sttarterdemo.activities.auth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.MainActivity;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.common.utils.STTarterConstants;
import com.sttarter.helper.interfaces.STTSuccessListener;
import com.sttarter.init.STTKeys;
import com.sttarter.init.STTarterManager;

/**
 * Created by kevalprabhu on 18/11/16.
 */

public class LoginWithOTPActivity extends AppCompatActivity implements View.OnClickListener{

    Button loginButton;
    EditText phoneNumberEditText, otpCodeEditText;
    ImageView imageViewlolo;

    IntentFilter smsIntentFilter;
    SmsListener smsListener;
    TextView newUserSignUp;

    private static final String TAG = "LoginResponse User";
    Dialog otpDialog;

    Context ctx;

    ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        ctx = this;

        loginButton = (Button) findViewById(R.id.loginButton);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        newUserSignUp = (TextView) findViewById(R.id.newUserSignUp);
        loginButton.setOnClickListener(this);
        newUserSignUp.setOnClickListener(this);

        smsListener = new SmsListener();
        smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        smsIntentFilter.setPriority(2147483647);
        this.registerReceiver(smsListener, smsIntentFilter);

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);


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

                STTarterManager.getInstance().init(getApplicationContext(),getResources().getString(R.string.app_key),getResources().getString(R.string.app_secret), phoneNumberEditText.getText().toString().trim(),"",getOTPSuccessListener(), getLoginResponseListener(), STTarterConstants.AuthType.STTARTER_OTP_AUTH);

            }
        }
        else if (v.getId() == R.id.newUserSignUp){
            startActivity(new Intent(this,SignUpWithOTPActivity.class));
            finish();
        }
        /* else if (v.getId() == R.id.confirmButton) {
            confirmOTPWithServer();
        } else if (v.getId() == R.id.dismissButton) {
            otpDialog.dismiss();
        }*/
    }


    private STTSuccessListener getOTPSuccessListener() {

        return new STTSuccessListener() {
            @Override
            public void Response(STTResponse sttResponse) {
                removeTimer();
                String message = "";
                if (sttResponse.getStatus()==STTKeys.PERFECT_RESPONSE) {
                    showConfirmationDialog();
                } else if (sttResponse.getStatus() == STTKeys.USER_NOT_FOUND || sttResponse.getStatus() == STTKeys.ORGANISATION_NOT_FOUND) {
                    message = "Invalid User or Organization chosen. Please check the entries and try again.";
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginWithOTPActivity.this,SignUpWithAccountActivity.class).putExtra("phone_number",phoneNumberEditText.getText().toString()));
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
                CommonFuntions.onErrorResponse(LoginWithOTPActivity.this,error);
            }
        };
    }


    //PART 2 - Get the OTP and then continue

    private void showConfirmationDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginWithOTPActivity.this);
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

    }


    // On click on SUBMIT BUTTON
    public void confirmOTPWithServer() {
        if (otpCodeEditText.getText().toString() != null && otpCodeEditText.getText().toString().trim().trim() != "") {
            showTimer("Please wait..");
            loginWithOTP(otpCodeEditText.getText().toString().trim());
        } else {
            String message = "Please enter a valid OTP in order to continue.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

    }

    private STTSuccessListener getOtpSuccessListener() {

        return new STTSuccessListener() {
            @Override
            public void Response(STTResponse sttResponse) {
                removeTimer();
                if (sttResponse.getStatus()==200) {
                    //We will receive the APP Key and APP Secret here. We will need to set this up.
                    loginIntoSttarter();

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
        if (LoginWithOTPActivity.this != null) {
            STTarterManager.getInstance().confirmOTPWithServer(getApplicationContext(), otpCode, getOtpSuccessListener(), getOTPResponseListener(), phoneNumberEditText.getText().toString().trim());
        }
    }

    private void loginIntoSttarter() {

        //STTarter Intialize - needs app key, secret, user and user token
        STTarterManager.getInstance().initNotificationHelper(getApplicationContext(), AppController.getInstance().getNotificationHelperListener());
        // TODO redirect user to login screen
        Intent loginIntent = new Intent(LoginWithOTPActivity.this, MainActivity.class);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);

        finish();
    }

    private void showTimer(String message) {
        progress.setMessage(message);
        progress.show();
    }

    private void removeTimer() {
        if (progress.isShowing())
            progress.hide();
    }

    private void showToast(String message) {
        Toast.makeText(LoginWithOTPActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}