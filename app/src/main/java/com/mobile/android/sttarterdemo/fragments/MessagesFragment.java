package com.mobile.android.sttarterdemo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.android.sttarterdemo.R;

/**
 * Created by Aishvarya on 04-11-2016.
 */

public class MessagesFragment extends Fragment {

    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {

        activity = getActivity();


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){

        }
    }
}