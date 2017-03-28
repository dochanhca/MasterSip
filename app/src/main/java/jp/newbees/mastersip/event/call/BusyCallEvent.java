package jp.newbees.mastersip.event.call;

/**
 * Created by ducpv on 3/9/17.
 */

public class BusyCallEvent {

    private String handleName;

    public BusyCallEvent() {
    }

    public BusyCallEvent(String handleName) {
        this.handleName = handleName;
    }

    public String getHandleName() {
        return handleName;
    }
}
