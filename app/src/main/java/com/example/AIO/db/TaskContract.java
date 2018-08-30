package com.example.AIO.db;

import android.provider.BaseColumns;

import com.example.AIO.Task;

public final class TaskContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TaskContract() {}

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COL_TASK_NAME = "name";
        public static final String COL_TASK_NOTES = "notes";
    }
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COL_TASK_NAME + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COL_TASK_NOTES + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;
}