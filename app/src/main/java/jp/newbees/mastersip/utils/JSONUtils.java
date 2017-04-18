package jp.newbees.mastersip.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.CallChatItem;
import jp.newbees.mastersip.model.CallLogItem;
import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.model.DeletedChatItem;
import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.model.FollowItem;
import jp.newbees.mastersip.model.FootprintItem;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.GiftChatItem;
import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.model.HistoryCallItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.presenter.TopPresenter;

import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_DELETED;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_GIFT;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_IMAGE;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_TEXT;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VIDEO_CALL;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VOICE;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VOICE_CALL;
import static jp.newbees.mastersip.model.BaseChatItem.RoomType.ROOM_CHAT_CHAT;
import static jp.newbees.mastersip.utils.Constant.JSON.CALL_ID;

/**
 * Created by vietbq on 12/20/16.
 */
public class JSONUtils {

    private JSONUtils() {
        //Prevent init object
    }

    public static UserItem parseUserDetail(JSONObject data) throws JSONException {
        JSONObject jUser = data.getJSONObject(Constant.JSON.USER);
        UserItem userItem = new UserItem();
        parseBasicUserInfo(userItem, jUser);

        if (!jUser.isNull(Constant.JSON.AVATAR)) {
            JSONObject jAvatar = jUser.getJSONObject(Constant.JSON.AVATAR);

            ImageItem avatar = new ImageItem();
            avatar.setImageId(getInt(jAvatar, Constant.JSON.AVATAR_ID));
            avatar.setOriginUrl(getString(jAvatar, Constant.JSON.PATH));
            avatar.setThumbUrl(getString(jAvatar, Constant.JSON.THUMB));
            userItem.setAvatarItem(avatar);
        }

        if (jUser.has(Constant.JSON.SETTING)) {
            JSONObject jSetting = jUser.getJSONObject(Constant.JSON.SETTING);
            SettingItem settingItem = parseSettings(jSetting);
            userItem.setSettings(settingItem);
        }

        return userItem;
    }

    public static List<UserItem> parseUsers(JSONObject data) throws JSONException {
        JSONArray jArray = data.getJSONArray(Constant.JSON.USERS);
        ArrayList<UserItem> result = new ArrayList<>();
        for (int i = 0, n = jArray.length(); i < n; i++) {
            JSONObject jUser = jArray.getJSONObject(i);
            UserItem userItem = new UserItem();
            parseBasicUserInfo(userItem, jUser);

            if (jUser.has(Constant.JSON.SETTING)) {
                JSONObject jSetting = jUser.getJSONObject(Constant.JSON.SETTING);
                SettingItem settingItem = parseSettings(jSetting);
                userItem.setSettings(settingItem);
            }

            ImageItem avatar = new ImageItem();
            avatar.setOriginUrl(jUser.getString(Constant.JSON.AVATAR));
            userItem.setAvatarItem(avatar);
            result.add(userItem);
        }
        return result;
    }

    public static SettingItem parseSettings(JSONObject jSetting) throws JSONException {
        SettingItem settingItem = new SettingItem();

        if (jSetting.has(Constant.JSON.CHAT)) {
            settingItem.setChat(jSetting.getInt(Constant.JSON.CHAT));
        } else if (jSetting.has(Constant.JSON.CHAT_SET)) {
            settingItem.setChat(jSetting.getInt(Constant.JSON.CHAT_SET));
        }

        if (jSetting.has(Constant.JSON.VOICE_CALL)) {
            settingItem.setVoiceCall(jSetting.getInt(Constant.JSON.VOICE_CALL));
        } else if (jSetting.has(Constant.JSON.VOICE_CALL_SET)) {
            settingItem.setVoiceCall(jSetting.getInt(Constant.JSON.VOICE_CALL_SET));
        }

        if (jSetting.has(Constant.JSON.VIDEO_CALL)) {
            settingItem.setVideoCall(jSetting.getInt(Constant.JSON.VIDEO_CALL));
        } else if (jSetting.has(Constant.JSON.VIDEO_CALL_SET)) {
            settingItem.setVideoCall(jSetting.getInt(Constant.JSON.VIDEO_CALL_SET));
        }

        return settingItem;
    }

    private static void parseBasicUserInfo(UserItem userItem, JSONObject jUser) throws JSONException {
        userItem.setUsername(jUser.getString(Constant.JSON.HANDLE_NAME));
        userItem.setUserId(jUser.getString(Constant.JSON.ID));

        if (!jUser.isNull(Constant.JSON.SLOGAN)) {
            userItem.setMemo(jUser.getString(Constant.JSON.SLOGAN));
        }

        SipItem sipItem = new SipItem();
        sipItem.setExtension(jUser.getString(Constant.JSON.EXTENSION));
        userItem.setSipItem(sipItem);

        if (jUser.has(Constant.JSON.RELATIONS)) {
            JSONObject jRelationship = jUser.getJSONObject(Constant.JSON.RELATIONS);
            RelationshipItem relationshipItem = parseRelationship(jRelationship);
            userItem.setRelationshipItem(relationshipItem);
        }

        String birthDay = jUser.getString(Constant.JSON.BIRTHDAY);
        userItem.setDateOfBirth(birthDay);

        SelectionItem location = new SelectionItem();
        JSONObject jProvince = jUser.getJSONObject(Constant.JSON.PROVINCE);

        if (!jProvince.isNull(Constant.JSON.USER_PROVINCE_ID)) {
            location.setId(jProvince.getInt(Constant.JSON.USER_PROVINCE_ID));
        }

        if (!jProvince.isNull(Constant.JSON.PROVINCE_NAME)) {
            location.setTitle(jProvince.getString(Constant.JSON.PROVINCE_NAME));
        }

        userItem.setLocation(location);

        SelectionItem job = new SelectionItem();
        job.setTitle(JSONUtils.getString(jUser, Constant.JSON.JOB_NAME));
        userItem.setJobItem(job);

        String lastLogin = jUser.getString(Constant.JSON.LAST_LOGIN);
        userItem.setLastLogin(lastLogin);

        int status = getInt(jUser, Constant.JSON.STATUS);
        userItem.setStatus(status);

        userItem.setGender(getInteractionUserGender());
        if (jUser.has(Constant.JSON.EXTEND_INFO)) {
            JSONObject jExtendInfo = jUser.getJSONObject(Constant.JSON.EXTEND_INFO);
            if (userItem.getGender() == UserItem.FEMALE && jExtendInfo.length() > 0) {
                String charmPoint = jExtendInfo.getString(Constant.JSON.CHARM_POINT);
                userItem.setCharmingPoint(charmPoint);
                String freeTime = jExtendInfo.getString(Constant.JSON.FREE_TIME);
                SelectionItem availableTime = new SelectionItem();
                availableTime.setTitle(freeTime);
                userItem.setAvailableTimeItem(availableTime);
                String typeBoy = jExtendInfo.getString(Constant.JSON.TYPE_BOY);
                userItem.setTypeBoy(typeBoy);
                String favoriteType = jExtendInfo.getString(Constant.JSON.FAVORITE_TYPE);
                SelectionItem typeGirl = new SelectionItem();
                typeGirl.setTitle(favoriteType);
                userItem.setTypeGirl(typeGirl);
            }
        }
    }


