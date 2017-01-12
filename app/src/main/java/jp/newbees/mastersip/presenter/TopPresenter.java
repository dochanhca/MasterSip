package jp.newbees.mastersip.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.ui.top.TopCenterActivity;
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

    public void didGrantedCameraPermission() {
        requestMicrophonePermission();
    }

    public interface TopView {

    }

    public final void requestPermissions() {
        requestCameraPermission();
    }

    private final void requestCameraPermission() {
        int camera = context.getPackageManager().checkPermission(Manifest.permission.CAMERA, context.getPackageName());
        Logger.e("TopPresenter", "[Permission] Camera permission is " + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (camera != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA, TopCenterActivity.PERMISSIONS_REQUEST_CAMERA);
        } else {
            requestMicrophonePermission();
        }
    }

    private final void requestMicrophonePermission() {
        int recordAudio = context.getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, context.getPackageName());
        Logger.e("TopPresenter", "[Permission] Record audio permission is " + (recordAudio == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.RECORD_AUDIO, TopCenterActivity.PERMISSIONS_ENABLED_MIC);
        }
    }

    private void requestPermission(String permission, int result) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) view, permission)) {
            Logger.e("TopPresenter", "[Permission] Asking for " + permission);
            ActivityCompat.requestPermissions((Activity) view, new String[]{permission}, result);
//        }
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }
}
