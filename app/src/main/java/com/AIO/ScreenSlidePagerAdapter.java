package com.AIO;

import android.content.Context;

import com.AIO.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/** A simple pager adapter that represents 3 Fragment objects,
 *  in sequence. */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    /** The number of pages defaulted to 3 (email, calender, tasks). */
    private static final int NUM_PAGES = 3;
    private Context mContext;
    private EmailFragment mEmailFragment;
    private EventsFragment mEventFrag;
    private TasksFragment mTaskFrag;

    public ScreenSlidePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    /** If there is not already an item at POSITION,
     *  instantiate and return the appropriate Fragment.*/
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            mEmailFragment = new EmailFragment();
            return mEmailFragment;
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
            return mContext.getString(R.string.email_tab_name);
        } else if (position == 1) {
            return mContext.getString(R.string.calendar_tab_name);
        } else if (position == 2) {
            return mContext.getString(R.string.tasks_tab_name);
        } else {
            throw new IllegalArgumentException(
                    "call getPageTitle(" + position + ") is illegal, 0 <= arg < 3");
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
    public EmailFragment getEmailFrag() { return mEmailFragment; }
    public EventsFragment getEventFrag() { return mEventFrag; }
    public TasksFragment getTaskFrag() { return mTaskFrag; }
}