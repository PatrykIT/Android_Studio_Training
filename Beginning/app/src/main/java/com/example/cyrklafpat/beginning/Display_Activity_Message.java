package com.example.cyrklafpat.beginning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Display_Activity_Message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display___message);

        /* Get the Intent that started this activity and extract the string */
        Intent intent_received = getIntent();
        String message_received = intent_received.getStringExtra(MainActivity.EXTRA_MESSAGE);

        /* Capture the layout's TextView and set the string as its text */
        TextView text_view_handle = (TextView) findViewById(R.id.textView);
        text_view_handle.setText(message_received);
    }
}
