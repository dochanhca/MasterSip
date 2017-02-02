package jp.newbees.mastersip.ui.gift;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.AdapterGiftsList;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.GiftItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.gift.GiftListPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 12/6/16.
 */

public class ListGiftFragment extends BaseFragment implements GiftListPresenter.GiftListView {

    @BindView(R.id.rcv_gifts_list)
    RecyclerView rcvGiftsList;
    @BindView(R.id.txt_gifts_description)
    HiraginoTextView txtGiftsDescription;
    private AdapterGiftsList adapterGiftsList;
    private GiftListPresenter giftListPresenter;

    public static final Fragment newInstance() {
        Fragment fragment = new ListGiftFragment();
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_gift;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        String titleGiftsList = getString(R.string.gifts_list);
        setFragmentTitle(titleGiftsList);
        txtGiftsDescription.setText(getDescription());
        adapterGiftsList = new AdapterGiftsList(getContext(), new ArrayList<GiftItem>());
        rcvGiftsList.setAdapter(adapterGiftsList);
        giftListPresenter = new GiftListPresenter(getContext(), this);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen._5dp);
        rcvGiftsList.addItemDecoration(itemDecoration);
    }

    private String getDescription(){
        UserItem userItem = ConfigManager.getInstance().getCurrentUser();
        int titleId = userItem.getGender() == UserItem.MALE ? R.string.gifts_description_male : R.string.gifts_description_female;
        return getString(titleId);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        giftListPresenter.loadGiftList();
    }

    @Override
    public void didLoadGiftsList(List<GiftItem> giftItems) {
        adapterGiftsList.addAll(giftItems);
        adapterGiftsList.notifyDataSetChanged();
    }

    @Override
    public void didLoadGiftsListFailure(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
    }
}
