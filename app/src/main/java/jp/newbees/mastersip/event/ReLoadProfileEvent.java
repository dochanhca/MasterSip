package jp.newbees.mastersip.event;

/**
 * Created by ducpv on 2/8/17.
 */

public class ReLoadProfileEvent {

    private boolean needReload;

    public ReLoadProfileEvent(boolean needReload) {
        this.needReload = needReload;
    }

    public boolean isNeedReload() {
        return needReload;
    }
}
