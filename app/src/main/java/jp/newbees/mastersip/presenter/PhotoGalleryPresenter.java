package jp.newbees.mastersip.presenter;

import android.content.Context;

import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.MyPhotosTask;

/**
 * Created by ducpv on 2/9/17.
 */

public class PhotoGalleryPresenter extends BasePresenter {

    private PhotoGalleryView photoGalleryView;

    public interface PhotoGalleryView {
        void didLoadMorePhotos(GalleryItem galleryItem);

        void didLoadMorePhotosError(int errorCode, String errorMessage);
    }

    public PhotoGalleryPresenter(Context context, PhotoGalleryView view) {
        super(context);
        this.photoGalleryView = view;
    }

    public void loadMorePhotos(GalleryItem galleryItem) {
        MyPhotosTask myPhotosTask = new MyPhotosTask(context, galleryItem);
        requestToServer(myPhotosTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof MyPhotosTask) {
            GalleryItem galleryItem = ((MyPhotosTask) task).getDataResponse();
            photoGalleryView.didLoadMorePhotos(galleryItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof MyPhotosTask) {
            photoGalleryView.didLoadMorePhotosError(errorCode, errorMessage);
        }
    }
}
