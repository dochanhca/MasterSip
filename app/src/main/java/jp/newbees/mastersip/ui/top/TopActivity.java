package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends BaseActivity implements View.OnClickListener {

    private NavigationLayoutGroup navigationLayoutGroup;
    private NavigationLayoutGroup.OnChildItemClickListener onNavigationChangeListener = new NavigationLayoutGroup.OnChildItemClickListener() {
        @Override
        public void onChildItemClick(View view, int position) {

        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_top;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.top_activity));

        navigationLayoutGroup = (NavigationLayoutGroup) findViewById(R.id.navigation_bar);
        navigationLayoutGroup.setOnChildItemClickListener(onNavigationChangeListener);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {

    }
}
