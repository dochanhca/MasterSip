package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatActivity extends BaseActivity {
    @BindView(R.id.edt_chat)
    HiraginoEditText edtChat;
    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.custom_action_header_in_chat)
    NavigationLayoutGroup customActionHeaderInChat;
    @BindView(R.id.call_action_header_in_chat)
    LinearLayout callActionHeaderInChat;

    private ChatAdapter chatAdapter;


    private View.OnClickListener mOnSwitchModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int first = callActionHeaderInChat.getVisibility();
            int second = customActionHeaderInChat.getVisibility();
            callActionHeaderInChat.setVisibility(second);
            customActionHeaderInChat.setVisibility(first);
        }
    };
    private TextView.OnEditorActionListener mOnChatEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // add new chat
            }
            return false;
        }
    };

    @Override
    protected int layoutId() {
        return R.layout.chat_activity;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        initHeader("たくみ＞");
        showSwitchModeInHeader(mOnSwitchModeClickListener);
        edtChat.setOnEditorActionListener(mOnChatEditorActionListener);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {


    }

    public static void start(Context context) {
        Intent starter = new Intent(context, ChatActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
