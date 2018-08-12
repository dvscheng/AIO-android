package com.example.swugger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.swugger.db.EventContract;
import com.example.swugger.db.EventDbHelper;

import java.util.ArrayList;

public class EventsFragment extends Fragment implements Events_AddDialogFragment.AddEventsDialogListener, Events_EditEventDialogFragment.EditEventsDialogListener {

    private CalendarView mCalendarView;
    private FloatingActionButton mFab;
    private EventsFragment mTargetFragment;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private EventDbHelper mDbHelper;
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
    public void onPositiveClickAdd(Event event) {
        // FIXME
        // SQL insecure
        // Add the info to the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.EventEntry.COL_EVENT_NAME, event.getName());
        contentValues.put(EventContract.EventEntry.COL_EVENT_NOTES, event.getNotes());
        contentValues.put(EventContract.EventEntry.COL_EVENT_MONTH, event.getMonth());
        contentValues.put(EventContract.EventEntry.COL_EVENT_DAY, event.getDay());
        contentValues.put(EventContract.EventEntry.COL_EVENT_YEAR, event.getYear());
        contentValues.put(EventContract.EventEntry.COL_EVENT_HOUR, event.getHour());
        contentValues.put(EventContract.EventEntry.COL_EVENT_MINUTE, event.getMinute());

        // TODO: this long should be a unique ID for each event, consider updating the same db entry right after this insert
        // Add edgecase for adding the same task name/note
        long newRowId = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);

