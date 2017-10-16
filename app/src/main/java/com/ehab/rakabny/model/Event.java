package com.ehab.rakabny.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by ehabhamdy on 9/10/17.
 */

public class Event implements Parcelable {
    public String name;
    public String description;
    public String detailedDescription;
    public int price;
    public String posterUrl;
    public String bannerUrl;
    public String status;

    public Event(){
    }

    protected Event(Parcel in) {
        name = in.readString();
        description = in.readString();
        detailedDescription = in.readString();
        price = in.readInt();
        posterUrl = in.readString();
        status = in.readString();
        bannerUrl = in.readString();
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
        dest.writeString(detailedDescription);
        dest.writeInt(price);
        dest.writeString(posterUrl);
        dest.writeString(status);
        dest.writeString(bannerUrl);
    }
}
