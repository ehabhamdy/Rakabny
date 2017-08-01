package com.ehab.rakabny.ui;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instabug.library.core.ui.BaseContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketsActivity extends AppCompatActivity {
    Toolbar mToolbar;

    @BindView(R.id.ticketCountsEditText)
    EditText numberOfTicketsEditText;

    @BindView(R.id.buyConfirmationButton)
    Button confirmationButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        tv.setText("Buy Tickets");

        ButterKnife.bind(this);
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberOfTickets = numberOfTicketsEditText.getText().toString();
                if(!TextUtils.isEmpty(numberOfTickets) && (Integer.parseInt(numberOfTickets) > 0)){
                    Toast.makeText(TicketsActivity.this, "Confirmed", Toast.LENGTH_SHORT).show();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    Toast.makeText(TicketsActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    mDatabase.child("passengers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("numberOfTickets").setValue(Integer.parseInt(numberOfTickets));
                }
            }
        });
    }
}