package jp.newbees.mastersip.presenter;

import android.content.Context;

import java.util.List;
import java.util.Map;

import jp.newbees.mastersip.model.CallLogItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetIncomingCallLogsTask;
import jp.newbees.mastersip.network.api.GetOutgoingCallLogsTask;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 4/18/17.
 */

public class HistoryCallPresenter extends BasePresenter {

    public interface HistoryCallPresenterView {
        void didLoadCallLogs(List<CallLogItem> data, int totalCallLog);
        void didLoadDataError(int errorCode, String errorMessage);
    }

    private HistoryCallPresenterView historyCallPresenterView;

    public HistoryCallPresenter(Context context, HistoryCallPresenterView historyCallPresenterView) {
        super(context);
        this.historyCallPresenterView = historyCallPresenterView;
    }

    final public void getIncomingCallLogs() {
        GetIncomingCallLogsTask task = new GetIncomingCallLogsTask(getContext());
        this.requestToServer(task);
    }

    final public void getOutgoingCallLogs() {
        GetOutgoingCallLogsTask task = new GetOutgoingCallLogsTask(getContext());
        this.requestToServer(task);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        Map<String, Object> data = (Map<String, Object>) task.getDataResponse();
        int total = (int) data.get(Constant.JSON.TOTAL);
        List<CallLogItem> list = (List<CallLogItem>) data.get(Constant.JSON.LIST);
        historyCallPresenterView.didLoadCallLogs(list, total);
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        historyCallPresenterView.didLoadDataError(errorCode, errorMessage);
    }

}
