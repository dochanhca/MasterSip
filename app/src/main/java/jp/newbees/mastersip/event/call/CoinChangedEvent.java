package jp.newbees.mastersip.event.call;

/**
 * Created by vietbq on 1/12/17.
 */

public class CoinChangedEvent {

    private int coin;

    public CoinChangedEvent(int coin) {
        this.coin = coin;
    }

    public int getCoin() {
        return coin;
    }
}
