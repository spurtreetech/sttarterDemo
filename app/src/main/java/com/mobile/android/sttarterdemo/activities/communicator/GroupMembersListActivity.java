package com.mobile.android.sttarterdemo.activities.communicator;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.adapters.MyRecyclerAdapter;
import com.sttarter.communicator.models.Group;
import com.sttarter.communicator.models.GroupMembers;
import com.sttarter.communicator.models.GroupUser;

/**
 * Created by Shahbaz on 9/15/2015.
 */
public class GroupMembersListActivity extends AppCompatActivity {

    RecyclerView recyclerViewMembers;
    GroupMembers group_members;
    MyRecyclerAdapter myRecyclerAdapter;
    TextView descriptionText;
    Toolbar toolBar;
    String topicImageUrl;

    Gson gson;
    Group groupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        recyclerViewMembers = (RecyclerView) findViewById(R.id.groupmembersRecyclerView);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);

        gson = new Gson();
        if (getIntent().hasExtra("topic"))
            groupModel = gson.fromJson(getIntent().getExtras().getString("topic"),Group.class);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(groupModel.getMeta().getName());

        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        descriptionText = (TextView) findViewById(R.id.descText);

        topicImageUrl = groupModel.getMeta().getImage();

        if (URLUtil.isValidUrl(topicImageUrl)) {

            Glide.with(this)
                    .load(topicImageUrl)
                    .fitCenter()
                    //.placeholder(R.drawable.map_placeholder)
                    .error(R.mipmap.ic_launcher)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

        }
        else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        descriptionText.setText(groupModel.getMeta().getGroup_desc()!=null?groupModel.getMeta().getGroup_desc():"");

        myRecyclerAdapter = new MyRecyclerAdapter(this,groupModel.getGroup_members());

        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewMembers.setAdapter(myRecyclerAdapter);

        myRecyclerAdapter.notifyDataSetChanged();
    }

}
