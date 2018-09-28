package com.AIO;

import android.widget.CheckBox;

public class Task {
    private String mName;
    private String mNotes;
    private String mId;
    private boolean isChecked;

    public Task(String name, String notes) {
        mName = name;
        mNotes = notes;
        isChecked = false;
        mId = this.toString();    // assumingg Task.toString() returns a hash.
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }

    public String getId() { return mId; }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) { isChecked = checked; }
}
