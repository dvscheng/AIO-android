package com.example.swugger;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

public class Events_EditEventDialogFragment extends DialogFragment {

    public Events_EditEventDialogFragment() { }

    /** Implements the AddTasksDialogListener so that any Activity
     *  that implements it can retrieve information from this Dialog. */
    public interface AddEventsDialogListener {
        /** Send data to the Activity in the form of strings STR. */
        void onPositiveClick(Event event);
        void onNegativeClick();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
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

        return dialog;
    }
}