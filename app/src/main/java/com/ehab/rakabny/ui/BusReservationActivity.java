package com.ehab.rakabny.ui;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.BusReservationInformation;
import com.ehab.rakabny.model.EventRegistrationInformation;
import com.ehab.rakabny.model.Ticket;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusReservationActivity extends BaseActivity {

    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView activityTitleTextView;

    @BindView(R.id.next_button)
    Button nextButton;

    @BindView(R.id.back_button)
    Button backButton;


    private int screens = 2;
    private int currentScreen = 1;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPassengersReference;
    private DatabaseReference mBusReservationsReference;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_reservation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityTitleTextView.setText(R.string.bus_reservation_activity_title);

        ReservationOrderFragment orderFragment = new ReservationOrderFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_content, orderFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.no_change, R.anim.slide_down);
        if(currentScreen > 1) {
            currentScreen--;
            nextButton.setText("Next");
        }
        if(currentScreen == 1)
            backButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.overridePendingTransition(R.anim.no_change, R.anim.slide_down);
        return super.onKeyUp(keyCode, event);

    }

    @OnClick(R.id.next_button)
    public void submit(View view) {
        if(currentScreen == screens){
            nextButton.setText("Finish");
            Toast.makeText(this, "Final Yahoo", Toast.LENGTH_SHORT).show();
        }else{
            OrderSummaryFragment orderFragment = new OrderSummaryFragment();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.main_content, orderFragment, "tran");
            ft.addToBackStack("tran");
            ft.commit();
            currentScreen++;
            backButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.back_button)
    public void backClick(View view) {
        onBackPressed();
    }

    /*private void saveEventData() {
        String key = mFirebaseDatabase.getReference().child("bus_reservations").push().getKey();

        String from = fromSpinner.getSelectedItem().toString();
        String to = toSpinner.getSelectedItem().toString();
        String date = reservationDateTextView.getText().toString();
        int seats = Integer.parseInt(seatsEditText.getText().toString());
        String time = timeSpinner.getSelectedItem().toString();


        final String userId = getUid();

        BusReservationInformation registrationInfo = new BusReservationInformation(name, from, to, date, time, seats);

        Map<String, Object> postValues = registrationInfo.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/bus_reservations/" +  date.replaceAll("/", "-") + "/" + time + "/" + key, postValues);
        childUpdates.put("/user-bus-reservations/" + userId + "/" + key, postValues);

        mFirebaseDatabase.getReference().updateChildren(childUpdates);

        Toast.makeText(this, "Thank you for registration", Toast.LENGTH_SHORT).show();
        finish();
    }*/
}
