package com.ehab.rakabny.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ehabhamdy on 7/10/17.
 */

public class Ticket {
    public String username;
    public LatLng userLocation;
    public String ticketNumber;
    public String userID;
    public boolean valid = true;

    public Ticket(String username, String ticketNumber, String userID ,LatLng userLocation) {
        this.username = username;
        this.ticketNumber = ticketNumber;
        this.userLocation = userLocation;
        this.userID = userID;
    }
}
