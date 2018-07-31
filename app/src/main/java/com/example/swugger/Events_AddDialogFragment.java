package com.example.swugger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class Events_AddDialogFragment extends DialogFragment {

    private AddEventsDialogListener mCallback;
    private EditText mEventName;
    private EditText mEventNotes;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private long date;
    private Toolbar mToolbar;

    public Events_AddDialogFragment() {

    }
    public Events_AddDialogFragment(long date) {
        this.date = date;
    }

    /** Implements the AddTasksDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface AddEventsDialogListener {
        /** Send data to the Activity in the form of strings STR. */
        void onPositiveClick(Event event);
        void onNegativeClick();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Sets Callback to the Activity
        mCallback = (AddEventsDialogListener) getTargetFragment();

        builder.setView(inflater.inflate(R.layout.dialog_new_event, null))
                .setPositiveButton(R.string.create_event_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: can probably take out the initializations of the variables, look into the dialog var passed in here and check if findViewById requires it
                        // Fetch the EditText fields, convert the text to strings
                        Dialog d = (Dialog) dialog;
                        mEventName = (EditText) d.findViewById(R.id.create_event_dialog_editText_name);
                        String eventName = mEventName.getText().toString();
                        mEventNotes = (EditText) d.findViewById(R.id.create_event_dialog_editText_notes);
                        String eventNotes = mEventNotes.getText().toString();
                        mDatePicker = (DatePicker) d.findViewById(R.id.create_event_dialog_datepicker);
                        int month = mDatePicker.getMonth(); // months are [0-11]
                        int day = mDatePicker.getDayOfMonth();
                        int year = mDatePicker.getYear();
                        mTimePicker = (TimePicker) d.findViewById(R.id.create_event_dialog_timepicker);
                        int hour = mTimePicker.getCurrentHour(); // hours are [0-23] was deprecated in API level 23 to getHour()
                        int minute = mTimePicker.getCurrentMinute(); // minutes are [0-59] was deprecated in API level 23 to getMinute()

                        Event event = new Event(eventName, eventNotes, month, day, year, hour, minute);

                        // Send to Activity the info.
                        if (!eventName.equals("")) {
                            mCallback.onPositiveClick(event);
                        }
                    }
                })
                .setNegativeButton(R.string.create_event_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onNegativeClick();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}