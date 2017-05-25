package jp.newbees.mastersip.presenter.profile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import jp.newbees.mastersip.event.ReLoadProfileEvent;
import jp.newbees.mastersip.event.SettingOnlineChangedEvent;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BlockUserTask;
import jp.newbees.mastersip.network.api.FollowUserTask;
import jp.newbees.mastersip.network.api.GetListReportReasonTask;
import jp.newbees.mastersip.network.api.GetListUserPhotos;
import jp.newbees.mastersip.network.api.GetProfileDetailTask;
import jp.newbees.mastersip.network.api.ReportTask;
import jp.newbees.mastersip.network.api.SendFootPrintTask;
import jp.newbees.mastersip.network.api.UnFollowUserTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

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

        void didFollowUser();

        void didFollowUserError(String errorMessage, int errorCode);

        void didUnFollowUser();

        void didUnFollowUserError(String errorMessage, int errorCode);

        void didEditProfileImage();

        void didSettingOnlineChanged(boolean isFollowing, String userId);

        void didGetListReportReason(List<SelectionItem> reportReasons);

        void didGetListReportReasonError(int errorCode, String errorMessage);

        void didReportUser();

        void didReportUserError(int errorCode, String errorMessage);

        void didBlockUser();

        void didBlockUserError(int errorCode, String errorMessage);
    }

    public ProfileDetailItemPresenter(BaseActivity context, ProfileDetailItemView view) {
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
        } else if (task instanceof GetListReportReasonTask) {
            view.didGetListReportReason(((GetListReportReasonTask) task).getDataResponse());
        } else if (task instanceof ReportTask) {
            view.didReportUser();
        } else if (task instanceof BlockUserTask) {
            view.didBlockUser();
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
        } else if (task instanceof GetListReportReasonTask) {
            view.didGetListReportReasonError(errorCode, errorMessage);
        } else if (task instanceof ReportTask) {
            view.didReportUserError(errorCode, errorMessage);
        } else if (task instanceof BlockUserTask) {
            view.didBlockUserError(errorCode, errorMessage);
        }
    }

    @Subscribe(sticky = true)
    public void onReloadProfileEvent(ReLoadProfileEvent event) {
        Logger.e(tag, "" + event.isNeedReload());
        view.didEditProfileImage();
    }

    @Subscribe(sticky = true)
    public void onSettingOnlineChangedEvent(SettingOnlineChangedEvent event) {
        view.didSettingOnlineChanged(event.isFollowing(), event.getUserId());
    }

    public final void registerEvent() {
        EventBus.getDefault().register(this);
    }

    public final void unRegisterEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void getProfileDetail(String userId) {
        GetProfileDetailTask getProfileDetailTask = new GetProfileDetailTask(context, userId);
        requestToServer(getProfileDetailTask);
    }

    public void getListPhotos(String userId) {
        nextId = -1;
        getListPhotos(false, userId);
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

    public void sendFootPrintToServer(String userId) {
        SendFootPrintTask sendFootPrintTask = new SendFootPrintTask(context, userId);
        requestToServer(sendFootPrintTask);
    }

    public void blockUser(String userId) {
        BlockUserTask blockUserTask = new BlockUserTask(getContext(), userId);
        requestToServer(blockUserTask);
    }

    public void getListReportReason() {
        GetListReportReasonTask getListReportReasonTask = new GetListReportReasonTask(getContext(),
                Constant.API.REPORT_PROFILE);
        requestToServer(getListReportReasonTask);
    }

    public void reportUser(String userId, int reportDetailId) {
        ReportTask reportTask = new ReportTask(getContext(), userId, Constant.API.REPORT_PROFILE,
                reportDetailId);
        requestToServer(reportTask);
    }

    private void getNextIdForLoadMore(GalleryItem galleryItem) {
        if (!"".equals(galleryItem.getNextId())) {
            nextId = Integer.parseInt(galleryItem.getNextId());
        } else {
            nextId = -1;
        }
    }

    private void getListPhotos(boolean isLoadMore, String userId) {
        this.isLoadMore = isLoadMore;

        GetListUserPhotos getListUserPhotos = new
                GetListUserPhotos(context, nextId, userId);
        requestToServer(getListUserPhotos);

    }
}
