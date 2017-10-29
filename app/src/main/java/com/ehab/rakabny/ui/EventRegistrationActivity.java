package com.ehab.rakabny.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Event;
import com.ehab.rakabny.model.EventRegistrationInformation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventRegistrationActivity extends BaseActivity {

    private Toolbar mToolbar;

    private String eventTitle;

    @BindView(R.id.poster_imageview)
    ImageView posterImageView;

    @BindView(R.id.first_name_edittext)
    EditText firstNameEditText;

    @BindView(R.id.last_name_edittext)
    EditText lastNameEditText;

    @BindView(R.id.phone_number_edittext)
    EditText phoneNumberEditText;

    @BindView(R.id.email_edittext)
    EditText emailEditText;

    @BindView(R.id.facebook_edittext)
    EditText facebookEditText;

    @BindView(R.id.no_seats_edittext)
    EditText numberOfSeatsEditText;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        ButterKnife.bind(this);

        setUpToolbar(getString(R.string.registration_activity_title));

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String user_email = sharedPref.getString(getString(R.string.saved_email), "");
        String user_username = sharedPref.getString(getString(R.string.saved_username), "");
        int numberOfTickets = sharedPref.getInt(getString(R.string.saved_ticket_credits), 0);

        firstNameEditText.setText(user_username);
        emailEditText.setText(user_email);

        Bundle bundle = getIntent().getExtras();
        Event event = bundle.getParcelable(EventsActivity.EXTRA_EVENT_DETAILS);
        Picasso.with(this).load(event.bannerUrl).into(posterImageView);

        eventTitle = event.name.replaceAll(" ", "_").toLowerCase();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();

    }

    @OnClick(R.id.register_button)
    public void register(View view) {
        if (!validateForm()) {
            return;
        }else {
            /*Intent intent = new Intent(this, InformationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            new LovelyStandardDialog(this)
                    .setTopColorRes(R.color.colorPrimary)
                    .setButtonsColorRes(R.color.primary_dark)
                    .setIcon(R.drawable.ic_info_black_48dp)
                    .setTitle(R.string.registration_confirmation_dialog_title)
                    .setMessage(R.string.registration_confirmation_dialog_message)
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveEventData();
                            Toast.makeText(getApplicationContext(), R.string.registration_confirmation_dialog_title, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.edit, null)
                    .show();
        }
    }

    private void saveEventData() {
        String key = mDatabaseReference.child("orders").push().getKey();
        String username = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        int seats = Integer.parseInt(numberOfSeatsEditText.getText().toString());
        int phone = Integer.parseInt(phoneNumberEditText.getText().toString());
        String facebook = facebookEditText.getText().toString();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


        final String userId = getUid();

        EventRegistrationInformation registrationInfo = new EventRegistrationInformation(eventTitle, username, lastName, email, phone, facebook, seats, currentDateTimeString);

        Map<String, Object> postValues = registrationInfo.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/event_registrations/" + eventTitle + "/" + key, postValues);
        childUpdates.put("/user-events/" + userId + "/" + key, postValues);

        mDatabaseReference.updateChildren(childUpdates);

        Toast.makeText(this, "Thank you for registration", Toast.LENGTH_SHORT).show();
        finish();

    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(firstNameEditText.getText().toString())) {
            firstNameEditText.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            firstNameEditText.setError(null);
        }

        if (TextUtils.isEmpty(lastNameEditText.getText().toString())) {
            lastNameEditText.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            lastNameEditText.setError(null);
        }

        if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            emailEditText.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(phoneNumberEditText.getText().toString())) {
            phoneNumberEditText.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            phoneNumberEditText.setError(null);
        }

        if (TextUtils.isEmpty(numberOfSeatsEditText.getText().toString())) {
            numberOfSeatsEditText.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            numberOfSeatsEditText.setError(null);
        }

        if (TextUtils.isEmpty(facebookEditText.getText().toString())) {
            facebookEditText.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            facebookEditText.setError(null);
        }


        return result;
    }
}
