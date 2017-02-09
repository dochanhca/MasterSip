package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryModeFourAdapter;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.presenter.PhotoGalleryPresenter;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.utils.GridSpacingItemDecoration;

/**
 * Created by ducpv on 2/7/17.
 */

public class PhotoGalleryActivity extends CallCenterActivity implements GalleryModeFourAdapter.OnItemClickListener,
PhotoGalleryPresenter.PhotoGalleryView {

    private static final int SPAN_COUNT = 4;
    private static final String GALLERY_ITEM = "GALLERY_ITEM";

    private RecyclerView recyclerGallery;
    private GalleryItem galleryItem;
    private boolean isLoading = false;
    private GalleryModeFourAdapter galleryModeFourAdapter;

    private PhotoGalleryPresenter photoGalleryPresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_photo_gallery;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        photoGalleryPresenter = new PhotoGalleryPresenter(getApplicationContext(), this);
        initHeader(getString(R.string.photo_gallery));
        recyclerGallery = (RecyclerView) findViewById(R.id.recycler_gallery);

        galleryItem = getIntent().getParcelableExtra(GALLERY_ITEM);

        initRecyclerGallery();
    }

    private void initRecyclerGallery() {
        galleryModeFourAdapter =
                new GalleryModeFourAdapter(getApplicationContext(), galleryItem.getPhotos());
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
                            && !isLoading && galleryItem.hasMorePhotos()) {
                        showLoading();
                        isLoading = true;
                        photoGalleryPresenter.loadMorePhotos(galleryItem);
                    }
                }
            }
        });
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        //TODO
    }

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
}
