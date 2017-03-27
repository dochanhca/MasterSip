package jp.newbees.mastersip.ui.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.PaymentSuccessEvent;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 2/21/17.
 */

public class PaymentFragment extends BaseFragment implements BaseActivity.OnBackPressed {

    private static final String IS_FROM_ACTIVITY = "IS_FROM_ACTIVITY";
    public static final String POINT = "POINT";

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.txt_action_bar_title)
    TextView txtActionBarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;

    private TopPresenter topPresenter;
    private boolean isFromActivity;

    public static PaymentFragment newInstance(boolean isFromActivity) {
        Bundle args = new Bundle();
        args.putBoolean(IS_FROM_ACTIVITY, isFromActivity);
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        txtActionBarTitle.setText(Constant.Application.PURCHASE);
        isFromActivity = getArguments().getBoolean(IS_FROM_ACTIVITY);

        loadWebView(webview);

        if (isFromActivity) {
            topPresenter = ((PaymentActivity) getActivity()).getPresenter();
        } else {
            topPresenter = ((TopActivity) getActivity()).getPresenter();
        }
        topPresenter.setupForPurchase();
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnBackPressed(this);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        setOnBackPressed(null);
        imgBack.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if (webview.getUrl().contains(Utils.getURLBuyPoint())) {
            webview.goBack();
        } else if (isFromActivity){
            getActivity().finish();
        } else {
            getFragmentManager().popBackStackImmediate();
        }
    }

    private void loadWebView(WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new PaymentWebViewClient());
        webview.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
        webview.loadUrl(Utils.getURLChosePaymentType());
    }

    private void handlePaymentSuccess(String point) {
        if (isFromActivity) {
            Intent intent = new Intent();
            intent.putExtra(POINT, point);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            EventBus.getDefault().postSticky(new PaymentSuccessEvent(point));
            getFragmentManager().popBackStackImmediate();
        }
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
            if (isPaymentSuccess(url)) {
                checkPaymentSuccess(url);
                return true;
            }
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (isPaymentSuccess(url)) {
                checkPaymentSuccess(url);
                return true;
            }
            return false;
        }

        public void checkPaymentSuccess(String url) {
            String point = splitUrl(url);
            handlePaymentSuccess(point);
        }

        private String splitUrl(String url) {
            String[] parts = url.split("point=");
            return parts[1];
        }

        public boolean isPaymentSuccess(String url) {
            return url.contains(Constant.API.BIT_CASH_PAYMENT_SUCCESS) ||
                    url.contains(Constant.API.CREDIT_CASH_PAYMENT_SUCCESS);
        }
    }
}