    private static String getString(JSONObject jsonObject, String name) throws JSONException {
        if (jsonObject.has(name)) {
            return jsonObject.getString(name);
        } else {
            return "";
        }
    }

    public static final RelationshipItem parseRelationship(JSONObject jsonObject) throws JSONException {
        RelationshipItem relationshipItem = new RelationshipItem();
        if (jsonObject.has(Constant.JSON.FOLLOWED)) {
            int followed = jsonObject.getInt(Constant.JSON.FOLLOWED);
            relationshipItem.setFollowed(followed);
        }

        if (jsonObject.has(Constant.JSON.ONLINE_NOTIFICATION)) {
            int isNotification = jsonObject.getInt(Constant.JSON.ONLINE_NOTIFICATION);
            relationshipItem.setIsNotification(isNotification);
        }
        return relationshipItem;
    }

    /**
     * @param jsonObject
     * @param name
     * @return Default value is -1
     * @throws JSONException
     */
    private static final int getInt(JSONObject jsonObject, String name) throws JSONException {
        if (jsonObject.has(name)) {
            return jsonObject.getInt(name);
        } else {
            return -1;
        }
    }

    public static final BaseChatItem parseChatItem(JSONObject jData, UserItem me) throws JSONException {
        BaseChatItem chatItem = new BaseChatItem();
        int type = jData.getInt(Constant.JSON.TYPE);
        switch (type) {
            case CHAT_DELETED:
                chatItem = parseDeletedChatItem(jData, me);
                break;
            case CHAT_VOICE:
                break;
            case CHAT_TEXT:
                chatItem = parseTextChatItem(jData);
                break;
            case CHAT_IMAGE:
                chatItem = parseImageChatItem(jData);
                break;
            case CHAT_GIFT:
                chatItem = parseGiftChatItem(jData);
                break;
            case CHAT_VOICE_CALL:
                chatItem = parseVoiceCallChatItem(jData, me);
                break;
            case CHAT_VIDEO_CALL:
                chatItem = parseVideoCallChatItem(jData, me);
                break;
            case CHAT_VIDEO_CHAT_CALL:
                chatItem = parseVideoChatCallChatItem(jData, me);
                break;
            default:
                break;
        }

        return chatItem;
    }

    public static UserItem parseSender(JSONObject jSender) throws JSONException {
        UserItem userItem = new UserItem();
        userItem.setUserId(jSender.getString(Constant.JSON.USER_ID));
        userItem.setGender(jSender.getInt(Constant.JSON.GENDER));
        userItem.setUsername(jSender.getString(Constant.JSON.HANDLE_NAME));

        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(jSender.getString(Constant.JSON.AVATAR));
        imageItem.setThumbUrl(jSender.getString(Constant.JSON.AVATAR));
        userItem.setAvatarItem(imageItem);

        SipItem sipItem = new SipItem();
        sipItem.setExtension(jSender.getString(Constant.JSON.EXTENSION));
        userItem.setSipItem(sipItem);
        return userItem;
    }

    public static GiftItem parseGift(JSONObject jGift) throws JSONException {
        String giftName = jGift.getString(Constant.JSON.NAME);
        int point = jGift.getInt(Constant.JSON.POINT);

        GiftItem giftItem = new GiftItem();
        giftItem.setGiftId(jGift.getInt(Constant.JSON.ID));
        giftItem.setName(giftName);
        giftItem.setPrice(point);

        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(jGift.getString(Constant.JSON.IMAGE));
        giftItem.setGiftImage(imageItem);

        return giftItem;
    }

