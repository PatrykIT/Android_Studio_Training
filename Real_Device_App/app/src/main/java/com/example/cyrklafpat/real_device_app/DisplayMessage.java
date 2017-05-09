package com.example.cyrklafpat.real_device_app;

import android.content.Intent;;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


//, LocationUser_Fragment.OnFragmentInteractionListener
public class DisplayMessage extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent_received = getIntent();
        String message_received = intent_received.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if(message_received.isEmpty())
            message_received = "Empty!";

        TextView text_view_handle = (TextView) findViewById(R.id.textView);
        text_view_handle.setText(message_received);



    }



//    @Override
//    public void onFragmentInteraction(Uri uri)
//    {
//
//    }



}
