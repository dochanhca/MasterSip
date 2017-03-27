package jp.newbees.mastersip.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;

/**
 * Created by thangit14 on 2/7/17.
 */

public class ProfileDetailItemActivity extends WrapperWithBottomNavigationActivity {

    private UserItem userItem;

    public static void startActivity(Context context, UserItem userItem) {
        Intent intent = new Intent(context, ProfileDetailItemActivity.class);
        intent.putExtra(ProfileDetailItemFragment.USER_ITEM, (Parcelable) userItem);
        context.startActivity(intent);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        userItem = getIntent().getParcelableExtra(ProfileDetailItemFragment.USER_ITEM);
        Fragment fragment = ProfileDetailItemFragment.newInstance(userItem, false);
        initHeader(userItem.getUsername());
        showFragmentContent(fragment, ProfileDetailItemFragment.class.getName());
    }

    @Override
    public void onBackPressed() {
        changeHeaderText(userItem.getUsername());
        super.onBackPressed();
    }
}
