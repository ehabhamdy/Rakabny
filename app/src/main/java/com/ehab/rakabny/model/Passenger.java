package com.ehab.rakabny.model;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ehab on 10/22/2016.
 */


@IgnoreExtraProperties
public class Passenger {

    public String username;
    public String email;
    public String line;
    public int numberOfTickets = 0;
    public String photoUrl;
    public String phoneNumber;
    public String gender;
    public String area;
    public String college;

    public Passenger() {
        // Default constructor required for calls to DataSnapshot.getValue(Driver.class)
    }

    public Passenger(String username) {
        this.username = username;
    }

    public Passenger(String username, String email, String line, int numberOfTickets, String photoUrl, String phoneNumber, String gender, String area, String college) {
        this.username = username;
        this.email = email;
        this.line = line;
        this.numberOfTickets = numberOfTickets;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.area = area;
        this.gender = gender;
        this.college = college;
    }
}