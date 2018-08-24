package com.example.swugger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  Singleton Database helper
 * */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper eventsInstance;
    private static DatabaseHelper remindersInstance;
    private static DatabaseHelper tasksInstance;

    public static final String EVENTS_DB_NAME = "com.example.swugger.db.event";
    public static final String REMINDERS_DB_NAME = "com.example.swugger.db.reminders";
    public static final String TASKS_DB_NAME = "com.example.swugger.db";
    public static final int DATABASE_VERSION = 1;

    public static synchronized DatabaseHelper getInstance(Context context, String databaseName, int databaseVersion) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        switch (databaseName) {
            case EVENTS_DB_NAME:
                if (eventsInstance == null) {
                    eventsInstance = new DatabaseHelper(context.getApplicationContext(), databaseName, databaseVersion);
                }
                return eventsInstance;

            case REMINDERS_DB_NAME:
                if (remindersInstance == null) {
                    remindersInstance = new DatabaseHelper(context.getApplicationContext(), databaseName, databaseVersion);
                }
                return remindersInstance;

            case TASKS_DB_NAME:
                if (tasksInstance == null) {
                    tasksInstance = new DatabaseHelper(context.getApplicationContext(), databaseName, databaseVersion);
                }
                return tasksInstance;
        }
        // TODO: should never get here, enforce that the database name is one of the listed strings
        throw new IllegalArgumentException("BAD DATABASE NAME");
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(EventContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(EventContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    /** Removes appropriate rows from the given database, returns the number of rows affected. */
    public int deleteFromDatabase(DatabaseHelper dbHelper, String tableName, String whereClause, String[] whereArgs) {
        // TODO: sql insecure
        // Delete info from the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(tableName, whereClause, whereArgs);
        return rowsDeleted;
    }
}
