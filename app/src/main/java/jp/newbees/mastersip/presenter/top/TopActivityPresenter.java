package jp.newbees.mastersip.presenter.top;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import jp.newbees.mastersip.model.MasterDataItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.LoadMasterDataTask;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 5/11/17.
 */

public class TopActivityPresenter extends BasePresenter {
    private TopActivityListener listener;

    public TopActivityPresenter(Context context, TopActivityListener listener) {
        super(context);
        this.listener = listener;
    }

    public void loadMasterData() {
        LoadMasterDataTask task = new LoadMasterDataTask(getContext());
        requestToServer(task);
    }

    public interface TopActivityListener {
        void onLoadMasterDataSuccess(MasterDataItem masterDataItem);

        void onLoadMasterDataError(int errorCode, String errorMessage);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof LoadMasterDataTask) {
            MasterDataItem item = ((LoadMasterDataTask) task).getDataResponse();
            listener.onLoadMasterDataSuccess(item);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof LoadMasterDataTask) {
            listener.onLoadMasterDataError(errorCode, errorMessage);
        }
    }

    public final void requestPermissions() {
        for (Permission permission : Permission.values()) {
            if (ContextCompat.checkSelfPermission(context, permission.getPermission()) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(permission.getPermission(), permission.getResult());
                return;
            }
        }
    }

    private void requestPermission(String permission, int result) {
        Logger.e("InAppPurchasePresenter", "[Permission] Asking for " + permission);
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, result);
    }

    private BaseActivity getActivity() {
        return (BaseActivity) getContext();

    }

    public void saveCoin(int coin) {
        ConfigManager.getInstance().setCoin(coin);
    }

    public enum Permission {
        CAMERA(201, Manifest.permission.CAMERA),
        RECORD_AUDIO(202, Manifest.permission.RECORD_AUDIO),
        WRITE_EXTERNAL_STORAGE(203, Manifest.permission.WRITE_EXTERNAL_STORAGE),
        GET_ACCOUNTS(204, Manifest.permission.GET_ACCOUNTS),
        READ_PHONE_STATE(205, Manifest.permission.READ_PHONE_STATE);

        private final int result;
        private final String permissionName;

        Permission(int result, String permissionName) {
            this.result = result;
            this.permissionName = permissionName;
        }

        public int getResult() {
            return result;
        }

        public String getPermission() {
            return permissionName;
        }
    }
}
