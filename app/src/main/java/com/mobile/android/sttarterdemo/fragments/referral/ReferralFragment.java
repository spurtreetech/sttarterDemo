package com.mobile.android.sttarterdemo.fragments.referral;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.fragments.referral.adapters.UserListAdapter;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.common.models.UserList;
import com.sttarter.referral.ReferralManager;
import com.sttarter.referral.interfaces.STTReferralInterface;
import com.sttarter.referral.models.ReferralResponse;

/**
 * Created by Aishvarya on 04-11-2016.
 */

public class ReferralFragment extends Fragment {

    Activity activity;
    EditText textViewReferralCode;
    ImageButton editReferral;
    boolean editMode = false;
    RecyclerView userListRecyclerview;
    UserListAdapter userListAdapter;

    String oldCode = "";

    ProgressDialog progress;

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
        initializeProgressIndicator();
        getReferralCode();
        trackUsage();

        textViewReferralCode.setInputType(InputType.TYPE_NULL);

        editReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editMode){
                    if (!isKeyBoardOpen(getActivity())) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(textViewReferralCode, InputMethodManager.SHOW_IMPLICIT);
                    }
                    textViewReferralCode.setInputType(InputType.TYPE_CLASS_TEXT);
                    textViewReferralCode.requestFocus();
                    textViewReferralCode.setSelection(textViewReferralCode.length());
                    editMode = true;
                    editReferral.setImageResource(R.drawable.tick);
                    editReferral.setBackgroundResource(R.color.colorPrimary);

                }
                else {
                    if (isKeyBoardOpen(getActivity())) {
                        InputMethodManager imm = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(
                                textViewReferralCode.getWindowToken(), 0);
                    }
                    textViewReferralCode.setInputType(InputType.TYPE_NULL);
                    editMode = false;
                    editReferral.setImageResource(R.drawable.edit_referral);
                    editReferral.setBackgroundResource(android.R.color.transparent);

                    if (!oldCode.equalsIgnoreCase(textViewReferralCode.getText().toString())) {
                        STTReferralInterface sttReferralInterface = new STTReferralInterface() {
                            @Override
                            public void Response(ReferralResponse referralResponse) {
                                hideProgressIndicator();
                                if (referralResponse.getStatus()==200) {
                                    oldCode = textViewReferralCode.getText().toString();
                                }
                                Toast.makeText(activity, referralResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        };

                        showProgressIndicator("Loading..");
                        ReferralManager.getInstance().customizeReferralCode(oldCode, textViewReferralCode.getText().toString().trim(), sttReferralInterface, getreferralErrorResponseListener());
                    }

                }

            }
        });

        return rootView;
    }

    private Response.Listener<UserList> gettrackUsageSuccessListener() {

        return new Response.Listener<UserList>() {
            @Override
            public void onResponse(final UserList userResponse) {

                if (userResponse.getMsg()!=null)
                    Toast.makeText(activity, userResponse.getMsg(), Toast.LENGTH_SHORT).show();

                userListAdapter = new UserListAdapter(getActivity(),userResponse.getUsers());
                userListRecyclerview.setAdapter(userListAdapter);

            }
        };
    }

    private void trackUsage() {

        ReferralManager.getInstance().trackUsage(gettrackUsageSuccessListener(),getreferralErrorResponseListener());

    }

    private void init(View rootView) {

        activity = getActivity();
        textViewReferralCode = (EditText) rootView.findViewById(R.id.textViewReferralCode);
        editReferral = (ImageButton) rootView.findViewById(R.id.editReferral);
        userListRecyclerview = (RecyclerView) rootView.findViewById(R.id.userListRecyclerview);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userListRecyclerview.setLayoutManager(layoutManager);
    }

    private void initializeProgressIndicator(){
        progress = new ProgressDialog(getActivity());
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
    }

    public static boolean isKeyBoardOpen(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            Log.d("Keyboard", "Software Keyboard was shown");
            return true;
        } else {
            Log.d("Keyboard", "Software Keyboard was not shown");
            return false;
        }
    }

    void getReferralCode(){

        STTReferralInterface sTTReferralInterface = new STTReferralInterface() {
            @Override
            public void Response(ReferralResponse referralResponse) {
                if (referralResponse.getCode()!=null){
                    oldCode = referralResponse.getCode();
                    textViewReferralCode.setText(referralResponse.getCode());
                }
            }
        };

        ReferralManager.getInstance().getReferral(sTTReferralInterface,getreferralErrorResponseListener());
    }

    public Response.ErrorListener getreferralErrorResponseListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                hideProgressIndicator();
                CommonFuntions.onErrorResponse(getActivity(),error);

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

    private void showProgressIndicator(String message) {
        progress.setMessage(message);
        progress.show();
    }

    private void hideProgressIndicator() {
        if (progress.isShowing())
            progress.hide();
    }

}