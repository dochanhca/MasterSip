package jp.newbees.mastersip.presenter;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.SendPurchaseResultToServerTask;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.purchase.IabResult;
import jp.newbees.mastersip.purchase.Inventory;
import jp.newbees.mastersip.purchase.Purchase;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

import static jp.newbees.mastersip.utils.Constant.InAppBilling.RC_REQUEST;
import static jp.newbees.mastersip.utils.Constant.InAppBilling.SKUS;
import static jp.newbees.mastersip.utils.Constant.InAppBilling.base64EncodedPublicKey;

/**
 * Created by vietbq on 12/21/16.
 */

public class TopPresenter extends BasePresenter {

    private static final String IN_APP_BILLING_TAG = "In app billing";

    private IabHelper iabHelper;
    private volatile int numberOfItemNeedConsume = 0;
    private boolean needPurchaseWithServer = false;
    private TopPresenterListener listener;

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Logger.e(IN_APP_BILLING_TAG, "Query inventory finished.");

            if (iabHelper == null) {
                getActivity().disMissLoading();
                return;
            }

            if (result.isFailure()) {
                Logger.e(IN_APP_BILLING_TAG, "Failed to query inventory: " + result);
                getActivity().disMissLoading();
                return;
            }

            Logger.e(IN_APP_BILLING_TAG, "Query inventory was successful.");

            for (String SKU : SKUS) {
                Purchase purchase = inventory.getPurchase(SKU);
                if (purchase != null && verifyDeveloperPayload(purchase)) {
                    Logger.e(IN_APP_BILLING_TAG, "We have " + purchase.getSku() + ". Consuming it.");
                    numberOfItemNeedConsume++;
                    iabHelper.consumeAsync(inventory.getPurchase(SKU), mConsumeFinishedListener);
                }
            }

