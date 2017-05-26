package jp.newbees.mastersip.model;

/**
 * Created by thangit14 on 5/10/17.
 */

public class MasterDataItem {
    private int totalChat;
    private int totalFootPrint;
    private int totalFollower;
    private String totalMyMenu;
    private int coin;
    private int minPointDownImageChat;
    private int getMinPointDownImageGallery;

    public int getMinPointDownImageChat() {
        return minPointDownImageChat;
    }

    public void setMinPointDownImageChat(int minPointDownImageChat) {
        this.minPointDownImageChat = minPointDownImageChat;
    }

    public int getGetMinPointDownImageGallery() {
        return getMinPointDownImageGallery;
    }

    public void setGetMinPointDownImageGallery(int getMinPointDownImageGallery) {
        this.getMinPointDownImageGallery = getMinPointDownImageGallery;
    }

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

    public String getTotalMyMenu() {
        return totalMyMenu;
    }

    public void setTotalMyMenu(String totalMyMenu) {
        this.totalMyMenu = totalMyMenu;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
