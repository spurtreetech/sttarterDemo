package com.mobile.android.sttarterdemo.activities.communicator;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.adapters.MyRecyclerAdapter;
import com.sttarter.communicator.models.GroupMembers;

/**
 * Created by Shahbaz on 9/15/2015.
 */
public class GroupMembersListActivity extends AppCompatActivity {

    //RecyclerView recyclerViewMembers;
    GroupMembers group_members;
    MyRecyclerAdapter myRecyclerAdapter;
    TextView descriptionText;
    Toolbar toolBar;
    String topicImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        //recyclerViewMembers = (RecyclerView) findViewById(R.id.groupmembersRecyclerView);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getExtras().getString("TOPIC_NAME"));

        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        descriptionText = (TextView) findViewById(R.id.descText);

        topicImageUrl = getIntent().getExtras().getString("TOPIC_IMAGE");

        if (URLUtil.isValidUrl(topicImageUrl)) {

            //topicImageUrl.setImageToImageView(topicImageUrl, imageView);

        }
        else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        descriptionText.setText(getIntent().getExtras().containsKey("TOPIC_DESC")?getIntent().getExtras().getString("TOPIC_DESC"):"");



        /*Gson gson = new Gson();

        Log.d("TOPIC_GROUP",getIntent().getExtras().getString("TOPIC_GROUP"));

        GroupUser[] group_membersArrays = gson.fromJson(getIntent().getExtras().getString("TOPIC_GROUP"),GroupUser[].class);

        myRecyclerAdapter = new MyRecyclerAdapter(this,group_membersArrays);

        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewMembers.setAdapter(myRecyclerAdapter);

        myRecyclerAdapter.notifyDataSetChanged();*/
    }

}
