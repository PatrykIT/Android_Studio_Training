import android.app.IntentService;
import android.content.Intent;
import android.location.Geocoder;
import android.support.annotation.Nullable;

import java.util.Locale;

/**
 * Created by cyrklaf.pat on 5/10/2017.
 */

/* This class is your address lookup service. The intent service handles an intent asynchronously on
 a worker thread and stops itself when it runs out of work. */
public class FetchAddressIntentService extends IntentService
{
    public FetchAddressIntentService()
    {
        super("AAA");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        Geocoder my_geocoder = new Geocoder(this, Locale.getDefault());
    }
}

