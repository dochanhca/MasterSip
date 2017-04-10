package jp.newbees.mastersip.ui.call.base;

import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;

import jp.newbees.mastersip.thread.MyCountingTimerThread;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 4/7/17.
 * inherited by waiting fragment(voice calling) and video calling, video chat calling
 */

public abstract class CallingFragment extends BaseFragment{
    private MyCountingTimerThread countingCallDurationThread;

    private Handler countingCallDurationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onCallingBreakTime(msg);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countingCallDurationThread != null) {
            countingCallDurationThread.turnOffCounting();
        }
    }

    public void countingCallDuration() {
        countingCallDurationThread = new MyCountingTimerThread(countingCallDurationHandler);
        new Thread(countingCallDurationThread).start();
    }

    protected abstract void onCallingBreakTime(Message msg);

    public abstract void onCoinChanged(int coin);

    public abstract void onCallResume();

    public abstract void onCallPaused();

    public final void muteMicrophone(boolean mute) {
        getCallActivity().muteMicrophone(mute);
    }

    public final void terminalCall(String calId) {
        getCallActivity().terminalCall(calId);
    }

    public final void enableSpeaker(boolean enable) {
        getCallActivity().enableSpeaker(enable);
    }

    public void switchCamera(SurfaceView mCaptureView) {
        getCallActivity().switchCamera(mCaptureView);
    }

    public final void enableCamera(boolean enable) {
        getCallActivity().enableCamera(enable);
    }

    public  final BaseHandleCallActivity getCallActivity() {
        BaseHandleCallActivity activity = (BaseHandleCallActivity) getActivity();
        return activity;
    }
}
