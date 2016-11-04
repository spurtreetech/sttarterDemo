package com.mobile.android.sttarterdemo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.ChatActivity;
import com.mobile.android.sttarterdemo.adapters.ChatHistoryCursorAdapter;
import com.spurtreetech.sttarter.lib.helper.STTGeneralRoutines;
import com.spurtreetech.sttarter.lib.helper.STTKeys;
import com.spurtreetech.sttarter.lib.helper.STTarter;
import com.spurtreetech.sttarter.lib.helper.models.TopicMeta;
import com.spurtreetech.sttarter.lib.helper.utils.SpacesItemDecoration;
import com.spurtreetech.sttarter.lib.provider.STTProviderHelper;
import com.spurtreetech.sttarter.lib.provider.topics.TopicsColumns;
import com.spurtreetech.sttarter.lib.provider.topics.TopicsSelection;

public class MessagesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ChatHistoryCursorAdapter.ChatInitiateListener {

    Activity activity;
    RecyclerView recyclerViewMessages;

    private ChatHistoryCursorAdapter chatHistoryCursorAdapter;
    LoaderManager loaderManager;
    RecyclerView chatHistoryRecyclerView;
    TextView textIfNoGroupView;
    Uri uri;
    STTGeneralRoutines sttGeneralRoutines;
    Bundle bundleARG;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //uri = Uri.parse("content://" + STTContentProvider.AUTHORITY + "/" + DatabaseHelper.TABLE_TOPICS + "/getMyTopics");
        sttGeneralRoutines = new STTGeneralRoutines();
        bundleARG = getArguments();
        try {
            STTProviderHelper ph = new STTProviderHelper();
            chatHistoryCursorAdapter = new ChatHistoryCursorAdapter(STTarter.getInstance().getContext(), null, this);
        } catch (STTarter.ContextNotInitializedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        init(rootView);

        chatHistoryRecyclerView.isInEditMode();
        try {
            chatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(STTarter.getInstance().getContext()));
        } catch (STTarter.ContextNotInitializedException e) {
            e.printStackTrace();
        }

        STTProviderHelper ph = new STTProviderHelper();
        chatHistoryRecyclerView.addItemDecoration(new SpacesItemDecoration(0));

        chatHistoryRecyclerView.setAdapter(chatHistoryCursorAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sttGeneralRoutines.getMyTopics();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void init(View rootView) {

        activity = getActivity();
        chatHistoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewMessages);
        textIfNoGroupView = (TextView) rootView.findViewById(R.id.textViewNoNews);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){

        }
    }

    @Override
    public void chatClicked(String chatId, TopicMeta tm, String groupMembers) {
        Log.d("ChatHistoryFragment", "chatId - " + chatId);
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        chatIntent.putExtra(STTKeys.CHAT_TOPIC, chatId);
        chatIntent.putExtra("ALLOW_REPLY", tm.getAllow_reply());
        chatIntent.putExtra("TOPIC_NAME", tm.getName());
        chatIntent.putExtra("TOPIC_DESC", tm.getGroup_desc());
        chatIntent.putExtra("TOPIC_IMAGE", tm.getImage());
        chatIntent.putExtra("TOPIC_GROUP", groupMembers);
        //chatIntent.putExtra("TOPIC", )
        startActivity(chatIntent);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        TopicsSelection where = new TopicsSelection();
        switch (id) {
            case 0:
                where.topicIsSubscribed(true).and().topicTypeNot("master").and().topicTypeNot("org");
                where.orderBy(TopicsColumns.TOPIC_UPDATED_UNIX_TIMESTAMP, true);
                break;
            default:
                break;
        }

        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), TopicsColumns.CONTENT_URI, null, where.sel(), where.args(), where.order());
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        switch(loader.getId()) {
            case 0:

                Log.d("MessagesFragment", "Data Loaded: data size - " + data.getCount());
                data.setNotificationUri(
                        activity.getContentResolver(),
                        TopicsColumns.CONTENT_URI);

                Log.d("MessagesFragment", "data loaded size - " + data.getCount());
                chatHistoryCursorAdapter.swapCursor(data);
                //if(data.getCount()!=0) {

                if (data.getCount()==0){
                    textIfNoGroupView.setVisibility(View.VISIBLE);
                    textIfNoGroupView.setText(getActivity().getString(R.string.no_groups_available));
                }
                else {
                    textIfNoGroupView.setVisibility(View.GONE);
                }

                if (bundleARG !=null && bundleARG.containsKey("Topic")){

                    data.moveToFirst();
                    while (!data.isAfterLast() && bundleARG != null){

                        if (data.getString(data.getColumnIndex(TopicsColumns.TOPIC_NAME)).equals(getArguments().getString("Topic"))){

                            Gson gson = new Gson();
                            chatClicked(data.getString(data.getColumnIndex(TopicsColumns.TOPIC_NAME)),gson.fromJson(data.getString(data.getColumnIndex(TopicsColumns.TOPIC_META)), TopicMeta.class), data.getString(data.getColumnIndex(TopicsColumns.TOPIC_GROUP_MEMBERS)));
                            bundleARG = null;
                            getArguments().clear();
                            break;
                        }
                        else {
                            data.moveToNext();
                        }
                    }

                }

                //}

        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        chatHistoryCursorAdapter.swapCursor(null);
    }
}