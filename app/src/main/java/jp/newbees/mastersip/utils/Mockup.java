package jp.newbees.mastersip.utils;

import java.util.ArrayList;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 12/26/16.
 */
public class Mockup {
    public static ArrayList<UserItem> getUserItems() {
        ArrayList<UserItem> items = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            UserItem userItem = getUserItem(i);
            items.add(userItem);

        }
        return items;
    }

    private static UserItem getUserItem(int id) {
        UserItem userItem = new UserItem();
        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(id % 2 == 0 ?
                "http://www.wonderslist.com/wp-content/uploads/2015/10/Doutzen-Kroes-Most-Beautiful-Dutch-Woman.jpg" :
                "http://www.missgloss.net/wp-content/uploads/2016/08/Miranda-Kerr.jpg");
        imageItem.setThumbUrl(imageItem.getOriginUrl());
        userItem.setAvatarItem(imageItem);
        userItem.setUserId("Thanglh_"+id);
        userItem.setUsername("Thanglh "+id);
        return userItem;
    }

}
