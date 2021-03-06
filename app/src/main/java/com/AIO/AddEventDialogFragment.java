package com.AIO;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.AIO.R;

public class AddEventDialogFragment extends DialogFragment {

    private AddEventDialogListener mCallback;
    private EditText mEventName;
    private EditText mEventNotes;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private long date;
    private Toolbar mToolbar;

    public AddEventDialogFragment() {

    }
    // TODO: do bundle with args instead of a diff constructor
    public AddEventDialogFragment(long date) {
        this.date = date;
    }

    /** Implements the AddTasksDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface AddEventDialogListener {
        /** Send data to the Activity in the form of strings STR. */
        void onPositiveClickAdd(String name, String notes, int month, int day, int year, int hour, int minute);
        void onNegativeClickAdd();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Sets Callback to the Activity
        mCallback = (AddEventDialogListener) getTargetFragment();

        builder.setView(inflater.inflate(R.layout.dialog_new_event, null))
                .setPositiveButton(R.string.create_event_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: can probably take out the initializations of the variables, look into the dialog var passed in here and check if findViewById requires it
                        // Fetch the EditText fields, convert the text to strings
                        Dialog d = (Dialog) dialog;
                        mEventName = d.findViewById(R.id.editText_name_new_event_dialog);
                        String eventName = mEventName.getText().toString();
                        mEventNotes = d.findViewById(R.id.editText_notes_new_event_dialog);
                        String eventNotes = mEventNotes.getText().toString();
                        mDatePicker = d.findViewById(R.id.datePicker_new_event_dialog);
                        int month = mDatePicker.getMonth(); // months are [0-11]
                        int day = mDatePicker.getDayOfMonth();
                        int year = mDatePicker.getYear();
                        mTimePicker = d.findViewById(R.id.timePicker_new_event_dialog);
                        int hour = mTimePicker.getCurrentHour(); // hours are [0-23] was deprecated in API level 23 to getHour()
                        int minute = mTimePicker.getCurrentMinute(); // minutes are [0-59] was deprecated in API level 23 to getMinute()

                        // Send to the callback the info.
                        if (!eventName.equals("")) {
                            mCallback.onPositiveClickAdd(eventName, eventNotes, month, day, year, hour, minute);
                        } else {
                            Toast.makeText(getContext(), "Make sure your event has a title!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.create_event_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onNegativeClickAdd();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}