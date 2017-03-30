package jp.newbees.mastersip.presenter.top;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import jp.newbees.mastersip.model.FootprintItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.ListFootprintViewedByMe;
import jp.newbees.mastersip.network.api.ListFootprintViewedByOther;
import jp.newbees.mastersip.presenter.BasePresenter;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 3/28/17.
 */

public class FootprintPresenter extends BasePresenter {
    private final FootprintPresenterView view;

    public interface FootprintPresenterView {
        void didLoadListFootprint(ArrayList<FootprintItem> data, int totalFootprint);
        void didLoadDataError(int errorCode, String errorMessage);
    }

    public FootprintPresenter(Context context, FootprintPresenterView view) {
        super(context);
        this.view = view;
    }

    final public void getListFootprintViewedByOther() {
        ListFootprintViewedByOther task = new ListFootprintViewedByOther(getContext());
        this.requestToServer(task);
    }

    final public void getListFootprintViewedByMe() {
        ListFootprintViewedByMe task = new ListFootprintViewedByMe(getContext());
        this.requestToServer(task);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        Map<String, Object> data;

        if (task instanceof ListFootprintViewedByOther) {
            data = ((ListFootprintViewedByOther) task).getDataResponse();
        }else {
            data = ((ListFootprintViewedByMe) task).getDataResponse();
        }
            ArrayList<FootprintItem> listFootprint = (ArrayList<FootprintItem>) data.get(Constant.JSON.LIST);
            int totalFootprint = (int) data.get(Constant.JSON.TOTAL);
            this.view.didLoadListFootprint(listFootprint, totalFootprint);
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        this.view.didLoadDataError(errorCode, errorMessage);
    }
}
