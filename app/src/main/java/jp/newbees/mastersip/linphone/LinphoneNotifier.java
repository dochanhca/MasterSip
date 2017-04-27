package jp.newbees.mastersip.linphone;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

import jp.newbees.mastersip.event.RegisterVoIPEvent;

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
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_SUCCESS, registrationProgress));
            }
        });
    }

    public void registerVoIPFailed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_FAILED));
            }
        });
    }

    public Handler getHandler() {
        return mHandler;
    }
}