        // Add the item to the list and notify of a change iff it matches the currently selected date
        String date = Event.convertEpochToDate(mCalendarView.getDate());
        int currentMonth = Integer.parseInt(date.substring(0, 2)) - 1;  // months are between [0-11]
        int currentDay = Integer.parseInt(date.substring(3, 5));    // could also use delimiter here
        int currentYear = Integer.parseInt(date.substring(6, 10));
        // TODO: eventually need to refresh entire RecyclerView because needs to be listed by hour
        if (event.getMonth() == currentMonth
                && event.getDay() == currentDay
                && event.getYear() == currentYear) {
            mEventList.add(event);
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
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
                                    int newMonth, int newDay, int newYear, int newHour, int newMinute) {
        // TODO: get the new event info from the dialog instance and update the event and refresh recyclerview
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (hasEdits) {
            // TODO: query for Event ID, which can the obj put through a hashing function or incrementally during construction
            String whereClause = EventContract.EventEntry.COL_EVENT_NAME + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_NOTES + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_MONTH + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_DAY + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_YEAR + " =? AND " +
                    EventContract.EventEntry.COL_EVENT_HOUR+ " =? AND " +
                    EventContract.EventEntry.COL_EVENT_MINUTE + " =?";
            String whereArgs[] = new String[]{origEvent.getName(), origEvent.getNotes(),
                    Integer.toString(origEvent.getMonth()), Integer.toString(origEvent.getDay()), Integer.toString(origEvent.getYear()),
                    Integer.toString(origEvent.getHour()), Integer.toString(origEvent.getMinute())};

            // insert the new values, always update name and notes, further checking for date and time
            ContentValues values = new ContentValues();
            values.put(EventContract.EventEntry.COL_EVENT_NAME, newName);
            values.put(EventContract.EventEntry.COL_EVENT_NOTES, newNotes);
            if (dateChanged) {
                // if somehow these values are -1, do not update, something is wrong
                if (newMonth == -1 || newDay == -1 || newYear == -1) {
                    throw new IllegalArgumentException("attempted to update the date without valid params, check EventsFragment.java");
                }
                values.put(EventContract.EventEntry.COL_EVENT_MONTH, Integer.toString(newMonth));
                values.put(EventContract.EventEntry.COL_EVENT_DAY, Integer.toString(newDay));
                values.put(EventContract.EventEntry.COL_EVENT_YEAR, Integer.toString(newYear));
            }
            if (timeChanged) {
                // if somehow these values are -1, do not update, something is wrong
                if (newHour == -1 || newMinute == -1) {
                    throw new IllegalArgumentException("attempted to update the time without valid params (they're -1), check EventsFragment.java");
                }
                values.put(EventContract.EventEntry.COL_EVENT_HOUR, Integer.toString(newHour));
                values.put(EventContract.EventEntry.COL_EVENT_MINUTE, Integer.toString(newMinute));
            }

            // update the SINGLE row (should not have multiple events with same params) and check that only 1 row was updated
            int rowsUpdated = db.update(EventContract.EventEntry.TABLE_NAME, values, whereClause, whereArgs);
            if (rowsUpdated != 1) {
                throw new SecurityException("we somehow updated not 1 row, but.. " + rowsUpdated + " to be exact. \n"
                        + "name: " + origEvent.getName() + "\n"
                        + "notes: " + origEvent.getNotes() + "\n"
                        + "month: " + Integer.toString(origEvent.getMonth()) + "\n"
                        + "day: " + Integer.toString(origEvent.getDay()) + "\n"
                        + "year: " + Integer.toString(origEvent.getYear()) + "\n"
                        + "hour: " + Integer.toString(origEvent.getHour()) + "\n"
                        + "minute: " + Integer.toString(origEvent.getMinute()) + "\n");
            }

            // TODO: if time and/or date has changed and updating is successful AND there are existing reminders,
            // TODO: grab the PendingIntents from SharedPrefs using gson and edit them

            // the old event should disappear (if appropriate) and RecyclerView should be re-sorted and updated
            refreshRecyclerView(currentMonth, currentDay, currentYear);
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

        mCalendarView = (CalendarView) rootView.findViewById(R.id.events_view);
        mTargetFragment = this;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.events_recycler_view);
        mDbHelper = new EventDbHelper(getContext());
        mEventList = new ArrayList<>();

        // convert from epoch to readable time
        // CalenderView.getDate() should returns real life current date NOT selected date
        String date = Event.convertEpochToDate(mCalendarView.getDate());
        // we parseInt and then toString to get rid of leading 0's. i.e. "07" -> "7"
        currentMonth = Integer.parseInt(date.substring(0, 2)) - 1;  // months in db are saved as between [0-11] so need to decrement the month # by 1
        currentDay =  Integer.parseInt(date.substring(3, 5));
        currentYear = Integer.parseInt(date.substring(6, 10));

        // Specify and set an adapter
        mRecyclerViewAdapter = new Events_RecyclerViewAdapter(getContext(), mEventList, this, getFragmentManager()); // was rootView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Set LayoutManager
        mRecyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        // Re-populate the RecyclerView
        refreshRecyclerView(currentMonth, currentDay, currentYear);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.events_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Animation fabRotate = AnimationUtils.loadAnimation(
                        getActivity().getApplication(), R.anim.fab_rotation);
                mFab.startAnimation(fabRotate);*/

                Events_AddDialogFragment eventDialog = new Events_AddDialogFragment(mCalendarView.getDate());

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
                refreshRecyclerView(month, dayOfMonth, year);   // months from [0-11] already
            }
        });

        return rootView;
    }

    /* Updates the RecyclerView list (removes all elements and then re-populates) according to the selected date. */
    private void refreshRecyclerView(int month, int day, int year) {
        // should remove everything in the list because of reference
        mEventList.clear();

        // convert from epoch to readable time
        String currentMonth = Integer.toString(month);  // month bounds ([0-11]) is already handled
        String currentDay = Integer.toString(day);
        String currentYear = Integer.toString(year);

        retrieveAndPopulate(currentMonth, currentDay, currentYear);

        mRecyclerViewAdapter.notifyDataSetChanged();
    }
    /* Retrieve Events from the database and then populate the RecyclerView. */
    private void retrieveAndPopulate(String currentMonth, String currentDay, String currentYear) {
        // Read information from SQLDatabase
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // db.execSQL("DROP TABLE IF EXISTS events");
        // mDbHelper.onCreate(db);
        String[] projection = {
                EventContract.EventEntry._ID,
                //TaskContract.TaskEntry.COL_TASK_ID,       // don't need the id because the id will be gen'd depending on name & notes (look at Task.java)
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
        String[] whereArgs = new String[]{currentMonth, currentDay, currentYear};
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
        int colName = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_NAME);
        int colNotes = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_NOTES);
        int colMonth = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_MONTH);
        int colDay = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_DAY);
        int colYear = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_YEAR);
        int colHour = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_HOUR);
        int colMinute = cursor.getColumnIndex(EventContract.EventEntry.COL_EVENT_MINUTE);
        try {
            while (cursor.moveToNext()) {
                mEventList.add(new Event(cursor.getString(colName), cursor.getString(colNotes),
                        cursor.getInt(colMonth), cursor.getInt(colDay), cursor.getInt(colYear),
                        cursor.getInt(colHour), cursor.getInt(colMinute)));
            }
        } finally {
            cursor.close();
        }
    }
    /** Used for Debugging, print all rows of the given database. */
    // TODO: make it a static method of an appropriate class
    public void printDatabase() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String tableString = String.format("Table %s:\n", EventContract.EventEntry.TABLE_NAME);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + EventContract.EventEntry.TABLE_NAME, null);
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
}