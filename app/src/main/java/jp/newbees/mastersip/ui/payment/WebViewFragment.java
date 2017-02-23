package jp.newbees.mastersip.ui.payment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 2/21/17.
 */

public class WebViewFragment extends BaseFragment {

    private static final String MY_URL = "MY_URL";
    private static final String TITLE = "TITLE";
    @BindView(R.id.webview)
    WebView webview;

    public static WebViewFragment newInstance(String url, String title) {
        Bundle args = new Bundle();
        args.putString(MY_URL,url);
        args.putString(TITLE, title);
        WebViewFragment fragment = new WebViewFragment();
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

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(getArguments().getString(MY_URL));
    }
}
