package com.ehab.rakabny.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends BaseActivity {

    private static final String TAG = "Message";
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference muser;
    private FirebaseAuth mAuth;

    @BindView(R.id.login_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView tv;
    @BindView(R.id.sigup_textview)
    TextView mSignUpTextView;
    @BindView(R.id.email_edittext)
    TextView mEmailField;
    @BindView(R.id.password_edittext)
    TextView mPasswordField;
    @BindView(R.id.login_button)
    TextView loginButton;

    @BindView(R.id.smslogin_button)
    TextView smsLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tv.setText(R.string.login_activity_title);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String e = mEmailField.getText().toString();
                    String p = mPasswordField.getText().toString();
                    login(e, p);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.connection_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        smsLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SMSSignupActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login(String email, String password) {

        //Check if the email or password fields are empty
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        hideProgressDialog();

                        if (task.isSuccessful() && mAuth.getCurrentUser().isEmailVerified()) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, R.string.authentication_failed_message, Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();

                            } else {
                                Toast.makeText(LoginActivity.this, R.string.authentication_error_message, Toast.LENGTH_SHORT).show();
                            }
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
}
