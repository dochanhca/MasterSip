package jp.newbees.mastersip.eventbus;

import java.util.ArrayList;

import jp.newbees.mastersip.model.LocationItem;

/**
 * Created by ducpv on 12/27/16.
 */

public class SelectLocationEvent {

    private ArrayList<LocationItem> locationItems;

    public SelectLocationEvent(ArrayList<LocationItem> locationItems) {
        this.locationItems = locationItems;
    }

    public ArrayList<LocationItem> getLocationItems() {
        return locationItems;
    }
}
