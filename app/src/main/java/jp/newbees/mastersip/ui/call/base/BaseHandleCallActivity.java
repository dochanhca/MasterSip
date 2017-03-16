package jp.newbees.mastersip.ui.call.base;

import android.support.v4.app.FragmentTransaction;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleCallPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.VideoCallFragment;

/**
 * Created by thangit14 on 3/16/17.
 */

public abstract class BaseHandleCallActivity extends BaseActivity {
    private BaseHandleCallPresenter presenter;
    private VideoCallFragment videoCallFragment;

    public void setPresenter(BaseHandleCallPresenter presenter) {
        this.presenter = presenter;
    }

    public BaseHandleCallPresenter getPresenter() {
        return presenter;
    }

    public final void rejectCall(String caller, int callType, String calId) {
        this.presenter.rejectCall(caller, callType, calId);
    }

    public final void acceptCall(String calId) {
        this.presenter.acceptCall(calId);
    }

    public final void endCall(String caller, int callType, String calId) {
        this.presenter.endCall(caller, callType, calId);
    }

    public final void endCall(UserItem callee, int callType) {
        this.presenter.endCall(callee, callType);
    }

    public final void endCall() {
        presenter.endCall(getCurrentUser(), getCallType());
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    public void switchCamera() {
        presenter.switchCamera();
    }

    protected void useFrontCamera() {
        presenter.useFrontCamera();
    }

    public final void enableCamera(boolean enable) {
        presenter.enableCamera(enable);
    }

    public abstract UserItem getCurrentUser();

    public abstract int getCallType();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    protected void showVideoCallFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        videoCallFragment = VideoCallFragment.newInstance(getCurrentUser(), getCallType());
        transaction.replace(R.id.fragment_container, videoCallFragment,
                VideoCallFragment.class.getName()).commit();
    }

    public VideoCallFragment getVideoCallFragment() {
        return videoCallFragment;
    }
}
