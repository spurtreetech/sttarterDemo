package com.mobile.android.sttarterdemo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mobile.android.sttarterdemo.R;
import com.sttarter.communicator.ui.BuzzFeedCursorAdapter;

/**
 * Created by Shahbaz on 14-11-2016.
 */

public class ExtendBuzz extends BuzzFeedCursorAdapter {

    public ExtendBuzz(Context context, Cursor cursor) {
        super(context, cursor);

    }

    @Override
    public BuzzFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buzz_feed, parent, false);
        return new BuzzFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        super.onBindViewHolder(holder, cursor);

        ExtendedBuzzViewHolder extendedBuzzViewHolder = (ExtendedBuzzViewHolder) holder;



    }

    class ExtendedBuzzViewHolder extends BuzzFeedViewHolder {

        public TextView message, time;
        NetworkImageView messageImg;

        public ExtendedBuzzViewHolder(View itemView) {

            super(itemView);
            this.message = (TextView) itemView.findViewById(R.id.messagetxt);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.messageImg = (NetworkImageView) itemView.findViewById(R.id.message_img);
        }
    }

}
