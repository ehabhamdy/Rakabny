package com.ehab.rakabny.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.customviews.TextViewLight;
import com.ehab.rakabny.model.Event;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent inte = getIntent();

        Bundle bundle = getIntent().getExtras();
        Event event = bundle.getParcelable(EventsActivity.EXTRA_EVENT_DETAILS);


//        tv = (TextView) findViewById(R.id.tt);
//        tv.setText(event.description);

    }
}
