package com.example.swugger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.swugger.db.EventContract;
import com.example.swugger.db.EventDbHelper;
import com.example.swugger.db.ReminderContract;
import com.example.swugger.db.ReminderDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EventsFragment extends Fragment implements AddEventDialogFragment.AddEventDialogListener, EditEventDialogFragment.EditEventsDialogListener {

    private Context mContext;
    private CalendarView mCalendarView;
    private FloatingActionButton mFab;
    private EventsFragment mTargetFragment;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private EventDbHelper eventDbHelper;
    private ReminderDbHelper reminderDbHelper;
    private ArrayList<Event> mEventList;
    /** These variables keep track of the selected date. */
    private int currentMonth;
    private int currentDay;
    private int currentYear;

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    public EventsFragment() {
    }

    /* Positive click for the fab. */
    @Override
    public void onPositiveClickAdd(String name, String notes, int month, int day, int year, int hour, int minute) {
        // Add the info to the database
        // It's okay to convert int values to String because upon retrieval (through cursor.getInt()) they will be ints again
        String[] colNames = {
                EventContract.EventEntry.COL_EVENT_NAME,
                EventContract.EventEntry.COL_EVENT_NOTES,
                EventContract.EventEntry.COL_EVENT_MONTH,
                EventContract.EventEntry.COL_EVENT_DAY,
                EventContract.EventEntry.COL_EVENT_YEAR,
                EventContract.EventEntry.COL_EVENT_HOUR,
                EventContract.EventEntry.COL_EVENT_MINUTE };
        String[] values = {
                name,
                notes,
                Integer.toString(month),
                Integer.toString(day),
                Integer.toString(year),
                Integer.toString(hour),
                Integer.toString(minute) };

        // This newRowId should be BaseColumns._ID, which we use as the unique id for each event
        // also, using this id makes duplicate events distinguishable
        long newRowId = addToDatabase(eventDbHelper, EventContract.EventEntry.TABLE_NAME, colNames, values);

        // Refresh the current RecyclerView to reflect possible changes
        refreshEventRecyclerView(currentMonth, currentDay, currentYear);
    }
    /* Negative click for the fab. */
    @Override
    public void onNegativeClickAdd() {
        // Do nothing
    }
    /* Positive click for edit event dialog. */
    @Override
    public void onPositiveClickEdit(Event origEvent, boolean hasEdits, boolean dateChanged, boolean timeChanged,
                                    String newName, String newNotes,
                                    int newMonth, int newDay, int newYear, int newHour, int newMinute,
                                    ArrayList<Reminder> displayedReminderList, ArrayList<ReminderWithId> remindersToDeleteList) {
        // TODO: get the new event info from the dialog instance and update the event and refresh recyclerview
        SQLiteDatabase db = eventDbHelper.getReadableDatabase();
        if (hasEdits) {
            /*
             *  HANDLING EVENT DETAILS
             */
            // find the ONE event with the corresponding id
            String whereClauseEvent = EventContract.EventEntry._ID + " =?";
            String WhereArgsEvent[] = { Long.toString(origEvent.getId()) };
            /*String whereClause = EventContract.EventEntry.COL_EVENT_NAME + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_NOTES + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_MONTH + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_DAY + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_YEAR + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_HOUR+ " =? AND " +
                    EventContract.EventEntry.COL_EVENT_MINUTE + " =?";
            String whereArgs[] = new String[]{origEvent.getName(), origEvent.getNotes(),
                    Integer.toString(origEvent.getMonth()), Integer.toString(origEvent.getDay()), Integer.toString(origEvent.getYear()),
                    Integer.toString(origEvent.getHour()), Integer.toString(origEvent.getMinute())};*/

            // insert the new values, always update name and notes, further checking for date and time
            ContentValues contentValues = new ContentValues();
            contentValues.put(EventContract.EventEntry.COL_EVENT_NAME, newName);
            contentValues.put(EventContract.EventEntry.COL_EVENT_NOTES, newNotes);
            if (dateChanged) {
                // if somehow these values are -1, do not update, something is wrong
                if (newMonth == -1 || newDay == -1 || newYear == -1) {
                    throw new IllegalArgumentException("attempted to update the date without valid params, check EventsFragment.onPositiveClickEdit");
                }
                contentValues.put(EventContract.EventEntry.COL_EVENT_MONTH, Integer.toString(newMonth));
                contentValues.put(EventContract.EventEntry.COL_EVENT_DAY, Integer.toString(newDay));
                contentValues.put(EventContract.EventEntry.COL_EVENT_YEAR, Integer.toString(newYear));
            }
            if (timeChanged) {
                // if somehow these values are -1, do not update, something is wrong
                if (newHour == -1 || newMinute == -1) {
                    throw new IllegalArgumentException("attempted to update the time without valid params (they're -1), check EventsFragment.onPositiveClickEdit");
                }
                contentValues.put(EventContract.EventEntry.COL_EVENT_HOUR, Integer.toString(newHour));
                contentValues.put(EventContract.EventEntry.COL_EVENT_MINUTE, Integer.toString(newMinute));
            }

            /*
             *  ADDING TO DATABASE
             */
            // update the SINGLE row (should not have multiple or no events with these params) and check that only 1 row was updated
            int rowsUpdatedEvent = db.update(EventContract.EventEntry.TABLE_NAME, contentValues, whereClauseEvent, WhereArgsEvent);
            if (rowsUpdatedEvent != 1) {
                throw new SecurityException("we somehow updated not 1 event, but.. " + rowsUpdatedEvent + " to be exact. \n" + origEvent.toString());
            }

            /*
             *  HANDLING REMINDERS
             */
            for (ReminderWithId savedReminder : remindersToDeleteList) {
                String whereClauseReminder = ReminderContract.ReminderEntry._ID + " =?";
                String[] whereArgsReminder = { Long.toString(savedReminder.getId()) };
                int rowsDeletedReminder = deleteFromDatabase(reminderDbHelper, ReminderContract.ReminderEntry.TABLE_NAME, whereClauseReminder, whereArgsReminder);
                if (rowsDeletedReminder != 1) {
                    throw new SecurityException("we somehow deleted not 1 reminder, but.. " + rowsDeletedReminder + " to be exact. \n"
                            + "reminder info: " + savedReminder.toString() + "\n"
                            + "event info: " + origEvent.toString());
                }

                setOrCancelAlarm(origEvent, savedReminder, false);
            }

            // add the reminders (this is PURELY to save to the backend, as reminders are no longer in view of the user)
            for (Reminder reminder : displayedReminderList) {
                // If the reminder has not already been saved and
                // if the original event already has reminders set at this new reminder's time, continue to the next newReminder
                if (!reminder.isSavedReminder() && !origEvent.isDuplicateReminder(reminder)) {
                    // If there isn't already a reminder with the new reminder's time, add it to the event and database
                    String[] colNames = {
                            ReminderContract.ReminderEntry.COL_REMINDER_EVENT_ID,
                            ReminderContract.ReminderEntry.COL_REMINDER_MILLISECONDS,
                            ReminderContract.ReminderEntry.COL_REMINDER_DAYS_BEFORE,
                            ReminderContract.ReminderEntry.COL_REMINDER_HOURS_BEFORE,
                            ReminderContract.ReminderEntry.COL_REMINDER_MINUTES_BEFORE
                    };
                    String[] values = {
                            Long.toString(reminder.getEventId()),
                            Long.toString(reminder.getTimeInMilliseconds()),
                            Integer.toString(reminder.getDaysBefore()),
                            Integer.toString(reminder.getHoursBefore()),
                            Integer.toString(reminder.getMinutesBefore())
                    };
                    long newRowIdReminder = addToDatabase(reminderDbHelper, ReminderContract.ReminderEntry.TABLE_NAME, colNames, values);

                    // Create a ReminderWithId object with the newly saved reminder in order to set an alarm.
                    ReminderWithId savedReminder = new ReminderWithId(newRowIdReminder, reminder.getEventId(), reminder.getTimeInMilliseconds(),
                                                                        reminder.getDaysBefore(), reminder.getHoursBefore(), reminder.getMinutesBefore());
                    setOrCancelAlarm(origEvent, savedReminder, true);
                }
            }
            refreshReminders(origEvent);        // TODO: this could be more efficient by updating locally rather than retrieving all reminders again

            // the old event should disappear (if appropriate) and RecyclerView should be re-sorted and updated
            refreshEventRecyclerView(currentMonth, currentDay, currentYear);
        }
    }
    /* Negative click for edit event dialog. */
    @Override
    public void onNegativeClickEdit() {
        // Don't do anything
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_events, container, false);

        mContext = getContext();
        mCalendarView = (CalendarView) rootView.findViewById(R.id.calendarView_events_fragment);
        mTargetFragment = this;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_events_fragment);
        eventDbHelper = new EventDbHelper(mContext);
        reminderDbHelper = new ReminderDbHelper(mContext);
        mEventList = new ArrayList<>();

        // convert from epoch to readable time
        // CalenderView.getDate() should returns real life current date NOT selected date
        String date = Event.convertEpochToReadableDate(mCalendarView.getDate());
        // we parseInt and then toString to get rid of leading 0's. i.e. "07" -> "7"
        currentMonth = Integer.parseInt(date.substring(0, 2)) - 1;  // months in db are saved as between [0-11] so need to decrement the month # by 1
        currentDay =  Integer.parseInt(date.substring(3, 5));
        currentYear = Integer.parseInt(date.substring(6, 10));

        // Specify and set an adapter
        mRecyclerViewAdapter = new EventsRecyclerViewAdapter(mContext, mEventList, this, getFragmentManager()); // was rootView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Set LayoutManager
        mRecyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        // Re-populate the RecyclerView
        refreshEventRecyclerView(currentMonth, currentDay, currentYear);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab_events_fragment);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Animation fabRotate = AnimationUtils.loadAnimation(
                        getActivity().getApplication(), R.anim.fab_rotation);
                mFab.startAnimation(fabRotate);*/

                AddEventDialogFragment eventDialog = new AddEventDialogFragment(mCalendarView.getDate());

                eventDialog.setTargetFragment(mTargetFragment, 0);
                //eventDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogFragmentTheme);       // makes the dialog fullscreen
                eventDialog.show(getActivity().getSupportFragmentManager(), "new event");
                // The device is smaller, so show the fragment fullscreen
                /*FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // For a little polish, specify a transition animation
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                // To make it fullscreen, use the 'content' root view as the container
                // for the fragment, which is always the root view for the activity
                transaction.add(android.R.id.content, eventDialog)
                        .addToBackStack(null).commit();*/
            }
        });

        // When the date is changed, refresh the RecyclerView
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // when user selects new date, save the newly selected date as current date
                currentMonth = month;
                currentDay = dayOfMonth;
                currentYear = year;
                refreshEventRecyclerView(month, dayOfMonth, year);   // months from [0-11] already
            }
        });

        return rootView;
    }

    /** Updates the RecyclerView list (removes all elements and then re-populates) according to the selected date. */
    private void refreshEventRecyclerView(int month, int day, int year) {
        // should remove everything in the attached adapter's list because of reference
        mEventList.clear();

        // convert from epoch to readable time
        String currentMonth = Integer.toString(month);  // month bounds ([0-11]) is already handled
        String currentDay = Integer.toString(day);
        String currentYear = Integer.toString(year);

        retrieveAndPopulateEvents(currentMonth, currentDay, currentYear);

        mRecyclerViewAdapter.notifyDataSetChanged();
    }
    /** Retrieve Events from the database and then populate the ArrayList for the RecyclerView. */
    private void retrieveAndPopulateEvents(String currentMonth, String currentDay, String currentYear) {
        // Read information from SQLDatabase
        SQLiteDatabase db = eventDbHelper.getReadableDatabase();
        // db.execSQL("DROP TABLE IF EXISTS even");
        // eventDbHelper.onCreate(db);
        String[] projection = {
                EventContract.EventEntry._ID,
                EventContract.EventEntry.COL_EVENT_NAME,
                EventContract.EventEntry.COL_EVENT_NOTES,
                EventContract.EventEntry.COL_EVENT_MONTH,       // [0-11]
                EventContract.EventEntry.COL_EVENT_DAY,
                EventContract.EventEntry.COL_EVENT_YEAR,
                EventContract.EventEntry.COL_EVENT_HOUR,        // [0-23]
                EventContract.EventEntry.COL_EVENT_MINUTE       // [0-59]
        };
        String whereClause = EventContract.EventEntry.COL_EVENT_MONTH + " =? AND " +
                EventContract.EventEntry.COL_EVENT_DAY + " =? AND " +
                EventContract.EventEntry.COL_EVENT_YEAR + " =?";
        String[] whereArgs = { currentMonth, currentDay, currentYear };
        String orderByClause = EventContract.EventEntry.COL_EVENT_HOUR + " ASC";
        // Get the information as a Cursor by calling db.query()
        Cursor cursor = db.query(
                EventContract.EventEntry.TABLE_NAME,                      // The table to query
                projection,                               // The columns to return
                whereClause,                                     // The columns for the WHERE clause
                whereArgs,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                orderByClause                                // The sort order
        );
        // Get info from cursor and add it as an event to mTaskList
        int colId = cursor.getColumnIndex(EventContract.EventEntry._ID);
        int colName = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_NAME);
        int colNotes = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_NOTES);
        int colMonth = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_MONTH);
        int colDay = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_DAY);
        int colYear = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_YEAR);
        int colHour = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_HOUR);
        int colMinute = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_MINUTE);
        try {
            while (cursor.moveToNext()) {
                Event newEvent = new Event(cursor.getLong(colId), cursor.getString(colName), cursor.getString(colNotes),
                        cursor.getInt(colMonth), cursor.getInt(colDay), cursor.getInt(colYear),
                        cursor.getInt(colHour), cursor.getInt(colMinute));

                // get reminders based on event id and add them to event's arraylist
                retrieveAndPopulateReminders(newEvent);

                // add the event to the eventlist for adapter use
                mEventList.add(newEvent);
            }
        } finally {
            cursor.close();
        }
    }
    /** Refresh the remindersList of the given event. */
    private void refreshReminders(Event event) {
        event.clearReminders();

        retrieveAndPopulateReminders(event);
    }
    /** SHOULD ONLY BE CALLED BY refreshReminders.
     *  Retrieve Reminders from the database and then populate the provided event's RemindersList */
    private void retrieveAndPopulateReminders(Event event) {
        SQLiteDatabase db = reminderDbHelper.getReadableDatabase();

        String[] projection = {
                ReminderContract.ReminderEntry._ID,
                ReminderContract.ReminderEntry.COL_REMINDER_EVENT_ID,
                ReminderContract.ReminderEntry.COL_REMINDER_MILLISECONDS,
                ReminderContract.ReminderEntry.COL_REMINDER_DAYS_BEFORE,
                ReminderContract.ReminderEntry.COL_REMINDER_HOURS_BEFORE,
                ReminderContract.ReminderEntry.COL_REMINDER_MINUTES_BEFORE
        };
        String whereClause = ReminderContract.ReminderEntry.COL_REMINDER_EVENT_ID + " =?";
        String[] whereArgs = { Long.toString(event.getId()) };
        String orderByClause = ReminderContract.ReminderEntry.COL_REMINDER_MILLISECONDS + " ASC";
        Cursor cursor = db.query(
                ReminderContract.ReminderEntry.TABLE_NAME,                      // The table to query
                projection,                               // The columns to return
                whereClause,                                     // The columns for the WHERE clause
                whereArgs,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                orderByClause                                // The sort order
        );
        // Get info from cursor and add it as an event to mTaskList
        int colId = cursor.getColumnIndex(ReminderContract.ReminderEntry._ID);
        int colEventId = cursor.getColumnIndex(ReminderContract.ReminderEntry.COL_REMINDER_EVENT_ID);
        int colMilliseconds = cursor.getColumnIndex(ReminderContract.ReminderEntry.COL_REMINDER_MILLISECONDS);
        int colDaysBefore = cursor.getColumnIndex(ReminderContract.ReminderEntry.COL_REMINDER_DAYS_BEFORE);
        int colHoursBefore = cursor.getColumnIndex(ReminderContract.ReminderEntry.COL_REMINDER_HOURS_BEFORE);
        int colMinutesBefore = cursor.getColumnIndex(ReminderContract.ReminderEntry.COL_REMINDER_MINUTES_BEFORE);
        try {
            while (cursor.moveToNext()) {
                event.addReminder(new ReminderWithId(cursor.getLong(colId), cursor.getLong(colEventId), cursor.getLong(colMilliseconds),
                                cursor.getInt(colDaysBefore), cursor.getInt(colHoursBefore), cursor.getInt(colMinutesBefore)));
            }
        } finally {
            cursor.close();
        }
    }
    /** Adds to given db. Make sure colNames Strings come from a _Contract class. */
    private long addToDatabase(SQLiteOpenHelper dbHelper, String tableName, String[] colNames, String[] values) {
        // TODO: sql insecure
        // if somehow the number of values are not the same for both arrays, something is wrong
        if (colNames.length != values.length) {
            throw new ArrayIndexOutOfBoundsException("provided number of columns vs provided number of values is not equal, check EventsFragment.addToDatabase");
        }

        // Add info to the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < colNames.length; i++) {
            contentValues.put(colNames[i], values[i]);
        }

        long newRowId = db.insert(tableName, null, contentValues);
        return newRowId;
    }
    /** Removes appropriate rows from the given database, returns the number of rows affected. */
    private int deleteFromDatabase(SQLiteOpenHelper dbHelper, String tableName, String whereClause, String[] whereArgs) {
        // TODO: sql insecure
        // Delete info from the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(tableName, whereClause, whereArgs);
        return rowsDeleted;
    }
    /** Used for Debugging, print all rows of the given database. */
    // TODO: make it a static method of an appropriate class
    public void printDatabase(String tableName) {
        // TODO: inefficient check of table name
        SQLiteDatabase db = eventDbHelper.getReadableDatabase();
        if (tableName.equals(ReminderContract.ReminderEntry.TABLE_NAME)) {
            db = reminderDbHelper.getReadableDatabase();
        }

        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s ", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        allRows.close();
        System.out.println(tableString);
    }
    /** Create a Notification object, which contains content given by the given event and reminder. */
    private Notification createNotification(Event event, ReminderWithId reminder) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        // intent.setFlags()  TODO: do this later
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NotificationPublisher.EVENT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_today_black_24dp)
                .setContentTitle(event.getName())
                .setContentText("Your event is in " + reminder.toString())      // TODO: consider reformatting string to be more readable
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(event.getNotes()))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
    /** Always use this to get a PendingIntent that corresponds with a reminder in order to make consistent the creation of PendingIntents for alarm cancellation.
     * Should ALWAYS return the same PendingIntent when given event and reminder. */
    private PendingIntent getPendingIntent(Event event, ReminderWithId reminder) {
        Intent intent = new Intent(mContext, NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.EVENT_NOTIFICATION_ID, (int) reminder.getId());
        intent.putExtra(NotificationPublisher.EVENT_NOTIFICATION, createNotification(event, reminder));

        return PendingIntent.getBroadcast(mContext, (int) reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);     // reminder.getId() will (probably) never pass 2.1b because you'd need 2.1b reminders
    }
    /** Sets or cancels the alarm for the given reminder. */
    private void setOrCancelAlarm(Event event, ReminderWithId reminder, boolean set) {
        // make sure the reminder is not before the current date
        long currentTimeInMilliseconds = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).getTimeInMillis();
        if (reminder.getTimeInMilliseconds() <= currentTimeInMilliseconds) {
            return;
        }

        PendingIntent pendingIntent = getPendingIntent(event, reminder);        // This PendingIntent should always be the same given the same event and reminder
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        // TODO: handle NPE
        if (set) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getTimeInMilliseconds(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }
}