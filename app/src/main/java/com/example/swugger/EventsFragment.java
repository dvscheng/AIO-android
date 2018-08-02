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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;

import com.example.swugger.db.EventContract;
import com.example.swugger.db.EventDbHelper;

import java.util.ArrayList;

public class EventsFragment extends Fragment implements Events_AddDialogFragment.AddEventsDialogListener {

    private CalendarView mCalendarView;
    private FloatingActionButton mFab;
    private EventsFragment mTargetFragment;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private EventDbHelper mDbHelper;
    private ArrayList<Event> mEventList;

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    public EventsFragment() {
    }

    // For dialog communication
    @Override
    public void onPositiveClick(Event event) {
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

        // FIXME
        // Add edgecase for adding the same task name/note
        // Do something with this long later
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

    @Override
    public void onNegativeClick() {
        // Do nothing
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

        // Specify and set an adapter
        mRecyclerViewAdapter = new Events_RecyclerViewAdapter(getContext(), mEventList, this, getFragmentManager()); // was rootView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Set LayoutManager
        mRecyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        // Re-populate the RecyclerView
        refreshRecyclerView();

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
                refreshRecyclerView(month, dayOfMonth, year);   // months from [0-11] already
            }
        });

        return rootView;
    }

    /* When app is first opened, populates the RecyclerView according to what day it is. */
    private void refreshRecyclerView() {
        // TODO: should remove everything in the list because of reference
        mEventList.clear();

        // convert from epoch to readable time
        String date = Event.convertEpochToDate(mCalendarView.getDate());
        String currentMonth = Integer.toString(Integer.parseInt(date.substring(0, 2)) - 1);  // months are between [0-11] so need to decrement the month # by 1
        String currentDay = date.substring(3, 5);
        String currentYear = date.substring(6, 10);

        retrieveAndPopulate(currentMonth, currentDay, currentYear);

        mRecyclerViewAdapter.notifyDataSetChanged();
    }
    /* Updates the RecyclerView list (removes all elements and then re-populates) according to the selected date. */
    private void refreshRecyclerView(int month, int day, int year) {
        // TODO: should remove everything in the list because of reference
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
}