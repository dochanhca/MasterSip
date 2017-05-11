package jp.newbees.mastersip.model;

/**
 * Created by ducpv on 5/11/17.
 */

public class SettingPushItem {

    public static final int UN_RECEIVE = 0;
    public static final int RECEIVE_ALL_PUSH = 1;
    public static final int RECEIVE_USER_FOLLOW_PUSH = 2;
    public static final int RECEIVE_ADMIN_PUSH = 3;


    private int allUser;
    private int userFollow;
    private int admin;

    public int getAllUser() {
        return allUser;
    }

    public void setAllUser(int allUser) {
        this.allUser = allUser;
    }

    public int getUserFollow() {
        return userFollow;
    }

    public void setUserFollow(int userFollow) {
        this.userFollow = userFollow;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
