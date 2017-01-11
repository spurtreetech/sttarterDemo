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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.sttarter.common.models.User;
import com.sttarter.communicator.models.Group;
import com.sttarter.communicator.ui.UserListCursorAdapter;
import com.sttarter.helper.interfaces.AddRemoveUserInterface;
import com.sttarter.helper.interfaces.GetCursor;
import com.sttarter.helper.utils.MessageCursorLoader;
import com.sttarter.helper.utils.UserCursorLoader;
import com.sttarter.provider.topics.TopicsColumns;
import com.sttarter.provider.users.UsersColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahbaz on 07-12-2016.
 */

public class AddUsersActivity extends AppCompatActivity implements GetCursor, AddRemoveUserInterface {

    EditText searchUserEdit;
    RecyclerView addUserRecyclerView;
    LinearLayout addUserLinearLayout;
    FloatingActionButton floatingButton;
    UserListCursorAdapter userListCursorAdapter;

    Runnable searchrunnable;
    Handler mHandler = new Handler();
    List<String> usersList;

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

        addUserRecyclerView.setAdapter(userListCursorAdapter);

        searchrunnable = new Runnable() {
            @Override
            public void run() {

                if (!TextUtils.isEmpty(searchUserEdit.getText().toString())) {

                    if (getSupportLoaderManager().getLoader(0) == null) {
                        getSupportLoaderManager().initLoader(0, null, new UserCursorLoader(AddUsersActivity.this, searchUserEdit.getText().toString(), AddUsersActivity.this));
                    } else {
                        getSupportLoaderManager().restartLoader(0, null, new UserCursorLoader(AddUsersActivity.this, searchUserEdit.getText().toString(), AddUsersActivity.this));
                    }

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
                String allUser = "";
                for (int i = 0; i < usersList.size(); i++) {
                    allUser = TextUtils.isEmpty(allUser) ? usersList.get(i) : allUser + "," + usersList.get(i);
                }
                Intent createGroupIntent = new Intent(AddUsersActivity.this, CreateGroupActivity.class);
                createGroupIntent.putExtra("allusers",allUser);
                startActivity(createGroupIntent);
                finish();
            }
        });

    }

    private void init() {
        searchUserEdit = (EditText) findViewById(R.id.searchUserEditText);
        addUserRecyclerView = (RecyclerView) findViewById(R.id.userRecyclerview);
        addUserLinearLayout = (LinearLayout) findViewById(R.id.usersView);
        floatingButton = (FloatingActionButton) findViewById(R.id.floatingButton);
        usersList = new ArrayList<>();
        userListCursorAdapter = new UserListCursorAdapter(AddUsersActivity.this, this, null);
        try {
            addUserRecyclerView.setLayoutManager(new LinearLayoutManager(AddUsersActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void refreshAddedUsers() {

        addUserLinearLayout.removeAllViews();
        for (int i = 0; i < usersList.size(); i++) {
            View addView = getLayoutInflater().inflate(R.layout.users_item_view, null);

            ImageView userImage = (ImageView) addView.findViewById(R.id.userImage);
            ImageButton removeUser = (ImageButton) addView.findViewById(R.id.remove);
            TextView username = (TextView) addView.findViewById(R.id.userName);

            /*Glide.with(AddUsersActivity.this)
                    .load(usersList.get(i).getAvatar())
                    .fitCenter()
                    //.placeholder(R.drawable.map_placeholder)
                    .error(R.drawable.avatar)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userImage);*/

            username.setText(usersList.get(i)/*.getUsername()*/);

            removeUser.setTag(i);

            removeUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    usersList.remove(usersList.get(pos));
                    refreshAddedUsers();
                }
            });

            addUserLinearLayout.addView(addView);
            addView.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth()/5;
        }

    }


    @Override
    public void getCursor(Cursor cursor) {
        userListCursorAdapter.swapCursor(cursor);
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
        Log.d("CursorJson", cursorJson);
    }

    @Override
    public void addOrRemove(String user, boolean add) {
        if (add) {
            if (!usersList.contains(user))
                usersList.add(user);
        } else if (!add) {
            if (usersList.contains(user))
                usersList.remove(user);
        }

        refreshAddedUsers();

    }
}
