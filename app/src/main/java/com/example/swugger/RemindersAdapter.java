package com.example.swugger;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 8/14/2018.
 */

public class RemindersAdapter extends ArrayAdapter<Reminder> {

    public class ViewHolder {
        public final View itemView;
        public TextView mReminderText;
        public ImageButton mDeleteReminderBtn;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
            mReminderText = (TextView) itemView.findViewById(R.id.text_reminder_edit_event_dialog);
            mDeleteReminderBtn = (ImageButton) itemView.findViewById(R.id.button_remove_reminder_edit_event_dialog);
        }
    }

    public final int MAX_NUM_OF_REMINDERS = 3;
    private Context mContext;
    private List<Reminder> remindersList;

    public RemindersAdapter(Context context, int resource, @NonNull ArrayList<Reminder> objects) {
        super(context, resource, objects);
        mContext = context;
        remindersList = objects;
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
        viewHolder.mReminderText.setText(reminder.toString());
        viewHolder.mDeleteReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: tell the EditEventDialogFragment that this reminder is to be deleted
            }
        });




        return reminderView;
    }
}
