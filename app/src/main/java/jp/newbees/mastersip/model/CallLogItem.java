package jp.newbees.mastersip.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vietbq on 4/18/17.
 */

public class CallLogItem {

    private String date;
    private List<HistoryCallItem> historyCallItems;

    public CallLogItem() {
        this.historyCallItems = new ArrayList<>();
        this.date = "";
    }

    public List<HistoryCallItem> getHistoryCallItems() {
        return historyCallItems;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addHistoryCall(HistoryCallItem historyCallItem) {
        this.historyCallItems.add(historyCallItem);
    }
}
