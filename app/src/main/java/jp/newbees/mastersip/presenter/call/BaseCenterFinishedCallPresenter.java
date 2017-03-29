package jp.newbees.mastersip.presenter.call;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jp.newbees.mastersip.event.call.AdminHangUpEvent;
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.event.call.HangUpForGirlEvent;
import jp.newbees.mastersip.event.call.RunOutOfCoinEvent;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.presenter.BasePresenter;

/**
 * Created by vietbq on 1/10/17.
 *
 * use for listener incoming call and some common listener
 */

public class BaseCenterFinishedCallPresenter extends BasePresenter {
    private FinishedCallListener finishedCallListener;

    public BaseCenterFinishedCallPresenter(Context context, FinishedCallListener finishedCallListener) {
        super(context);
        this.finishedCallListener = finishedCallListener;
    }

    /**
     * Listen when call ended less than 1 minute
     *
     * @param event
     */
    @Subscribe()
    public void onHangUpForGirlEvent(HangUpForGirlEvent event) {
        finishedCallListener.didCallHangUpForGirl();
    }

    @Subscribe()
    public void onCoinChangedEvent(CoinChangedEvent event) {
        finishedCallListener.didCoinChangedAfterHangUp(event.getTotal(), event.getCoin());
    }

    @Subscribe()
    public void onRunOutOfCoinEvent(RunOutOfCoinEvent event) {
        finishedCallListener.didRunOutOfCoin();
    }

    @Subscribe()
    public void onAdminHangUpEvent(AdminHangUpEvent event) {
        finishedCallListener.didAdminHangUpCall();
    }

    public void registerCallEvent() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterCallEvent() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void didResponseTask(BaseTask task) {

    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {

    }

    public interface FinishedCallListener {

        void didCallHangUpForGirl();

        void didCoinChangedAfterHangUp(int totalCoinChanged, int currentCoin);

        void didRunOutOfCoin();

        void didAdminHangUpCall();
    }
}
