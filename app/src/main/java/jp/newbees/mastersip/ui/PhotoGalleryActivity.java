package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.GalleryModeFourAdapter;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.utils.GridSpacingItemDecoration;

/**
 * Created by ducpv on 2/7/17.
 */

public class PhotoGalleryActivity extends CallCenterActivity implements GalleryModeFourAdapter.OnItemClickListener {

    private static final String LIST_PHOTO = "LIST_PHOTO";
    private static final int SPAN_COUNT = 4;

    private RecyclerView recyclerGallery;
    private TextView txtNumberPhoto;

    @Override
    protected int layoutId() {
        return R.layout.activity_photo_gallery;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.photo_gallery));
        recyclerGallery = (RecyclerView) findViewById(R.id.recycler_gallery);
        txtNumberPhoto = (TextView) findViewById(R.id.txt_number_photo);

        List<ImageItem> photos = getIntent().getParcelableArrayListExtra(LIST_PHOTO);
        GalleryModeFourAdapter galleryModeFourAdapter = new GalleryModeFourAdapter(getApplicationContext(), photos);
        galleryModeFourAdapter.setOnItemClickListener(this);
        recyclerGallery.setAdapter(galleryModeFourAdapter);
        GridSpacingItemDecoration mItemDecoration = new GridSpacingItemDecoration(SPAN_COUNT,
                getResources().getDimensionPixelSize(R.dimen.item_offset_mode_four), true);
        recyclerGallery.addItemDecoration(mItemDecoration);

        StringBuilder numberPhoto = new StringBuilder();
        numberPhoto.append(photos.size()).append(getString(R.string.photos));
        txtNumberPhoto.setText(numberPhoto);
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

    /**
     *
     * @param activity
     * @param photos
     * @param requestCode
     */
    public static void startActivityForResult(Activity activity, List<ImageItem> photos,
                                              int requestCode) {
        Intent intent = new Intent(activity, PhotoGalleryActivity.class);
        intent.putParcelableArrayListExtra(LIST_PHOTO, (ArrayList<? extends Parcelable>) photos);
        activity.startActivityForResult(intent, requestCode);
    }
}
