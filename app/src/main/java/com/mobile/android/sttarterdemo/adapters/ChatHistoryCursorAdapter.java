package com.mobile.android.sttarterdemo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobile.android.sttarterdemo.R;
import com.spurtreetech.sttarter.lib.helper.STTarter;
import com.spurtreetech.sttarter.lib.helper.adapters.CursorRecyclerAdapter;
import com.spurtreetech.sttarter.lib.helper.models.TopicMeta;
import com.spurtreetech.sttarter.lib.helper.utils.DateTimeHelper;
import com.spurtreetech.sttarter.lib.helper.volley.CircularNetworkImageView;
import com.spurtreetech.sttarter.lib.provider.STTProviderHelper;
import com.spurtreetech.sttarter.lib.provider.messages.MessagesCursor;
import com.spurtreetech.sttarter.lib.provider.topics.TopicsColumns;

public class ChatHistoryCursorAdapter extends CursorRecyclerAdapter<ChatHistoryViewHolder> implements View.OnClickListener {

    ChatInitiateListener cil;
    Gson gson = new Gson();
    STTProviderHelper ph;
    Context context;

    public ChatHistoryCursorAdapter(Context context, Cursor cursor, ChatInitiateListener chatInitiateListener) {
        //super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        super(cursor);
        this.cil = chatInitiateListener;
        ph = new STTProviderHelper();
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Log.d(getClass().getSimpleName(), ((TextView) v.findViewById(R.id.name)).getText().toString());
        Log.d(getClass().getSimpleName() + ">", ((TextView) v.findViewById(R.id.channel)).getText().toString());

        TextView channelTextView = ((TextView) v.findViewById(R.id.channel));

        cil.chatClicked(channelTextView.getText().toString(), (TopicMeta) v.getTag(), channelTextView.getTag().toString());
    }

    @Override
    public void onBindViewHolder(ChatHistoryViewHolder holder, Cursor cursor) {
        holder.channel.setText(cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_NAME)));
        holder.channel.setTag(cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_GROUP_MEMBERS)));

        STTProviderHelper sttProviderHelper = new STTProviderHelper();
        int count = 0;
        count = sttProviderHelper.getUnreadMessageCountForTopic(cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_NAME)));

        if (count > 0) {
            holder.textViewUnreadCount.setVisibility(View.VISIBLE);
            holder.textViewUnreadCount.setText(count + "");
        } else {
            holder.textViewUnreadCount.setVisibility(View.GONE);
        }

        Log.d("Topic Meta", cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_META)).toString());

        holder.tm = gson.fromJson(cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_META)), TopicMeta.class);
        if (URLUtil.isValidUrl(holder.tm.getImage()) && holder.tm.getImage() != null) {
            try {
                holder.imageViewGroupIcon.setImageUrl(holder.tm.getImage(), STTarter.getInstance().getImageLoader());
                holder.imageViewGroupIcon.setErrorImageResId(R.mipmap.ic_launcher);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //ImageRequestHelper.setImageToImageView(holder.tm.getImage(), holder.groupIconImageView);
        } else {
            holder.imageViewGroupIcon.setImageResource(R.mipmap.ic_launcher);
        }
        holder.textViewGroupName.setText(holder.tm.getName());
        holder.topicList.setTag(holder.tm);
        //holder.latestMsgTextView.setText();
        MessagesCursor mc = ph.getLastMessageForTopic(cursor.getString(cursor.getColumnIndex(TopicsColumns.TOPIC_NAME)));
        if (mc != null && mc.getCount() > 0) {
            mc.moveToFirst();
            holder.textViewName.setText(mc.getMessageText());
            holder.textViewDescription.setText(mc.getMessageFrom());

            String tmstmp = DateTimeHelper.getTimeOrDateString(mc.getUnixTimestamp().getTime());

            Log.d(">>>>>>>>>>", tmstmp);

            holder.textViewTimeStamp.setText(tmstmp);

        } else {
            holder.textViewName.setText("");
            holder.textViewTimeStamp.setText("");
            holder.textViewDescription.setText("");
        }
    }


    @Override
    public ChatHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_topiclist, parent, false);
        //((TextView)v.findViewById(R.id.name)).setOnClickListener(this);
        v.setOnClickListener(this);
        return new ChatHistoryViewHolder(v);
    }

    public interface ChatInitiateListener {
        void chatClicked(String chatId, TopicMeta topicMeta, String groupMembers);
    }
}

class ChatHistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView channel, textViewGroupName, textViewDescription, textViewName, textViewTimeStamp, textViewUnreadCount;
    public int position;
    public TopicMeta tm;
    public LinearLayout topicList;
    public CircularNetworkImageView imageViewGroupIcon;

    public ChatHistoryViewHolder(View itemView) {
        super(itemView);
        this.channel = (TextView) itemView.findViewById(R.id.channel);
        this.textViewGroupName = (TextView) itemView.findViewById(R.id.textViewGroupName);
        this.textViewTimeStamp = (TextView) itemView.findViewById(R.id.textViewTimeStamp);
        this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
        textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
        topicList = (LinearLayout) itemView.findViewById(R.id.topicListLayout);
        imageViewGroupIcon = (CircularNetworkImageView) itemView.findViewById(R.id.imageViewGroupIcon);
        textViewUnreadCount = (TextView) itemView.findViewById(R.id.textViewUnreadCount);
        this.position = this.getPosition();
    }
}
