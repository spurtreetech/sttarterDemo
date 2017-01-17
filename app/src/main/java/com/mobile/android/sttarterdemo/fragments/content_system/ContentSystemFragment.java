package com.mobile.android.sttarterdemo.fragments.content_system;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.fragments.content_system.models.Testcsettings;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.sttarter.content_system.ContentSystemManager;
import com.sttarter.content_system.interfaces.STTContentSystemInterface;
import com.sttarter.content_system.models.ContentSystemResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shahbaz on 12-01-2017.
 */

public class ContentSystemFragment extends Fragment{

    String response = "{\"status\":200,\"schema\":[{\"field_name\":\"question\",\"field_type\":\"text\",\"validation\":[{\"required\":true}],\"appearance\":[],\"display_name\":\"QUESTION\"},{\"field_name\":\"answer\",\"field_type\":\"text\",\"validation\":[{\"required\":true}],\"appearance\":[],\"display_name\":\"ANSWER\"}],\"data\":[{\"question\":\"What is MDC ?\",\"answer\":\"MDC is a liqour selling app\"},{\"question\":\"Which Countries do you allow ?\",\"answer\":\"India is not allowed\"},{\"question\":\"Hi ?\",\"answer\":\"hi , hello\"}]}";
    ProgressDialog progress;

    public static ContentSystemFragment newInstance() {

        Bundle args = new Bundle();

        ContentSystemFragment fragment = new ContentSystemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content_system, null);

        final Gson gson = new Gson();
        initializeProgressIndicator();

        Log.d("response",response);

        /*ContentSystemResponse contentSystemResponse = gson.fromJson(response, ContentSystemResponse.class);

        if (contentSystemResponse.getStatus() == 200) {



            for (int i = 0; i < contentSystemResponse.getData().length(); i++) {

                for (int j = 0; j < contentSystemResponse.getSchema().size(); j++) {
                    String name = contentSystemResponse.getSchema().get(j).getField_name();
                    String type = contentSystemResponse.getSchema().get(j).getField_type();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = contentSystemResponse.getData().getJSONObject(i);
                        if (type.equalsIgnoreCase("text")) {
                            String value = jsonObject.getString(name);
                            Log.d("name:" + name, "type:"+type +" value:" + value);
                        }
                        else if (type.equalsIgnoreCase("integer")){
                            Integer value = jsonObject.getInt(name);
                            Log.d("name:" + name, "type:"+type +" value:" + value);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("-----------", "--------------");
            }

        }*/

        STTContentSystemInterface<ArrayList<Testcsettings>> sttContentSystemInterface = new STTContentSystemInterface<ArrayList<Testcsettings>>() {

            @Override
            public void Response(ArrayList<Testcsettings> testcsettingses) {
                for (int i = 0;i<testcsettingses.size();i++){

                }
            }

        };

        ContentSystemManager.getInstance().contentsystem(getActivity()/*,Testcsettings.class*/,"Testcsettings",sttContentSystemInterface,getResponseErrorListener());

        return rootView;
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
