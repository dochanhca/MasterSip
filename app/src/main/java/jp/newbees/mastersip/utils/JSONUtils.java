package jp.newbees.mastersip.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by vietbq on 12/20/16.
 */
public class JSONUtils {

    public static ArrayList<UserItem> parseUsers(JSONObject data) throws JSONException {
        JSONArray jArray = data.getJSONArray(Constant.JSON.kUsers);
        ArrayList<UserItem> result = new ArrayList<>();
        for (int i=0, n=jArray.length(); i<n; i++){
            JSONObject jUser = jArray.getJSONObject(i);
            UserItem userItem = new UserItem();
            userItem.setUsername(jUser.getString(Constant.JSON.kHandleName));
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
            job.setTitle(getString(jUser,Constant.JSON.kJobName));
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
//
//            JSONObject jAvatar = jUser.getJSONObject(Constant.JSON.kAvatar);
//            ImageItem avatarItem = new ImageItem();
//            int imageId = jAvatar.getInt(Constant.JSON.kID);
//            String imagePath = jAvatar.getString(Constant.JSON.kPath);
//            avatarItem.setImageId(imageId);
//            avatarItem.setOriginUrl(imagePath);
//            userItem.setAvatarItem(avatarItem);
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

    public final static RelationshipItem parseRelationship(JSONObject jsonObject) throws JSONException {
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
    private final static int getInt(JSONObject jsonObject, String name) throws JSONException {
        if (jsonObject.has(name)){
            return jsonObject.getInt(name);
        }else {
            return -1;
        }
    }
}
