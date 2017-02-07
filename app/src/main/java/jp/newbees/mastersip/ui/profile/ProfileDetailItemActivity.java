package jp.newbees.mastersip.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.WrapperWithFooterNavigationActivity;

/**
 * Created by thangit14 on 2/7/17.
 */

public class ProfileDetailItemActivity extends WrapperWithFooterNavigationActivity {

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        UserItem userItem = getIntent().getParcelableExtra(ProfileDetailItemFragment.USER_ITEM);
        Fragment fragment = ProfileDetailItemFragment.newInstance(userItem);
        initHeader(userItem.getUsername());
        showFragmentContent(fragment);
    }

    public static void startActivity(Context context, UserItem userItem) {
        Intent intent = new Intent(context, ProfileDetailItemActivity.class);
        intent.putExtra(ProfileDetailItemFragment.USER_ITEM, (Parcelable) userItem);
        context.startActivity(intent);
    }
}
