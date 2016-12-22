package jp.newbees.mastersip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseFragment extends Fragment {
    protected View mRoot;

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

    protected abstract int layoutId();

    protected abstract void init(View mRoot, Bundle savedInstanceState);

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }
}
