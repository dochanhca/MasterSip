package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/10/17.
 */

public class SpeakerEvent {
    private boolean enable;

    public SpeakerEvent(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }
}
