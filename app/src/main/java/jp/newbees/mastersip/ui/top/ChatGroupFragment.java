package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 12/22/16.
 */

public class ChatGroupFragment extends BaseFragment {
    @Override
    protected int layoutId() {
        return R.layout.chat_group_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {

    }

    public static Fragment newInstance() {
        Fragment fragment = new ChatGroupFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}
