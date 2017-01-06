package jp.newbees.mastersip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseFragment extends Fragment {
    protected View mRoot;

    protected TextView txtActionBarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(layoutId(), null);
        init(mRoot, savedInstanceState);
        return mRoot;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    protected abstract int layoutId();

    protected abstract void init(View mRoot, Bundle savedInstanceState);

    protected void setFragmentTitle(String title) {
        txtActionBarTitle = (TextView) mRoot.findViewById(R.id.txt_action_bar_title);

        txtActionBarTitle.setText(title);
    }

    protected void showLoading() {
        ((BaseActivity) getActivity()).showLoading();
    }

    protected void disMissLoading() {
        ((BaseActivity) getActivity()).disMissLoading();
    }

    protected void showToastExceptionVolleyError(int errorCode, String errorMessage) {
        ((BaseActivity) getActivity()).showToastExceptionVolleyError(getActivity().getApplicationContext(),
                errorCode, errorMessage);
    }
}
