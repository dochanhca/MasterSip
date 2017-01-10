package jp.newbees.mastersip.ui.top;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 12/22/16.
 */

public class MyMenuFragment extends BaseFragment {

    private Button btnFakeLogout;

    @Override
    protected int layoutId() {
        return R.layout.my_menu_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        btnFakeLogout = (Button) mRoot.findViewById(R.id.btn_fake_logout);
        btnFakeLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    public static Fragment newInstance() {
        Fragment fragment = new MyMenuFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void logout() {
        stopLinphoneService();
        ConfigManager.getInstance().resetSettings();
        Intent intent = new Intent(getActivity().getApplicationContext(), StartActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    private void stopLinphoneService(){
        Intent intent = new Intent(getActivity(), LinphoneService.class);
        getActivity().stopService(intent);
    }

}
