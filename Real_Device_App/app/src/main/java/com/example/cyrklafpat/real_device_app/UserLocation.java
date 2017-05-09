package com.example.cyrklafpat.real_device_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class UserLocation extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks
{

    private GoogleApiClient my_google_api_client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        Intent intent_received = getIntent();

        my_google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart()
    {
        my_google_api_client.connect();
        super.onStart();
    }

    protected void onStop()
    {
        my_google_api_client.disconnect();
        super.onStop();
    }




    @Override
    public void onConnected(Bundle var1)
    {
        TextView text_view_handle = (TextView) findViewById(R.id.textView5);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling ActivityCompat#requestPermissions
            // to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(...)
            // to handle the case where the user grants the permission.

            Location user_LastLocation = LocationServices.FusedLocationApi.getLastLocation(my_google_api_client);
            if (user_LastLocation != null) {

                text_view_handle.setText(String.valueOf(user_LastLocation.getLatitude()));

                text_view_handle.append("\n");
                text_view_handle.append(String.valueOf(user_LastLocation.getLongitude()));
            }
        }
        else
        {
            text_view_handle.setText("Error: First enable LOCATION ACCESS in settings.");
            return;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult var1)
    {
        // We tried to connect but failed!
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        // We are not connected anymore!
    }
}