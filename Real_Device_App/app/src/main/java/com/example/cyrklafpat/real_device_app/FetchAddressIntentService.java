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
 */
public class FetchAddressIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.cyrklafpat.real_device_app.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.cyrklafpat.real_device_app.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.cyrklafpat.real_device_app.extra.PARAM2";

    /* Generic interface for receiving a callback result from someone. */
    protected ResultReceiver result_receiver;

    public FetchAddressIntentService()
    {
        super("FetchAddressIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2)
    {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /** The IntentService is triggered using an Intent, it spawns a new worker thread and the method
     * onHandleIntent() is called on this thread. */

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Geocoder my_geocoder = new Geocoder(this, Locale.getDefault());

        /* Get the receiver passed as extra. */
        result_receiver = intent.getParcelableExtra(Constants.RECEIVER);
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
            deliverResultToReceiver(Constants.FAILURE_RESULT, ioException.getMessage());
        }

        if(addresses == null || addresses.size() == 0)
        {
            Log.e(TAG, "No address found.");
            deliverResultToReceiver(Constants.FAILURE_RESULT, "Failure bro.");
        }
        else
        {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

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
        /* Bundles are generally used for passing data between various Android activities.

        Bundles can be used to send arbitrary data from one activity to another by way of Intents.
        When you broadcast an Intent, interested Activities (and other BroadcastRecievers) will be notified of this.
        An intent can contain a Bundle so that you can send extra data along with the Intent.

        A Bundle is very much like a Java Map object that maps String keys to values. */

        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        result_receiver.send(resultCode, bundle);
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

}


/** Service vs IntentService:
 *
 *      The Service is triggered by calling method startService().
 *      The IntentService is triggered using an Intent, it spawns a new worker thread and the method onHandleIntent()
 *      is called on this thread.
 *
 *      The Service runs in background but it runs on the Main Thread of the application.
 *      The IntentService runs on a separate worker thread.
 *
 *      The Service may block the Main Thread of the application.
 *      The IntentService cannot run tasks in parallel. Hence all the consecutive intents will go into the message queue for
 *      the worker thread and will execute sequentially.
 *
 *      In Service, it is your responsibility to stop the service when its work is done, by calling stopSelf() or stopService().
 *      The IntentService stops the service after all start requests have been handled, so you never have to call stopSelf()
 *
 *
 * */