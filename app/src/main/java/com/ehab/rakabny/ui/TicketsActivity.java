package com.ehab.rakabny.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketsActivity extends AppCompatActivity {
    Toolbar mToolbar;

    @BindView(R.id.ticketCountsEditText)
    EditText numberOfTicketsEditText;

    @BindView(R.id.buyConfirmationButton)
    Button confirmationButton;

    private DatabaseReference mDatabase;
    int tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
        mToolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        tv.setText(R.string.tickets_activity_title);

        Intent intent = getIntent();

        if(intent.getStringExtra(NavigationDrawerUtil.TICKETS_EXTRA) != null)
             tickets =  Integer.parseInt(intent.getStringExtra(NavigationDrawerUtil.TICKETS_EXTRA));
        else{
            FirebaseDatabase mFirebaseDatabase;
            DatabaseReference mPassengersReference;
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mPassengersReference = mFirebaseDatabase.getReference().child("passengers");
            mPassengersReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Passenger user = dataSnapshot.getValue(Passenger.class);
                    tickets = user.numberOfTickets;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        ButterKnife.bind(this);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberOfTickets = numberOfTicketsEditText.getText().toString();
                if(!TextUtils.isEmpty(numberOfTickets) && (Integer.parseInt(numberOfTickets) > 0)){
                    Toast.makeText(TicketsActivity.this, R.string.ticket_purchase_confirmed_message, Toast.LENGTH_SHORT).show();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    //Toast.makeText(TicketsActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    mDatabase.child("passengers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("numberOfTickets").setValue(Integer.parseInt(numberOfTickets) + tickets);

                    SharedPreferences sharedPref = TicketsActivity.this.getSharedPreferences(getString(R.string.ticket_pref_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.tickets_number), Integer.parseInt(numberOfTickets) + tickets);
                    editor.commit();
                }
            }
        });
    }
}