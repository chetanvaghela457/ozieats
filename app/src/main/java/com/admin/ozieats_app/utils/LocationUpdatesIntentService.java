package com.admin.ozieats_app.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.LocationResult;

public class LocationUpdatesIntentService extends IntentService {
    private static final String ACTION_PROCESS_UPDATES = "com.admin.ozieats_app.locationupdatespendingintent.action.PROCESS_UPDATES";
    private static final String TAG = "LocationUpdatesIntentService";

    public LocationUpdatesIntentService() {
        super(TAG);
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (ACTION_PROCESS_UPDATES.equals(intent.getAction())) {
                LocationResult extractResult = LocationResult.extractResult(intent);
                if (extractResult != null) {
                    Utils.setLocationUpdatesResult(this, extractResult.getLocations());
                    Log.i("Hahahaha", Utils.getLocationUpdatesResult(this));
                }
            }
        }
    }
}
