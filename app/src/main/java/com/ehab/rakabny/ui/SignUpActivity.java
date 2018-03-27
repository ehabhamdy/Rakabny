package com.ehab.rakabny.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends BaseActivity {


    private static final String TAG = "Message";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    EditText mUsernameField;
    EditText mEmailField;
    EditText mPasswordField;

    // new variables added linking using butterknife
    @BindView (R.id.signup_phoneTextfield)
    EditText mPhonenumberField;

    @BindView(R.id.gender_spinner)
    Spinner mGenderSpinner;

    @BindView(R.id.signup_area_spinner)
    Spinner mAreaSpinner;

    @BindView(R.id.college_spinner)
    Spinner mCollegeSpinner;

    private ProgressDialog mProgressDialog;

    String defaultLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        defaultLine = getString(R.string.default_line_text);

        TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        tv.setText(R.string.signup_activity_title);
        //Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
        //tv.setTypeface(custom_font);

        //populating gender spinner with genders
        List<String> genderList =  Arrays.asList(getResources().getStringArray(R.array.genders));
        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(genderAdapter);

        //populating area spinner from tunnels resource file
        List<String> mainLocationsList = Arrays.asList(getResources().getStringArray(R.array.main_places));
        ArrayAdapter<CharSequence> mainPlacesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mainLocationsList);
        mainPlacesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAreaSpinner.setAdapter(mainPlacesAdapter);

        //populating college spinner from the resource files
        List<String> collegesList = Arrays.asList(getResources().getStringArray(R.array.colleges));
        ArrayAdapter<CharSequence> collegesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, collegesList);
        collegesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCollegeSpinner.setAdapter(collegesAdapter);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mEmailField = (EditText) findViewById(R.id.emailEditText);
        mPasswordField = (EditText) findViewById(R.id.passwordEditText);
        mUsernameField = (EditText) findViewById(R.id.usernameEditText);

        final Button signupBtn = (Button) findViewById(R.id.signupButton);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameField.getText().toString();
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                String phoneNumber = mPhonenumberField.getText().toString();
                String gender = mGenderSpinner.getSelectedItem().toString();
                String area = mAreaSpinner.getSelectedItem().toString();
                String college = mCollegeSpinner.getSelectedItem().toString();

                //create new user
                signUp(username, email, password, phoneNumber, gender, area, college);
            }
        });


    }

    public void signUp(final String username, final String email, String password, final String phoneNumber, final String gender, final String area, final String college) {

        //Check if the email or password fields are empty
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser(), username, email, phoneNumber, gender, area, college);
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.signup_failed_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(getString(R.string.email_field_required_text));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(getString(R.string.password_field_required_text));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if(TextUtils.isEmpty(mPhonenumberField.getText().toString())){
            mPhonenumberField.setError(getString(R.string.password_field_required_text));
            result = false;
        }else{
            mPhonenumberField.setError(null);
        }

        if(mGenderSpinner.getSelectedItem() == null){
            result = false;
        }
        if(mAreaSpinner.getSelectedItem() == null){
            result = false;
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser user, String username, String email, String phoneNumber, String gender, String area, String college) {
        //String username = usernameFromEmail(user.getEmail());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        // Write new user
        writeNewUser(user.getUid(), username, email, phoneNumber, gender, area, college);

        Toast.makeText(getApplicationContext(), R.string.signup_feedback_message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email, String phoneNumber, String gender, String area, String college) {
        int numberOfTickets = 0;
        Passenger passenger = new Passenger(name, email, defaultLine, numberOfTickets, "", phoneNumber, gender, area, college);

        mDatabase.child("passengers").child(userId).setValue(passenger);
    }
    // [END basic_write]

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.progressbar_loading_message));
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