            if (numberOfItemNeedConsume != 0) {
                return;
            }
            getActivity().disMissLoading();

        }
    };

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Logger.e(IN_APP_BILLING_TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            if (iabHelper == null) {
                getActivity().disMissLoading();
                return;
            }

            if (result.isFailure()) {
                listener.onPurchaseError(Constant.Error.IN_APP_PURCHASE_CANCEL,
                        "Error purchasing: " + result, "", "");
                getActivity().disMissLoading();
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                listener.onPurchaseError(Constant.Error.IN_APP_PURCHASE_CANCEL,
                        "Error purchasing. Authenticity verification failed.", "", "");
                getActivity().disMissLoading();
                return;
            }

            Logger.e(IN_APP_BILLING_TAG, "Purchase successful.");
            Logger.e(IN_APP_BILLING_TAG, "Starting consumption " + purchase.getSku());
            numberOfItemNeedConsume++;
            iabHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            numberOfItemNeedConsume--;
            if (iabHelper == null) {
                getActivity().disMissLoading();
                return;
            }

            if (result.isSuccess()) {
                Logger.e(IN_APP_BILLING_TAG, "success : " + purchase.getSku());

                if (needPurchaseWithServer) {
                    listener.onInAppBillingSuccess(purchase.getSku(), purchase.getToken());
                } else {
                    if (numberOfItemNeedConsume == 0) {
                        getActivity().disMissLoading();
                    }
                }
            } else {
                if (needPurchaseWithServer) {
                    listener.onPurchaseError(Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS, "Error while consuming: " + result,
                            purchase.getSku(), purchase.getToken());
                } else {
                    Logger.e(IN_APP_BILLING_TAG, "Error while consuming: " + result);
                }
            }
            needPurchaseWithServer = false;
        }
    };



    public interface TopPresenterListener {
        void onInAppBillingSuccess(String sku, String token);

        void onPurchaseError(int errorCode, String errorMessage, String sku, String token);

        void onSendPurchaseResultToServerSuccess(int point);

        void onSendPurchaseResultToServerError(int errorCode, String errorMessage);
    }

    public TopPresenter(BaseActivity context, TopPresenterListener listener) {
        super(context);
        this.listener = listener;
    }


    public IabHelper getIabHelper() {
        return iabHelper;
    }

    public final void requestPermissions() {
        for (Permission permission : Permission.values()) {
            if (ContextCompat.checkSelfPermission(context, permission.getPermission()) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(permission.getPermission(), permission.getResult());
                return;
            }
        }
    }

    private void requestPermission(String permission, int result) {
        Logger.e("TopPresenter", "[Permission] Asking for " + permission);
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, result);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof SendPurchaseResultToServerTask) {
            int point = ((SendPurchaseResultToServerTask) task).getDataResponse();
            listener.onSendPurchaseResultToServerSuccess(point);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof SendPurchaseResultToServerTask) {
            listener.onSendPurchaseResultToServerError(errorCode, errorMessage);
        }
    }

    public void disposeIabHelper() {
        iabHelper.dispose();
        iabHelper = null;
    }

    private BaseActivity getActivity() {
        return (BaseActivity) getContext();

    }

    public void setupForPurchase() {
        if (!deviceHasGoogleAccount()) {
            openPlayStore();
            return;
        }
        getActivity().showLoading();
        numberOfItemNeedConsume = 0;

        iabHelper = new IabHelper(getContext(), base64EncodedPublicKey);
        iabHelper.enableDebugLogging(true);

        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Logger.e(IN_APP_BILLING_TAG, "Problem setting up in-app billing: ");
                    Toast.makeText(context, "Problem setting up in-app billing: " + result, Toast.LENGTH_LONG).show();
                    TopPresenter.this.getActivity().disMissLoading();
                    iabHelper = null;
                    return;
                }

                if (iabHelper == null) {
                    TopPresenter.this.getActivity().disMissLoading();
                    return;
                }

                iabHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    public void performPurchaseItem(String id) {
        if (iabHelper == null) {
            setupForPurchase();
            return;
        }
        getActivity().showLoading();
        needPurchaseWithServer = true;
        String payload = ConfigManager.getInstance().getCurrentUser().getUserId();

        iabHelper.launchPurchaseFlow((Activity) getContext(), id, RC_REQUEST, mPurchaseFinishedListener, payload);
    }

    public void sendPurchaseResultToServer(PurchaseStatus purchaseStatus, String sku, String token) {
        getActivity().showLoading();
        SendPurchaseResultToServerTask sendPurchaseResultToServerTask = new
                SendPurchaseResultToServerTask(getContext(), sku, token, purchaseStatus);
        requestToServer(sendPurchaseResultToServerTask);
    }

    private boolean verifyDeveloperPayload(Purchase purchase) {
        String payload = purchase.getDeveloperPayload();
        return payload.equalsIgnoreCase(ConfigManager.getInstance().getCurrentUser().getUserId());
    }

    private boolean deviceHasGoogleAccount() {
        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accArray = accountManager.getAccountsByType("com.google");
        return accArray.length >= 1 ? true : false;
    }

    public void openPlayStore() {
        final String appPackageName = getActivity().getPackageName();
        try {
            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public enum PurchaseStatus {
        SUCCESS(1), FAIL(0), PENDING(2), NOT_SUCCESS(3);
        int value;

        PurchaseStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Permission {
        CAMERA(201, Manifest.permission.CAMERA),
        RECORD_AUDIO(202, Manifest.permission.RECORD_AUDIO),
        WRITE_EXTERNAL_STORAGE(203, Manifest.permission.WRITE_EXTERNAL_STORAGE),
        GET_ACCOUNTS(204, Manifest.permission.GET_ACCOUNTS),
        READ_PHONE_STATE(205, Manifest.permission.READ_PHONE_STATE);

        private final int result;
        private final String permissionName;

        Permission(int result, String permissionName) {
            this.result = result;
            this.permissionName = permissionName;
        }

        public int getResult() {
            return result;
        }

        public String getPermission() {
            return permissionName;
        }
    }
}
