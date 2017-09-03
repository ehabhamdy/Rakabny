package com.ehab.rakabny.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.model.Ticket;
import com.ehab.rakabny.utils.NavigationDrawerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ehab.rakabny.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cn.pedant.SweetAlert.SweetAlertDialog.*;

public class LineSubscriptionActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @BindView(R.id.radioManshiaAsfra)
    RadioButton r1;

    @BindView(R.id.radioManshiaVictoria)
    RadioButton r2;

    @BindView(R.id.radioManshiaMandra)
    RadioButton r3;

    @BindView(R.id.radioAsfraMahta)
    RadioButton r4;

    @BindView(R.id.radioMandraMahta)
    RadioButton r5;

    @BindView(R.id.radioMandraAboker)
    RadioButton r6;

    @BindView(R.id.radioLinesGroup)
    RadioGroup linesGroup;

    @BindView(R.id.btnConfirm)
    Button confirmButton;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_subscription);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        tv.setText(R.string.subscription_activity_label);

        //drawerUtil = new NavigationDrawerUtil();
        //drawerUtil.SetupNavigationDrawer(mToolbar, LineSubscriptionActivity.this ,username, email);
        Intent intent = getIntent();
        final String line = intent.getStringExtra(NavigationDrawerUtil.SUB_LINE_EXTRA);

        setCurrentLine(line);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int selectedId = linesGroup.getCheckedRadioButtonId();

                new SweetAlertDialog(LineSubscriptionActivity.this, WARNING_TYPE)
                        .setTitleText(getString(R.string.linechange_dialog_title))
                        .setContentText(getString(R.string.linechange_dialog_body))
                        .setConfirmText(getResources().getString(R.string.reservation_dialog_yes_button_text))
                        .setConfirmClickListener(new OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                // find the radiobutton by returned id
                                RadioButton selectedLineRadio = (RadioButton) findViewById(selectedId);

                                mDatabase = FirebaseDatabase.getInstance().getReference();

                                // TODO: Change the path to read from the passenger node
                                mDatabase.child("passengers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("line").setValue(selectedLineRadio.getText());



                                sDialog
                                        .setTitleText(getString(R.string.reservation_completed_dialog_title))
                                        .setContentText(getString(R.string.reservation_completed_dialog_content_text))
                                        .setConfirmText(getString(R.string.done_label_text))
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SUCCESS_TYPE);

                            }

                        })
                        .setCancelText(getResources().getString(R.string.reservation_dialog_no_button_text))
                        .setCancelClickListener(new OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                setCurrentLine(line);
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void setCurrentLine(String line) {

        if (line.equals(getString(R.string.manshia_asfra_line_label))) {
            r1.setChecked(true);
        } else if (line.equals(getString(R.string.manshia_victoria_line_label))) {
            r2.setChecked(true);
        } else if (line.equals(getString(R.string.manshia_mandra_line_label))) {
            r3.setChecked(true);
        } else if (line.equals(getString(R.string.asfra_mahta_line_label))) {
            r4.setChecked(true);
        } else if (line.equals(getString(R.string.mandra_mahta_line_label))) {
            r5.setChecked(true);
        } else {
            r6.setChecked(true);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);

    }
}
