package jp.newbees.mastersip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseFragment extends Fragment {
    protected View mRoot;

    protected TextView txtActionBarTitle;
    private String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.e(TAG, "onCreateView");
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

    /**
     * clear animation and set visible of view after animation finish
     * @param view
     * @param anim
     * @param visibility
     */
    protected void clearViewAnimation(final View view, Animation anim, final int visibility) {
        ((BaseActivity) getActivity()).clearViewAnimation(view, anim, visibility);
    }

    protected void setTransitionAnimation(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
