package jp.newbees.mastersip.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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

import org.greenrobot.eventbus.EventBus;
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
import jp.newbees.mastersip.event.BlockUserEvent;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.profile.ProfileDetailItemPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.BaseCallFragment;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.ImageDetailActivity;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.gift.ListGiftFragment;
import jp.newbees.mastersip.ui.mymenu.BlockListFragment;
import jp.newbees.mastersip.ui.top.SearchContainerFragment;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailItemFragment extends BaseCallFragment implements
        ProfileDetailItemPresenter.ProfileDetailItemView,
        UserPhotoAdapter.OnItemClickListener,
        TextDialog.OnTextDialogPositiveClick, SelectionDialog.OnSelectionDialogClick {

    private static final String NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT = "NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT";
    public static final String USER_ITEM = "USER_ITEM";
    private static final String SELECTED = "SELECTED";
    private static final int CONFIRM_SEND_GIFT_DIALOG = 11;
    private static final int CONFIRM_BLOCK_USER = 41;
    private static final int REQUEST_GO_BLOCK_LIST = 42;
    private static final long TIME_DELAY = 500;

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
    @BindView(R.id.layout_chat)
    ViewGroup layoutChat;
    @BindView(R.id.txt_video_call)
    TextView txtVideoCall;
    @BindView(R.id.txt_voice_call)
    TextView txtVoiceCall;

    private ProfileDetailItemPresenter profileDetailItemPresenter;
    private UserItem userItem;
    private GalleryItem galleryItem;
    private boolean isLoading;
    private UserPhotoAdapter userPhotoAdapter;
    private boolean needSendFootprint;
    private boolean needShowActionBar;
    private List<SelectionItem> reportReasons;

    private long mLastClickTime = 0;

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
        return ProfileDetailItemFragment.newInstance(data, needShowActionBarInGiftFragment, true);
    }

    public static ProfileDetailItemFragment newInstance(UserItem data, boolean needShowActionBarInGiftFragment, boolean selected) {
        ProfileDetailItemFragment profileDetailItemFragment = new ProfileDetailItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ProfileDetailItemFragment.USER_ITEM, data);
        args.putBoolean(NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT, needShowActionBarInGiftFragment);
        args.putBoolean(SELECTED, selected);
        profileDetailItemFragment.setArguments(args);
        return profileDetailItemFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.item_profile_detail;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        profileDetailItemPresenter = new ProfileDetailItemPresenter((BaseActivity) getActivity(),
                this);
        ButterKnife.bind(this, mRoot);

        userItem = getArguments().getParcelable(USER_ITEM);
        boolean selected = getArguments().getBoolean(SELECTED);
        if (selected) {
            super.setShowingProfile(userItem);
        }
        needShowActionBar = getArguments().getBoolean(NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT);
        needSendFootprint = !needShowActionBar;

        progressWheel.spin();
        progressWheel.setVisibility(View.VISIBLE);
        restoreNavigationBarState();

        initRecyclerUserImage();
        initVariables();
        initActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        profileDetailItemPresenter.registerEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        profileDetailItemPresenter.unRegisterEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        super.setShowingProfile(null);
    }

    @OnClick({R.id.btn_follow, R.id.btn_on_off_notify, R.id.btn_send_gift,
            R.id.layout_chat, R.id.layout_voice_call, R.id.layout_video_call, R.id.layout_report_user,
    R.id.layout_block_user})
    public void onClick(View view) {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()) {
            case R.id.btn_follow:
                doFollowUser();
                break;
            case R.id.btn_send_gift:
                showGiftFragment();
                break;
            case R.id.btn_on_off_notify:
                boolean needShowActionBar = getArguments().getBoolean(NEED_SHOW_ACTION_BAR_IN_GIFT_FRAGMENT);
                SearchContainerFragment.showSettingOnlineFragment(getActivity(), userItem, needShowActionBar);
                break;
            case R.id.layout_chat:
                chatWithUser(userItem);
                break;
            case R.id.layout_voice_call:
                super.callVoice(userItem, true);
                break;
            case R.id.layout_video_call:
                super.callVideo(userItem, true);
                break;
            case R.id.layout_report_user:
                showLoading();
                profileDetailItemPresenter.getListReportReason();
                break;
            case R.id.layout_block_user:
                showDialogConfirmBlockUser();
                break;
            default:
                break;
        }
    }

    private void showDialogConfirmBlockUser() {
        String content = String.format(getString(R.string.mess_confirm_block_user), userItem.getUsername());
        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(getString(R.string.user_block))
                .build(this, content
                        , CONFIRM_BLOCK_USER);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public void didGetProfileDetail(final UserItem userItem) {
        this.userItem = userItem;
        fillDataToView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getUserVisibleHint() && needSendFootprint) {
                    profileDetailItemPresenter.sendFootPrintToServer(userItem.getUserId());
                }
            }
        }, TIME_DELAY);
    }

    @Override
    public void didGetProfileDetailError(String errorMessage, int errorCode) {
        if (errorCode == Constant.Error.HAS_BEEN_BLOCKED) {
            redirectToListProfile();
        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
        progressWheel.spin();
        progressWheel.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void didGetListPhotos(GalleryItem galleryItem) {
        this.galleryItem = galleryItem;
        userPhotoAdapter.clearData();
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
        if (errorCode != Constant.Error.HAS_BEEN_BLOCKED) {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
        swipeRefreshLayout.setRefreshing(false);
        isLoading = false;
    }

    @Override
    public void didFollowUser() {
        disMissLoading();
        String title = getString(R.string.mess_followed);
        StringBuilder content = new StringBuilder();
        content.append(userItem.getUsername()).append(getString(R.string.notify_follow_user_success));
        String positiveTitle = getString(R.string.send_a_give);
        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(title)
                .setPositiveTitle(positiveTitle)
                .build(this, content.toString(), CONFIRM_SEND_GIFT_DIALOG);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
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
    public void didEditProfileImage() {
        profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
        profileDetailItemPresenter.getListPhotos(userItem.getUserId());
    }

    @Override
    public void didSettingOnlineChanged(boolean isFollowing, String userId) {
        if (userId.equals(userItem.getUserId())) {
            // Update following state after setting changed
            RelationshipItem relationshipItem = userItem.getRelationshipItem();
            relationshipItem.setIsNotification(isFollowing ? RelationshipItem.REGISTER
                    : RelationshipItem.UN_REGISTER);
            userItem.setRelationshipItem(relationshipItem);
            updateBtnOnOffNotify(isFollowing);
        }
    }

    @Override
    public void didGetListReportReason(List<SelectionItem> reportReasons) {
        this.reportReasons = reportReasons;
        disMissLoading();
        SelectionDialog.openSelectionDialogFromFragment(this, -1, getFragmentManager(),
                (ArrayList<SelectionItem>) reportReasons
                , getString(R.string.report_user), getString(R.string.report), reportReasons.get(0));
    }

    @Override
    public void didGetListReportReasonError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didReportUser() {
        disMissLoading();
        showMessageDialog(getString(R.string.reported_user),
                getString(R.string.mess_report_user_sucess), "", false);
    }

    @Override
    public void didReportUserError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didBlockUser() {
        disMissLoading();
        redirectToListProfile();
    }

    private void redirectToListProfile() {
        Activity activity = this.getActivity();
        if (activity instanceof TopActivity) {
            EventBus.getDefault().post(new BlockUserEvent(userItem));
        } else if (activity instanceof ProfileDetailItemActivity) {
            Intent intent = new Intent(getApplicationContext(), TopActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(USER_ITEM, userItem);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void didBlockUserError(int errorCode, String errorMessage) {
        disMissLoading();
        if (errorCode == Constant.Error.MAXIMUM_USER_BLOCK) {
            showDialogMaximumUserBlock();
        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
    }

    private void showDialogMaximumUserBlock() {
        String title = getString(R.string.maximum_number_of_block_user);
        String content = String.format(getString(R.string.mess_maximum_user_block), userItem.getUsername());
        String positiveTitle = getString(R.string.go_block_list);
        TextDialog textDialog = new TextDialog.Builder()
                .setPositiveTitle(positiveTitle)
                .setTitle(title).build(this, content, REQUEST_GO_BLOCK_LIST);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public void onUserImageClick(int position) {
        if (isCurrentUser()) {
            ImageDetailActivity.startActivity(getActivity(), galleryItem, position,
                    ImageDetailActivity.MY_PHOTOS, userItem.getUserId());
        } else {
            ImageDetailActivity.startActivity(getActivity(), galleryItem,
                    position, ImageDetailActivity.OTHER_USER_PHOTOS, userItem.getUserId());
        }
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == CONFIRM_SEND_GIFT_DIALOG) {
            showGiftFragment();
        } else if (requestCode == CONFIRM_BLOCK_USER) {
            showLoading();
            profileDetailItemPresenter.blockUser(userItem.getUserId());
        } else if (requestCode == REQUEST_GO_BLOCK_LIST) {
            showBlockListFragment();
        }
    }

    @Override
    public void onItemSelected(int position) {
        showLoading();
        profileDetailItemPresenter.reportUser(userItem.getUserId(), reportReasons.get(position).getId());
    }

    private boolean isCurrentUser() {
        return userItem.getUserId().equals(ConfigManager.getInstance().getCurrentUser().getUserId());
    }

    private void doFollowUser() {
        showLoading();
        if (btnFollow.isChecked()) {
            profileDetailItemPresenter.followUser(userItem.getUserId());
        } else {
            profileDetailItemPresenter.unFollowUser(userItem.getUserId());
        }
    }

    private void initActions() {
        if (isCurrentUser()) {
            btnFollow.setEnabled(false);
            btnSendGift.setEnabled(false);
            btnOnOffNotify.setEnabled(false);
            layoutVideoCall.setEnabled(false);
            layoutVoiceCall.setEnabled(false);
            layoutChat.setEnabled(false);
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
                needSendFootprint = true;
                profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
                profileDetailItemPresenter.getListPhotos(userItem.getUserId());
            }
        });

        scrollView.setOnScrollChangeListener(onViewScrollListener);

        profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
        profileDetailItemPresenter.getListPhotos(userItem.getUserId());
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
                            && !isLoading && profileDetailItemPresenter.canLoadMoreUser()) {
                        isLoading = true;
                        profileDetailItemPresenter.loadMoreListPhotos(userItem.getUserId());
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
            boolean isFollowing = userItem.getRelationshipItem().getIsNotification() == RelationshipItem.REGISTER
                    ? true : false;
            updateBtnOnOffNotify(isFollowing);
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

    private void updateBtnOnOffNotify(boolean isFollowing) {

        btnOnOffNotify.setBackgroundResource(isFollowing
                ? R.drawable.bg_btn_on_notify : R.drawable.bg_btn_off_notify);
        btnOnOffNotify.setTextColor(getResources().getColor(isFollowing
                ? R.color.white : R.color.colorPrimaryDark));
        btnOnOffNotify.setCompoundDrawablesWithIntrinsicBounds(0, isFollowing
                ? R.drawable.ic_notify_on : R.drawable.ic_notify_off, 0, 0);
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

    private void showGiftFragment() {
        SearchContainerFragment.showGiftFragment(getActivity(), userItem,
                ListGiftFragment.OPEN_FROM_PROFILE_DETAILS, needShowActionBar);
    }


    private void showBlockListFragment() {
        BaseFragment fragment = BlockListFragment.newInstance();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, fragment,
                BlockListFragment.class.getSimpleName()).commit();
    }

    public final void onPageSelected() {
        super.setShowingProfile(userItem);
    }
}
