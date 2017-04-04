package jp.newbees.mastersip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemFragment;

/**
 * Created by ducpv on 1/18/17.
 */

public class AdapterViewPagerProfileDetail extends FragmentStatePagerAdapter {
    private  int currentIndex;
    private List<UserItem> userItems;
    private Map<Integer, ProfileDetailItemFragment> listFragments;

    public AdapterViewPagerProfileDetail(FragmentManager fm, List<UserItem> userItems, int currentIndex) {
        super(fm);
        this.userItems = userItems;
        this.listFragments = new HashMap<>();
        this.currentIndex = currentIndex;
    }

    @Override
    public Fragment getItem(int position) {
        boolean selected = position == currentIndex ? true : false;
        if (selected) {
            currentIndex = -1;
        }
        ProfileDetailItemFragment fragment = ProfileDetailItemFragment.newInstance(userItems.get(position), true, selected);
        listFragments.put(position, fragment);
        return fragment;
    }

    public final ProfileDetailItemFragment getFragmentByIndex(int index) {
        Set<Integer> keys = listFragments.keySet();
        for (Iterator<Integer>it= keys.iterator();it.hasNext();) {
            int position = it.next();
            if (position == index) {
                return listFragments.get(position);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return userItems.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        this.listFragments.remove(position);
    }
}
