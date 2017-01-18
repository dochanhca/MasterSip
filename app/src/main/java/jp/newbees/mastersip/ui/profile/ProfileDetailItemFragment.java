package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.profile.ProfileDetailItemPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailItemFragment extends BaseFragment implements ProfileDetailItemPresenter.ProfileDetailItemView {
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

    private static final String USER_ITEM = "USER_ITEM";

    private ProfileDetailItemPresenter profileDetailItemPresenter;

    private UserItem userItem;

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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
            }
        });

        profileDetailItemPresenter.getProfileDetail(userItem.getUserId());
        progressWheel.spin();
        progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    public void didGetProfileDetail(UserItem userItem) {
        fillDataToView();
    }

    @Override
    public void didGetProfileDetailError(String errorMessage, int errorCode) {
        showToastExceptionVolleyError(errorCode, errorMessage);
        progressWheel.spin();
        progressWheel.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void fillDataToView() {
        progressWheel.spin();
        progressWheel.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        txtOnlineTime.setText(getPrettyTimeLastLogin(userItem.getLastLogin()));

        Date age = DateTimeUtils.convertStringToDate(userItem.getDateOfBirth(), DateTimeUtils.ENGLISH_DATE_FORMAT);

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
        }
    }

    private String getPrettyTimeLastLogin(String lastLogin) {
        Locale locale = Utils.getCurrentLocale(getActivity().getApplicationContext());
        PrettyTime prettyTime = new PrettyTime(locale);

        return prettyTime.format(DateTimeUtils.convertStringToDate(lastLogin,
                DateTimeUtils.SERVER_DATE_FORMAT));
    }
}
