package jp.newbees.mastersip.model;

/**
 * Created by thangit14 on 5/10/17.
 */

public class MasterDataItem {
    private int totalChat;
    private int totalFootPrint;
    private int totalFollower;
    private int totalMyMenu;
    private int coin;

    public int getTotalChat() {
        return totalChat;
    }

    public void setTotalChat(int totalChat) {
        this.totalChat = totalChat;
    }

    public int getTotalFootPrint() {
        return totalFootPrint;
    }

    public void setTotalFootPrint(int totalFootPrint) {
        this.totalFootPrint = totalFootPrint;
    }

    public int getTotalFollower() {
        return totalFollower;
    }

    public void setTotalFollower(int totalFollower) {
        this.totalFollower = totalFollower;
    }

    public int getTotalMyMenu() {
        return totalMyMenu;
    }

    public void setTotalMyMenu(int totalMyMenu) {
        this.totalMyMenu = totalMyMenu;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
