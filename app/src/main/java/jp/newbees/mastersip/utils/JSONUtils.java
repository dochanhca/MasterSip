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
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.SelectionItem;
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

    private JSONUtils(){
        //Prevent init object
    }

    public static List<UserItem> parseUsers(JSONObject data) throws JSONException {
        JSONArray jArray = data.getJSONArray(Constant.JSON.kUsers);
        ArrayList<UserItem> result = new ArrayList<>();
        for (int i=0, n=jArray.length(); i<n; i++){
            JSONObject jUser = jArray.getJSONObject(i);
            UserItem userItem = new UserItem();
            userItem.setUsername(jUser.getString(Constant.JSON.HANDLE_NAME));
            userItem.setUserId(jUser.getString(Constant.JSON.kID));

            userItem.setMemo(jUser.getString(Constant.JSON.kSlogan));

            ImageItem avatar = new ImageItem();
            avatar.setImageId(getInt(jUser, Constant.JSON.kAvatarId));
            avatar.setOriginUrl(jUser.getString(Constant.JSON.kAvatar));
            userItem.setAvatarItem(avatar);

            SipItem sipItem = new SipItem();
            sipItem.setExtension(jUser.getString(Constant.JSON.kExtension));
            userItem.setSipItem(sipItem);

            if (jUser.has(Constant.JSON.kRelationship)) {
                JSONObject jRelationship = jUser.getJSONObject(Constant.JSON.kRelationship);
                RelationshipItem relationshipItem = parseRelationship(jRelationship);
                userItem.setRelationshipItem(relationshipItem);
            }

            String birthDay = jUser.getString(Constant.JSON.kBirthday);
            userItem.setDateOfBirth(birthDay);

            SelectionItem location = new SelectionItem();
            JSONObject jProvince = jUser.getJSONObject(Constant.JSON.kProvince);
            location.setId(jProvince.getInt(Constant.JSON.kUserProvinceId));
            location.setTitle(jProvince.getString(Constant.JSON.kUserProvinceName));
            userItem.setLocation(location);

            SelectionItem job = new SelectionItem();
            job.setTitle(JSONUtils.getString(jUser,Constant.JSON.kJobName));
            userItem.setJobItem(job);

            String lastLogin = jUser.getString(Constant.JSON.kLastLogin);
            userItem.setLastLogin(lastLogin);

            int status = getInt(jUser,Constant.JSON.kStatus);
            userItem.setStatus(status);


            int gender = getInt(jUser,Constant.JSON.kUserGender);
            userItem.setGender(gender);
            if (jUser.has(Constant.JSON.kExtendInfo)) {
                JSONObject jExtendInfo = jUser.getJSONObject(Constant.JSON.kExtendInfo);
                if (userItem.getGender() == UserItem.FEMALE && jExtendInfo.length() > 0) {
                    String charmPoint = jExtendInfo.getString(Constant.JSON.kCharmPoint);
                    userItem.setCharmingPoint(charmPoint);
                    String freeTime = jExtendInfo.getString(Constant.JSON.kFreeTime);
                    SelectionItem availableTime = new SelectionItem();
                    availableTime.setTitle(freeTime);
                    userItem.setAvailableTimeItem(availableTime);
                    String typeBoy = jExtendInfo.getString(Constant.JSON.kTypeBoy);
                    userItem.setTypeBoy(typeBoy);
                    String favoriteType = jExtendInfo.getString(Constant.JSON.kFavoriteType);
                    SelectionItem typeGirl = new SelectionItem();
                    typeGirl.setTitle(favoriteType);
                    userItem.setTypeGirl(typeGirl);
                }
            }
            /*
            JSONObject jAvatar = jUser.getJSONObject(Constant.JSON.kAvatar);
            ImageItem avatarItem = new ImageItem();
            int imageId = jAvatar.getInt(Constant.JSON.kID);
            String imagePath = jAvatar.getString(Constant.JSON.kPath);
            avatarItem.setImageId(imageId);
            avatarItem.setOriginUrl(imagePath);
            userItem.setAvatarItem(avatarItem);
            */
            result.add(userItem);
        }
        return result;
    }

    private static String getString(JSONObject jsonObject, String name) throws JSONException {
        if (jsonObject.has(name)){
            return jsonObject.getString(name);
        }else {
            return "";
        }
    }

    public static final RelationshipItem parseRelationship(JSONObject jsonObject) throws JSONException {
        RelationshipItem relationshipItem = new RelationshipItem();
        int followed = jsonObject.getInt(Constant.JSON.kFollowed);
        boolean isNotification = jsonObject.getBoolean(Constant.JSON.kNotification);
        relationshipItem.setFollowed(followed);
        relationshipItem.setNotification(isNotification);
        return relationshipItem;
    }

    /**
     *
     * @param jsonObject
     * @param name
     * @return Default value is -1
     * @throws JSONException
     */
    private static final int getInt(JSONObject jsonObject, String name) throws JSONException {
        if (jsonObject.has(name)){
            return jsonObject.getInt(name);
        }else {
            return -1;
        }
    }

    public static final BaseChatItem parseChatItem(JSONObject jData,UserItem sender) throws JSONException {
        BaseChatItem chatItem = new BaseChatItem();
        int type = jData.getInt(Constant.JSON.kType);
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
        JSONObject jDeletedItem = jData.getJSONObject(Constant.JSON.kDeleted);
        String extensionSender = jData.getJSONObject(Constant.JSON.kSender).getString(Constant.JSON.kExtension);
        String content = jDeletedItem.getString(Constant.JSON.kContent);
        if (sender.getSipItem().getExtension().equalsIgnoreCase(extensionSender)) {
            deletedChatItem.setSender(true);
        }else {
            deletedChatItem.setSender(false);
        }
        deletedChatItem.setMessage(content);
        deletedChatItem.setRoomType(ROOM_CHAT_CHAT);
        return deletedChatItem;
    }

    private static final TextChatItem parseTextChatItem(JSONObject jData, UserItem me) throws JSONException {
        JSONObject jText = jData.getJSONObject(Constant.JSON.kText);
        JSONObject jSender = jData.getJSONObject(Constant.JSON.kSender);

        String extensionSender = jSender.getString(Constant.JSON.kExtension);
        String content = jText.getString(Constant.JSON.kContent);

        TextChatItem textChatItem = new TextChatItem(content);

        if (me.getSipItem().getExtension().equalsIgnoreCase(extensionSender)) {
            textChatItem.setSender(true);
        }else {
            textChatItem.setSender(false);
        }

        int roomType = jText.getInt(Constant.JSON.kRoomType);
        textChatItem.setRoomType(roomType);
        textChatItem.setChatType(CHAT_TEXT);
        textChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));

        UserItem userItem = new UserItem();
        SipItem sipItem = new SipItem(extensionSender);

        ImageItem imageItem = new ImageItem();
        imageItem.setThumbUrl(jSender.getString(Constant.JSON.kAvatar));
        imageItem.setOriginUrl(jSender.getString(Constant.JSON.kAvatar));
        userItem.setAvatarItem(imageItem);
        userItem.setSipItem(sipItem);

        textChatItem.setOwner(userItem);
        textChatItem.setFullDate(jData.getString(Constant.JSON.kDate));
        textChatItem.setShortDate(DateTimeUtils.getShortTime(textChatItem.getFullDate()));
        return textChatItem;
    }


    public static PacketItem parsePacketItem(String raw) throws JSONException {
        JSONObject jData = new JSONObject(raw);
        String action = jData.getString(Constant.JSON.ACTION);
        String message = jData.getString(Constant.JSON.kMessage);
        JSONObject response = jData.getJSONObject(Constant.JSON.kResponse);
        PacketItem packetItem = new PacketItem(action, message, response.toString());
        return packetItem;
    }

    public static BaseChatItem parseDateOnUpdateMessageState(JSONObject jData) throws JSONException {
        BaseChatItem baseChatItem = new BaseChatItem();
        baseChatItem.setMessageState(jData.getInt(Constant.JSON.kStatus));
        baseChatItem.setRoomId(jData.getInt(Constant.JSON.kRoomId));
        baseChatItem.setMessageId(jData.getInt(Constant.JSON.MESSAGE_ID));
        baseChatItem.setRoomType(jData.getInt(Constant.JSON.kRoomType));
        return baseChatItem;
    }
}
