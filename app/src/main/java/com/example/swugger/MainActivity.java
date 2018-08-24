package com.example.swugger;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    /** The custom toolbar that I made, has teal-ish color. */
    private Toolbar mToolbar;
    /** The button that for now goes to the next screen. */
    private Button mNameButton;
    /** The EditText for the user's email address. */
    private EditText mEmailText;
    /** The EditText for the user's password for their email address. */
    private EditText mPassText;
    /** Miscellaneous intent messages. */
    public final static String EXTRA_MESSAGE_E = "com.example.Swugger.MESSAGE_E";
    public final static String EXTRA_MESSAGE_P = "com.example.Swugger.MESSAGE_P";

    /** 1. Create the custom toolbar.
     *  2. Create the button that continues to the HomeActivity. This button creates
     *   an intent containing the user's email address and password that is to be intercepted
     *   by the HomeActivity. Also attaches intent messages to the intent. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the email and pass..
        mEmailText = (EditText) findViewById(R.id.email_text_field);
        mPassText = (EditText) findViewById(R.id.password_text_field);

        // 1.
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);

        // 2.
        mNameButton = (Button) findViewById(R.id.name_pass_button);
        mNameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Create the intent.
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                // make them into Strings..
                String inputEmail = mEmailText.getText().toString();
                String inputPassword = mPassText.getText().toString();

                // attach intent messages and then add them to the intent.
                intent.putExtra(EXTRA_MESSAGE_E, inputEmail);
                intent.putExtra(EXTRA_MESSAGE_P, inputPassword);

                startActivity(intent);
                finish();
            }
        });
    }

    /** Clear the EditText values for the email and password fields when the
     * activity is closed. */
    @Override
    public void onStop() {
        super.onStop();
        mEmailText.setText("");
        mPassText.setText("");
    }

    /** Default. */
    @Override
    public void onPause() {
        super.onPause();
        finish();
    }
}
