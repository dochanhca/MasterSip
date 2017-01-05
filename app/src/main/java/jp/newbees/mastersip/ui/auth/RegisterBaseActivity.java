package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.eventbus.RegisterVoIPEvent;
import jp.newbees.mastersip.linphone.LinPhoneNotifier;
import jp.newbees.mastersip.test.LinphoneHandler;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/4/17.
 */

public abstract class RegisterBaseActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    private Handler mHandler =  new Handler() ;
    private LinphoneHandler linphoneHandler;
    private String sipAddress;
    private String sipPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LinPhoneNotifier notifier = new LinPhoneNotifier(mHandler);

        linphoneHandler = new LinphoneHandler(notifier, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * @param event listener Register VoIP response
     */
    @Subscribe(sticky = true)
    public void onRegisterVoIPEvent(RegisterVoIPEvent event) {
        Logger.e(TAG, "onRegisterVoIPEvent receive: " + event.getResponseCode());
        if (event.getResponseCode() == RegisterVoIPEvent.REGISTER_SUCCESS) {
            saveLoginState();
            startTopScreenWithNewTask();
        } else {
            //
        }
        disMissLoading();
    }

    protected void registerLinPhone(String sipAddress, String sipPassword) {
        LinPhoneRegisterThread registerThread = new LinPhoneRegisterThread();
        this.sipAddress = sipAddress;
        this.sipPassword = sipPassword;
        registerThread.start();
    }

    protected void startTopScreenWithNewTask() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }


    private void saveLoginState() {
        getEditor().putBoolean(Constant.Application.LOGIN_FLAG, true);
        getEditor().commit();
    }

    private class LinPhoneRegisterThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                linphoneHandler.loginVoIPServer(
                        sipAddress, sipPassword);
            } catch (LinphoneCoreException e) {
                Logger.e(TAG, e.getMessage());
            }
        }
    }
}
