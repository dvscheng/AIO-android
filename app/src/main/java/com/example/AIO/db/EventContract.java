package com.example.AIO.db;

import android.provider.BaseColumns;

public final class EventContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private EventContract() {}

    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COL_EVENT_NAME = "name";
        public static final String COL_EVENT_NOTES = "notes";
        public static final String COL_EVENT_MONTH = "month";
        public static final String COL_EVENT_DAY = "day";
        public static final String COL_EVENT_YEAR = "year";
        public static final String COL_EVENT_HOUR = "hour";
        public static final String COL_EVENT_MINUTE = "minute";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventEntry.COL_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COL_EVENT_NOTES + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COL_EVENT_MONTH + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COL_EVENT_DAY + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COL_EVENT_YEAR + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COL_EVENT_HOUR + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COL_EVENT_MINUTE + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;
}