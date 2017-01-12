package jp.newbees.mastersip.linphone;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.linphone.core.tutorials.TutorialNotifier;

import jp.newbees.mastersip.event.RegisterVoIPEvent;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/4/17.
 */

public class LinphoneNotifier extends TutorialNotifier {

    private Handler mHandler;
    private String TAG = "LinphoneNotifier";

    public LinphoneNotifier(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void notify(final String s) {
        mHandler.post(new Runnable() {
            public void run() {
                // do something
            }
        });
    }

    public void registerVoIPSuccess() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG,"registerVoIPSuccess");
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_SUCCESS));
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
}