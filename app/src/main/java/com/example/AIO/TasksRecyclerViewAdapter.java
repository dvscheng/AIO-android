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
        public CheckBox mCheckBox;
        public Button mDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.text_name_task_item);
            mNotesTextView = itemView.findViewById(R.id.text_notes_task_item);
            mCheckBox = itemView.findViewById(R.id.checkBox_task_item);
            mDeleteButton = itemView.findViewById(R.id.delete_task_button);
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

        // Set item views based on your views and data model
        TextView nameTextView = viewHolder.mNameTextView;
        nameTextView.setText(task.getName());
        TextView notesTextView = viewHolder.mNotesTextView;
        notesTextView.setText(task.getNotes());
        viewHolder.mCheckBox.setChecked(task.isChecked());

        final TasksRecyclerViewAdapter thisAdapter = this;

        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setChecked(isChecked);
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
                // remove task from recyclerview list and notify adapter
                mTaskList.remove(task);
                thisAdapter.notifyDataSetChanged();

                // TODO: create the task by task id
                // remove the task from the db
                TaskDbHelper dbHelper = TaskDbHelper.getInstance(mContext);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                db.delete(TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry.COL_TASK_NAME + "= '" + task.getName()
                                + "' and "
                                + TaskContract.TaskEntry.COL_TASK_NOTES + "= '" + task.getNotes() + "'",
                        null);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}
