package jp.newbees.mastersip.ui.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.presenter.InAppPurchasePresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 3/27/17.
 */

public class PaymentActivity extends WrapperWithBottomNavigationActivity implements InAppPurchasePresenter.InAppPurchaseListener {

    private InAppPurchasePresenter inAppPurchasePresenter;

    public static void startActivityForResult(Activity activity,int requestCode) {
        Intent intent = new Intent(activity, PaymentActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity().getApplicationContext(), PaymentActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        hideActionBar();
        PaymentFragment paymentFragment = PaymentFragment.newInstance(true);
        showFragmentContent(paymentFragment, PaymentFragment.class.getName());
        inAppPurchasePresenter = new InAppPurchasePresenter(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Logger.e(TAG, "Destroying helper.");
        if (getIabHelper() != null) {
            inAppPurchasePresenter.disposeIabHelper();
        }
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
            Log.e(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onInAppBillingSuccess(String sku, String token) {
        showLoading();
        inAppPurchasePresenter.sendPurchaseResultToServer(InAppPurchasePresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String transection) {
        InAppPurchasePresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = InAppPurchasePresenter.PurchaseStatus.NOT_SUCCESS;
            inAppPurchasePresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = InAppPurchasePresenter.PurchaseStatus.FAIL;
            inAppPurchasePresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_CANCEL) {
            disMissLoading();
            showMessageDialog(getString(R.string.cancel_purchase));
        }
    }

    @Override
    public void onSendPurchaseResultToServerSuccess(int point) {
        disMissLoading();
        Intent intent = new Intent();
        intent.putExtra(PaymentFragment.POINT, String.valueOf(point));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSendPurchaseResultToServerError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    private IabHelper getIabHelper() {
        return inAppPurchasePresenter.getIabHelper();
    }

    public InAppPurchasePresenter getPresenter() {
        return inAppPurchasePresenter;
    }

}
