package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryPagerAdapter;
import jp.newbees.mastersip.customviews.HackyViewPager;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.event.ReLoadProfileEvent;
import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.presenter.ImageDetailPresenter;
import jp.newbees.mastersip.ui.auth.CropImageActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;

/**
 * Created by ducpv on 2/6/17.
 */

public class ImageDetailActivity extends CallActivity implements ImageDetailPresenter.PhotoDetailView {

    private static final String GALLERY_ITEM = "GALLERY_ITEM";
    private static final String VIEW_TYPE = "VIEW_TYPE";
    public static final String POSITION = "POSITION";
    private static final int VIEW_ALL_PHOTO = 12;

    public static final int MY_PHOTOS = 1;
    public static final int OTHER_USER_PHOTOS = 3;
    public static final int RECEIVED_PHOTOS_FROM_CHAT = 2;
    private static final int REQUEST_DELETE_IMAGE = 24;

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
    @BindView(R.id.btn_view_all)
    TextView btnViewAll;

    private GalleryItem galleryItem;
    private List<ImageItem> photos;
    private int currentPosition;
    private int viewType;
    private ImageDetailPresenter imageDetailPresenter;
    private Uri pickedImage;
    private GalleryPagerAdapter galleryPagerAdapter;
    private boolean needReloadProfile;
    private boolean isLoadingMorePhotos = false;

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        boolean lastPageChanged = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int lastIndex = galleryPagerAdapter.getCount() - 1;
            if (lastPageChanged && position == lastIndex && !isLoadingMorePhotos && galleryItem.hasMorePhotos()) {
                loadMorePhotos();
            }
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            updateViewVisibility();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int lastIndex = galleryPagerAdapter.getCount() - 1;
            lastPageChanged = (currentPosition == lastIndex && state == 1) ? true : false;
        }

        private void loadMorePhotos() {
            showLoading();
            isLoadingMorePhotos = true;
            if (viewType == MY_PHOTOS) {
                imageDetailPresenter.loadMorePhotos(galleryItem);
            } else {
                ChattingGalleryItem chattingGalleryItem = (ChattingGalleryItem) galleryItem;
                imageDetailPresenter.loadMoreChattingPhotos(chattingGalleryItem);
            }
        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        galleryItem = getIntent().getParcelableExtra(GALLERY_ITEM);
        currentPosition = getIntent().getIntExtra(POSITION, 0);
        viewType = getIntent().getIntExtra(VIEW_TYPE, 1);

        photos = galleryItem.getPhotos();
        initHeader(currentPosition + 1 + "/" + photos.size());
        initViewsWithViewType();
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
            case R.id.txt_change_photo:
                SelectImageDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.txt_delete_photo:
                confirmDeleteImage();
                break;
            case R.id.btn_view_all:
                PhotoGalleryActivity.startActivityForResult(this, galleryItem, VIEW_ALL_PHOTO);
                break;
            case R.id.txt_save_photo:
            case R.id.txt_report:
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
                    CropImageActivity.startActivityForResult(this, pickedImage);
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
                    viewPagerGallery.setCurrentItem(position, false);
                }
                break;
            default:
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
        if (photos.isEmpty()) {
            EventBus.getDefault().post(new ReLoadProfileEvent(true));
            super.onBackPressed();
        }
        updateHeaderView();
        galleryPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void didDeleteImageError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void didLoadMorePhotos(GalleryItem galleryItem) {
        isLoadingMorePhotos = false;
        makeAllImageApproved(galleryItem);
        updatePhotos(galleryItem);
        disMissLoading();
        viewPagerGallery.setCurrentItem(++currentPosition);
    }

    @Override
    public void didLoadMorePhotosError(int errorCode, String errorMessage) {
        isLoadingMorePhotos = false;
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void didLoadMoreChattingPhotos(ChattingGalleryItem chattingGalleryItem) {
        isLoadingMorePhotos = false;
        disMissLoading();
        makeAllImageApproved(chattingGalleryItem);
        updatePhotos(chattingGalleryItem);
        viewPagerGallery.setCurrentItem(++currentPosition);
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        super.onTextDialogOkClick(requestCode);
        if (requestCode == REQUEST_DELETE_IMAGE) {
            showLoading();
            imageDetailPresenter.deleteImage(photos.get(currentPosition));
        }
    }

    @Override
    public void onBackPressed() {
        if (needReloadProfile && viewType == MY_PHOTOS) {
            EventBus.getDefault().postSticky(new ReLoadProfileEvent(true));
        }
        super.onBackPressed();
    }

    /**
     * Trick because images from chat room don't need approve
     * @param galleryItem
     */
    private void makeAllImageApproved(GalleryItem galleryItem) {
        if (viewType == RECEIVED_PHOTOS_FROM_CHAT) {
            for (ImageItem item : galleryItem.getPhotos()) {
                item.setImageStatus(ImageItem.IMAGE_APPROVED);
            }
        }
    }

    private void confirmDeleteImage() {
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_DELETE_IMAGE).setTitle(getString(R.string.delete_photo))
                .build(getString(R.string.do_you_want_to_delete_this_photo));
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    private void initViewPager() {
        galleryPagerAdapter = new GalleryPagerAdapter(photos, getApplicationContext());
        viewPagerGallery.setAdapter(galleryPagerAdapter);
        viewPagerGallery.setCurrentItem(currentPosition);
        viewPagerGallery.addOnPageChangeListener(onPageChangeListener);
        updateViewVisibility();
    }

    private void initViewsWithViewType() {
        if (viewType == RECEIVED_PHOTOS_FROM_CHAT) {
            txtChangePhoto.setVisibility(View.GONE);
            txtDeletePhoto.setVisibility(View.GONE);
            txtSavePhoto.setVisibility(View.VISIBLE);
            txtReport.setVisibility(View.VISIBLE);
            btnViewAll.setVisibility(View.GONE);
        } else if (viewType == MY_PHOTOS) {
            txtChangePhoto.setVisibility(View.VISIBLE);
            txtDeletePhoto.setVisibility(View.VISIBLE);
            txtSavePhoto.setVisibility(View.GONE);
            txtReport.setVisibility(View.GONE);
            btnViewAll.setVisibility(View.VISIBLE);
        } else {
            txtChangePhoto.setVisibility(View.GONE);
            txtDeletePhoto.setVisibility(View.GONE);
            txtSavePhoto.setVisibility(View.GONE);
            txtReport.setVisibility(View.GONE);
        }
    }

    private void updateViewVisibility() {
        ImageItem currentItem = photos.get(currentPosition);
        if (currentItem.getImageStatus() == ImageItem.IMAGE_PENDING) {
            layoutBottomAction.setVisibility(View.GONE);
        } else {
            layoutBottomAction.setVisibility(View.VISIBLE);
        }
        updateHeaderView();

    }

    private void updateHeaderView() {
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
            CropImageActivity.startActivityForResult(this, pickedImage);
        }
    }

    private void updatePhotos(GalleryItem galleryItem) {
        this.galleryItem = galleryItem;
        this.photos.addAll(galleryItem.getPhotos());
        this.galleryItem.setImageItems(photos);

        galleryPagerAdapter.notifyDataSetChanged();
    }

    /**
     * @param activity
     * @param galleryItem
     * @param imagePosition
     * @param type
     */
    public static void startActivity(Activity activity, GalleryItem galleryItem, int imagePosition,
                                     int type) {
        Intent intent = new Intent(activity, ImageDetailActivity.class);
        intent.putExtra(GALLERY_ITEM, galleryItem);
        intent.putExtra(POSITION, imagePosition);
        intent.putExtra(VIEW_TYPE, type);
        activity.startActivity(intent);
    }
}
