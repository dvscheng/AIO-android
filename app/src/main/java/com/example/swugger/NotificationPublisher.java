package com.example.swugger;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.example.swugger.db.DatabaseHelper;

import static android.content.ContentValues.TAG;

/**
 * Created by David on 8/18/2018.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public final static String EVENT_CHANNEL_ID = "event_channel_id";
    public final static String EVENT_NOTIFICATION = "event_notification";
    public final static String EVENT_NOTIFICATION_ID = "event_notification_id";
    public final static String DEBUG_NOTIFICATION = "debug_notification";
    public final static String DEBUG_NOTIFICATION_ID = "debug_notification_id";


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Notification notification;
        int id;
        /*Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }*/

        DatabaseHelper databaseHelper;
        if (intent.hasExtra(DEBUG_NOTIFICATION) && intent.hasExtra(DEBUG_NOTIFICATION_ID)) {
            notification = intent.getParcelableExtra(DEBUG_NOTIFICATION);
            id = intent.getIntExtra(DEBUG_NOTIFICATION_ID, 0);
        } else {
            // DEFAULT TO EVENT NOTIFICATION
            notification = intent.getParcelableExtra(EVENT_NOTIFICATION);
            id = intent.getIntExtra(EVENT_NOTIFICATION_ID, 0);
            databaseHelper = DatabaseHelper.getInstance(context, DatabaseHelper.EVENTS_DB_NAME, DatabaseHelper.DATABASE_VERSION);
            String whereClause = "";
            String[] whereArgs = { "hi" };
            // TODO:
            databaseHelper.deleteFromDatabase(databaseHelper, DatabaseHelper.EVENTS_DB_NAME, whereClause, whereArgs);
        }

        notificationManager.notify(id, notification);
    }
}
