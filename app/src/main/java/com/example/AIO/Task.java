package com.example.AIO;

import android.widget.CheckBox;

public class Task {
    private String mName;
    private String mNotes;
    private String mId;
    private CheckBox vCheckBox;

    public Task(String name, String notes) {
        mName = name;
        mNotes = notes;
        mId = this.toString();    // assumingg Task.toString() returns a hash.
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }

    public String getId() { return mId; }

    public void setCheckBoxView(CheckBox checkBox) { vCheckBox = checkBox; }        // check TasksRecyclerViewAdapter for checkbox ref assignment

    public CheckBox getCheckBoxView() { return vCheckBox; }
}
