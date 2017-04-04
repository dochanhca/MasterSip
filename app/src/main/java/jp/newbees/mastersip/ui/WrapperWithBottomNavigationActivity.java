package jp.newbees.mastersip.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 2/7/17.
 */

public abstract class WrapperWithBottomNavigationActivity extends CallActivity implements BaseActivity.BottomNavigation {

    private NavigationLayoutGroup.OnChildItemClickListener onChildItemClickListener = new NavigationLayoutGroup.OnChildItemClickListener() {
        @Override
        public void onChildItemClick(View view, int position) {
            ConfigManager.getInstance().setCurrentTabInRootNavigater(position);
            goTopActivity();
        }

        private void goTopActivity() {
            Intent intent = new Intent(getApplicationContext(), TopActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_wrapper_with_bottom_navgiation;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        navigationLayoutGroup.setOnChildItemClickListener(onChildItemClickListener);
        setUnreadMessageValue(ConfigManager.getInstance().getUnreadMessage());
        navigationLayoutGroup.setSelectedItem(ConfigManager.getInstance().getCurrentTabInRootNavigater());
    }

    public void showFragmentContent(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_search_container, fragment, tag).commit();
    }
}
