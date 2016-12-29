package jp.newbees.mastersip.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.MaxAgeAdapter;
import jp.newbees.mastersip.adapter.MinAgeAdapter;
import jp.newbees.mastersip.adapter.SelectionAdapter;
import jp.newbees.mastersip.model.AgeItem;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Utils;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ducpv on 12/28/16.
 */

public class SelectMinMaxAgeDialog extends BaseDialog implements
        MaxAgeAdapter.OnMaxAgeAdapterClick, MinAgeAdapter.OnMinAgeAdapterClick {

    public static final String LIST_AGE = "LIST_AGE";
    public static final String MIN_AGE_SELECTED = "MIN_AGE_SELECTED";
    public static final String MAX_AGE_SELECTED = "MAX_AGE_SELECTED";

    private RecyclerView recyclerMinAge, recyclerMaxAge;
    private List<AgeItem> minAge, maxAge;
    private MinAgeAdapter minAgeAdapter;
    private MaxAgeAdapter maxAgeAdapter;

    private int selectedMinAge, selectedMaxAge;
    private int minAgePosition = -1, maxAgePosition = -1;

    public interface OnSelectAgeDialogClick {

        void onAgeSelected(int minAgeIndex, int maxAgeIndex);
    }

    private OnSelectAgeDialogClick onSelectAgeDialogClick;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        setDialogHeader(getString(R.string.age));

        recyclerMinAge = (RecyclerView) rootView.findViewById(R.id.recycler_min_age);
        recyclerMaxAge = (RecyclerView) rootView.findViewById(R.id.recycler_max_age);
        minAge = getArguments().getParcelableArrayList(SelectMinMaxAgeDialog.LIST_AGE);
        maxAge = new ArrayList<>();
        for (AgeItem age: minAge) {
            maxAge.add(age.clone());
        }


        selectedMinAge = getArguments().getInt(MIN_AGE_SELECTED);
        selectedMaxAge = getArguments().getInt(MAX_AGE_SELECTED);

        initRecyclerView();

        minAgeAdapter.setOnMinAgeAdapterClick(this);
        maxAgeAdapter.setOnMaxAgeAdapterClick(this);

        setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectAgeDialogClick.onAgeSelected(minAgePosition, maxAgePosition);
                dismiss();
            }
        });

    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_select_min_max_age;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.onSelectAgeDialogClick = (OnSelectAgeDialogClick) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (Utils.getScreenWidth(getApplicationContext()) * 0.9);
        int height = (int) (Utils.getScreenHeight(getApplicationContext()) * 0.6);

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width,
                    height);
        }
    }

    @Override
    public void onMinAgeSelected(int position) {
        minAgePosition = position;
        /**
         * If user choose min age = 40, disable all max age < 40
         */
        selectedMinAge = minAge.get(minAgePosition).getSelectionItem().getId();
        for (AgeItem age : maxAge) {
            if (age.getSelectionItem().getId() < selectedMinAge) {
                age.setDisable(true);
            } else {
                age.setDisable(false);
            }
        }
        maxAgeAdapter.notifyDataSetChanged();
    }


    @Override
    public void onMaxAgeSelected(int position) {
        maxAgePosition = position;
        /**
         * If user choose max age = 40, disable all min age > 40
         */
        selectedMaxAge = maxAge.get(maxAgePosition).getSelectionItem().getId();
        for (AgeItem age : minAge) {
            if (age.getSelectionItem().getId() > selectedMaxAge) {
                age.setDisable(true);
            } else {
                age.setDisable(false);
            }
        }
        minAgeAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        if (selectedMinAge != -1) {
            minAgePosition = selectedMinAge - Constant.Application.MIN_AGE + 1;
        }

        if (selectedMaxAge != -1) {
            maxAgePosition = selectedMaxAge - Constant.Application.MIN_AGE + 1;
        }

        calculateRecyclerViewWidth(recyclerMinAge);
        recyclerMinAge.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        minAgeAdapter = new MinAgeAdapter(getActivity().getApplicationContext(), minAge, minAgePosition);
        recyclerMinAge.setAdapter(minAgeAdapter);

        calculateRecyclerViewWidth(recyclerMaxAge);
        recyclerMaxAge.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        maxAgeAdapter = new MaxAgeAdapter(getActivity().getApplicationContext(), maxAge, maxAgePosition);
        recyclerMaxAge.setAdapter(maxAgeAdapter);
    }

    private void calculateRecyclerViewWidth(RecyclerView recyclerView) {
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.width = (int) (Utils.getScreenWidth(getApplicationContext()) * 0.9) / 2;
        recyclerView.setLayoutParams(params);
    }
}
