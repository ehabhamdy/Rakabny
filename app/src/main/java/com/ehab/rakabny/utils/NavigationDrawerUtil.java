package com.ehab.rakabny.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ehab.rakabny.R;
import com.ehab.rakabny.model.Passenger;
import com.ehab.rakabny.ui.ActivityLogin;
import com.ehab.rakabny.ui.EventsActivity;
import com.ehab.rakabny.ui.LineSubscriptionActivity;
import com.ehab.rakabny.ui.ProfileActivity;
import com.ehab.rakabny.ui.TicketsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.Locale;

/**
 * Created by ehabhamdy on 7/3/17.
 */

public class NavigationDrawerUtil {
    Drawer drawer;
    AccountHeader headerResult;

    public static final String SUB_LINE_EXTRA = "Line_Extra_Data";
    public static final String USERNAME_EXTRA = "Username_Data";
    public static final String EMAIL_EXTRA = "email_Data";
    public static final String TICKETS_EXTRA = "tickets_Data";

    public Drawer getDrawer() {
        return drawer;
    }

    public Drawer SetupNavigationDrawer(Toolbar mToolbar, final Activity activity, final Passenger user) {
        // Create the AccountHeader

        AssetManager am = activity.getApplicationContext().getAssets();

        Typeface typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "OpenSans-Semibold.ttf"));

        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.drawer_bg)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.username).withEmail(user.email).withIcon(activity.getResources().getDrawable(R.drawable.toobar_icon))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //new DrawerBuilder().withActivity(this).withAccountHeader(headerResult).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem main = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.nav_main_label).withTypeface(typeface).withIcon(R.drawable.ic_room_black_24dp);
        PrimaryDrawerItem subsToLine = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.nav_subscribe_label).withTypeface(typeface).withIcon(R.drawable.ic_trending_up_black_24dp);
        //PrimaryDrawerItem tickets = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.nav_tickets_label).withTypeface(typeface).withIcon(R.drawable.ic_ticket_black_24dp);
        PrimaryDrawerItem matchReservation = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.nav_current_events_label).withTypeface(typeface).withIcon(R.drawable.football);
        PrimaryDrawerItem profile = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.nav_profile_label).withTypeface(typeface).withIcon(R.drawable.ic_person_black_24dp);
        PrimaryDrawerItem logout = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.nav_logout_label).withTypeface(typeface).withIcon(R.drawable.ic_out_black_24dp);
        //create the drawer and remember the `Drawer` result object
        drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        main,
                        new DividerDrawerItem(),
                        subsToLine,
                        new DividerDrawerItem(),
                       /* tickets,
                        new DividerDrawerItem(),
                       */ matchReservation,
                        new DividerDrawerItem(),
                        profile,
                        new DividerDrawerItem(),
                        logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 1:
                                drawer.closeDrawer();
                                return true;
                            case 3:
                                Intent openSubscriptionIntent = new Intent(activity.getApplicationContext(), LineSubscriptionActivity.class);
                                openSubscriptionIntent.putExtra(SUB_LINE_EXTRA, user.line);
                                activity.startActivity(openSubscriptionIntent);
                                activity.overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                                drawer.closeDrawer();
                                return true;
                           /* case 5:
                                Intent openTicketsIntent = new Intent(activity.getApplicationContext(), TicketsActivity.class);
                                openTicketsIntent.putExtra(TICKETS_EXTRA, String.valueOf(user.numberOfTickets));
                                activity.startActivity(openTicketsIntent);
                                drawer.closeDrawer();
                                return true;*/
                            case 5:
                                Intent openMatchResIntent = new Intent(activity.getApplicationContext(), EventsActivity.class);
                                activity.startActivity(openMatchResIntent);
                                drawer.closeDrawer();
                                return true;
                            case 7:
                                Intent openProfileIntent = new Intent(activity.getApplicationContext(), ProfileActivity.class);
                                openProfileIntent.putExtra(USERNAME_EXTRA, user.username);
                                openProfileIntent.putExtra(EMAIL_EXTRA, user.email);
                                openProfileIntent.putExtra(TICKETS_EXTRA, String.valueOf(user.numberOfTickets));
                                activity.startActivity(openProfileIntent);
                                drawer.closeDrawer();
                                return true;
                            case 9:
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(activity.getApplicationContext(), ActivityLogin.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(intent);
                                activity.finish();
                                break;

                        }


                        return true;
                    }
                })
                .withAccountHeader(headerResult)
                .build();


        return drawer;
    }
}
