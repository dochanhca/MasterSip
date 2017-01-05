package jp.newbees.mastersip.linphone;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.linphone.core.tutorials.TutorialNotifier;

import jp.newbees.mastersip.eventbus.RegisterVoIPEvent;

/**
 * Created by ducpv on 1/4/17.
 */

public class LinPhoneNotifier extends TutorialNotifier {

    private Handler mHandler;

    public LinPhoneNotifier(Handler mHandler) {
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
                // do something
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_SUCCESS));
            }
        });
    }

    public void registerVoIPFailed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // do something
                EventBus.getDefault().post(new RegisterVoIPEvent(RegisterVoIPEvent.REGISTER_FAILED));
            }
        });
    }
}
