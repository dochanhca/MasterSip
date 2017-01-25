package jp.newbees.mastersip.event;

/**
 * Created by ducpv on 1/4/17.
 */

public class RegisterVoIPEvent {
    public static final int REGISTER_SUCCESS = 1;
    public static final int REGISTER_FAILED = 2;

    private int responseCode;

    /**
     *
     * @param responseCode
     */
    public RegisterVoIPEvent(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
