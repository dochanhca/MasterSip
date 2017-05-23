package jp.newbees.mastersip.ui.chatting;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryModeFourAdapter;
import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.presenter.chatting.ChattingPhotoGalleryPresenter;
import jp.newbees.mastersip.ui.BaseGalleryActivity;
import jp.newbees.mastersip.ui.ImageDetailActivity;

/**
 * Created by ducpv on 2/10/17.
 */

public class ChattingPhotoGalleryActivity extends BaseGalleryActivity implements GalleryModeFourAdapter.OnItemClickListener, ChattingPhotoGalleryPresenter.ChattingPhotoGalleryView {

    private static final String USER_ID = "USER_ID";

    private String userId;
    private ChattingPhotoGalleryPresenter chattingPhotoGalleryPresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_photo_gallery;
    }

    @Override
    protected void loadMore() {
        chattingPhotoGalleryPresenter.loadChattingPhotos(userId,
                galleryItem.getNextId(), true);
    }

    @Override
    protected void init() {
        imgBack.setImageResource(R.drawable.ic_close_white);

        userId = getIntent().getStringExtra(USER_ID);

        chattingPhotoGalleryPresenter = new ChattingPhotoGalleryPresenter(getApplicationContext(), this);
        showLoading();
        chattingPhotoGalleryPresenter.loadChattingPhotos(userId, "", false);
    }

    @Override
    public void onItemClick(int position) {
        ImageDetailActivity.startActivity(this, galleryItem, position,
                ImageDetailActivity.RECEIVED_PHOTOS_FROM_CHAT, userId);
    }

    @Override
    public void didLoadChattingPhotos(ChattingGalleryItem chattingGalleryItem) {
        makeAllImageApproved(chattingGalleryItem);
        this.galleryItem = chattingGalleryItem;
        galleryModeFourAdapter.addAll(chattingGalleryItem.getPhotos());
        disMissLoading();
    }

    @Override
    public void didLoadChattingPhotosError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void didLoadMoreChattingPhotos(ChattingGalleryItem chattingGalleryItem) {
        makeAllImageApproved(chattingGalleryItem);
        galleryModeFourAdapter.addAll(chattingGalleryItem.getPhotos());
        updatePhotos(chattingGalleryItem);
        disMissLoading();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bot);
    }

    /**
     * Trick because images from chat room don't need approve
     * @param chattingGalleryItem
     */
    private void makeAllImageApproved(ChattingGalleryItem chattingGalleryItem) {
        for (ImageItem item : chattingGalleryItem.getPhotos()) {
            item.setImageStatus(ImageItem.IMAGE_APPROVED);
        }
    }

    private void updatePhotos(ChattingGalleryItem chattingGallery) {
        List<ImageItem> tempPhotos = this.galleryItem.getPhotos();
        this.galleryItem = chattingGallery;
        tempPhotos.addAll(chattingGallery.getPhotos());
        this.galleryItem.setImageItems(tempPhotos);
    }

    /**
     * @param activity
     * @param userId
     */
    public static void startActivity(Activity activity, String userId) {
        Intent intent = new Intent(activity, ChattingPhotoGalleryActivity.class);
        intent.putExtra(USER_ID, userId);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_bot, R.anim.exit_to_top);
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(int type) {

    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {

    }
}
