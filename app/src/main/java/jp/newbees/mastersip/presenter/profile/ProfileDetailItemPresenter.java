package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListUserPhotos;
import jp.newbees.mastersip.network.api.GetProfileDetailTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailItemPresenter extends BasePresenter {

    private final ProfileDetailItemView view;
    private int nextId = -1;
    private boolean isLoadMore = false;

    public interface ProfileDetailItemView {
        void didGetProfileDetail(UserItem userItem);

        void didGetProfileDetailError(String errorMessage, int errorCode);

        void didGetListPhotos(GalleryItem galleryItem);

        void didLoadMoreListPhotos(GalleryItem galleryItem);

        void didGetListPhotosError(String errorMessage, int errorCode);
    }

    public ProfileDetailItemPresenter(Context context, ProfileDetailItemView view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetProfileDetailTask) {
            UserItem userItem = (UserItem) task.getDataResponse();
            view.didGetProfileDetail(userItem);
        } else if (task instanceof GetListUserPhotos) {
            GalleryItem galleryItem = (GalleryItem) task.getDataResponse();
            if (isLoadMore) {
                view.didLoadMoreListPhotos(galleryItem);
            } else {
                view.didGetListPhotos(galleryItem);
            }
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetProfileDetailTask) {
            view.didGetProfileDetailError(errorMessage, errorCode);
        } else if (task instanceof GetListUserPhotos) {
            view.didGetListPhotosError(errorMessage, errorCode);
        }
    }

    public void getProfileDetail(String userId) {
        GetProfileDetailTask getProfileDetailTask = new GetProfileDetailTask(context, userId);
        requestToServer(getProfileDetailTask);
    }

    public void getListPhotos(String userId) {
        nextId = -1;
        getListPhotos(false, userId);
    }

    private void getListPhotos(boolean isLoadMore, String userId) {
        this.isLoadMore = isLoadMore;

        GetListUserPhotos getListUserPhotos = new
                GetListUserPhotos(context, nextId, userId);
        requestToServer(getListUserPhotos);

    }

    public void loadMoreListPhotos(String userId) {
        getListPhotos(true, userId);
    }

    public final boolean canLoadMoreUser() {
        return (nextId > 0) ? true : false;
    }
}
