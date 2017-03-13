package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.UserPhotoAdapter;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableVoiceCallTask;
import jp.newbees.mastersip.presenter.profile.ProfileDetailPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.ImageDetailActivity;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.ConfirmSendGiftDialog;
import jp.newbees.mastersip.ui.dialog.ConfirmVoiceCallDialog;
import jp.newbees.mastersip.ui.dialog.SelectVideoCallDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.gift.ListGiftFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailItemFragment extends BaseFragment implements
        ProfileDetailPresenter.ProfileDetailItemView, UserPhotoAdapter.OnItemClickListener,
        ConfirmSendGiftDialog.OnConfirmSendGiftDialog, ConfirmVoiceCallDialog.OnDialogConfirmVoiceCallClick,
        TextDialog.OnTextDialogPositiveClick, SelectVideoCallDialog.OnSelectVideoCallDialog {

    public static final String USER_ITEM = "USER_ITEM";
    private static final int REQUEST_NOTIFY_NOT_ENOUGH_POINT = 1;
    private static final int REQUEST_NOTIFY_CALLEE_REJECT_CALL = 2;
    private static final int CONFIRM_SEND_GIFT_DIALOG = 11;
    private static final int CONFIRM_VOICE_CALL_DIALOG = 10;
    private static final int SELECT_VIDEO_CALL_DIALOG = 14;
    private static final int CONFIRM_REQUEST_ENABLE_VOICE_CALL = 12;
    private static final int CONFIRM_REQUEST_ENABLE_VIDEO_CALL = 13;

    private static final String NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT = "NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT";


    @BindView(R.id.txt_online_time)
    HiraginoTextView txtOnlineTime;
    @BindView(R.id.txt_name)
    HiraginoTextView txtName;
    @BindView(R.id.txt_age)
    HiraginoTextView txtAge;
    @BindView(R.id.txt_area)
    HiraginoTextView txtArea;
    @BindView(R.id.txt_slogan)
    HiraginoTextView txtSlogan;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @BindView(R.id.img_status)
    ImageView imgStatus;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_user_image)
    RecyclerView recyclerUserImage;
    @BindView(R.id.btn_follow)
    CheckBox btnFollow;
    @BindView(R.id.btn_send_gift)
    HiraginoButton btnSendGift;
    @BindView(R.id.btn_on_off_notify)
    HiraginoButton btnOnOffNotify;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.txt_name_content)
    HiraginoTextView txtNameContent;
    @BindView(R.id.txt_age_content)
    HiraginoTextView txtAgeContent;
    @BindView(R.id.txt_area_content)
    HiraginoTextView txtAreaContent;
    @BindView(R.id.txt_profession_content)
    HiraginoTextView txtProfessionContent;
    @BindView(R.id.txt_type_content)
    HiraginoTextView txtTypeContent;
    @BindView(R.id.layout_type)
    LinearLayout layoutType;
    @BindView(R.id.txt_type_of_men_content)
    HiraginoTextView txtTypeOfMenContent;
    @BindView(R.id.layout_type_of_men)
    LinearLayout layoutTypeOfMen;
    @BindView(R.id.txt_charm_point_content)
    HiraginoTextView txtCharmPointContent;
    @BindView(R.id.layout_charm_point)
    LinearLayout layoutCharmPoint;
    @BindView(R.id.txt_available_time_content)
    HiraginoTextView txtAvailableTimeContent;
    @BindView(R.id.layout_available_time)
    LinearLayout layoutAvailableTime;
    @BindView(R.id.txt_status_content)
    HiraginoTextView txtStatusContent;
    @BindView(R.id.divider_type)
    View dividerType;
    @BindView(R.id.divider_type_of_men)
    View dividerTypeOfMen;
    @BindView(R.id.divier_charm_point)
    View divierCharmPoint;
    @BindView(R.id.divider_available_time)
    View dividerAvailableTime;
    @BindView(R.id.layout_voice_call)
    ViewGroup layoutVoiceCall;
    @BindView(R.id.layout_video_call)
    ViewGroup layoutVideoCall;
    @BindView(R.id.txt_video_call)
    TextView txtVideoCall;
    @BindView(R.id.txt_voice_call)
    TextView txtVoiceCall;

    private ProfileDetailPresenter profileDetailPresenter;
    private UserItem userItem;
    private GalleryItem galleryItem;
    private boolean isLoading;

    private UserPhotoAdapter userPhotoAdapter;

    private NestedScrollView.OnScrollChangeListener onViewScrollListener = new NestedScrollView.OnScrollChangeListener() {

        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if ((scrollUp(scrollY, oldScrollY))
                    && !isNavigationBarShowing()) {
                showNavigationBar();
            } else if ((scrollDown(scrollY, oldScrollY))
                    && isNavigationBarShowing()) {
                hideNavigationBar();
            }
        }

        private boolean scrollUp(int y, int oldY) {
            return y < oldY;
        }

        private boolean scrollDown(int y, int oldY) {
            return y > oldY;
        }

        private void hideNavigationBar() {
            ((BaseActivity) getActivity()).hideNavigation();
        }

        private void showNavigationBar() {
            ((BaseActivity) getActivity()).showNavigation();
        }
    };


    public static ProfileDetailItemFragment newInstance(UserItem data, boolean needShowActionBarInGiftFragment) {
        ProfileDetailItemFragment profileDetailItemFragment = new ProfileDetailItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ProfileDetailItemFragment.USER_ITEM, data);
        args.putBoolean(NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT, needShowActionBarInGiftFragment);
        profileDetailItemFragment.setArguments(args);
        return profileDetailItemFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.item_profile_detail;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        profileDetailPresenter = new ProfileDetailPresenter(getActivity().getApplicationContext(),
                this);
        ButterKnife.bind(this, mRoot);

        userItem = getArguments().getParcelable(USER_ITEM);

        progressWheel.spin();
        progressWheel.setVisibility(View.VISIBLE);
        restoreNavigationBarState();

        initRecyclerUserImage();
        initVariables();
    }

    @Override
    public void onResume() {
        super.onResume();
        profileDetailPresenter.registerEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        profileDetailPresenter.unRegisterEvent();
    }

    @OnClick({R.id.btn_follow, R.id.btn_on_off_notify, R.id.btn_send_gift,
            R.id.layout_chat, R.id.layout_voice_call, R.id.layout_video_call,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_follow:
                doFollowUser();
                break;
            case R.id.btn_send_gift:
                showGiftFragment();
                break;
            case R.id.btn_on_off_notify:
                break;
            case R.id.layout_chat:
                ChatActivity.startChatActivity(getContext(), userItem);
                break;
            case R.id.layout_voice_call:
                handleVoiceCallClick();
                break;
            case R.id.layout_video_call:
                handleVideoCallClick();
                break;
            default:
                break;
        }
    }

    private void handleVideoCallClick() {
        if (userItem.getSettings().getVideoCall() == SettingItem.OFF) {
            String content = getResources().getString(R.string.confirm_request_enable_video_call);
            String positive = getResources().getString(R.string.confirm_request_enable_video_call_positive);
            TextDialog.openTextDialog(this, CONFIRM_REQUEST_ENABLE_VIDEO_CALL, getFragmentManager(), content, "", positive);
        } else {
            SelectVideoCallDialog.openDialog(this, SELECT_VIDEO_CALL_DIALOG, this, getFragmentManager());
        }
    }

    private void handleVoiceCallClick() {
        if (userItem.getSettings().getVoiceCall() == SettingItem.OFF) {
            String content = getResources().getString(R.string.confirm_request_enable_voice_call);
            String positive = getResources().getString(R.string.confirm_request_enable_voice_call_positive);
            TextDialog.openTextDialog(this, CONFIRM_REQUEST_ENABLE_VOICE_CALL, getFragmentManager(), content, "", positive);
        } else {
            ConfirmVoiceCallDialog.openConfirmVoiceCallDialog(this,
                    CONFIRM_VOICE_CALL_DIALOG, getFragmentManager());
        }
    }

    @Override
    public void didGetProfileDetail(UserItem userItem) {
        this.userItem = userItem;
        fillDataToView();
    }

    @Override
    public void didGetProfileDetailError(String errorMessage, int errorCode) {
        showToastExceptionVolleyError(errorCode, errorMessage);
        progressWheel.spin();
        progressWheel.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void didGetListPhotos(GalleryItem galleryItem) {
        this.galleryItem = galleryItem;
        userPhotoAdapter.addAll(galleryItem.getPhotos());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void didLoadMoreListPhotos(GalleryItem galleryItem) {
        isLoading = false;
        userPhotoAdapter.addAll(galleryItem.getPhotos());
        updatePhotos(galleryItem);
    }

    @Override
    public void didGetListPhotosError(String errorMessage, int errorCode) {
        showToastExceptionVolleyError(errorCode, errorMessage);
        swipeRefreshLayout.setRefreshing(false);
        isLoading = false;
    }

    @Override
    public void didFollowUser() {
        disMissLoading();
        ConfirmSendGiftDialog.openConfirmSendGiftDialog(this, CONFIRM_SEND_GIFT_DIALOG,
                getFragmentManager(), userItem.getUsername());
        btnFollow.setText(getString(R.string.un_follow));
    }

    @Override
    public void didFollowUserError(String errorMessage, int errorCode) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didUnFollowUser() {
        disMissLoading();
        StringBuilder content = new StringBuilder();
        content.append(userItem.getUsername()).append(getString(R.string.notify_un_follow_user_success));

        showMessageDialog(getString(R.string.mess_un_followed), content.toString(), "", false);
        btnFollow.setText(getString(R.string.follow));
    }

    @Override
    public void didUnFollowUserError(String errorMessage, int errorCode) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableVoiceCallTask.Type type) {
        disMissLoading();
        TextDialog.openTextDialog(this, -1, getFragmentManager(),
                profileDetailPresenter.getMessageSendRequestSuccess(userItem, type), "", true);
    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {
        disMissLoading();
    }

    @Override
    public void didCheckCallError(String errorMessage, int errorCode) {
        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
            showDialogNotifyNotEnoughPoint();
        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
    }

    @Override
    public void didCalleeRejectCall(String calleeExtension) {
        if (calleeExtension.equals(userItem.getSipItem().getExtension())) {
            String message = userItem.getUsername() + " " + getString(R.string.mess_callee_reject_call);
            String positiveTitle = getString(R.string.back_to_profile_detail);
            TextDialog.openTextDialog(this, REQUEST_NOTIFY_CALLEE_REJECT_CALL, getFragmentManager()
                    , message, "", positiveTitle, true);
        }
    }

    @Override
    public void onUserImageClick(int position) {
        //do something
        ImageDetailActivity.startActivity(getActivity(), galleryItem, position, ImageDetailActivity.OTHER_USER_PHOTOS);
    }

    @Override
    public void onOkConfirmSendGiftClick() {
        showGiftFragment();
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == CONFIRM_REQUEST_ENABLE_VOICE_CALL) {
            showLoading();
            profileDetailPresenter.sendMessageRequestEnableSettingCall(userItem, SendMessageRequestEnableVoiceCallTask.Type.VOICE);
        } else if (requestCode == CONFIRM_REQUEST_ENABLE_VIDEO_CALL) {
            //send request to enalbe video
        }
    }

    private void doFollowUser() {
        showLoading();
        if (btnFollow.isChecked()) {
            profileDetailPresenter.followUser(userItem.getUserId());
        } else {
            profileDetailPresenter.unFollowUser(userItem.getUserId());
        }
    }

    @Override
    public void onOkVoiceCallClick() {
        profileDetailPresenter.checkVoiceCall(userItem);
    }

    @Override
    public void onSelectedVideoCall(SelectVideoCallDialog.VideoCall videoCall) {
        if (videoCall == SelectVideoCallDialog.VideoCall.VIDEO_VIDEO) {
            profileDetailPresenter.checkVideoCall(userItem);
        } else {
            profileDetailPresenter.checkVideoChatCall(userItem);
        }
    }

    private void updatePhotos(GalleryItem galleryItem) {
        List<ImageItem> tempPhotos = this.galleryItem.getPhotos();
        this.galleryItem = galleryItem;
        tempPhotos.addAll(galleryItem.getPhotos());
        this.galleryItem.setImageItems(tempPhotos);
    }

    private void initVariables() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileDetailPresenter.getProfileDetail(userItem.getUserId());
                profileDetailPresenter.getListPhotos(userItem.getUserId());
            }
        });

        scrollView.setOnScrollChangeListener(onViewScrollListener);

        profileDetailPresenter.getProfileDetail(userItem.getUserId());
        profileDetailPresenter.getListPhotos(userItem.getUserId());
    }

    private void initRecyclerUserImage() {
        userPhotoAdapter = new UserPhotoAdapter(getActivity().getApplicationContext(),
                new ArrayList<ImageItem>());
        userPhotoAdapter.setOnItemClickListener(this);

        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerUserImage.setLayoutManager(mLayoutManager);
        recyclerUserImage.setAdapter(userPhotoAdapter);
        addScrollToLoadMoreRecyclerView();
    }

    private void addScrollToLoadMoreRecyclerView() {
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerUserImage.getLayoutManager();
        recyclerUserImage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int visibleItemCount;
            int totalItemCount;
            int firstVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0
                            && !isLoading && profileDetailPresenter.canLoadMoreUser()) {
                        isLoading = true;
                        profileDetailPresenter.loadMoreListPhotos(userItem.getUserId());
                    }
                }
            }
        });
    }

    private void fillDataToView() {
        if (userItem.getSettings().getVideoCall() == SettingItem.OFF) {
            layoutVideoCall.setBackgroundColor(getActivity().getResources().getColor(R.color.color_gray_bg));
            txtVideoCall.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_video_call_off, 0, 0, 0);
        }

        if (userItem.getSettings().getVoiceCall() == SettingItem.OFF) {
            layoutVoiceCall.setBackgroundColor(getActivity().getResources().getColor(R.color.color_gray_bg));
            txtVoiceCall.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_voice_call_off, 0, 0, 0);
        }

        progressWheel.spin();
        progressWheel.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        Date age = DateTimeUtils.convertStringToDate(userItem.getDateOfBirth(), DateTimeUtils.ENGLISH_DATE_FORMAT);

        fillBasicProfile(age);
        fillDetailProfile(age);
    }

    private void fillDetailProfile(Date age) {
        if (userItem.getRelationshipItem() != null) {
            boolean isFollowed = userItem.getRelationshipItem().isFollowed() == RelationshipItem.FOLLOW
                    ? true : false;
            btnFollow.setChecked(isFollowed);
            btnFollow.setText(isFollowed ? getString(R.string.un_follow) : getString(R.string.follow));
        }

        txtNameContent.setText(userItem.getUsername());
        txtAgeContent.setText(String.valueOf(DateTimeUtils.calculateAgeWithDOB(age)));
        txtAreaContent.setText(userItem.getLocation().getTitle());
        txtProfessionContent.setText(userItem.getJobItem().getTitle());
        txtStatusContent.setText(userItem.getMemo().trim());

        if (userItem.getGender() == UserItem.FEMALE) {
            fillProfileForFemale();
        }
    }

    private void fillProfileForFemale() {
        layoutType.setVisibility(View.VISIBLE);
        layoutTypeOfMen.setVisibility(View.VISIBLE);
        layoutAvailableTime.setVisibility(View.VISIBLE);
        layoutCharmPoint.setVisibility(View.VISIBLE);
        dividerType.setVisibility(View.VISIBLE);
        dividerTypeOfMen.setVisibility(View.VISIBLE);
        dividerAvailableTime.setVisibility(View.VISIBLE);
        divierCharmPoint.setVisibility(View.VISIBLE);

        txtTypeContent.setText(userItem.getTypeGirl().getTitle().trim());
        txtTypeOfMenContent.setText(userItem.getTypeBoy().trim());
        txtAvailableTimeContent.setText(userItem.getAvailableTimeItem().getTitle().trim());
        txtCharmPointContent.setText(userItem.getCharmingPoint().trim());
    }

    private void fillBasicProfile(Date age) {
        txtOnlineTime.setText(getPrettyTimeLastLogin(userItem.getLastLogin()));

        imgStatus.setImageResource(userItem.getStatus() == UserItem.ONLINE ? R.drawable.ic_clock_green :
                R.drawable.ic_clock_white);
        txtAge.setText(String.valueOf(DateTimeUtils.calculateAgeWithDOB(age))
                + getString(R.string.year_old));
        txtName.setText(userItem.getUsername());
        txtArea.setText(userItem.getLocation().getTitle());
        txtSlogan.setText(userItem.getMemo().trim());

        int drawableId = userItem.getGender() == UserItem.MALE ? R.drawable.ic_boy_default :
                R.drawable.ic_girl_default;

        if (userItem.getAvatarItem() != null) {
            Glide.with(this).load(userItem.getAvatarItem().getOriginUrl())
                    .asBitmap()
                    .placeholder(drawableId).error(drawableId)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .thumbnail(0.1f)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(drawableId);
        }
    }

    private String getPrettyTimeLastLogin(String lastLogin) {
        Locale locale = Utils.getCurrentLocale(getActivity().getApplicationContext());
        PrettyTime prettyTime = new PrettyTime(locale);

        return prettyTime.format(DateTimeUtils.convertStringToDate(lastLogin,
                DateTimeUtils.SERVER_DATE_FORMAT));
    }

    private void showDialogNotifyNotEnoughPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        String title, content, positiveTitle;
        if (gender == UserItem.MALE) {
            title = getString(R.string.point_are_missing);
            content = getString(R.string.mess_suggest_buy_point);
            positiveTitle = getString(R.string.add_point);
        } else {
            title = getString(R.string.partner_point_are_missing);
            content = userItem.getUsername() + getString(R.string.mess_suggest_missing_point_for_girl);
            positiveTitle = getString(R.string.to_attack);
        }
        TextDialog.openTextDialog(this, REQUEST_NOTIFY_NOT_ENOUGH_POINT, getFragmentManager(),
                content, title, positiveTitle, false);
    }

    private void showGiftFragment() {
        boolean needShowActionBar = getArguments().getBoolean(NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT);
        Fragment giftFragment = ListGiftFragment.newInstance(userItem, ListGiftFragment.OPEN_FROM_PROFILE_DETAILS, needShowActionBar);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, giftFragment,
                ListGiftFragment.class.getName()).commit();
    }
}
