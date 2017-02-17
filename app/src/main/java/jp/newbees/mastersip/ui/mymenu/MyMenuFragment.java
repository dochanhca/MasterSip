package jp.newbees.mastersip.ui.mymenu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryAdapter;
import jp.newbees.mastersip.adapter.UserPhotoAdapter;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.MyMenuPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.ImageDetailActivity;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.ui.auth.CropImageActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.top.MyMenuContainerFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by thangit14 on 12/22/16.
 */

public class MyMenuFragment extends BaseFragment implements MyMenuPresenter.MyMenuView, UserPhotoAdapter.OnItemClickListener,
        SelectImageDialog.OnSelectAvatarDiaLogClick, TextDialog.OnTextDialogClick {

    private static final int REQUEST_SELECT_PHOTO_FOR_AVATAR = 8888;
    private static final int REQUEST_SELECT_PHOTO_FOR_GALLERY = 8989;
    private static final String REFRESH_DATA = "REFRESH_DATA";
    private static final int CONFIRM_DELETE_AVATAR = 11;

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

        galleryAdapter = new GalleryAdapter(getContext(), new ArrayList<ImageItem>());
        galleryAdapter.setOnItemClickListener(this);
        rcvListPhoto.setAdapter(galleryAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListPhoto.getContext(),
                DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getDivider());
        rcvListPhoto.addItemDecoration(dividerItemDecoration);
        needRefreshData = getArguments().getBoolean(REFRESH_DATA);
        initLoadMorePhotoInGallery();
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
            btnBuyPoint.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
        isFragmentRunning = true;
        if (needRefreshData) {
            needRefreshData = false;
            presenter.requestMyMenuInfo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnClick({
            R.id.btn_buy_point, R.id.btn_upload_photo, R.id.btn_change_avatar,
            R.id.group_avatar, R.id.layout_my_notify, R.id.txt_online_list, R.id.txt_call_history,
            R.id.txt_block_list, R.id.txt_notify_setting, R.id.txt_mail_backup_setting,
            R.id.txt_call_setting, R.id.layout_guide, R.id.layout_contact,
            R.id.layout_common_guide, R.id.layout_profile_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_buy_point:
                break;
            case R.id.btn_upload_photo:
                handleUploadPhotoForGallery();
                break;
            case R.id.btn_change_avatar:
                break;
            case R.id.group_avatar:
                handleUploadAvatar();
                break;
            case R.id.layout_my_notify:
                break;
            case R.id.txt_online_list:
                MyMenuContainerFragment.showOnlineListFragment(getActivity());
                break;
            case R.id.txt_call_history:
                break;
            case R.id.txt_block_list:
                break;
            case R.id.txt_notify_setting:
                break;
            case R.id.txt_mail_backup_setting:
                MyMenuContainerFragment.showRegisterEmailBackupFragment(getActivity());
                break;
            case R.id.txt_call_setting:
                MyMenuContainerFragment.showSettingCallFragment(getActivity());
                break;
            case R.id.layout_guide:
                break;
            case R.id.layout_contact:
                break;
            case R.id.layout_common_guide:
                break;
            case R.id.layout_profile_detail:
                break;
            default:
                break;
        }
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

    @Override
    public void didLogout() {
        disMissLoading();
        Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void didLoadMyProfile(UserItem userItem) {
        this.txtPoint.setText("" + userItem.getCoin());
        this.updateAvatarView(userItem.getAvatarItem());
    }

    @Override
    public void didLoadGallery(GalleryItem galleryItem) {
        this.galleryItem = galleryItem;
        this.galleryAdapter.setPhotos(galleryItem.getPhotos());
        this.galleryAdapter.notifyDataSetChanged();
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
    public void didUploadAvatarFailure(String errorMessage) {
        uploadingAvatar = false;
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        groupUploadAvatar.setVisibility(View.GONE);
        imgMaskApproving.setVisibility(View.INVISIBLE);
        txtApproving.setVisibility(View.INVISIBLE);
        btnChangeAvatar.setVisibility(View.VISIBLE);
        imgAvatar.setImageResource(defaultAvatar);
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
        Glide.with(getContext())
                .load(new File(filePath))
                .fitCenter()
                .dontAnimate()
                .dontTransform()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
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
    public void didDeleteAvatarFailure() {
        disMissLoading();
    }

    @Override
    public void didUploadPhotoGallery(ImageItem photo) {
        this.uploadingPhoto = false;
        this.groupUploadPhotoGallery.setVisibility(View.GONE);
        galleryAdapter.addPhoto(photo);
        galleryAdapter.notifyDataSetChanged();
    }


    private void updateAvatarView(ImageItem avatarItem) {
        if (avatarItem != null) {
            int visibility = avatarItem.getImageStatus() == ImageItem.IMAGE_APPROVED ? View.INVISIBLE : View.VISIBLE;
            int visibilityCamera = avatarItem.getImageStatus() == ImageItem.IMAGE_PENDING ? View.INVISIBLE : View.VISIBLE;
            imgMaskApproving.setVisibility(visibility);
            txtApproving.setVisibility(visibility);
            btnChangeAvatar.setVisibility(visibilityCamera);
            groupUploadAvatar.setVisibility(View.GONE);
            Glide.with(imgAvatar.getContext())
                    .load(avatarItem.getThumbUrl())
                    .placeholder(defaultAvatar)
                    .fitCenter()
                    .dontAnimate()
                    .dontTransform()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgAvatar);
        } else {
            groupUploadAvatar.setVisibility(View.GONE);
            imgMaskApproving.setVisibility(View.INVISIBLE);
            txtApproving.setVisibility(View.INVISIBLE);
            btnChangeAvatar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onUserImageClick(int position) {
        // Show full Image
        ImageDetailActivity.startActivity(getActivity(), galleryItem, position, ImageDetailActivity.MY_PHOTOS);
    }

    @Override
    public void onDeleteImageClick() {
        String confirmDeleteAvatar = getString(R.string.confirm_delete_avatar);
        TextDialog.openTextDialog(this, CONFIRM_DELETE_AVATAR, getFragmentManager(),
                confirmDeleteAvatar, "");
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
        byte[] result = data.getByteArrayExtra(CropImageActivity.IMAGE_CROPPED);
        if (currentRequestPhoto == REQUEST_SELECT_PHOTO_FOR_AVATAR) {
            presenter.uploadAvatar(result);
        } else {
            presenter.uploadPhotoForGallery(result);
        }
    }

    private void handleImageFromGallery() {
        getImageFilePath();
    }

    private void getImageFilePath() {
        if (pickedImage.toString().startsWith("content://com.google.android.apps.photos.content")) {
            pickedImage = ImageUtils.getImageUrlWithAuthority(getContext(), pickedImage);
        }
        CropImageActivity.startActivityForResult(this, pickedImage);
    }

    public void reloadData() {
        presenter.requestMyMenuInfo();
    }
}
