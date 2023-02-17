package com.admin.ozieats_app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.admin.ozieats_app.R;
import com.admin.ozieats_app.ui.splash.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String tokenaa = FirebaseInstanceId.getInstance().getToken();
                        Log.e("tokenaa", "This is your Firebase token=======>" + tokenaa);

                        Context ctx = getApplicationContext();
                        getSharedPreferences("toekn_pref", MODE_PRIVATE).edit().putString(Preference.PREF_TOKEN, tokenaa).apply();

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e("My Token", token);
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        int type = getSharedPreferences("login_info", MODE_PRIVATE).getInt("usertype", -1);

        Map<String, String> data = remoteMessage.getData();
        String body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : "Not Found";
        String title = remoteMessage.getNotification().getTitle();

        Intent intent;
        if (type == 2) {
            intent = new Intent(getApplicationContext(), SplashActivity.class);
        } else
            intent = new Intent(getApplicationContext(), SplashActivity.class);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 101, intent, 0);

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        getApplicationContext(), "222")
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setLargeIcon(((BitmapDrawable) getDrawable(R.drawable.logo)).getBitmap())
                        .setSmallIcon(R.drawable.logo)
                        //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                        .setContentText(body)
                        .setSmallIcon(R.drawable.logo)
                        .setContentIntent(pi);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        if (nm != null) {
            nm.notify(101, builder.build());
        }
    }
}

