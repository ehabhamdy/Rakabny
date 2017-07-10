package com.ehab.rakabny.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ehab on 10/22/2016.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        return mDatabase;
    }
}
