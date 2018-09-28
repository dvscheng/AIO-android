package com.AIO;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    public static final String SERIALIZE_KEY = "event";
    public static final int MAX_NUM_OF_REMINDERS = 3;
    private long id;
    private String name;
    private String notes;
    private int month;      // [0-11]
    private int day;
    private int year;
    private int hour;       // [0-23]
    private int minute;     // [0-59]
    private ArrayList<Reminder> remindersList;


    /* Used when the USER creates a new event. */
    public Event(long id, String name, String notes, int month, int day, int year, int hour, int minute) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.month = month;
        this.day = day;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        remindersList = new ArrayList<>();
        // creates a universally unique id
    }

    /**  Converts epoch (long) to a date in readable format. */
    public static String convertEpochToReadableDate(long date) {
        // from https://www.epochconverter.com/
        return new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(date));
    }

    /** Converts the given hour and minute to an AM/PM, readable timestamp (i.e. "7:23 PM"). */
    public static String convertHourAndMinuteToTimeStamp(int hour, int minute) {
        // TODO: localization of strings AM and PM
        // Handle the labeling of AM or PM
        String AmOrPm = "";
        if (0 <= hour && hour <= 11) {
            AmOrPm = "AM";
        } else if (12 <= hour && hour <= 23) {
            AmOrPm = "PM";
        }

        // Handle the conversion of hours
        if (AmOrPm.equals("AM")) {
            if (hour == 0) {        // this means it's 12AM midnight
                hour = 12;
            }
        } else if (AmOrPm.equals("PM")) {
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

        if (AmOrPm.equals("") || strMinute.equals("")) {
            // Throw an exception because hours and minutes are not within bounds [0-11] and [0-59] respectively
        }
        // return in "HH:mm AM/PM" format.
        return "" + hour + ":" + strMinute + " " + AmOrPm;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getNotes() {
        return notes;
    }
    /** Zero-indexed. [0-11] */
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getYear() {
        return year;
    }
    /** Zero-indexed. [0-23] */
    public int getHour() {
        return hour;
    }
    /** Zero-indexed. [0-59] */
    public int getMinute() {
        return minute;
    }
    public ArrayList<Reminder> getRemindersList() { return remindersList; }
    /** Clears the remindersList ArrayList. */
    public void clearReminders() {
        remindersList.clear();
    }

    public void addReminder(ReminderWithId newReminder) {
        if (newReminder == null) {
            throw new NullPointerException("attempted to add a null Reminder object, check Event.addReminder()");
        }
        remindersList.add(newReminder);
    }

    /** Checks whether or not this event already has a reminder set at the given new reminder's time. */
    public boolean isDuplicateReminder(Reminder reminder) {
        for (Reminder savedReminder : remindersList) {
            if (savedReminder.getDaysBefore() == reminder.getDaysBefore()
                    && savedReminder.getHoursBefore() == reminder.getHoursBefore()
                    && savedReminder.getMinutesBefore() == reminder.getMinutesBefore()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "id: " + id + "\n"
                + "name: " + name + "\n"
                + "notes: " + notes + "\n"
                + "month: " + month + "\n"
                + "day: " + day + "\n"
                + "year: " + year + "\n"
                + "hour: " + hour + "\n"
                + "minute: " + minute + "\n";
    }
}

