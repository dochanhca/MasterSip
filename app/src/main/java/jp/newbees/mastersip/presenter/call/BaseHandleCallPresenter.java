package jp.newbees.mastersip.presenter.call;

import android.content.Context;
import android.view.SurfaceView;

import com.android.volley.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.event.CompetitorChangeBackgroundStateEvent;
import jp.newbees.mastersip.event.GSMCallEvent;
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.ReceivingCallEvent;
import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendDirectMessageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.JSONUtils;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ducpv on 3/10/17.
 */

public abstract class BaseHandleCallPresenter extends BasePresenter{

    private CallView view;

    public BaseHandleCallPresenter(Context context, CallView callView) {
        super(context);
        this.view = callView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        view.onCoinChanged(event.getCoin());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompetitorChangeBackgroundState(CompetitorChangeBackgroundStateEvent event) {
        view.onCompetitorChangeBGState(event.getAction());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRunOutOfCoinEvent(RunOutOfCoinEvent event) {
        view.onRunningOutOfCoin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGSMCallStateChanged(GSMCallEvent event) {
        if (event.getCallEvent() == GSMCallEvent.PAUSED_GSM_CALL_EVENT) {
            view.onCallPaused();
        }else if(event.getCallEvent() == GSMCallEvent.RESUME_GSM_CALL_EVENT) {
            view.onCallGSMResuming();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReceivingEndCallEvent(ReceivingCallEvent receivingCallEvent) {
        int event = receivingCallEvent.getCallEvent();
        if (event == ReceivingCallEvent.RELEASE_CALL
                || event == ReceivingCallEvent.END_CALL
                || event == ReceivingCallEvent.LINPHONE_ERROR
                ) {
                handleCallEnd();
        }
    }

    public final void enableSpeaker(boolean enable) {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().enableSpeaker(enable);
    }

    public final void enableMicrophone(boolean enable) {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().enableMic(enable);
    }

    public final void switchCamera(SurfaceView mCaptureView) {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().switchCamera(mCaptureView);
    }

    public final void useFrontCamera() {
        LinphoneHandler.getInstance().useFrontCamera();
    }

    public final void useFrontCameraAndUpdateCall() {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().useFrontCameraAndUpdateCall();
    }

    public final void enableCamera(boolean enable) {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().enableVideo(enable);
    }

    public final void acceptCall(String calId, int callType) throws LinphoneCoreException {
        boolean video = callType == Constant.API.VOICE_CALL ? false : true;
        LinphoneHandler.getInstance().acceptCall(video);
    }

    public final void declineCall() {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().declineCall();
    }

    public void terminalCall() {
        if (LinphoneHandler.getInstance() == null) {
            return;
        }
        LinphoneHandler.getInstance().terminalCall();
    }

    protected void handleCallEnd() {
        unregisterEvents();
        view.onCallEnd();
    }

    protected void handleCallConnected() {
        view.onCallConnected();
    }

    public void registerEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEvents() {
        EventBus.getDefault().unregister(this);
    }

    public void registerActivityMonitorListener(MyLifecycleHandler.ActivityMonitorListener listener) {
        MyLifecycleHandler.getInstance().registerActivityMonitorListener(listener);
    }

    public void unregisterActivityMonitorListener(MyLifecycleHandler.ActivityMonitorListener listener) {
        MyLifecycleHandler.getInstance().unregisterActivityMonitorListener(listener);
    }

    public void sendBackgroundState(String toExtension, String action) {
        try {
            String message = JSONUtils.genMessageChangeBackgroundState(action);
            SendDirectMessageTask messageTask = new SendDirectMessageTask(getApplicationContext(),
                    toExtension, message);

            messageTask.request(new Response.Listener<Boolean>() {
                @Override
                public void onResponse(Boolean response) {
                    Logger.e("BaseHandleCallPresenter", "sendBackgroundState success");
                }
            }, new BaseTask.ErrorListener() {
                @Override
                public void onError(int errorCode, String errorMessage) {
                    Logger.e("BaseHandleCallPresenter", "sendBackgroundState " + errorMessage);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface CallView {
        void onCallConnected();

        void onCallEnd();

        void onCoinChanged(int coin);

        void onRunningOutOfCoin();

        void onCallPaused();

        void onCallGSMResuming();

        void onCompetitorChangeBGState(String action);
    }
}
