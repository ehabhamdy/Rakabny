package com.ehab.rakabny.ui;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {
    public static final int RC_PHOTO_PICKER = 2;


    @BindView(R.id.profile_username_textview)
    TextView usernameTextview;

    @BindView(R.id.profile_email_textview)
    TextView emailTextview;

    @BindView(R.id.tickets_textview)
    TextView ticketsTextView;

    @BindView(R.id.profile_college_textview)
    TextView mCollegeTextView;

    @BindView(R.id.profile_gender_textview)
    TextView mGenderTextView;

    @BindView(R.id.profile_area_textview)
    TextView mAreaTextView;

    @BindView(R.id.profile_phone_textview)
    TextView mPhoneTextView;

    @BindView(R.id.logout_button)
    Button logoutButton;

    @BindView(R.id.profile_photo_imageview)
    ImageView mProfileImageView;




    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mUserPhotosStorageReference;
    private FirebaseUser currentUser;
    private String userId;

    String downloadUri;
    String userName;
    String email;
    String tickets;
    String college;
    String gender;
    String phoneNumber;
    String area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mUserPhotosStorageReference = mFirebaseStorage.getReference().child("ProfilePhotos");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference().child("passengers");

        Intent intent = getIntent();
        userName = intent.getStringExtra(NavigationDrawerUtil.USERNAME_EXTRA);
        email = intent.getStringExtra(NavigationDrawerUtil.EMAIL_EXTRA);
        tickets =  intent.getStringExtra(NavigationDrawerUtil.TICKETS_EXTRA);
        college = intent.getStringExtra(NavigationDrawerUtil.COLLEGE_EXTRA);
        phoneNumber = intent.getStringExtra(NavigationDrawerUtil.PHONE_EXTRA);
        gender = intent.getStringExtra(NavigationDrawerUtil.GENDER_EXTRA);
        area = intent.getStringExtra(NavigationDrawerUtil.AREA_EXTRA);

        usernameTextview.setText(userName);
        emailTextview.setText(email);
        ticketsTextView.setText(tickets);
        mAreaTextView.setText(area);
        mCollegeTextView.setText(college);
        mPhoneTextView.setText(phoneNumber);
        mGenderTextView.setText(gender);

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

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        mUsersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Passenger user = dataSnapshot.getValue(Passenger.class);
                if(user.photoUrl == null)
                    mProfileImageView.setImageResource(R.drawable.default_thumbnail);
                else
                    Picasso.with(mProfileImageView.getContext()).load(user.photoUrl).into(mProfileImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void backButtonPressed(View view){
        this.finish();
    }

    @OnClick(R.id.profile_photo_imageview)
    public void changeProfileImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            // get reference to store the file
            StorageReference photoRef = mUserPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            // upload file to Firebase Storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    downloadUri = downloadUrl.toString();
//                    Passenger userUpdatedInfo = new Passenger(userName,email,getString(R.string.default_line_text)
//                            , Integer.parseInt(tickets),taskSnapshot.getDownloadUrl(),phoneNumber,gender,area,college);
                    //Passenger user = new Passenger(userName,email,getString(R.string.default_line_text),Integer.parseInt(tickets),downloadUrl,phoneNumber,gender,area,college);
                    //mUsersReference.child(userId).setValue(user);
                    mUsersReference.child(userId).child("photoUrl").setValue(downloadUri);
                    Picasso.with(mProfileImageView.getContext()).load(downloadUri).into(mProfileImageView);
                }
            });
        }
    }
}
