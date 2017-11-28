package com.dke.grimtrigger.grimtrigger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationModule implements IModule {

    private Context mContext;

    public NotificationModule(Context context) {
        mContext = context;
    }

    public void showNotification(String msg) {
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class), 0);
        Resources r = mContext.getResources();

        // set system alert within app itself
        // Context.systemAlert.setText(r.getString(R.string.notification_text));

        // send notification
        Notification notification = new NotificationCompat.Builder(mContext)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(r.getString(R.string.notification_title))
                // .setContentText(r.getString(R.string.notification_text))
                .setContentText(msg) // custom message in notification
                .setContentIntent(pi)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    public void Initialize() {
    }

    @Override
    public void Finalize() {
    }
}
