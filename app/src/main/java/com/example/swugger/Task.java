package com.example.swugger;

public class Task {
    private String mName;
    private String mNotes;

    public Task(String name, String notes) {
        mName = name;
        mNotes = notes;
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }
}
