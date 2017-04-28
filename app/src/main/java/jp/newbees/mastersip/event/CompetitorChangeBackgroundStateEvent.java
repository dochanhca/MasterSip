package jp.newbees.mastersip.event;

/**
 * Created by thangit14 on 4/27/17.
 */

public class CompetitorChangeBackgroundStateEvent {
    private String action;

    public CompetitorChangeBackgroundStateEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
