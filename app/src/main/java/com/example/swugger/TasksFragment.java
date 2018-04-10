package com.example.swugger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.example.swugger.db.TaskContract;
import com.example.swugger.db.TaskDbHelper;

import java.util.ArrayList;


public class TasksFragment extends Fragment implements AddTasksDialogFragment.AddTasksDialogListener {

    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private TasksFragment mTargetFragment;
    private TaskDbHelper mDbHelper;

    // For dialog communication
    @Override
    public void onPositiveClick(Task task) {
        // Get the position of the newly added item
        int pos = mRecyclerViewAdapter.getItemCount();

        // Add the item to the list
        mTaskList.add(task);

        // FIXME
        // SQL insecure
        // Add the info to the database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COL_TASK_NAME, task.getName());
        contentValues.put(TaskContract.TaskEntry.COL_TASK_NOTES, task.getNotes());

        // FIXME
        // Add edgecase for adding the same task name/note
        // Do something with this long later
        long newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, contentValues);

        // Notify the Adapter of the change, where pos is the position the task
        // was added to and the int after that is how many tasks added (defaulted to 1
        // mRecyclerViewAdapter.notifyItemRangeInserted(pos, 1);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNegativeClick() {
        // Do nothing
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    public TasksFragment() {
    }

    private ArrayList<Task> mTaskList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tasks, container, false);

        /* RecyclerView stuff */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tasks_recycler_view);

        // Initialize the mDbHelper and mTaskList
        mDbHelper = new TaskDbHelper(getContext());
        mTaskList = new ArrayList<>();

        // Read information from SQLDatabase
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TaskContract.TaskEntry._ID,
                //TaskContract.TaskEntry.COL_TASK_ID,       // don't need the id because the id will be gen'd depending on name & notes (look at Task.java)
                TaskContract.TaskEntry.COL_TASK_NAME,
                TaskContract.TaskEntry.COL_TASK_NOTES
        };
        // Get the information as a Cursor by calling db.query()
        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,        // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        // Get info from cursor and add it as a task to mTaskList
        int colName = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_NAME);
        int colNotes = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_NOTES);
        try {
            while (cursor.moveToNext()) {
                mTaskList.add(new Task(cursor.getString(colName), cursor.getString(colNotes)));
            }
        } finally {
            cursor.close();
        }

        // Specify and set an adapter
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(), mTaskList); // was rootView
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Set LayoutManager
        mRecyclerViewLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        mTargetFragment = this;

        //Creates a FAB
        mFab = (FloatingActionButton) rootView.findViewById(R.id.Fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fabRotate = AnimationUtils.loadAnimation(
                        getActivity().getApplication(), R.anim.fab_rotation);
                mFab.startAnimation(fabRotate);

                AddTasksDialogFragment tasksDialog = new AddTasksDialogFragment();

                tasksDialog.setTargetFragment(mTargetFragment, 0);
                tasksDialog.show(getActivity().getSupportFragmentManager(), "new task");
            }
        });

        return rootView;
    }
}