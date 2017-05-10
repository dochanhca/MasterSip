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
import jp.newbees.mastersip.presenter.InAppPurchasePresenter;
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

public abstract class BaseHandleCallActivity extends BaseActivity implements InAppPurchasePresenter.InAppPurchaseListener,
        PaymentDialog.OnPaymentDialogClickListener, BaseHandleCallPresenter.CallView, MyLifecycleHandler.ActivityMonitorListener {

    protected static final String KEY_COMPETITOR = "KEY_COMPETITOR";
    protected static final String CALL_ID = "CALL_ID";
    protected static final String RUN_FROM = "RUN_FROM";
    public static final int RUN_FROM_BG = 1;
    public static final int RUN_FROM_FG = 2;

    private UserItem competitor;
    private String callId;
    private boolean isUpadedView;

    /**
     * use for handle call
     */
    private BaseHandleCallPresenter presenter;

    /**
     * use for in-app purchase
     */
    private InAppPurchasePresenter inAppPurchasePresenter;

    private CallingFragment callingFragment;

    private WaitingFragment waitingFragment;

    private int runFrom;

    public final void declineCall() {
        this.presenter.declineCall();
    }

    public final void acceptCall(String calId, int callType) throws LinphoneCoreException {
        this.presenter.acceptCall(calId, callType);
        this.presenter.registerActivityMonitorListener(this);
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

    @Override
    protected int layoutId() {
        return R.layout.activity_calling;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        competitor = getIntent().getExtras().getParcelable(KEY_COMPETITOR);
        callId = getIntent().getExtras().getString(CALL_ID);
        runFrom = getIntent().getExtras().getInt(RUN_FROM, RUN_FROM_FG);

        onShowWaitingFragment();
        initCallingFragment();
        presenter = getPresenter();
    }

    protected abstract BaseHandleCallPresenter getPresenter();

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
            default:
                break;
        }
    }

    protected abstract void onShowWaitingFragment();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
        presenter.unregisterActivityMonitorListener(this);
        if (getIabHelper() != null) {
            inAppPurchasePresenter.disposeIabHelper();
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
        if (competitor.getGender() == UserItem.MALE && callingFragment != null) {
            callingFragment.onCoinChanged(coin);
        }
    }

    @Override
    public void onCompetitorChangeBGState(String action) {
        callingFragment.onCompetitorChangeBGState(action);
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
        if (isUpadedView == false) {
            isUpadedView = true;
            presenter.registerActivityMonitorListener(this);
            updateUIWhenInCall();
        }
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
            default:
                break;
        }
        callingFragment.updateUIWhenStartCalling();
    }

    protected void initVideoChatFragment() {
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
            initVideoChatFragmentForMale();
        } else {
            initVideoChatFragmentForFemale();
        }
    }

    private void showCallingFragment() {
        findViewById(R.id.container_fragment_calling).setVisibility(View.VISIBLE);
    }

    private void hideWaitingFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(WaitingFragment.class.getName());
        if (fragment != null) {
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
        addCallingFragment(callingFragment, VideoCallFragment.class.getName());
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
        if (inAppPurchasePresenter == null) {
            return null;
        }
        return inAppPurchasePresenter.getIabHelper();
    }

    @Override
    public void onRunningOutOfCoin() {
        inAppPurchasePresenter = new InAppPurchasePresenter(this, this);
        inAppPurchasePresenter.setupForPurchase();
        PaymentDialog.openPaymentDialog(getSupportFragmentManager());
    }

    @Override
    public void onPaymentItemClick(PaymentAdOnItem item) {
        inAppPurchasePresenter.performPurchaseItem(item.getId());
    }

    @Override
    public void onInAppBillingSuccess(String sku, String token) {
        showLoading();
        inAppPurchasePresenter.sendPurchaseResultToServer(InAppPurchasePresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String token) {
        InAppPurchasePresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = InAppPurchasePresenter.PurchaseStatus.NOT_SUCCESS;
            inAppPurchasePresenter.sendPurchaseResultToServer(status, sku, token);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = InAppPurchasePresenter.PurchaseStatus.FAIL;
            inAppPurchasePresenter.sendPurchaseResultToServer(status, sku, token);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_CANCEL) {
            disMissLoading();
            showMessageDialog(getString(R.string.cancel_purchase));
        }
    }

    @Override
    public void onSendPurchaseResultToServerSuccess(int point) {
        disMissLoading();
        showMessageDialog(String.format(getString(R.string.purchase_success), Integer.toString(point)));
    }

    @Override
    public void onSendPurchaseResultToServerError(int errorCode, String errorMessage) {
        // Send Payment Result to server error
        disMissLoading();
    }

    @Override
    public void onCallEnd() {
        if (MyLifecycleHandler.getInstance().getNumberOfActivity() == 1 && runFrom == RUN_FROM_BG) {
            ExitActivity.exitApplication(this);
        } else {
            BaseHandleCallActivity.this.finish();
        }
    }

    @Override
    public void onForegroundMode() {
        presenter.sendBackgroundState(getCompetitor().getSipItem().getExtension(), Constant.SOCKET.ACTION_ENTER_FOREGROUND);
        presenter.enableCamera(true);
    }

    @Override
    public void onBackgroundMode() {
        presenter.sendBackgroundState(getCompetitor().getSipItem().getExtension(), Constant.SOCKET.ACTION_ENTER_BACKGROUND);
        presenter.enableCamera(false);
    }

    protected static Bundle getBundle(UserItem competitor, String callID) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_COMPETITOR, competitor);
        bundle.putString(CALL_ID, callID);
        return bundle;
    }
}
