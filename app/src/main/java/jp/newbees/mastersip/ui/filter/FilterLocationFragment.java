package jp.newbees.mastersip.ui.filter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.LocationAdapter;
import jp.newbees.mastersip.model.LocationItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by vietbq on 12/6/16.
 */

public class FilterLocationFragment extends BaseFragment implements
        LocationAdapter.OnLocationAdapterClick, View.OnClickListener {

    private RecyclerView recyclerLocation;
    private ImageView imgBack;
    private ImageView btnUnCheckAll;

    private List<LocationItem> locationItems;
    private LocationAdapter locationAdapter;

    public static Fragment newInstance() {
        Fragment fragment = new FilterLocationFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_filter_location;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        recyclerLocation = (RecyclerView) mRoot.findViewById(R.id.recycler_location);
        imgBack = (ImageView) mRoot.findViewById(R.id.img_back);
        btnUnCheckAll = (ImageView) mRoot.findViewById(R.id.btn_un_check_all);

        btnUnCheckAll.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        setFragmentTitle(getString(R.string.region_selection));

        initLocationItem();
        initRecyclerLocation();
    }

    @Override
    public void onClick(View view) {
        if (view == btnUnCheckAll) {
            unCheckAllArea();
        }

        if (view == imgBack) {
            putDataBack();
        }
    }

    private void putDataBack() {

    }

    private void unCheckAllArea() {
        for (LocationItem locationItem : locationItems) {
            locationItem.setChecked(false);
        }
        locationAdapter.notifyDataSetChanged();
    }

    private void initLocationItem() {

        locationItems = new ArrayList<>();

        String districts[] = getResources().getStringArray(R.array.districts);
        String cities[] = getResources().getStringArray(R.array.cities);

        // Hokkaido
        LocationItem hokkaido = new LocationItem();
        SelectionItem hokkaidoItem = new SelectionItem(1, districts[0]);
        hokkaido.setSelectionItem(hokkaidoItem);
        hokkaido.setType(LocationItem.CHILD);
        hokkaido.setParentId(LocationItem.HOKKAIDO);
        locationItems.add(hokkaido);

        // Northeast
        SelectionItem northeastItem = new SelectionItem(LocationItem.NORTHEAST, cities[0]);
        addLocationItemParent(northeastItem);
        for (int i = 1; i <= 6; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.NORTHEAST);
        }

        // Kanto
        SelectionItem kantoItem = new SelectionItem(LocationItem.KANTO, cities[1]);
        addLocationItemParent(kantoItem);
        for (int i = 7; i <= 13; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.KANTO);
        }

        // Middle
        SelectionItem middleItem = new SelectionItem(LocationItem.MIDDLE, cities[2]);
        addLocationItemParent(middleItem);
        for (int i = 14; i <= 22; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.MIDDLE);
        }

        // Kinki
        SelectionItem kinkiItem = new SelectionItem(LocationItem.KINKI, cities[3]);
        addLocationItemParent(kinkiItem);
        for (int i = 23; i <= 29; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.KINKI);
        }

        // China
        SelectionItem chinaItem = new SelectionItem(LocationItem.CHINA, cities[4]);
        addLocationItemParent(chinaItem);
        for (int i = 30; i <= 34; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.CHINA);

        }

        // Shikoku
        SelectionItem shikokuItem = new SelectionItem(LocationItem.SHIKOKU, cities[5]);
        addLocationItemParent(shikokuItem);
        for (int i = 35; i <= 38; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.SHIKOKU);
        }

        // Kyushu
        SelectionItem kyushuItem = new SelectionItem(LocationItem.KYUSHU, cities[6]);
        addLocationItemParent(kyushuItem);
        for (int i = 39; i <= 46; i++) {
            SelectionItem selectionItem = new SelectionItem((i + 1), districts[i]);
            addLocationItemChild(selectionItem, LocationItem.KYUSHU);
        }

        //other
        LocationItem otherParent = new LocationItem(new SelectionItem(LocationItem.OTHER, getString(R.string.foreign_country)
        ), false, 0, LocationItem.PARENT);
        LocationItem otherChild = new LocationItem(new SelectionItem(47, getString(R.string.foreign_country)
        ), false, LocationItem.OTHER, LocationItem.CHILD);
        locationItems.add(otherParent);
        locationItems.add(otherChild);
    }

    private void addLocationItemChild(SelectionItem selectionItem, int parentId) {
        LocationItem locationItem = new LocationItem();
        locationItem.setParentId(parentId);
        locationItem.setType(LocationItem.CHILD);
        locationItem.setSelectionItem(selectionItem);
        locationItems.add(locationItem);
    }

    private void addLocationItemParent(SelectionItem selectionItem) {
        LocationItem locationItem = new LocationItem();
        locationItem.setParentId(0);
        locationItem.setType(LocationItem.PARENT);
        locationItem.setSelectionItem(selectionItem);
        locationItems.add(locationItem);
    }

    private void initRecyclerLocation() {
        locationAdapter = new LocationAdapter(getActivity().getApplicationContext(), locationItems);
        locationAdapter.setOnLocationAdapterClick(this);

        recyclerLocation.setAdapter(locationAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerLocation.setLayoutManager(mLayoutManager);
        recyclerLocation.setItemAnimator(new DefaultItemAnimator());
        recyclerLocation.setNestedScrollingEnabled(false);
    }

    @Override
    public void onSelectAllClick(int id) {
        for (LocationItem item : locationItems) {
            if (item.getParentId() == id) {
                item.setChecked(true);
            }
        }
        locationAdapter.notifyDataSetChanged();
    }
}
