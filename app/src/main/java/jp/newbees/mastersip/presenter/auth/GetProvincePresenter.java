package jp.newbees.mastersip.presenter.auth;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetProvinceTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by ducpv on 12/20/16.
 */

public class GetProvincePresenter extends BasePresenter {

    public interface View {

         void onGetProvinceSuccess(SelectionItem selectionItem);

         void onGetProvinceFailure(int errorCode, String errorMessage);
    }

    private final Context context;
    private final View view;

    public GetProvincePresenter(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public final void getProvince(LatLng latLng) {
        GetProvinceTask getProvinceTask = new GetProvinceTask(context, latLng);
        requestToServer(getProvinceTask);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetProvinceTask) {
            SelectionItem selectionItem = (SelectionItem) task.getDataResponse();
            view.onGetProvinceSuccess(selectionItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        view.onGetProvinceFailure(errorCode,errorMessage);
    }
}
