package jp.newbees.mastersip.ui.top;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonicartos.superslim.LayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.customviews.SoftKeyboardLsnedRelaytiveLayout;
import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.eventbus.ReceivingReadMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.ChatPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.Logger;

import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatActivity extends BaseActivity {
    private static final String USER = "USER";
    public static final String TAG = "ChatActivity";

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
    @BindView(R.id.swap_layout)
    LinearLayout swapRecycleChatLayout;

    private ChatAdapter chatAdapter;

    private ChatPresenter presenter;

    private UserItem userItem;
    private Animation slideDown;
    private Animation slideUp;

    private volatile boolean donotHideSoftKeyboard = false;
    private boolean isCustomActionHeaderInChatOpened = true;
    private boolean isCallActionHeaderInChatOpened = false;
    private boolean isSoftKeyboardOpened = false;

    private boolean isResume = false;

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
            updateTopPaddingRecycle();
            callActionHeaderInChat.setVisibility(second);
            customActionHeaderInChat.setVisibility(first);
            if (isSoftKeyboardOpened) {
                slideUpCustomActionheaderInChat();
            }
        }
    };

    private void updateTopPaddingRecycle() {
        if (isCallActionHeaderInChatOpened || isCustomActionHeaderInChatOpened) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerChat.getLayoutParams();
            layoutParams.topMargin = (int) getResources().getDimension(R.dimen.header_search_height);
            recyclerChat.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerChat.getLayoutParams();
            layoutParams.topMargin = 0;
            recyclerChat.setLayoutParams(layoutParams);
        }
    }

    private ChatPresenter.ChatPresenterListener mOnChatListener = new ChatPresenter.ChatPresenterListener() {
        @Override
        public void didSendChatToServer(BaseChatItem baseChatItem) {
            Logger.e(TAG, "sending message to server success");
            chatAdapter.addItemAndHeaderIfNeed(baseChatItem);
            edtChat.setEnabled(true);
            txtSend.setEnabled(true);
            recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }

        @Override
        public void didChatError(int errorCode, String errorMessage) {
            donotHideSoftKeyboard = true;

        }

        @Override
        public void didSendingReadMessageToServer(BaseChatItem baseChatItem) {
            chatAdapter.updateSendeeLastMessageStateToRead();
            presenter.sendingReadMessageUsingLinPhone(baseChatItem, userItem);
        }

        @Override
        public void didSendingReadMessageToServerError(int errorCode, String errorMessage) {
            Logger.e(TAG, errorCode + " : " + errorMessage);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void didLoadChatHistory(ArrayList<BaseChatItem> chatItems) {
            boolean needScroolToTheEnd = false;
            if (chatAdapter.getItemCount() == 0) {
                needScroolToTheEnd = true;
            }
            chatAdapter.addDataFromBeginning(chatItems);

            if (needScroolToTheEnd) {
                recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
            updateStateLastMessage();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void didLoadChatHistoryError(int errorCode, String errorMessage) {

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

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        public int dy;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            this.dy = dy;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!donotHideSoftKeyboard && dy != 0) {
                    if (scrollDown(dy)) {
                        slideDownCustomActionheaderInChat();
                    } else {
                        slideUpCustomActionheaderInChat();
                    }
                }
                if (donotHideSoftKeyboard) {
                    donotHideSoftKeyboard = false;
                } else {
                    hideSoftInputKeyboard();
                }
            }
        }

        private boolean scrollDown(int dy) {
            return dy < 0;
        }

        private void hideSoftInputKeyboard() {
            View view = ChatActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    };

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
                presenter.loadChatHistory(userItem, getLastMessageId());
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
    }

    private int getLastMessageId() {
        return chatAdapter.getLastMessageID();
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_to_show);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_to_hide);

        presenter = new ChatPresenter(this, mOnChatListener);
        userItem = getIntent().getParcelableExtra(USER);

        initHeader(userItem.getUsername());
        EventBus.getDefault().register(this);

        chatAdapter = new ChatAdapter(this, new ArrayList<BaseChatItem>());
        LayoutManager layoutManager = new LayoutManager(ChatActivity.this);

        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        updateTopPaddingRecycle();

        presenter.loadChatHistory(userItem, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        updateStateLastMessage();

    }

    private void slideDownCustomActionheaderInChat() {
        if (!isCustomActionHeaderInChatOpened) {
            customActionHeaderInChat.startAnimation(slideDown);
        }
        isCustomActionHeaderInChatOpened = true;
        updateTopPaddingRecycle();
    }

    private void slideUpCustomActionheaderInChat() {
        if (isCustomActionHeaderInChatOpened) {
            customActionHeaderInChat.startAnimation(slideUp);
        }
        isCustomActionHeaderInChatOpened = false;
        updateTopPaddingRecycle();
    }

    private void updateStateLastMessage() {
        if (chatAdapter != null) {
            BaseChatItem lastSenderMessage = chatAdapter.getLastSendeeUnreadMessage();
            if (lastSenderMessage != null) {
                presenter.sendingReadMessageToServer(lastSenderMessage);
            }
        }
    }

    public static void start(Context context, UserItem userItem) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(USER, (Parcelable) userItem);
        context.startActivity(starter);
    }

    @Subscribe()
    public void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent) {
        BaseChatItem chatItem = newChatMessageEvent.getBaseChatItem();
        if (presenter.isMessageOfCurrentUser(chatItem.getOwner(), userItem)) {
            donotHideSoftKeyboard = true;
            chatAdapter.addItemAndHeaderIfNeed(newChatMessageEvent.getBaseChatItem());
            recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            if (isResume) {
                presenter.sendingReadMessageToServer(newChatMessageEvent.getBaseChatItem());
            }
        }
    }

    @Subscribe()
    public void onStateMessageChange(final ReceivingReadMessageEvent receivingReadMessageEvent) {
        chatAdapter.updateOwnerStateMessageToRead(receivingReadMessageEvent.getBaseChatItem());
    }

    @OnClick({R.id.action_phone, R.id.action_video, R.id.txt_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_phone:
                break;
            case R.id.action_video:
                break;
            case R.id.txt_send:
                doSendMessage();
                break;
            default:
                break;
        }
    }

    private void doSendMessage() {
        String newMessage = edtChat.getText().toString();
        if (!"".equalsIgnoreCase(newMessage)) {
            donotHideSoftKeyboard = true;
            edtChat.setText("");
            presenter.sendText(newMessage, userItem);
        }
    }
}
