package jp.newbees.mastersip.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/21/16.
 */

public class TopPresenter extends BasePresenter {

    private final TopView view;

    public TopPresenter(Context context, TopView topView) {
        super(context);
        this.view = topView;
    }


    public interface TopView {

    }

    public final void requestPermissions() {
        for (Permission permission : Permission.values()) {
            if (ContextCompat.checkSelfPermission(context, permission.getPermission()) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(permission.getPermission(), permission.getResult());
            }
        }
    }

    private void requestPermission(String permission, int result) {
        Logger.e("TopPresenter", "[Permission] Asking for " + permission);
        ActivityCompat.requestPermissions((Activity) view, new String[]{permission}, result);
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public enum Permission {
        CAMERA(201, Manifest.permission.CAMERA), RECORD_AUDIO(202, Manifest.permission.RECORD_AUDIO),
        WRITE_EXTERNAL_STORAGE(203, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        private final int result;
        private final String permission;

        Permission(int result, String permission) {
            this.result = result;
            this.permission = permission;
        }

        public int getResult() {
            return result;
        }

        public String getPermission() {
            return permission;
        }
    }

}
