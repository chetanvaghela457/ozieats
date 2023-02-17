package com.admin.ozieats_app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.Observer;

import com.admin.ozieats_app.R;
import com.admin.ozieats_app.data.OrderRepository;
import com.admin.ozieats_app.model.OrderTime;
import com.admin.ozieats_app.model.User;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";

    String[] separated;
        public static final String ACTION_PROCESS_UPDATES = "com.admin.ozieats_app.locationupdatespendingintent.action.PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Log.e("UpdateLocation", "onReceive: " + locations.get(0).getLatitude() + "-----" + locations.get(0).getLongitude());

                    LatLng sourceLatLng = new LatLng(locations.get(0).getLatitude(),locations.get(0).getLongitude());

                    UtilityKt.addLatLngToPreference(context,sourceLatLng);

                    ArrayList<OrderTime> orderTimeArrayList = UtilityKt.getOrderTimePreference(context);
                    for (int i = 0; i < orderTimeArrayList.size(); i++) {
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LatLng destinationLatLng = new LatLng(orderTimeArrayList.get(i).getLat(), orderTimeArrayList.get(i).getLng());
                        getDurationTime(context, sourceLatLng ,destinationLatLng, orderTimeArrayList, i);
                        //OrderTime orderTime=orderTimeArrayList.get(i);

                    }

                  //  Utils.setLocationUpdatesResult(context, locations);
                  //  Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations));
                    Log.i(TAG, Utils.getLocationUpdatesResult(context));
                }
                else
                {
                    Log.e("UpdateLocation", "onReceive: Result Null" );
                }
            }
            else
            {
                Log.e("UpdateLocation", "ACTION_PROCESS_UPDATES not match " );
            }
        }
        else
        {
            Log.e("UpdateLocation", "onReceive: Intent Null" );
        }
    }

    private void getDurationTime(Context context, LatLng source, LatLng destination, ArrayList<OrderTime> orderTime, int position)
    {
        String serverKey = context.getResources().getString(R.string.google_direction_api_key);

        GoogleDirection.withServerKey(serverKey)
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        if(direction.isOK()) {
                            // Do something
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            //Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            //String distance = distanceInfo.getText();
                            String duration = durationInfo.getText();
                            Log.e("CheckDuration", "onDirectionSuccess: "+duration);
                            separated = duration.split(" ");

                            if(Integer.parseInt(separated[0]) <= 5)
                            {
                                if (orderTime.get(position).getNotify())
                                {
                                    // TODO: 15-07-2020 Call Api
                                    OrderRepository orderRepository = new OrderRepository(context);
                                    User user = UtilityKt.getUserFromPreference(context);
                                    orderRepository.pushNotifications(user.getId(), orderTime.get(position).getRequest_id(), 4, user.getUsername() + " is arriving in 5 mins.").observeForever(new Observer<Result<String>>() {
                                        @Override
                                        public void onChanged(Result<String> stringResult) {
                                            if (stringResult.getStatus()==Result.Status.SUCCESS)
                                            {
                                                orderTime.get(position).setNotify(false);

                                                //SharedPrefsManager.newInstance(context).putString(Preference.ORDERTIMING,Gson().toJson(orderTimeArrayList));
                                                UtilityKt.addOrderTimeArrayToPreference(context,orderTime);
                                            }
                                        }
                                    });
                                }

                            }

                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });
    }
}
