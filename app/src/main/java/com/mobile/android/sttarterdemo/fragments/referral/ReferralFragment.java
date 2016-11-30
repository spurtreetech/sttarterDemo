package com.mobile.android.sttarterdemo.fragments.referral;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
}