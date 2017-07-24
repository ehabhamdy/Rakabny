package com.ehab.rakabny.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.util.NavigationDrawerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_username_textview)
    TextView usernameTextview;

    @BindView(R.id.profile_email_textview)
    TextView emailTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String username = intent.getStringExtra(NavigationDrawerUtil.USERNAME_EXTRA);
        String email = intent.getStringExtra(NavigationDrawerUtil.EMAIL_EXTRA);

        usernameTextview.append(" "+ username);
        emailTextview.append("    "+ email);

    }
}
