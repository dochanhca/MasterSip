package jp.newbees.mastersip.ui.chatting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryModeFourAdapter;
import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.presenter.chatting.ChattingPhotoGalleryPresenter;
import jp.newbees.mastersip.ui.ImageDetailActivity;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.utils.GridSpacingItemDecoration;

/**
 * Created by ducpv on 2/10/17.
 */

public class ChattingPhotoGalleryActivity extends CallCenterActivity implements GalleryModeFourAdapter.OnItemClickListener, ChattingPhotoGalleryPresenter.ChattingPhotoGalleryView {

    private static final int SPAN_COUNT = 4;
    private static final String USER_ID = "USER_ID";

    private RecyclerView recyclerGallery;
    private String userId;
    private ChattingGalleryItem chattingGalleryItem = new ChattingGalleryItem();
    private boolean isLoading = false;
    private GalleryModeFourAdapter galleryModeFourAdapter;

    private ChattingPhotoGalleryPresenter chattingPhotoGalleryPresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_photo_gallery;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.photo_gallery));
        recyclerGallery = (RecyclerView) findViewById(R.id.recycler_gallery);
        imgBack.setImageResource(R.drawable.ic_close_white);

        initRecyclerGallery();
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        userId = getIntent().getStringExtra(USER_ID);

        chattingPhotoGalleryPresenter = new ChattingPhotoGalleryPresenter(getApplicationContext(), this);
        showLoading();
        chattingPhotoGalleryPresenter.loadChattingPhotos(userId, "", false);
    }

    private void initRecyclerGallery() {
        galleryModeFourAdapter =
                new GalleryModeFourAdapter(getApplicationContext(), new ArrayList<ImageItem>());
        galleryModeFourAdapter.setOnItemClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), SPAN_COUNT);
        recyclerGallery.setAdapter(galleryModeFourAdapter);
        recyclerGallery.setLayoutManager(gridLayoutManager);
        GridSpacingItemDecoration mItemDecoration = new GridSpacingItemDecoration(SPAN_COUNT,
                getResources().getDimensionPixelSize(R.dimen.item_offset_mode_four), true);
        recyclerGallery.addItemDecoration(mItemDecoration);
        addScrollToLoadMoreData();

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return galleryModeFourAdapter.isPositionFooter(position) ? SPAN_COUNT : 1;
            }
        });
    }

    private void addScrollToLoadMoreData() {
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerGallery.getLayoutManager();
        recyclerGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int visibleItemCount;
            int totalItemCount;
            int firstVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0
                            && !isLoading && chattingGalleryItem.hasMorePhotos()) {
                        showLoading();
                        isLoading = true;
                        chattingPhotoGalleryPresenter.loadChattingPhotos(userId,
                                chattingGalleryItem.getNextId(), true);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        ImageDetailActivity.startActivity(this, chattingGalleryItem, position,
                ImageDetailActivity.RECEIVED_PHOTOS_FROM_CHAT);
    }

    @Override
    public void didLoadChattingPhotos(ChattingGalleryItem chattingGalleryItem) {
        this.chattingGalleryItem = chattingGalleryItem;
        galleryModeFourAdapter.addAll(chattingGalleryItem.getPhotos());
        disMissLoading();
    }

    @Override
    public void didLoadChattingPhotosError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void didLoadMoreChattingPhotos(ChattingGalleryItem chattingGalleryItem) {
        galleryModeFourAdapter.addAll(chattingGalleryItem.getPhotos());
        updatePhotos(chattingGalleryItem);
        disMissLoading();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bot);
    }

    private void updatePhotos(ChattingGalleryItem chattingGallery) {
        List<ImageItem> tempPhotos = this.chattingGalleryItem.getPhotos();
        this.chattingGalleryItem = chattingGallery;
        tempPhotos.addAll(chattingGallery.getPhotos());
        this.chattingGalleryItem.setImageItems(tempPhotos);
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
}
