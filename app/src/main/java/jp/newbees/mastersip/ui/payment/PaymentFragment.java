package jp.newbees.mastersip.ui.payment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.PaymentSuccessEvent;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 2/21/17.
 */

public class PaymentFragment extends BaseFragment {

    private static final String MY_URL = "MY_URL";
    private static final String TITLE = "TITLE";

    @BindView(R.id.webview)
    WebView webview;

    private TopPresenter topPresenter;

    public static PaymentFragment newInstance(String url, String title) {
        Bundle args = new Bundle();
        args.putString(MY_URL, url);
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

        topPresenter = ((TopActivity) getActivity()).getPresenter();
        topPresenter.setupForPurchase();
    }

    private void loadWebView(WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new PaymentWebViewClient());
        webview.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
        webview.loadUrl(getArguments().getString(MY_URL));
    }

    private void redirectToMyMenuFragment(String point) {
        EventBus.getDefault().postSticky(new PaymentSuccessEvent(point));
        getFragmentManager().popBackStackImmediate();
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void purchaseItem(String id) {
            topPresenter.performPurchaseItem(id);
        }
    }

    public class PaymentWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isPaymentSucces(url)) {
                checkPaymentSuccess(url);
                return true;
            }
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (isPaymentSucces(url)) {
                checkPaymentSuccess(url);
                return true;
            }
            return false;
        }

        public void checkPaymentSuccess(String url) {
            String point = splitUrl(url);
            redirectToMyMenuFragment(point);
        }

        private String splitUrl(String url) {
            String[] parts = url.split("point=");
            return parts[1];
        }

        public boolean isPaymentSucces(String url) {
            return url.contains(Constant.API.BIT_CASH_PAYMENT_SUCCESS) ||
                    url.contains(Constant.API.CREDIT_CASH_PAYMENT_SUCCESS);
        }
    }
}
