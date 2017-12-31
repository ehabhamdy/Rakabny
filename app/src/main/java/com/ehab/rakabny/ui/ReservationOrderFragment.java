package com.ehab.rakabny.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ehabhamdy on 12/31/17.
 */

public class ReservationOrderFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reservation_order, container, false);
        ButterKnife.bind(this, rootView);

        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        Date tomorrow = gc.getTime();
        final DateFormat dateFormat = DateFormat.getDateInstance();
        reservationDateTextView.setText(dateFormat.format(tomorrow));

        final DatePickerDialog StartTime = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                reservationDateTextView.setText(dateFormat.format(newDate.getTime()));
            }

        }, gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
/*
        Intent intent = getActivity().getIntent();
        name = intent.getStringExtra(NavigationDrawerUtil.USERNAME_EXTRA);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPassengersReference = mFirebaseDatabase.getReference().child("passengers");
        mBusReservationsReference = mFirebaseDatabase.getReference().child("bus-reservations");*/

        reservationDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
        return rootView;
    }
}
