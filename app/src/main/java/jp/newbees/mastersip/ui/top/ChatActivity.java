package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.customviews.SoftKeyboardLsnedRelaytiveLayout;
import jp.newbees.mastersip.eventbus.EventManage;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.ChatPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.Constant;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatActivity extends BaseActivity {
    private static final String USER = "USER";

    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.custom_action_header_in_chat)
    NavigationLayoutGroup customActionHeaderInChat;
    @BindView(R.id.call_action_header_in_chat)
    LinearLayout callActionHeaderInChat;
    @BindView(R.id.edt_chat)
    HiraginoEditText edtChat;
    @BindView(R.id.action_phone)
    LinearLayout actionPhone;
    @BindView(R.id.action_video)
    LinearLayout actionVideo;
    @BindView(R.id.txt_send)
    TextView txtSend;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.container)
    SoftKeyboardLsnedRelaytiveLayout container;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;

    private ChatPresenter presenter;

    private UserItem user;
    private String fromExtension;
    private boolean donotHideSoftKeyboard = false;
    private boolean isCustomActionHeaderInChatOpened = true;
    private boolean isCallActionHeaderInChatOpened = false;
    private boolean isSoftKeyboardOpened = false;

    private View.OnClickListener mOnSwitchModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int first = callActionHeaderInChat.getVisibility();
            int second = customActionHeaderInChat.getVisibility();
            if (first == View.VISIBLE) {
                isCustomActionHeaderInChatOpened = true;
                isCallActionHeaderInChatOpened = false;
            } else {
                isCustomActionHeaderInChatOpened = false;
                isCallActionHeaderInChatOpened = true;
            }
            updateRecycleChatPaddingTop(isCallActionHeaderInChatOpened);
            callActionHeaderInChat.setVisibility(second);
            customActionHeaderInChat.setVisibility(first);
            if (isSoftKeyboardOpened) {
                slideUpCustomActionheaderInChat();
            }
        }
    };

    private void updateRecycleChatPaddingTop(boolean isCallActionHeaderInChatOpened) {
        if (isCallActionHeaderInChatOpened) {
            recyclerChat.setPadding(0, (int) getResources().getDimension(R.dimen.header_search_height),
                    0, (int) getResources().getDimension(R.dimen.xnormal_margin));
        } else {
            recyclerChat.setPadding(0, 0,
                    0, (int) getResources().getDimension(R.dimen.xnormal_margin));
        }
    }

    private TextView.OnEditorActionListener mOnChatEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // add new chat
            }
            return false;
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            if (!donotHideSoftKeyboard && dy != 0) {
                hideFilterAndNavigationBar();
                if (scrollDown(dy)) {
                    slideDownCustomActionheaderInChat();
                } else {
                    slideUpCustomActionheaderInChat();
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (donotHideSoftKeyboard) {
                        donotHideSoftKeyboard = false;
                    } else {
                        hideFilterAndNavigationBar();
                    }
                    break;
            }
        }

        private boolean scrollDown(int dy) {
            return dy < 0;
        }

        private void hideFilterAndNavigationBar() {
            View view = ChatActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    };

    private void slideDownCustomActionheaderInChat() {
        updateRecycleChatPaddingTop(isCallActionHeaderInChatOpened);
        if (!isCustomActionHeaderInChatOpened) {
            customActionHeaderInChat.startAnimation(slideDown);
        }
        isCustomActionHeaderInChatOpened = true;
    }

    private void slideUpCustomActionheaderInChat() {
        updateRecycleChatPaddingTop(isCallActionHeaderInChatOpened);
        if (isCustomActionHeaderInChatOpened) {
            customActionHeaderInChat.startAnimation(slideUp);
        }
        isCustomActionHeaderInChatOpened = false;
    }

    private ChatPresenter.ChatPresenterListener mOnChatListener = new ChatPresenter.ChatPresenterListener() {
        @Override
        public void didSendChatToServer(BaseChatItem baseChatItem) {
            chatAdapter.add(baseChatItem);
            edtChat.setEnabled(true);
            txtSend.setEnabled(true);
            recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }

        @Override
        public void didChatError(int errorCode, String errorMessage) {
            donotHideSoftKeyboard = true;

        }
    };
    private Animation slideDown;
    private Animation slideUp;

    @Override
    protected int layoutId() {
        return R.layout.chat_activity;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        showSwitchModeInHeader(mOnSwitchModeClickListener);
        edtChat.setOnEditorActionListener(mOnChatEditorActionListener);

        swipeRefreshLayout.setProgressViewOffset(false, (int) getResources().getDimension(R.dimen.header_filter_height),
                2 * (int) getResources().getDimension(R.dimen.header_filter_height));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(ChatActivity.this, "load more  chat history", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerChat.addOnScrollListener(onScrollListener);
        container.setListener(new SoftKeyboardLsnedRelaytiveLayout.SoftKeyboardLsner() {
            @Override
            public void onSoftKeyboardShow() {
                isSoftKeyboardOpened = true;
                slideUpCustomActionheaderInChat();

            }

            @Override
            public void onSoftKeyboardHide() {
                isSoftKeyboardOpened = false;
                slideDownCustomActionheaderInChat();
            }
        });
        updateRecycleChatPaddingTop(isCallActionHeaderInChatOpened);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_to_show);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_to_hide);

        presenter = new ChatPresenter(this, mOnChatListener);

        user = getIntent().getParcelableExtra(USER);
        fromExtension = user.getSipItem().getExtension();

        initHeader(user.getUsername());

        chatAdapter = new ChatAdapter(this, new ArrayList<BaseChatItem>());
        layoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setItemAnimator(new DefaultItemAnimator());
        recyclerChat.setNestedScrollingEnabled(false);
        recyclerChat.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        EventManage.getInstance().registerChattingEventName(fromExtension, Constant.API.KIND_CHAT, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        String eventName = EventManage.getInstance().genChattingEventName(fromExtension, Constant.API.KIND_CHAT);
        EventManage.getInstance().unregisterEventName(eventName);
    }

    public static void start(Context context, UserItem userItem) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(USER, (Parcelable) userItem);
        context.startActivity(starter);
    }

    @Subscribe()
    public void onChatMessageEvent(BaseChatItem baseChatItem) {
        donotHideSoftKeyboard = true;
        chatAdapter.add(baseChatItem);
        recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }


    @OnClick({R.id.action_phone, R.id.action_video, R.id.txt_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_phone:
                break;
            case R.id.action_video:
                break;
            case R.id.txt_send:
                String newMessage = edtChat.getText().toString();
                if (!newMessage.equalsIgnoreCase("")) {
                    donotHideSoftKeyboard = true;
                    edtChat.setText("");
                    presenter.sendText(newMessage, user);
                }
                break;
        }
    }
}
