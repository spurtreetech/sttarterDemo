package com.mobile.android.sttarterdemo.fragments.referral.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.models.User;

import java.util.ArrayList;

/**
 * Created by Shahbaz on 28/11/16.
 */
public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<User> data;
    Activity activity;

    private final int NORMAL = 1;

    public UserListAdapter(Activity activity, ArrayList<User> data) {

        this.data = data;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {

        if (data.get(position) instanceof User) {
            return NORMAL;
        }
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {

            case NORMAL:
                View vItem = LayoutInflater.from(activity).inflate(R.layout.item_user_list, viewGroup, false);
                viewHolder = new ViewHolderItem(vItem);
                vItem.setTag(viewHolder);
                break;

            default:
                View vH = LayoutInflater.from(activity).inflate(R.layout.item_user_list, viewGroup, false);
                viewHolder = new ViewHolderItem(vH);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        // to give strikethrough on a the price textview if discounted price is available
        // tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        switch (viewHolder.getItemViewType()) {
            case NORMAL:
                ViewHolderItem viewHolderItem = (ViewHolderItem) viewHolder;
                configureItems(viewHolderItem, position);
                break;
            /*
            default:
                ViewHolderHeader viewHolderHeader1 = (ViewHolderHeader) viewHolder;
                configureHeader(viewHolderHeader1, position);
                break;
                */
        }
    }

    private void configureItems(ViewHolderItem viewHolderItem, final int position) {

        final User user = data.get(position);

        if (URLUtil.isValidUrl(user.getAvatar()))
        Glide.with(activity)
                .load(user.getAvatar())
                .fitCenter()
                //.placeholder(R.drawable.map_placeholder)
                .error(R.drawable.account)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolderItem.userIconImageView);

        viewHolderItem.userNameTextView.setText(user.getUsername());

        if (user.getSignup_date()!=null){
            viewHolderItem.dateTimeText.setText(CommonFuntions.convertTimeStamp(user.getSignup_date()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public ImageView userIconImageView;
        public TextView userNameTextView;
        public TextView dateTimeText;

        public ViewHolderItem(View itemView) {
            super(itemView);

            userIconImageView = (ImageView) itemView.findViewById(R.id.userImage);
            userNameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
            dateTimeText = (TextView) itemView.findViewById(R.id.dateTimeText);
        }
    }

    public void removeAt(int position) {
        if (data.size() != 0) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
        }
    }


    public void clearAll() {
        data.clear();
        notifyDataSetChanged();
    }

    public void clearAllSurvey() {

        for (int i = 0; i < data.size(); i++) {
            data.remove(i);
            notifyItemRemoved(i);
        }
    }

    public void addItem(User itemss) {
        data.add(itemss);
        notifyItemInserted(data.size());
    }


}