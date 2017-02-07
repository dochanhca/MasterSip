package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryPagerAdapter;
import jp.newbees.mastersip.customviews.HackyViewPager;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.ui.call.CallCenterActivity;

/**
 * Created by ducpv on 2/6/17.
 */

public class ImageDetailActivity extends CallCenterActivity {

    private static final String LIST_PHOTO = "LIST_PHOTO";
    private static final String IS_FROM_MY_MENU = "IS_FROM_MY_MENU";
    public static final String POSITION = "POSITION";
    private static final int VIEW_ALL_PHOTO = 1;

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

    private List<ImageItem> photos;
    private int currentPosition;
    private boolean isFromMyMenu;

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

    }

    @OnClick({R.id.txt_save_photo, R.id.txt_change_photo, R.id.txt_report, R.id.txt_delete_photo,
            R.id.btn_view_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_save_photo:
                break;
            case R.id.txt_change_photo:
                break;
            case R.id.txt_report:
                break;
            case R.id.txt_delete_photo:
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
        if (requestCode == VIEW_ALL_PHOTO && resultCode == RESULT_OK) {
            int position = data.getIntExtra(POSITION, 0);
            viewPagerGallery.setCurrentItem(position);
        }
    }

    private void initViewPager() {
        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(photos, getApplicationContext());
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
