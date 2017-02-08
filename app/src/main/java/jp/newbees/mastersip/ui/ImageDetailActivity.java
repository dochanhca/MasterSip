package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryPagerAdapter;
import jp.newbees.mastersip.customviews.HackyViewPager;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.event.ReLoadProfileEvent;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.presenter.ImageDetailPresenter;
import jp.newbees.mastersip.ui.auth.CropImageActivity;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.ImageUtils;

/**
 * Created by ducpv on 2/6/17.
 */

public class ImageDetailActivity extends CallCenterActivity implements ImageDetailPresenter.PhotoDetailView,
        TextDialog.OnTextDialogClick {

    private static final String LIST_PHOTO = "LIST_PHOTO";
    private static final String IS_FROM_MY_MENU = "IS_FROM_MY_MENU";
    public static final String POSITION = "POSITION";
    private static final int VIEW_ALL_PHOTO = 12;

    @BindView(R.id.view_pager_gallery)
    HackyViewPager viewPagerGallery;
    @BindView(R.id.txt_save_photo)
    HiraginoTextView txtSavePhoto;
    @BindView(R.id.txt_change_photo)
    HiraginoTextView txtChangePhoto;
    @BindView(R.id.txt_report)
    HiraginoTextView txtReport;
    @BindView(R.id.txt_delete_photo)
    HiraginoTextView txtDeletePhoto;
    @BindView(R.id.layout_bottom_action)
    LinearLayout layoutBottomAction;
    @BindView(R.id.prw_update_image)
    ProgressWheel progressWheel;

    private List<ImageItem> photos;
    private int currentPosition;
    private boolean isFromMyMenu;
    private ImageDetailPresenter imageDetailPresenter;
    private Uri pickedImage;
    private GalleryPagerAdapter galleryPagerAdapter;
    private boolean needReloadProfile;

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            updateViewVisibility();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        photos = getIntent().getParcelableArrayListExtra(LIST_PHOTO);
        currentPosition = getIntent().getIntExtra(POSITION, 0);
        isFromMyMenu = getIntent().getBooleanExtra(IS_FROM_MY_MENU, false);

        initHeader(currentPosition + 1 + "/" + photos.size());
        initBottomActions();
        initViewPager();
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        imageDetailPresenter = new ImageDetailPresenter(getApplicationContext(), this);
    }

    @OnClick({R.id.txt_save_photo, R.id.txt_change_photo, R.id.txt_report, R.id.txt_delete_photo,
            R.id.btn_view_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_save_photo:
                break;
            case R.id.txt_change_photo:
                SelectImageDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.txt_report:
                break;
            case R.id.txt_delete_photo:
                confirmDeleteImage();
                break;
            case R.id.btn_view_all:
                PhotoGalleryActivity.startActivityForResult(this, photos, VIEW_ALL_PHOTO);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            case VIEW_ALL_PHOTO:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra(POSITION, 0);
                    viewPagerGallery.setCurrentItem(position);
                }
                break;
        }
    }

    @Override
    public void didUpdateImage(ImageItem imageItem) {
        progressWheel.setVisibility(View.GONE);
        photos.set(currentPosition, imageItem);
        galleryPagerAdapter.notifyDataSetChanged();
        viewPagerGallery.setPagingEnabled(true);
        needReloadProfile = true;
    }

    @Override
    public void didUpdateImageError(int errorCode, String errorMessage) {
        progressWheel.setVisibility(View.GONE);
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
        viewPagerGallery.setPagingEnabled(true);
    }

    @Override
    public void onUpdateImageProgressChanged(float percent) {
        progressWheel.setProgress(percent);
    }

    @Override
    public void onStartUploadPhotoGallery(String filePath) {
        progressWheel.resetCount();
        progressWheel.setVisibility(View.VISIBLE);
        viewPagerGallery.setPagingEnabled(false);
    }

    @Override
    public void didDeleteImage() {
        disMissLoading();
        needReloadProfile = true;
        photos.remove(currentPosition);
        if ((photos.size() == 0)) {
            EventBus.getDefault().post(new ReLoadProfileEvent(true));
            super.onBackPressed();
        }
        galleryPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void didDeleteImageError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        showLoading();
        imageDetailPresenter.deleteImage(photos.get(currentPosition));
    }

    @Override
    public void onBackPressed() {
        if (needReloadProfile) {
            EventBus.getDefault().post(new ReLoadProfileEvent(true));
        }
        super.onBackPressed();
    }

    @Override
    protected void onImageBackPressed() {
        if (needReloadProfile) {
            EventBus.getDefault().post(new ReLoadProfileEvent(true));
        }
        super.onImageBackPressed();
    }

    private void confirmDeleteImage() {
        TextDialog.openTextDialog(getSupportFragmentManager(), getString(R.string.do_you_want_to_delete_this_photo),
                getString(R.string.delete_photo),
                "", false);
    }

    private void initViewPager() {
        galleryPagerAdapter = new GalleryPagerAdapter(photos, getApplicationContext());
        viewPagerGallery.setAdapter(galleryPagerAdapter);
        viewPagerGallery.setCurrentItem(currentPosition);
        viewPagerGallery.addOnPageChangeListener(onPageChangeListener);
        updateViewVisibility();
    }

    private void initBottomActions() {
        if (isFromMyMenu) {
            txtChangePhoto.setVisibility(View.VISIBLE);
            txtDeletePhoto.setVisibility(View.VISIBLE);
            txtSavePhoto.setVisibility(View.GONE);
            txtReport.setVisibility(View.GONE);
        } else {
            txtChangePhoto.setVisibility(View.GONE);
            txtDeletePhoto.setVisibility(View.GONE);
            txtSavePhoto.setVisibility(View.VISIBLE);
            txtReport.setVisibility(View.VISIBLE);
        }
    }

    private void updateViewVisibility() {
        ImageItem currentItem = photos.get(currentPosition);
        if (currentItem.getImageStatus() == ImageItem.IMAGE_PENDING) {
            layoutBottomAction.setVisibility(View.GONE);
        } else {
            layoutBottomAction.setVisibility(View.VISIBLE);
        }
        StringBuilder header = new StringBuilder();
        header.append(currentPosition + 1).append("/").append(photos.size());
        txtActionBarTitle.setText(header.toString());
    }

    private void handleImageCropped(Intent data) {
        byte[] result = data.getByteArrayExtra(CropImageActivity.IMAGE_CROPPED);

        imageDetailPresenter.uploadPhotoForGallery(photos.get(currentPosition), result);
    }

    private void handleImageFromCamera() {
        File outFile = new File(Environment.getExternalStorageDirectory() + SelectImageDialog.AVATAR_NAME);
        if (!outFile.exists()) {
            Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
        } else {
            pickedImage = Uri.fromFile(outFile);
            gotoCropImageScreen(pickedImage);
        }
    }

    private void handleImageFromGallery() {
        getImageFilePath();
    }

    private void getImageFilePath() {
        if (pickedImage.toString().startsWith("content://com.google.android.apps.photos.content")) {
            pickedImage = ImageUtils.getImageUrlWithAuthority(this, pickedImage);
        }
        gotoCropImageScreen(pickedImage);
    }

    private void gotoCropImageScreen(Uri imagePath) {
        Intent intent = new Intent(getApplicationContext(), CropImageActivity.class);

        intent.putExtra(CropImageActivity.IMAGE_URI, imagePath);

        startActivityForResult(intent, SelectImageDialog.CROP_IMAGE);
    }

    /**
     * @param activity
     * @param imageItems
     * @param imagePosition
     * @param isFromMyMenu
     */
    public static void startActivity(Activity activity, List<ImageItem> imageItems, int imagePosition,
                                     boolean isFromMyMenu) {
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putParcelableArrayListExtra(LIST_PHOTO, (ArrayList<? extends Parcelable>) imageItems);
        intent.putExtra(POSITION, imagePosition);
        intent.putExtra(IS_FROM_MY_MENU, isFromMyMenu);
        activity.startActivity(intent);
    }
}