    public static BaseChatItem parseGiftChatItem(JSONObject jData) throws JSONException {
        GiftChatItem giftChatItem = new GiftChatItem();
        JSONObject jGift = jData.getJSONObject(Constant.JSON.GIFT);
        giftChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));
        giftChatItem.setOwner(parseSender(jData.getJSONObject(Constant.JSON.SENDER)));

        GiftItem giftItem = parseGift(jGift);
        String content = jGift.getString(Constant.JSON.CONTEXT);

        giftChatItem.setContent(content);
        giftChatItem.setGiftItem(giftItem);
        giftChatItem.setFullDate(jData.getString(Constant.JSON.DATE));
        giftChatItem.setShortDate(DateTimeUtils.getShortTime(giftChatItem.getFullDate()));
        giftChatItem.setChatType(CHAT_GIFT);

        return giftChatItem;
    }

    private static BaseChatItem parseVideoChatCallChatItem(JSONObject jData, UserItem me) throws JSONException {
        CallChatItem callChatItem = (CallChatItem) parseCallChatItem(jData, me);
        callChatItem.setChatType(CHAT_VIDEO_CHAT_CALL);
        return callChatItem;
    }


    private static BaseChatItem parseVideoCallChatItem(JSONObject jData, UserItem me) throws JSONException {
        CallChatItem callChatItem = (CallChatItem) parseCallChatItem(jData, me);
        callChatItem.setChatType(CHAT_VIDEO_CALL);
        return callChatItem;
    }

    private static BaseChatItem parseVoiceCallChatItem(JSONObject jData, UserItem me) throws JSONException {
        CallChatItem callChatItem = (CallChatItem) parseCallChatItem(jData, me);
        callChatItem.setChatType(CHAT_VOICE_CALL);
        return callChatItem;
    }

    private static BaseChatItem parseCallChatItem(JSONObject jData, UserItem me) throws JSONException {
        CallChatItem callChatItem = new CallChatItem();
        JSONObject jCall = jData.getJSONObject(Constant.JSON.CALL);
        callChatItem.setCallType(jCall.getInt(Constant.JSON.KIND_CALL));
        if (jCall.has(Constant.JSON.DURATION)) {
            callChatItem.setDuration(jCall.getString(Constant.JSON.DURATION));
        } else {
            callChatItem.setDuration("");
        }

        if (jCall.getString(Constant.JSON.EXTENSION_FROM).equalsIgnoreCase(me.getSipItem().getExtension())) {
            callChatItem.setOwner(me);
        } else {
            JSONObject jSender = jData.getJSONObject(Constant.JSON.SENDER);
            UserItem userItem = new UserItem();
            SipItem sipItem = new SipItem();

            sipItem.setExtension(jSender.getString(Constant.JSON.EXTENSION));
            userItem.setSipItem(sipItem);
            userItem.setUserId(jSender.getString(Constant.JSON.USER_ID));
            userItem.setUsername(jSender.getString(Constant.JSON.HANDLE_NAME));

            if (jSender.optBoolean(Constant.JSON.AVATAR)) {
                ImageItem myAvatar = new ImageItem();
                myAvatar.setThumbUrl(jSender.getString(Constant.JSON.AVATAR));
                myAvatar.setOriginUrl(jSender.getString(Constant.JSON.AVATAR));
                userItem.setAvatarItem(myAvatar);
            }

            callChatItem.setOwner(userItem);
        }

        callChatItem.setFullDate(jData.getString(Constant.JSON.DATE));
        callChatItem.setShortDate(DateTimeUtils.getShortTime(callChatItem.getFullDate()));

        return callChatItem;
    }

    private static BaseChatItem parseImageChatItem(JSONObject jData) throws JSONException {
        JSONObject jSender = jData.getJSONObject(Constant.JSON.SENDER);
        String extensionSender = jSender.getString(Constant.JSON.EXTENSION);

        ImageChatItem imageChatItem = new ImageChatItem();

        int roomType = jData.getInt(Constant.JSON.ROOM_TYPE);
        imageChatItem.setRoomType(roomType);
        imageChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));
        imageChatItem.setRoomId(jData.getInt(Constant.JSON.ROOM_ID));
        UserItem userItem = new UserItem();
        SipItem sipItem = new SipItem(extensionSender);
        userItem.setSipItem(sipItem);

        if (jSender.optBoolean(Constant.JSON.AVATAR)) {
            ImageItem myAvatar = new ImageItem();
            myAvatar.setThumbUrl(jSender.getString(Constant.JSON.AVATAR));
            myAvatar.setOriginUrl(jSender.getString(Constant.JSON.AVATAR));
            userItem.setAvatarItem(myAvatar);
        }

        imageChatItem.setOwner(userItem);
        imageChatItem.setFullDate(jData.getString(Constant.JSON.DATE));
        imageChatItem.setShortDate(DateTimeUtils.getShortTime(imageChatItem.getFullDate()));

        imageChatItem.setImageItem(parseChatImage(jData));
        imageChatItem.setChatType(CHAT_IMAGE);

        return imageChatItem;
    }

    private static ImageItem parseChatImage(JSONObject jData) throws JSONException {
        JSONObject jImage = jData.getJSONObject(Constant.JSON.IMAGE);
        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(jImage.getString(Constant.JSON.PATH));
        imageItem.setThumbUrl(jImage.getString(Constant.JSON.THUMB));
        imageItem.setWidth(jImage.getInt(Constant.JSON.WIDTH));
        imageItem.setHeight(jImage.getInt(Constant.JSON.HEIGHT));
        return imageItem;
    }

    private static final DeletedChatItem parseDeletedChatItem(JSONObject jData, UserItem sender) throws JSONException {
        DeletedChatItem deletedChatItem = new DeletedChatItem();
        JSONObject jDeletedItem = jData.getJSONObject(Constant.JSON.DELETED);
        String content = jDeletedItem.getString(Constant.JSON.CONTENT);

        deletedChatItem.setOwner(sender);
        deletedChatItem.setMessage(content);
        deletedChatItem.setRoomType(ROOM_CHAT_CHAT);
        deletedChatItem.setChatType(CHAT_DELETED);
        return deletedChatItem;
    }

    private static final TextChatItem parseTextChatItem(JSONObject jData) throws JSONException {
        JSONObject jText = jData.getJSONObject(Constant.JSON.TEXT);
        JSONObject jSender = jData.getJSONObject(Constant.JSON.SENDER);

        String extensionSender = jSender.getString(Constant.JSON.EXTENSION);
        String content = jText.getString(Constant.JSON.CONTENT);

        TextChatItem textChatItem = new TextChatItem(content);

        int roomType = jText.getInt(Constant.JSON.ROOM_TYPE);
        textChatItem.setRoomType(roomType);
        textChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));
        textChatItem.setRoomId(jData.getInt(Constant.JSON.ROOM_ID));
        UserItem userItem = new UserItem();
        SipItem sipItem = new SipItem(extensionSender);

        ImageItem imageItem = new ImageItem();
        imageItem.setThumbUrl(jSender.getString(Constant.JSON.AVATAR));
        imageItem.setOriginUrl(jSender.getString(Constant.JSON.AVATAR));
        userItem.setAvatarItem(imageItem);
        userItem.setSipItem(sipItem);

        textChatItem.setOwner(userItem);
        textChatItem.setFullDate(jData.getString(Constant.JSON.DATE));
        textChatItem.setShortDate(DateTimeUtils.getShortTime(textChatItem.getFullDate()));
        textChatItem.setChatType(CHAT_TEXT);
        return textChatItem;
    }

    public static String genRawToChangeMessageState(BaseChatItem baseChatItem, String fromExtension) throws JSONException {
        JSONObject jData = new JSONObject();
        jData.put(Constant.JSON.ACTION, Constant.SOCKET.ACTION_CHANGE_MESSAGE_STATE);
        jData.put(Constant.JSON.MESSAGE, fromExtension);
        JSONObject jResponse = new JSONObject();
        jResponse.put(Constant.JSON.ROOM_ID, baseChatItem.getRoomId());
        jResponse.put(Constant.JSON.ROOM_TYPE, baseChatItem.getRoomType());
        jResponse.put(Constant.JSON.MESSAGE_ID, baseChatItem.getMessageId());
        jResponse.put(Constant.JSON.kFromExtension, fromExtension);
        jResponse.put(Constant.JSON.STATUS, BaseChatItem.MessageState.STT_READ);
        jData.put(Constant.JSON.RESPONSE, jResponse);
        return jData.toString();
    }

    public static PacketItem parsePacketItem(String raw) throws JSONException {
        JSONObject jData = new JSONObject(raw);
        String action = jData.getString(Constant.JSON.ACTION);
        String message = "";
        if (jData.has(Constant.JSON.MESSAGE)) {
            message = jData.getString(Constant.JSON.MESSAGE);
        }
        String response = "";
        if (!action.equals(Constant.SOCKET.ACTION_ADMIN_HANG_UP)) {
            response = jData.getJSONObject(Constant.JSON.RESPONSE).toString();
        }
        PacketItem packetItem = new PacketItem(action, message, response);
        return packetItem;
    }

    public static BaseChatItem parseDateOnUpdateMessageState(JSONObject jData) throws JSONException {
        BaseChatItem baseChatItem = new BaseChatItem();
        baseChatItem.setMessageState(jData.getInt(Constant.JSON.STATUS));
        baseChatItem.setRoomId(jData.getInt(Constant.JSON.ROOM_ID));
        baseChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));
        baseChatItem.setRoomType(jData.getInt(Constant.JSON.ROOM_TYPE));
        return baseChatItem;
    }

    public static GalleryItem parseGallery(JSONObject jData) throws JSONException {
        GalleryItem galleryItem = new GalleryItem();
        galleryItem.setNextId(jData.getString(Constant.JSON.NEXT_ID));
        galleryItem.setTotalImage(jData.getInt(Constant.JSON.TOTAL_COUNT));

        JSONArray jsonImages = jData.getJSONArray(Constant.JSON.LIST_IMAGE);
        List<ImageItem> imageItems = new ArrayList<>();
        for (int i = 0; i < jsonImages.length(); i++) {
            JSONObject jImage = jsonImages.getJSONObject(i);
            ImageItem imageItem = new ImageItem();
            imageItem.setImageId(jImage.getInt(Constant.JSON.IMAGE_ID));
            imageItem.setOriginUrl(jImage.getString(Constant.JSON.IMAGE_PATH));
            imageItem.setThumbUrl(jImage.getString(Constant.JSON.IMAGE_PATH_THUMB));
            imageItem.setImageStatus(jImage.getInt(Constant.JSON.IMAGE_STATUS));
            imageItems.add(imageItem);
        }
        galleryItem.setImageItems(imageItems);
        return galleryItem;
    }

    public static UserItem parseMyMenuItem(JSONObject jData) throws JSONException {
        JSONObject jMyInfo = jData.getJSONObject(Constant.JSON.MY_INFO);
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setCoin(jMyInfo.getInt(Constant.JSON.POINT));
        userItem.setUsername(jMyInfo.getString(Constant.JSON.HANDLE_NAME));
        userItem.setEmail((jMyInfo.isNull(Constant.JSON.EMAIL)
                ? "" : jMyInfo.getString(Constant.JSON.EMAIL)));
        if (jMyInfo.has(Constant.JSON.AVATAR)) {
            JSONObject jAvatar = jMyInfo.getJSONObject(Constant.JSON.AVATAR);
            if (jAvatar.length() > 0) {
                ImageItem imageItem = JSONUtils.parseImageItem(jAvatar);
                userItem.setAvatarItem(imageItem);
            } else {
                userItem.setAvatarItem(null);
            }
        } else {
            userItem.setAvatarItem(null);
        }
        return userItem;
    }

    public static ImageItem parseImageItem(JSONObject jAvatar) throws JSONException {
        ImageItem imageItem = new ImageItem();
        int imageId = jAvatar.getInt(Constant.JSON.ID);
        String originPath = jAvatar.getString(Constant.JSON.PATH);
        String thumbnail = jAvatar.getString(Constant.JSON.THUMBNAIL);
        int imageStatus = jAvatar.getInt(Constant.JSON.STATUS);
        imageItem.setImageId(imageId);
        imageItem.setOriginUrl(originPath);
        imageItem.setImageStatus(imageStatus);
        imageItem.setThumbUrl(thumbnail);
        return imageItem;
    }

    public static List<BaseChatItem> parseChatHistory(JSONObject data, Map<String, UserItem> members, Context context) throws JSONException {
        List<BaseChatItem> result = new ArrayList<>();

        JSONArray jListMessages = data.getJSONArray(Constant.JSON.LIST_MESSAGES);
        int sectionFirstPosition = 0;
        for (int i = jListMessages.length() - 1; i >= 0; i--) {
            JSONObject jListMessage = jListMessages.getJSONObject(i);

            result.add(getHeaderChatItem(jListMessage, sectionFirstPosition, context));

            JSONArray jMessages = jListMessage.getJSONArray(Constant.JSON.MESSAGES);
            for (int j = jMessages.length() - 1; j >= 0; j--) {
                JSONObject jMessage = jMessages.getJSONObject(j);
                result.add(getBaseChatItemInHistory(jMessage, sectionFirstPosition, members));
            }
            sectionFirstPosition = result.size();
        }
        return result;
    }

    public static HashMap<String, UserItem> getMembers(JSONArray jsonArray) throws JSONException {
        HashMap<String, UserItem> members = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jMember = jsonArray.getJSONObject(i);
            UserItem userItem = new UserItem();
            userItem.setUserId(jMember.getString(Constant.JSON.ID));
            userItem.setUsername(jMember.getString(Constant.JSON.HANDLE_NAME));

            SipItem sipItem = new SipItem();
            sipItem.setExtension(jMember.getString(Constant.JSON.EXTENSION));
            userItem.setSipItem(sipItem);

            ImageItem imageItem = new ImageItem();
            imageItem.setOriginUrl(jMember.getString(Constant.JSON.AVATAR));
            imageItem.setThumbUrl(imageItem.getOriginUrl());
            userItem.setAvatarItem(imageItem);

            RelationshipItem relationshipItem = new RelationshipItem();
            relationshipItem.setFollowed(jMember.getInt(Constant.JSON.FOLLOWED));
            userItem.setRelationshipItem(relationshipItem);

            SettingItem settingItem = new SettingItem();
            settingItem.setChat(jMember.getInt(Constant.JSON.CHAT_SET));
            settingItem.setVoiceCall(jMember.getInt(Constant.JSON.VOICE_CALL_SET));
            settingItem.setVideoCall(jMember.getInt(Constant.JSON.VIDEO_CALL_SET));
            userItem.setSettings(settingItem);
            userItem.setGender(getInteractionUserGender());

            members.put(userItem.getUserId(), userItem);
        }
        return members;
    }

    private static BaseChatItem getBaseChatItemInHistory(JSONObject jMessage, int sectionFirstPosition,
                                                         Map<String, UserItem> members) throws JSONException {

        BaseChatItem baseChatItem;
        int type = jMessage.getInt(Constant.JSON.TYPE);
        switch (type) {
            case BaseChatItem.ChatType.CHAT_TEXT:
                baseChatItem = new TextChatItem();
                ((TextChatItem) baseChatItem).setMessage(jMessage.getJSONObject(Constant.JSON.TEXT).
                        getString(Constant.JSON.CONTENT));
                break;
            case BaseChatItem.ChatType.CHAT_IMAGE:
                baseChatItem = new ImageChatItem();
                ((ImageChatItem) baseChatItem).setImageItem(parseChatImage(jMessage));
                break;
            case BaseChatItem.ChatType.CHAT_GIFT:
                baseChatItem = parseGiftChatItem(jMessage, members);
                break;
            case BaseChatItem.ChatType.CHAT_VOICE_CALL:
            case BaseChatItem.ChatType.CHAT_VIDEO_CALL:
            case BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL:
                baseChatItem = parseCallChatItemInHistory(jMessage);
                break;
            default:
                baseChatItem = new BaseChatItem();
        }
        baseChatItem.setFullDate(jMessage.getString(Constant.JSON.DATE));
        baseChatItem.setShortDate(DateTimeUtils.getShortTime(baseChatItem.getFullDate()));
        baseChatItem.setChatType(type);
        baseChatItem.setMessageId(jMessage.getInt(Constant.JSON.MESSAGE_ID));
        baseChatItem.setMessageState(jMessage.getInt(Constant.JSON.STATUS));
        baseChatItem.setSectionFirstPosition(sectionFirstPosition);
        String sendId = jMessage.getJSONObject(Constant.JSON.SENDER).getString(Constant.JSON.ID);
        baseChatItem.setOwner(members.get(sendId));
        return baseChatItem;
    }

    private static BaseChatItem parseCallChatItemInHistory(JSONObject jMessage) throws JSONException {
        CallChatItem callChatItem = new CallChatItem();
        JSONObject jCall = jMessage.getJSONObject(Constant.JSON.CALL);
        callChatItem.setCallType(jCall.getInt(Constant.JSON.KIND_CALL));
        callChatItem.setDuration(jCall.getString(Constant.JSON.DURATION));
        return callChatItem;
    }

    private static BaseChatItem parseGiftChatItem(JSONObject jMessage, Map<String, UserItem> members) throws JSONException {
        GiftChatItem giftChatItem = new GiftChatItem();
        String sendId = jMessage.getJSONObject(Constant.JSON.SENDER).getString(Constant.JSON.ID);
        giftChatItem.setOwner(members.get(sendId));

        JSONObject jGift = jMessage.getJSONObject(Constant.JSON.GIFT);
        String content = jGift.getString(Constant.JSON.CONTEXT);

        GiftItem giftItem = parseGift(jGift);

        giftChatItem.setContent(content);
        giftChatItem.setGiftItem(giftItem);
        return giftChatItem;
    }

    private static BaseChatItem getHeaderChatItem(JSONObject jListMessage, int sectionFirstPosition, Context context) throws JSONException {
        BaseChatItem header = new BaseChatItem();
        header.setChatType(BaseChatItem.ChatType.HEADER);
        String strDate = jListMessage.getString(Constant.JSON.DATE);
        header.setFullDate(strDate);

        String displayDate = DateTimeUtils.getHeaderDisplayDateInChatHistory(
                DateTimeUtils.convertStringToDate(strDate, DateTimeUtils.ENGLISH_DATE_FORMAT), context);
        header.setDisplayDate(displayDate);

        header.setSectionFirstPosition(sectionFirstPosition);
        return header;
    }

    public static List<GiftItem> parseGiftsList(JSONArray jGifts) throws JSONException {
        ArrayList<GiftItem> giftItems = new ArrayList<>();
        for (int index = 0, n = jGifts.length(); index < n; index++) {
            JSONObject jGift = jGifts.getJSONObject(index);
            GiftItem giftItem = new GiftItem();
            giftItem.setGiftId(jGift.getInt(Constant.JSON.ID));
            giftItem.setName(jGift.getString(Constant.JSON.NAME));
            ImageItem imageItem = new ImageItem();
            imageItem.setOriginUrl(jGift.getString(Constant.JSON.IMAGE));
            giftItem.setGiftImage(imageItem);
            giftItem.setPrice(jGift.getInt(Constant.JSON.PRICE));
            giftItems.add(giftItem);
        }
        return giftItems;
    }

    public static List<RoomChatItem> parseListRoomChat(JSONArray jsonArray) throws JSONException {
        ArrayList<RoomChatItem> result = new ArrayList<>();
        for (int index = 0, n = jsonArray.length(); index < n; index++) {
            JSONObject jRoomChat = jsonArray.getJSONObject(index);
            JSONObject jInteractionUser = jRoomChat.getJSONObject(Constant.JSON.INTERACTION_USER);
            RoomChatItem roomChatItem = new RoomChatItem();
            String roomId = jRoomChat.getString(Constant.JSON.ROOM_ID);
            roomChatItem.setRoomId(roomId);
            UserItem userItem = new UserItem();
            userItem.setUserId(jInteractionUser.getString(Constant.JSON.ID));
            userItem.setUsername(jInteractionUser.getString(Constant.JSON.HANDLE_NAME));
            SipItem sipItem = new SipItem();
            sipItem.setExtension(jInteractionUser.getString(Constant.JSON.EXTENSION));
            ImageItem avatar = new ImageItem();
            avatar.setOriginUrl(jInteractionUser.getString(Constant.JSON.AVATAR));
            userItem.setAvatarItem(avatar);
            userItem.setSipItem(sipItem);
            roomChatItem.setUserChat(userItem);
            roomChatItem.setLastMessage(jRoomChat.getString(Constant.JSON.LAST_MSG_DESCRIPTION));
            roomChatItem.setLastMessageTimeStamp(jRoomChat.getString(Constant.JSON.LAST_MSG_TIMESTAMP));
            roomChatItem.setNumberMessageUnRead(jRoomChat.getInt(Constant.JSON.ROOM_MESS_UNREAD));
            result.add(roomChatItem);
        }
        return result;
    }

    public static ChattingGalleryItem parseChattingGallery(JSONObject jData) throws JSONException {
        ChattingGalleryItem chattingGalleryItem = new ChattingGalleryItem();
        chattingGalleryItem.setNextId(jData.getString(Constant.JSON.NEXT_ID));
        chattingGalleryItem.setTotalImage(jData.getInt(Constant.JSON.TOTAL));

        JSONArray jsonImages = jData.getJSONArray(Constant.JSON.LIST_PHOTO);
        List<ImageItem> imageItems = new ArrayList<>();
        for (int i = 0; i < jsonImages.length(); i++) {
            JSONObject jImage = jsonImages.getJSONObject(i);
            ImageItem imageItem = new ImageItem();
            imageItem.setMessageId(jImage.getInt(Constant.JSON.MESSAGE_ID));
            imageItem.setOriginUrl(jImage.getString(Constant.JSON.PATH));
            imageItem.setThumbUrl(jImage.getString("thumbail"));
            imageItems.add(imageItem);
        }
        chattingGalleryItem.setImageItems(imageItems);

        // Sender Item
        JSONObject jsonSender = jData.getJSONObject(Constant.JSON.SENDER);
        UserItem sender = new UserItem();
        sender.setUserId(jsonSender.getString(Constant.JSON.ID));
        //extension
        SipItem sipItem = new SipItem();
        sipItem.setExtension(jsonSender.getString(Constant.JSON.EXTENSION));
        sender.setSipItem(sipItem);
        //Avatar
        ImageItem avatar = new ImageItem();
        avatar.setThumbUrl(jsonSender.getString(Constant.JSON.AVATAR));
        avatar.setOriginUrl(jsonSender.getString(Constant.JSON.AVATAR));
        sender.setAvatarItem(avatar);
        chattingGalleryItem.setSender(sender);

        return chattingGalleryItem;
    }

    public static JSONObject genParamsToRegisterEmailBackup(EmailBackupItem emailBackupItem) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.EMAIL, emailBackupItem.getEmail());
        jsonObject.put(Constant.JSON.PASSWORD, emailBackupItem.getPass());
        jsonObject.put(Constant.JSON.PASSWORD_CONFIRMATION, emailBackupItem.getPass());
        jsonObject.put(Constant.JSON.EXTENSION, emailBackupItem.getExtension());

        return jsonObject;
    }

    public static JSONObject genParamsToCheckCode(String code) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.CODE, code);
        return jsonObject;
    }

    public static UserItem parseUserLoginWithEmail(JSONObject jData) throws JSONException {
        UserItem userItem = new UserItem();

        String userID = jData.getString(Constant.JSON.ID);
        String registerToken = jData.getString(Constant.JSON.REGIST_TOKEN);

        userItem.setUserId(userID);
        userItem.setUsername(jData.getString(Constant.JSON.HANDLE_NAME));
        userItem.setGender(jData.getInt(Constant.JSON.GEN));

        // Sip Item
        SipItem sipItem = new SipItem();
        sipItem.setExtension(jData.getString(Constant.JSON.EXTENSION));
        sipItem.setSecret(jData.getString(Constant.JSON.PASSWORD));
        userItem.setSipItem(sipItem);

        ConfigManager.getInstance().saveRegisterToken(registerToken);
        ConfigManager.getInstance().saveAuthId(userID);
        return userItem;
    }

    public static JSONObject genParamsToChangeEmailBackup(EmailBackupItem item) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.EMAIL_OLD, item.getOldEmail());
        jsonObject.put(Constant.JSON.PASSWORD_OLD, item.getOldPass());
        jsonObject.put(Constant.JSON.EMAIL_NEW, item.getEmail());
        jsonObject.put(Constant.JSON.PASSWORD_NEW, item.getPass());
        jsonObject.put(Constant.JSON.PASSWORD_CONFIRMATION, item.getPass());
        jsonObject.put(Constant.JSON.EXTENSION, item.getExtension());
        return jsonObject;
    }

    public static JSONObject genParamsToSendPurchaseResult(String skuID, String transection, TopPresenter.PurchaseStatus purchaseStatus, String createAt) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.JSON.ID_ADDON, skuID);
        jsonObject.put(Constant.JSON.TRANSECTION, transection);
        jsonObject.put(Constant.JSON.TYPE_ID, 9);
        jsonObject.put(Constant.JSON.VERSION, 2);
        jsonObject.put(Constant.JSON.STATUS, purchaseStatus.getValue());
        jsonObject.put(Constant.JSON.CREATE_AT, createAt);
        return jsonObject;
    }

    public static void parseCheckCall(Map<String, Object> result, JSONObject jData) throws JSONException {
        if (!jData.getString(Constant.JSON.MESSAGE_ID).isEmpty()) {
            int messageId = Integer.parseInt(jData.getString(Constant.JSON.MESSAGE_ID));
            result.put(CheckCallTask.MESSAGE_ID, messageId);
        }

        if (jData.has(Constant.JSON.RECEIVER_STATUS)) {
            result.put(CheckCallTask.CALLEE_ONLINE, false);
        } else {
            result.put(CheckCallTask.CALLEE_ONLINE, true);
        }

        if (jData.has(Constant.JSON.MIN_POINT)) {
            int minPoint = jData.getInt(Constant.JSON.MIN_POINT);
            result.put(Constant.JSON.MIN_POINT, minPoint);
        }

        String roomId = jData.optString(Constant.JSON.ROOM_FREE);
        result.put(CheckCallTask.ROOM_FREE, roomId);

        if (jData.has(CALL_ID)) {
            String callWaitId = jData.getString(CALL_ID);
            result.put(CheckCallTask.CALL_ID, callWaitId);
        }


    }

    public static List<PaymentAdOnItem> parsePaymentPackageList(JSONObject jData) throws JSONException {
        List<PaymentAdOnItem> paymentAdOnItems = new ArrayList<>();
        JSONArray jPackages = jData.getJSONArray(Constant.JSON.PACKAGE_LIST);
        for (int i = 0; i < jPackages.length(); i++) {
            JSONObject jPackage = jPackages.getJSONObject(i);

            PaymentAdOnItem paymentAdOnItem = new PaymentAdOnItem();
            paymentAdOnItem.setId(jPackage.getString(Constant.JSON.ID_ADDON));
            paymentAdOnItem.setCash(jPackage.getInt(Constant.JSON.CASH));
            paymentAdOnItem.setPoint(jPackage.getInt(Constant.JSON.POINT));
            paymentAdOnItem.setStatus(jPackage.getInt(Constant.JSON.STATUS));

            paymentAdOnItems.add(paymentAdOnItem);
        }
        return paymentAdOnItems;
    }

    public static int getInteractionUserGender() {
        return ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE ?
                UserItem.FEMALE : UserItem.MALE;
    }

    public static Map<String, Object> parseFootprintItem(JSONObject jData) throws JSONException {
        JSONArray jListGroupDate = jData.getJSONArray(Constant.JSON.LIST);
        int total = jData.getInt(Constant.JSON.TOTAL);

        ArrayList<FootprintItem> listFootprint = new ArrayList<>();

        for (int j = 0, m = jListGroupDate.length(); j < m; j++) {
            JSONObject jGroup = jListGroupDate.getJSONObject(j);
            JSONArray jUsers = jGroup.getJSONArray(Constant.JSON.USERS);
            String groupDate = jGroup.getString(Constant.JSON.DATE);

            FootprintItem footPrint = new FootprintItem();

            for (int i = 0, n = jUsers.length(); i < n; i++) {
                JSONObject jUser = jUsers.getJSONObject(i);
                UserItem userItem = parseUserForFootprint(jUser);
                footPrint.addUser(userItem);
            }
            footPrint.setDate(groupDate);
            listFootprint.add(footPrint);
        }
        HashMap result = new HashMap();
        result.put(Constant.JSON.LIST, listFootprint);
        result.put(Constant.JSON.TOTAL, total);
        return result;
    }

    @NonNull
    private static UserItem parseUserForFootprint(JSONObject jUser) throws JSONException {
        String userId = jUser.getString(Constant.JSON.USER_ID);
        String extension = jUser.getString(Constant.JSON.EXTENSION);
        String userName = jUser.getString(Constant.JSON.USER_NAME);
        String slogan = jUser.optString(Constant.JSON.SLOGAN, "");
        String avatarUrl = jUser.getString(Constant.JSON.AVATAR);
        String footprintTime = jUser.optString(Constant.JSON.TIMESTAMP, "");
        String birthDay = jUser.optString(Constant.JSON.BIRTHDAY, "");
        int status = jUser.getInt(Constant.JSON.STATUS);
        String lastLogin = jUser.optString(Constant.JSON.LAST_LOGIN, "");

        int videoSettingCall = jUser.getJSONObject(Constant.JSON.SETTING_CALL).getInt(Constant.JSON.VIDEO_CALL_SET);
        int voiceSettingCall = jUser.getJSONObject(Constant.JSON.SETTING_CALL).getInt(Constant.JSON.VOICE_CALL_SET);

        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(avatarUrl);

        SipItem sipItem = new SipItem();
        sipItem.setExtension(extension);

        SettingItem settingItem = new SettingItem();
        settingItem.setVideoCall(videoSettingCall);
        settingItem.setVoiceCall(voiceSettingCall);

        UserItem userItem = new UserItem();
        userItem.setUserId(userId);
        userItem.setUsername(userName);
        userItem.setMemo(slogan);
        userItem.setFootprintTime(footprintTime);
        userItem.setDateOfBirth(birthDay);
        userItem.setStatus(status);
        userItem.setLastLogin(lastLogin);
        userItem.setAvatarItem(imageItem);
        userItem.setSipItem(sipItem);
        userItem.setSettings(settingItem);
        return userItem;
    }

    public static FollowItem parseFollowerItem(JSONObject jData) throws JSONException{

        int total = jData.getInt(Constant.JSON.TOTAL);
        JSONArray jFollowers = jData.getJSONArray(Constant.JSON.FOLLOWER_LIST);
        ArrayList<UserItem> followers = parseFollower(jFollowers);
        FollowItem result = new FollowItem(total, followers);
        return result;
    }

    public static FollowItem parseFollowingItem(JSONObject jData) throws JSONException{
        int total = jData.getInt(Constant.JSON.TOTAL);
        JSONArray jFollowers = jData.getJSONArray(Constant.JSON.FOLLOW_LIST);
        ArrayList<UserItem> followers = parseFollower(jFollowers);
        FollowItem result = new FollowItem(total, followers);
        return result;
    }

    private static ArrayList<UserItem> parseFollower( JSONArray jFollowers) throws JSONException {
        ArrayList<UserItem> followers= new ArrayList<>();
        for (int i = 0, n = jFollowers.length() ;i <n ;i++) {
            JSONObject jFollower = jFollowers.getJSONObject(i);
            UserItem follower = new UserItem();
            follower.setUserId(jFollower.getString(Constant.JSON.USER_ID));
            SipItem sipItem = new SipItem();
            sipItem.setExtension(jFollower.getString(Constant.JSON.EXTENSION));
            follower.setSipItem(sipItem);
            follower.setUsername(jFollower.getString(Constant.JSON.USER_NAME));
            follower.setMemo(jFollower.getString(Constant.JSON.SLOGAN));
            ImageItem avatar = new ImageItem();
            avatar.setOriginUrl(jFollower.getString(Constant.JSON.AVATAR));
            follower.setAvatarItem(avatar);
            follower.setStatus(jFollower.getInt(Constant.JSON.STATUS));
            follower.setLastLogin(jFollower.getString(Constant.JSON.LAST_LOGIN));

            int videoSettingCall = jFollower.getJSONObject(Constant.JSON.SETTING_CALL).getInt(Constant.JSON.VIDEO_CALL_SET);
            int voiceSettingCall = jFollower.getJSONObject(Constant.JSON.SETTING_CALL).getInt(Constant.JSON.VOICE_CALL_SET);
            SettingItem settingItem = new SettingItem();
            settingItem.setVideoCall(videoSettingCall);
            settingItem.setVoiceCall(voiceSettingCall);
            follower.setSettings(settingItem);

            followers.add(follower);
        }
        return followers;
    }

    public static Map<String, Object> parseCallLogs(JSONObject jData) throws JSONException{
        JSONArray jListGroupDate = jData.getJSONArray(Constant.JSON.GROUP_BY_DAY);
        int total = jData.getInt(Constant.JSON.TOTAL);

        ArrayList<CallLogItem> callLogs = new ArrayList<>();

        for (int j = 0, m = jListGroupDate.length(); j < m; j++) {
            JSONObject jGroup = jListGroupDate.getJSONObject(j);
            JSONArray jCallLogs = jGroup.getJSONArray(Constant.JSON.CALL_LOGS);
            String groupDate = jGroup.getString(Constant.JSON.DATE);

            CallLogItem callLogItem = new CallLogItem();

            for (int i = 0, n = jCallLogs.length(); i < n; i++) {
                JSONObject jCallLog = jCallLogs.getJSONObject(i);
                UserItem userItem = parseUserForFootprint(jCallLog.getJSONObject(Constant.JSON.USER));
                int duration =  jCallLog.getInt(Constant.JSON.DURATION);
                int resultType = jCallLog.getInt(Constant.JSON.RESULT_TYPE);
                String lastTimeCallLog = jCallLog.getString(Constant.JSON.LAST_TIME_CALL_LOG);
                HistoryCallItem historyCallItem = new HistoryCallItem();
                historyCallItem.setUserItem(userItem);
                historyCallItem.setDuration(duration);
                historyCallItem.setCallLogResultType(resultType);
                historyCallItem.setLastTimeCallLog(lastTimeCallLog);
                callLogItem.addHistoryCall(historyCallItem);
            }
            callLogItem.setDate(groupDate);
            callLogs.add(callLogItem);
        }
        HashMap result = new HashMap();
        result.put(Constant.JSON.LIST, callLogs);
        result.put(Constant.JSON.TOTAL, total);
        return result;
    }
}
