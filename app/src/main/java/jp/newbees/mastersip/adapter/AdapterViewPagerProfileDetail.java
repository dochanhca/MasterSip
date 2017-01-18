package jp.newbees.mastersip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemFragment;

/**
 * Created by ducpv on 1/18/17.
 */

public class AdapterViewPagerProfileDetail extends FragmentStatePagerAdapter {

    private List<UserItem> userItems;

    public AdapterViewPagerProfileDetail(FragmentManager fm, List<UserItem> userItems) {
        super(fm);
        this.userItems = userItems;
    }

    @Override
    public Fragment getItem(int position) {
        ProfileDetailItemFragment profileDetailItemFragment
                = ProfileDetailItemFragment.newInstance(userItems.get(position));
        return profileDetailItemFragment;
    }

    @Override
    public int getCount() {
        return userItems.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
