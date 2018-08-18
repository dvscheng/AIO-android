package com.example.swugger;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.swugger.db.EventContract;
import com.example.swugger.db.ReminderContract;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    public final static int PENDING_INTENT_REQUEST_CODE = 1;
    /** A set containing all of the user's accounts. */
    private HashMap<String, String> userAccounts = new HashMap<>();
    /** The custom toolbar that I made, has teal-ish color.
     * Similar in flavor to MainActivity's. */
    private Toolbar mToolbar;
    /** An intent message that I don't use. */
    public final static String EXTRA_MESSAGE_EP = "com.example.Swugger.MESSAGE_E";

    private final static int NUM_OF_FRAGMENTS_OFF_SCREEN = 2;

    /** The pager widget, which handles animation and allows
     * swiping horizontally to access previous and next wizard steps. */
    private ViewPager mPager;

    /** The pager adapter, which provides the pages to the view pager widget. */
    private ScreenSlidePagerAdapter mPagerAdapter;      // was PagerAdapter

    /** 1. Set the toolbar.
     *  2. Create the fragment manager, which will be used for the ViewPager's PagerAdapter.
     *  3. Retrieve the email and password from the intent and save them.
     *  4. Adds the email and password to the dictionary
     *  5. Create a PagerTabStrip (the white indicator of which tab you're on),
     *      set the color to white (I think), and set the Strings' color to white.
     *  6. Instantiate a ViewPager and a PagerAdapter set the number of off-screen fragments
     *      to 2 so that the fragments don't get destroyed and remade when at an edge fragment.
     *      Set the starting 'screen' or fragment to the middle one - the calendar.
     * */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1.
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        // 2.
        FragmentManager fragMan = getSupportFragmentManager();

        // 3.
        Intent intent = getIntent();
        String inputEmail = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_E);
        String inputPassword = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_P);

        // 4.
        userAccounts.put(inputEmail, inputPassword);

        // 5.
        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pagertabstrip_main);
        tabStrip.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabStrip.setTextColor(Color.WHITE);
        tabStrip.setTabIndicatorColor(Color.WHITE);

        // 6.
        mPager = (ViewPager) findViewById(R.id.viewpager_main);
        mPagerAdapter = new ScreenSlidePagerAdapter(fragMan);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(NUM_OF_FRAGMENTS_OFF_SCREEN);
        mPager.setCurrentItem(1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.debug_toolbar_task_list:
                // User chose the "Accounts" action.
                // TODO: list out the tasks and events in a dialogfragment when pressed
                mPagerAdapter.getTaskFrag().printDatabase();
                return true;
            case R.id.debug_toolbar_event_list:
                mPagerAdapter.getEventFrag().printDatabase(EventContract.EventEntry.TABLE_NAME);
                return true;
            case R.id.debug_toolbar_reminder_list:
                mPagerAdapter.getEventFrag().printDatabase(ReminderContract.ReminderEntry.TABLE_NAME);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Show a DialogFragment
    }
}
