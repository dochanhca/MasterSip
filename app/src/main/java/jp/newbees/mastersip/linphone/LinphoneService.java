package jp.newbees.mastersip.linphone;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/9/17.
 */

public class LinphoneService extends Service{

    private Handler mHandler;
    private LinphoneHandler linphoneHandler;
    private final static String TAG = "LinphoneService";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e(TAG,"onCreate");
        mHandler = new Handler(Looper.getMainLooper());
        final LinphoneNotifier notifier = new LinphoneNotifier(mHandler);
        linphoneHandler = new LinphoneHandler(notifier, this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG,"OnStartCommand");
        SipItem sipItem = ConfigManager.getInstance().getCurrentUser().getSipItem();
        loginToVoIP(sipItem);
        return super.onStartCommand(intent, flags, startId);
    }

    private void loginToVoIP(final SipItem sipItem) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    linphoneHandler.loginVoIPServer(
                            sipItem.getExtension(), sipItem.getSecret());
                } catch (LinphoneCoreException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        linphoneHandler.stopMainLoop();
        Logger.e(TAG,"Stop Linphone Service");
    }


}
