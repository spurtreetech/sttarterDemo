package com.mobile.android.sttarterdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.auth.LoginExternalAuthActivity;
import com.mobile.android.sttarterdemo.activities.auth.LoginWithAccountActivity;
import com.mobile.android.sttarterdemo.activities.auth.LoginWithOTPActivity;
import com.mobile.android.sttarterdemo.application_controller.AppController;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.init.STTarterManager;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.helper.interfaces.STTSuccessListener;

/**
 * Created by kevalprabhu on 18/11/16.
 */

public class SplashActivity extends AppCompatActivity {

    Button btnSTTarterAuth, btnSTTarterOTP, btnCustomAuth;
    String appKey = "cb5d85ce01bbc179fece64b29be895d0", appSecret = "a5a2e3d81d8b57ec0fb4647079486678";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (STTarterManager.getInstance().isUserAuthenticated(SplashActivity.this)){
            STTarterManager.getInstance().init(SplashActivity.this, AppController.getInstance().getNotificationHelperListener());
            moveToMainActivity();
        }
        else {
            initializeUIVariables();
            initializeSTTarter();
        }
    }

    protected void initializeUIVariables(){
        btnSTTarterAuth = (Button)findViewById(R.id.btnSTTarterAuth);
        btnSTTarterOTP = (Button)findViewById(R.id.btnSTTarterOTP);
        btnCustomAuth = (Button)findViewById(R.id.btnCustomAuth);
    }

    private void moveToMainActivity(){
        Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void initializeSTTarter(){
        // At this point of time, the APP KEY and SECRET is available with the developer.
        // Authenticate the app first.

        STTSuccessListener STTSuccessListener = new STTSuccessListener() {
            @Override
            public void Response(STTResponse myResponse) {
                // STTarterManager has successfully authenticated the app key and secret.
                // Can proceed with the user authentication and other services.
                setOnClickListeners();
            }
        };

        STTarterManager.getInstance().AuthenticateApp(this,appKey,appSecret, STTSuccessListener,getAuthResponseListener());
    }

    public Response.ErrorListener getAuthResponseListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                CommonFuntions.onErrorResponse(SplashActivity.this,error);
            }
        };
    }


    private void setOnClickListeners(){
        btnSTTarterAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginWithAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSTTarterOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginWithOTPActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCustomAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginExternalAuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }







}
