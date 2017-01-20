package jp.newbees.mastersip.presenter.top;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.android.volley.Response;

import java.io.InputStream;

import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.GalleryItem;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.BaseUploadTask;
import jp.newbees.mastersip.network.api.LogoutTask;
import jp.newbees.mastersip.network.api.MyPhotosTask;
import jp.newbees.mastersip.network.api.MyProfileTask;
import jp.newbees.mastersip.network.api.UploadImageTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.ImageUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 1/19/17.
 */

public class MyMenuPresenter extends BasePresenter implements Response.Listener<ImageItem>,BaseUploadTask.ErrorListener {

    private final MyMenuView menuView;

    public MyMenuPresenter(Context context, MyMenuView menuView) {
        super(context);
        this.menuView = menuView;
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

    public void uploadAvatar(Bitmap bmAvatar) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        InputStream inputStream = ImageUtils.convertToInputStream(bmAvatar);
        UploadImageTask uploadImageTask = new UploadImageTask(getContext(),userItem.getUserId(),UploadImageTask.UPLOAD_FOR_AVATAR,inputStream);
        uploadImageTask.request(this, this);
    }

    @Override
    public void onResponse(ImageItem avatar) {
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        userItem.setAvatarItem(avatar);
        ConfigManager.getInstance().saveUser(userItem);
        menuView.didUploadAvatar(avatar);
    }

    @Override
    public void onErrorListener(int errorCode, String errorMessage) {
        Logger.e(TAG,errorMessage);
    }

    public interface MyMenuView {
        void didLogout();

        void didLoadMyProfile(UserItem userItem);

        void didLoadGallery(GalleryItem galleryItem);

        void didUploadAvatar(ImageItem avatar);

        void onUploadProgressChanged(float percent);
    }
}
