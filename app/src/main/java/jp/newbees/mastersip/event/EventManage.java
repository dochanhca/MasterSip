package jp.newbees.mastersip.event;

import java.util.HashMap;

import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/9/17.
 */

public class EventManage {
    private static EventManage instance;
    private HashMap<String, String> events;

    public EventManage(){
        events = new HashMap<>();
    }

    public static EventManage getInstance() {
        if (instance == null){
            instance = new EventManage();
        }
        return instance;
    }

    public final void registerEventName(String eventName) {
        events.put(eventName,"");
    }

    public final void unregisterEventName(String eventName) {
        if (events.containsKey(eventName)){
            events.remove(eventName);
        }
    }

    public final boolean hasRegistered(String eventName) {
        if (events.containsKey(eventName)){
            return true;
        }else {
            return false;
        }
    }

    public String genChattingEventName(String fromExtension, int roomType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.EVENT.ACTION_CHATTING).append("-").append(fromExtension).append("-").append(roomType);
        return stringBuilder.toString();
    }
}
