package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;

import jp.newbees.mastersip.adapter.GalleryModeFourAdapter;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.presenter.PhotoGalleryPresenter;

/**
 * Created by ducpv on 2/7/17.
 */

public class PhotoGalleryActivity extends BaseGalleryActivity implements GalleryModeFourAdapter.OnItemClickListener,
PhotoGalleryPresenter.PhotoGalleryView {

    private static final String GALLERY_ITEM = "GALLERY_ITEM";

    private PhotoGalleryPresenter photoGalleryPresenter;

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(ImageDetailActivity.POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void didLoadMorePhotos(GalleryItem galleryItem) {
        this.galleryItem = galleryItem;
        galleryModeFourAdapter.addAll(galleryItem.getPhotos());
        galleryModeFourAdapter.notifyDataSetChanged();
        isLoading = false;
        disMissLoading();
    }

    @Override
    public void didLoadMorePhotosError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
        isLoading = false;
        disMissLoading();
    }

    @Override
    protected void loadMore() {
        photoGalleryPresenter.loadMorePhotos(galleryItem);
    }

    @Override
    protected void init() {
        photoGalleryPresenter = new PhotoGalleryPresenter(this, this);
        galleryItem = getIntent().getParcelableExtra(GALLERY_ITEM);
    }

    /**
     *
     * @param activity
     * @param galleryItem
     * @param requestCode
     */
    public static void startActivityForResult(Activity activity, GalleryItem galleryItem,
                                              int requestCode) {
        Intent intent = new Intent(activity, PhotoGalleryActivity.class);
        intent.putExtra(GALLERY_ITEM, galleryItem);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableCallTask.Type type) {

    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {

    }
}
