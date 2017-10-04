package com.example.swugger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.EditText;

public class AddTasksDialogFragment extends DialogFragment {

    /** Implements the AddTasksDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface AddTasksDialogListener {
        /** Send data to the Activity in the form of strings STR. */
        void onPositiveClick(Task task);
        void onNegativeClick();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Sets Callback to the Activity
        mCallback = (AddTasksDialogListener) getTargetFragment();

        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
                .setPositiveButton(R.string.dialog_accept_task, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Fetch the EditText fields, convert the text to strings
                        Dialog d = (Dialog) dialog;
                        mTaskName = (EditText) d.findViewById(R.id.dialog_editText_name);
                        String taskName = mTaskName.getText().toString();
                        mTaskNotes = (EditText) d.findViewById(R.id.dialog_editText_notes);
                        String taskNotes = mTaskNotes.getText().toString();

                        Task task = new Task(taskName, taskNotes);

                        // Send to Activity the info.
                        if (!taskName.equals("")) {
                            mCallback.onPositiveClick(task);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_task, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mCallback.onNegativeClick();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private AddTasksDialogListener mCallback;

    private EditText mTaskName;

    private EditText mTaskNotes;

    private Toolbar mToolbar;
}