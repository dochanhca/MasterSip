package jp.newbees.mastersip.ui.mymenu;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SettingPushItem;
import jp.newbees.mastersip.presenter.mymenu.SettingPushPresenter;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 5/11/17.
 */

public class SettingPushFragment extends BaseFragment implements SettingPushPresenter.SettingPushView {

    @BindView(R.id.cb_push_from_admin)
    CheckBox cbPushFromAdmin;
    @BindView(R.id.cb_all_user)
    CheckBox cbAllUser;
    @BindView(R.id.cb_following_user)
    CheckBox cbFollowingUser;
    Unbinder unbinder;

    private SettingPushPresenter settingPushPresenter;
    private SettingPushItem settingPushItem;

    public static BaseFragment newInstance() {
        BaseFragment fragment = new SettingPushFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_setting_push;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentTitle(getString(R.string.notification_settings));
        settingPushPresenter = new SettingPushPresenter(getActivity(), this);
        showLoading();
        settingPushPresenter.getSettingPush();
        setCheckboxListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_setting_online)
    public void onViewClicked() {
        settingPushItem.setAdmin(cbPushFromAdmin.isChecked()
                ? SettingPushItem.RECEIVE_ADMIN_PUSH : SettingPushItem.UN_RECEIVE);
        settingPushItem.setAllUser(cbAllUser.isChecked()
                ? SettingPushItem.RECEIVE_ALL_PUSH : SettingPushItem.UN_RECEIVE);
        settingPushItem.setUserFollow(cbFollowingUser.isChecked()
                ? SettingPushItem.RECEIVE_USER_FOLLOW_PUSH : SettingPushItem.UN_RECEIVE);

        showLoading();
        settingPushPresenter.settingPush(settingPushItem);
    }

    @Override
    public void didGetSettingPush(SettingPushItem settingPushItem) {
        disMissLoading();
        this.settingPushItem = settingPushItem;
        cbPushFromAdmin.setChecked(settingPushItem.getAdmin() == SettingPushItem.RECEIVE_ADMIN_PUSH
                ? true : false);
        cbAllUser.setChecked(settingPushItem.getAllUser() == SettingPushItem.RECEIVE_ALL_PUSH
                ? true : false);
        cbFollowingUser.setChecked(settingPushItem.getUserFollow() == SettingPushItem.RECEIVE_USER_FOLLOW_PUSH
                ? true : false);
    }

    @Override
    public void didGetSettingPushError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didSettingPush() {
        disMissLoading();
        showMessageDialog(getString(R.string.notification_setting_saved));
    }

    @Override
    public void didSettingPushError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    private void setCheckboxListener() {
        cbAllUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbFollowingUser.setChecked(false);
                }
            }
        });

        cbFollowingUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbAllUser.setChecked(false);
                }
            }
        });
    }

}
