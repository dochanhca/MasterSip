package jp.newbees.mastersip.presenter.profile;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;

import jp.newbees.mastersip.event.ReLoadProfileEvent;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.CheckCallTask;
import jp.newbees.mastersip.network.api.FollowUserTask;
import jp.newbees.mastersip.network.api.GetListUserPhotos;
import jp.newbees.mastersip.network.api.GetProfileDetailTask;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.network.api.UnFollowUserTask;
import jp.newbees.mastersip.presenter.call.BaseActionCallPresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/18/17.
 */

public class ProfileDetailPresenter extends BaseActionCallPresenter {

    private final ProfileDetailItemView view;
    private int nextId = -1;
    private boolean isLoadMore = false;

    public interface ProfileDetailItemView {
        void didGetProfileDetail(UserItem userItem);

        void didGetProfileDetailError(String errorMessage, int errorCode);

        void didGetListPhotos(GalleryItem galleryItem);

        void didLoadMoreListPhotos(GalleryItem galleryItem);

        void didGetListPhotosError(String errorMessage, int errorCode);

        void didFollowUser();

        void didFollowUserError(String errorMessage, int errorCode);

        void didUnFollowUser();

        void didUnFollowUserError(String errorMessage, int errorCode);

        void didSendMsgRequestEnableSettingCall(SendMessageRequestEnableCallTask.Type type);

        void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode);

        void didCheckCallError(String errorMessage, int errorCode);

        void didCalleeRejectCall(String calleeExtension);

        void didEditProfileImage();
    }

    public ProfileDetailPresenter(Context context, ProfileDetailItemView view) {
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
            getNextIdForLoadMore(galleryItem);
            if (isLoadMore) {
                view.didLoadMoreListPhotos(galleryItem);
            } else {
                view.didGetListPhotos(galleryItem);
            }
        } else if (task instanceof FollowUserTask) {
            view.didFollowUser();
        } else if (task instanceof UnFollowUserTask) {
            view.didUnFollowUser();
        } else if (task instanceof SendMessageRequestEnableCallTask) {
            SendMessageRequestEnableCallTask.Type type = ((SendMessageRequestEnableCallTask) task).getDataResponse();
            view.didSendMsgRequestEnableSettingCall(type);
        } else if (task instanceof CheckCallTask) {
            handleResponseCheckCall(task);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetProfileDetailTask) {
            view.didGetProfileDetailError(errorMessage, errorCode);
        } else if (task instanceof GetListUserPhotos) {
            view.didGetListPhotosError(errorMessage, errorCode);
        } else if (task instanceof FollowUserTask) {
            view.didFollowUserError(errorMessage, errorCode);
        } else if (task instanceof UnFollowUserTask) {
            view.didUnFollowUserError(errorMessage, errorCode);
        } else if (task instanceof SendMessageRequestEnableCallTask) {
            view.didSendMsgRequestEnableSettingCallError(errorMessage, errorCode);
        } else if (task instanceof CheckCallTask) {
            view.didCheckCallError(errorMessage, errorCode);
        }
    }

    @Override
    protected void onCalleeRejectCall(BusyCallEvent busyCallEvent) {
        String calleeExtension = ConfigManager.getInstance().getCurrentCallee(busyCallEvent.getCallId())
                .getSipItem().getExtension();
        view.didCalleeRejectCall(calleeExtension);
    }

    @Subscribe(sticky =  true)
    public void onReloadProfileEvent(ReLoadProfileEvent event) {
        Logger.e(TAG, "" + event.isNeedReload());
        view.didEditProfileImage();
    }

    private void getNextIdForLoadMore(GalleryItem galleryItem) {
        if (!"".equals(galleryItem.getNextId())) {
            nextId = Integer.parseInt(galleryItem.getNextId());
        } else {
            nextId = -1;
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

    public void followUser(String destUserId) {
        FollowUserTask followUserTask = new FollowUserTask(context, destUserId);
        requestToServer(followUserTask);
    }

    public void unFollowUser(String destUserId) {
        UnFollowUserTask unFollowUserTask = new UnFollowUserTask(context, destUserId);
        requestToServer(unFollowUserTask);
    }
}
