package com.ehab.rakabny.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.BusReservationInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyReservationActivity extends AppCompatActivity {
    private static final String TAG = "Message";

    private DatabaseReference mDatabaseRef;
    private RecyclerView mReservationsRecycler;
    private ReservationsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.common_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("My Reservations");


        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user-bus-reservations").child(auth.getUid());

        mReservationsRecycler = (RecyclerView) findViewById(R.id.reservations_recyclerview);
        mReservationsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new ReservationsAdapter(this, mDatabaseRef);
        mReservationsRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.cleanupListener();
    }

    private static class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mReservationIds = new ArrayList<>();
        private List<BusReservationInformation> mReservation = new ArrayList<>();

        public ReservationsAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new Reservation has been added, add it to the displayed list
                    BusReservationInformation Reservation = dataSnapshot.getValue(BusReservationInformation.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mReservationIds.add(dataSnapshot.getKey());
                    mReservation.add(Reservation);
                    notifyItemInserted(mReservation.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    BusReservationInformation newOrder = dataSnapshot.getValue(BusReservationInformation.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int reservationIndex = mReservationIds.indexOf(commentKey);
                    if (reservationIndex > -1) {
                        // Replace with the new mData
                        mReservation.set(reservationIndex, newOrder);

                        // Update the RecyclerView
                        notifyItemChanged(reservationIndex);
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
                    String reservationKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int reservationIndex = mReservationIds.indexOf(reservationKey);
                    if (reservationIndex > -1) {
                        // Remove mData from the list
                        mReservationIds.remove(reservationIndex);
                        mReservation.remove(reservationIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(reservationIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + reservationKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    BusReservationInformation movedReservation = dataSnapshot.getValue(BusReservationInformation.class);
                    String reservationKey = dataSnapshot.getKey();

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
        public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.reservation_list_item, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReservationViewHolder holder, int position) {
            BusReservationInformation order = mReservation.get(position);
            holder.dateTextView.setText(order.getDate());
            holder.fromTextView.setText(order.getFrom());
            holder.toTextView.setText(order.getTo());
            holder.seatsTextView.setText(order.getSeats());
        }

        @Override
        public int getItemCount() {
            return mReservation.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

        class ReservationViewHolder extends RecyclerView.ViewHolder {

            public TextView dateTextView;
            public TextView fromTextView;
            public TextView toTextView;
            public TextView seatsTextView;


            public ReservationViewHolder(View itemView) {
                super(itemView);

                dateTextView = (TextView) itemView.findViewById(R.id.name_textview);
                fromTextView = (TextView) itemView.findViewById(R.id.from_textview);
                toTextView = (TextView) itemView.findViewById(R.id.to_textview);
                seatsTextView = (TextView) itemView.findViewById(R.id.seats_textview);
            }
        }

    }


}
