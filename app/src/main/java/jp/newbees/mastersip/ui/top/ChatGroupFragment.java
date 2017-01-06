package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.CheckCallPresenter;
import jp.newbees.mastersip.presenter.top.ChatGroupPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 12/22/16.
 */

public class ChatGroupFragment extends BaseFragment implements CheckCallPresenter.View {

    @BindView(R.id.btn_send_text)
    Button btnSendText;
    @BindView(R.id.btn_check_call)
    Button btnCheckCall;

    private ChatGroupPresenter presenter;
    private CheckCallPresenter checkCallPresenter;

    @Override
    protected int layoutId() {
        return R.layout.chat_group_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        presenter = new ChatGroupPresenter(getContext());
        checkCallPresenter = new CheckCallPresenter(getActivity().getApplicationContext(), this);
    }

    public static Fragment newInstance() {
        Fragment fragment = new ChatGroupFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @OnClick({R.id.btn_send_text, R.id.btn_check_call})
    public void onClick(View view) {
        if (view == btnSendText) {
            UserItem sendee = new UserItem();
            sendee.setSipItem(new SipItem("1000728"));
            presenter.sendText("Hello World", sendee);
        }

        if (view == btnCheckCall) {
            UserItem userItem = ConfigManager.getInstance().getCurrentUser();
            String callerExtesion = userItem.getSipItem().getExtension();
            Logger.e(getClass().getSimpleName(), callerExtesion);
            checkCallPresenter.checkCall(callerExtesion, "1104", Constant.API.VOICE_CALL,
                    Constant.API.KIND_CHAT);
        }
    }

    @Override
    public void didCheckCall(Map<String, Object> result) {
        Toast.makeText(getActivity().getApplicationContext(), "" + (int) result.get(Constant.JSON.K_MESSAGE_ID),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didCheckCallError(int errorCode, String errorMessage) {
        Toast.makeText(getActivity().getApplicationContext(),"Error: "+ errorMessage, Toast.LENGTH_SHORT).show();
    }
}
