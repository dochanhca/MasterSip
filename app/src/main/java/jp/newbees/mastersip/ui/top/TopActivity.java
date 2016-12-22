package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 12/6/16.
 */

public class TopActivity extends BaseActivity implements View.OnClickListener, TopPresenter.TopView {

    private TopPresenter topPresenter;
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
        topPresenter = new TopPresenter(getApplicationContext(),this);
        FilterItem filterItem = ConfigManager.getInstance().getFilterUser();
        topPresenter.requestFilterData(filterItem);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void didFilterData(ArrayList<UserItem> userItems) {

    }

    @Override
    public void didErrorFilterData(int errorCode, String errorMessage) {

    }
}
