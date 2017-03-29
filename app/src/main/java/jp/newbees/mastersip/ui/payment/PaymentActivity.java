package jp.newbees.mastersip.ui.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 3/27/17.
 */

public class PaymentActivity extends WrapperWithBottomNavigationActivity implements TopPresenter.TopPresenterListener {

    private TopPresenter topPresenter;

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
        topPresenter = new TopPresenter(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Logger.e(TAG, "Destroying helper.");
        if (getIabHelper() != null) {
            topPresenter.disposeIabHelper();
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
        topPresenter.sendPurchaseResultToServer(TopPresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String transection) {
        TopPresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = TopPresenter.PurchaseStatus.NOT_SUCCESS;
            topPresenter.sendPurchaseResultToServer(status, sku, transection);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = TopPresenter.PurchaseStatus.FAIL;
            topPresenter.sendPurchaseResultToServer(status, sku, transection);
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
        return topPresenter.getIabHelper();
    }

    public TopPresenter getPresenter() {
        return topPresenter;
    }
}
