package com.example.swugger;

import java.util.Calendar;

/**
 * Created by David on 8/11/2018.
 */

public class Reminder {
    private int monthsBefore;
    private int daysBefore;
    private int yearsBefore;
    private int hoursBefore;
    private int minutesBefore;

    Reminder() {

    }

    @Override
    public String toString() {
        return Integer.toString(monthsBefore) + Integer.toString(daysBefore) + Integer.toString(yearsBefore)
                + Integer.toString(hoursBefore) + Integer.toString(minutesBefore);
    }
}
