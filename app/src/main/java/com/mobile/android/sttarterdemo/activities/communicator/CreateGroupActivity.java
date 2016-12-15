package com.mobile.android.sttarterdemo.activities.communicator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.communicator.CommunicationManager;
import com.sttarter.helper.interfaces.STTSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Shahbaz on 06-12-2016.
 */

public class CreateGroupActivity extends AppCompatActivity {

    List<String> userList;
    FloatingActionButton createGroup;
    EditText groupNameEdit;

    ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);
        init();

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(groupNameEdit.getText().toString())) {
                    showTimer("Loading..");
                    CommunicationManager.getInstance().createGroup(groupNameEdit.getText().toString(),getIntent().getExtras().getString("allusers"),sttSuccessListener(),errorListener());
                }
            }
        });

    }

    private STTSuccessListener sttSuccessListener(){
        return new STTSuccessListener() {
            @Override
            public void Response(STTResponse sttResponse) {
                removeTimer();
                CommonFuntions.displayMessage(CreateGroupActivity.this,sttResponse.getMsg());
                if (sttResponse.getStatus()==200){
                    finish();
                }
            }
        };
    }

    private Response.ErrorListener errorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                removeTimer();
                CommonFuntions.onErrorResponse(CreateGroupActivity.this,error);
            }
        };
    }

    private void init() {

        createGroup = (FloatingActionButton) findViewById(R.id.createGroup);
        groupNameEdit = (EditText) findViewById(R.id.groupName);

        userList = new ArrayList<>();
        String allusersString = getIntent().getExtras().getString("allusers");
        String[] users = allusersString.split(",");

        userList = Arrays.asList(users);

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
