package com.ehab.rakabny.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by ehabhamdy on 9/10/17.
 */

public class Event implements Parcelable {
    public String name;
    public String description;
    public int price;
    public String posterUrl;
    public String status;

    public Event(){
    }

    protected Event(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readInt();
        posterUrl = in.readString();
        status = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeString(posterUrl);
        dest.writeString(status);
    }
}
