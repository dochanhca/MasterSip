package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/10/17.
 */

public class MicrophoneEvent {
    private boolean mute;

    public MicrophoneEvent(boolean mute) {
        this.mute = mute;
    }

    public boolean isMute() {
        return mute;
    }
}
