package com.example.swugger;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 8/14/2018.
 */

public class RemindersAdapter extends ArrayAdapter<Reminder> {

    /** Implements the RemindersAdapterListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface RemindersAdapterListener {
        /** boolean hasEdits = whether ANY attribute has been edited by user
         * boolean dateChanged = whether a new date has been chosen by user
         * boolean timeChanged = whether a new time has been chosen by user
         * the rest are params for an Event */
        void onDeleteReminder(Reminder reminder);
    }

    public class ViewHolder {
        public final View itemView;
        public Chip mChip;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
            mChip = itemView.findViewById(R.id.chip_reminder_edit_event_dialog);
        }
    }

    private RemindersAdapterListener mRemindersAdapterListener;
    private Context mContext;
    private List<Reminder> remindersList;

    public RemindersAdapter(RemindersAdapterListener remindersAdapterListener, Context context, int resource, @NonNull ArrayList<Reminder> objects) {
        this(context, resource, objects);
        mRemindersAdapterListener = remindersAdapterListener;
        mContext = context;
        remindersList = objects;
    }

    private RemindersAdapter(Context context, int resource, @NonNull ArrayList<Reminder> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View reminderView = convertView;
        if (reminderView == null) {
            reminderView = inflater.inflate(R.layout.item_reminder, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(reminderView);
        final Reminder reminder = remindersList.get(position);

        // set the text and set an onClickListener for the delete reminder button
        viewHolder.mChip.setText(reminder.toString());
        viewHolder.mChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: tell the EditEventDialogFragment that this reminder is to be deleted
                mRemindersAdapterListener.onDeleteReminder(reminder);
            }
        });

        return reminderView;
    }
}
