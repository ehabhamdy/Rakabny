package com.ehab.rakabny.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    }

    public void backButtonPressed(View view){
        this.finish();
    }

}
