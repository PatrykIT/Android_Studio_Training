package com.example.cyrklafpat.beginning;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;

public class Display_Activity_Message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display___message);
        Log.d("Activity_2 ", "Welcome onCreate()");

        /* Get the Intent that started this activity and extract the string */
        Intent intent_received = getIntent();
        String message_received = intent_received.getStringExtra(MainActivity.EXTRA_MESSAGE);

        /* Capture the layout's TextView and set the string as its text */
        TextView text_view_handle = (TextView) findViewById(R.id.textView);
        text_view_handle.setText(message_received);
    }

//    @Override
//    protected void onResume(){
//        super.onResume();
//        TextView text_view_handle = (TextView) findViewById(R.id.textView);
//        String welcome_text = "Welcome back!";
//        text_view_handle.setText(welcome_text);
//        text_view_handle.setTextColor(Color.RED);
//    }
}
