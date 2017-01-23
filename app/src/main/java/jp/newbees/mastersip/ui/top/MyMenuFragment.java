package jp.newbees.mastersip.ui.top;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.UserPhotoAdapter;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.MyMenuPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.ui.auth.CropImageActivity;
import jp.newbees.mastersip.ui.dialog.SelectAvatarDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.ImageUtils;
import jp.newbees.mastersip.utils.Logger;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thangit14 on 12/22/16.
 */

public class MyMenuFragment extends BaseFragment implements MyMenuPresenter.MyMenuView, UserPhotoAdapter.OnItemClickListener,
        SelectAvatarDialog.OnSelectAvatarDiaLogClick {
    private static final int REQUEST_SELECT_AVATAR = 8888;
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
    TextView txtApproving;
    @BindView(R.id.img_mask_approving)
    ImageView imgMaskApproving;
    @BindView(R.id.prw_upload_avatar)
    ProgressWheel prwUploadAvatar;
    @BindView(R.id.group_upload_avatar)
    RelativeLayout groupUploadAvatar;

    private MyMenuPresenter presenter;
    private int defaultAvatar;
    private UserPhotoAdapter userPhotoAdapter;
    private Uri pickedImage;
    private boolean requestingSelectPhoto;
    private boolean uploadingAvatar;


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
        defaultAvatar = ConfigManager.getInstance().getImageCallerDefault();
        this.txtActionBarTitle.setText(userItem.getUsername());
        this.imgAvatar.setImageResource(defaultAvatar);
        this.txtPoint.setText("" + userItem.getCoin());
        int isShowButtonBuyPoint = userItem.getGender() == UserItem.MALE ? View.VISIBLE : View.GONE;
        this.btnBuyPoint.setVisibility(isShowButtonBuyPoint);
        this.userPhotoAdapter = new UserPhotoAdapter(getContext(), new ArrayList<ImageItem>(), userItem.getGender());
        this.userPhotoAdapter.setOnItemClickListener(this);
        this.rcvListPhoto.setAdapter(userPhotoAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!requestingSelectPhoto) {
            presenter.requestMyMenuInfo();
//            testProgressWheel();
        }
    }

    public static Fragment newInstance() {
        Fragment fragment = new MyMenuFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({
            R.id.btn_buy_point,
            R.id.btn_upload_photo,
            R.id.btn_logout,
            R.id.btn_backup_email,
            R.id.group_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_buy_point:
                break;
            case R.id.btn_upload_photo:
                break;
            case R.id.btn_logout:
                this.handleLogout();
                break;
            case R.id.btn_backup_email:
                break;
            case R.id.group_avatar:
                handleUploadImage();
                break;
        }
    }

    private void handleUploadImage() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        if (!userItem.hasAvatar() && !uploadingAvatar) {
            SelectAvatarDialog.showDialogSelectAvatar(this, REQUEST_SELECT_AVATAR, getFragmentManager(), false);
        }
    }

    private void handleLogout() {
        showLoading();
        presenter.requestLogout();
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
        this.userPhotoAdapter.setPhotos(galleryItem.getImageItems());
        this.userPhotoAdapter.notifyDataSetChanged();
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
    public void onUploadProgressChanged(float percent) {
        Logger.e(TAG,"Percent uploading .." + percent);
        this.prwUploadAvatar.setProgress(percent);
    }

    @Override
    public void didUploadAvatarFailure(String errorMessage) {
        uploadingAvatar = false;
        Toast.makeText(getContext(),errorMessage,Toast.LENGTH_SHORT).show();
        groupUploadAvatar.setVisibility(View.GONE);
        imgMaskApproving.setVisibility(View.INVISIBLE);
        txtApproving.setVisibility(View.INVISIBLE);
        btnChangeAvatar.setVisibility(View.VISIBLE);
        imgAvatar.setImageResource(defaultAvatar);
    }

    @Override
    public void onStartUploadAvatarBitmap(Bitmap bitmap) {
        this.uploadingAvatar = true;
        this.imgAvatar.setImageBitmap(bitmap);
        this.btnChangeAvatar.setVisibility(View.GONE);
        this.groupUploadAvatar.setVisibility(View.VISIBLE);
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
                    .fitCenter().dontAnimate()
                    .dontTransform()
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

    }

    @Override
    public void onDeleteImageClick() {

    }

    @Override
    public void onStartSelectAvatar() {
        requestingSelectPhoto = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SelectAvatarDialog.PICK_AVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    handleImageFromCamera();
                }
                break;
            case SelectAvatarDialog.PICK_AVATAR_GALLERY:
                if (resultCode == RESULT_OK) {
                    pickedImage = data.getData();
                    handleImageFromGallery();
                }
                break;
            case SelectAvatarDialog.CROP_IMAGE:
                requestingSelectPhoto = false;
                if (resultCode == RESULT_OK) {
                    handleImageCropped(data);
                }
                break;
        }
    }

    private void handleImageFromCamera() {
        File outFile = new File(Environment.getExternalStorageDirectory() + SelectAvatarDialog.AVATAR_NAME);
        if (!outFile.exists()) {
            Toast.makeText(getContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
        } else {
            pickedImage = Uri.fromFile(outFile);
            gotoCropImageScreen(pickedImage);
        }
    }

    private void handleImageCropped(Intent data) {
        byte[] result = data.getByteArrayExtra(CropImageActivity.IMAGE_CROPPED);
        presenter.uploadAvatar(result);
    }

    private void handleImageFromGallery() {
        getImageFilePath();
    }

    private void getImageFilePath() {
        if (pickedImage.toString().startsWith("content://com.google.android.apps.photos.content")) {
            pickedImage = ImageUtils.getImageUrlWithAuthority(getContext(), pickedImage);
        }
        gotoCropImageScreen(pickedImage);
    }

    private void gotoCropImageScreen(Uri imagePath) {
        Intent intent = new Intent(getApplicationContext(), CropImageActivity.class);
        intent.putExtra(CropImageActivity.IMAGE_URI, imagePath);
        startActivityForResult(intent, SelectAvatarDialog.CROP_IMAGE);
    }
}
