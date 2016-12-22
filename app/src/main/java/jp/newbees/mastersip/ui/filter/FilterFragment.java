package jp.newbees.mastersip.ui.filter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 12/22/16.
 */

public class FilterFragment extends BaseFragment implements View.OnClickListener {

    private ViewGroup layoutAge;
    private ViewGroup layoutLocation;
    private ViewGroup layoutSort;
    private TextView txtAge;
    private TextView txtArea;
    private TextView txtSort;
    private Button btnSearch;
    private Button btnSearchByName;

    private CheckBox ck24h;

    @Override
    protected int layoutId() {
        return R.layout.fragment_filter;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        layoutAge = (ViewGroup) rootView.findViewById(R.id.layout_age);
        layoutLocation = (ViewGroup) rootView.findViewById(R.id.layout_location);
        layoutSort = (ViewGroup) rootView.findViewById(R.id.layout_sort);
        txtAge = (TextView) rootView.findViewById(R.id.txt_age);
        txtArea = (TextView) rootView.findViewById(R.id.txt_area);
        txtSort = (TextView) rootView.findViewById(R.id.txt_sort);
        btnSearch = (Button) rootView.findViewById(R.id.btn_search);
        btnSearchByName = (Button) rootView.findViewById(R.id.btn_search_by_name);

        layoutAge.setOnClickListener(this);
        layoutSort.setOnClickListener(this);
        layoutLocation.setOnClickListener(this);
        btnSearchByName.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    public static Fragment newInstance() {
        Fragment fragment = new FilterFragment();
        return fragment;
    }
}
