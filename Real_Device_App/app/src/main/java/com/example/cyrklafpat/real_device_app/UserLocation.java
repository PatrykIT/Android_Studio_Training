package com.example.cyrklafpat.real_device_app;

import android.Manifest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;


/** To use the Google’s Location Services, your app needs to connect to the GooglePlayServicesClient.
 *  To connect to the client, your activity (or fragment etc) needs to implement:
 *  GooglePlayServicesClient.ConnectionCallbacks and
 *  GooglePlayServicesClient.OnConnectionFailedListener interfaces. */

public class UserLocation extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, ResultCallback<Status>
{
    private static final String TAG = UserLocation.class.getSimpleName();

    public GoogleApiClient my_google_api_client;
    public Location user_last_location;
    public LocationRequest location_request;
    public TextView text_view_handle;
    public boolean mUpdatePeriodically = true;

    private AddressResultReceiver address_result_receiver;

    class AddressResultReceiver extends ResultReceiver
    {

        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            /* Since many contexts are bound to a limited-lifetime object such as an Activity,
            there needs to be a way to get a longer-lived context, for purposes such as registering
            for future notifications. That is achieved by Context.getApplicationContext().

            Use this instead of the current Activity context if you need a context tied to the
            lifecycle of the entire application, not just the current Activity.*/
            Context app_context = getApplicationContext();

            CharSequence text = "Received location.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast_notifier = Toast.makeText(app_context, text, duration);
            toast_notifier.show();

            String address_output = resultData.getString(Constants.RESULT_DATA_KEY);

            if(resultCode == Constants.SUCCESS_RESULT)
            {
                text_view_handle.setText("");
                text_view_handle.setTextColor(Color.BLUE);

                String welcome_string = "\nYour current location is:\n";
                String location_string = welcome_string + address_output;

                /* Change colour of the address. */
                Spannable my_spannable = new SpannableString(location_string);
                my_spannable.setSpan(new ForegroundColorSpan(Color.MAGENTA), welcome_string.length(),
                        location_string.length(), 0);

                text_view_handle.append(my_spannable);

                text_view_handle.append("\nCoordinates: \n " + String.valueOf(user_last_location.getLatitude())
                        + "\n" + String.valueOf(user_last_location.getLongitude()));
            }
            else if(resultCode == Constants.FAILURE_RESULT)
            {
                text_view_handle.setTextColor(Color.RED);
                text_view_handle.append("\nFailure: " + address_output);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        /* A Handler object registers itself with the thread in which it is created. It provides a channel
        to send data to this thread.
        For example, if you create a new Handler instance in the onCreate() method of your activity,
        it can be used to post data to the main thread.

        To move data from a background thread to the UI thread, use a Handler that's running on the UI thread.*/
        address_result_receiver = new AddressResultReceiver(new Handler());

        Intent intent_received = getIntent();

        my_google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /* Get UI widgets */
        text_view_handle = (TextView) findViewById(R.id.textView5);
        add_geofences_button = (Button) findViewById(R.id.Add_Geofence_Button_ID);
        remove_geofences_button = (Button) findViewById(R.id.Remove_Geofence_Button_ID);

        geofence_list = new ArrayList<Geofence>();

        shared_preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        /* The SharedPreferences class provides a general framework that allows you to save and retrieve
        persistent key-value pairs of primitive data types.
        It retrieves and holds the contents of the preferences file 'name', returning a SharedPreferences through which
        you can retrieve and modify its values.



        Arg_1_String - The 'name' parameter is the base name (without file extension) of an XML preferences file located
        in the private storage of the app.
        If a preferences file by this name does not exist, it will be created when you retrieve an editor
        (SharedPreferences.edit()) and then commit changes (Editor.commit()) */

        geofences_added = shared_preferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        addGeofencesToList();

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
                            /*
                            try
                            {
                                status.startResolutionForResult(OuterClass.this, REQUEST_CHECK_SETTINGS);
                            }
                            catch(IntentSender.SendIntentException exception)
                            {
                                //Handle error.
                            }
                            */
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });

