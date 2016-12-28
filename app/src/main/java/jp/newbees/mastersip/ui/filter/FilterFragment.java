package jp.newbees.mastersip.ui.filter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.eventbus.SelectLocationEvent;
import jp.newbees.mastersip.model.LocationItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 12/22/16.
 */

public class FilterFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    public static final String SELECTED_LOCATION = "SELECTED_LOCATION";

    private ViewGroup layoutAge;
    private ViewGroup layoutLocation;
    private ViewGroup layoutSort;
    private TextView txtAge;
    private TextView txtArea;
    private TextView txtSort;
    private Button btnSearch;
    private Button btnSearchByName;
    private ImageView imgBack;

    private String cities[];
    private ArrayList<LocationItem> selectedItems;
    private int northeast, kanto, middle, kinki, china, shikoku, kyushu;

    private CheckBox ck24h;

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_filter;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
//        selectedItems = ConfigManager.getInstance().getFilterUser()
        layoutAge = (ViewGroup) rootView.findViewById(R.id.layout_age);
        layoutLocation = (ViewGroup) rootView.findViewById(R.id.layout_location);
        layoutSort = (ViewGroup) rootView.findViewById(R.id.layout_sort);
        txtAge = (TextView) rootView.findViewById(R.id.txt_age);
        txtArea = (TextView) rootView.findViewById(R.id.txt_area);
        txtSort = (TextView) rootView.findViewById(R.id.txt_sort);
        btnSearch = (Button) rootView.findViewById(R.id.btn_search);
        btnSearchByName = (Button) rootView.findViewById(R.id.btn_search_by_name);
        imgBack = (ImageView) rootView.findViewById(R.id.img_back);

        setFragmentTitle(getString(R.string.search_condition));

        layoutAge.setOnClickListener(this);
        layoutSort.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);
        btnSearchByName.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_location:
                showFilterLocationFragment();
                break;
            case R.id.img_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        northeast = kanto = middle = kinki = china = shikoku = kyushu = 0;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * @param event Receive selected locations from FilterLocationFragment
     */
    @Subscribe(sticky = true)
    public void onSelectLocationEvent(SelectLocationEvent event) {
        Logger.e(TAG, "on Event bus receive");

        selectedItems = new ArrayList<>();
        selectedItems.addAll(event.getLocationItems());
        updateArea(event.getLocationItems());
    }

    public void updateArea(ArrayList<LocationItem> selectedItems) {
//        ArrayList<LocationItem> temps = new ArrayList<>();
        cities = getResources().getStringArray(R.array.cities);

        for (LocationItem item : selectedItems) {
            if (item.getParentId() == LocationItem.NORTHEAST) {
                northeast++;
            } else if (item.getParentId() == LocationItem.KANTO) {
                kanto++;
            } else if (item.getParentId() == LocationItem.MIDDLE) {
                middle++;
            } else if (item.getParentId() == LocationItem.KINKI) {
                kinki++;
            } else if (item.getParentId() == LocationItem.CHINA) {
                china++;
            } else if (item.getParentId() == LocationItem.SHIKOKU) {
                shikoku++;
            } else if (item.getParentId() == LocationItem.KYUSHU) {
                kyushu++;
            }
        }

        if (northeast == LocationItem.NORTHEAST_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.NORTHEAST);
        }

        if (kanto == LocationItem.KANTO_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.KANTO);
        }

        if (middle == LocationItem.MIDDLE_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.MIDDLE);
        }

        if (kinki == LocationItem.KINKI_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.KINKI);
        }

        if (china == LocationItem.CHINA_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.CHINA);
        }

        if (shikoku == LocationItem.SHIKOKU_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.SHIKOKU);
        }

        if (kyushu == LocationItem.KYUSHU_DISTRICTS) {
            grossLocations(selectedItems, LocationItem.KYUSHU);
        }

        showAreaToTextview(selectedItems);
    }


    /**
     * @param locationItems
     * @param parentId      If user select all districts from a city,
     *                      gross districts to city, only show this city to text view
     */
    private void grossLocations(ArrayList<LocationItem> locationItems, int parentId) {
        Iterator<LocationItem> i = locationItems.iterator();
        while (i.hasNext()) {
            LocationItem item = i.next();
            if (item.getParentId() == parentId) {
                i.remove();
            }
        }
        LocationItem locationItem = new LocationItem();
        SelectionItem city = new SelectionItem(parentId, cities[(-parentId) - 1]);
        locationItem.setSelectionItem(city);
        locationItems.add(locationItem);
    }

    private void showAreaToTextview(ArrayList<LocationItem> temps) {
        StringBuilder area = new StringBuilder();
        for (int i = 0; i < temps.size(); i++) {
            area.append(temps.get(i).getSelectionItem().getTitle());
            if (i < temps.size() - 1) {
                area.append(", ");
            }
        }

        if (area.length() > 0) {
            txtArea.setText(area.toString());
        }
    }

    private void showFilterLocationFragment() {
        Bundle args = new Bundle();
        args.putParcelableArrayList(SELECTED_LOCATION, selectedItems);
        FilterLocationFragment filterLocationFragment = FilterLocationFragment.newInstance(args);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment_search_container, filterLocationFragment,
                FilterLocationFragment.class.getName())
                .addToBackStack(null).commit();
    }
}
