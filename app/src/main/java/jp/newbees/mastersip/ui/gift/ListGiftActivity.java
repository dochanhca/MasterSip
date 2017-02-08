package jp.newbees.mastersip.ui.gift;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.Serializable;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.WrapperWithBottomNavigationActivity;

/**
 * Created by thangit14 on 2/7/17.
 */

public class ListGiftActivity extends WrapperWithBottomNavigationActivity {

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        UserItem userItem = getIntent().getParcelableExtra(ListGiftFragment.USER_ITEM);
        Fragment fragment = ListGiftFragment.newInstance(userItem, ListGiftFragment.OPEN_FROM_CHAT);
        initHeader(userItem.getUsername());
        showFragmentContent(fragment);
    }

    public static void startActivity(Context context, UserItem userItem) {
        Intent intent = new Intent(context, ListGiftActivity.class);
        intent.putExtra(ListGiftFragment.USER_ITEM, (Serializable) userItem);
        context.startActivity(intent);
    }
}
