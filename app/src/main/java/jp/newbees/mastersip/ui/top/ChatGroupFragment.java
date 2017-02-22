package jp.newbees.mastersip.ui.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatGroupAdapter;
import jp.newbees.mastersip.customviews.OnSwipeTouchListener;
import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.model.RoomChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.top.ChatGroupPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 12/22/16.
 */

public class ChatGroupFragment extends BaseFragment implements ChatGroupPresenter.ChatGroupView,
        ChatGroupAdapter.OnItemClickListener, TextDialog.OnTextDialogClick {

    private static final int REQUEST_CONFIRM_MARK_ALL_MESSAGE_AS_READ = 2;
    private static final int REQUEST_CONFIRM_DELETE_MESSAGE = 3;
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
    @BindView(R.id.txt_edit)
    TextView txtEdit;
    @BindView(R.id.layout_actions)
    LinearLayout layoutActions;
    @BindView(R.id.cb_select_all)
    CheckBox cbSelectAll;
    @BindView(R.id.txt_delete)
    TextView txtDelete;

    private ChatGroupPresenter presenter;
    private ChatGroupAdapter chatGroupAdapter;

    private boolean isLoading;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstVisibleItem;
    private boolean isEditing = false;

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

        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    selectAllChatRoom(isChecked);
                }
                cbSelectAll.setText(isChecked ? getString(R.string.select_all)
                        : getString(R.string.remove_all));
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

    @OnClick({R.id.txt_read_all, R.id.txt_edit, R.id.btn_go, R.id.txt_delete, R.id.cb_select_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go:
                ((TopActivity) getActivity()).showSearchFragment();
                break;
            case R.id.txt_read_all:
                TextDialog.openTextDialog(this, REQUEST_CONFIRM_MARK_ALL_MESSAGE_AS_READ,
                        getFragmentManager(), getString(R.string.would_you_like_mark_all_message_as_read), "");
                break;
            case R.id.txt_edit:
                updateViewWithMode();
                break;
            case R.id.txt_delete:
                TextDialog.openTextDialog(this, REQUEST_CONFIRM_DELETE_MESSAGE,
                        getFragmentManager(), getString(R.string.delete_selected_message), "");
                break;
            case R.id.cb_select_all:
                if (!cbSelectAll.isChecked()) {
                    selectAllChatRoom(false);
                }
            default:
                break;
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
        if (isEditing) {
            item.setSelected(!item.isSelected());
            chatGroupAdapter.notifyDataSetChanged();
            cbSelectAll.setChecked(isAllChatRoomSelected() ? true : false);
        } else {
            ChatActivity.startChatActivity(getContext(), item.getUserChat());
        }
    }

    @Subscribe()
    public void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent) {
        Logger.e(TAG, "ChatMessageEvent: " + newChatMessageEvent.toString());
        presenter.loadListRoom();
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == REQUEST_CONFIRM_MARK_ALL_MESSAGE_AS_READ) {
            // Call API mark all message as read
        } else if (requestCode == REQUEST_CONFIRM_DELETE_MESSAGE) {
            // Call API delete message
        }
    }

    private void updateUI() {
        boolean isFirstTimeChatting = ConfigManager.getInstance().getFirstTimeChattingFlag();

        if (chatGroupAdapter.getItemCount() == 0 && !isFirstTimeChatting) {
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
        listenSwipeLeftToRightEvent();
    }

    private void listenSwipeLeftToRightEvent() {
        recyclerChatGroup.setOnTouchListener(new OnSwipeTouchListener(getActivity().getApplicationContext()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Toast.makeText(getActivity().getApplicationContext(), "slide right", Toast.LENGTH_SHORT).show();
               if (!isEditing) {
                   notifyChatRoomListWithMode();
                   initEditMode();
               }
            }
        });
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
                            && !isLoading && !isEditing && presenter.hasMoreData()) {
                        isLoading = true;
                        showLoading();
                        presenter.loadMoreRoom();
                    }
                }
            }
        });
    }

    private void updateViewWithMode() {
        notifyChatRoomListWithMode();
        if (isEditing) {
            initNonEditMode();
        } else {
            initEditMode();
        }
    }

    private void initEditMode() {
        layoutActions.setVisibility(View.VISIBLE);
        txtEdit.setText(getString(R.string.done));
        swipeRefreshLayout.setEnabled(false);
        isEditing = true;
    }

    private void initNonEditMode() {
        layoutActions.setVisibility(View.GONE);
        txtEdit.setText(getString(R.string.edit));
        cbSelectAll.setChecked(false);
        selectAllChatRoom(false);
        swipeRefreshLayout.setEnabled(true);
        isEditing = false;

    }

    private void notifyChatRoomListWithMode() {
        for (RoomChatItem item : chatGroupAdapter.getData()) {
            item.setShowingCheckbox(isEditing ? false : true);
        }
        chatGroupAdapter.notifyDataSetChanged();
    }

    private void selectAllChatRoom(boolean isSelected) {
        for (RoomChatItem item : chatGroupAdapter.getData()) {
            item.setSelected(isSelected ? true : false);
        }
        chatGroupAdapter.notifyDataSetChanged();
    }

    private boolean isAllChatRoomSelected() {
        boolean selected = true;
        for (RoomChatItem item : chatGroupAdapter.getData()) {
            if (!item.isSelected()) {
                selected = false;
            }
        }
        return selected;
    }
}
