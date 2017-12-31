package com.ehab.rakabny.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
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

    @BindView(R.id.from_spinner)
    Spinner fromSpinner;

    @BindView(R.id.to_spinner)
    Spinner toSpinner;

    @BindView(R.id.reservation_date_textview)
    TextView reservationDateTextView;

    @BindView(R.id.time_spinner)
    Spinner timeSpinner;

    @BindView(R.id.number_seats_edittext)
    EditText seatsEditText;

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

        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        Date tomorrow = gc.getTime();
        final DateFormat dateFormat = DateFormat.getDateInstance();
        reservationDateTextView.setText(dateFormat.format(tomorrow));

        final DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                reservationDateTextView.setText(dateFormat.format(newDate.getTime()));
            }

        }, gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));

        Intent intent = getIntent();
        name = intent.getStringExtra(NavigationDrawerUtil.USERNAME_EXTRA);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPassengersReference = mFirebaseDatabase.getReference().child("passengers");
        mBusReservationsReference = mFirebaseDatabase.getReference().child("bus-reservations");

        reservationDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.overridePendingTransition(R.anim.no_change, R.anim.slide_down);
        return super.onKeyUp(keyCode, event);

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
    }
}
