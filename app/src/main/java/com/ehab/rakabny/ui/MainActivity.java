package com.ehab.rakabny.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.BuildConfig;
import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.ehab.rakabny.util.JsonUtil;
import com.ehab.rakabny.util.NavigationDrawerUtil;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    public static final String TAG = MainActivity.class.getName();
    private static final int LOCATION_REQUEST = 50;
    public static final String PUBLISH_KEY = BuildConfig.PUB_KEY;
    public static final String SUBSCRIBE_KEY = BuildConfig.SUB_KEY;

    Toolbar mToolbar;
    private GoogleMap mMap;
    private PubNub mPubNub;
    private Marker mMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };


    @BindView(R.id.currentLineTextView)
    TextView lineTextView;

    String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPassengersReference;

    String username;
    String email;
    String lineChannelSubscription;

    NavigationDrawerUtil drawerUtil = new NavigationDrawerUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.isEmailVerified()) {

                setContentView(R.layout.activity_main);
                ButterKnife.bind(MainActivity.this);

                mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                mapFragment.getMapAsync(MainActivity.this);

                userId = user.getUid();
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                mPassengersReference = mFirebaseDatabase.getReference().child("passengers");

                // addValueEventListener will always listen for changes so if the user update his profile or
                // change subscription line every thing will be updated properly in thins activity
                mPassengersReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Passenger user = dataSnapshot.getValue(Passenger.class);
                        username = user.username;
                        email = user.email;
                        lineChannelSubscription = user.line;

                        lineTextView.setText(getResources().getString(R.string.current_line_textview_text) + " " + lineChannelSubscription);

                        initPubNub();
                        drawerUtil.SetupNavigationDrawer(mToolbar, MainActivity.this ,username, email, lineChannelSubscription);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                new DrawerBuilder().withActivity(this).withToolbar(mToolbar).build();

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
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 500, null);

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
        this.mPubNub.subscribe().channels(Arrays.asList(lineChannelSubscription)).execute();
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

                //Camera shouldn't move with every bus location
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmark_menu:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    public void openLineSubscription(View view){
        Intent openSubscriptionIntent = new Intent(getApplicationContext(), LineSubscriptionActivity.class);
        openSubscriptionIntent.putExtra(NavigationDrawerUtil.SUB_LINE_EXTRA, lineChannelSubscription);
        if(lineChannelSubscription != null)
            startActivity(openSubscriptionIntent);
            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(drawerUtil.getDrawer() != null) {
            drawerUtil.getDrawer().setSelection(1);
            drawerUtil.getDrawer().closeDrawer();
        }
    }
}
