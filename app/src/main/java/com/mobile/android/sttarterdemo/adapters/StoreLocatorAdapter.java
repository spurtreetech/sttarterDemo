package com.mobile.android.sttarterdemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.fragments.content_system.models.AppleStores;

import java.lang.ref.WeakReference;
import java.util.List;

public class StoreLocatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<AppleStores> mData;

    private LayoutInflater mLayoutInflater;

    private boolean mIsSpaceVisible = true;
    Context context;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    private WeakReference<ItemClickListener> mCallbackRef;

    public StoreLocatorAdapter(Context ctx, List<AppleStores> data, ItemClickListener listener) {
        this.context = ctx;
        mLayoutInflater = LayoutInflater.from(ctx);
        mData = data;
        mCallbackRef = new WeakReference<>(listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View v = mLayoutInflater.inflate(R.layout.view_map_list, parent, false);
            return new MyItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = mLayoutInflater.inflate(R.layout.transparent_header_view, parent, false);
            return new HeaderItem(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyItem) {
            final AppleStores dataItem = mData.get(position-1);
            ((MyItem) holder).name.setText(dataItem.getName()!=null?dataItem.getName():"");
            ((MyItem) holder).address.setText(dataItem.getAddress()!=null?dataItem.getAddress():"");
            String distanceString = (dataItem.getDistance()!=0)?(String.format("%.1f",dataItem.getDistance()/1000))+" kms":"";
            ((MyItem) holder).distance.setText(distanceString);
            ((MyItem) holder).mPosition = position-1;

            ((MyItem)holder).call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+dataItem.getPhone()));
                    context.startActivity(callIntent);
                }
            });


        } else if (holder instanceof HeaderItem) {
            ((HeaderItem) holder).mSpaceView.setVisibility(mIsSpaceVisible ? View.VISIBLE : View.GONE);
            ((HeaderItem) holder).mPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    class MyItem extends HeaderItem {
        TextView name;
        TextView address;
        TextView distance;
        ImageView call;

        public MyItem(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            address = (TextView) itemView.findViewById(R.id.addresss);
            distance = (TextView) itemView.findViewById(R.id.distance);
            call = (ImageView) itemView.findViewById(R.id.call);
        }
    }

    class HeaderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mSpaceView;
        int mPosition;

        public HeaderItem(View itemView) {
            super(itemView);
            mSpaceView = itemView.findViewById(R.id.space);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ItemClickListener callback = mCallbackRef != null ? mCallbackRef.get() : null;
            if (callback != null) {
                callback.onItemClicked(mPosition);
            }

        }
    }

    public void hideSpace() {
        mIsSpaceVisible = false;
        notifyItemChanged(0);
    }

    public void showSpace() {
        mIsSpaceVisible = true;
        notifyItemChanged(0);
    }
}