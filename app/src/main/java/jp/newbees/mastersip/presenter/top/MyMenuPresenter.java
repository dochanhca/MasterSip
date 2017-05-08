package jp.newbees.mastersip.presenter.top;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.android.volley.Response;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.DeleteImageTask;
import jp.newbees.mastersip.network.api.GetMyPhotosTask;
import jp.newbees.mastersip.network.api.LogoutTask;
import jp.newbees.mastersip.network.api.MyProfileTask;
import jp.newbees.mastersip.network.api.UploadImageWithProcessTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/19/17.
 */

public class MyMenuPresenter extends BasePresenter {

    private final MyMenuView menuView;
    private Handler handler;
    private GalleryItem lastGalleryItem;
    private boolean isLoadMorePhotoInGallery;
    private boolean isRequestingMyInfo;
    private int uploadType;

    public MyMenuPresenter(Context context, MyMenuView menuView) {
        super(context);
        this.menuView = menuView;
        this.handler = new Handler();
        this.isLoadMorePhotoInGallery = false;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof LogoutTask) {
            menuView.didLogout();
        } else if (task instanceof MyProfileTask) {
            this.handleMyInfo((MyProfileTask) task);
        } else if (task instanceof GetMyPhotosTask) {
            this.handleMyPhotos((GetMyPhotosTask) task);
        } else if (task instanceof DeleteImageTask) {
            this.handleDeleteAvatar();
        }
    }

    private void handleDeleteAvatar() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setAvatarItem(null);
        ConfigManager.getInstance().saveUser(userItem);
        menuView.didDeleteAvatar();
    }

    private void handleMyPhotos(GetMyPhotosTask task) {
        this.lastGalleryItem = task.getDataResponse();
        if (isLoadMorePhotoInGallery) {
            menuView.didLoadMorePhotosInGallery(lastGalleryItem);
        } else {
            menuView.didLoadGallery(lastGalleryItem);
        }
    }

    private void handleMyInfo(MyProfileTask task) {
        UserItem userItem = task.getDataResponse();
        ConfigManager.getInstance().saveUser(userItem);
        menuView.didLoadMyProfile(userItem);
        isRequestingMyInfo = false;
        isLoadMorePhotoInGallery = false;
        lastGalleryItem = null;
        requestGetGallery(new GalleryItem());
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof LogoutTask) {
            menuView.didLogoutError(errorCode, errorMessage);
        } else if (task instanceof DeleteImageTask) {
            menuView.didDeleteAvatarFailure(errorCode, errorMessage);
        }
    }

    public final void requestMyMenuInfo() {
        if (!isRequestingMyInfo) {
            isRequestingMyInfo = true;
            MyProfileTask myProfileTask = new MyProfileTask(context);
            requestToServer(myProfileTask);
        }
    }

    private void requestGetGallery(GalleryItem galleryItem) {
        GetMyPhotosTask getMyPhotosTask = new GetMyPhotosTask(context, galleryItem);
        requestToServer(getMyPhotosTask);
    }

    /**
     * Do not change this order to logout
     * 1. Stop Linphone Core
     * 2. Request Logout
     * 3. Remove all cached
     */
    public void requestLogout() {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        LogoutTask logoutTask = new LogoutTask(getContext(), userItem);
        requestToServer(logoutTask);
    }

    public void stopLinphoneService() {
        Intent intent = new Intent(getContext(), LinphoneService.class);
        getContext().stopService(intent);
    }

    public void uploadPhoto(final String filePath, final int uploadType) {
        this.uploadType = uploadType;

        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        UploadImageWithProcessTask uploadImageWithProcessTask = new UploadImageWithProcessTask(getContext(),
                userItem.getUserId(),
                uploadType,
                filePath);
        uploadImageWithProcessTask.request(new Response.Listener<ImageItem>() {
            @Override
            public void onResponse(ImageItem response) {
                if (response.getImageType() == UploadImageWithProcessTask.UPLOAD_FOR_AVATAR) {
                    handleDidUploadAvatar(response);
                } else if (response.getImageType() == UploadImageWithProcessTask.UPLOAD_FOR_GALLERY) {
                    handleDidUploadPhotoForGallery(response);
                }
            }
        }, new BaseUploadTask.ErrorListener() {
            @Override
            public void onErrorListener(int errorCode, String errorMessage) {
                if (uploadType == UploadImageWithProcessTask.UPLOAD_FOR_AVATAR){
                    menuView.didUploadAvatarFailure(errorCode, errorMessage);
                } else {
                    menuView.didUpLoadPhotoGalleryFailure(errorCode, errorMessage);
                }
            }
        }, new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                handleUpdateProgressUploadImage(transferredBytes, totalSize, uploadType);
            }
        });
    }

    private void handleDidUploadPhotoForGallery(ImageItem photo) {
        menuView.didUploadPhotoGallery(photo);
    }

    private void handleUpdateProgressUploadImage(final long transferredBytes, final long totalSize, final int uploadType) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                float percent = (transferredBytes * 100) / totalSize;
                if (uploadType == UploadImageWithProcessTask.UPLOAD_FOR_AVATAR) {
                    menuView.onUploadAvatarProgressChanged(percent);
                } else if (uploadType == UploadImageWithProcessTask.UPLOAD_FOR_GALLERY) {
                    menuView.onUploadGalleryProgressChanged(percent);
                }
            }
        });
    }

    private void handleDidUploadAvatar(ImageItem avatar) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setAvatarItem(avatar);
        ConfigManager.getInstance().saveUser(userItem);
        menuView.didUploadAvatar(avatar);
    }

    public void uploadAvatar(final String imagePath) {
        menuView.onStartUploadAvatarBitmap(imagePath);
        uploadPhoto(imagePath, UploadImageWithProcessTask.UPLOAD_FOR_AVATAR);
    }

    public void deleteAvatar() {
        UserItem userItem = getCurrentUserItem();
        DeleteImageTask deleteImageTask = new DeleteImageTask(getContext(), userItem, userItem.getAvatarItem());
        requestToServer(deleteImageTask);
    }

    public void uploadPhotoForGallery(final String imagePath) {
        menuView.onStartUploadPhotoGallery(imagePath);
        uploadPhoto(imagePath, UploadImageWithProcessTask.UPLOAD_FOR_GALLERY);
    }

    public void loadMorePhotoInGallery() {
        if (null != lastGalleryItem && lastGalleryItem.hasMorePhotos()) {
            isLoadMorePhotoInGallery = true;
            requestGetGallery(lastGalleryItem);
        } else {
            menuView.photoNoMoreInGallery();
        }
    }

    public String getVersion() {
        String version = context.getResources().getString(R.string.version) + " ";
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version += pInfo.versionName;
            version += "(" + pInfo.versionCode + ") \n";
            version += context.getResources().getString(R.string.domain) + " ";
            version += Constant.API.BASE_URL;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface MyMenuView {
        void didLogout();

        void didLogoutError(int errorCode, String errorMessage);

        void didLoadMyProfile(UserItem userItem);

        void didLoadGallery(GalleryItem galleryItem);

        void didUploadAvatar(ImageItem avatar);

        void onUploadAvatarProgressChanged(float percent);

        void didUploadAvatarFailure(int errorCode, String errorMessage);

        void didUpLoadPhotoGalleryFailure(int code, String errorMessage);

        void onStartUploadAvatarBitmap(String filePath);

        void didDeleteAvatar();

        void didDeleteAvatarFailure(int errorCode, String errorMessage);

        void didUploadPhotoGallery(ImageItem photo);

        void onStartUploadPhotoGallery(String filePath);

        void onUploadGalleryProgressChanged(float percent);

        void photoNoMoreInGallery();

        void didLoadMorePhotosInGallery(GalleryItem gallery);
    }
}
