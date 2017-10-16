package com.ehab.rakabny.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Event;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ehab.rakabny.ui.EventsActivity.EXTRA_EVENT_DETAILS;

public class EventDetailsActivity extends AppCompatActivity {

    @BindView(R.id.poster_imageview)
    ImageView posterImageView;

    @BindView(R.id.findLocation_button)
    LinearLayout findLocationView;

    @BindView(R.id.detailedDescription_textview)
    TextView detailedDescriptionTextView;

    Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        event = bundle.getParcelable(EXTRA_EVENT_DETAILS);

        Picasso.with(this).load(event.bannerUrl).into(posterImageView);

        findLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //37.7749,-122.4194 31.229867,29.951686
                Uri gmmIntentUri = Uri.parse("geo: 31.229867,29.951686?q=31.229867,29.951686(Level One Gift Shop)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        detailedDescriptionTextView.setText(event.detailedDescription);

    }

    @OnClick(R.id.gotoRegister_button)
    public void gotoRegistration(View view) {
        Intent intent = new Intent(getApplicationContext(), EventRegistrationActivity.class);
        intent.putExtra(EXTRA_EVENT_DETAILS, event);
        startActivity(intent);
    }
}
