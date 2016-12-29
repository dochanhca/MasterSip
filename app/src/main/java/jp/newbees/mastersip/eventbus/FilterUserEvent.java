package jp.newbees.mastersip.eventbus;

/**
 * Created by ducpv on 12/29/16.
 */

public class FilterUserEvent  {
    private boolean needFilter;

    public FilterUserEvent(boolean needFilter) {
        this.needFilter = needFilter;
    }

    public boolean isNeedFilter() {
        return needFilter;
    }
}
