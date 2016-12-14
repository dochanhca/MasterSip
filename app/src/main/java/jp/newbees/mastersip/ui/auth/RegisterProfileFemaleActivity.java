package jp.newbees.mastersip.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectAvatarDialog;

/**
 * Created by ducpv on 12/13/16.
 */

public class RegisterProfileFemaleActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_register_profile_female;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initHeader(getString(R.string.register_profile));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @OnClick({R.id.img_select_avatar, R.id.layout_area, R.id.layout_profession, R.id.layout_type,
            R.id.layout_type_of_men, R.id.layout_charm_point, R.id.layout_available_time,
            R.id.layout_status, R.id.img_complete_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this);
                break;
            case R.id.layout_area:
                break;
            case R.id.layout_profession:
                break;
            case R.id.layout_type:
                break;
            case R.id.layout_type_of_men:
                break;
            case R.id.layout_charm_point:
                break;
            case R.id.layout_available_time:
                break;
            case R.id.layout_status:
                break;
            case R.id.img_complete_register:
                break;
        }
    }

    @BindView(R.id.img_select_avatar)
    ImageView imgSelectAvatar;
    @BindView(R.id.edt_nickname)
    HiraginoEditText edtNickname;
    @BindView(R.id.txt_area)
    HiraginoTextView txtArea;
    @BindView(R.id.layout_area)
    RelativeLayout layoutArea;
    @BindView(R.id.txt_profession)
    HiraginoTextView txtProfession;
    @BindView(R.id.layout_profession)
    RelativeLayout layoutProfession;
    @BindView(R.id.txt_type)
    HiraginoTextView txtType;
    @BindView(R.id.layout_type)
    RelativeLayout layoutType;
    @BindView(R.id.layout_type_of_men)
    RelativeLayout layoutTypeOfMen;
    @BindView(R.id.txt_type_of_men_content)
    HiraginoTextView txtTypeOfMenContent;
    @BindView(R.id.layout_charm_point)
    RelativeLayout layoutCharmPoint;
    @BindView(R.id.txt_charm_point_content)
    HiraginoTextView txtCharmPointContent;
    @BindView(R.id.txt_avaiable_time)
    HiraginoTextView txtAvaiableTime;
    @BindView(R.id.layout_available_time)
    RelativeLayout layoutAvailableTime;
    @BindView(R.id.layout_status)
    RelativeLayout layoutStatus;
    @BindView(R.id.txt_status_content)
    HiraginoTextView txtStatusContent;
    @BindView(R.id.img_complete_register)
    ImageView imgCompleteRegister;

}
