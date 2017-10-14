package com.ehab.rakabny.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;


    @BindView(R.id.profile_username_textview)
    TextView usernameTextview;

    @BindView(R.id.profile_email_textview)
    TextView emailTextview;

    @BindView(R.id.tickets_textview)
    TextView ticketsTextView;

    @BindView(R.id.logout_button)
    Button logoutButton;

    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        String username = intent.getStringExtra(NavigationDrawerUtil.USERNAME_EXTRA);
        String email = intent.getStringExtra(NavigationDrawerUtil.EMAIL_EXTRA);
        String tickets =  intent.getStringExtra(NavigationDrawerUtil.TICKETS_EXTRA);

        usernameTextview.setText(username);
        emailTextview.setText(email);
        ticketsTextView.setText(tickets);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void backButtonPressed(View view){
        this.finish();
    }

}
