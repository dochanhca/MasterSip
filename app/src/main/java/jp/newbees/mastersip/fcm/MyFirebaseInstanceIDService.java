package jp.newbees.mastersip.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thanglh on 11/21/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Logger.e(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
//        TaskSendTokenToServer taskSendTokenToServer = new TaskSendTokenToServer(getApplicationContext(), refreshedToken);
//        taskSendTokenToServer.request(new Response.Listener<Void>() {
//            @Override
//            public void onResponse(Void response) {
//                SmartLog.logE(TAG, " send Refreshed Token to sever successful");
//            }
//        }, new TaskNetworkBase.ErrorListener() {
//            @Override
//            public void onErrorListener(int errorCode, String errorMessage) {
//                SmartLog.logE(TAG, errorCode + " : " + errorMessage);
//            }
//        });
    }
}
