package com.ehab.rakabny.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.BusReservationInformation;
import com.ehab.rakabny.model.OverviewPolyline;
import com.ehab.rakabny.model.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusOrderSummaryActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase mFirebaseDatabase;

    @BindView(R.id.trip_duration_textview)
    TextView durationTextView;

    @BindView(R.id.pickup_textview)
    TextView pickupTextView;

    @BindView(R.id.dropoff_textview)
    TextView dropoffTextView;

    @BindView(R.id.startTime_textview)
    TextView startTimeTextView;

    @BindView(R.id.seats_textview)
    TextView seatsTextView;

    @BindView(R.id.price_textview)
    TextView pricesTextView;

    @BindView(R.id.date_textview)
    TextView dateTextView;

    private int ticketPrice = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_order_summary);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(BusOrderSummaryActivity.this);

        String api = getString(R.string.google_api_key);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Bundle");

        String pickupCoord = bundle.getString(LocationChooserActivity.EXTRA_FROM_COORD);
        String dropoffCoord = bundle.getString(LocationChooserActivity.EXTRA_TO_COORD);

        pickupTextView.setText(bundle.getString(LocationChooserActivity.EXTRA_FROM));
        dropoffTextView.setText(bundle.getString(LocationChooserActivity.EXTRA_TO));
        startTimeTextView.setText(bundle.getString("Time"));
        dateTextView.setText(bundle.getString("Date"));
        seatsTextView.setText(bundle.getString("Seats"));
        int price = Integer.parseInt(bundle.getString("Seats")) * ticketPrice;
        pricesTextView.setText(String.valueOf(price) + " LE");

        if (isOnline()) {
            new FetchTimeAsyncTask() {
                @Override
                protected void onPostExecute(Route route) {
                    super.onPostExecute(route);
                    //adapter.setData(recipes);
                    durationTextView.setText(route.getLegs().get(0).getDuration().getText());
                    drawDirectionToStop(route.getOverviewPolyline(), BusOrderSummaryActivity.this, mMap);
                }
            }.execute(pickupCoord, dropoffCoord, "1541202457", api);
        } else {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void drawDirectionToStop(OverviewPolyline overviewPolyline, Context context, GoogleMap mGoogleMap) {
        if (overviewPolyline != null) {
            List<LatLng> polyz = decodeOverviewPolyLinePonts(overviewPolyline.getPoints());
            if (polyz != null) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(polyz);
                lineOptions.width(9);
                lineOptions.color(ContextCompat.getColor(context, R.color.colorPrimary));
                mGoogleMap.addPolyline(lineOptions);
                LatLngBounds bounds;
                List<String> dropOffLocations = Arrays.asList(getResources().getStringArray(R.array.to_locations_labels));
                if (dropOffLocations.contains(pickupTextView.getText())) {
                    bounds = new LatLngBounds(
                            polyz.get(0), polyz.get(polyz.size() - 1));
                } else {
                    bounds = new LatLngBounds(
                            polyz.get(polyz.size() - 1), polyz.get(0));
                }
                //CameraPosition cp = CameraPosition.builder().target(polyz.get(0)).zoom(15).build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 90    );
                //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 500, null);
                mGoogleMap.animateCamera(cu);
                Marker pickupMarker = mGoogleMap.addMarker(new MarkerOptions().position(polyz.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_mark)));
                Marker dropoffMarker = mGoogleMap.addMarker(new MarkerOptions().position(polyz.get(polyz.size() - 1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_mark)));

            }
        }
    }


    //This function is to parse the value of "points"
    public List<LatLng> decodeOverviewPolyLinePonts(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        if (encoded != null && !encoded.isEmpty() && encoded.trim().length() > 0) {
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        }
        return poly;
    }

    @OnClick(R.id.next_button)
    public void submit(View view) {
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.primary_dark)
                .setIcon(R.drawable.ic_info_black_48dp)
                .setTitle(R.string.reservation_dialog_title)
                .setMessage(R.string.reservation_dialog_body_text)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveEventData();
                    }

                })
                .setNegativeButton(android.R.string.no, null)
                .show();


    }

    private void saveEventData() {
        String key = mFirebaseDatabase.getReference().child("bus_reservations").push().getKey();

        String from = pickupTextView.getText().toString();
        String to = dropoffTextView.getText().toString();
        String date = dateTextView.getText().toString();
        String seats = seatsTextView.getText().toString();
        String time = startTimeTextView.getText().toString();


        final String userId = getUid();

        BusReservationInformation registrationInfo = new BusReservationInformation(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), from, to, date, time, seats);

        Map<String, Object> postValues = registrationInfo.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/bus_reservations/" + date.replaceAll("/", "-") + "/" + time + "/" + key, postValues);
        childUpdates.put("/user-bus-reservations/" + userId + "/" + key, postValues);

        mFirebaseDatabase.getReference().updateChildren(childUpdates);

        Toast.makeText(this, "Thank you for registration", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
