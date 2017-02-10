package jp.newbees.mastersip.presenter;

import android.content.Context;

import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetMyPhotosTask;

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
        GetMyPhotosTask getMyPhotosTask = new GetMyPhotosTask(context, galleryItem);
        requestToServer(getMyPhotosTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetMyPhotosTask) {
            GalleryItem galleryItem = ((GetMyPhotosTask) task).getDataResponse();
            photoGalleryView.didLoadMorePhotos(galleryItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetMyPhotosTask) {
            photoGalleryView.didLoadMorePhotosError(errorCode, errorMessage);
        }
    }
}
