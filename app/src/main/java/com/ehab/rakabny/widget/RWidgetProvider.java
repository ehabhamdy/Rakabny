package com.ehab.rakabny.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import com.ehab.rakabny.R;
import com.ehab.rakabny.ui.MainActivity;
import com.ehab.rakabny.ui.TicketsActivity;


/**
 * Created by ehabhamdy on 7/31/17.
 */

public class RWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rakkebny_widget_layout);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_header, pendingIntent);

            Intent ticketPurchaseIntent = new Intent(context, TicketsActivity.class);
            PendingIntent ticketPurchasePendingIntent = PendingIntent.getActivity(context, 0, ticketPurchaseIntent, 0);
            views.setOnClickPendingIntent(R.id.buyTicket_button, ticketPurchasePendingIntent);


            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }



            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }


    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {

    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {

    }
}
