package jp.newbees.mastersip.event.call;

/**
 * Created by thangit14 on 5/12/17.
 */

public class CoinChangedEvent {
    private int coin;
    private int total;

    public CoinChangedEvent(int coin) {
        this.coin = coin;
    }

    public CoinChangedEvent(int coin, int total) {
        this.coin = coin;
        this.total = total;
    }

    public int getCoin() {
        return coin;
    }

    public int getTotal() {
        return total;
    }
}
