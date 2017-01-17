package jp.newbees.mastersip.event;

import java.util.List;

import jp.newbees.mastersip.model.LocationItem;

/**
 * Created by ducpv on 12/27/16.
 */

public class SelectLocationEvent {

    private List<LocationItem> locationItems;
    private boolean isFromFilterLocationFragment = false;

    public SelectLocationEvent(List<LocationItem> locationItems, boolean isFromFilterLocationFragment) {
        this.locationItems = locationItems;
        this.isFromFilterLocationFragment = isFromFilterLocationFragment;
    }

    public List<LocationItem> getLocationItems() {
        return locationItems;
    }

    public boolean isFromFilterLocationFragment() {
        return isFromFilterLocationFragment;
    }

    public void setFromFilterLocationFragment(boolean fromFilterLocationFragment) {
        isFromFilterLocationFragment = fromFilterLocationFragment;
    }
}
