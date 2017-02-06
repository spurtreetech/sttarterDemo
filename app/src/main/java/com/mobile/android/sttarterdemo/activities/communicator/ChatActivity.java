package com.mobile.android.sttarterdemo.activities.communicator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.communicator.CommunicationManager;
import com.sttarter.communicator.ui.BuzzFeedCursorAdapter;
import com.sttarter.communicator.ui.ChatAdapter;
import com.sttarter.helper.interfaces.STTSuccessListener;
import com.sttarter.init.ChatClient;
import com.sttarter.init.ISTTSystemEvent;
import com.sttarter.init.STTProviderHelper;
import com.sttarter.init.STTarterManager;
import com.sttarter.init.SysMessage;
import com.sttarter.init.SystemMessageReceiver;
import com.sttarter.communicator.models.Group;
import com.sttarter.helper.utils.SpacesItemDecoration;
import com.sttarter.helper.uitools.CircularNetworkImageView;
import com.sttarter.helper.utils.MessageCursorLoader;
import com.sttarter.helper.interfaces.GetCursor;

import java.util.Timer;
import java.util.TimerTask;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener/*, LoaderManager.LoaderCallbacks<Cursor>*/,GetCursor, ISTTSystemEvent {

    private RecyclerView chatRecyclerView;
    private EditText editTextSendMessage;
    private ImageButton sendButton;
    private CardView messageInputLayout;
    //private String topic, topicName, topicImageUrl, allow_reply,topicDesc,topicMembers;
    ChatAdapter ca;
    BuzzFeedCursorAdapter buzzFeedCursorAdapter;
    LoaderManager loaderManager;
    SystemMessageReceiver sysMessageReceiver;
    ActionBar actionBar;
    TextView typingText, topicNameTextView,topicDescTextView;
    RelativeLayout typingTextLayout;
    Toolbar toolBar;
    CircularNetworkImageView topicImageView;
    LayoutInflater inflater;
    View customActionBar;
    Toolbar toolbar;

    Group groupModel;
    Gson gson;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /*topic = getIntent().getStringExtra(STTKeys.CHAT_TOPIC);
        allow_reply = getIntent().getStringExtra("ALLOW_REPLY");
        topicName = (getIntent().getStringExtra("TOPIC_NAME") == null) ? "" : getIntent().getStringExtra("TOPIC_NAME");
        topicImageUrl = (getIntent().getStringExtra("TOPIC_IMAGE") == null) ? "" : getIntent().getStringExtra("TOPIC_IMAGE");
        topicDesc = (getIntent().getStringExtra("TOPIC_DESC") == null) ? "" : getIntent().getStringExtra("TOPIC_DESC");
        topicMembers = (getIntent().getStringExtra("TOPIC_GROUP") == null) ? "" : getIntent().getStringExtra("TOPIC_GROUP");*/

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        gson = new Gson();
        if (getIntent().hasExtra("topic"))
            groupModel = gson.fromJson(getIntent().getExtras().getString("topic"),Group.class);

        View customActionBar = inflater.inflate(R.layout.chat_actionbar, null);
        topicNameTextView = (TextView) customActionBar.findViewById(R.id.topicNameTextView);
        topicDescTextView = (TextView) customActionBar.findViewById(R.id.topicDescTextView);
        topicImageView = (CircularNetworkImageView) customActionBar.findViewById(R.id.topicImageView);

        topicNameTextView.setText(groupModel.getMeta().getName()+"");
        topicDescTextView.setText(groupModel.getMeta().getGroup_desc()+"");
        topicDescTextView.setSelected(true);

        topicNameTextView.setTextColor(getResources().getColor(R.color.white));
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();


        //mTitleTextView.setText("My Own Title");
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(customActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

        customActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,GroupMembersListActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            }
        });


        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        editTextSendMessage = (EditText) findViewById(R.id.sendMessage);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        messageInputLayout = (CardView) findViewById(R.id.messageInputLayout);
        typingText = (TextView) findViewById(R.id.typingText);
        typingTextLayout = (RelativeLayout) findViewById(R.id.typingTextLayout);

        sendButton.setOnClickListener(this);

        //actionBar.setLogo(R.drawable.ic_launcher);

        // Retrieves an image specified by the URL, displays it in the UI.

        if (URLUtil.isValidUrl(groupModel.getMeta().getImage())){
            topicImageView.setImageUrl(groupModel.getMeta().getImage(), STTarterManager.getInstance().getImageLoader());
        }
        else {
            topicImageView.setImageResource(R.mipmap.ic_launcher);
        }

        /*ImageRequest request = new ImageRequest(topicImageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //Drawable d = new BitmapDrawable(getResources(), bitmap);
                        //ChatActivity.this.actionBar.setLogo(d);
                        topicImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        topicImageView.setImageResource(R.drawable.ic_launcher);
                        //ChatActivity.this.actionBar.setLogo(R.drawable.ic_launcher);
                    }
                });*/

        // Access the RequestQueue through your singleton class.
        //RequestQueueHelper.addToRequestQueue(request, "");
        //actionBar.setTitle(topicName);


        if(groupModel.getMeta()!=null && groupModel.getMeta().getAllow_reply()!=null){
            if(!groupModel.getMeta().getAllow_reply().equals("true")) {
                messageInputLayout.setVisibility(View.GONE);
            }
        }

        // Get topic info, if topic is not two way then do not show the input layout and Change the background in the adapter view

        sysMessageReceiver = new SystemMessageReceiver(this);

        //chatRecyclerView.isInEditMode();
        try {
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(STTarterManager.getInstance().getContext()));
        } catch (STTarterManager.ContextNotInitializedException e) {
            e.printStackTrace();
        }


        STTProviderHelper ph = new STTProviderHelper();
        if(groupModel.getMeta()!=null && groupModel.getMeta().getAllow_reply()!=null) {
            if (!groupModel.getMeta().getAllow_reply().equals("true")) {
                buzzFeedCursorAdapter = new BuzzFeedCursorAdapter(ChatActivity.this, null);
                chatRecyclerView.setAdapter(buzzFeedCursorAdapter);
            }
            else {
                chatRecyclerView.addItemDecoration(new SpacesItemDecoration(32));
                ca = new ChatAdapter(null);
                chatRecyclerView.setAdapter(ca);
            }
        }


        editTextSendMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ChatClient.sendSystemMessage(ChatActivity.this.groupModel.getTopic(), SysMessage.user_typing);
                }
            }
        });

        initOrRestartLoader();

    }

    void initOrRestartLoader(){
        if (getSupportLoaderManager().getLoader(0) == null) {
            getSupportLoaderManager().initLoader(0, null, new MessageCursorLoader(this, groupModel.getTopic(), this));
        }
        else {
            getSupportLoaderManager().restartLoader(0, null, new MessageCursorLoader(this, groupModel.getTopic(), this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO registerReceiver with onResume
        LocalBroadcastManager.getInstance(this).registerReceiver(sysMessageReceiver, new IntentFilter("system"));
        STTarterManager.getInstance().setApplicationInBackground(false);
        updateRead();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // TODO unregister the receiver onPause
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sysMessageReceiver);
        STTarterManager.getInstance().setApplicationInBackground(true);
        updateRead();
    }

    void updateRead(){
        CommunicationManager.getInstance().updateRead(groupModel.getTopic());
    }

    @Override
    public void onClick(View v) {
        //ChatClient.send(((EditText)editTextSendMessage).getText().toString().trim(), this.topic, "message");
        if(!editTextSendMessage.getText().toString().trim().equals("")) {
            ChatClient.sendMessage(((EditText) editTextSendMessage).getText().toString().trim(), this.groupModel.getTopic());
            editTextSendMessage.setText("");
        }
    }

    @Override
    public void systemMessageReceived(String message, SysMessage eventType, String topic) {
        Log.d(getClass().getSimpleName(), "System event received: " + message + ", " + eventType + ", " + topic);

        typingText.setText(message);
        typingTextLayout.setVisibility(View.VISIBLE);
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ChatActivity.this.setTitle(ChatActivity.this.getClass().getSimpleName());
                        ChatActivity.this.typingTextLayout.setVisibility(View.GONE);
                    }
                });
            }
        };
        Timer t = new Timer();
        t.schedule(tt, 1500);

    }

    @Override
    public void refreshUI() {
        initOrRestartLoader();
    }

    private STTSuccessListener sttSuccessListener(){
        return new STTSuccessListener() {
            @Override
            public void Response(STTResponse sttResponse) {
                removeTimer();
                CommonFuntions.displayMessage(ChatActivity.this,sttResponse.getMsg());
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
                CommonFuntions.onErrorResponse(ChatActivity.this,error);
            }
        };
    }

    private void showTimer(String message) {
        progress.setMessage(message);
        progress.show();
    }

    private void removeTimer() {
        if (progress.isShowing())
            progress.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_unsubscribe, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.unsubscribe:
                showTimer("Loading..");
                if (groupModel.getTopic()!=null && groupModel.getTopic().contains("-group-"))
                    CommunicationManager.getInstance().leaveGroup(ChatActivity.this,groupModel.getTopic(),sttSuccessListener(),errorListener());
                else
                    removeTimer();
                //STTarterManager.getInstance().unsubscribe(topic);
                //Log.d("STTTopicsCursorAdapter", "unsubscribed");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getCursor(Cursor cursor) {
        if(groupModel.getMeta()!=null && groupModel.getMeta().getAllow_reply()!=null) {
            if (!groupModel.getMeta().getAllow_reply().equals("true")) {
                buzzFeedCursorAdapter.swapCursor(cursor);
                //ca.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(buzzFeedCursorAdapter.getItemCount() - 1);
            }else {
                ca.swapCursor(cursor);
                //ca.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(ca.getItemCount() - 1);
            }
        }

    }

    @Override
    public void getCursorJsonArrayString(String cursorJson) {
        Log.d("MyMsgTag",cursorJson);
    }
}
