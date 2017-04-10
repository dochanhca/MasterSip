package jp.newbees.mastersip.ui.call.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceView;

import org.linphone.core.LinphoneCoreException;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.presenter.call.BaseHandleCallPresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.call.VideoCallFragment;
import jp.newbees.mastersip.ui.dialog.PaymentDialog;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 3/16/17.
 */

public abstract class BaseHandleCallActivity extends BaseActivity implements TopPresenter.TopPresenterListener,
        PaymentDialog.OnPaymentDialogClickListener, BaseHandleCallPresenter.CallView {
    protected static final String COMPETITOR = "COMPETITOR";
    protected static final String CALL_ID = "CALL_ID";

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

    private CallingFragment visibleFragment;

    public void setPresenter(BaseHandleCallPresenter presenter) {
        this.presenter = presenter;
    }

    public BaseHandleCallPresenter getPresenter() {
        return presenter;
    }

    public final void declineCall(String calId) {
        this.presenter.declineCall(calId);
    }

    public final void acceptCall(String calId) throws LinphoneCoreException {
        this.presenter.acceptCall(calId);
    }

    public final void terminalCall(String calId) {
        this.presenter.terminalCall(calId);
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    public void switchCamera(SurfaceView mCaptureView) {
        presenter.switchCamera(mCaptureView);
    }

    protected void useFrontCamera() {
        presenter.useFrontCamera(true);
    }

    public final void enableCamera(boolean enable) {
        presenter.enableCamera(enable);
    }

    public abstract int getCallType();

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
    }

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
            if (visibleFragment != null) {
                visibleFragment.onCoinChanged(coin);
            }
        }
    }

    @Override
    public void onCallPaused() {
        if (visibleFragment != null) {
            visibleFragment.onCallPaused();
        }
    }

    @Override
    public void onCallResuming() {
        if (visibleFragment != null) {
            visibleFragment.onCallResume();
        }
    }

    protected final void showWaitingFragment(WaitingFragment waitingFragment) {
        this.visibleFragment = waitingFragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, waitingFragment,
                WaitingFragment.class.getName()).commit();
    }

    protected final void showVideoCallFragment(UserItem competitor, String callID, int callType,
                                         boolean enableSpeaker, boolean enableMic) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        visibleFragment = VideoCallFragment.newInstance(competitor, callID, callType, enableSpeaker, enableMic);
        transaction.replace(R.id.fragment_container, visibleFragment,
                VideoCallFragment.class.getName()).commit();
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
        this.finish();
    }

    protected static Bundle getBundle(UserItem competitor, String callID) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(COMPETITOR, competitor);
        bundle.putString(CALL_ID, callID);
        return bundle;
    }

    protected void countingCallDuration() {
        visibleFragment.countingCallDuration();
    }

    protected void updateViewWhenVoiceConnected() {
        if (visibleFragment instanceof WaitingFragment) {
            ((WaitingFragment)visibleFragment).updateViewWhenVoiceConnected();
        }
    }

    public UserItem getCompetitor() {
        return competitor;
    }

    public String getCallId() {
        return callId;
    }

    public CallingFragment getVisibleFragment() {
        return visibleFragment;
    }
}
