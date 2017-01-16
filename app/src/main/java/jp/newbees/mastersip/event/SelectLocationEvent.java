package jp.newbees.mastersip.event;

import java.util.List;

import jp.newbees.mastersip.model.LocationItem;

/**
 * Created by ducpv on 12/27/16.
 */

public class SelectLocationEvent {

    private List<LocationItem> locationItems;

    public SelectLocationEvent(List<LocationItem> locationItems) {
        this.locationItems = locationItems;
    }

    public List<LocationItem> getLocationItems() {
        return locationItems;
    }
}
