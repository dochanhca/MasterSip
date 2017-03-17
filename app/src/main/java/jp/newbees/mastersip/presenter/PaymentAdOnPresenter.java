package jp.newbees.mastersip.presenter;

import android.content.Context;

import java.util.List;

import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.GetListPaymentAdOnTask;

/**
 * Created by ducpv on 3/16/17.
 */

public class PaymentAdOnPresenter extends BasePresenter {

    private View view;

    public interface View {
        void didGetListPaymentAdOn(List<PaymentAdOnItem> paymentAdOnItems);

        void didGetListPaymentAdOnError(int errorCode, String errorMessage);
    }

    public PaymentAdOnPresenter(Context context, View view) {
        super(context);
        this.view = view;
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof GetListPaymentAdOnTask) {
            view.didGetListPaymentAdOn(((GetListPaymentAdOnTask) task).getDataResponse());
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (task instanceof GetListPaymentAdOnTask) {
            view.didGetListPaymentAdOnError(errorCode, errorMessage);
        }
    }

    public void getListPaymentAdOn() {
        GetListPaymentAdOnTask getListPaymentAdOnTask = new GetListPaymentAdOnTask(context);
        requestToServer(getListPaymentAdOnTask);
    }
}
