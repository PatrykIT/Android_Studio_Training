package com.example.cyrklafpat.real_device_app;

/**
 * Created by cyrklaf.pat on 5/10/2017.
 */


public final class Constants
{
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    /** Used to set an expiration time for a geofence. Aftet this time Location Services stops tracking the geofence. */
    public static final long GEOFENCE_EXPIRATION_HOURS = 12;


    public static final String PACKAGE_NAME =
            "com.example.cyrklafpat.real_device_app";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static final String GEOFENCE_ID = "DEFAULT_GEOFENCE_ID";
    public static final float GEOFENCE_RADIUS_METERS = 500.0f;
    public static final long GEOFENCE_DURATION_MILISECONDS =  GEOFENCE_EXPIRATION_HOURS * 60 * 60 * 1000;
}