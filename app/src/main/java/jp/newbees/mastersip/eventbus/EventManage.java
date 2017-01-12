package jp.newbees.mastersip.eventbus;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/9/17.
 */

public class EventManage {
    private static EventManage instance;
    private HashMap<String, String> events;

    public EventManage() {
        events = new HashMap<>();
    }

    public static EventManage getInstance() {
        if (instance == null) {
            instance = new EventManage();
        }
        return instance;
    }

    public final void registerChattingEventName(String fromExtension, int roomType, Object subscriber) {
        registerEventName(genChattingEventName(fromExtension,roomType),subscriber);
    }

    public final void registerEventName(String eventName, Object subscriber) {
        events.put(eventName, "");
        EventBus.getDefault().register(subscriber);
    }

    public final void unregisterEventName(String eventName) {
        if (events.containsKey(eventName)) {
            events.remove(eventName);
        }
    }

    public final boolean hasRegistered(String eventName) {
        if (events.containsKey(eventName)) {
            return true;
        } else {
            return false;
        }
    }

    public String genChattingEventName(String fromExtension, int roomType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.EVENT.ACTION_CHATTING).append("-").append(fromExtension).append("-").append(roomType);
        return stringBuilder.toString();
    }
}
