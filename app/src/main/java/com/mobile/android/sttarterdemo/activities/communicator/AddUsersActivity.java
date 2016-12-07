package com.mobile.android.sttarterdemo.activities.communicator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.sttarter.common.models.User;
import com.sttarter.communicator.models.Group;
import com.sttarter.helper.interfaces.GetCursor;
import com.sttarter.helper.utils.MessageCursorLoader;
import com.sttarter.helper.utils.UserCursorLoader;
import com.sttarter.provider.topics.TopicsColumns;
import com.sttarter.provider.users.UsersColumns;

/**
 * Created by Shahbaz on 07-12-2016.
 */

public class AddUsersActivity extends AppCompatActivity implements GetCursor{

    EditText searchUserEdit;
    RecyclerView addUserRecyclerView;
    FloatingActionButton floatingButton;

    Runnable searchrunnable;
    Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_users_activity);

        init();

        try {
            addUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchrunnable = new Runnable() {
            @Override
            public void run() {

                if (!TextUtils.isEmpty(searchUserEdit.getText().toString())) {

                    getSupportLoaderManager().initLoader(0,null, new UserCursorLoader(AddUsersActivity.this, searchUserEdit.getText().toString(),AddUsersActivity.this));

                } else {

                }
            }

        };

        searchUserEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mHandler.removeCallbacks(searchrunnable);
                mHandler.postDelayed(searchrunnable, 3000);
            }
        });

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AddUsersActivity.this,CreateGroupActivity.class));
                finish();
            }
        });

    }

    private void init() {
        searchUserEdit = (EditText) findViewById(R.id.searchUserEditText);
        addUserRecyclerView = (RecyclerView) findViewById(R.id.userRecyclerview);
        floatingButton = (FloatingActionButton) findViewById(R.id.floatingButton);
    }

    @Override
    public void getCursor(Cursor cursor) {
        if (cursor.getCount() == 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                //if (cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_NAME)).equals(TopicName)) {

                    Gson gson = new Gson();
                    User user = UsersColumns.completeRow(cursor);
                    String jsonTopic = gson.toJson(user);

                    //chatClicked(cursor.getString(cursor.getColumnIndex(UsersColumns.USERS_USERNAME)), jsonTopic/*gson.fromJson(data.getString(data.getColumnIndex(TopicsColumns.TOPIC_META)), GroupMeta.class), data.getString(data.getColumnIndex(TopicsColumns.TOPIC_GROUP_MEMBERS))*/);
                    break;
                /*} else {
                    cursor.moveToNext();
                }*/
            }

        } else {

        }
    }

    @Override
    public void getCursorJsonArrayString(String cursorJson) {
        Log.d("CursorJson",cursorJson);
    }
}
