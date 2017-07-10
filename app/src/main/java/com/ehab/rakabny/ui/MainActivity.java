package com.ehab.rakabny.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.util.JsonUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = MainActivity.class.getName();
    Toolbar mToolbar;

    private GoogleMap mMap;
    private PubNub mPubNub;
    public static final String PUBLISH_KEY = "pub-c-df29012c-0242-42b8-8940-95c1d6f06927";
    public static final String SUBSCRIBE_KEY = "sub-c-1a6bbb64-2858-11e7-b284-02ee2ddab7fe";
    private Marker mMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int LOCATION_REQUEST = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            if (user.isEmailVerified()) {
                setContentView(R.layout.activity_main);

                mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                mapFragment.getMapAsync(this);

                initPubNub();
                
            } else {
                // Driver signed out or No Network Connection
                openLoginActivity();
            }
        } else {
            openLoginActivity();
        }
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, ActivityLogin.class);
        //Removing HomeActivity from the back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        LOCATION_PERMS,
                        LOCATION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        LOCATION_PERMS,
                        LOCATION_REQUEST);
            }

        }else{
            // Permissions is already granted
            // setup map configurations
            mMap.setMyLocationEnabled(true);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // ...
                                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraPosition cp = CameraPosition.builder().target(loc).zoom(15).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, null);

                            }
                        }
                    });
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    onMapReady(mMap);

                } else {
                    // permission denied,
                    // Ask again for the permission
                    Toast.makeText(this, "Location permission is mandatory for this application", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private final void initPubNub() {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(PUBLISH_KEY);
        config.setSubscribeKey(SUBSCRIBE_KEY);
        config.setSecure(true);


        this.mPubNub = new PubNub(config);

        this.mPubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                // no status handler for simplicity
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                try {
                    Log.v(TAG, JsonUtil.asJson(message));

                    Map<String, LinkedHashMap> map = JsonUtil.convert(message.getMessage(), LinkedHashMap.class);
                    Map<String, Double> data = map.get("nameValuePairs");
                    Double lat = data.get("lat");
                    Double lng = data.get("lng");

                    updateLocation(new LatLng(lat, lng));
                } catch (Exception e) {
                    //throw Throwables.propagate(e);
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                // no presence handler for simplicity
            }
        });


        // I am still subsciping to a static channel
        // TODO : Implement functionality that allow users to select the line they want to subscripe to
        this.mPubNub.subscribe().channels(Arrays.asList("Mandra : Mahta")).execute();
    }

    private void updateLocation(final LatLng location) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (MainActivity.this.mMarker != null) {
                    MainActivity.this.mMarker.setPosition(location);
                } else {
                    MainActivity.this.mMarker = mMap.addMarker(new MarkerOptions().position(location));
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        });
    }


    class PubnubFetching extends AsyncTask<PNConfiguration, Void, LatLng>{

        @Override
        protected LatLng doInBackground(PNConfiguration... params) {
            PNConfiguration config = new PNConfiguration();

            config.setPublishKey(PUBLISH_KEY);
            config.setSubscribeKey(SUBSCRIBE_KEY);
            config.setSecure(true);

            mPubNub = new PubNub(config);

            mPubNub.addListener(new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {
                    // no status handler for simplicity
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    try {
                        Log.v(TAG, JsonUtil.asJson(message));

                        Map<String, LinkedHashMap> map = JsonUtil.convert(message.getMessage(), LinkedHashMap.class);
                        Map<String, Double> data = map.get("nameValuePairs");
                        Double lat = data.get("lat");
                        Double lng = data.get("lng");

                    } catch (Exception e) {
                        //throw Throwables.propagate(e);
                    }
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                    // no presence handler for simplicity
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
        }
    }
}
