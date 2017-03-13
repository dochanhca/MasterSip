package jp.newbees.mastersip.event.call;

/**
 * Created by ducpv on 3/13/17.
 */

public class RunOutOfCoinEvent {

    private int coin;
    private int total;

    public RunOutOfCoinEvent() {
    }

    public RunOutOfCoinEvent(int coin, int total) {
        this.coin = coin;
        this.total = total;
    }
}
