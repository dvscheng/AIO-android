package com.example.swugger;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;

public class Events_EditEventDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Events_EditEventDialogFragment thisDialog;
    private EditEventsDialogListener mCallback;     // the callback fragment
    private Event mEvent;
    private ImageButton mBackButton;
    private ImageButton mSaveButton;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private TextView mDateText;
    private TextView mTimeText;
    private EditText mNameText;
    private EditText mNotesText;

    public Events_EditEventDialogFragment() { }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // regardless of whether or not the user chose a different date, update the text
        mDateText.setText(String.format(Locale.US, "%d/%d/%d", month+1, dayOfMonth, year));     // months+1 because months is zero-indexed
        // change the text color to indicate whether or not there's a change
        if (mEvent.getMonth() != month || mEvent.getDay() != dayOfMonth || mEvent.getYear() != year) {
            mDateText.setTextColor(getResources().getColor(R.color.colorAccentTeal));
        } else {
            mDateText.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // regardless of whether or not the user chose a different time, update the text
        mTimeText.setText(Event.convertHourAndMinuteToTimeStamp(hourOfDay, minute));
        // change the text color to indicate whether or not there's a change
        if (mEvent.getHour() != hourOfDay|| mEvent.getMinute() != minute) {
            mTimeText.setTextColor(getResources().getColor(R.color.colorAccentTeal));
        } else {
            mTimeText.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    /** Implements the EditEventsDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface EditEventsDialogListener {
        void onPositiveClickEdit(Events_EditEventDialogFragment editDialog, boolean hasEdits);
        void onNegativeClickEdit();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        thisDialog = this;
        // Sets Callback to the Activity/Fragment
        mCallback = (EditEventsDialogListener) getTargetFragment();
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // the content
        View root = inflater.inflate(R.layout.dialog_edit_event, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));       // mandatory for fullscreen... why?
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // grabs the Event object corresponding to the event ItemView
        mEvent = (Event) getArguments().getSerializable(Event.SERIALIZE_KEY);

        // add listeners to back and save buttons
        mBackButton = (ImageButton) root.findViewById(R.id.back_toolbar_dialog_edit_event);
        mBackButton.setOnClickListener(new View.OnClickListener() { // can it be ImageButton.OnClickListener()?
            @Override
            public void onClick(View v) {
                mCallback.onNegativeClickEdit();
                dialog.dismiss();
            }
        });
        mSaveButton = (ImageButton) root.findViewById(R.id.save_toolbar_dialog_edit_event);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPositiveClickEdit(thisDialog, hasBeenEdited());
                dialog.dismiss();
            }
        });

        // get references for and initialize various views in the edit template
        mDateText = (TextView) root.findViewById(R.id.date_dialog_edit_event);
        mDateText.setText(String.format(Locale.US, "%d/%d/%d", mEvent.getMonth()+1, mEvent.getDay(), mEvent.getYear()));            // months+1 because months is zero-indexed
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), thisDialog, mEvent.getYear(), mEvent.getMonth(), mEvent.getDay());
                datePickerDialog.show();
            }
        });

        mTimeText = (TextView) root.findViewById(R.id.time_dialog_edit_event);
        mTimeText.setText(Event.convertHourAndMinuteToTimeStamp(mEvent.getHour(), mEvent.getMinute()));
        mTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), thisDialog, mEvent.getHour(), mEvent.getMinute(), false);
                timePickerDialog.show();
            }
        });

        mNameText = (EditText) root.findViewById(R.id.name_dialog_edit_event);
        mNameText.setText(mEvent.getName());
        mNotesText = (EditText) root.findViewById(R.id.notes_dialog_edit_event);
        mNotesText.setText(mEvent.getNotes());

        // TODO: possibly need to reset hasEdits? it is an instance so probably not
        return dialog;
    }

    /** Compares original event values and potentially newly edited values to check
     * whether the user has to edits for event. */
    private boolean hasBeenEdited() {
        String eventName = mEvent.getName();
        String eventNotes = mEvent.getNotes();
        String viewName = mNameText.getText().toString();
        String viewNotes = mNotesText.getText().toString();

        if (!eventName.equals(viewName) || !eventNotes.equals(viewNotes)
                || dateChanged() || timeChanged()) {
            return true;
        }
        return false;
    }
    /** Checks whether the user has edited the date. */
    private boolean dateChanged() {
        // mDatePicker == null when the user never attempts to edit the date
        if (mDatePicker == null
                || (mDatePicker.getMonth() == mEvent.getMonth()
                    && mDatePicker.getDayOfMonth() == mEvent.getDay()
                    && mDatePicker.getYear() == mEvent.getYear())) {
            return false;
        }
        return true;
    }
    /** Checks whether the user has edited the time. */
    private boolean timeChanged() {
        // mTimePicker == null when the user never attempts to edit the time
        if (mTimePicker == null
            || (mTimePicker.getCurrentHour() == mEvent.getHour()
                && mTimePicker.getCurrentMinute() == mEvent.getMinute())) {      // Integer should unbox because mEvent.getHour() returns an int primitive
            return false;
        }
        return true;
    }
}