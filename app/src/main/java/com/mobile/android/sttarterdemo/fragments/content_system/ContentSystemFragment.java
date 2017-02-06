package com.mobile.android.sttarterdemo.fragments.content_system;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.activities.MainActivity;
import com.mobile.android.sttarterdemo.adapters.StoreLocatorAdapter;
import com.mobile.android.sttarterdemo.fragments.content_system.models.AppleStores;
import com.mobile.android.sttarterdemo.fragments.content_system.models.Testcsettings;
import com.mobile.android.sttarterdemo.utils.CommonFuntions;
import com.mobile.android.sttarterdemo.utils.LockableRecyclerView;
import com.mobile.android.sttarterdemo.utils.SlidingUpPanelLayout;
import com.sttarter.content_system.ContentSystemManager;
import com.sttarter.content_system.interfaces.STTContentSystemInterface;
import com.sttarter.content_system.models.ContentSystemResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahbaz on 12-01-2017.
 */

public class ContentSystemFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener, StoreLocatorAdapter.ItemClickListener,OnMapReadyCallback {

    ProgressDialog progress;
    private static final String ARG_LOCATION = "arg.location";

    // private LockableListView mListView;
    private LockableRecyclerView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    // ListView stuff
    //private View mTransparentHeaderView;
    //private View mSpaceView;
    private View mTransparentView;
    private View mWhiteSpaceView;

    private StoreLocatorAdapter mStoreLocatorAdapter;

    private LatLng mLocation;
    private Marker mLocationMarker;

    private SupportMapFragment mMapFragment;

    private GoogleMap mMap;
    private boolean mIsNeedLocationUpdate = true;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location currentLocation;
    List<AppleStores> storesArrayList;

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

        mListView = (LockableRecyclerView) rootView.findViewById(android.R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = rootView.findViewById(R.id.transparentView);
        mWhiteSpaceView = rootView.findViewById(R.id.whiteSpaceView);

        // init header view for ListView
        // mTransparentHeaderView = inflater.inflate(R.layout.transparent_header_view, mListView, false);
        // mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });

        final Gson gson = new Gson();
        initializeProgressIndicator();

        STTContentSystemInterface<ArrayList<AppleStores>> sttContentSystemInterface = new STTContentSystemInterface<ArrayList<AppleStores>>() {

            @Override
            public void Response(ArrayList<AppleStores> appleStoresArrayList) {
                try {
                    storesArrayList = appleStoresArrayList;

                    mStoreLocatorAdapter = new StoreLocatorAdapter(getActivity(), storesArrayList, ContentSystemFragment.this);
                    mListView.setItemAnimator(null);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mListView.setLayoutManager(layoutManager);
                    mListView.setAdapter(mStoreLocatorAdapter);
                    mStoreLocatorAdapter.notifyDataSetChanged();

                    setUpMapIfNeeded();
                    collapseMap();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        };

        ContentSystemManager.getInstance().getContent(getActivity(),AppleStores.class,"store-locator",sttContentSystemInterface,getResponseErrorListener());

        return rootView;
    }

    public Response.ErrorListener getResponseErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                hideProgressIndicator();
                CommonFuntions.onErrorResponse(getActivity(),error);
                checkDistanceTo();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLocation = getArguments().getParcelable(ARG_LOCATION);
        if (mLocation == null) {
            mLocation = getLastKnownLocation(false);
        }

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();

        Gson gson = new Gson();
        /*String jStr = null;
        try {
            jStr = gson.toJson(new JSONObject(storesJsonStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jStr!=null)*/
        storesArrayList = new ArrayList<>();

        // show white bg if there are not too many items
        // mWhiteSpaceView.setVisibility(View.VISIBLE);

        // ListView approach
        /*mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSlidingUpPanelLayout.collapsePane();
            }
        });*/

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        //if (mMap == null) {
        // Try to obtain the map from the SupportMapFragment.
        mMapFragment.getMapAsync(this);
        // Check if we were successful in obtaining the map.
        showMarkers();
        // }
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private LatLng getLastKnownLocation() {
        return getLastKnownLocation(true);
    }

    private LatLng getLastKnownLocation(boolean isMoveMarker) {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (isMoveMarker) {
                moveMarker(latLng);
            }
            return latLng;
        }
        return null;
    }

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        mLocationMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .position(latLng).anchor(0.5f, 0.5f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && moveCamera) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 11.0f)));
                }
            }
        });
    }

    private void collapseMap() {
        if (mStoreLocatorAdapter != null) {
            mStoreLocatorAdapter.showSpace();
        }
        mTransparentView.setVisibility(View.GONE);
        if (mMap != null && mLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 8f), 1000, null);
        }
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (mStoreLocatorAdapter != null) {
            mStoreLocatorAdapter.hideSpace();
        }
        mTransparentView.setVisibility(View.INVISIBLE);
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        mListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            currentLocation = location;
            moveToLocation(location);
            checkDistanceTo();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, ContentSystemFragment.this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClicked(int position) {
        mSlidingUpPanelLayout.collapsePane();
        if (storesArrayList!=null ) {
            AppleStores tempAppleStore = storesArrayList.get(position);
            LatLng tempLatLng = new LatLng(Double.parseDouble(tempAppleStore.getLatitude()), Double.parseDouble(tempAppleStore.getLongitude()));
            moveMarker(tempLatLng);
        }

    }

    void checkDistanceTo(){
        if (currentLocation!=null && storesArrayList!=null) {
            for (int i = 0; i < storesArrayList.size(); i++) {

                Location loc1 = new Location("");
                loc1.setLatitude(currentLocation.getLatitude());
                loc1.setLongitude(currentLocation.getLongitude());

                Location loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(storesArrayList.get(i).getLatitude()));
                loc2.setLongitude(Double.parseDouble(storesArrayList.get(i).getLongitude()));

                float distanceInMeters = loc1.distanceTo(loc2);
                storesArrayList.get(i).setDistance(distanceInMeters);

            }
            if (mStoreLocatorAdapter!=null)
                mStoreLocatorAdapter.notifyDataSetChanged();
        }
    }

    void showMarkers(){
        if (mMap != null) {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            LatLng update = getLastKnownLocation();
            if (update != null) {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 8.0f)));
            }
                /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mIsNeedLocationUpdate = false;
                        moveToLocation(latLng, false);
                    }
                });*/

            if(storesArrayList != null) {
                List<AppleStores> storeList = storesArrayList;
                for(AppleStores eachAppleStore: storesArrayList) {
                    LatLng tempLatLng = new LatLng(Double.parseDouble(eachAppleStore.getLatitude()), Double.parseDouble(eachAppleStore.getLongitude()));
                    Marker mLocationMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                            .title(eachAppleStore.getName()!=null?eachAppleStore.getName():"")
                            .position(tempLatLng).anchor(0.5f, 0.5f));
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showMarkers();
        /*// Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

}
