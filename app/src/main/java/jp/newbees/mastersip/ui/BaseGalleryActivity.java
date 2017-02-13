package jp.newbees.mastersip.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryModeFourAdapter;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.utils.GridSpacingItemDecoration;

/**
 * Created by thangit14 on 2/13/17.
 */

public abstract class BaseGalleryActivity extends CallCenterActivity implements GalleryModeFourAdapter.OnItemClickListener {

    protected static final int SPAN_COUNT = 4;

    protected RecyclerView recyclerGallery;
    protected GalleryItem galleryItem;
    protected boolean isLoading = false;
    protected GalleryModeFourAdapter galleryModeFourAdapter;

    @Override
    protected int layoutId() {
        return R.layout.activity_photo_gallery;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.photo_gallery));
        recyclerGallery = (RecyclerView) findViewById(R.id.recycler_gallery);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        init();
        initRecyclerGallery();
    }

    private void initRecyclerGallery() {
        if (galleryItem == null) {
            galleryItem = new GalleryItem();
            galleryItem.setImageItems(new ArrayList<ImageItem>());
        }
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
                        loadMore();
                    }
                }
            }
        });
    }

    protected abstract void loadMore();

    protected abstract void init();

}
