package com.mobile.android.sttarterdemo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mobile.android.sttarterdemo.R;
import com.spurtreetech.sttarter.lib.helper.STTarter;
import com.spurtreetech.sttarter.lib.helper.adapters.CursorRecyclerAdapter;
import com.spurtreetech.sttarter.lib.helper.utils.DateTimeHelper;
import com.spurtreetech.sttarter.lib.provider.messages.MessagesColumns;

public class BuzzFeedCursorAdapter extends CursorRecyclerAdapter<BuzzFeedViewHolder> {

    Context context;

    public BuzzFeedCursorAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(BuzzFeedViewHolder holder, Cursor cursor) {

        //final BuzzFeedViewHolder holder1 = holder;

        holder.message.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(MessagesColumns.MESSAGE_TEXT))));

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

        holder.time.setText(DateTimeHelper.getTimeOrDateString(Long.parseLong(cursor.getString(cursor.getColumnIndex(MessagesColumns.UNIX_TIMESTAMP)))));
    }

    @Override
    public BuzzFeedViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buzz_feed, parent, false);

        return new BuzzFeedViewHolder(v);
    }
}

class BuzzFeedViewHolder extends RecyclerView.ViewHolder {
    public TextView message, time;
    NetworkImageView messageImg;


    public BuzzFeedViewHolder(View itemView) {
        super(itemView);
        this.message = (TextView) itemView.findViewById(R.id.messagetxt);
        this.time = (TextView) itemView.findViewById(R.id.time);
        this.messageImg = (NetworkImageView) itemView.findViewById(R.id.message_img);
    }
}