package jp.newbees.mastersip.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.model.DeletedChatItem;
import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.GiftChatItem;
import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.PacketItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.RoomChatItem;
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
                break;
            case CHAT_TEXT:
                chatItem = parseTextChatItem(jData, sender);
                break;
            case CHAT_IMAGE:
                chatItem = parseImageChatItem(jData, sender);
                break;
            case CHAT_GIFT:
                break;
            case CHAT_VOICE_CALL:
                break;
            case CHAT_VIDEO_CALL:
                break;
            case CHAT_VIDEO_CHAT_CALL:
                break;
            default:
                break;
        }
        return chatItem;
    }

    private static BaseChatItem parseImageChatItem(JSONObject jData, UserItem me) throws JSONException {
        ImageChatItem imageChatItem = new ImageChatItem();

        int roomType = jData.getInt(Constant.JSON.ROOM_TYPE);
        imageChatItem.setRoomType(roomType);
        imageChatItem.setChatType(CHAT_IMAGE);
        imageChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));
        imageChatItem.setRoomId(jData.getInt(Constant.JSON.ROOM_ID));

        imageChatItem.setOwner(me);
        imageChatItem.setFullDate(jData.getString(Constant.JSON.DATE));
        imageChatItem.setShortDate(DateTimeUtils.getShortTime(imageChatItem.getFullDate()));

        JSONObject jImage = jData.getJSONObject(Constant.JSON.IMAGE);
        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(jImage.getString(Constant.JSON.PATH));
        imageItem.setThumbUrl(jImage.getString(Constant.JSON.THUMB));

        imageChatItem.setImageItem(imageItem);

        return imageChatItem;
    }

    private static final DeletedChatItem parseDeletedChatItem(JSONObject jData, UserItem sender) throws JSONException {
        DeletedChatItem deletedChatItem = new DeletedChatItem();
        JSONObject jDeletedItem = jData.getJSONObject(Constant.JSON.DELETED);
        String content = jDeletedItem.getString(Constant.JSON.CONTENT);

        deletedChatItem.setOwner(sender);
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

        textChatItem.setOwner(me);
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

    public static ArrayList<BaseChatItem> parseChatHistory(JSONObject data, HashMap<String, UserItem> members, Context context) throws JSONException {
        ArrayList<BaseChatItem> result = new ArrayList<>();

        JSONArray jListMessages = data.getJSONArray(Constant.JSON.LIST_MESSAGES);
        int sectionFirstPosition = 0;
        for (int i = 0; i < jListMessages.length(); i++) {
            JSONObject jListMessage = jListMessages.getJSONObject(i);

            result.add(getHeaderChatItem(jListMessage, sectionFirstPosition, context));

            JSONArray jMessages = jListMessage.getJSONArray(Constant.JSON.MESSAGES);
            for (int j = 0; j < jMessages.length(); j++) {
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

            members.put(userItem.getUserId(), userItem);
        }
        return members;
    }

    private static BaseChatItem getBaseChatItemInHistory(JSONObject jMessage, int sectionFirstPosition,
                                                         HashMap<String, UserItem> members) throws JSONException {

        BaseChatItem baseChatItem;
        UserItem owner = ConfigManager.getInstance().getCurrentUser();
        int type = jMessage.getInt(Constant.JSON.TYPE);
        switch (type) {
            case BaseChatItem.ChatType.CHAT_TEXT:
                baseChatItem = new TextChatItem();
                ((TextChatItem) baseChatItem).setMessage(jMessage.getJSONObject(Constant.JSON.TEXT).
                        getString(Constant.JSON.CONTENT));
                break;
            case BaseChatItem.ChatType.CHAT_IMAGE:
                baseChatItem = new ImageChatItem();
                JSONObject jImage = jMessage.getJSONObject(Constant.JSON.IMAGE);
                ImageItem imageItem = new ImageItem(jImage.getString(Constant.JSON.PATH),
                        jImage.getString(Constant.JSON.THUMBNAIL));
                ((ImageChatItem) baseChatItem).setImageItem(imageItem);
                break;
            case BaseChatItem.ChatType.CHAT_GIFT:
                baseChatItem = parseGiftChatItem(jMessage);
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

    private static BaseChatItem parseGiftChatItem(JSONObject jMessage) throws JSONException {
        GiftChatItem giftChatItem = new GiftChatItem();
        JSONObject jGift = jMessage.getJSONObject(Constant.JSON.GIFT);
        String context = jGift.getString(Constant.JSON.CONTEXT);

        GiftItem giftItem = new GiftItem();
        int giftId = jGift.getInt(Constant.JSON.ID);
        String giftName = jGift.getString(Constant.JSON.NAME);
        int point = jGift.getInt(Constant.JSON.POINT);
        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(jGift.getString(Constant.JSON.IMAGE));

        giftItem.setGiftId(giftId);
        giftItem.setName(giftName);
        giftItem.setPrice(point);
        giftItem.setGiftImage(imageItem);

        giftChatItem.setContent(context);
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
            roomChatItem.setNumberMessageUnRead(jRoomChat.getInt(Constant.JSON.UNREAD_NUMBER));
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
        String encrypted = AESHelper.encrypt(emailBackupItem.getPass());
        jsonObject.put(Constant.JSON.PASSWORD, encrypted);
        jsonObject.put(Constant.JSON.PASSWORD_CONFIRMATION, encrypted);
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
        userItem.setGender(jData.getInt(Constant.JSON.GENDER));

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
        jsonObject.put(Constant.JSON.PASSWORD_OLD, AESHelper.encrypt(item.getOldPass()));
        jsonObject.put(Constant.JSON.EMAIL_NEW, item.getEmail());
        String pass = AESHelper.encrypt(item.getPass());
        jsonObject.put(Constant.JSON.PASSWORD_NEW, pass);
        jsonObject.put(Constant.JSON.PASSWORD_CONFIRMATION, pass);
        jsonObject.put(Constant.JSON.EXTENSION, item.getExtension());
        return jsonObject;
    }
}
