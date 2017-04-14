package jp.newbees.mastersip.linphone;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

import jp.newbees.mastersip.event.RegisterVoIPEvent;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/4/17.
 */

public class LinphoneNotifier {

    private Handler mHandler;
    private static final String TAG = "LinphoneNotifier";

    /**
     *
     * @param mHandler
     */
    public LinphoneNotifier(Handler mHandler) {
        this.mHandler = mHandler;
    }


    public void registerVoIPSuccess(final boolean registrationProgress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "registerVoIPSuccess");
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_SUCCESS, registrationProgress));
            }
        });
    }

    public void registerVoIPFailed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "registerVoIP failed");
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_FAILED));
            }
        });
    }
}
