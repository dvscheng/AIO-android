package com.example.swugger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.swugger.db.TaskContract;
import com.example.swugger.db.TaskDbHelper;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        // For now, name only
        public TextView mNameTextView;
        public TextView mNotesTextView;
        public View mThisCheckbox;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.task_name);
            mNotesTextView = (TextView) itemView.findViewById(R.id.task_notes);
            mThisCheckbox = itemView.findViewById(R.id.check_box);
        }
    }

    private List<Task> mTaskList;

    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(Context context, List<Task> tasks) {
        mContext = context;
        mTaskList = tasks;
    }

    private Context getContext() {
        return mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Task task = mTaskList.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = viewHolder.mNameTextView;
        nameTextView.setText(task.getName());
        TextView notesTextView = viewHolder.mNotesTextView;
        notesTextView.setText(task.getNotes());

        final RecyclerViewAdapter thisAdapter = this;

        ((CheckBox) viewHolder.mThisCheckbox)
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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

                    /* Notify the adapter of change and remove the task object from the list.  Thang creds*/
                    for (int i = 0; i < mTaskList.size(); i++) {
                        if (mTaskList.get(i).getName().equals(name)
                                && mTaskList.get(i).getNotes().equals(notes)) {
                            mTaskList.remove(i);
                        }
                    }
                    thisAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}
