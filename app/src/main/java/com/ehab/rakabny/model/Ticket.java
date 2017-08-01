package com.ehab.rakabny.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ehabhamdy on 7/10/17.
 */

public class Ticket {
    public String username;
    public LatLng userLocation;
    public String ticketNumber;

    public Ticket(String username, String ticketNumber, LatLng userLocation) {
        this.username = username;
        this.ticketNumber = ticketNumber;
        this.userLocation = userLocation;
    }
}
