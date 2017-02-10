package jp.newbees.mastersip.utils;

import java.util.ArrayList;

import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.TextChatItem;
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

    public static UserItem getUserItem(int id) {
        UserItem userItem = new UserItem();
        ImageItem imageItem = new ImageItem();
        imageItem.setOriginUrl(id % 2 == 0 ?
                "http://www.wonderslist.com/wp-content/uploads/2015/10/Doutzen-Kroes-Most-Beautiful-Dutch-Woman.jpg" :
                "http://www.missgloss.net/wp-content/uploads/2016/08/Miranda-Kerr.jpg");
        imageItem.setThumbUrl(imageItem.getOriginUrl());
        userItem.setAvatarItem(imageItem);
        userItem.setUserId("Thanglh_" + id);
        userItem.setUsername("Thanglh " + id);
        return userItem;
    }

    public static ArrayList<BaseChatItem> getListChat() {
        ArrayList<BaseChatItem> result = new ArrayList<>();
        result.addAll(getListTextChat(10));
        return result;
    }

    public static ArrayList<TextChatItem> getListTextChat(int total) {
        ArrayList<TextChatItem> datas = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            datas.add(getTextChatItem(i % 2 == 0,"I teleport this friendship, it's called virtual tragedy."));
        }
        return datas;
    }

    public static TextChatItem getTextChatItem(boolean isOwner, String message) {
        TextChatItem textChatItem = new TextChatItem(message);
        textChatItem.setChatType(BaseChatItem.ChatType.CHAT_TEXT);
        textChatItem.setSendee(getUserItem(0));
        return textChatItem;
    }
}
