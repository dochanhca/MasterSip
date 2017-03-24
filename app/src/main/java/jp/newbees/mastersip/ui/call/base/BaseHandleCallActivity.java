package jp.newbees.mastersip.ui.call.base;

import android.content.Intent;
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
import jp.newbees.mastersip.ui.dialog.PaymentDialog;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 3/16/17.
 */

public abstract class BaseHandleCallActivity extends BaseActivity implements TopPresenter.TopPresenterListener,
        PaymentDialog.OnPaymentDialogClickListener, BaseHandleCallPresenter.CallView {
    /**
     * use for handle call
     */
    private BaseHandleCallPresenter presenter;

    /**
     * use for in-app purchase
     */
    private TopPresenter topPresenter;

    public void setPresenter(BaseHandleCallPresenter presenter) {
        this.presenter = presenter;
    }

    public BaseHandleCallPresenter getPresenter() {
        return presenter;
    }

    public final void rejectCall(String caller, int callType, String calId) {
        this.presenter.rejectCall(caller, callType, calId);
    }

    public final void acceptCall(String calId) throws LinphoneCoreException {
        this.presenter.acceptCall(calId);
    }

    public final void endCall(String caller, int callType, String calId) {
        this.presenter.endCall(caller, callType, calId);
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

    public void switchCamera(SurfaceView mCaptureView) {
        presenter.switchCamera(mCaptureView);
    }

    protected void useFrontCamera() {
        presenter.useFrontCamera(true);
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
}
