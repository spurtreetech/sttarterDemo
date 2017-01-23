package com.mobile.android.sttarterdemo.activities.communicator;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.sttarter.common.models.User;
import com.sttarter.communicator.ui.UserListCursorAdapter;
import com.sttarter.helper.interfaces.GetCursor;
import com.sttarter.helper.utils.UserCursorLoader;
import com.sttarter.provider.users.UsersColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahbaz on 07-12-2016.
 */

public class AllUsersActivity extends Fragment implements GetCursor {

    RecyclerView addUserRecyclerView;
    UserListCursorAdapter userListCursorAdapter;

    Runnable searchrunnable;
    Handler mHandler = new Handler();
    List<String> usersList;

    public static AllUsersActivity newInstance() {

        Bundle args = new Bundle();

        AllUsersActivity fragment = new AllUsersActivity();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_users_activity,null);

        init(rootView);

        try {
            addUserRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        addUserRecyclerView.setAdapter(userListCursorAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity().getSupportLoaderManager().getLoader(1) == null) {
            getActivity().getSupportLoaderManager().initLoader(1, null, new UserCursorLoader(getActivity(), "", AllUsersActivity.this));
        } else {
            getActivity().getSupportLoaderManager().restartLoader(1, null, new UserCursorLoader(getActivity(), "", AllUsersActivity.this));
        }
    }

    private void init(View rootView) {
        addUserRecyclerView = (RecyclerView) rootView.findViewById(R.id.userRecyclerview);
        usersList = new ArrayList<>();
        userListCursorAdapter = new UserListCursorAdapter(getActivity(), null, null);
        try {
            addUserRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
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

}
