package com.mobile.android.sttarterdemo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.mobile.android.sttarterdemo.R;
import com.sttarter.communicator.models.GroupUser;
import com.sttarter.helper.uitools.CircularNetworkImageView;
import com.sttarter.init.STTarterManager;

/**
 * Created by Shahbaz on 9/15/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    private GroupUser[] feedItemList;
    private Context mContext;

    public MyRecyclerAdapter(Context context, GroupUser[] feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected CircularNetworkImageView imageView;
        protected TextView name;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (CircularNetworkImageView) view.findViewById(R.id.thumbnail);
            this.name = (TextView) view.findViewById(R.id.userName);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_group_members, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        GroupUser feedItem = feedItemList[i];

        //Download image using picasso library

        if (URLUtil.isValidUrl(feedItem.getAvatar())) {
            try{
                customViewHolder.imageView.setImageUrl(feedItem.getAvatar(), STTarterManager.getInstance().getImageLoader());
                customViewHolder.imageView.setErrorImageResId(R.mipmap.ic_launcher);
            }catch (Exception e){
                e.printStackTrace();
                customViewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
            }

        }
        else {
            customViewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        //Setting text view title
        customViewHolder.name.setText(Html.fromHtml(feedItem.getName()==null || TextUtils.isEmpty(feedItem.getName())? feedItem.getUsername() : feedItem.getName()));
    }

    @Override
    public int getItemCount() {
        return (feedItemList == null ? 0 : feedItemList.length);
    }
}