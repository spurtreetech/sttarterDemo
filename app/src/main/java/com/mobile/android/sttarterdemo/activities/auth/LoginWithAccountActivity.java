package com.mobile.android.sttarterdemo.activities.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.MainActivity;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.utils.STTarterConstants;
import com.sttarter.init.STTarterManager;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.helper.interfaces.STTSuccessListener;

/**
 * Created by Shahbaz on 10/18/2016.
 */

public class LoginWithAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LOGIN_WITH_ACCOUNT";

    EditText usernameEditText, passwordEditText;
    Button loginButton;
    ImageView imageViewLogo;
    TextView newUserSignUp;
    Context ctx;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);
        ctx = this;

        //TODO: If user has already been authenticated in the past. Need STTarterManager to tell us.
        boolean isUserAuthenticated = STTarterManager.getInstance().isUserAuthenticated(LoginWithAccountActivity.this);
        //Check if the user has already been authenticated.

        if (isUserAuthenticated) {
            // Move to the next page - main activity
            STTarterManager.getInstance().initNotificationHelper(LoginWithAccountActivity.this, AppController.getInstance().getNotificationHelperListener());
            moveToMainActivity();
        } else {
            initializeUIVariables();
            initializeProgressIndicator();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressIndicator();
    }

    private void initializeUIVariables() {
        loginButton = (Button) findViewById(R.id.loginButton);
        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        imageViewLogo = (ImageView) findViewById(R.id.logo);
        newUserSignUp = (TextView) findViewById(R.id.newUserSignUp);
        loginButton.setOnClickListener(this);
        newUserSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = "", password = "";
        if (v.getId() == R.id.loginButton) {
            if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
                usernameEditText.setError(getResources().getString(R.string.please_enter_your_username));
            } else {
                username = usernameEditText.getText().toString();
            }

            if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
                passwordEditText.setError(getResources().getString(R.string.password_needed));
            } else {
                password = passwordEditText.getText().toString();
            }

            doLogin(username, password);
        } else if (v.getId() == R.id.newUserSignUp) {
            Intent intent = new Intent(this, SignUpWithAccountActivity.class);
            startActivity(intent);
        }
    }

    // STTarterManager Integration
    //  User Authentication with STTarterManager Account With Username and Password stored in STTarterManager
    public void doLogin(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            //Appropriate error message to be shown.

        } else {
            showProgressIndicator("Please wait...");
            STTSuccessListener STTSuccessListener = new STTSuccessListener() {
                @Override
                public void Response(final STTResponse myResponse) {
                    hideProgressIndicator();
                    // Go to the next screen. User has been successfully authenticated by STTarterManager
                    STTarterManager.getInstance().initNotificationHelper(LoginWithAccountActivity.this, AppController.getInstance().getNotificationHelperListener());
                    moveToMainActivity();

                }
            };

            STTarterManager.getInstance().init(this,getResources().getString(R.string.app_key),getResources().getString(R.string.app_secret), username, password, STTSuccessListener, getLoginErrorListener(), STTarterConstants.AuthType.STTARTER_ACCOUNT_AUTH);
        }
    }

    public Response.ErrorListener getLoginErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                hideProgressIndicator();
                CommonFuntions.onErrorResponse(LoginWithAccountActivity.this,error);
            }
        };
    }


    private void moveToMainActivity() {
        Intent loginIntent = new Intent(LoginWithAccountActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void initializeProgressIndicator() {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
    }

    private void showProgressIndicator(String message) {
        try {
            progress.setMessage(message);
            progress.show();
        } catch (Exception e) {

        }
    }

    private void hideProgressIndicator() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }
}
