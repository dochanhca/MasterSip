package jp.newbees.mastersip.ui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by ducpv on 1/24/17.
 */

public class GiftFragment extends BaseFragment {
    private static final String USER_ITEM = "USER_ITEM";

    private ImageView imgBack;

    private UserItem userItem;

    public static Fragment newInstance(UserItem userItem) {
        Fragment fragment = new GiftFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_ITEM, userItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_gift;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        userItem = getArguments().getParcelable(USER_ITEM);

        imgBack = (ImageView) mRoot.findViewById(R.id.img_back);

        setFragmentTitle(userItem.getUsername());
    }
}
