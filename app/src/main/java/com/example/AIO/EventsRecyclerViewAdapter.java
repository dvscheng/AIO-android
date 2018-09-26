package com.example.AIO;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public TextView mTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.text_name_event_item);
            mTimeTextView = itemView.findViewById(R.id.text_time_event_item);
        }
    }

    private List<Event> mEventList;
    private Context mContext;
    private Fragment mEventsFragment;
    private FragmentManager mFragMan;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EventsRecyclerViewAdapter(Context context, List<Event> events, Fragment eventsFragment,
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
    public EventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View eventView = inflater.inflate(R.layout.item_event, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final EventsRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Event event = mEventList.get(position);

        // Set item views based on your views and data model
        TextView nameTextView = viewHolder.mNameTextView;
        nameTextView.setText(event.getName());
        TextView timeTextView = viewHolder.mTimeTextView;
        int hour = event.getHour();
        int minute = event.getMinute();
        timeTextView.setText(Event.convertHourAndMinuteToTimeStamp(hour, minute));

        // Make item views clickable
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventDialogFragment eventDialog = new EditEventDialogFragment();

                // pass in the event object into the edit dialog
                Bundle args = new Bundle();
                args.putSerializable(Event.SERIALIZE_KEY, event);
                eventDialog.setArguments(args);

                eventDialog.setTargetFragment(mEventsFragment, 0);
                //eventDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogFragmentTheme);       // makes the dialog fullscreen
                eventDialog.show(mFragMan, "new event");
            }
        });


        final EventsRecyclerViewAdapter thisAdapter = this;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
