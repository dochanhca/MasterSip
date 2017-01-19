package jp.newbees.mastersip.ui.top;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.MyMenuPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 12/22/16.
 */

public class MyMenuFragment extends BaseFragment implements MyMenuPresenter.MyMenuView {
    @BindView(R.id.switch_mode_in_header)
    ImageView switchModeInHeader;
    @BindView(R.id.txt_action_bar_title)
    HiraginoTextView txtActionBarTitle;
    @BindView(R.id.ll_action_bar)
    RelativeLayout llActionBar;
    @BindView(R.id.img_avatar)
    CircleImageView imgAvatar;
    @BindView(R.id.btn_change_avatar)
    ImageButton btnChangeAvatar;
    @BindView(R.id.img_point)
    ImageView imgPoint;
    @BindView(R.id.txt_point)
    TextView txtPoint;
    @BindView(R.id.parent_point)
    LinearLayout parentPoint;
    @BindView(R.id.btn_buy_point)
    HiraginoButton btnBuyPoint;
    @BindView(R.id.group_point)
    RelativeLayout groupPoint;
    @BindView(R.id.group_0)
    RelativeLayout group0;
    @BindView(R.id.btn_upload_photo)
    ImageButton btnUploadPhoto;
    @BindView(R.id.img_mask)
    ImageView imgMask;
    @BindView(R.id.group_upload_photo)
    RelativeLayout groupUploadPhoto;
    @BindView(R.id.group_1)
    RelativeLayout group1;
    @BindView(R.id.rcv_list_photo)
    RecyclerView rcvListPhoto;
    @BindView(R.id.group_2)
    LinearLayout group2;
    @BindView(R.id.btn_logout)
    ImageButton btnLogout;
    @BindView(R.id.img_logout)
    ImageView imgLogout;
    @BindView(R.id.btn_backup_email)
    ImageButton btnBackupEmail;
    @BindView(R.id.img_email)
    ImageView imgEmail;
    @BindView(R.id.txt_back_up_email)
    HiraginoTextView txtBackUpEmail;
    @BindView(R.id.txt_approving)
    HiraginoTextView txtApproving;

    private MyMenuPresenter presenter;

    @Override
    protected int layoutId() {
        return R.layout.my_menu_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        initDefaultViews();
        presenter = new MyMenuPresenter(getContext(), this);
    }

    private void initDefaultViews() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        int defaultAvatar = ConfigManager.getInstance().getImageCallerDefault();
        this.txtActionBarTitle.setText(userItem.getUsername());
        this.imgAvatar.setImageResource(defaultAvatar);
        this.txtPoint.setText("" + userItem.getCoin());
        int isShowButtonBuyPoint = userItem.getGender() == UserItem.MALE ? View.VISIBLE : View.INVISIBLE;
        this.btnBuyPoint.setVisibility(isShowButtonBuyPoint);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.requestMyMenuInfo();
    }

    public static Fragment newInstance() {
        Fragment fragment = new MyMenuFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.btn_change_avatar, R.id.btn_buy_point, R.id.btn_upload_photo, R.id.btn_logout, R.id.btn_backup_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_avatar:
                break;
            case R.id.btn_buy_point:
                break;
            case R.id.btn_upload_photo:
                break;
            case R.id.btn_logout:
                presenter.requestLogout();
                break;
            case R.id.btn_backup_email:
                break;
        }
    }

    @Override
    public void didLogout() {
        Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
