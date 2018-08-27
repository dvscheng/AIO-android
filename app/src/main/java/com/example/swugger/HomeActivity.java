package com.example.swugger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.swugger.db.EventContract;
import com.example.swugger.db.ReminderContract;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {

    /** The custom toolbar that I made, has teal-ish color.
     * Similar in flavor to MainActivity's. */
    private Toolbar mToolbar;
    /** An intent message that I don't use. */
    public final static String EXTRA_MESSAGE_EP = "com.example.Swugger.MESSAGE_E";

    private final static int NUM_OF_FRAGMENTS_OFF_SCREEN = 2;

    /** The pager widget, which handles animation and allows
     * swiping horizontally to access previous and next wizard steps. */
    private ViewPager mPager;

    /** The pager adapter, which provides the pages to the view pager widget. */
    private ScreenSlidePagerAdapter mPagerAdapter;      // was PagerAdapter

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NotificationPublisher.REMINDER_NOTIFICATION_ID)) {
            int reminderId = intent.getIntExtra(NotificationPublisher.REMINDER_NOTIFICATION_ID, -1);
            mPagerAdapter.getEventFrag().onNotificationClick(reminderId);
        }
    }

    /** 1. Set the toolbar.
     *  5. Create a PagerTabStrip (the white indicator of which tab you're on),
     *      set the color to white (I think), and set the Strings' color to white.
     *  6. Instantiate a ViewPager and a PagerAdapter set the number of off-screen fragments
     *      to 2 so that the fragments don't get destroyed and remade when at an edge fragment.
     *      Set the starting 'screen' or fragment to the middle one - the calendar.
     * */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1.
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        // 2.
        FragmentManager fragMan = getSupportFragmentManager();

        // 5.
        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pagertabstrip_main);
        tabStrip.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabStrip.setTextColor(Color.WHITE);
        tabStrip.setTabIndicatorColor(Color.WHITE);

        // 6.
        mPager = (ViewPager) findViewById(R.id.viewpager_main);
        mPagerAdapter = new ScreenSlidePagerAdapter(fragMan);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(NUM_OF_FRAGMENTS_OFF_SCREEN);
        mPager.setCurrentItem(1);

        // Create a notification channel
        createNotificationChannel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.debug_toolbar_task_list:
                // User chose the "Accounts" action.
                // TODO: list out the tasks and events in a dialogfragment when pressed
                mPagerAdapter.getTaskFrag().printDatabase();
                return true;
            case R.id.debug_toolbar_event_list:
                mPagerAdapter.getEventFrag().printDatabase(EventContract.EventEntry.TABLE_NAME);
                return true;
            case R.id.debug_toolbar_reminder_list:
                mPagerAdapter.getEventFrag().printDatabase(ReminderContract.ReminderEntry.TABLE_NAME);
                return true;
            case R.id.debug_show_notification_now:
                showNotification(true);
                return true;
            case R.id.debug_show_notification_delayed:
                showNotification(false);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Show a DialogFragment
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        // TODO: more than one channel for other notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;       // allows heads-up notification to display
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            NotificationChannel channel = new NotificationChannel(NotificationPublisher.EVENT_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);
            channel.enableVibration(true);      // may be unnecessary
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, audioAttributes);        // may be unnecessary
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(boolean now) {
        Intent intentHome = new Intent(this, HomeActivity.class);
        intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // intent.setFlags()  TODO: do this later
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentHome, PendingIntent.FLAG_UPDATE_CURRENT);

        // Only user Notification.Builder(Context, channelId) when API 26+
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, NotificationPublisher.EVENT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_today_black_24dp)
                    .setContentTitle("Test notification!")
                    .setContentText("This is a nice old test that tests long sentences to see if the test that tests long sentences to see")      // TODO: consider reformatting string to be more readable
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_EVENT);
                    // priority is now set in channel creation

            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_today_black_24dp)
                    .setContentTitle("Test notification!")
                    .setContentText("This is a nice old test that tests long sentences to see if the test that tests long sentences to see")      // TODO: consider reformatting string to be more readable
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_MAX);         // max priority allows heads-up notification to display

            notification = builder.build();
        }

        int debugNotificationId = 2100000000;   // 2.1b
        if (now) {
            NotificationManagerCompat.from(this).notify(debugNotificationId, notification);
        } else {
            long currentTimeInMilliseconds = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTimeInMillis();
            long delay = 5;  // in seconds
            long delayInMilliseconds = currentTimeInMilliseconds + (delay * 1000);

            Intent intentAlarm = new Intent(this, NotificationPublisher.class);
            intentAlarm.putExtra(NotificationPublisher.DEBUG_NOTIFICATION, notification);
            intentAlarm.putExtra(NotificationPublisher.DEBUG_NOTIFICATION_ID, debugNotificationId);
            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(this, debugNotificationId, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);     // reminder.getId() will (probably) never pass 2.1b because you'd need 2.1b reminders

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, delayInMilliseconds, pendingIntentAlarm);
        }
    }
}
