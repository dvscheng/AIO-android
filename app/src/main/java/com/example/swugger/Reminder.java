package com.example.swugger;

import android.app.PendingIntent;

/**
 * Created by David on 8/11/2018.
 */

public class Reminder {
    private PendingIntent mPendingIntent;
    private long timeInMilliseconds;
    private int daysBefore;
    private int hoursBefore;
    private int minutesBefore;

    Reminder(long timeInMilliseconds, int days, int hours, int minutes) {
        this.timeInMilliseconds = timeInMilliseconds;
        daysBefore = days;
        hoursBefore = hours;
        minutesBefore = minutes;
    }

    public void arm() {

    }

    public void cancel() {

    }

    @Override
    public String toString() {
        return Long.toString(timeInMilliseconds)
                + Integer.toString(daysBefore) + Integer.toString(hoursBefore) + Integer.toString(minutesBefore);
    }
}
