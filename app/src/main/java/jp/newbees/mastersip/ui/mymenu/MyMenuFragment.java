package jp.newbees.mastersip.ui.mymenu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryAdapter;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.event.PaymentSuccessEvent;
import jp.newbees.mastersip.event.ReLoadProfileEvent;
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.MyMenuPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.ImageDetailActivity;
import jp.newbees.mastersip.ui.ProfileBaseActivity;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.ui.auth.CropImageActivity;
import jp.newbees.mastersip.ui.auth.UpdateProfileFemaleActivity;
import jp.newbees.mastersip.ui.auth.UpdateProfileMaleActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemActivity;
import jp.newbees.mastersip.ui.top.MyMenuContainerFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.Utils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by thangit14 on 12/22/16.
 */

public class MyMenuFragment extends BaseFragment implements MyMenuPresenter.MyMenuView, GalleryAdapter.OnItemClickListener,
        SelectImageDialog.OnSelectAvatarDiaLogClick, TextDialog.OnTextDialogPositiveClick {

    private static final int REQUEST_SELECT_PHOTO_FOR_AVATAR = 8888;
    private static final int REQUEST_SELECT_PHOTO_FOR_GALLERY = 8989;
    private static final String REFRESH_DATA = "REFRESH_DATA";
    private static final int CONFIRM_DELETE_AVATAR = 11;
    private static final int REQUEST_EDIT_PROFILE = 31;

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
    @BindView(R.id.group_1)
    RelativeLayout group1;
    @BindView(R.id.rcv_list_photo)
    RecyclerView rcvListPhoto;
    @BindView(R.id.group_2)
    LinearLayout group2;
    @BindView(R.id.txt_approving)
    TextView txtApproving;
    @BindView(R.id.img_mask_approving)
    ImageView imgMaskApproving;
    @BindView(R.id.prw_upload_avatar)
    ProgressWheel prwUploadAvatar;
    @BindView(R.id.group_upload_avatar)
    RelativeLayout groupUploadAvatar;
    @BindView(R.id.prw_upload_photo_gallery)
    ProgressWheel prwUploadPhotoGallery;
    @BindView(R.id.group_upload_photo_gallery)
    RelativeLayout groupUploadPhotoGallery;
    @BindView(R.id.txt_message_number)
    HiraginoTextView txtMessageNumber;
    @BindView(R.id.txt_online_list)
    HiraginoTextView txtOnlineList;
    @BindView(R.id.txt_email_backup_setting)
    HiraginoTextView txtEmailBackup;
    @BindView(R.id.divider_email_backup)
    View dividerEmailBackup;
    @BindView(R.id.txt_version)
    HiraginoTextView txtVersion;

    private MyMenuPresenter presenter;
    private int defaultAvatar;
    private GalleryAdapter galleryAdapter;
    private Uri pickedImage;
    private boolean uploadingAvatar;
    private int currentRequestPhoto;
    private boolean uploadingPhoto;
    private boolean needRefreshData;
    private boolean isLoadingMorePhoto;
    private int visibleThreshold = 5;
    private boolean isFragmentRunning = false;
    private GalleryItem galleryItem;
    private long mLastClickTime = 0;

    public static BaseFragment newInstance() {
        BaseFragment fragment = new MyMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(REFRESH_DATA, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    public final void onTabSelected() {
        if (isFragmentRunning) {
            presenter.requestMyMenuInfo();
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.my_menu_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        presenter = new MyMenuPresenter(getContext(), this);
        initDefaultViews();
    }

    private void initDefaultViews() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        defaultAvatar = ConfigManager.getInstance().getImageCallerDefault();

        txtActionBarTitle.setText(userItem.getUsername());
        txtVersion.setText(presenter.getVersion());

        if (userItem.getAvatarItem() == null) {
            imgAvatar.setImageResource(defaultAvatar);
        } else {
            Glide.with(this).load(userItem.getAvatarItem().getOriginUrl())
                    .placeholder(defaultAvatar)
                    .error(defaultAvatar)
                    .centerCrop().into(imgAvatar);
        }
        txtPoint.setText("" + userItem.getCoin());

        initViewWithGender(userItem);
        initMailBackupView(userItem);

        galleryAdapter = new GalleryAdapter(getActivity().getApplicationContext(),
                new ArrayList<ImageItem>());
        galleryAdapter.setOnItemClickListener(this);
        rcvListPhoto.setAdapter(galleryAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListPhoto.getContext(),
                DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getDivider());
        rcvListPhoto.addItemDecoration(dividerItemDecoration);
        needRefreshData = getArguments().getBoolean(REFRESH_DATA);
        initLoadMorePhotoInGallery();
    }

    private void initMailBackupView(UserItem userItem) {
        int visible = userItem.isLoginByFacebook() ? View.GONE : View.VISIBLE;
        txtEmailBackup.setVisibility(visible);
        dividerEmailBackup.setVisibility(visible);
    }

    private void initViewWithGender(UserItem userItem) {
        StringBuilder onlineList = new StringBuilder();
        if (userItem.getGender() == UserItem.MALE) {
            onlineList.append(getString(R.string.girl)).append("\n").append(getString(R.string.online_notify));
            btnBuyPoint.setVisibility(View.VISIBLE);
            txtOnlineList.setText(onlineList.toString());
            txtOnlineList.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_online_list_female, 0, 0);
        } else {
            onlineList.append(getString(R.string.boy)).append("\n").append(getString(R.string.online_notify));
            btnBuyPoint.setVisibility(View.INVISIBLE);
            Utils.addPropertyForRelativeChildView(parentPoint, RelativeLayout.CENTER_IN_PARENT);
            txtOnlineList.setText(onlineList.toString());
            txtOnlineList.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_oneline_list_male, 0, 0);
        }
    }

    private void initLoadMorePhotoInGallery() {
        rcvListPhoto.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!isLoadingMorePhoto && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    isLoadingMorePhoto = true;
                    onLoadMorePhotoInGallery();
                }
            }
        });
    }

    private void onLoadMorePhotoInGallery() {
        presenter.loadMorePhotoInGallery();
    }

    private Drawable getDivider() {
        return getResources().getDrawable(R.drawable.divider_photo);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        isFragmentRunning = true;
        if (needRefreshData) {
            needRefreshData = false;
            presenter.requestMyMenuInfo();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({
            R.id.btn_buy_point, R.id.btn_upload_photo, R.id.btn_change_avatar, R.id.img_edit_profile,
            R.id.group_avatar, R.id.layout_my_notify, R.id.txt_online_list, R.id.txt_call_history,
            R.id.txt_block_list, R.id.txt_notify_setting, R.id.txt_email_backup_setting,
            R.id.txt_call_setting, R.id.layout_guide, R.id.layout_contact,
            R.id.layout_common_guide, R.id.layout_profile_detail, R.id.btn_logout, R.id.group_point})
    public void onClick(View view) {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()) {
            case R.id.img_edit_profile:
                startEditProfileScreen();
                break;
            case R.id.btn_change_avatar:
            case R.id.btn_upload_photo:
                handleUploadPhotoForGallery();
                break;
            case R.id.group_avatar:
                handleUploadAvatar();
                break;
            case R.id.txt_online_list:
                MyMenuContainerFragment.showOnlineListFragment(getActivity());
                break;
            case R.id.txt_call_history:
                MyMenuContainerFragment.showHistoryCallFragment(getActivity());
                break;
            case R.id.txt_email_backup_setting:
                if (ConfigManager.getInstance().getCurrentUser().getEmail().length() == 0) {
                    MyMenuContainerFragment.showRegisterEmailBackupFragment(getActivity());
                } else {
                    MyMenuContainerFragment.showChangeEmailBackupFragment(getActivity());
                }
                break;
            case R.id.txt_call_setting:
                MyMenuContainerFragment.showSettingCallFragment(getActivity());
                break;
            case R.id.layout_profile_detail:
                UserItem me = ConfigManager.getInstance().getCurrentUser();
                ProfileDetailItemActivity.startActivity(getActivity(), me);
                break;
            case R.id.btn_buy_point:
                MyMenuContainerFragment.showChosePaymentTypeFragment(getActivity());
                break;
            case R.id.btn_logout:
                handleLogout();
                break;
            case R.id.txt_notify_setting:
                MyMenuContainerFragment.showSettingPushFragment(getActivity());
                break;
            case R.id.txt_block_list:
                MyMenuContainerFragment.showBlockListFragment(getActivity());
                break;
            case R.id.layout_my_notify:
            case R.id.layout_guide:
            case R.id.layout_contact:
            case R.id.layout_common_guide:

            default:
                break;
        }
    }

    @Override
    public void didLogout() {
        disMissLoading();
        ConfigManager.getInstance().resetSettings();
        presenter.stopLinphoneService();
        Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
        startActivity(intent);
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getActivity().finish();
    }

    @Override
    public void didLogoutError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void didLoadMyProfile(UserItem userItem) {
        txtPoint.setText("" + userItem.getCoin());
        txtActionBarTitle.setText(userItem.getUsername());
        updateAvatarView(userItem.getAvatarItem());
    }

    @Override
    public void didLoadGallery(GalleryItem galleryItem) {
        this.galleryItem = galleryItem;
        galleryAdapter.clearData();
        galleryAdapter.setPhotos(galleryItem.getPhotos());
        Logger.e(TAG, "load Gallery: " + galleryAdapter.getItemCount());
    }

    @Override
    public void didUploadAvatar(ImageItem avatar) {
        uploadingAvatar = false;
        groupUploadAvatar.setVisibility(View.GONE);
        imgMaskApproving.setVisibility(View.VISIBLE);
        txtApproving.setVisibility(View.VISIBLE);
        btnChangeAvatar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUploadAvatarProgressChanged(float percent) {
        this.prwUploadAvatar.setProgress(percent);
    }

    @Override
    public void didUploadAvatarFailure(int errorCode, String errorMessage) {
        uploadingAvatar = false;
        showToastExceptionVolleyError(errorCode, errorMessage);
        groupUploadAvatar.setVisibility(View.GONE);
        imgMaskApproving.setVisibility(View.INVISIBLE);
        txtApproving.setVisibility(View.INVISIBLE);
        btnChangeAvatar.setVisibility(View.VISIBLE);
        imgAvatar.setImageResource(defaultAvatar);
    }

    @Override
    public void didUpLoadPhotoGalleryFailure(int errorCode, String errorMessage) {
        uploadingPhoto = false;
        showToastExceptionVolleyError(errorCode, errorMessage);
        groupUploadPhotoGallery.setVisibility(View.GONE);
    }

    @Override
    public void onStartUploadPhotoGallery(String filePath) {
        this.uploadingPhoto = true;
        this.prwUploadPhotoGallery.resetCount();
        this.groupUploadPhotoGallery.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadGalleryProgressChanged(float percent) {
        this.prwUploadPhotoGallery.setProgress(percent);
    }

    @Override
    public void photoNoMoreInGallery() {
        isLoadingMorePhoto = false;
    }

    @Override
    public void didLoadMorePhotosInGallery(GalleryItem gallery) {
        updatePhotos(gallery);
        isLoadingMorePhoto = false;
    }

    private void updatePhotos(GalleryItem gallery) {
        gallery.addALlFromFirst(this.galleryItem.getPhotos());
        this.galleryItem = gallery;
        galleryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStartUploadAvatarBitmap(final String filePath) {
        this.uploadingAvatar = true;
        this.prwUploadAvatar.resetCount();
        Glide.with(getContext()).load(new File(filePath))
                .fitCenter()
                .dontAnimate().dontTransform()
                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(this.imgAvatar);
        this.btnChangeAvatar.setVisibility(View.GONE);
        this.groupUploadAvatar.setVisibility(View.VISIBLE);
    }

    @Override
    public void didDeleteAvatar() {
        disMissLoading();
        String notifyDeleteAvatar = getString(R.string.delete_avatar_notify);
        Toast.makeText(getContext(), notifyDeleteAvatar, Toast.LENGTH_SHORT).show();
        groupUploadAvatar.setVisibility(View.GONE);
        imgMaskApproving.setVisibility(View.INVISIBLE);
        txtApproving.setVisibility(View.INVISIBLE);
        btnChangeAvatar.setVisibility(View.VISIBLE);
        imgAvatar.setImageResource(defaultAvatar);
    }

    @Override
    public void didDeleteAvatarFailure(int erroCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(erroCode, errorMessage);
    }

    @Override
    public void didUploadPhotoGallery(ImageItem photo) {
        this.uploadingPhoto = false;
        this.groupUploadPhotoGallery.setVisibility(View.GONE);
        galleryAdapter.addPhoto(photo);
        galleryAdapter.notifyDataSetChanged();
    }

    @Subscribe(sticky = true)
    public void onPaymentSuccessEvent(PaymentSuccessEvent paymentSuccessEvent) {
        Logger.e(TAG, "Payment Successfull: " + paymentSuccessEvent.getPoint() + "------------------");

        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.settlement_is_completed))
                .append("\n")
                .append(paymentSuccessEvent.getPoint())
                .append(getString(R.string.pt))
                .append(getString(R.string.have_been_granted));
        showMessageDialog(message.toString());

        EventBus.getDefault().removeStickyEvent(paymentSuccessEvent);
    }

    @Subscribe(sticky = true)
    public void onReloadProfileEvent(ReLoadProfileEvent event) {
        presenter.requestMyMenuInfo();
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangedEvent(CoinChangedEvent event) {
        txtPoint.setText(event.getCoin() + "");
    }

    private void handleUploadPhotoForGallery() {
        if (!uploadingPhoto) {
            currentRequestPhoto = REQUEST_SELECT_PHOTO_FOR_GALLERY;
            SelectImageDialog.showDialogSelectAvatar(this, currentRequestPhoto, getFragmentManager(), false);
        }
    }

    private void handleUploadAvatar() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        if (userItem.hasAvatar() && !uploadingAvatar) {
            if (userItem.getAvatarItem().isApproved()) {
                currentRequestPhoto = REQUEST_SELECT_PHOTO_FOR_AVATAR;
                SelectImageDialog.showDialogSelectAvatar(this, currentRequestPhoto, getFragmentManager(), true);
            }
        } else if (!userItem.hasAvatar() && !uploadingAvatar) {
            currentRequestPhoto = REQUEST_SELECT_PHOTO_FOR_AVATAR;
            SelectImageDialog.showDialogSelectAvatar(this, currentRequestPhoto, getFragmentManager(), false);
        }
    }

    private void handleLogout() {
        showLoading();
        presenter.requestLogout();
    }

    private void updateAvatarView(ImageItem avatarItem) {
        if (avatarItem != null) {
            int visibility = avatarItem.getImageStatus() == ImageItem.IMAGE_APPROVED ? View.INVISIBLE : View.VISIBLE;
            int visibilityCamera = avatarItem.getImageStatus() == ImageItem.IMAGE_PENDING ? View.INVISIBLE : View.VISIBLE;
            imgMaskApproving.setVisibility(visibility);
            txtApproving.setVisibility(visibility);
            btnChangeAvatar.setVisibility(visibilityCamera);
            groupUploadAvatar.setVisibility(View.GONE);
            Glide.with(getActivity().getApplicationContext())
                    .load(avatarItem.getThumbUrl())
                    .placeholder(defaultAvatar).error(defaultAvatar)
                    .fitCenter().dontAnimate().dontTransform()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(defaultAvatar);
            groupUploadAvatar.setVisibility(View.GONE);
            imgMaskApproving.setVisibility(View.INVISIBLE);
            txtApproving.setVisibility(View.INVISIBLE);
            btnChangeAvatar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onUserImageClick(int position) {
        // Show full Image
        ImageDetailActivity.startActivity(getActivity(), galleryItem, position, ImageDetailActivity.MY_PHOTOS,
                ConfigManager.getInstance().getCurrentUser().getUserId());
    }

    @Override
    public void onDeleteImageClick() {
        String confirmDeleteAvatar = getString(R.string.confirm_delete_avatar);
        TextDialog textDialog = new TextDialog.Builder()
                .build(this, confirmDeleteAvatar, CONFIRM_DELETE_AVATAR);
        textDialog.show(getFragmentManager(), TextDialog.class.getSimpleName());
    }

    /**
     * On Confirm Delete Image Click Ok listener
     *
     * @param requestCode
     */
    @Override
    public void onTextDialogOkClick(int requestCode) {
        showLoading();
        presenter.deleteAvatar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e(TAG, "onActivity Result ");
        switch (requestCode) {
            case SelectImageDialog.PICK_AVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    handleImageFromCamera();
                }
                break;
            case SelectImageDialog.PICK_AVATAR_GALLERY:
                if (resultCode == RESULT_OK) {
                    pickedImage = data.getData();
                    handleImageFromGallery();
                }
                break;
            case SelectImageDialog.CROP_IMAGE:
                if (resultCode == RESULT_OK) {
                    handleImageCropped(data);
                }
                break;
            case REQUEST_EDIT_PROFILE:
                if (resultCode == RESULT_OK) {
                    presenter.requestMyMenuInfo();
                }
                break;
            default:
                break;
        }
    }

    private void handleImageFromCamera() {
        File outFile = new File(Environment.getExternalStorageDirectory() + SelectImageDialog.AVATAR_NAME);
        if (!outFile.exists()) {
            Toast.makeText(getContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
        } else {
            pickedImage = Uri.fromFile(outFile);
            CropImageActivity.startActivityForResult(this, pickedImage);
        }
    }

    private void handleImageCropped(Intent data) {
        String imagePath = data.getStringExtra(CropImageActivity.IMAGE_CROPPED);
        if (currentRequestPhoto == REQUEST_SELECT_PHOTO_FOR_AVATAR) {
            presenter.uploadAvatar(imagePath);
        } else {
            presenter.uploadPhotoForGallery(imagePath);
        }
    }

    private void handleImageFromGallery() {
        getImageFilePath();
    }

    private void getImageFilePath() {
        CropImageActivity.startActivityForResult(this, pickedImage);
    }

    private void startEditProfileScreen() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        if (gender == UserItem.MALE) {
            UpdateProfileMaleActivity.startActivityForResult(this,
                    ProfileBaseActivity.MODE_UPDATE, REQUEST_EDIT_PROFILE);
        } else {
            UpdateProfileFemaleActivity.startActivityForResult(this,
                    ProfileBaseActivity.MODE_UPDATE, REQUEST_EDIT_PROFILE);
        }
    }
}
