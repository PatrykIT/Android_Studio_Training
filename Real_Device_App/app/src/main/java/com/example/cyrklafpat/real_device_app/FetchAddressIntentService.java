package com.example.cyrklafpat.real_device_app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import android.location.Address;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchAddressIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.cyrklafpat.real_device_app.action.FOO";
    private static final String ACTION_BAZ = "com.example.cyrklafpat.real_device_app.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.cyrklafpat.real_device_app.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.cyrklafpat.real_device_app.extra.PARAM2";


    protected ResultReceiver mReceiver;


    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

        Geocoder my_geocoder = new Geocoder(this, Locale.getDefault());
        /* Get the location passed to this service through an extra. */
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        List<Address> addresses = null;
        try
        {
            addresses = my_geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch(IOException ioException)
        {
            Log.e(TAG, ioException.getMessage());
        }

        if(addresses == null || addresses.size() == 0)
        {
            Log.e(TAG, "No address found.");
            deliverResultToReceiver(Constants.FAILURE_RESULT, "Failure bro.");
        }
        else
        {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++)
            {
                addressFragments.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
