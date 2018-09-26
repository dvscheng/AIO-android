package com.example.AIO;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by David on 8/11/2018.
 */

public class AddReminderDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private static final int DAY_SPINNER_DEFAULT_POS = 0;
    private static final int HOUR_SPINNER_DEFAULT_POS = 0;
    private static final int MINUTE_SPINNER_DEFAULT_POS = 15;
    private EditEventDialogFragment mCallback;       // should be set by caller
    private Spinner mDaySpinner;
    private Spinner mHourSpinner;
    private Spinner mMinuteSpinner;
    private ArrayAdapter daySpinnerAdapter;
    private ArrayAdapter hourSpinnerAdapter;
    private ArrayAdapter minuteSpinnerAdapter;
    private Integer daysBefore;
    private Integer hoursBefore;
    private Integer minutesBefore;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_days_new_reminder_dialog:
                daysBefore = Integer.parseInt((String) parent.getItemAtPosition(position));

            case R.id.spinner_hours_new_reminder_dialog:
                hoursBefore = Integer.parseInt((String) parent.getItemAtPosition(position));

            case R.id.spinner_minutes_new_reminder_dialog:
                minutesBefore = Integer.parseInt((String) parent.getItemAtPosition(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /** MUST CALL
     *  Attaches this DialogFragment to another DialogFragment (in this case, the edit events DialogFragment. */
    public void setTargetDialogFragment(EditEventDialogFragment dialogFragment) {
        mCallback = dialogFragment;
    }

    /** Implements the AddReminderDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface AddReminderDialogListener {
        void onPositiveClickAddReminder(Integer daysBefore, Integer hoursBefore, Integer minutesBefore);
        void onNegativeClickAddReminder();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mCallback.getActivity());
        // Get the layout inflater
        LayoutInflater inflater = mCallback.getActivity().getLayoutInflater();
        // Create the dialog view
        View root = inflater.inflate(R.layout.dialog_new_reminder, null);

        builder.setView(root)
                .setPositiveButton(R.string.create_reminder_dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: check if there is a valid reminder to create
                        mCallback.onPositiveClickAddReminder(daysBefore, hoursBefore, minutesBefore);
                    }
                })
                .setNegativeButton(R.string.create_reminder_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onNegativeClickAddReminder();
                    }
                });

        // set spinners for reminders
        mDaySpinner = root.findViewById(R.id.spinner_days_new_reminder_dialog);
        daySpinnerAdapter = ArrayAdapter.createFromResource(mCallback.getContext(), R.array.reminder_numbers, android.R.layout.simple_spinner_item);
        daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDaySpinner.setAdapter(daySpinnerAdapter);
        mDaySpinner.setOnItemSelectedListener(this);
        mDaySpinner.setSelection(DAY_SPINNER_DEFAULT_POS);
        daysBefore = DAY_SPINNER_DEFAULT_POS;

        mHourSpinner = root.findViewById(R.id.spinner_hours_new_reminder_dialog);
        hourSpinnerAdapter = ArrayAdapter.createFromResource(mCallback.getContext(), R.array.reminder_numbers, android.R.layout.simple_spinner_item);
        hourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHourSpinner.setAdapter(hourSpinnerAdapter);
        mHourSpinner.setOnItemSelectedListener(this);
        mHourSpinner.setSelection(HOUR_SPINNER_DEFAULT_POS);
        hoursBefore = HOUR_SPINNER_DEFAULT_POS;

        mMinuteSpinner = root.findViewById(R.id.spinner_minutes_new_reminder_dialog);
        minuteSpinnerAdapter = ArrayAdapter.createFromResource(mCallback.getContext(), R.array.reminder_numbers, android.R.layout.simple_spinner_item);
        minuteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMinuteSpinner.setAdapter(minuteSpinnerAdapter);
        mMinuteSpinner.setOnItemSelectedListener(this);
        mMinuteSpinner.setSelection(MINUTE_SPINNER_DEFAULT_POS);
        minutesBefore = MINUTE_SPINNER_DEFAULT_POS;

        return builder.create();
    }

    // TODO: make sure it closes if the app is closed
}
