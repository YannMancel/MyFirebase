package com.mancel.yann.myfirebase.models.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mancel.yann.myfirebase.R;
import com.mancel.yann.myfirebase.controllers.activities.MainActivity;

/**
 * Created by Yann MANCEL on 05/06/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.models.notifications
 */
public class NotificationsService extends FirebaseMessagingService {

    // FIELDS --------------------------------------------------------------------------------------

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "MYFIREBASE";

    // METHODS -------------------------------------------------------------------------------------

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Checks if the notification existed
        if (remoteMessage.getNotification() != null) {
            // Retrieves the message
            final String message = remoteMessage.getNotification().getBody();

            // Sends a notification message
            this.sendVisualNotification(message);
        }
    }

    /**
     * Sends a notification message
     * @param messageBody a String object that contains the message body
     */
    private void sendVisualNotification(final String messageBody) {
        // Creates an Intent object that will be shown when the user will click on the notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Creates a style for the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        inboxStyle.addLine(messageBody);

        // Creates the channel Id (Android 8)
        final String channelId = getString(R.string.default_notification_channel_id);

        // Creates the NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_image_notification)
               .setContentTitle(getString(R.string.app_name))
               .setContentText(getString(R.string.notification_title))
               .setAutoCancel(true)
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .setContentIntent(pendingIntent)
               .setStyle(inboxStyle)
               .setPriority(NotificationManager.IMPORTANCE_HIGH);

        // Creates the NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Support version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creates the NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                                                                              getString(R.string.title_into_notification),
                                                                              NotificationManager.IMPORTANCE_HIGH);

            // Creates a short description of the channel
            notificationChannel.setDescription(getString(R.string.channel_description));

            // Binds the channel to the NotificationManager
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Shows the notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
    }
}