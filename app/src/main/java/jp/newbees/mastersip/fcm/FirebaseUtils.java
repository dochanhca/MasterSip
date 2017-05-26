package jp.newbees.mastersip.fcm;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jp.newbees.mastersip.model.FCMPushItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 3/29/17.
 */

public class FirebaseUtils {

    private FirebaseUtils() {
        // Prevent instance object
    }

    public static Map<String, Object> parseData(Map<String, String> data) throws JSONException, NullPointerException {

        JSONObject jAps = new JSONObject(data.get(Constant.FCM.APS));
        FCMPushItem fcmPushItem = parsePushItem(jAps);

        switch (fcmPushItem.getCategory()) {
            case FCMPushItem.CATEGORY.CHAT_TEXT:
                return parseDataForChatMessage(data, fcmPushItem);
            case FCMPushItem.CATEGORY.FOLLOW:
                return parseDataForFollowMessage(data, fcmPushItem);
            case FCMPushItem.CATEGORY.USER_ONLINE:
                return parseDataForUserOnlMessage(data, fcmPushItem);
            case FCMPushItem.CATEGORY.INCOMING_CALL:
            case FCMPushItem.CATEGORY.MISS_CALL:
                return parseDataForCallMessage(data, fcmPushItem);
            default:
                return null;
        }
    }

    private static Map<String, Object> parseDataForFollowMessage(Map<String, String> data,
                                                                 FCMPushItem fcmPushItem) throws JSONException {
        Map<String, Object> result = new HashMap<>();

        result.put(Constant.JSON.FCM_PUSH_ITEM, fcmPushItem);
        result.put(Constant.JSON.RECEIVER_STATUS, data.get(Constant.JSON.RECEIVER_STATUS));
        return result;
    }

    private static Map<String, Object> parseDataForUserOnlMessage(Map<String, String> data,
                                                                  FCMPushItem fcmPushItem) throws JSONException {
        Map<String, Object> result = new HashMap<>();

        result.put(Constant.JSON.FCM_PUSH_ITEM, fcmPushItem);
        result.put(Constant.JSON.RECEIVER_STATUS, data.get(Constant.JSON.RECEIVER_STATUS));
        return result;
    }

    private static Map<String, Object> parseDataForCallMessage(Map<String, String> data, FCMPushItem fcmPushItem) throws JSONException {
        Map<String, Object> result = new HashMap<>();
        UserItem caller = parseCaller(new JSONObject(data.get(Constant.JSON.CALLER)));
        String callId = data.get(Constant.JSON.CALL_ID);

        result.put(Constant.JSON.FCM_PUSH_ITEM, fcmPushItem);
        result.put(Constant.JSON.CALLER, caller);
        result.put(Constant.JSON.CALL_ID, callId);
        return result;
    }

    private static Map<String, Object> parseDataForChatMessage(Map<String, String> data, FCMPushItem fcmPushItem)
            throws JSONException, NullPointerException {
        Map<String, Object> result = new HashMap<>();

        String roomId = data.get(Constant.JSON.ROOM_ID);
        String extension = data.get(Constant.JSON.EXTENSION);
        String userId = data.get(Constant.JSON.USER_ID);

        SipItem sipItem = new SipItem(extension);
        UserItem userItem = new UserItem();
        userItem.setUserId(userId);
        userItem.setSipItem(sipItem);

        result.put(Constant.JSON.FCM_PUSH_ITEM, fcmPushItem);
        result.put(Constant.JSON.USER, userItem);
        result.put(Constant.JSON.ROOM_ID, roomId);

        return result;
    }

    private static UserItem parseCaller(JSONObject jCaller) throws JSONException {
        UserItem userItem = new UserItem();

        if (jCaller.has(Constant.JSON.USER_ID)) {
            userItem.setUserId(jCaller.getString(Constant.JSON.USER_ID));
        }
        userItem.setUsername(jCaller.getString(Constant.FCM.HANDLE_NAME));

        SipItem sipItem = new SipItem();
        sipItem.setExtension(jCaller.getString(Constant.FCM.EXTENSION_ID));
        userItem.setSipItem(sipItem);

        return userItem;
    }

    private static FCMPushItem parsePushItem(JSONObject jsonObject) throws JSONException {
        FCMPushItem fcmPushItem = new FCMPushItem();

        if (jsonObject.has(Constant.FCM.BADGE)) {
            fcmPushItem.setBadge(jsonObject.getString(Constant.FCM.BADGE));
        }

        fcmPushItem.setSound(jsonObject.getString(Constant.FCM.SOUND));
        fcmPushItem.setCategory(jsonObject.getString(Constant.FCM.CATEGORY));

        JSONObject jAlert = jsonObject.getJSONObject(Constant.FCM.ALERT);
        fcmPushItem.setUserName(jAlert.getJSONArray(Constant.FCM.LOC_ARGS).get(0).toString());
        fcmPushItem.setMessage(jAlert.getString(Constant.FCM.LOC_KEY));

        return fcmPushItem;
    }
}
