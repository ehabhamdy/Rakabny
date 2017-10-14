package com.ehab.rakabny.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Event;
import com.ehab.rakabny.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends BaseActivity implements EventOnClickListener {

    private static final String TAG = "Message";
    public static final String EXTRA_EVENT_DETAILS = "event";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mEventsReference;
    private RecyclerView mEventsRecycler;
    private EventsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mToolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        tv.setText("Current Events");

        mDatabase = Utils.getDatabase();

        mEventsReference = mDatabase.getReference().child("events");

        mEventsRecycler = (RecyclerView) findViewById(R.id.recycler_events);
        mEventsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new EventsAdapter(this, mEventsReference);
        mEventsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(Event event) {
        Intent intent = new Intent(getApplicationContext(), EventDetailsActivity.class);
        intent.putExtra(EXTRA_EVENT_DETAILS, event);
        startActivity(intent);
    }


    private static class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        final private EventOnClickListener mOnClickListener;

        private List<String> mEventIds = new ArrayList<>();
        private List<Event> mEvents = new ArrayList<>();

        public EventsAdapter(final EventsActivity context, DatabaseReference ref) {
            mOnClickListener = context;
            mContext = context;
            mDatabaseReference = ref;

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    Event order = dataSnapshot.getValue(Event.class);

                    // Update RecyclerView
                    mEventIds.add(dataSnapshot.getKey());
                    mEvents.add(order);
                    notifyItemInserted(mEvents.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    Event newEvent = dataSnapshot.getValue(Event.class);
                    String commentKey = dataSnapshot.getKey();

                    int orderIndex = mEventIds.indexOf(commentKey);
                    if (orderIndex > -1) {
                        // Replace with the new data
                        mEvents.set(orderIndex, newEvent);

                        // Update the RecyclerView
                        notifyItemChanged(orderIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    String orderKey = dataSnapshot.getKey();

                    int orderIndex = mEventIds.indexOf(orderKey);
                    if (orderIndex > -1) {
                        // Remove data from the list
                        mEventIds.remove(orderIndex);
                        mEvents.remove(orderIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(orderIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + orderKey);
                    }

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    Event movedOrder = dataSnapshot.getValue(Event.class);
                    String orderKey = dataSnapshot.getKey();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load orders.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(mChildEventListener);
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.event_list_item, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventViewHolder holder, int position) {
            Event event = mEvents.get(position);
            holder.titleView.setText(event.name);
            holder.descriptionView.setText(event.description);
            holder.priceTextView.setText(Integer.toString(event.price));
            Picasso.with(mContext).load(event.posterUrl).into(holder.posterImageView);
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

        public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView titleView;
            public TextView descriptionView;
            public ImageView posterImageView;
            public TextView priceTextView;

            public EventViewHolder(View itemView) {
                super(itemView);

                titleView = (TextView) itemView.findViewById(R.id.event_name_textview);
                descriptionView = (TextView) itemView.findViewById(R.id.description_textview);
                posterImageView = (ImageView) itemView.findViewById(R.id.poster_imageview);
                priceTextView = (TextView) itemView.findViewById(R.id.price_textview);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int clickPosition = getAdapterPosition();
                Event clickedEvent = mEvents.get(clickPosition);
                mOnClickListener.onListItemClick(clickedEvent);
            }
        }


    }
}
