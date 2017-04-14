package jp.newbees.mastersip.event;

/**
 * Created by ducpv on 1/4/17.
 */

public class RegisterVoIPEvent {
    public static final int REGISTER_SUCCESS = 1;
    public static final int REGISTER_FAILED = 2;
    private final boolean inProgress;

    private int responseCode;

    /**
     *
     * @param responseCode
     */
    public RegisterVoIPEvent(int responseCode, boolean inProgress) {
        this.responseCode = responseCode;
        this.inProgress = inProgress;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public RegisterVoIPEvent(int responseCode) {
        this(responseCode, false);
    }

    public int getResponseCode() {
        return responseCode;
    }


}
