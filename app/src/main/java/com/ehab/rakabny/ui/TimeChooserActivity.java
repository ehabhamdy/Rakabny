package com.ehab.rakabny.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ehab.rakabny.ui.LocationChooserActivity.EXTRA_FROM;

public class TimeChooserActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView activityTitleTextView;
    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.next_button)
    Button nextButtons;
    Bundle bundle;

    @BindView(R.id.time1_textview)
    TextView time1TextView;
    @BindView(R.id.time2_textview)
    TextView time2TextView;
    @BindView(R.id.time3_textview)
    TextView time3TextView;
    @BindView(R.id.time4_textview)
    TextView time4TextView;

    @BindView(R.id.passengers_edittext)
    EditText passengersEditText;

    @BindView(R.id.date_textview)
    TextView reservationDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_chooser);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityTitleTextView.setText("Choose Time");

        List<String> dropOffLocations = Arrays.asList(getResources().getStringArray(R.array.to_locations_labels));

        Intent intent = getIntent();
        bundle = intent.getBundleExtra("Bundle");
        bundle.putString("Time", "8:00 AM");

        String pickupLocation = bundle.getString(EXTRA_FROM);


        if (dropOffLocations.contains(pickupLocation)) {
            time1TextView.setText("2:00 PM");
            time2TextView.setText("2:30 PM");
            time3TextView.setText("3:00 PM");
            time4TextView.setText("3:30 PM");

            bundle.putString("Time", "2:00 PM");
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        Date tomorrow = gc.getTime();
        final DateFormat dateFormat = DateFormat.getDateInstance();
        reservationDateTextView.setText(dateFormat.format(tomorrow));
        bundle.putString("Date", reservationDateTextView.getText().toString());

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                reservationDateTextView.setText(dateFormat.format(newDate.getTime()));
                bundle.putString("Date", reservationDateTextView.getText().toString());
            }

        }, gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));




        reservationDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
    }


    public void timeClicked(View view) {
        TextView timeText = (TextView) view;
        String time = timeText.getText().toString();
        bundle.putString("Time", time);
        time1TextView.setBackgroundColor(getResources().getColor(R.color.white));
        time2TextView.setBackgroundColor(getResources().getColor(R.color.white));
        time3TextView.setBackgroundColor(getResources().getColor(R.color.white));
        time4TextView.setBackgroundColor(getResources().getColor(R.color.white));
        view.setBackgroundColor(getResources().getColor(R.color.accent));

    }

    @OnClick(R.id.next_button)
    public void nextClicked() {
        Intent intent = new Intent(this, BusOrderSummaryActivity.class);
        bundle.putString("Seats", passengersEditText.getText().toString());
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }
}
