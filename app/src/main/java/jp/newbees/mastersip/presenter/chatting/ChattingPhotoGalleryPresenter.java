package jp.newbees.mastersip.presenter.chatting;

import android.content.Context;

import jp.newbees.mastersip.model.ChattingGalleryItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListChattingPhotos;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 2/10/17.
 */

public class ChattingPhotoGalleryPresenter extends BasePresenter {

    private ChattingPhotoGalleryView chattingPhotoGalleryView;
    private boolean isLoadMore = false;

    public interface ChattingPhotoGalleryView {
        void didLoadChattingPhotos(ChattingGalleryItem chattingGalleryItem);

        void didLoadChattingPhotosError(int errorCode, String errorMessage);

        void didLoadMoreChattingPhotos(ChattingGalleryItem chattingGalleryItem);
    }

    public ChattingPhotoGalleryPresenter(Context context, ChattingPhotoGalleryView chattingPhotoGalleryView) {
        super(context);
        this.chattingPhotoGalleryView = chattingPhotoGalleryView;
    }

    public void loadChattingPhotos(String userId, String nextId, boolean isLoadMore) {
        this.isLoadMore = isLoadMore;

        GetListChattingPhotos getListChattingPhotos = new GetListChattingPhotos(context, nextId, userId);
        requestToServer(getListChattingPhotos);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetListChattingPhotos) {
            if (isLoadMore) {
                chattingPhotoGalleryView.didLoadMoreChattingPhotos((ChattingGalleryItem) task.getDataResponse());
            } else {
                chattingPhotoGalleryView.didLoadChattingPhotos((ChattingGalleryItem) task.getDataResponse());
            }
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetListChattingPhotos) {
            chattingPhotoGalleryView.didLoadChattingPhotosError(errorCode, errorMessage);
        }
    }
}
