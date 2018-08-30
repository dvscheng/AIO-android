package com.example.AIO;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditEventDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        AddReminderDialogFragment.AddReminderDialogListener, RemindersAdapter.RemindersAdapterListener {

    public static final long millisecondsPerDay = 86400000;
    public static final long millisecondsPerHour = 3600000;
    public static final long millisecondsPerMinute = 60000;
    private EditEventDialogFragment thisDialog;
    private EditEventsDialogListener mCallback;     // the callback fragment for THIS DoalogFragment
    private Event mEvent;
    private Context mContext;
    private ImageButton mBackButton;
    private ImageButton mDeleteButton;
    private ImageButton mSaveButton;
    private RelativeLayout mDateLayout;
    private RelativeLayout mTimeLayout;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private ImageView mDateIcon;
    private ImageView mTimeIcon;
    private ImageButton mAddRemindersBtn;
    private RemindersAdapter mRemindersAdapter;
    private ListView mDisplayedRemindersListView;
    private ArrayList<Reminder> displayedRemindersList;
    private ArrayList<ReminderWithId> remindersToDeleteList;
    private boolean remindersEdited;
    private TextView mDateText;
    private TextView mTimeText;
    private EditText mNameText;
    private EditText mNotesText;

    public EditEventDialogFragment() { }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mDatePicker = view;
        // regardless of whether or not the user chose a different date, update the text
        mDateText.setText(String.format(Locale.US, "%d/%d/%d", month+1, dayOfMonth, year));     // months+1 because months is zero-indexed
        // change the text color to indicate whether or not there's a change
        Resources res = getResources();
        if (mEvent.getMonth() != month || mEvent.getDay() != dayOfMonth || mEvent.getYear() != year) {
            mDateIcon.setColorFilter(res.getColor(R.color.colorPrimary));
            mDateText.setTextColor(res.getColor(R.color.colorPrimary));
        } else {
            mDateIcon.setColorFilter(res.getColor(R.color.black));
            mDateText.setTextColor(res.getColor(R.color.gray));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimePicker = view;
        // regardless of whether or not the user chose a different time, update the text
        mTimeText.setText(Event.convertHourAndMinuteToTimeStamp(hourOfDay, minute));
        // change the text color to indicate whether or not there's a change
        Resources res = getResources();
        if (mEvent.getHour() != hourOfDay|| mEvent.getMinute() != minute) {
            mTimeIcon.setColorFilter(res.getColor(R.color.colorPrimary));
            mTimeText.setTextColor(res.getColor(R.color.colorPrimary));
        } else {
            mTimeIcon.setColorFilter(getResources().getColor(R.color.black));
            mTimeText.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    /** This method should not be called when Event.MAX_NUM_REMINDERS is reached because the button should be invisible.*/
    @Override
    public void onPositiveClickAddReminder(Integer daysBefore, Integer hoursBefore, Integer minutesBefore) {
        long daysMilliseconds = daysBefore.longValue() * millisecondsPerDay;      // if days is super huge (not likely), watch for overflows
        long hoursMilliseconds = hoursBefore.longValue() * millisecondsPerHour;
        long minutesMilliseconds = minutesBefore.longValue() * millisecondsPerMinute;
        long totalMilliseconds = daysMilliseconds + hoursMilliseconds + minutesMilliseconds;
        // Get a Calendar representation of the date given by the original event
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(mEvent.getYear(), mEvent.getMonth(), mEvent.getDay(), mEvent.getHour(), mEvent.getMinute());
        // Calculate the date of the reminder
        Calendar reminderDate = Calendar.getInstance();
        long reminderDateInMilliseconds = eventDate.getTimeInMillis() - totalMilliseconds;
        // eventDate should ALWAYS be >= reminderDate
        if (reminderDateInMilliseconds < 0) {
            throw new ArithmeticException("The reminder date should never be in the future.");
        } else {
            reminderDate.setTimeInMillis(reminderDateInMilliseconds);
        }
        // TODO: more efficient way of comparing reminder dates instead of having to instantiate a Reminder object and string comparing
        Reminder newReminder = new Reminder(mEvent.getId(), reminderDateInMilliseconds, daysBefore, hoursBefore, minutesBefore);
        // Stop if the event already has this reminder
        for (Reminder reminder : displayedRemindersList) {
            if (reminder.toString().equals(newReminder.toString())) {
                Toast.makeText(mContext, "A reminder with such a date has already been set!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create separate Reminder objects in case we need to modify them later
        displayedRemindersList.add(newReminder);

        mRemindersAdapter.notifyDataSetChanged();

        if (!canAddReminder()) {
            mAddRemindersBtn.setVisibility(View.INVISIBLE);
        }

        remindersEdited = true;
    }

    @Override
    public void onNegativeClickAddReminder() {
        // Do nothing.
    }

    @Override
    public void onDeleteReminder(Reminder reminder) {
        // If the reminder has an Id, this means it's been added to the database and therefore must have its alarm canceled.
        if (reminder.isSavedReminder()) {
            remindersToDeleteList.add((ReminderWithId) reminder);
            remindersEdited = true;
        }

        // Proceed with removing the reminder from the DISPLAYED (potentially unsaved) list
        displayedRemindersList.remove(reminder);
        if (canAddReminder()) {
            mAddRemindersBtn.setVisibility(View.VISIBLE);
        }

        mRemindersAdapter.notifyDataSetChanged();
    }

    /** Implements the EditEventsDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface EditEventsDialogListener {
        /** boolean hasEdits = whether ANY attribute has been edited by user
         * boolean dateChanged = whether a new date has been chosen by user
         * boolean timeChanged = whether a new time has been chosen by user
         * the rest are params for an Event */
        void onSaveClickEdit(Event event, boolean hasEdits, boolean dateChanged, boolean timeChanged,
                             String newName, String newNotes,
                             int newMonth, int newDay, int newYear, int newHour, int newMinute,
                             ArrayList<Reminder> displayedReminderList, ArrayList<ReminderWithId> remindersToDeleteList);
        void onBackClickEdit();
        void onDeleteClickEdit(Event event);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        thisDialog = this;
        mCallback = (EditEventsDialogListener) getTargetFragment();
        // grabs the Event object corresponding to the event ItemView
        mEvent = (Event) getArguments().getSerializable(Event.SERIALIZE_KEY);
        mContext = getContext();
        displayedRemindersList = new ArrayList<>(mEvent.getRemindersList());
        remindersToDeleteList = new ArrayList<>();

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // the content
        View root = inflater.inflate(R.layout.dialog_edit_event, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity(), R.style.FullscreenDialogFragmentTheme);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));       // mandatory for fullscreen... why?
        // dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.FullscreenDialogFragmentAnimation;

        // add listeners to back and save buttons
        mBackButton = (ImageButton) root.findViewById(R.id.button_back_edit_event_dialog);
        mBackButton.setOnClickListener(new View.OnClickListener() { // can it be ImageButton.OnClickListener()?
            @Override
            public void onClick(View v) {
                // TODO: show confirmation dialog if changes have been made
                mCallback.onBackClickEdit();
                dialog.dismiss();
            }
        });
        mDeleteButton = root.findViewById(R.id.button_delete_edit_event_dialog);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.delete_event_dialog_title)
                        .setMessage(R.string.delete_event_dialog_text)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCallback.onDeleteClickEdit(mEvent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // DO NOTHING
                            }
                        })
                        .create()
                        .show();
            }
        });
        mSaveButton = (ImageButton) root.findViewById(R.id.button_save_edit_event_dialog);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = mNameText.getText().toString();
                String newNotes = mNotesText.getText().toString();
                int newMonth = -1;
                int newDay = -1;
                int newYear = -1;
                if (dateChanged()) {
                    newMonth = mDatePicker.getMonth();
                    newDay = mDatePicker.getDayOfMonth();
                    newYear = mDatePicker.getYear();
                }
                int newHour = -1;
                int newMinute = -1;
                if (timeChanged()) {
                    newHour = mTimePicker.getCurrentHour();
                    newMinute = mTimePicker.getCurrentMinute();
                }
                mCallback.onSaveClickEdit(mEvent, hasBeenEdited(), dateChanged(), timeChanged(),
                                                newName, newNotes,
                                                newMonth, newDay, newYear, newHour, newMinute,
                                                displayedRemindersList, remindersToDeleteList);
                dialog.dismiss();
            }
        });

        // get references for and initialize various views in the edit template
        mDateIcon = (ImageView) root.findViewById(R.id.image_date_edit_event_dialog);
        mDateText = (TextView) root.findViewById(R.id.text_date_edit_event_dialog);
        mDateText.setText(String.format(Locale.US, "%d/%d/%d", mEvent.getMonth()+1, mEvent.getDay(), mEvent.getYear()));            // months+1 because months is zero-indexed
        mDateLayout = (RelativeLayout) root.findViewById(R.id.layout_date_edit_event_dialog);
        mDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, thisDialog, mEvent.getYear(), mEvent.getMonth(), mEvent.getDay());
                datePickerDialog.show();
            }
        });

        mTimeIcon = (ImageView) root.findViewById(R.id.image_time_edit_event_dialog);
        mTimeText = (TextView) root.findViewById(R.id.text_time_edit_event_dialog);
        mTimeText.setText(Event.convertHourAndMinuteToTimeStamp(mEvent.getHour(), mEvent.getMinute()));
        mTimeLayout = (RelativeLayout) root.findViewById(R.id.layout_time_edit_event_dialog);
        mTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, thisDialog, mEvent.getHour(), mEvent.getMinute(), false);
                timePickerDialog.show();
            }
        });

        mRemindersAdapter = new RemindersAdapter(this, mContext, R.layout.item_reminder, displayedRemindersList);
        mDisplayedRemindersListView = (ListView) root.findViewById(R.id.listView_reminder_edit_event_dialog);
        mDisplayedRemindersListView.setAdapter(mRemindersAdapter);

        mAddRemindersBtn = (ImageButton) root.findViewById(R.id.button_add_reminder_edit_event_dialog);
        mAddRemindersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddReminderDialogFragment addReminderDialog = new AddReminderDialogFragment();

                // MUST BE CALLED, saves a reference to this DialogFragment in the new add reminder DialogFragment
                addReminderDialog.setTargetDialogFragment(thisDialog);

                addReminderDialog.show(getActivity().getFragmentManager(), "new reminder");
            }
        });
        if (!canAddReminder()) { mAddRemindersBtn.setVisibility(View.INVISIBLE); }

        mNameText = (EditText) root.findViewById(R.id.editText_name_edit_event_dialog);
        mNameText.setText(mEvent.getName());
        mNotesText = (EditText) root.findViewById(R.id.editText_notes_edit_event_dialog);
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
                || dateChanged() || timeChanged() || remindersChanged()) {
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
    /** Checks whether the user has edited any reminders. */
    // TODO implement better handling of this reminders-changed later
    private boolean remindersChanged() {
        return remindersEdited;
    }
    /** Returns whether more reminders can be set for the associated event. */
    public boolean canAddReminder() {
        return displayedRemindersList.size() < Event.MAX_NUM_OF_REMINDERS;
    }
}