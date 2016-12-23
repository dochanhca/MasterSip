package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.presenter.top.SearchPresenter;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.customviews.SegmentedGroup;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by vietbq on 12/6/16.
 */

public class SearchFragment extends BaseFragment {
    private SearchPresenter presenter;
    private static final int MODE_FOUR_COLUMN = 0;
    private static final int MODE_TWO_COLUMN = 1;
    private static final int MODE_LIST = 2;

    private int currentFilterMode;

    @BindView(R.id.txt_search)
    HiraginoTextView txtSearch;
    @BindView(R.id.txt_phone)
    HiraginoTextView txtPhone;
    @BindView(R.id.header_search)
    RelativeLayout headerSearch;
    @BindView(R.id.btn_filter_call_waiting)
    RadioButton btnFilterCallWaiting;
    @BindView(R.id.btn_filter_new)
    RadioButton btnFilterNew;
    @BindView(R.id.btn_filter_all)
    RadioButton btnFilterAll;
    @BindView(R.id.segmented_filter)
    SegmentedGroup segmentedFilter;
    @BindView(R.id.img_filter)
    ImageView imgFilter;


    private HashMap<Integer, Integer> FILTER_MODE_INDEXS;
    @Override
    protected int layoutId() {
        return R.layout.search_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        presenter = new SearchPresenter(getContext());
        ButterKnife.bind(this, mRoot);
        btnFilterCallWaiting.setChecked(true);

        initFilterMode();
    }

    public static Fragment newInstance() {
        Fragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.img_filter)
    public void onClick() {
        changeMode();

    }

    private void changeMode() {
        setCurrentToNextFilterMode();
        changeFilterImage();

    }

    private void changeFilterImage() {
        StringBuilder uri = new StringBuilder("@drawable/ic_");
        uri.append(currentFilterMode + "");
        int imageResource = getResources().getIdentifier(uri.toString(), null, getActivity().getPackageName());
        imgFilter.setImageDrawable(getResources().getDrawable(imageResource));
    }

    private void setCurrentToNextFilterMode() {
        currentFilterMode = FILTER_MODE_INDEXS.get(currentFilterMode);
    }

    private void initFilterMode() {
        FILTER_MODE_INDEXS = new HashMap<>();
        FILTER_MODE_INDEXS.put(MODE_FOUR_COLUMN, MODE_TWO_COLUMN);
        FILTER_MODE_INDEXS.put(MODE_TWO_COLUMN, MODE_LIST);
        FILTER_MODE_INDEXS.put(MODE_LIST, MODE_FOUR_COLUMN);
    }
}
