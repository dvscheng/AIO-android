package com.example.AIO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.AIO.db.TaskContract;
import com.example.AIO.db.TaskDbHelper;

import java.util.List;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        // For now, name only
        public TextView mNameTextView;
        public TextView mNotesTextView;
        public CheckBox mThisCheckbox;
        public Button mDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.text_name_task_item);
            mNotesTextView = (TextView) itemView.findViewById(R.id.text_notes_task_item);
            mThisCheckbox = (CheckBox) itemView.findViewById(R.id.checkBox_task_item);
            mDeleteButton = (Button) itemView.findViewById(R.id.delete_task_button);
        }
    }

    private List<Task> mTaskList;

    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TasksRecyclerViewAdapter(Context context, List<Task> tasks) {
        mContext = context;
        mTaskList = tasks;
    }

    private Context getContext() {
        return mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TasksRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.item_task, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final TasksRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Task task = mTaskList.get(position);
        task.setCheckBoxView(viewHolder.mThisCheckbox);             // set the checkbox ref in the obj

        // Set item views based on your views and data model
        TextView nameTextView = viewHolder.mNameTextView;
        nameTextView.setText(task.getName());
        TextView notesTextView = viewHolder.mNotesTextView;
        notesTextView.setText(task.getNotes());

        final TasksRecyclerViewAdapter thisAdapter = this;

        viewHolder.mThisCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // strike-through the text doesn't work with app-widgets, check remoteview for that
                    viewHolder.mNameTextView.setPaintFlags(viewHolder.mNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.mNotesTextView.setPaintFlags(viewHolder.mNotesTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    viewHolder.mNameTextView.setPaintFlags(viewHolder.mNameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.mNotesTextView.setPaintFlags(viewHolder.mNotesTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });

        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*
                // deletes the task
                TaskDbHelper dbHelper = new TaskDbHelper(mContext);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String name = viewHolder.mNameTextView.getText().toString();
                String notes = viewHolder.mNotesTextView.getText().toString();
                // SQL matches the col_name and col_notes to delete the correct task.

                db.delete(TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry.COL_TASK_NAME + "= '" + name
                                + "' and "
                                + TaskContract.TaskEntry.COL_TASK_NOTES + "= '" + notes + "'",
                        null);

                //* Notify the adapter of change and remove the task object from the list.  Thang creds*//*
                // inefficient, but the number of tasks is low (presumably)
                for (int i = 0; i < mTaskList.size(); i++) {
                    if (mTaskList.get(i).getName().equals(name)
                            && mTaskList.get(i).getNotes().equals(notes)) {
                        mTaskList.remove(i);
                    }
                }
                */
                // uncheck the checkbox
                task.getCheckBoxView().setChecked(false);

                // TODO: create the task by task id
                // remove the task from the db
                TaskDbHelper dbHelper = TaskDbHelper.getInstance(mContext);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                db.delete(TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry.COL_TASK_NAME + "= '" + task.getName()
                                + "' and "
                                + TaskContract.TaskEntry.COL_TASK_NOTES + "= '" + task.getNotes() + "'",
                        null);

                // remove task from recyclerview list and notify adapter
                mTaskList.remove(task);
                thisAdapter.notifyDataSetChanged();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}
