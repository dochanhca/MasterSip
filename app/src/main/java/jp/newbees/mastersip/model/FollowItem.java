package jp.newbees.mastersip.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vietbq on 4/10/17.
 */

public class FollowItem {

    private int total;
    private List<UserItem> followers;

    public FollowItem(int total, List<UserItem> followers) {
        this.followers = followers;
        this.total = total;
    }

    public FollowItem() {
        this.followers = new ArrayList<>();
        this.total = 0;
    }

    public int getTotal() {
        return total;
    }

    public List<UserItem> getFollowers() {
        return followers;
    }
}
