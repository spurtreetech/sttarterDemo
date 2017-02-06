package com.mobile.android.sttarterdemo.fragments.communicator;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.communicator.AddUsersActivity;
import com.mobile.android.sttarterdemo.activities.communicator.ChatActivity;
import com.sttarter.communicator.CommunicationManager;
import com.sttarter.communicator.models.Group;
import com.sttarter.communicator.ui.ChatHistoryCursorAdapter;
import com.sttarter.database.models.TopicsColumns;
import com.sttarter.helper.interfaces.GetCursor;
import com.sttarter.helper.utils.GroupCursorLoader;
import com.sttarter.helper.utils.SpacesItemDecoration;
import com.sttarter.init.ISTTSystemEvent;
import com.sttarter.init.STTProviderHelper;
import com.sttarter.init.STTarterManager;
import com.sttarter.init.SysMessage;
import com.sttarter.init.SystemMessageReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

public class MessagesFragment extends Fragment implements /*LoaderManager.LoaderCallbacks<Cursor>,*/ ChatHistoryCursorAdapter.ChatInitiateListener, GetCursor,ISTTSystemEvent {

    Activity activity;
    RecyclerView recyclerViewMessages;

    private ChatHistoryCursorAdapter chatHistoryCursorAdapter;
    LoaderManager loaderManager;
    RecyclerView chatHistoryRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textIfNoGroupView;
    Uri uri;
    //CommunicationManager sttGeneralRoutines;
    Bundle bundleARG;

    SystemMessageReceiver sysMessageReceiver;

    public static MessagesFragment newInstance() {

        Bundle args = new Bundle();

        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        try {
            sysMessageReceiver = new SystemMessageReceiver(this);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(sysMessageReceiver, new IntentFilter("system"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //uri = Uri.parse("content://" + STTContentProvider.AUTHORITY + "/" + DatabaseHelper.TABLE_TOPICS + "/getMyTopics");
        //sttGeneralRoutines = new CommunicationManager();
        bundleARG = getArguments();
        try {
            STTProviderHelper ph = new STTProviderHelper();
            chatHistoryCursorAdapter = new ChatHistoryCursorAdapter(getActivity(), null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        init(rootView);

        chatHistoryRecyclerView.isInEditMode();
        try {
            chatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (Exception e) {
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
        try {
            CommunicationManager.getInstance().subscribeInitalize();
            STTarterManager.getInstance().setApplicationInBackground(false);
            initOrRefreshLoader();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void initOrRefreshLoader(){
        if (getActivity().getSupportLoaderManager().getLoader(0) == null) {
            getActivity().getSupportLoaderManager().initLoader(0, null, new GroupCursorLoader(getActivity(), this));
        }
        else {
            getActivity().getSupportLoaderManager().restartLoader(0, null, new GroupCursorLoader(getActivity(), this));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        STTarterManager.getInstance().setApplicationInBackground(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;


    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(sysMessageReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(View rootView) {

        activity = getActivity();
        chatHistoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewMessages);
        textIfNoGroupView = (TextView) rootView.findViewById(R.id.textViewNoNews);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initOrRefreshLoader();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void chatClicked(String chatId, String/*GroupMeta*/ tm/*, String groupMembers*/) {
        Log.d("ChatHistoryFragment", "chatId - " + chatId);
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
        /*chatIntent.putExtra(STTKeys.CHAT_TOPIC, chatId);
        chatIntent.putExtra("ALLOW_REPLY", tm.getAllow_reply());
        chatIntent.putExtra("TOPIC_NAME", tm.getName());
        chatIntent.putExtra("TOPIC_DESC", tm.getGroup_desc());
        chatIntent.putExtra("TOPIC_IMAGE", tm.getImage());
        chatIntent.putExtra("TOPIC_GROUP", groupMembers);*/
        //chatIntent.putExtra("TOPIC", )

        chatIntent.putExtra("topic",tm);

        startActivity(chatIntent);
    }

    @Override
    public void getCursor(Cursor data) {
        Log.d("MessagesFragment", "data loaded size - " + data.getCount());
        chatHistoryCursorAdapter.swapCursor(data);
        //if(data.getCount()!=0) {

        if (data.getCount() == 0) {
            textIfNoGroupView.setVisibility(View.VISIBLE);
            textIfNoGroupView.setText(getActivity().getString(R.string.no_groups_available));
        } else {
            textIfNoGroupView.setVisibility(View.GONE);
        }

        if (bundleARG != null && bundleARG.containsKey("notification_data")) {
            try {
                JSONObject jsonObject1;
                String TopicName = "";  //60bf1fa3148cc0a95c33377e7773cd13-group-1
                boolean multiTopics = false;
                boolean buzzPresent = false;

                Log.d("Buzzed>>>>>", "" + bundleARG.containsKey("notification_data"));
                jsonObject1 = new JSONObject(bundleARG.getString("notification_data"));
                JSONArray jsonArray = new JSONArray(jsonObject1.getString("topic_name"));

                Log.d("Buzz>>>>>", jsonObject1.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    String topicNameInArray = jsonArray.getString(i);
                    if (topicNameInArray.contains("org")) {
                        buzzPresent = true;
                    }

                }

                if (jsonObject1.getInt("count") >= 1 && buzzPresent) {

                } else if (jsonObject1.getInt("count") > 1) {
                    multiTopics = true;
                } else if (jsonObject1.getInt("count") == 1) {
                    if (jsonArray.length() != 0) {

                        // JSONObject jsonObject = jsonArray.getJSONObject(0);

                        if (!buzzPresent) {
                            TopicName = jsonArray.getString(0);
                        }

                    }
                }


                data.moveToFirst();
                while (!data.isAfterLast() && bundleARG != null) {

                    if (data.getString(data.getColumnIndex(TopicsColumns.TOPIC_NAME)).equals(TopicName)) {

                        Gson gson = new Gson();
                        Group group = TopicsColumns.completeRow(data);
                        String jsonTopic = gson.toJson(group);

                        chatClicked(data.getString(data.getColumnIndex(TopicsColumns.TOPIC_NAME)), jsonTopic/*gson.fromJson(data.getString(data.getColumnIndex(TopicsColumns.TOPIC_META)), GroupMeta.class), data.getString(data.getColumnIndex(TopicsColumns.TOPIC_GROUP_MEMBERS))*/);
                        bundleARG = null;
                        getArguments().clear();
                        break;
                    } else {
                        data.moveToNext();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void getCursorJsonArrayString(String cursorJson) {
        Log.d("MyGrpTag",cursorJson);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_messages, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createGroup:
                startActivity(new Intent(getActivity(), AddUsersActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void systemMessageReceived(String message, SysMessage eventType, String topic) {

    }

    @Override
    public void refreshUI() {
        initOrRefreshLoader();
    }
}