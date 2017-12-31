package com.ehab.rakabny.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehabhamdy on 12/26/17.
 */

public class BusReservationInformation {
    String name;
    String from;
    String to;
    String date;
    String time;
    int numberOfSeats;

    public BusReservationInformation() {
    }

    public BusReservationInformation(String name, String from, String to, String date, String time, int numberOfSeats) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.numberOfSeats = numberOfSeats;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("from", from);
        result.put("to", to);
        result.put("date", date);
        result.put("time", time);
        result.put("seats", numberOfSeats);

        return result;
    }
}
