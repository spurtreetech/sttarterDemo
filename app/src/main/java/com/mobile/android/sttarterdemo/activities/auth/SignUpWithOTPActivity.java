package com.mobile.android.sttarterdemo.activities.auth;

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
import android.text.TextUtils;
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
import com.mobile.android.sttarterdemo.activities.MainActivity;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.helper.interfaces.STTSuccessListener;
import com.sttarter.init.STTarterManager;

/**
 * Created by Shahbaz on 12/09/2016.
 */

public class SignUpWithOTPActivity extends AppCompatActivity {

    Button buttonSignUpSubmit;
    EditText editTextName, editTextEmail, phoneEditText;

    EditText otpCodeEditText;

    IntentFilter smsIntentFilter;
    SmsListener smsListener;

    ProgressDialog progress;

    private static final String TAG = "LoginResponse User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_otp);

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

                    Response.Listener<STTResponse> sttSuccessListener = new Response.Listener<STTResponse>() {
                        @Override
                        public void onResponse(STTResponse response) {
                            removeTimer();

                            if (response.getStatus()==666) {

                                loginIntoSttarter();

                                showConfirmationDialog();

                            }
                            else if (response.getStatus()==777){
                                Toast.makeText(SignUpWithOTPActivity.this,"Registered but OTP Not sent", Toast.LENGTH_LONG).show();
                            }
                            else if (response.getStatus()==888){
                                Toast.makeText(SignUpWithOTPActivity.this,"User already exists", Toast.LENGTH_LONG).show();
                            }

                        }
                    };

                    STTarterManager.getInstance().signupWithOTP(SignUpWithOTPActivity.this,getResources().getString(R.string.app_key),getResources().getString(R.string.app_secret), sttSuccessListener, getErrorListener(), editTextName.getText().toString(), phoneEditText.getText().toString(), editTextEmail.getText().toString());
                }
            }

        });


    }

    public Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                removeTimer();
                CommonFuntions.onErrorResponse(SignUpWithOTPActivity.this,error);
            }
        };
    }


    private void init() {
        buttonSignUpSubmit = (Button) findViewById(R.id.buttonSignUpSubmit);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        if (getIntent().getExtras()!=null) {
            if (getIntent().getExtras().containsKey("phone_number")) {
                phoneEditText.setText(getIntent().getExtras().getString("phone_number"));
                phoneEditText.setSelection(phoneEditText.getText().length());
            }
        }

    }

    private void showToast(String message) {
        Toast.makeText(SignUpWithOTPActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void showConfirmationDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpWithOTPActivity.this);
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


    private void loginIntoSttarter() {


        //STTarter Intialize - needs app key, secret, user and user token
        STTarterManager.getInstance().initNotificationHelper(getApplicationContext(), AppController.getInstance().getNotificationHelperListener());
        //startService(new Intent(this, MessagingService.class));
        // TODO redirect user to login screen
        Intent loginIntent = new Intent(SignUpWithOTPActivity.this, MainActivity.class);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);

        finish();
    }

    private void loginWithOTP(String otpCode) {
        if (otpCodeEditText != null)
            otpCodeEditText.setText(otpCode);
        //confirmOTPWithServer(otpCode);
        if (SignUpWithOTPActivity.this != null) {
            STTarterManager.getInstance().confirmOTPWithServer(getApplicationContext(),otpCode, getOtpSuccessListener(), getOTPResponseListener(), phoneEditText.getText().toString().trim());
        }
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
