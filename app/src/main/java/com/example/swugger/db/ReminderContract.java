package com.example.swugger.db;

import android.provider.BaseColumns;

public final class ReminderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ReminderContract() {}

    public static class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COL_REMINDER_EVENT_ID = "event_id";
        public static final String COL_REMINDER_MILLISECONDS = "month";
        public static final String COL_REMINDER_DAYS_BEFORE = "days_before";
        public static final String COL_REMINDER_HOURS_BEFORE = "hours_before";
        public static final String COL_REMINDER_MINUTES_BEFORE = "minutes_before";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ReminderEntry.TABLE_NAME + " (" +
                    ReminderEntry._ID + " INTEGER PRIMARY KEY," +
                    ReminderEntry.COL_REMINDER_EVENT_ID + TEXT_TYPE + COMMA_SEP +
                    ReminderEntry.COL_REMINDER_MILLISECONDS + TEXT_TYPE + COMMA_SEP +
                    ReminderEntry.COL_REMINDER_DAYS_BEFORE + TEXT_TYPE + COMMA_SEP +
                    ReminderEntry.COL_REMINDER_HOURS_BEFORE + TEXT_TYPE + COMMA_SEP +
                    ReminderEntry.COL_REMINDER_MINUTES_BEFORE + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME;
}