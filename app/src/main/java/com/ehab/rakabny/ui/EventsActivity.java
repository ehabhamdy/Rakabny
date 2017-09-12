package com.ehab.rakabny.ui;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends ActivityBase {

    private static final String TAG = "Message";
    private FirebaseDatabase mDatabase;
    private DatabaseReference mEventsReference;
    private RecyclerView mEventsRecycler;
    private EventsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

    private static class EventViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public  TextView descriptionView;
        public ImageView posterImageView;
        public TextView priceTextView;

        public EventViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.event_name_textview);
            descriptionView = (TextView) itemView.findViewById(R.id.description_textview);
            posterImageView = (ImageView) itemView.findViewById(R.id.poster_imageview);
            priceTextView = (TextView) itemView.findViewById(R.id.price_textview);
        }
    }

    private static class EventsAdapter extends RecyclerView.Adapter<EventViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mOrderIds = new ArrayList<>();
        private List<Event> mOrders = new ArrayList<>();

        public EventsAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new order has been added, add it to the displayed list
                    Event order = dataSnapshot.getValue(Event.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mOrderIds.add(dataSnapshot.getKey());
                    mOrders.add(order);
                    notifyItemInserted(mOrders.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Event newOrder = dataSnapshot.getValue(Event.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int orderIndex = mOrderIds.indexOf(commentKey);
                    if (orderIndex > -1) {
                        // Replace with the new data
                        mOrders.set(orderIndex, newOrder);

                        // Update the RecyclerView
                        notifyItemChanged(orderIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String orderKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int orderIndex = mOrderIds.indexOf(orderKey);
                    if (orderIndex > -1) {
                        // Remove data from the list
                        mOrderIds.remove(orderIndex);
                        mOrders.remove(orderIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(orderIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + orderKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Event movedOrder = dataSnapshot.getValue(Event.class);
                    String orderKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load orders.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(mChildEventListener);
            // [END child_event_listener_recycler]
        }

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.event_list_item, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventViewHolder holder, int position) {
            Event order = mOrders.get(position);
            holder.titleView.setText(order.name);
            holder.descriptionView.setText(order.description);
            holder.priceTextView.setText(Integer.toString(order.price));
            Picasso.with(mContext).load(order.posterUrl).into(holder.posterImageView);
        }

        @Override
        public int getItemCount() {
            return mOrders.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }




}
