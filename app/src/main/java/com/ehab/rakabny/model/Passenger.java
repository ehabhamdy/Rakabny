package com.ehab.rakabny.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ehab on 10/22/2016.
 */


@IgnoreExtraProperties
public class Passenger {

    public String username;
    public String email;
    public String line;
    public int numberOfTickets;


    public Passenger() {
        // Default constructor required for calls to DataSnapshot.getValue(Driver.class)
    }

    public Passenger(String username) {
        this.username = username;
    }
    public Passenger(String username, String email, String line, int numberOfTickets) {
        this.username = username;
        this.email = email;
        this.line = line;
        this.numberOfTickets = numberOfTickets;
    }
}