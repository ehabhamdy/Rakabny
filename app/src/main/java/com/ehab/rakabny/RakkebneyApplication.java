package com.ehab.rakabny;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ehabhamdy on 7/14/17.
 */

public class RakkebneyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
