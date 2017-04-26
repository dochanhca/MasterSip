package jp.newbees.mastersip.ui.call.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceView;
import android.widget.TextView;

import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.thread.MyCountingTimerThread;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 4/7/17.
 * inherited by waiting fragment(voice calling) and video calling, video chat calling
 */

public abstract class CallingFragment extends BaseFragment {

    protected static final String COMPETITOR = "USER ITEM";
    protected static final String CALL_ID = "CALL_ID";
    protected static final String CALL_TYPE = "CALL TYPE";

    private static final int BREAK_TIME_TO_HIDE_ACTION = 5;
    private static final String ID_TIMER_HIDE_ACTION = "ID_TIMER_HIDE_ACTION";

    private MyCountingTimerThread countingCallDurationThread;
    private MyCountingTimerThread myCountingThreadToHideAction;

    private Handler countingCallDurationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            onCallingBreakTime(msg);
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj.toString().equalsIgnoreCase(ID_TIMER_HIDE_ACTION)) {
                if (CallingFragment.this instanceof CountableToHideAction) {
                    ((CountableToHideAction) CallingFragment.this).onBreakTimeToHide();
                }
                if (myCountingThreadToHideAction != null) {
                    myCountingThreadToHideAction.reset();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        int coin = ConfigManager.getInstance().getCurrentUser().getCoin();
        onCoinChanged(coin);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countingCallDurationThread != null) {
            countingCallDurationThread.turnOffCounting();
        }
        if (myCountingThreadToHideAction != null) {
            myCountingThreadToHideAction.turnOffCounting();
        }
    }

    protected final void startCountingToHideAction() {
        myCountingThreadToHideAction = new MyCountingTimerThread(handler, ID_TIMER_HIDE_ACTION, BREAK_TIME_TO_HIDE_ACTION);
        new Thread(myCountingThreadToHideAction).start();
    }

    protected final void resetCountingToHideAction() {
        if (myCountingThreadToHideAction != null) {
            myCountingThreadToHideAction.reset();
        }
    }

    protected final void countingCallDuration() {
        countingCallDurationThread = new MyCountingTimerThread(countingCallDurationHandler);
        new Thread(countingCallDurationThread).start();
    }

    public final void onCoinChanged(int coin) {
        if (isDetached() || getTxtPoint() == null) {
            return;
        }
        StringBuilder point = new StringBuilder();
        point.append(" ")
                .append(String.valueOf(coin))
                .append(getString(R.string.pt));
        getTxtPoint().setText(point.toString());
    }

    protected abstract void onCallingBreakTime(Message msg);

    protected abstract TextView getTxtPoint();

    protected abstract void onCallResume();

    protected abstract void onCallPaused();

    protected abstract void updateUIWhenStartCalling();

    protected final void enableMicrophone(boolean enable) {
        if (getCallActivity() != null) {
            getCallActivity().enableMicrophone(enable);
        }
    }

    protected final void terminalCall() {
        if (getCallActivity() != null) {
            getCallActivity().terminalCall();
        }
    }

    protected final void declineCall() {
        if (getCallActivity() != null) {
            getCallActivity().declineCall();
        }
    }

    protected final void acceptCall(String callId, int callType) throws LinphoneCoreException {
        if (getCallActivity() != null) {
            getCallActivity().acceptCall(callId, callType);
        }
    }

    protected final void enableSpeaker(boolean enable) {
        if (getCallActivity() != null) {
            getCallActivity().enableSpeaker(enable);
        }
    }

    protected void switchCamera(SurfaceView mCaptureView) {
        if (getCallActivity() != null) {
            getCallActivity().switchCamera(mCaptureView);
        }
    }

    protected void useFrontCamera() {
        if (getCallActivity() != null) {
            getCallActivity().useFrontCamera();
        }
    }

    protected final void enableCamera(boolean enable) {
        if (getCallActivity() != null) {
            getCallActivity().enableCamera(enable);
        }
    }

    protected final boolean isSpeakerEnalbed() {
        return LinphoneHandler.getInstance().isSpeakerEnalbed();
    }

    protected final boolean isMicEnalbed() {
        return LinphoneHandler.getInstance().isMicEnabled();
    }

    protected final BaseHandleCallActivity getCallActivity() {
        if (getActivity() instanceof BaseHandleCallActivity) {
            BaseHandleCallActivity activity = (BaseHandleCallActivity) getActivity();
            return activity;
        } else {
            Logger.e("CallingFragment", " The activity contain calling fragment must extend BaseHandleCallActivity");
            return null;
        }
    }

    public interface CountableToHideAction {
        void onBreakTimeToHide();
    }
}
