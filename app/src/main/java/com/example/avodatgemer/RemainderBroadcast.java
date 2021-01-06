package com.example.avodatgemer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.POWER_SERVICE;

public class RemainderBroadcast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyLemubit")
                .setSmallIcon(R.drawable.man)
                .setContentTitle("Reminder")
                .setContentText("Time for today's picture!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        Intent notificationIntent = new Intent(context,ProcessesActivity.class);
        PendingIntent conPendingIntent = PendingIntent.getActivity(context,0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(conPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());
    }

}
