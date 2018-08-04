package com.example.swugger;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Events_EditEventDialogFragment extends DialogFragment {

    private boolean hasEdits;
    private Events_EditEventDialogFragment thisDialog;
    private EditEventsDialogListener mCallback;     // the callback fragment
    private Event mEvent;
    private ImageButton mBackButton;
    private ImageButton mSaveButton;
    private TextView mDateText;
    private EditText mNameText;
    private EditText mNotesText;

    public Events_EditEventDialogFragment() { }

    /** Implements the EditEventsDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface EditEventsDialogListener {
        void onPositiveClickEdit(Events_EditEventDialogFragment editDialog);
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
                mCallback.onPositiveClickEdit(thisDialog);
                dialog.dismiss();
            }
        });
        mSaveButton = (ImageButton) root.findViewById(R.id.save_toolbar_dialog_edit_event);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNegativeClickEdit();
                dialog.dismiss();
            }
        });

        // get references for and initialize various views in the edit template
        mDateText = (TextView) root.findViewById(R.id.date_dialog_edit_event);
        mDateText.setText(Event.convertHourAndMinuteToTimeStamp(mEvent.getHour(), mEvent.getMinute()));
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
        // TODO: check the date and time as well

        if (eventName.equals(viewName) || eventNotes.equals(viewNotes)) {
            hasEdits = true;
            return true;
        }
        return false;
    }

    public boolean hasEdits() {
        return hasEdits;
    }
}