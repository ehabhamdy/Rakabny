package com.ehab.rakabny.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehabhamdy on 10/15/17.
 */

public class EventRegistrationInformation {
    public String username;
    public String lastName;
    public String email;
    public int phoneNumber;
    public String facebookAccountUrl;
    public int numberOfSeats;
    public String date;
    public String eventTitle;

    public EventRegistrationInformation() {
    }

    public EventRegistrationInformation(String eventTitle, String username, String lastName, String email, int phone ,String facebookAccountUrl, int numberOfSeats, String date) {
        this.eventTitle  = eventTitle;
        this.username = username;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phone;
        this.facebookAccountUrl = facebookAccountUrl;
        this.numberOfSeats = numberOfSeats;
        this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("lastName", lastName);
        result.put("phoneNumber", phoneNumber);
        result.put("facebook", facebookAccountUrl);
        result.put("email", email);
        result.put("seats", numberOfSeats);
        result.put("date", date);
        result.put("event", eventTitle);

        return result;
    }
}
