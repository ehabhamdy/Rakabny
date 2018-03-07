package com.ehab.rakabny.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationChooserActivity extends AppCompatActivity implements LocationsAdapter.ItemClickListener {

    public static final String EXTRA_FROM = "from";
    public static final String EXTRA_TO = "to";
    public static final String EXTRA_FROM_COORD = "from_coord";
    public static final String EXTRA_TO_COORD = "to_coord";

    @BindView(R.id.toolbar_title)
    TextView activityTitleTextView;
    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.locations_recyclerview)
    RecyclerView locationRecyclerView;
    @BindView(R.id.pickup_textview)
    TextView pickupTextView;
    @BindView(R.id.dropoff_textview)
    TextView dropoffTextview;
    @BindView(R.id.drop_layout)
    LinearLayout dropLayout;
    @BindView(R.id.next_button)
    Button nextButtons;

    LocationsAdapter adapter;

    int clickPointer = 0;
    List<String> pickupLocationLabels;
    List<String> dropOffLocationlabels;
    List<String> pickupCoord;
    List<String> dropOffCoord;
    List<String> all_locations;
    List<String> all_locations_coord;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAvailabilityReference;
    private Boolean isReservationAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAvailabilityReference = mFirebaseDatabase.getReference().child("reservation-availability");
        mAvailabilityReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isReservationAvailable = (Boolean) dataSnapshot.getValue();
                setupActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setupActivity() {
        if (isReservationAvailable) {
            setContentView(R.layout.activity_location_chooser);

            ButterKnife.bind(this);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activityTitleTextView.setText("Choose your route");


            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            locationRecyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                    locationRecyclerView.getContext(),
                    layoutManager.getOrientation()
            );
            locationRecyclerView.addItemDecoration(mDividerItemDecoration);

            pickupLocationLabels = Arrays.asList(getResources().getStringArray(R.array.from_locations_labels));
            dropOffLocationlabels = Arrays.asList(getResources().getStringArray(R.array.to_locations_labels));

            pickupCoord = Arrays.asList(getResources().getStringArray(R.array.from_locations_coordinates));
            dropOffCoord = Arrays.asList(getResources().getStringArray(R.array.to_locations_coordinates));

            all_locations = new ArrayList<>();
            all_locations.addAll(pickupLocationLabels);
            all_locations.addAll(dropOffLocationlabels);

            all_locations_coord = new ArrayList<>();
            all_locations_coord.addAll(pickupCoord);
            all_locations_coord.addAll(dropOffCoord);

            adapter = new LocationsAdapter(all_locations, this);
            locationRecyclerView.setAdapter(adapter);
        } else {
            setContentView(R.layout.reservation_closed);
        }
    }

    @Override
    public void onClick(String location) {
        if (clickPointer == 0) {
            pickupTextView.setText(location);
            dropLayout.setVisibility(View.VISIBLE);

            if (pickupLocationLabels.contains(location))
                adapter.setData(dropOffLocationlabels);
            else
                adapter.setData(pickupLocationLabels);

            clickPointer++;
        } else if (clickPointer == 1) {
            nextButtons.setEnabled(true);
            dropoffTextview.setText(location);
        }
    }

    @Override
    public void onBackPressed() {
        if (clickPointer == 0)
            super.onBackPressed();
        else if (clickPointer == 1) {
            clickPointer--;
            pickupTextView.setText(getString(R.string.pickup_location_label));
            dropoffTextview.setText(getString(R.string.dropoff_location_label));
            dropLayout.setVisibility(View.GONE);
            adapter.setData(all_locations);
            nextButtons.setEnabled(false);
        }
    }

    @OnClick(R.id.next_button)
    public void nextClicked() {
        Intent intent = new Intent(this, BusReservationDetailsChooserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FROM, pickupTextView.getText().toString());
        bundle.putString(EXTRA_TO, dropoffTextview.getText().toString());
        bundle.putString(EXTRA_FROM_COORD, all_locations_coord.get(all_locations.indexOf(pickupTextView.getText())));
        bundle.putString(EXTRA_TO_COORD, all_locations_coord.get(all_locations.indexOf(dropoffTextview.getText())));
        intent.putExtra("Bundle", bundle);
        //intent.putExtra(EXTRA_FROM, pickupTextView.getText());
        //intent.putExtra(EXTRA_TO, dropoffTextview.getText());
        //intent.putExtra(EXTRA_FROM_COORD, all_locations_coord.get(all_locations.indexOf(pickupTextView.getText())));
        //intent.putExtra(EXTRA_TO_COORD, all_locations_coord.get(all_locations.indexOf(dropoffTextview.getText())));
        startActivity(intent);
    }
}