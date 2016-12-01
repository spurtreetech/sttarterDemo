package com.mobile.android.sttarterdemo.fragments.referral;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.sttarter.referral.ReferralManager;
import com.sttarter.referral.interfaces.STTReferralInterface;
import com.sttarter.referral.models.ReferralResponse;

/**
 * Created by Aishvarya on 04-11-2016.
 */

public class ReferralFragment extends Fragment {

    Activity activity;
    TextView textViewReferralCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_referral, container, false);

        init(rootView);
        getReferralCode();

        return rootView;
    }

    private void init(View rootView) {

        activity = getActivity();
        textViewReferralCode = (TextView) rootView.findViewById(R.id.textViewReferralCode);
        textViewReferralCode.setHeight((int)(getActivity().getWindowManager().getDefaultDisplay().getHeight()/3.5));
    }

    void getReferralCode(){

        STTReferralInterface sTTReferralInterface = new STTReferralInterface() {
            @Override
            public void Response(ReferralResponse referralResponse) {
                if (referralResponse.getCode()!=null){
                    textViewReferralCode.setText(referralResponse.getCode());
                }
            }
        };

        ReferralManager.getInstance().getReferral(sTTReferralInterface,getreferralResponseListener());
    }

    public Response.ErrorListener getreferralResponseListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        };
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.referral_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_copy:
                Toast.makeText(activity, getActivity().getResources().getString(R.string.referral_code_copied), Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ReferralCode", textViewReferralCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Use my Referral code for " + getActivity().getResources().getString(R.string.app_name) + " on sign up and get rewards \n"+textViewReferralCode.getText().toString());
                sendIntent.setType("text/plain");
                activity.startActivity(Intent.createChooser(sendIntent, "Share"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}