package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.ChatGroupPresenter;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 12/22/16.
 */

public class ChatGroupFragment extends BaseFragment {
    @BindView(R.id.btn_send_text)
    Button btnSendText;

    private ChatGroupPresenter presenter;

    @Override
    protected int layoutId() {
        return R.layout.chat_group_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        presenter = new ChatGroupPresenter(getContext());
    }

    public static Fragment newInstance() {
        Fragment fragment = new ChatGroupFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @OnClick(R.id.btn_send_text)
    public void onClick() {
        UserItem sendee = new UserItem();
        sendee.setSipItem(new SipItem("1000728"));
        presenter.sendText("Hello World",sendee);
    }
}
