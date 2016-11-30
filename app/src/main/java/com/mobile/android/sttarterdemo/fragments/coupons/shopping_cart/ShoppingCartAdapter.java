package com.mobile.android.sttarterdemo.fragments.coupons.shopping_cart;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.android.sttarterdemo.R;

import java.util.ArrayList;

/**
 * Created by Shahbaz on 28/11/16.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    ArrayList<CartItem> data;
    Activity activity;
    CartClickListener cartClickListener;

    private final int NORMAL = 1;

    public ShoppingCartAdapter(Activity activity, CartClickListener cartClickListener, ArrayList<CartItem> data) {

        this.data = data;
        this.activity = activity;
        this.cartClickListener = cartClickListener;
    }

    @Override
    public int getItemViewType(int position) {

        if (data.get(position) instanceof CartItem) {
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
                View vItem = LayoutInflater.from(activity).inflate(R.layout.shopping_cart_product_row, viewGroup, false);
                viewHolder = new ViewHolderItem(vItem);
                vItem.setTag(viewHolder);
                break;

            default:
                View vH = LayoutInflater.from(activity).inflate(R.layout.shopping_cart_product_row, viewGroup, false);
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

        final CartItem cartItem = data.get(position);

        viewHolderItem.itemId = cartItem.getItem_id();

        Glide.with(activity)
                .load(cartItem.getImage())
                .fitCenter()
                //.placeholder(R.drawable.map_placeholder)
                .error(R.drawable.bag)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolderItem.itemImageNetworkImageView);

        viewHolderItem.itemNameTextView.setText(cartItem.getName());

        double normalPrice = Double.valueOf(cartItem.getPrice() == null ? "0" : cartItem.getPrice());

        viewHolderItem.itemPriceTextView.setText(Html.fromHtml(normalPrice+""));

        viewHolderItem.decrementImage.setOnClickListener(this);
        viewHolderItem.incrementImage.setOnClickListener(this);
        viewHolderItem.quantity.setText("" + cartItem.getOrdered_qty());

        if (cartItem.getOrdered_qty() == 1) {
            viewHolderItem.decrementImageText.setVisibility(View.VISIBLE);
        } else {
            viewHolderItem.decrementImageText.setVisibility(View.GONE);
        }

        //viewHolderItem.removeItemImageView.setOnClickListener(this);

        viewHolderItem.decrementImage.setTag(viewHolderItem);
        viewHolderItem.incrementImage.setTag(viewHolderItem);
        //viewHolderItem.removeItemImageView.setTag(viewHolderItem);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {

        ViewHolderItem tempCartItem = (ViewHolderItem) v.getTag();
        if (v.getId() == R.id.removeItemImageView) {

            //ShoppingCartFragment.deleteShoppingCartItem(tempCartItem.itemId,activity,ShoppingCartAdapter.this);
            cartClickListener.deleteCartItemClicked((ViewHolderItem) v.getTag());
        } else if (v.getId() == R.id.decrementImage) {
            //int tempQuantity = Integer.parseInt(tempCartItem.quantity.getText().toString());
            //tempQuantity++;
            //ShoppingCartFragment.updateShoppingCartItemQuantity(tempCartItem.itemId,Integer.valueOf(tempCartItem.quantity.getText().toString()), false,ShoppingCartAdapter.this);
            cartClickListener.onCartItemQuantityClicked((ViewHolderItem) v.getTag(), false);
        } else if (v.getId() == R.id.incrementImage) {
            cartClickListener.onCartItemQuantityClicked((ViewHolderItem) v.getTag(), true);
        }
        /*
        if(v.getTag() instanceof ViewHolderItem) {
        }
        */
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public ImageView itemImageNetworkImageView;
        public TextView itemNameTextView, itemPriceTextView, quantity, decrementImageText;
        public ImageView /*removeItemImageView,*/ decrementImage, incrementImage;
        public String itemId = "0";

        public ViewHolderItem(View itemView) {
            super(itemView);

            itemImageNetworkImageView = (ImageView) itemView.findViewById(R.id.itemImageNetworkImageView);

            itemNameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = (TextView) itemView.findViewById(R.id.itemPriceTextView);
            decrementImageText = (TextView) itemView.findViewById(R.id.decrementImageText);

            //removeItemImageView = (ImageView) itemView.findViewById(R.id.removeItemImageView);
            //moveToCartButton = (Button) itemView.findViewById(R.id.moveToCartButton);

            quantity = (TextView) itemView.findViewById(R.id.quantity);
            decrementImage = (ImageView) itemView.findViewById(R.id.decrementImage);
            incrementImage = (ImageView) itemView.findViewById(R.id.incrementImage);
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

    public void addItem(CartItem itemss) {
        data.add(itemss);
        notifyItemInserted(data.size());
    }

    public interface CartClickListener {

        void onCartItemQuantityClicked(ViewHolderItem holderItem, boolean increment);

        void updateCartItemClicked(ViewHolderItem holderItem);

        void deleteCartItemClicked(ViewHolderItem holderItem);
    }


}