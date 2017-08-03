package com.ehab.rakabny.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySignUp extends ActivityBase {


    private static final String TAG = "Message";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    EditText mUsernameField;
    EditText mEmailField;
    EditText mPasswordField;

    private ProgressDialog mProgressDialog;

    String defaultLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        defaultLine = getString(R.string.default_line_text);

        TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        tv.setText(R.string.signup_activity_title);
        //Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/VarelaRound-Regular.ttf");
        //tv.setTypeface(custom_font);


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

                //create new user
                signUp(username, email, password);
                //Toast.makeText(SignUpActivity.this, email, Toast.LENGTH_SHORT).show();
                //Log.i(TAG, email);


            }
        });


    }

    public void signUp(final String username, final String email, String password) {

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
                            onAuthSuccess(task.getResult().getUser(), username, email);
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

        return result;
    }

    private void onAuthSuccess(FirebaseUser user, String username, String email) {
        //String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, email);

        // Go to MainActivity
//        Intent intent = new Intent(getActivity(), ActivityUserMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);

        Toast.makeText(getApplicationContext(), R.string.signup_feedback_message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        //finish();
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        int numberOfTickets = 0;
        Passenger passenger = new Passenger(name, email, defaultLine, numberOfTickets);

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

