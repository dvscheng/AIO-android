package com.example.swugger;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Events_RecyclerViewAdapter extends RecyclerView.Adapter<Events_RecyclerViewAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public TextView mTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.event_name);
            mTimeTextView = (TextView) itemView.findViewById(R.id.event_time);
        }
    }

    private List<Event> mEventList;
    private Context mContext;
    private Fragment mEventsFragment;
    private FragmentManager mFragMan;

    // Provide a suitable constructor (depends on the kind of dataset)
    public Events_RecyclerViewAdapter(Context context, List<Event> events, Fragment eventsFragment,
                                      FragmentManager fragMan) {
        mContext = context;
        mEventList = events;
        mEventsFragment = eventsFragment;
        mFragMan = fragMan;
    }

    private Context getContext() {
        return mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Events_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);



        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.item_event, parent, false);

        // Make event items clickable
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open new dialogfragment when clicking on event to show the notes etc

                Events_EditEventDialogFragment eventDialog = new Events_EditEventDialogFragment();

                eventDialog.setTargetFragment(mEventsFragment, 0);
                //eventDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogFragmentTheme);       // makes the dialog fullscreen
                eventDialog.show(mFragMan, "new event");
            }
        });

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final Events_RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Event event = mEventList.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = viewHolder.mNameTextView;
        nameTextView.setText(event.getName());
        TextView timeTextView = viewHolder.mTimeTextView;
        int hour = event.getHour();
        int minute = event.getMinute();
        timeTextView.setText(Event.convertHourAndMinuteToTimeStamp(hour, minute));


        final Events_RecyclerViewAdapter thisAdapter = this;

        /*viewHolder.mThisCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        });*/

        /*viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                task.getCheckBoxView().setChecked(false);

                // TODO: create the task by task id
                // remove the task from the db
                TaskDbHelper dbHelper = new TaskDbHelper(mContext);
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
        });*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
