package jp.newbees.mastersip.presenter.top;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.android.volley.Response;

import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.LogoutTask;
import jp.newbees.mastersip.network.api.MyPhotosTask;
import jp.newbees.mastersip.network.api.MyProfileTask;
import jp.newbees.mastersip.network.api.UploadImageWithProcessTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.FileUtils;

/**
 * Created by vietbq on 1/19/17.
 */

public class MyMenuPresenter extends BasePresenter  {

    private final MyMenuView menuView;
    private Handler handler;

    public MyMenuPresenter(Context context, MyMenuView menuView) {
        super(context);
        this.menuView = menuView;
        this.handler = new Handler();
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof LogoutTask){
            menuView.didLogout();
        }else if(task instanceof MyProfileTask) {
            this.handleMyInfo((MyProfileTask) task);
        }else if(task instanceof MyPhotosTask) {
            this.handleMyPhotos((MyPhotosTask) task);
        }
    }

    private void handleMyPhotos(MyPhotosTask task) {
        GalleryItem galleryItem = task.getDataResponse();
        menuView.didLoadGallery(galleryItem);
    }

    private void handleMyInfo(MyProfileTask task) {
        UserItem userItem =  task.getDataResponse();
        ConfigManager.getInstance().saveUser(userItem);
        menuView.didLoadMyProfile(userItem);
        requestGetGallery();
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof LogoutTask) {
            menuView.didLogout();
        }
    }

    public final void requestMyMenuInfo(){
        MyProfileTask myProfileTask = new MyProfileTask(context);
        requestToServer(myProfileTask);
    }

    private void requestGetGallery() {
        MyPhotosTask myPhotosTask = new MyPhotosTask(context, new GalleryItem(0));
        requestToServer(myPhotosTask);
    }

    /**
     * Do not change this order to logout
     * 1. Stop Linphone Core
     * 2. Request Logout
     * 3. Remove all cached
     */
    public void requestLogout() {
        stopLinphoneService();
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        LogoutTask logoutTask = new LogoutTask(getContext(),userItem);
        requestToServer(logoutTask);
        ConfigManager.getInstance().resetSettings();
    }

    private void stopLinphoneService() {
        Intent intent = new Intent(getContext(), LinphoneService.class);
        getContext().stopService(intent);
    }

    public void uploadAvatar(final String filePath) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        UploadImageWithProcessTask uploadImageWithProcessTask = new UploadImageWithProcessTask(getContext(),
                userItem.getUserId(),
                UploadImageWithProcessTask.UPLOAD_FOR_AVATAR,
                filePath);
        uploadImageWithProcessTask.request(new Response.Listener<ImageItem>() {
            @Override
            public void onResponse(ImageItem response) {
                FileUtils.deleteFilePath(filePath);
                handleDidUploadAvatar(response);
            }
        }, new BaseUploadTask.ErrorListener() {
            @Override
            public void onErrorListener(int errorCode, String errorMessage) {
                menuView.didUploadAvatarFailure(errorMessage);
            }
        }, new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                handleUpdateProgressUploadAvatar(transferredBytes, totalSize);
            }
        });
    }

    private void handleUpdateProgressUploadAvatar(final long transferredBytes,final long totalSize) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                float percent = (transferredBytes*100) / totalSize;
                menuView.onUploadProgressChanged(percent);
            }
        });
    }

    private void handleDidUploadAvatar(ImageItem avatar) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setAvatarItem(avatar);
        ConfigManager.getInstance().saveUser(userItem);
        menuView.didUploadAvatar(avatar);
    }

    public void uploadAvatar(final byte[] result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(
                        result, 0, result.length);
                final String filePath = FileUtils.saveBitmapToFile(bitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        menuView.onStartUploadAvatarBitmap(bitmap);
                        uploadAvatar(filePath);
                    }
                });
            }
        }).start();
    }

    public interface MyMenuView {
        void didLogout();

        void didLoadMyProfile(UserItem userItem);

        void didLoadGallery(GalleryItem galleryItem);

        void didUploadAvatar(ImageItem avatar);

        void onUploadProgressChanged(float percent);

        void didUploadAvatarFailure(String errorMessage);

        void onStartUploadAvatarBitmap(Bitmap bitmap);
    }
}
