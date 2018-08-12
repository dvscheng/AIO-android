package com.example.swugger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Event implements Serializable {
    public static final String SERIALIZE_KEY = "event";
    private String uuid;
    private String mName;
    private String mNotes;
    private String mID;
    private int month;      // [0-11]
    private int day;
    private int year;
    private int hour;       // [0-23]
    private int minute;     // [0-59]
    private ArrayList<Reminder> remindersList;


    /* Used when the USER creates a new event. */
    public Event(String name, String notes, int month, int day, int year, int hour, int minute) {
        mName = name;
        mNotes = notes;
        this.month = month;
        this.day = day;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        mID = this.toString();    // assumingg Task.toString() returns a hash.
        // creates a universally unique id
        uuid = UUID.randomUUID().toString();
    }

    /*  Converts epoch (long) to a date in readable format. */
    public static String convertEpochToDate(long date) {
        // from https://www.epochconverter.com/
        return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(date));
    }

    public static String convertHourAndMinuteToTimeStamp(int hour, int minute) {
        // TODO: localization of strings AM and PM
        // Handle the labeling of AM or PM
        String AMorPM = "";
        if (0 <= hour && hour <= 11) {
            AMorPM = "AM";
        } else if (12 <= hour && hour <= 23) {
            AMorPM = "PM";
        }

        // Handle the conversion of hours
        if (AMorPM.equals("AM")) {
            if (hour == 0) {        // this means it's 12AM midnight
                hour = 12;
            }
        } else if (AMorPM.equals("PM")) {
            if (hour != 12) {       // convert from 24hour to 12hour time
                hour -= 12;
            }
        }

        // handle the conversion of minutes
        String strMinute = "";
        if (minute < 10) {
            strMinute = "0" + minute;
        } else if (minute < 59) {
            strMinute = Integer.toString(minute);
        }

        if (AMorPM.equals("") || strMinute.equals("")) {
            // Throw an exception because hours and minutes are not within bounds [0-11] and [0-59] respectively
        }
        // return in "HH:mm AM/PM" format.
        return "" + hour + ":" + strMinute + " " + AMorPM;
    }

    public String getName() {
        return mName;
    }

    public String getNotes() {
        return mNotes;
    }

    public String getID() {
        return mID;
    }

    /* NOT zero-indexed. [1-12] */
    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}

