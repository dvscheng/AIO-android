package com.example.swugger;

/**
 * Everything is the same as the Reminder class, but with an Id, where Id comes from inserting it into a db.
 */

public class ReminderWithId extends Reminder {
    private long id;

    public ReminderWithId(long id, long eventId, long timeInMilliseconds, int daysBefore, int hoursBefore, int minutesBefore) {
        this(eventId, timeInMilliseconds, daysBefore, hoursBefore, minutesBefore);
        this.id = id;
    }

    /** Used in conjunction with the constructor requiring an id param. */
    private ReminderWithId(long eventId, long timeInMilliseconds, int daysBefore, int hoursBefore, int minutesBefore) {
        super(eventId, timeInMilliseconds, daysBefore, hoursBefore, minutesBefore);
    }
}
