package com.ehab.rakabny.ui;

import android.view.View;

import com.ehab.rakabny.model.Event;

/**
 * Created by ehabhamdy on 10/7/17.
 */

public interface EventOnClickListener {
    void onListItemClick(Event clickedItemIndex, View v);
}
