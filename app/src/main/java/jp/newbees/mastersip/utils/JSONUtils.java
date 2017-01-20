package jp.newbees.mastersip.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.DeletedChatItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.model.PhotoItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.TextChatItem;
import jp.newbees.mastersip.model.UserItem;

import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_DELETED;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_GIFT;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_IMAGE;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_TEXT;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VIDEO_CALL;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VIDEO_CHAT_CALL;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VOICE;
import static jp.newbees.mastersip.model.BaseChatItem.ChatType.CHAT_VOICE_CALL;
import static jp.newbees.mastersip.model.BaseChatItem.RoomType.ROOM_CHAT_CHAT;

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

            ImageItem avatar = new ImageItem();
            avatar.setImageId(getInt(jUser, Constant.JSON.AVATAR_ID));
            avatar.setOriginUrl(jUser.getString(Constant.JSON.AVATAR));
            userItem.setAvatarItem(avatar);
            result.add(userItem);
        }
        return result;
    }

    public static SettingItem parseSettings(JSONObject jSetting) throws JSONException {
        SettingItem settingItem = new SettingItem();

        settingItem.setChat(jSetting.getInt(Constant.JSON.CHAT));
        settingItem.setVoiceCall(jSetting.getInt(Constant.JSON.VOICE_CALL));
        settingItem.setVideoCall(jSetting.getInt(Constant.JSON.VIDEO_CALL));

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


        int gender = getInt(jUser, Constant.JSON.K_USER_GENDER);
        userItem.setGender(gender);
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
        int followed = jsonObject.getInt(Constant.JSON.FOLLOWED);
        int isNotification = jsonObject.getInt(Constant.JSON.ONLINE_NOTIFICATION);
        relationshipItem.setFollowed(followed);
        relationshipItem.setIsNotification(isNotification);
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

    public static final BaseChatItem parseChatItem(JSONObject jData, UserItem sender) throws JSONException {
        BaseChatItem chatItem = new BaseChatItem();
        int type = jData.getInt(Constant.JSON.TYPE);
        switch (type) {
            case CHAT_DELETED:
                chatItem = parseDeletedChatItem(jData, sender);
                break;
            case CHAT_VOICE:
//                chatItem = [self getAudioItem:dictChatItem ofExtension:extension];
                break;
            case CHAT_TEXT:
                chatItem = parseTextChatItem(jData, sender);
                break;
            case CHAT_IMAGE:
//                chatItem = [self getImgeItem:dictChatItem ofExtension:extension];
                break;
            case CHAT_GIFT:
//                chatItem = [self getGifiItem:dictChatItem ofExtension:extension];
                break;
            case CHAT_VOICE_CALL:
            case CHAT_VIDEO_CALL:
            case CHAT_VIDEO_CHAT_CALL:
//                chatItem = [self getCallItem:dictChatItem ofExtension:extension];
                break;
            default:
                break;
        }
        return chatItem;
    }

    private static final DeletedChatItem parseDeletedChatItem(JSONObject jData, UserItem sender) throws JSONException {
        DeletedChatItem deletedChatItem = new DeletedChatItem();
        JSONObject jDeletedItem = jData.getJSONObject(Constant.JSON.DELETED);
        String extensionSender = jData.getJSONObject(Constant.JSON.SENDER).getString(Constant.JSON.EXTENSION);
        String content = jDeletedItem.getString(Constant.JSON.CONTENT);
        if (sender.getSipItem().getExtension().equalsIgnoreCase(extensionSender)) {
            deletedChatItem.setSender(true);
        } else {
            deletedChatItem.setSender(false);
        }
        deletedChatItem.setMessage(content);
        deletedChatItem.setRoomType(ROOM_CHAT_CHAT);
        return deletedChatItem;
    }

    private static final TextChatItem parseTextChatItem(JSONObject jData, UserItem me) throws JSONException {
        JSONObject jText = jData.getJSONObject(Constant.JSON.TEXT);
        JSONObject jSender = jData.getJSONObject(Constant.JSON.SENDER);

        String extensionSender = jSender.getString(Constant.JSON.EXTENSION);
        String content = jText.getString(Constant.JSON.CONTENT);

        TextChatItem textChatItem = new TextChatItem(content);

        if (me.getSipItem().getExtension().equalsIgnoreCase(extensionSender)) {
            textChatItem.setSender(true);
        } else {
            textChatItem.setSender(false);
        }

        int roomType = jText.getInt(Constant.JSON.ROOM_TYPE);
        textChatItem.setRoomType(roomType);
        textChatItem.setChatType(CHAT_TEXT);
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
        String message = jData.getString(Constant.JSON.MESSAGE);
        JSONObject response = jData.getJSONObject(Constant.JSON.RESPONSE);
        PacketItem packetItem = new PacketItem(action, message, response.toString());
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

    public static PhotoItem parseListPhotos(JSONObject jData) throws JSONException {
        PhotoItem photoItem = new PhotoItem();

        if (!jData.getString(Constant.JSON.NEXT_ID).equals("")) {
            photoItem.setNextId(Integer.parseInt(jData.getString(Constant.JSON.NEXT_ID)));
        }
        photoItem.setTotalImage(jData.getInt(Constant.JSON.TOTAL_COUNT));

        JSONArray jsonImages = jData.getJSONArray(Constant.JSON.LIST_IMAGE);
        List<ImageItem> imageItems = new ArrayList<>();
        for (int i = 0; i < jsonImages.length(); i++) {
            JSONObject jImage = jsonImages.getJSONObject(i);
            ImageItem imageItem = new ImageItem();
            imageItem.setImageId(jImage.getInt(Constant.JSON.IMAGE_ID));
            imageItem.setOriginUrl(jImage.getString(Constant.JSON.IMAGE_PATH));
            imageItem.setThumbUrl(jImage.getString(Constant.JSON.IMAGE_PATH_THUMB));

            imageItems.add(imageItem);
        }

        photoItem.setImageItems(imageItems);

        return photoItem;
    }

    public static UserItem parseMyMenuItem(JSONObject jData) throws JSONException {
        JSONObject jMyInfo = jData.getJSONObject(Constant.JSON.MY_INFO);
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setCoin(jMyInfo.getInt(Constant.JSON.POINT));
        if (!jMyInfo.isNull(Constant.JSON.AVATAR)) {
            JSONObject jAvatar = jMyInfo.getJSONObject(Constant.JSON.AVATAR);
            ImageItem imageItem = JSONUtils.parseImageItem(jAvatar);
            userItem.setAvatarItem(imageItem);
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
}
