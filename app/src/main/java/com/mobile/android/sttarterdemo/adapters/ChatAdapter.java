package com.mobile.android.sttarterdemo.adapters;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mobile.android.sttarterdemo.R;
import com.spurtreetech.sttarter.lib.helper.STTarter;
import com.spurtreetech.sttarter.lib.helper.adapters.CursorRecyclerAdapter;
import com.spurtreetech.sttarter.lib.helper.utils.DateTimeHelper;
import com.spurtreetech.sttarter.lib.provider.messages.MessagesColumns;

import java.util.Date;

/**
 * Created by Shahbaz on 19-10-2016.
 */
public class ChatAdapter extends CursorRecyclerAdapter<ChatViewHolder> {

    int mLayout;
    Cursor cursor;
    Date date;

    public ChatAdapter(int layout, Cursor c) {
        super(c);
        mLayout = layout;
        this.cursor = c;
        date = new Date();
    }
    public ChatAdapter(Cursor c) {
        this(R.layout.item_chat_message, c);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, Cursor cursor) {

        holder.message.setText(cursor.getString(cursor.getColumnIndex(MessagesColumns.MESSAGE_TEXT)));
        Log.d("ChatAdapter", "message by sender? : " + cursor.getString(cursor.getColumnIndex(MessagesColumns.IS_SENDER)));
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        if(cursor.getString(cursor.getColumnIndex(MessagesColumns.IS_SENDER)).equals("1")) {
            holder.userInfo.setText(cursor.getString(cursor.getColumnIndex(MessagesColumns.MESSAGE_FROM)));

            holder.userInfo.setTextColor(Color.WHITE);
            holder.timeStamp.setTextColor(Color.WHITE);
            holder.message.setTextColor(Color.WHITE);
            holder.dividerLine.setBackgroundColor(Color.WHITE);

            String tmstmp = DateTimeHelper.getTimeOrDateString(Long.parseLong(cursor.getString(cursor.getColumnIndex(MessagesColumns.UNIX_TIMESTAMP))));

            holder.timeStamp.setText(tmstmp);
            holder.container.setBackgroundResource(R.drawable.chatboxorange);
            holder.mainContainer.setHorizontalGravity(Gravity.RIGHT);
        } else {
            holder.userInfo.setText(cursor.getString(cursor.getColumnIndex(MessagesColumns.MESSAGE_FROM)));
            String tmstmp = DateTimeHelper.getTimeOrDateString(Long.parseLong(cursor.getString(cursor.getColumnIndex(MessagesColumns.UNIX_TIMESTAMP))));

            holder.userInfo.setTextColor(Color.rgb(255, 171 ,64));
            holder.timeStamp.setTextColor(Color.rgb(114, 114, 114));
            holder.message.setTextColor(Color.rgb(114, 114, 114));
            holder.dividerLine.setBackgroundColor(Color.rgb(196, 196, 196));

            holder.timeStamp.setText(tmstmp);
            holder.container.setBackgroundResource(R.drawable.chatboxwhite);
            holder.mainContainer.setHorizontalGravity(Gravity.LEFT);
        }
        //holder.time.setText(date.toString());

        if (cursor.getString(cursor.getColumnIndex(MessagesColumns.FILE_TYPE)).contains("none") || cursor.getString(cursor.getColumnIndex(MessagesColumns.FILE_TYPE)) == null) {
            holder.messageImg.setVisibility(View.GONE);
        } else if (URLUtil.isValidUrl(cursor.getString(cursor.getColumnIndex(MessagesColumns.FILE_URL)))) {
            holder.messageImg.setVisibility(View.VISIBLE);
            try {
                holder.messageImg.setImageUrl(cursor.getString(cursor.getColumnIndex(MessagesColumns.FILE_URL)), STTarter.getInstance().getImageLoader());

                holder.messageImg.setErrorImageResId(R.mipmap.ic_launcher);

            } catch (Exception e) {
                e.printStackTrace();

            }

            /*ImageRequest request = new ImageRequest(cursor.getString(cursor.getColumnIndex(MessagesColumns.FILE_URL)),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            //Drawable d = new BitmapDrawable(getResources(), bitmap);
                            //ChatActivity.this.actionBar.setLogo(d);
                            holder1.messageImg.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            holder1.messageImg.setImageResource(R.drawable.nps_logo);
                            //ChatActivity.this.actionBar.setLogo(R.drawable.nps_logo);
                        }
                    });

            RequestQueueHelper.addToRequestQueue(request, "");*/

        }


    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false);

        return new ChatViewHolder(v);
    }
}

class ChatViewHolder extends RecyclerView.ViewHolder
{
    public TextView message, time, userInfo,timeStamp;
    public LinearLayout container, mainContainer;
    public View dividerLine;
    NetworkImageView messageImg;

    public ChatViewHolder (View itemView)
    {
        super(itemView);
        message = (TextView) itemView.findViewById(R.id.message);
        time = (TextView) itemView.findViewById(R.id.time);
        userInfo = (TextView) itemView.findViewById(R.id.userInfo);
        container = (LinearLayout) itemView.findViewById(R.id.chatBoxLayout);
        timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
        dividerLine = (View) itemView.findViewById(R.id.divider_line);
        this.messageImg = (NetworkImageView) itemView.findViewById(R.id.message_img);
        mainContainer = (LinearLayout) itemView;
    }
}