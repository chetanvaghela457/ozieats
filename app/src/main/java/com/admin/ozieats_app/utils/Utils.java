package com.admin.ozieats_app.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.admin.ozieats_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Utils {

    private static final String CHANNEL_ID = "channel_01";
    private static final long FASTEST_UPDATE_INTERVAL = 30000;
    public static final String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";
    public static final String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    private static final long MAX_WAIT_TIME = 60000;
    private static final long UPDATE_INTERVAL = 60000;

    /* renamed from: a */
    static final /* synthetic */ boolean f2812a = true;
    private static LocationRequest mLocationRequest;

    public static boolean checkDoNotDisturbPermisssion(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (f2812a || notificationManager != null) {
            return Build.VERSION.SDK_INT < 23 || notificationManager.isNotificationPolicyAccessGranted();
        }
        throw new AssertionError();
    }

    public static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == 0;
    }

    public static void createLocationRequest(Context context) {
        Log.e("createLocation", "createLocationRequest: ");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(100);
        mLocationRequest.setMaxWaitTime(60000);
        locationUpdateRequest(context, fusedLocationProviderClient);
    }


    public static double formatLatLong(double d) {
        return Double.valueOf(String.format("%1.2f", new Object[]{Double.valueOf(d)})).doubleValue();
    }

    private static String getLocationResultText(Context context, List<Location> list) {
        if (list.isEmpty()) {
            return context.getString(R.string.unknown_location);
        }
        StringBuilder sb = new StringBuilder();
        for (Location location : list) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String getLocationResultTitle(Context context, List<Location> list) {
        String quantityString = context.getResources().getQuantityString(R.plurals.num_locations_reported, list.size(), new Object[]{Integer.valueOf(list.size())});
        StringBuilder sb = new StringBuilder();
        sb.append(quantityString);
        sb.append(": ");
        sb.append(DateFormat.getDateTimeInstance().format(new Date()));
        return sb.toString();
    }

    public static String getLocationUpdatesResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LOCATION_UPDATES_RESULT, "");
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static boolean getRequestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (f2812a || connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo() != null;
        }
        throw new AssertionError();
    }

    private static void locationUpdateRequest(Context context, FusedLocationProviderClient fusedLocationProviderClient) {
        try {
            Log.i("requestLocationUpdates", "Starting location updates");
            setRequestingLocationUpdates(context, true);
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getPendingIntent(context));
        } catch (SecurityException e) {
            setRequestingLocationUpdates(context, false);
            e.printStackTrace();
        }
    }

    public static void setLocationUpdatesResult(Context context, List<Location> list) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String str = KEY_LOCATION_UPDATES_RESULT;
        StringBuilder sb = new StringBuilder();
        sb.append(getLocationResultTitle(context, list));
        sb.append("\n");
        sb.append(getLocationResultText(context, list));
        edit.putString(str, sb.toString()).apply();
    }

    public static void setRequestingLocationUpdates(Context context, boolean z) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_LOCATION_UPDATES_REQUESTED, z).apply();
    }
}
