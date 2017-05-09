package com.example.cyrklafpat.real_device_app;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity  {
    public static final String EXTRA_MESSAGE = "com.example.cyrklafpat.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view){
        Intent my_intent = new Intent(this, DisplayMessage.class);
        EditText text_edit_handle = (EditText) findViewById(R.id.editText);
        String final_message = text_edit_handle.getText().toString();
        my_intent.putExtra(EXTRA_MESSAGE, final_message);
        startActivity(my_intent);
    }

    public void activateLocationActivity(View view)
    {
        Intent location_intent = new Intent(this, UserLocation.class);
        startActivity(location_intent);
    }

}
