package com.example.swugger;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/** A simple pager adapter that represents 3 Fragment objects,
 *  in sequence. */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    /** The number of pages defaulted to 3 (email, calender, tasks). */
    private static final int NUM_PAGES = 3;
    private EventsFragment mEventFrag;
    private TasksFragment mTaskFrag;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /** If there is not already an item at POSITION,
     *  instantiate and return the appropriate Fragment.*/
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new EmailFragment();
        } else if (position == 1) {
            // TODO: is this the best way to get a reference to the event fragment?
            mEventFrag = new EventsFragment();
            return mEventFrag;
        } else if (position == 2) {
            mTaskFrag = new TasksFragment();
            return mTaskFrag;
        } else {
            throw new IllegalArgumentException(
                    "call getItem(" + position + ") is illegal, 0 <= arg < 3");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Email";
        } else if (position == 1) {
            return "Calendar";
        } else if (position == 2) {
            return "Tasks";
        } else {
            throw new IllegalArgumentException(
                    "call getPageTitle(" + position + ") is illegal, 0 <= arg < 3");
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
    public EventsFragment getEventFrag() { return mEventFrag; }
    public TasksFragment getTaskFrag() { return mTaskFrag; }
}