package jp.newbees.mastersip.ui.filter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.AdapterSearchUserModeList;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.FilterByNamePresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailFragment;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class FilterByNameFragment extends BaseFragment implements View.OnClickListener,
        FilterByNamePresenter.FilterByNameView, View.OnTouchListener, AdapterSearchUserModeList.OnItemClickListener {

    private ImageView imgClose;
    private EditText edtSearch;
    private RecyclerView recyclerUser;
    private TextView txtCancel;
    private TextView txtNote;
    private ImageView imgBack;

    private AdapterSearchUserModeList adapterSearchUserModeList;
    private List<UserItem> userItems;

    private FilterByNamePresenter filterUserPresenter;

    private boolean isLoading;
    private boolean firstTimeLoadData = true;
    private boolean isShowFilterAndNavigationBar = true;

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = edtSearch.getText().toString().trim();
            if (!isLoading && name.length() > 0) {
                filterUserPresenter.filterUserByName(s.toString());
                isLoading = true;
            } else {
                adapterSearchUserModeList.clearData();
                imgClose.setVisibility(View.GONE);
                txtNote.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!firstTimeLoadData) {
                if (scrollUp(dy) && isShowFilterAndNavigationBar) {
                    isShowFilterAndNavigationBar = false;
                    hideFilterAndNavigationBar();
                } else if (scrollDown(dy) && !isShowFilterAndNavigationBar) {
                    isShowFilterAndNavigationBar = true;
                    showFilterAndNavigationBar();
                }
            }

            firstTimeLoadData = false;
        }

        private boolean scrollUp(int dy) {
            return dy > 0;
        }

        private boolean scrollDown(int dy) {
            return dy < 0;
        }

        private void hideFilterAndNavigationBar() {
            ((TopActivity) getActivity()).hideNavigation();
        }

        private void showFilterAndNavigationBar() {
            ((TopActivity) getActivity()).showNavigation();
        }
    };

    @Override
    protected int layoutId() {
        return R.layout.fragment_filter_by_name;
    }

    public static FilterByNameFragment newInstance() {
        return new FilterByNameFragment();
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        setFragmentTitle(getString(R.string.filter_by_name));
        restoreNavigationBarState();
        userItems = new ArrayList<>();
        filterUserPresenter = new FilterByNamePresenter(getActivity().getApplicationContext(), this);

        imgClose = (ImageView) mRoot.findViewById(R.id.img_close);
        imgBack = (ImageView) mRoot.findViewById(R.id.img_back);
        edtSearch = (EditText) mRoot.findViewById(R.id.edt_search);
        recyclerUser = (RecyclerView) mRoot.findViewById(R.id.recycler_user);
        txtCancel = (TextView) mRoot.findViewById(R.id.txt_cancel);
        txtNote = (TextView) mRoot.findViewById(R.id.txt_note);

        txtCancel.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        imgBack.setOnClickListener(this);


        initRecycler();

        edtSearch.addTextChangedListener(onTextChangedListener);
        edtSearch.setOnTouchListener(this);
    }

    private void initRecycler() {
        adapterSearchUserModeList = new AdapterSearchUserModeList(getContext(), userItems);
        adapterSearchUserModeList.setOnItemClickListener(this);

        recyclerUser.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerUser.setAdapter(adapterSearchUserModeList);
        recyclerUser.addOnScrollListener(onScrollListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cancel:
                resetDataAndView();
                break;
            case R.id.img_close:
                resetData();
                break;
            case R.id.img_back:
                hideSoftKeyboard();
                restoreNavigationBarState();
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view instanceof EditText && txtCancel.getVisibility() == View.GONE) {
            // User touched edittext
            txtCancel.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public void didFilterUser(List<UserItem> data) {
        userItems.clear();
        userItems.addAll(data);
        adapterSearchUserModeList.notifyDataSetChanged();
        imgClose.setVisibility(View.VISIBLE);
        txtNote.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        isLoading = false;
    }

    @Override
    public void didFilterUserError(int errorCode, String errorMessage) {
        isLoading = false;
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void onItemClick(UserItem item, int position) {
        showProfileDetailFragment(position);
    }

    private void resetDataAndView() {
        resetData();
        hideSoftKeyboard();
        edtSearch.clearFocus();
        txtCancel.setVisibility(View.GONE);
    }

    private void resetData() {
        imgClose.setVisibility(View.GONE);
        txtNote.setVisibility(View.VISIBLE);
        edtSearch.setText("");
        adapterSearchUserModeList.clearData();
    }

//    private void hideSoftKeyboard() {
//        Utils.closeKeyboard(getActivity(), edtSearch.getWindowToken());
//    }

    private void showProfileDetailFragment(int position) {
        ProfileDetailFragment profileDetailFragment =
                ProfileDetailFragment.newInstance(userItems, position, "0", Constant.API.ALL_USER);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        setTransitionAnimation(transaction);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_search_container, profileDetailFragment,
                ProfileDetailFragment.class.getName()).commit();
    }
}
