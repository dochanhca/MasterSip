package jp.newbees.mastersip.ui.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.purchase.IabResult;
import jp.newbees.mastersip.purchase.Purchase;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 2/21/17.
 */

public class PaymentFragment extends BaseFragment {

    private static final String MY_URL = "MY_URL";
    private static final String TITLE = "TITLE";
    @BindView(R.id.webview)
    WebView webview;

    static final int RC_REQUEST = 10001;

    private IabHelper mHelper;

    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqnLWbhSgEG1u+0PZ3frkI7EeRge5iD8gOthik3llKyCbSpHPCY/9YjMIXrbe97XQZj7vp2MUeX4DHMB7sBNHT/T2rcpHvoezTZrUiUEPb4rTodEd9c1Ks1pcOEJ+cZpBRHOVRkG1Y+ZM4ftvvYnfsQE9xdaGAhWm+BJDoFmBP9YNwSyLI4WC07qp4s38a9hpB3XWXJG6p20oCyhVAyY/vazW53BpWlupyGpfI4C5Au8rwOGbJ/2scl0xAfKsxQxj2pNPU7yrs1XLDUjdPiS7swSuVp803Fu8v5o1CWRnQhEXi/XPjtEqSM/MRS04JP3PoV/YjrdgYboskqIRbRbZcwIDAQAB";

    public static PaymentFragment newInstance(String url, String title) {
        Bundle args = new Bundle();
        args.putString(MY_URL,url);
        args.putString(TITLE, title);
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WebView getWebview() {
        return webview;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        setFragmentTitle(getArguments().getString(TITLE));

        loadWebView(webview);

        setupForPurchase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.e(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.e(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private void loadWebView(WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.addJavascriptInterface(new WebAppInterface(getContext()),"Android");
        webview.loadUrl(getArguments().getString(MY_URL));
    }

    private void setupForPurchase() {
        mHelper = new IabHelper(getContext(), base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                if (mHelper == null) {
                    return;
                }
            }
        });
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void purchaseItem(String id) {
            Toast.makeText(mContext, "purchase " + id, Toast.LENGTH_SHORT).show();
            performPurchaseItem(id);
        }
    }

    private void performPurchaseItem(String id) {
        String payload = ConfigManager.getInstance().getCurrentUser().getUserId();

        mHelper.launchPurchaseFlow(getActivity(), id, RC_REQUEST, mPurchaseFinishedListener, payload);
    }

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.e(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.e(TAG, "Purchase successful.");

            Log.e(TAG, "Starting consumption "+purchase.getSku());
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;

            if (result.isSuccess()) {
                Toast.makeText(getContext(), "success : " + purchase.getSku(), Toast.LENGTH_SHORT).show();
            } else {
                complain("Error while consuming: " + result);
            }
        }
    };

    private boolean verifyDeveloperPayload(Purchase purchase) {
        String payload = purchase.getDeveloperPayload();
        return payload.equalsIgnoreCase(ConfigManager.getInstance().getCurrentUser().getUserId());
    }


    private void complain(String message) {
        Log.e(TAG, "**** Master Sip purchase error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

}
