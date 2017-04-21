package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.event.SettingOnlineChangedEvent;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.profile.SettingOnlinePresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.dialog.TextDialog;

/**
 * Created by ducpv on 4/20/17.
 */

public class SettingOnlineFragment extends BaseFragment implements
        SettingOnlinePresenter.SettingOnlineView, TextDialog.OnTextDialogPositiveClick {
    private static final String USER_ITEM = "USER_ITEM";
    private static final int REQUEST_ON_ONLINE_NOTIFY = 1;
    private static final int REQUEST_OFF_ONLINE_NOTIFY = 2;

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.txt_description)
    HiraginoTextView txtDescription;
    @BindView(R.id.cb_online_setting)
    CheckBox cbOnlineSetting;
    Unbinder unbinder;

    private boolean notifyChangeSetting;
    private boolean isFollowing;
    private UserItem currentUser;
    private SettingOnlinePresenter settingOnlinePresenter;

    public static BaseFragment newInstance(UserItem userItem) {
        Bundle args = new Bundle();
        args.putParcelable(USER_ITEM, userItem);
        BaseFragment fragment = new SettingOnlineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_setting_online;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, rootView);
        setFragmentTitle(getString(R.string.online_notify_setting));

        currentUser = getArguments().getParcelable(USER_ITEM);
        loadAvatar();
        isFollowing = currentUser.getRelationshipItem().getIsNotification() == RelationshipItem.REGISTER
                ? true : false;
        cbOnlineSetting.setChecked(isFollowing);
        StringBuilder description = new StringBuilder(currentUser.getUsername())
                .append(getString(R.string.when_user_online)).append("\n")
                .append(getString(R.string.you_can_receiev_push_notify_every_time)).append("\n")
                .append(getString(R.string.turn_on_notify_and_free_to_check)).append("\n");
        txtDescription.setText(description);

        settingOnlinePresenter = new SettingOnlinePresenter(getActivity(), this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_setting_online)
    public void onViewClicked() {
        StringBuilder dialogContent = new StringBuilder(currentUser.getUsername());
        if (cbOnlineSetting.isChecked()) {
            dialogContent.append(getString(R.string.user_is_online)).append("\n")
                    .append(getString(R.string.do_you_want_to_on_online_notify));

            showConfirmDialog(dialogContent.toString(), REQUEST_ON_ONLINE_NOTIFY);
        } else {
            dialogContent.append("さんの").append("\n")
                    .append(getString(R.string.do_you_want_to_off_online_notify));

            showConfirmDialog(dialogContent.toString(), REQUEST_OFF_ONLINE_NOTIFY);
        }
    }

    @Override
    public void didOnOnlineNotifySuccess() {
        disMissLoading();
        StringBuilder message = new StringBuilder();
        message.append("今後").append(currentUser.getUsername())
                .append("さんがオンラインになる度に").append("\n")
                .append(getString(R.string.we_will_notify_you_with_push_notice));
        showMessageDialog(getString(R.string.setting_online_notify_done), message.toString(), "", false);
        notifyChangeSetting = true;
        isFollowing = true;
    }

    @Override
    public void didOffOnlineNotifySuccess() {
        disMissLoading();
        notifyChangeSetting = true;
        isFollowing = false;
    }

    @Override
    public void didSettingOnlineNotifyError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        showLoading();
        if (requestCode == REQUEST_ON_ONLINE_NOTIFY) {
            settingOnlinePresenter.onOnlineNotify(currentUser.getUserId());
        } else {
            settingOnlinePresenter.offOnlineNotify(currentUser.getUserId());
        }
    }

    @Override
    protected void onImageBackPressed() {
        super.onImageBackPressed();
        if (notifyChangeSetting) {
            EventBus.getDefault().postSticky(new SettingOnlineChangedEvent(isFollowing,
                    currentUser.getUserId()));
        }
    }

    private void showConfirmDialog(String content, int requestCode) {
        TextDialog dialogConfirmOnOnlineSetting = new TextDialog.Builder()
                .build(this, content, requestCode);
        dialogConfirmOnOnlineSetting.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    private void loadAvatar() {
        int drawableId = currentUser.getGender() == UserItem.MALE ? R.drawable.ic_boy_default :
                R.drawable.ic_girl_default;

        if (currentUser.getAvatarItem() != null) {
            Glide.with(this).load(currentUser.getAvatarItem().getOriginUrl())
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .placeholder(drawableId).error(drawableId)
                    .thumbnail(0.1f)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(profileImage);
        } else {
            profileImage.setImageResource(drawableId);
        }
    }
}
