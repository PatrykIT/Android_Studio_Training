package com.example.cyrklafpat.real_device_app;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class UserLocation extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener
{

    private GoogleApiClient my_google_api_client;
    private Location user_last_location;
    private LocationRequest location_request;
    private boolean mUpdatePeriodically = true;
    private TextView text_view_handle;

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

        text_view_handle = (TextView) findViewById(R.id.textView5);

    }

    private void updateUI()
    {
        text_view_handle.setText(String.valueOf(user_last_location.getLatitude()));
        text_view_handle.append("\n");
        text_view_handle.append(String.valueOf(user_last_location.getLongitude()));
    }

    @Override
    protected void onStart()
    {
        my_google_api_client.connect();
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        my_google_api_client.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        /* Consider whether you want to stop the location updates when the activity is no longer in
        focus, such as when the user switches to another app or to a different activity in the same app.
        This can be handy to reduce power consumption. */
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume()
    {
        /* Check whether location updates are currently active, and activate them if not. */
        super.onResume();
        if(my_google_api_client.isConnected() && mUpdatePeriodically == false)
        {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        user_last_location = location;
        updateUI();
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

    @Override
    public void onConnected(Bundle var1)
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
             /* TODO: Consider calling ActivityCompat#requestPermissions
             to request the missing permissions, and then overriding
             public void onRequestPermissionsResult(...)
             to handle the case where the user grants the permission. */

            /* Before requesting location updates, your app must connect to location services and make a location request. */
            location_request = new LocationRequest();
            location_request.setInterval(10000);
            location_request.setFastestInterval(5000);
            location_request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            /* Get the current location settings of a user's device */
            LocationSettingsRequest.Builder location_request_builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(location_request);

            /* Check whether the current location settings are satisfied */
            final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(my_google_api_client, location_request_builder.build());

            /* Check the location settings, and prompt user to change location settings if they are not available. */
            result.setResultCallback(new ResultCallback<LocationSettingsResult>()
            {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
                {
                    final Status status = locationSettingsResult.getStatus();
                    final LocationSettingsStates location_states = locationSettingsResult.getLocationSettingsStates();
                    /* NOTE: In debug mode none of this statements seems to bind. Status is = SUCCESS. */
                    switch(status.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //Location settings are not satisfied, but this can be fixed by showing the user a dialog:
//                            try
//                            {
//                                status.startResolutionForResult(OuterClass.this, REQUEST_CHECK_SETTINGS);
//                            }
//                            catch(IntentSender.SendIntentException exception)
//                            {
//                                //Handle error.
//                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });

            user_last_location = LocationServices.FusedLocationApi.getLastLocation(my_google_api_client);
            if (user_last_location != null)
            {
                text_view_handle.setText(String.valueOf(user_last_location.getLatitude()));
                text_view_handle.append("\n");
                text_view_handle.append(String.valueOf(user_last_location.getLongitude()));
                if(user_last_location.hasAltitude())
                {
                    text_view_handle.append("\n");
                    text_view_handle.append(String.valueOf(user_last_location.getAltitude()));
                }
            }
        }

        if(mUpdatePeriodically)
            startLocationUpdates();

        else
        {
            text_view_handle.setText("Error: First enable LOCATION ACCESS in settings.");
            return;
        }
    }

    protected void startLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(my_google_api_client,
                    location_request, this);
        }
    }

    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(my_google_api_client, this);
    }

    public void getAddress() throws IOException
    {
        Geocoder my_geocoder = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addresses = my_geocoder.getFromLocation(user_last_location.getLatitude(),
                user_last_location.getLongitude(), 1);
    }
}


