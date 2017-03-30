package jp.newbees.mastersip.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vietbq on 3/28/17.
 */

public class FootprintItem {
    private String date;
    private List<UserItem> userItems;

    public FootprintItem() {
        this.userItems = new ArrayList<>();
        this.date = "";
    }

    public List<UserItem> getUserItems() {
        return userItems;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addUser(UserItem userItem) {
        this.userItems.add(userItem);
    }
}
