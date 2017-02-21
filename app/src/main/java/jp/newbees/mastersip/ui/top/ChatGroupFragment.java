package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatGroupAdapter;
import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.ChatGroupPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 12/22/16.
 */

public class ChatGroupFragment extends BaseFragment implements ChatGroupPresenter.ChatGroupView, ChatGroupAdapter.OnItemClickListener {

    @BindView(R.id.txt_action_bar_title)
    TextView txtHeaderTitle;
    @BindView(R.id.recycler_chat_group)
    RecyclerView recyclerChatGroup;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layout_introduce)
    LinearLayout layoutIntroduce;
    @BindView(R.id.txt_message)
    TextView txtMessage;
    @BindView(R.id.txt_message_2)
    TextView txtMessage2;

    private ChatGroupPresenter presenter;
    private ChatGroupAdapter chatGroupAdapter;

    private boolean isLoading;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItem;

    public static Fragment newInstance() {
        Fragment fragment = new ChatGroupFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.chat_group_fragment;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        presenter = new ChatGroupPresenter(getContext(), this);
        initRecyclerChatGroup();
        txtHeaderTitle.setText(getString(R.string.chat_list));

        showLoading();
        presenter.loadListRoom();
        // Swipe to refresh data
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadListRoom();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        presenter.loadListRoom();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btn_go)
    public void onClick(View view) {
        if (view.getId() == R.id.btn_go) {
            ((TopActivity) getActivity()).showSearchFragment();
        }
    }

    @Override
    public void didLoadChatRoom(List<RoomChatItem> roomChatItems) {
        chatGroupAdapter.clearData();
        chatGroupAdapter.addAll(roomChatItems);
        swipeRefreshLayout.setRefreshing(false);
        updateUI();
        disMissLoading();
    }

    @Override
    public void didLoadChatRoomFailure(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
        swipeRefreshLayout.setRefreshing(false);
        disMissLoading();
        isLoading = false;
    }

    @Override
    public void didLoadMoreChatRoom(List<RoomChatItem> roomChatItems) {
        chatGroupAdapter.addAll(roomChatItems);
        isLoading = false;
        disMissLoading();
    }

    @Override
    public void onRoomChatItemClick(RoomChatItem item, int position) {
        ChatActivity.startChatActivity(getContext(), item.getUserChat());
    }

    @Subscribe()
    public void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent) {
        Logger.e(TAG, "ChatMessageEvent: " + newChatMessageEvent.toString());
        presenter.loadListRoom();
    }

    private void updateUI() {
        boolean isFirstTimeChatting = ConfigManager.getInstance().getFirstTimeChattingFlag();

        if (chatGroupAdapter.getItemCount() == 0 && isFirstTimeChatting) {
            layoutIntroduce.setVisibility(View.VISIBLE);

            SpannableString message = new SpannableString(getString(R.string.there_is_no_conversation_yet));
            message.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.sip_red)), 13, 15, 0);
            txtMessage.setText(message, TextView.BufferType.SPANNABLE);

            initMessageByGender();
        } else if (chatGroupAdapter.getItemCount() > 0 && !isFirstTimeChatting) {
            saveFirstTimeChattingFlag();
        }
    }

    private void initMessageByGender() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        SpannableString message;
        if (gender == UserItem.MALE) {
            message = new SpannableString(getString(R.string.mess_chat_room_for_boy));
            message.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.sip_red))
                    , 3, 7, 0);
            message.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.sip_red))
                    , 8, 13, 0);
        } else {
            message = new SpannableString(getString(R.string.mess_chat_room_for_girl));
            message.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.sip_red))
                    , 15, 19, 0);
            message.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.sip_red))
                    , 20, 22, 0);
        }

        txtMessage2.setText(message, TextView.BufferType.SPANNABLE);
    }

    private void saveFirstTimeChattingFlag() {
        ConfigManager.getInstance().saveFirstTimeChattingFlag();
    }

    private void initRecyclerChatGroup() {
        chatGroupAdapter = new ChatGroupAdapter(getActivity().getApplicationContext(), new ArrayList<RoomChatItem>());
        chatGroupAdapter.setOnItemClickListener(this);
        recyclerChatGroup.setAdapter(chatGroupAdapter);

        addScrollToLoadMoreRecyclerView();
    }

    private void addScrollToLoadMoreRecyclerView() {
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerChatGroup.getLayoutManager();
        recyclerChatGroup.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0
                            && !isLoading && presenter.hasMoreData()) {
                        isLoading = true;
                        showLoading();
                        presenter.loadMoreRoom();
                    }
                }
            }
        });
    }
}