            /* Fused Location Provider automatically chooses what underlying provider to use
            (GPS or Network[wi-fi or cell tower]), based on accuracy, battery usage, etc.
            It is fast because you get location from a system-wide service that keeps updating it.
            Thus it is better alternative then Androids Location API.
            You can also use more advanced features such as geofencing.*/
            user_last_location = LocationServices.FusedLocationApi.getLastLocation(my_google_api_client);
            if (user_last_location != null)
            {
                if(user_last_location.hasAltitude())
                {
                    text_view_handle.append("\n");
                    text_view_handle.append("Altitude: " + String.valueOf(user_last_location.getAltitude()));
                }
            }
        }

        if(Geocoder.isPresent() == false)
        {
            text_view_handle.setText("Geocoder is not present!");
            return;
        }

        if(my_google_api_client.isConnected() && user_last_location != null)
            startIntentService();

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
        /* Check for permissions */
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(my_google_api_client,
                    location_request, this);
        }
    }

    /** You have to register Location Listener for your Location Manager, then only onLocationChanged()
     * will be called according the settings you supplied while registering location listener.
     * http://stackoverflow.com/questions/8600688/android-when-exactly-is-onlocationchanged-called */
    @Override
    public void onLocationChanged(Location location)
    {
        user_last_location = location;
        startIntentService();
    }


    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(my_google_api_client, this);
    }

    protected void startIntentService()
    {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, address_result_receiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, user_last_location);

        startService(intent);
    }

    /* -------------------------- -------------------------- GEOFENCING --------------------------  -------------------------- */

    /** Used when requesting to add or remove geofences. */
    private PendingIntent geofence_pending_intent = null;

    /** The GeofencingRequest class receives the geofences that should be monitored. */
    private GeofencingRequest geofencing_request;

    /** List of our geofences */
    protected ArrayList<Geofence> geofence_list;

    /** Indicator wheter geofences were added. TODO: Can it be done withou bool? List surely has a method .empty() ;p */
    private boolean geofences_added;

    /** Buttons for starting the process of adding / removing geofences. */
    private Button add_geofences_button;
    private Button remove_geofences_button;

    /** Used to persist application state about whether geofences were added. */
    private SharedPreferences shared_preferences;

    /** Application specific request code to match with a result reported to
     * {@link ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult */
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onResult(@NonNull Status operation_status)
    {
        if(operation_status.isSuccess() == false)
        {
            Toast.makeText(this, "Operation Status is NOT successful.", Toast.LENGTH_SHORT).show();
            return;
        }

        geofences_added = !geofences_added; //TODO: Could it be set to 'true' instead of '!' ?
        SharedPreferences.Editor preferences_editor = shared_preferences.edit();
        preferences_editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, geofences_added);
        preferences_editor.apply();

        /* Update the UI. Adding geofences enables the Remove Geofences button, and removing
        geofences enables the Add Geofences button. */
        enableGeofenceButtons();
    }

    /** A PendingIntent specifies an action to take in the future. It is an Intent action that you want to perform, but at a
     * later time.
     *
     * By giving a PendingIntent to another application, you are
     * granting it the right to perform the operation you have specified as if the other application was yourself. */
    private PendingIntent getGeofencePendingIntent()
    {
        if(geofence_pending_intent != null)
            return geofence_pending_intent;

        Intent intent = new Intent(this, GeofenceLocatorService.class);

        /* We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
         * addGeofences() and removeGeofences() */
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofencesToList()
    {
        Geofence geofence = new Geofence.Builder()
                /* Request ID is a string to identify this geofence inside your application. */
                .setRequestId(Constants.GEOFENCE_ID)
                .setCircularRegion(user_last_location.getLatitude(),  user_last_location.getLongitude(),
                        Constants.GEOFENCE_RADIUS_METERS)
                .setExpirationDuration(Constants.GEOFENCE_DURATION_MILISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        geofence_list.add(geofence);

        //geofencing_request = getGeofencingRequest();
    }

    /** Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored. */
    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder geofencing_request = new GeofencingRequest.Builder();
        /* Specifying INITIAL_TRIGGER_ENTER tells Location services that GEOFENCE_TRANSITION_ENTER should be triggered
        if the the device is already inside the geofence. */
        geofencing_request.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        /* Add the geofences to be monitored by geofencing service. */
        geofencing_request.addGeofences(geofence_list);

        return geofencing_request.build();
    }

    public void addGeofencesButton(View view)
    {
        if(my_google_api_client.isConnected() == false)
        {
            Toast.makeText(this, "Not connected Google API!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.GeofencingApi.addGeofences(my_google_api_client, getGeofencingRequest(),
                    getGeofencePendingIntent()).setResultCallback(this);
        }

        try
        {
            LocationServices.GeofencingApi.addGeofences(my_google_api_client, getGeofencingRequest(),
                    getGeofencePendingIntent()).setResultCallback(this); //Result processed in onResult()
        }
        catch(SecurityException security_exception)
        {
            Toast.makeText(this, security_exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void removeGeofencesButton(View view)
    {
        if(my_google_api_client.isConnected() == false)
        {
            Toast.makeText(this, "Google client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            LocationServices.GeofencingApi.removeGeofences(my_google_api_client, getGeofencePendingIntent())
                    .setResultCallback(this); //Result processed in onResult()
        }
        catch(SecurityException security_exception)
        {
            Toast.makeText(this, security_exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /** Ensures that only one button is enabled at any time. */
    public void enableGeofenceButtons()
    {
        if(geofences_added)
        {
            add_geofences_button.setEnabled(false);
            remove_geofences_button.setEnabled(true);
        }
        else
        {
            add_geofences_button.setEnabled(true);
            remove_geofences_button.setEnabled(false);
        }
    }

}












