package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by ducpv on 12/7/16.
 */

public class TipPageActivity extends BaseActivity implements View.OnClickListener {

    private WebView wbTipPage;

    @Override
    protected int layoutId() {
        return R.layout.activity_tip_page;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        wbTipPage = (WebView) findViewById(R.id.wb_tip_page);

        wbTipPage.getSettings().setJavaScriptEnabled(true);
        wbTipPage.loadUrl(Constant.API.TIP_PAGE);

        initHeader(getString(R.string.tip_page_title));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        wbTipPage.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(Constant.API.TIP_PAGE_DIRECTION)) {
                    redirectToUpdateProfile();
                }
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.equals(Constant.API.TIP_PAGE_DIRECTION)) {
                    redirectToUpdateProfile();
                }
                return true;
            }
        });
    }

    private void redirectToUpdateProfile() {
        Intent intent = new Intent(getApplicationContext(),
                RegisterProfileFemaleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
    }
}
