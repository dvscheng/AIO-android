package com.example.swugger;

import android.view.View;
import android.widget.CheckBox;

public class Task {
    private String mName;
    private String mNotes;
    private String mID;
    private CheckBox vCheckBox;

    public Task(String name, String notes) {
        mName = name;
        mNotes = notes;
        mID = this.toString();    // assumingg Task.toString() returns a hash.
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }

    public String getID() { return mID; }

    public void setCheckBoxView(CheckBox checkBox) { vCheckBox = checkBox; }        // check Tasks_RecyclerViewAdapter for checkbox ref assignment

    public CheckBox getCheckBoxView() { return vCheckBox; }
}
