package com.mobile.android.sttarterdemo.fragments.coupons.shopping_cart;

/**
 * Created by Shahbaz on 28/11/16.
 */
public class CartItem {

    String item_id, product_id, name, sku, price, product_price, image;

    public int getOrdered_qty() {
        return ordered_qty;
    }

    public void setOrdered_qty(int ordered_qty) {
        this.ordered_qty = ordered_qty;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    int ordered_qty;

}
