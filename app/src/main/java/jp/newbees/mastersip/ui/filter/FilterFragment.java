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
import jp.newbees.mastersip.eventbus.FilterUserEvent;
import jp.newbees.mastersip.eventbus.SelectLocationEvent;
import jp.newbees.mastersip.model.AgeItem;
import jp.newbees.mastersip.model.FilterItem;
import jp.newbees.mastersip.model.LocationItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.dialog.SelectMinMaxAgeDialog;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 12/22/16.
 */

public class FilterFragment extends BaseFragment implements View.OnClickListener,
        SelectMinMaxAgeDialog.OnSelectAgeDialogClick, SelectionDialog.OnSelectionDialogClick {

    public static final int SELECT_AGE_DIALOG = 1;
    private static final int SELECT_DIALOG = 2;

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

    private CheckBox cb24h;

    private FilterItem filterItem;

    private AgeItem minAge, maxAge, defaultAge;
    private ArrayList<AgeItem> ages;
    private ArrayList<SelectionItem> sortCondition;
    private SelectionItem orderBy;

    private ConfigManager configManager;

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
        configManager = ConfigManager.getInstance();

        layoutAge = (ViewGroup) rootView.findViewById(R.id.layout_age);
        layoutLocation = (ViewGroup) rootView.findViewById(R.id.layout_location);
        layoutSort = (ViewGroup) rootView.findViewById(R.id.layout_sort);
        txtAge = (TextView) rootView.findViewById(R.id.txt_age);
        txtArea = (TextView) rootView.findViewById(R.id.txt_area);
        txtSort = (TextView) rootView.findViewById(R.id.txt_sort);
        btnSearch = (Button) rootView.findViewById(R.id.btn_search);
        btnSearchByName = (Button) rootView.findViewById(R.id.btn_search_by_name);
        imgBack = (ImageView) rootView.findViewById(R.id.img_back);
        cb24h = (CheckBox) rootView.findViewById(R.id.cb_24h);

        setFragmentTitle(getString(R.string.search_condition));

        layoutAge.setOnClickListener(this);
        layoutSort.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);
        btnSearchByName.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        getFilterCondition();
    }

    private void getFilterCondition() {
        // Order by
        sortCondition = new ArrayList<>();
        SelectionItem lastLogin = new SelectionItem(Constant.Application.LAST_LOGIN, getString(R.string.last_login));
        SelectionItem lastRegister = new SelectionItem(Constant.Application.LAST_REGISTER, getString(R.string.last_register));
        sortCondition.add(lastLogin);
        sortCondition.add(lastRegister);

        filterItem = configManager.getFilterUser();
        selectedItems = filterItem.getLocations();

        getAgeFromMemory();
        updateView();
        updateArea(selectedItems);
    }

    private void getAgeFromMemory() {
        // if users don't select min -> max age
        defaultAge = new AgeItem(new SelectionItem(-1, getString(R.string.do_not_care)), false);

        if (filterItem.getMinAge() > 0) {
            minAge = new AgeItem(new SelectionItem(filterItem.getMinAge(), filterItem.getMinAge() + ""), false);
        } else {
            minAge = defaultAge;
        }

        if (filterItem.getMaxAge() > 0) {
            maxAge = new AgeItem(new SelectionItem(filterItem.getMaxAge(), filterItem.getMaxAge() + ""), false);
        } else {
            maxAge = defaultAge;
        }
    }

    private void updateView() {
        String age = minAge.getSelectionItem().getTitle() + "~" + maxAge.getSelectionItem().getTitle();
        txtAge.setText(age);

        if (filterItem.getOrderBy().getTitle().length() == 0) {
            // default sort condition is user last login
            orderBy = new SelectionItem(Constant.Application.LAST_LOGIN, getString(R.string.last_login));
        } else {
            orderBy = filterItem.getOrderBy();
        }

        txtSort.setText(orderBy.getTitle());
        cb24h.setChecked(filterItem.isLogin24hours());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_location:
                showFilterLocationFragment();
                break;
            case R.id.layout_age:
                openSelectAgeDialog();
                break;
            case R.id.layout_sort:
                SelectionDialog.openSelectionDialogFromFragment(this, SELECT_DIALOG, getFragmentManager(),
                        sortCondition, getString(R.string.sort), orderBy);
                break;
            case R.id.btn_search:
                getSearchCondition();
                break;
            case R.id.btn_search_by_name:
                showFilterByNameFragment();
                break;
            case R.id.img_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void onAgeSelected(int minAgeIndex, int maxAgeIndex) {
        minAge = ages.get(minAgeIndex);
        maxAge = ages.get(maxAgeIndex);

        String age = minAge.getSelectionItem().getTitle() + "~" + maxAge.getSelectionItem().getTitle();
        txtAge.setText(age);
    }

    /**
     * @param position Select sort condition
     */
    @Override
    public void onItemSelected(int position) {
        orderBy = sortCondition.get(position);
        txtSort.setText(orderBy.getTitle());
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
        Logger.e(TAG, "onSelectLocationEvent receive: " + event.getLocationItems().size());

        selectedItems = new ArrayList<>();
        selectedItems.addAll(event.getLocationItems());
        updateArea(selectedItems);
    }

    public void updateArea(ArrayList<LocationItem> selectedItems) {
        ArrayList<LocationItem> temps = new ArrayList<>();
        for (LocationItem item : selectedItems) {
            temps.add(item.clone());
        }

        cities = getResources().getStringArray(R.array.cities);

        for (LocationItem item : temps) {
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
            grossLocations(temps, LocationItem.NORTHEAST);
        }

        if (kanto == LocationItem.KANTO_DISTRICTS) {
            grossLocations(temps, LocationItem.KANTO);
        }

        if (middle == LocationItem.MIDDLE_DISTRICTS) {
            grossLocations(temps, LocationItem.MIDDLE);
        }

        if (kinki == LocationItem.KINKI_DISTRICTS) {
            grossLocations(temps, LocationItem.KINKI);
        }

        if (china == LocationItem.CHINA_DISTRICTS) {
            grossLocations(temps, LocationItem.CHINA);
        }

        if (shikoku == LocationItem.SHIKOKU_DISTRICTS) {
            grossLocations(temps, LocationItem.SHIKOKU);
        }

        if (kyushu == LocationItem.KYUSHU_DISTRICTS) {
            grossLocations(temps, LocationItem.KYUSHU);
        }

        showAreaToTextView(temps);
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

    private void showAreaToTextView(ArrayList<LocationItem> temps) {
        if (temps.size() == 0) {
            txtArea.setText(getString(R.string.do_not_care));
        } else {
            StringBuilder area = new StringBuilder();
            for (int i = 0; i < temps.size(); i++) {
                area.append(temps.get(i).getSelectionItem().getTitle());
                if (i < temps.size() - 1) {
                    area.append(", ");
                }
            }
            txtArea.setText(area.toString());
        }
    }

    /**
     * open select Location screen
     */
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

    private void showFilterByNameFragment() {
        FilterByNameFragment filterByNameFragment = FilterByNameFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.fragment_search_container, filterByNameFragment)
                .addToBackStack(null).commit();
    }

    private void openSelectAgeDialog() {
        ages = new ArrayList<>();
        initAges();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SelectMinMaxAgeDialog.LIST_AGE, ages);
        bundle.putInt(SelectMinMaxAgeDialog.MIN_AGE_SELECTED, filterItem.getMinAge());
        bundle.putInt(SelectMinMaxAgeDialog.MAX_AGE_SELECTED, filterItem.getMaxAge());
        SelectMinMaxAgeDialog selectMinMaxAgeDialog = new SelectMinMaxAgeDialog();
        selectMinMaxAgeDialog.setArguments(bundle);
        selectMinMaxAgeDialog.setTargetFragment(this, SELECT_AGE_DIALOG);

        selectMinMaxAgeDialog.show(getFragmentManager(), "SelectMinMaxAgeDialog");
    }

    private void initAges() {
        ages.add(defaultAge);
        for (int i = Constant.Application.MIN_AGE; i <= Constant.Application.MAX_AGE; i++) {
            SelectionItem age = new SelectionItem(i, i + "");
            AgeItem ageItem = new AgeItem(age, false);
            ages.add(ageItem);
        }
    }

    private void getSearchCondition() {
        filterItem.setLogin24hours(cb24h.isChecked());
        filterItem.setMinAge(minAge.getSelectionItem().getId());
        filterItem.setMaxAge(maxAge.getSelectionItem().getId());
        filterItem.setOrderBy(orderBy);
        filterItem.setLocations(selectedItems);

        configManager.saveFilterSetting(filterItem);
        EventBus.getDefault().postSticky(new FilterUserEvent());
        getFragmentManager().popBackStack();
    }
}
