package jp.newbees.mastersip.ui.call.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.presenter.call.BaseHandleCallPresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.ExitActivity;
import jp.newbees.mastersip.ui.call.VideoCallFragment;
import jp.newbees.mastersip.ui.call.VideoChatForFemaleFragment;
import jp.newbees.mastersip.ui.call.VideoChatForMaleFragment;
import jp.newbees.mastersip.ui.dialog.PaymentDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.MyLifecycleHandler;

/**
 * Created by thangit14 on 3/16/17.
 */

public abstract class BaseHandleCallActivity extends BaseActivity implements TopPresenter.TopPresenterListener,
        PaymentDialog.OnPaymentDialogClickListener, BaseHandleCallPresenter.CallView {

    protected static final String COMPETITOR = "COMPETITOR";
    protected static final String CALL_ID = "CALL_ID";
    protected static final String RUN_FROM = "RUN_FROM";
    public static final int RUN_FROM_BG = 1;
    public static final int RUN_FROM_FG = 2;

    private UserItem competitor;
    private String callId;

    /**
     * use for handle call
     */
    private BaseHandleCallPresenter presenter;

    /**
     * use for in-app purchase
     */
    private TopPresenter topPresenter;

    private CallingFragment callingFragment;

    private WaitingFragment waitingFragment;

    private int runFrom;

    public void setPresenter(BaseHandleCallPresenter presenter) {
        this.presenter = presenter;
    }

    public BaseHandleCallPresenter getPresenter() {
        return presenter;
    }

    public final void declineCall() {
        this.presenter.declineCall();
    }

    public final void acceptCall(String calId, int callType) throws LinphoneCoreException {
        this.presenter.acceptCall(calId,  callType);
    }

    public final void terminalCall() {
        this.presenter.terminalCall();
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void enableMicrophone(boolean enable) {
        this.presenter.enableMicrophone(enable);
    }

    public void switchCamera(SurfaceView mCaptureView) {
        presenter.switchCamera(mCaptureView);
    }

    protected void useFrontCamera() {
        presenter.useFrontCameraAndUpdateCall();
    }

    public final void enableCamera(boolean enable) {
        presenter.enableCamera(enable);
    }

    public abstract int getCallType();

    public UserItem getCompetitor() {
        return competitor;
    }

    public String getCallId() {
        return callId;
    }

    public CallingFragment getCallingFragment() {
        return callingFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_calling;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        competitor = getIntent().getExtras().getParcelable(COMPETITOR);
        callId = getIntent().getExtras().getString(CALL_ID);
        runFrom = getIntent().getExtras().getInt(RUN_FROM, RUN_FROM_FG);

        onShowWaitingFragment();
        initCallingFragment();
    }

    private void initCallingFragment() {
        switch (getCallType()) {
            case Constant.API.VOICE_CALL:
                break;
            case Constant.API.VIDEO_CALL:
                initVideoCallFragment();
                break;
            case Constant.API.VIDEO_CHAT_CALL:
                initVideoChatFragment();
                break;
        }
    }

    protected abstract void onShowWaitingFragment();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
        if (getIabHelper() != null) {
            topPresenter.disposeIabHelper();
        }
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getIabHelper() == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (!getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.e("PaymentDialog:", "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onCoinChanged(int coin) {
        if (competitor.getGender() == UserItem.MALE) {
            if (callingFragment != null) {
                callingFragment.onCoinChanged(coin);
            }
        }
    }

    @Override
    public void onCallPaused() {
        if (callingFragment != null) {
            callingFragment.onCallPaused();
        }
    }

    @Override
    public void onCallGSMResuming() {
        if (callingFragment != null) {
            callingFragment.onCallResume();
        }
    }

    @Override
    public void onCallConnected() {
        updateUIWhenInCall();
    }

    protected final void updateUIWhenInCall() {
        switch (getCallType()) {
            case Constant.API.VOICE_CALL:
                // in voice call, we only change the ui of waiting fragment to calling
                callingFragment = waitingFragment;
                break;
            case Constant.API.VIDEO_CALL:
            case Constant.API.VIDEO_CHAT_CALL:
                hideWaitingFragment();
                showCallingFragment();
                break;
        }
//        callingFragment.updateUIWhenStartCalling();
    }

    protected void initVideoChatFragment() {
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
            initVideoChatFragmentForMale();
        } else {
            initVideoChatFragmentForFemale();
        }
    }

    private void showCallingFragment() {
        addCallingFragment(callingFragment, "Calling");
        findViewById(R.id.container_fragment_calling).setVisibility(View.VISIBLE);
    }

    private void hideWaitingFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(WaitingFragment.class.getName());
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    protected final void showWaitingFragment(WaitingFragment waitingFragment) {
        this.waitingFragment = waitingFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_fragment_waiting, waitingFragment,
                WaitingFragment.class.getName()).commit();
    }

    protected final void initVideoCallFragment() {
        callingFragment = VideoCallFragment.newInstance(competitor, getCallId());
//        addCallingFragment(callingFragment, VideoCallFragment.class.getName());
    }

    private void initVideoChatFragmentForMale() {
        callingFragment = VideoChatForMaleFragment.newInstance(competitor, getCallId());
        addCallingFragment(callingFragment, VideoChatForMaleFragment.class.getName());
    }

    private void initVideoChatFragmentForFemale() {
        callingFragment = VideoChatForFemaleFragment.newInstance(competitor, getCallId());
        addCallingFragment(callingFragment, VideoChatForFemaleFragment.class.getName());
    }

    private void addCallingFragment(CallingFragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container_fragment_calling, fragment,
                tag).commit();
    }


    private IabHelper getIabHelper() {
        if (topPresenter == null) {
            return null;
        }
        return topPresenter.getIabHelper();
    }

    @Override
    public void onRunningOutOfCoin() {
        topPresenter = new TopPresenter(this, this);
        topPresenter.setupForPurchase();
        PaymentDialog.openPaymentDialog(getSupportFragmentManager());
    }

    @Override
    public void onPaymentItemClick(PaymentAdOnItem item) {
        topPresenter.performPurchaseItem(item.getId());
    }

    @Override
    public void onInAppBillingSuccess(String sku, String token) {
        showLoading();
        topPresenter.sendPurchaseResultToServer(TopPresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String token) {
        TopPresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = TopPresenter.PurchaseStatus.NOT_SUCCESS;
            topPresenter.sendPurchaseResultToServer(status, sku, token);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = TopPresenter.PurchaseStatus.FAIL;
            topPresenter.sendPurchaseResultToServer(status, sku, token);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_CANCEL) {
            disMissLoading();
            showMessageDialog(getString(R.string.cancel_purchase));
        }
    }

    @Override
    public void onSendPurchaseResultToServerSuccess(int point) {
        disMissLoading();
        showMessageDialog(String.format(getString(R.string.purchase_success), point + ""));
    }

    @Override
    public void onSendPurchaseResultToServerError(int errorCode, String errorMessage) {
        // Send Payment Result to server error
        disMissLoading();
    }

    @Override
    public void onCallEnd() {
        if (MyLifecycleHandler.getNumberOfActivity() == 1 && runFrom == RUN_FROM_BG) {
            ExitActivity.exitApplication(this);
        } else {
            BaseHandleCallActivity.this.finish();
        }
    }

    protected static Bundle getBundle(UserItem competitor, String callID) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(COMPETITOR, competitor);
        bundle.putString(CALL_ID, callID);
        return bundle;
    }
}
