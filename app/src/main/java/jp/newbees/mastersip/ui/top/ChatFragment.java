package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.ChatHistory;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 1/6/17.
 */
public class ChatFragment extends BaseFragment {
    @BindView(R.id.edt_chat)
    HiraginoEditText edtChat;
    @BindView(R.id.ll_chat_history)
    ChatHistory llChatHistory;

    @Override
    protected int layoutId() {
        return R.layout.chat_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        llChatHistory.addMyChat("vasa Est camerarius lanista, cesaris.If you experiment or lure with a great trust, manifestation yearns you.");
        llChatHistory.addReplyChat("vasa Est camerarius lanista, cesaris.If you experiment or lure with a great trust, manifestation yearns you.",
                "http://www.wikiexclusive.com/phsychology-and-relationship/images/girl.jpg");

    }

    public static Fragment newInstance() {
        Fragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}
