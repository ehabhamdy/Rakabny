package com.ehab.rakabny.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.ehab.rakabny.BuildConfig;
import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.ehab.rakabny.model.Ticket;
import com.ehab.rakabny.utils.JsonUtil;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
//import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int RC_SIGN_IN = 123;
    public static final String TAG = MainActivity.class.getName();
    private static final int LOCATION_REQUEST = 50;
    public static final String PUBLISH_KEY = BuildConfig.PUB_KEY;
    public static final String SUBSCRIBE_KEY = BuildConfig.SUB_KEY;


    SharedPreferences sharedPref;
    Toolbar mToolbar;
    private GoogleMap mMap;
    private PubNub mPubNub;
    private HashMap<Double, Marker> busMarkers;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    LatLng userCurrentLocation;


    @BindView(R.id.currentLineTextView)
    TextView lineTextView;

    @BindView(R.id.tickets_textview)
    TextView ticketsTextView;

    String userId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPassengersReference;

    String username;
    String email;
    String lineChannelSubscription;
    int tickets;

    NavigationDrawerUtil drawerUtil = new NavigationDrawerUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busMarkers = new HashMap<>();
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.getPhoneNumber() != null || user.isEmailVerified()) {

                activityUISetup();

                userId = user.getUid();
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                mPassengersReference = mFirebaseDatabase.getReference().child("passengers");

                // addValueEventListener will always listen for changes so if the user update his profile or
                // change subscription line every thing will be updated properly in thins activity

                ValueEventListener userInformationListener = createUserInformationListner();

                mPassengersReference.child(userId).addValueEventListener(userInformationListener);

                new DrawerBuilder().withActivity(this).withToolbar(mToolbar).build();

            } else {
                // Driver signed out or No Network Connection
                openLoginActivity();
               /* startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                                .build(),
                        RC_SIGN_IN);*/
            }
        } else {
            openLoginActivity();
           /* startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);*/
        }
    }

    private void activityUISetup() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }


    private ValueEventListener createUserInformationListner() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Passenger user = dataSnapshot.getValue(Passenger.class);
                        /*email = user.email;*/
                username = user.username;
                tickets = user.numberOfTickets;
                lineChannelSubscription = user.line;

                ticketsTextView.setText("          " + tickets + " " + getString(R.string.tickets_text1_text));

                lineTextView.setText(getResources().getString(R.string.current_line_textview_text) + " " + user.line);
                busMarkers.clear();
                mMap.clear();

                initPubNub();
                drawerUtil.SetupNavigationDrawer(mToolbar, MainActivity.this, user);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_email), user.email);
                editor.putInt(getString(R.string.saved_ticket_credits), user.numberOfTickets);
                editor.putString(getString(R.string.saved_username), user.username);
                editor.commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
    }


    private void openLoginActivity() {
        Intent intent = new Intent(this, StartActivity.class);
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
        mMap.setOnMarkerClickListener(this);
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

        } else {
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
                                userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraPosition cp = CameraPosition.builder().target(userCurrentLocation).zoom(15).build();
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
                    Toast.makeText(this, R.string.location_permission_required_message, Toast.LENGTH_SHORT).show();
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
                    final Map<String, Double> data = map.get("nameValuePairs");
                    if (data.get("closeSignal") != null) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (busMarkers.get(data.get("bnum")) != null) {
                                    busMarkers.get(data.get("bnum")).remove();
                                    busMarkers.remove(data.get("bnum"));
                                }
                            }
                        });
                        return;
                    }
                    final Double lat = data.get("lat");
                    final Double lng = data.get("lng");
                    final Double busNumber = data.get("bnum");
                    Double numberOfFreePlaces = data.get("free");

                    if (busMarkers.get(busNumber) != null)
                        updateLocation(new LatLng(lat, lng), busNumber, numberOfFreePlaces);
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_car)));
                                m.setTitle(String.valueOf(busNumber));
                                busMarkers.put(busNumber, m);
                            }
                        });
                    }
                } catch (Exception e) {
                    //throw Throwables.propagate(e);
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                // no presence handler for simplicity
            }
        });

        // TODO : Implement functionality that allow users to select the line they want to subscripe to
        this.mPubNub.subscribe().channels(Arrays.asList(lineChannelSubscription)).execute();
    }

    private void updateLocation(final LatLng location, final double num, final double numberOfFreePlaces) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (busMarkers.get(num) != null) {
                    busMarkers.get(num).setPosition(location);
                    if (numberOfFreePlaces > 0)
                        busMarkers.get(num).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_car));
                    else
                        busMarkers.get(num).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_car));
                    busMarkers.get(num).setTitle(String.valueOf(num));
                }
            }
        });
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
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    public void openBusReservationcClick(View view) {
        Intent openReservationIntent = new Intent(getApplicationContext(), LocationChooserActivity.class);
        startActivity(openReservationIntent);
        overridePendingTransition(R.anim.slide_up, R.anim.no_change);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerUtil.getDrawer() != null) {
            drawerUtil.getDrawer().setSelection(1);
            drawerUtil.getDrawer().closeDrawer();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.primary_dark)
                .setIcon(R.drawable.ic_info_black_48dp)
                .setTitle(R.string.reservation_dialog_title)
                .setMessage(R.string.reservation_dialog_body_text)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tickets > 0) {
                            //Charge one ticket from the user
                            tickets--;

                            mPassengersReference.child(userId).child("numberOfTickets").setValue(tickets);

                            //store reservation in the database
                            String key = mFirebaseDatabase.getReference().child("reservations").push().getKey();
                            Ticket t = new Ticket(username, key, FirebaseAuth.getInstance().getCurrentUser().getUid(), userCurrentLocation);
                            String bus = marker.getTitle();
                            mFirebaseDatabase.getReference().child("reservations").child(bus.substring(0, bus.length() - 2)).child(key).setValue(t);


                            new LovelyStandardDialog(MainActivity.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setButtonsColorRes(R.color.primary_dark)
                                    .setIcon(R.drawable.ic_info_black_48dp)
                                    .setTitle(R.string.reservation_completed_dialog_title)
                                    .setMessage(R.string.reservation_completed_dialog_content_text).show();


                        } else {

                            new LovelyStandardDialog(MainActivity.this)
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setButtonsColorRes(R.color.primary_dark)
                                    .setIcon(R.drawable.ic_info_black_48dp)
                                    .setTitle(R.string.reservation_error_dialog_title)
                                    .setMessage(R.string.reservation_error_dialog_body_text).show();

                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();

        return false;
    }
}
