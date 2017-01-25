package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.presenter.top.SettingCallPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/25/17.
 */

public class SettingCallFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, SettingCallPresenter.SettingCallView {

    private SettingCallPresenter settingCallPresenter;
    private SettingItem settingCall;

    @BindView(R.id.chk_voice_setting)
    AppCompatCheckBox chkVoiceSetting;
    @BindView(R.id.chk_video_setting)
    AppCompatCheckBox chkVideoSetting;
    @BindView(R.id.btn_confirm_setting_call)
    HiraginoButton btnConfirmSettingCall;

    @Override
    protected int layoutId() {
        return R.layout.fragment_setting_call;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        setFragmentTitle(getString(R.string.title_setting_call));
        updateSettingCallView();
        chkVoiceSetting.setOnCheckedChangeListener(this);
        chkVideoSetting.setOnCheckedChangeListener(this);
        settingCall = new SettingItem();
        settingCallPresenter = new SettingCallPresenter(getContext(), this);
    }

    private void updateSettingCallView(){
        SettingItem settingCall = ConfigManager.getInstance().getCurrentUser().getSettings();
        chkVideoSetting.setChecked(settingCall.getVideoCall() > 0 ? true : false);
        chkVoiceSetting.setChecked(settingCall.getVoiceCall() > 0 ? true : false);
    }

    public static SettingCallFragment newInstance() {
        SettingCallFragment settingCallFragment = new SettingCallFragment();
        return settingCallFragment;
    }

    @OnClick(R.id.btn_confirm_setting_call)
    public void onClick() {
        showLoading();
        settingCallPresenter.requestChangeCallSetting(settingCall);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int state = b ? SettingItem.ON : SettingItem.OFF;
        if (compoundButton == chkVoiceSetting){
            settingCall.setVoiceCall(state);
        }else if(compoundButton == chkVideoSetting) {
            settingCall.setVideoCall(state);
        }
    }

    @Override
    public void didUpdateSettingCall() {
        disMissLoading();
        String messageSettingCallChange = getString(R.string.mess_call_setting_changed);
        showMessageDialog("",messageSettingCallChange,"",false);
    }

    @Override
    public void didUpdateSettingCallFailure(String messageError) {
        disMissLoading();
        Toast.makeText(getContext(),messageError, Toast.LENGTH_SHORT).show();
    }
}
