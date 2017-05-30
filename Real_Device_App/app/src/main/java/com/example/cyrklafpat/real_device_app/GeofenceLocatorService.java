package com.example.cyrklafpat.real_device_app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeofenceLocatorService extends IntentService
{
    private static final String TAG = GeofenceLocatorService.class.getName();

    public GeofenceLocatorService()
    {
        super("GeofenceLocatorService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        GeofencingEvent geofencing_event = GeofencingEvent.fromIntent(intent);
        if(geofencing_event.hasError())
        {
            Toast.makeText(this, "Error code from geo_event: " + Integer.toString(geofencing_event.getErrorCode()),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int geofence_transition_type = geofencing_event.getGeofenceTransition();

        /* Test that the reported transition was of interest. */
        if(geofence_transition_type == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofence_transition_type == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            /* Get the geofences that were triggered. A single event can trigger multiple geofences. */
            List<Geofence> triggering_geofences = geofencing_event.getTriggeringGeofences();

            /* Get the transition details as String */
            String geofence_tranistion_details = getGeofenceTransitionDetails(this, geofence_transition_type,
                    triggering_geofences);

            sendNotification(geofence_tranistion_details);
        }
        else
        {
            Log.e(TAG, "Error code: " + Integer.toString(geofence_transition_type));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */

    private String getGeofenceTransitionDetails(Context app_context, int geofence_transition_ID,
                                                List<Geofence> geofences_triggered)
    {
        String geofence_transition = getTransitionString(geofence_transition_ID);

        ArrayList triggered_geofences_IDs_list = new ArrayList();
        for(Geofence geofence : geofences_triggered)
        {
            triggered_geofences_IDs_list.add(geofence.getRequestId());
        }

        String triggered_geofences_IDs = TextUtils.join(", ", triggered_geofences_IDs_list);

        return geofence_transition + ": " + triggered_geofences_IDs;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transition_type)
    {
        return "";
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notification_details)
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

}
