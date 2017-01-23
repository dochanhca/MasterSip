package jp.newbees.mastersip.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.ui.top.TopActivity;
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
        requestReadExternalStoragePermission();
    }

    public interface TopView {

    }

    public final void requestPermissions() {
        requestCameraPermission();
    }

    private final void requestCameraPermission() {
        int camera = context.getPackageManager().checkPermission(Manifest.permission.CAMERA, context.getPackageName());
        Logger.e("TopPresenter", "[Permission] Camera permission is "
                +(camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (camera != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA, TopActivity.PERMISSIONS_REQUEST_CAMERA);
        } else {
            requestReadExternalStoragePermission();
        }
    }

    private final void requestMicrophonePermission() {
        int recordAudio = context.getPackageManager().checkPermission(Manifest.permission.RECORD_AUDIO, context.getPackageName());
        Logger.e("TopPresenter", "[Permission] Record audio permission is "
                +(recordAudio == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.RECORD_AUDIO, TopActivity.PERMISSIONS_ENABLED_MIC);
        }
    }

    private final void requestWriteExternalStoragePermission() {
        int writeExternalStorage = context.getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName());
        Logger.e("TopPresenter", "[Permission] Write external storage permission is "
                +(writeExternalStorage == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, TopActivity.PERMISSIONS_REQUEST_CAMERA);
        } else {
            requestMicrophonePermission();
        }

    }

    private final void requestReadExternalStoragePermission() {
        int readExternalStorage = context.getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, context.getPackageName());
        Logger.e("TopPresenter", "[Permission] Read external storage permission is "
                +(readExternalStorage == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (readExternalStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, TopActivity.PERMISSIONS_REQUEST_CAMERA);
        } else {
            requestWriteExternalStoragePermission();
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
}
