package com.example.AIO;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddTaskDialogFragment extends DialogFragment {


    private AddTasksDialogListener mCallback;
    private EditText mTaskName;
    private EditText mTaskNotes;
    private Toolbar mToolbar;

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
        View root = inflater.inflate(R.layout.dialog_new_task, null);

        // Sets Callback to the Activity
        mCallback = (AddTasksDialogListener) getTargetFragment();

        mTaskName = root.findViewById(R.id.editText_name_new_task_dialog);
        mTaskNotes = root.findViewById(R.id.editText_notes_new_task_dialog);


        builder.setView(root)
                .setPositiveButton(R.string.dialog_accept_task, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Fetch the EditText fields, convert the text to strings
                        String taskName = mTaskName.getText().toString();
                        String taskNotes = mTaskNotes.getText().toString();

                        Task task = new Task(taskName, taskNotes);

                        // Send to Activity the info.
                        if (!taskName.equals("")) {
                            mCallback.onPositiveClick(task);
                        } else {
                            Toast.makeText(getContext(), "Make sure your task has a title!", Toast.LENGTH_SHORT).show();
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
}