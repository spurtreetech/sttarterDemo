package com.mobile.android.sttarterdemo.activities.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.MainActivity;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.init.STTarterManager;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.common.models.User;
import com.sttarter.helper.interfaces.STTSuccessListener;


/**
 * Created by Aishvarya on 10/21/2016.
 */

public class SignUpWithAccountActivity extends AppCompatActivity {

    Button buttonSignUpSubmit;
    EditText editTextName, editTextEmail, phoneEditText,editTextUsername,editTextPassword,editTextReferralCode;

    ProgressDialog progress;

    private static final String TAG = "SignUp Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_account);

        initializeProgressIndicator();
        init();

        buttonSignUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextName.getText().toString())) {
                    editTextName.setError(getResources().getString(R.string.please_enter_your_name));
                } else if (TextUtils.isEmpty(editTextUsername.getText().toString())) {
                    editTextUsername.setError(getResources().getString(R.string.please_enter_your_username));
                } else if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getResources().getString(R.string.please_enter_your_email_id));
                } else if (!isValidEmail(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getResources().getString(R.string.please_enter_valid_email_id));
                }else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
                    phoneEditText.setError(getResources().getString(R.string.please_enter_phone_number));
                }  else {
                    doSignUp();
                }
            }

        });
    }

    private void doSignUp(){
        String name, email, mobile,username,password;
        showProgressIndicator("Please wait..");
        name= editTextName.getText().toString();
        email = editTextEmail.getText().toString();
        mobile = phoneEditText.getText().toString();
        username = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();


        if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(username)|| TextUtils.isEmpty(password))){

            String referralCode = editTextReferralCode.getText().toString();
            User signUpModel = new User();
            signUpModel.setName(name);
            signUpModel.setEmail(email);
            signUpModel.setMobile(mobile);
            signUpModel.setUsername(username);
            signUpModel.setPassword(password);

            STTSuccessListener STTSuccessListener = new STTSuccessListener() {
                @Override
                public void Response(STTResponse myResponse) {
                    hideProgressIndicator();

                    STTarterManager.getInstance().initNotificationHelper(SignUpWithAccountActivity.this, AppController.getInstance().getNotificationHelperListener());
                    moveToMainActivity();

                }
            };

            //Sign up with STTarterManager.
            STTarterManager.getInstance().signupSTTarterAuth(this,getResources().getString(R.string.app_key),getResources().getString(R.string.app_secret),signUpModel,referralCode, STTSuccessListener,getSignUpErrorListener());

        }
    }

    public Response.ErrorListener getSignUpErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                hideProgressIndicator();

                CommonFuntions.onErrorResponse(SignUpWithAccountActivity.this,error);

            }
        };
    }

    private void moveToMainActivity() {
        Intent loginIntent = new Intent(SignUpWithAccountActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void initializeProgressIndicator(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
    }

    private void init() {
        buttonSignUpSubmit = (Button) findViewById(R.id.buttonSignUp);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        phoneEditText = (EditText) findViewById(R.id.editTextMobile);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextReferralCode = (EditText) findViewById(R.id.editTextReferralCode);

        /*if (getIntent().getExtras().containsKey("phone_number")){
            phoneEditText.setText(getIntent().getExtras().getString("phone_number"));
            phoneEditText.setSelection(phoneEditText.getText().length());
        }*/

    }

    private void showToast(String message) {
        Toast.makeText(SignUpWithAccountActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    private void showProgressIndicator(String message) {
        progress.setMessage(message);
        progress.show();
    }

    private void hideProgressIndicator() {
        if (progress.isShowing())
            progress.hide();
    }

}
