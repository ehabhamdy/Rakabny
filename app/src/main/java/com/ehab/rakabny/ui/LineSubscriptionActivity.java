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
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

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
        mToolbar = (Toolbar) findViewById(R.id.common_toolbar);
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

                new LovelyStandardDialog(LineSubscriptionActivity.this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setButtonsColorRes(R.color.primary_dark)
                        .setIcon(R.drawable.ic_info_black_48dp)
                        .setTitle(R.string.linechange_dialog_title)
                        .setMessage(R.string.linechange_dialog_body)
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // find the radiobutton by returned id
                                RadioButton selectedLineRadio = (RadioButton) findViewById(selectedId);

                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("passengers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("line").setValue(selectedLineRadio.getText());
                            }
                        })
                        .setNegativeButton(R.string.edit, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setCurrentLine(line);
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
