package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by vietbq on 1/25/17.
 */

public class SettingCallFragment extends BaseFragment {
    @Override
    protected int layoutId() {
        return R.layout.fragment_setting_call;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        setFragmentTitle(getString(R.string.title_setting_call));
    }

    public static SettingCallFragment newInstance() {
        SettingCallFragment settingCallFragment = new SettingCallFragment();
        return settingCallFragment;
    }
}
