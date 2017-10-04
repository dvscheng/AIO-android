package com.example.swugger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** A simple pager adapter that represents 3 Fragment objects,
 *  in sequence. */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    /** The number of pages defaulted to 3 (email, calender, tasks). */
    private static final int NUM_PAGES = 3;

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
            return new ArticleFragment();
        } else if (position == 2) {
            return new TasksFragment();
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
}