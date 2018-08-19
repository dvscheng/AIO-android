package com.example.swugger;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by David on 8/18/2018.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public final static String EVENT_CHANNEL_ID = "event_channel_id";
    public final static String EVENT_NOTIFICATION = "event_notification";
    public final static String EVENT_NOTIFICATION_ID = "event_notification_id";


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = intent.getParcelableExtra(EVENT_NOTIFICATION);
        int id = intent.getIntExtra(EVENT_NOTIFICATION_ID, 0);

        notificationManager.notify(id, notification);
    }
}
