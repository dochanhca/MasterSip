package jp.newbees.mastersip.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by thanglh on 11/21/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    public static final String FROM_USER = "FROM_USER";
    public static final String PUSH_TYPE = "PUSH_TYPE";
    private static boolean showMissedCallPush = true;

    public static final int PUSH_MISS_CALL = 1;
    public static final int PUSH_CHAT = 2;
    public static final int PUSH_FOLLOWED = 3;
    public static final int PUSH_USER_ONLINE = 4;
    public static final int PUSH_FROM_ADMIN = 5;


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
        Logger.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.e(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                Map<String, Object> data = FirebaseUtils.parseData(remoteMessage.getData());
                handlePushMessage(data);

            } catch (JSONException e) {
                Logger.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null) {
            Logger.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.

    private void handlePushMessage(Map<String, Object> data) {
        FCMPushItem fcmPushItem = (FCMPushItem) data.get(Constant.JSON.FCM_PUSH_ITEM);
        String message = fcmPushItem.getMessage();
        Logger.e(TAG, "Push from server: " + fcmPushItem.getCategory());
        switch (fcmPushItem.getCategory()) {
            case FCMPushItem.CATEGORY.INCOMING_CALL:
                handleIncomingCallMessage(data);
                break;
            case FCMPushItem.CATEGORY.MISS_CALL:
                if (showMissedCallPush) {
                    handleMissCallMessage((UserItem) data.get(Constant.JSON.CALLER));
                }
                break;
            case FCMPushItem.CATEGORY.CHAT_TEXT:
                handleNotification((UserItem) data.get(Constant.JSON.USER), message, PUSH_CHAT);
                break;
            case FCMPushItem.CATEGORY.FOLLOW:
                message = String.format(getString(R.string.mess_be_followed), fcmPushItem.getUserName());
                handleNotification(null, message, PUSH_FOLLOWED);
                break;
            case FCMPushItem.CATEGORY.USER_ONLINE:
                message = String.format(getString(R.string.mess_be_uerOnl), fcmPushItem.getMessage());
                handleNotification(null, message, PUSH_USER_ONLINE);
                break;
            case FCMPushItem.CATEGORY.NOTIFY:
                handleNotification(null, message, PUSH_FROM_ADMIN);
                break;
            default:
                sendNotification(message);
                break;
        }
    }

    private void handleMissCallMessage(UserItem caller) {
        String message = caller.getUsername() + getString(R.string.push_missed_call);
        handleNotification(caller, message, PUSH_MISS_CALL);
    }

    private void handleNotification(@Nullable UserItem userItem, String message, int type) {
        if (!MyLifecycleHandler.getInstance().isApplicationVisible()) {
            Intent intent = new Intent(this, SplashActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(PUSH_TYPE, type);
            if (type == PUSH_CHAT || type == PUSH_MISS_CALL) {
                bundle.putParcelable(FROM_USER, userItem);
            }
            intent.putExtras(bundle);
            sendNotification(message, intent);
        }
    }

    private void handleIncomingCallMessage(Map<String, Object> data) {
        if (MyLifecycleHandler.getInstance().isApplicationVisible()) {
            showMissedCallPush = false;
        } else {
            showMissedCallPush = true;
        }
        handleIncomingCall((String) data.get(Constant.JSON.CALL_ID));
    }

    private void handleIncomingCall(String callId) {
        ConfigManager.getInstance().setCallId(callId);
        if (!LinphoneService.isRunning()) {
            LinphoneService.startLinphone(getApplicationContext(), LinphoneService.START_FROM_PUSH_NOTIFICATION);
        }
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, SplashActivity.class);
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

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.push_title))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= 21) {
            notificationBuilder.setVibrate(new long[0])
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId, notificationBuilder.build());
    }
}
