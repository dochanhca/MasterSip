package jp.newbees.mastersip.presenter;

import android.content.Context;
import android.os.Handler;

import com.android.volley.Response;

import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.DeleteImageTask;
import jp.newbees.mastersip.network.api.GetListChattingPhotos;
import jp.newbees.mastersip.network.api.GetListUserPhotos;
import jp.newbees.mastersip.network.api.GetMyPhotosTask;
import jp.newbees.mastersip.network.api.UpdateImageTask;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.FileUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 2/8/17.
 */

public class ImageDetailPresenter extends BasePresenter {

    private PhotoDetailView view;
    private Handler handler;

    public interface PhotoDetailView {
        void didUpdateImage(ImageItem imageItem);

        void didUpdateImageError(int errorCode, String errorMessage);

        void onUpdateImageProgressChanged(float percent);

        void onStartUploadPhotoGallery(String filePath);

        void didDeleteImage();

        void didDeleteImageError(int errorCode, String errorMessage);

        void didLoadMorePhotos(GalleryItem galleryItem);

        void didLoadMorePhotosError(int errorCode, String errorMessage);

        void didLoadMoreChattingPhotos(ChattingGalleryItem chattingGalleryItem);

        void didLoadMoreOtherUserPhotos(GalleryItem galleryItem);
    }

    public ImageDetailPresenter(Context context, PhotoDetailView view) {
        super(context);
        this.view = view;
        this.handler = new Handler();
    }

    public void deleteImage(ImageItem imageItem) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        DeleteImageTask deleteImageTask = new DeleteImageTask(context, userItem, imageItem);
        requestToServer(deleteImageTask);
    }

    public void uploadPhotoForGallery(final ImageItem imageItem, final byte[] result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                String fileName = "android_" + System.currentTimeMillis() + ".png";
                final String filePath = FileUtils.saveImageBytesToFile(result, fileName);
                long end = System.currentTimeMillis() - start;
                Logger.e("Update photo", "time : " + end);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.onStartUploadPhotoGallery(filePath);
                        updateImage(imageItem, filePath);
                    }
                });
            }
        }).start();
    }

    public void loadMorePhotos(GalleryItem galleryItem) {
        GetMyPhotosTask getMyPhotosTask = new GetMyPhotosTask(context, galleryItem);
        requestToServer(getMyPhotosTask);
    }

    public void loadMoreChattingPhotos(ChattingGalleryItem chattingGalleryItem) {
        String userID = chattingGalleryItem.getSender().getUserId();
        GetListChattingPhotos getListChattingPhotos = new GetListChattingPhotos(context,
                chattingGalleryItem.getNextId(), userID);
        requestToServer(getListChattingPhotos);
    }

    public void loadMoreOtherUserPhotos(int nextId, String userId) {
        GetListUserPhotos getListUserPhotos = new GetListUserPhotos(context, nextId,
                userId);
        requestToServer(getListUserPhotos);
    }

    private void updateImage(ImageItem imageItem, String imagePath) {
        int imageId = imageItem.getImageId();
        String userId = ConfigManager.getInstance().getCurrentUser().getUserId();

        UpdateImageTask updateImageTask = new UpdateImageTask(context, imageId, userId, imagePath);
        updateImageTask.request(new Response.Listener<ImageItem>() {
            @Override
            public void onResponse(ImageItem response) {
                view.didUpdateImage(response);
            }
        }, new BaseUploadTask.ErrorListener() {
            @Override
            public void onErrorListener(int errorCode, String errorMessage) {
                view.didUpdateImageError(errorCode, errorMessage);
            }
        }, new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                handleUpdateProgressUpdateImage(transferredBytes, totalSize);
            }
        });
    }

    private void handleUpdateProgressUpdateImage(final long transferredBytes, final long totalSize) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                float percent = (float) (((double) transferredBytes * 100) / totalSize);
                view.onUpdateImageProgressChanged(percent);
            }
        });
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof DeleteImageTask) {
            view.didDeleteImage();
        } else if (task instanceof GetMyPhotosTask) {
            GalleryItem galleryItem = ((GetMyPhotosTask) task).getDataResponse();
            view.didLoadMorePhotos(galleryItem);
        } else if (task instanceof GetListChattingPhotos) {
            view.didLoadMoreChattingPhotos(((GetListChattingPhotos) task).getDataResponse());
        } else if (task instanceof GetListUserPhotos) {
            view.didLoadMoreOtherUserPhotos(((GetListUserPhotos) task).getDataResponse());
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof DeleteImageTask) {
            view.didDeleteImageError(errorCode, errorMessage);
        } else if (task instanceof GetMyPhotosTask || task instanceof GetListChattingPhotos
                || task instanceof GetListUserPhotos) {
            view.didLoadMorePhotosError(errorCode, errorMessage);
        }
    }
}
