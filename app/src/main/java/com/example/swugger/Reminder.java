package com.example.swugger;

import android.app.PendingIntent;

/**
 * Created by David on 8/11/2018.
 */

public class Reminder {
    /* The associated event's id. */
    private long eventId;
    /* The exact date (MM/DD/YYYY; HH:mm) in milliseconds of the reminder. */
    private long timeInMilliseconds;
    /* The days, hours, and minutes before the associated event */
    private int daysBefore;
    private int hoursBefore;
    private int minutesBefore;

    Reminder(long eventId, long timeInMilliseconds, int daysBefore, int hoursBefore, int minutesBefore) {
        this.eventId = eventId;
        this.timeInMilliseconds = timeInMilliseconds;
        this.daysBefore = daysBefore;
        this.hoursBefore = hoursBefore;
        this.minutesBefore = minutesBefore;
    }

    public void arm() {

    }

    public void cancel() {

    }

    public long getEventId() {
        return eventId;
    }

    public long getTimeInMilliseconds() { return timeInMilliseconds; }

    public int getDaysBefore() {
        return daysBefore;
    }

    public int getHoursBefore() {
        return hoursBefore;
    }

    public int getMinutesBefore() {
        return minutesBefore;
    }

    public boolean isSavedReminder() { return false; }

    @Override
    public String toString() {
        String day = "";
        String hour = "";
        String minute = "";
        if (daysBefore != 0) {
            day += Integer.toString(daysBefore) + " day";
            if (daysBefore > 1) {
                day += "s";
            }
            day += " ";
        }
        if (hoursBefore != 0) {
            hour += Integer.toString(hoursBefore) + " hour";
            if (hoursBefore > 1) {
                hour += "s";
            }
            hour += " ";
        }
        if (minutesBefore != 0) {
            minute += Integer.toString(minutesBefore) + " minute";
            if (minutesBefore > 1) {
                minute += "s";
            }
        }

        return day + hour + minute;
    }
}
