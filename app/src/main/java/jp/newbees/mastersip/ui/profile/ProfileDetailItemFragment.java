package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.UserPhotoAdapter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.customviews.SegmentedGroup;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.profile.ProfileDetailItemPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailItemFragment extends BaseFragment implements ProfileDetailItemPresenter.ProfileDetailItemView, UserPhotoAdapter.OnItemClickListener {
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
    @BindView(R.id.segmented_interaction)
    SegmentedGroup segmentedInteraction;
    @BindView(R.id.btn_follow)
    RadioButton btnFollow;
    @BindView(R.id.btn_send_gift)
    RadioButton btnSendGift;
    @BindView(R.id.btn_on_off_notify)
    RadioButton btnOnOffNotify;
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

    private static final String USER_ITEM = "USER_ITEM";


    private ProfileDetailItemPresenter profileDetailItemPresenter;
    private UserItem userItem;
    private List<ImageItem> imageItems;
    private GalleryItem galleryItem;

    private RecyclerView.LayoutManager mLayoutManager;
    private UserPhotoAdapter userPhotoAdapter;


    private RadioGroup.OnCheckedChangeListener onSegmendtedInteractionListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            // listen checked button event
        }
    };

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
            ((TopActivity) getActivity()).hideNavigation();
        }

        private void showNavigationBar() {
            ((TopActivity) getActivity()).showNavigation();
        }
    };


    public static ProfileDetailItemFragment newInstance(UserItem data) {
        ProfileDetailItemFragment profileDetailItemFragment = new ProfileDetailItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ProfileDetailItemFragment.USER_ITEM, data);
        profileDetailItemFragment.setArguments(args);
        return profileDetailItemFragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.item_profile_detail;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        profileDetailItemPresenter = new ProfileDetailItemPresenter(getActivity().getApplicationContext(),
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
        imageItems.clear();
        imageItems.addAll(galleryItem.getPhotos());
        userPhotoAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void didLoadMoreListPhotos(GalleryItem galleryItem) {

    }

    @Override
    public void didGetListPhotosError(String errorMessage, int errorCode) {
        showToastExceptionVolleyError(errorCode, errorMessage);
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onUserImageClick(int position) {
        //do something
    }

    private void initVariables() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
                profileDetailItemPresenter.getListPhotos(userItem.getUserId());
            }
        });

        scrollView.setOnScrollChangeListener(onViewScrollListener);

        profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
        profileDetailItemPresenter.getListPhotos(userItem.getUserId());

        segmentedInteraction.setOnCheckedChangeListener(onSegmendtedInteractionListener);

        boolean isFollowed = false;

        if (userItem.getRelationshipItem() != null) {
            isFollowed = userItem.getRelationshipItem().isFollowed() == RelationshipItem.FOLLOW
                    ? true : false;
        }
        btnFollow.setChecked(isFollowed);
    }

    private void initRecyclerUserImage() {
        imageItems = new ArrayList<>();
        userPhotoAdapter = new UserPhotoAdapter(getActivity().getApplicationContext(), imageItems,
                userItem.getGender());
        userPhotoAdapter.setOnItemClickListener(this);

        mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerUserImage.setLayoutManager(mLayoutManager);
        recyclerUserImage.setAdapter(userPhotoAdapter);
    }

    private void fillDataToView() {
        progressWheel.spin();
        progressWheel.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        Date age = DateTimeUtils.convertStringToDate(userItem.getDateOfBirth(), DateTimeUtils.ENGLISH_DATE_FORMAT);

        fillBasicProfile(age);
        fillDetailProfile(age);
    }

    private void fillDetailProfile(Date age) {
        txtNameContent.setText(userItem.getUsername());
        txtAgeContent.setText(String.valueOf(DateTimeUtils.calculateAgeWithDOB(age)));
        txtAreaContent.setText(userItem.getLocation().getTitle());
        txtProfessionContent.setText(userItem.getJobItem().getTitle());
        txtStatusContent.setText(userItem.getMemo());

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

        txtTypeContent.setText(userItem.getTypeGirl().getTitle());
        txtTypeOfMenContent.setText(userItem.getTypeBoy());
        txtAvailableTimeContent.setText(userItem.getAvailableTimeItem().getTitle());
        txtCharmPointContent.setText(userItem.getCharmingPoint());
    }

    private void fillBasicProfile(Date age) {
        txtOnlineTime.setText(getPrettyTimeLastLogin(userItem.getLastLogin()));

        imgStatus.setImageResource(userItem.getStatus() == UserItem.ONLINE ? R.drawable.ic_clock_green :
                R.drawable.ic_clock_white);
        txtAge.setText(String.valueOf(DateTimeUtils.calculateAgeWithDOB(age))
                + getString(R.string.year_old));
        txtName.setText(userItem.getUsername());
        txtArea.setText(userItem.getLocation().getTitle());
        txtSlogan.setText(userItem.getMemo());

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
}
