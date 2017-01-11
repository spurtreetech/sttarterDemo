package com.mobile.android.sttarterdemo.fragments.coupons;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.fragments.coupons.models.CartItem;
import com.mobile.android.sttarterdemo.fragments.coupons.adapters.ShoppingCartAdapter;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.responses.STTResponse;
import com.sttarter.coupons.CouponManager;
import com.sttarter.coupons.interfaces.STTCouponInterface;
import com.sttarter.coupons.models.CouponResponse;
import com.sttarter.helper.interfaces.STTSuccessListener;
import com.sttarter.referral.ReferralManager;

import java.util.ArrayList;

/**
 * Created by Aishvarya on 04-11-2016.
 */

public class CouponsFragment extends Fragment implements View.OnClickListener{

    Activity activity;
    EditText couponEdit;
    Button couponApply;
    TextView errorMessage,errorDescription;
    LinearLayout messageLayout;
    ArrayList<CartItem> cartItemArrayList;
    RecyclerView recyclerViewCart;
    TextView textViewSubtotalAmount, textViewTotalAmount;
    Button confirmPayment;
    int subtotal = 0;
    int shipping = 90;
    ProgressDialog progress;
    ShoppingCartAdapter shoppingCartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_coupons, container, false);

        init(rootView);
        initializeProgressIndicator();

        couponEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (messageLayout.getVisibility()==View.VISIBLE){
                    messageLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        couponEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    messageLayout.setVisibility(View.VISIBLE);

                }
                return false;
            }
        });

        couponApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(couponEdit.getText().toString())){

                    STTCouponInterface sttCouponInterface = new STTCouponInterface() {
                        @Override
                        public void Response(CouponResponse sttCouponResponse) {

                            if (sttCouponResponse.getResult()==null){
                                messageLayout.setVisibility(View.VISIBLE);



                            }

                        }
                    };

                    CouponManager.getInstance().redeemCoupon((subtotal + shipping)+"",couponEdit.getText().toString(),sttCouponInterface,getResponseErrorListener());
                }
            }
        });

        confirmPayment.setOnClickListener(this);

        return rootView;
    }

    private void init(View rootView) {

        activity = getActivity();
        couponEdit = (EditText) rootView.findViewById(R.id.couponEdit);
        recyclerViewCart = (RecyclerView) rootView.findViewById(R.id.recyclerViewCart);
        textViewSubtotalAmount = (TextView) rootView.findViewById(R.id.textViewSubtotalAmount);
        textViewTotalAmount = (TextView) rootView.findViewById(R.id.textViewTotalAmount);
        messageLayout = (LinearLayout) rootView.findViewById(R.id.messageLayout);
        confirmPayment = (Button) rootView.findViewById(R.id.confirmPayment);
        couponApply = (Button) rootView.findViewById(R.id.applyCoupon);
        errorMessage = (TextView) rootView.findViewById(R.id.msg);
        errorDescription = (TextView) rootView.findViewById(R.id.description);
        messageLayout.setVisibility(View.INVISIBLE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewCart.setLayoutManager(layoutManager);

        cartItemArrayList = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            CartItem cartItem = new CartItem();
            cartItem.setItem_id(i + "");
            cartItem.setProduct_id(i + "");
            cartItem.setName("Product " + i);
            cartItem.setSku("S" + i);
            cartItem.setPrice((i * 11) + "");
            cartItem.setProduct_price((i * 12) + "");
            cartItem.setImage("");
            cartItem.setOrdered_qty(i);
            cartItemArrayList.add(cartItem);
        }

        shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), new ShoppingCartAdapter.CartClickListener() {
            @Override
            public void onCartItemQuantityClicked(ShoppingCartAdapter.ViewHolderItem holderItem, boolean increment) {

                int position = holderItem.getLayoutPosition();

                int currentQuantity =  cartItemArrayList.get(position).getOrdered_qty();

                if (!increment && currentQuantity==1){

                }
                else {
                    // change here
                    cartItemArrayList.get(position).setOrdered_qty(increment ? (currentQuantity+1):(currentQuantity-1));
                    shoppingCartAdapter.notifyItemChanged(position);
                    setCounts();
                }


            }

            @Override
            public void updateCartItemClicked(ShoppingCartAdapter.ViewHolderItem holderItem) {

            }

            @Override
            public void deleteCartItemClicked(ShoppingCartAdapter.ViewHolderItem holderItem) {

            }
        }, cartItemArrayList);
        recyclerViewCart.setAdapter(shoppingCartAdapter);

        setCounts();

    }

    void setCounts(){
        subtotal = 0;
        for (int i = 0; i < cartItemArrayList.size(); i++) {
            int itemTotal = Integer.parseInt(cartItemArrayList.get(i).getPrice())*cartItemArrayList.get(i).getOrdered_qty();
            subtotal = subtotal + itemTotal ;
        }

        textViewSubtotalAmount.setText("Rs. " + subtotal);
        textViewTotalAmount.setText("Rs. " + (subtotal + shipping));

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirmPayment:
                showProgressIndicator("Please wait..");
                STTSuccessListener sttReferralInterface = new STTSuccessListener() {
                    @Override
                    public void Response(STTResponse sttResponse) {

                        if (sttResponse.getMsg()!=null){
                            Toast.makeText(activity, sttResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressIndicator();

                    }
                };

                ReferralManager.getInstance().addTransaction("aDemoIDFromAndroid",(subtotal + shipping)+"",sttReferralInterface,getResponseErrorListener());
                break;
        }
    }



    public Response.ErrorListener getResponseErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                hideProgressIndicator();
                CommonFuntions.onErrorResponse(getActivity(),error);

            }
        };
    }

    private void initializeProgressIndicator() {
        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
    }

    private void showProgressIndicator(String message) {
        try {
            progress.setMessage(message);
            progress.show();
        } catch (Exception e) {

        }
    }

    private void hideProgressIndicator() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

}