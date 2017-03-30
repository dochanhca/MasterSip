package jp.newbees.mastersip.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;

import java.util.Map;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.FCMPushItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.SplashActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by thanglh on 11/21/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    public static final String FROM_USER = "FROM_USER";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Logger.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                Map<String, Object> data = FirebaseUtils.parseData(remoteMessage.getData());
                handlePushMessage(data);
                Logger.e(TAG, data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

//        if (!LinphoneHandler.isRunning()) {
//            Logger.e(TAG,"Linphone is not running, start Linphone service");
//            startLinphone();
//        }
    }

    private void handlePushMessage(Map<String, Object> data) {
        FCMPushItem fcmItem = (FCMPushItem) data.get(Constant.JSON.FCM_PUSH_ITEM);
        if (!MyLifecycleHandler.isApplicationVisible() &&
                fcmItem.getCategory().equals(FCMPushItem.CATEGORY.CHAT_TEXT)) {
            sendNotificationForChat(fcmItem.getMessage(), (UserItem) data.get(Constant.JSON.USER));
        }
    }

    private void startLinphone() {
        Intent intent = new Intent(getApplicationContext(), LinphoneService.class);
        getApplicationContext().startService(intent);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        sendNotification(message, intent);

    }

    private void sendNotificationForChat(String message, UserItem fromUser) {
        Intent intent = new Intent(this, SplashActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(FROM_USER, fromUser);
        intent.putExtras(bundle);
        sendNotification(message, intent);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, Intent intent) {
        int messageId = (int) System.currentTimeMillis();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, messageId, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.push_title))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(messageId, notificationBuilder.build());
    }
}
