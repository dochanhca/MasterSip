package jp.newbees.mastersip.ui.chatting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tonicartos.superslim.LayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.NavigationLayoutChild;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.customviews.SoftKeyboardLsnedRelativeLayout;
import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.eventbus.ReceivingReadMessageEvent;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.RelationshipItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.LoadChatHistoryResultItem;
import jp.newbees.mastersip.presenter.top.ChatPresenter;
import jp.newbees.mastersip.ui.call.CallCenterActivity;
import jp.newbees.mastersip.ui.dialog.ConfirmSendGiftDialog;
import jp.newbees.mastersip.ui.dialog.ConfirmVoiceCallDialog;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.gift.ListGiftActivity;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.ImageFilePath;
import jp.newbees.mastersip.utils.ImageUtils;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.Utils;

import static jp.newbees.mastersip.ui.dialog.SelectImageDialog.AVATAR_NAME;
import static jp.newbees.mastersip.ui.dialog.SelectImageDialog.PICK_AVATAR_CAMERA;
import static jp.newbees.mastersip.ui.dialog.SelectImageDialog.PICK_AVATAR_GALLERY;

/**
 * Created by thangit14 on 1/9/17.
 */
public class ChatActivity extends CallCenterActivity implements ConfirmVoiceCallDialog.OnDialogConfirmVoiceCallClick, ConfirmSendGiftDialog.OnConfirmSendGiftDialog, ChatAdapter.OnItemClickListener {
    private static final String USER = "USER";
    public static final String TAG = "ChatActivity";

    public static final int NAV_PROFILE = 0;
    public static final int NAV_GALLERY = 1;
    public static final int NAV_GIFT = 2;
    public static final int NAV_FOLLOW = 3;

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
    @BindView(R.id.swap_layout)
    LinearLayout swapRecycleChatLayout;
    @BindView(R.id.container)
    SoftKeyboardLsnedRelativeLayout container;
    @BindView(R.id.layout_select_image)
    LinearLayout layoutSelectImage;
    @BindView(R.id.rl_open_camera)
    RelativeLayout rlOpenCamera;
    @BindView(R.id.rl_open_gallery)
    RelativeLayout rlOpenGallery;
    @BindView(R.id.img_left_bottom_action)
    ImageView imgLeftBottomAction;
    @BindView(R.id.nav_follow)
    NavigationLayoutChild navFollow;

    private UIMode uiMode = UIMode.INPUT_TEXT_MODE;

    private ChatAdapter chatAdapter;
    private ChatPresenter presenter;

    private UserItem userItem;
    private Animation slideDown;
    private Animation slideUp;

    private volatile boolean donotHideSoftKeyboard = true;
    private boolean isCustomActionHeaderInChatOpened = true;
    private boolean isCallActionHeaderInChatOpened = false;
    private boolean isSoftKeyboardOpened = false;

    private boolean isResume = false;
    private boolean isShowDialogForHandleImage = false;

    private Bitmap bitmap;
    private int maxImageSize = Constant.Application.MAX_IMAGE_SIZE;

    private HashMap<String, UserItem> members;

    private NavigationLayoutGroup.OnChildItemClickListener onCustomActionHeaderInChatClickListener =
            new NavigationLayoutGroup.OnChildItemClickListener() {
        @Override
        public void onChildItemClick(View view, int position) {
            switch (position) {
                case NAV_PROFILE:
                    gotoProfileDetailActivity();
                    break;
                case NAV_GALLERY:
                    ChattingPhotoGalleryActivity.startActivity(ChatActivity.this, userItem.getUserId());
                    break;
                case NAV_GIFT:
                    gotoListGiftActivity();
                    break;
                case NAV_FOLLOW:
                    showLoading();
                    presenter.doFollowUser(presenter.
                            getUserHasRelationShipItem(userItem, members));
                    break;
                default:
                    break;
            }
        }
    };

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
                slideUpCustomActionHeaderInChat();
            }
        }
    };

    private SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner softKeyboardListener = new SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner() {
        @Override
        public void onSoftKeyboardShow() {
            isSoftKeyboardOpened = true;
            slideUpCustomActionHeaderInChat();
            if (uiMode == UIMode.SELECT_IMAGE_MODE) {
                switchUIMode();
            }
        }

        @Override
        public void onSoftKeyboardHide() {
            isSoftKeyboardOpened = false;
            slideDownCustomActionHeaderInChat();
        }
    };


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
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void didLoadChatHistory(LoadChatHistoryResultItem resultItem) {
            boolean needScrollToTheEnd = false;
            members = resultItem.getMembers();
            if (chatAdapter.getItemCount() == 0) {
                needScrollToTheEnd = true;
                updateFollowView(presenter
                        .getUserHasRelationShipItem(userItem,members)
                        .getRelationshipItem());
            }
            chatAdapter.addDataFromBeginning(resultItem.getBaseChatItems());

            if (needScrollToTheEnd) {
                recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
            updateStateLastMessage();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void didLoadChatHistoryError(int errorCode, String errorMessage) {
            showToastExceptionVolleyError(ChatActivity.this,errorCode,errorMessage);

        }

        @Override
        public void didUploadImageToServer(ImageChatItem imageChatItem) {
            disMissLoading();
            isShowDialogForHandleImage = false;

            chatAdapter.addItemAndHeaderIfNeed(imageChatItem);
            recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }

        @Override
        public void didUploadImageToServerError(int errorCode, String errorMessage) {
            disMissLoading();
            showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
            isShowDialogForHandleImage = false;
        }

        @Override
        public void didFollowUser() {
            disMissLoading();
            updateFollowView(presenter.setUserHasRelationShipItem(userItem,members,RelationshipItem.FOLLOW));
            ConfirmSendGiftDialog.openConfirmSendGiftDialog(getSupportFragmentManager(), userItem.getUsername());
        }

        @Override
        public void didFollowUserError(String errorMessage, int errorCode) {
            disMissLoading();
            showToastExceptionVolleyError(ChatActivity.this,errorCode,errorMessage);
        }

        @Override
        public void didUnFollowUser() {
            disMissLoading();
            updateFollowView(presenter.setUserHasRelationShipItem(userItem,members,RelationshipItem.UN_FOLLOW));

            StringBuilder content = new StringBuilder();
            content.append(userItem.getUsername()).append(getString(R.string.notify_un_follow_user_success));

            showMessageDialog(getString(R.string.mess_un_followed), content.toString(), "", false);
        }

        @Override
        public void didUnFollowUserError(String errorMessage, int errorCode) {
            disMissLoading();
            showToastExceptionVolleyError(ChatActivity.this,errorCode,errorMessage);
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
        private int dy;

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
                        slideDownCustomActionHeaderInChat();
                    } else {
                        slideUpCustomActionHeaderInChat();
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

    private View.OnClickListener mOnHeaderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gotoProfileDetailActivity();
        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_chat;
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
        container.setListener(softKeyboardListener);

        customActionHeaderInChat.setOnChildItemClickListener(onCustomActionHeaderInChatClickListener);
    }

    private int getLastMessageId() {
        return chatAdapter.getLastMessageID();
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_to_show);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_to_hide);

        presenter = new ChatPresenter(this, mOnChatListener);
        userItem = getIntent().getParcelableExtra(USER);

        initHeader(userItem.getUsername(), mOnHeaderClickListener);
        EventBus.getDefault().register(this);

        chatAdapter = new ChatAdapter(this, new ArrayList<BaseChatItem>());
        chatAdapter.setOnItemClickListener(this);
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
        if (isShowDialogForHandleImage) {
            showLoading();
        }
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

    @OnClick({R.id.action_phone, R.id.action_video, R.id.txt_send, R.id.img_left_bottom_action,
            R.id.rl_open_gallery, R.id.rl_open_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_phone:
                ConfirmVoiceCallDialog.openConfirmVoiceCallDialog(getSupportFragmentManager());
                break;
            case R.id.action_video:
                break;
            case R.id.txt_send:
                doSendMessage();
                break;
            case R.id.img_left_bottom_action:
                switchUIMode();
                break;
            case R.id.rl_open_gallery:
                openGallery();
                break;
            case R.id.rl_open_camera:
                openCamera();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SelectImageDialog.PICK_AVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    isShowDialogForHandleImage = true;
                    handleImageFromCamera();
                }
                break;
            case SelectImageDialog.PICK_AVATAR_GALLERY:
                if (resultCode == RESULT_OK) {
                    handleImageFromGallery(data.getData());
                    isShowDialogForHandleImage = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onOkVoiceCallClick() {
        presenter.checkVoiceCall(userItem);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        donotHideSoftKeyboard = true;
        chatAdapter.clearData();
        presenter.loadChatHistory(userItem, 0);
    }

    @Override
    public void onOkConfirmSendGiftClick() {
        gotoListGiftActivity();
    }

    @Override
    public void onImageClick(int position) {
        ImageChatItem imageChatItem = (ImageChatItem) chatAdapter.getData().get(position);
        ChatImageDetailActivity.startActivity(this, imageChatItem);
    }

    @Override
    public void onFriendAvatarClick() {
        gotoProfileDetailActivity();
    }

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

    private void slideDownCustomActionHeaderInChat() {
        if (!isCustomActionHeaderInChatOpened) {
            customActionHeaderInChat.startAnimation(slideDown);
        }
        isCustomActionHeaderInChatOpened = true;
        updateTopPaddingRecycle();
    }

    private void slideUpCustomActionHeaderInChat() {
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

    /**
     * start new instance of ChatActivity
     * @param context
     * @param userItem
     */
    public static void startChatActivity(Context context, UserItem userItem) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(USER, (Parcelable) userItem);
        context.startActivity(starter);
    }

    private void doSendMessage() {
        String newMessage = edtChat.getText().toString();
        if (!"".equalsIgnoreCase(newMessage)) {
            donotHideSoftKeyboard = true;
            edtChat.setText("");
            presenter.sendText(newMessage, userItem);
        }
    }

    private void openCamera() {
        String path = Environment.getExternalStorageDirectory() + AVATAR_NAME;
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(takePicture, PICK_AVATAR_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_AVATAR_GALLERY);
        }
    }

    private void handleImageFromCamera() {
        File outFile = new File(Environment.getExternalStorageDirectory() + SelectImageDialog.AVATAR_NAME);
        if (!outFile.exists()) {
            Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
        } else {
            bitmap = ImageUtils.decodeBitmapFromFile(outFile.getPath(),
                    maxImageSize, maxImageSize);
            uploadImageToServer();
        }
    }

    private void uploadImageToServer() {
        if (null == bitmap) {
            return;
        }
        InputStream file = ImageUtils.convertToInputStream(bitmap);

        showLoading();
        presenter.sendFile(userItem.getSipItem().getExtension(), Constant.API.TYPE_UPLOAD_IMAGE,
                file);
    }

    private void handleImageFromGallery(Uri pickedImage) {
        getImageFilePath(pickedImage);
    }

    private void getImageFilePath(Uri pickedImage) {
        String imagePath;
        // Get Image path from Google photo
        if (pickedImage.toString().startsWith("content://com.google.android.apps.photos.content")) {
            imagePath = ImageFilePath.getPath(this,
                    ImageUtils.getImageUrlWithAuthority(this, pickedImage));

            bitmap = ImageUtils.decodeBitmapFromFile(imagePath, maxImageSize, maxImageSize);
            bitmap = ImageUtils.rotateBitmap(bitmap, imagePath);
        } else {
            imagePath = ImageFilePath.getPath(this, pickedImage);
            bitmap = ImageUtils.decodeBitmapFromFile(imagePath, maxImageSize, maxImageSize);
        }
        uploadImageToServer();
    }

    private void switchUIMode() {
        if (uiMode == UIMode.INPUT_TEXT_MODE) {
            imgLeftBottomAction.setImageResource(R.drawable.ic_keyboard_green);
            layoutSelectImage.setVisibility(View.VISIBLE);
            hideSoftKeyboard();
            uiMode = UIMode.SELECT_IMAGE_MODE;
        } else if (uiMode == UIMode.SELECT_IMAGE_MODE) {
            imgLeftBottomAction.setImageResource(R.drawable.img_capture);
            edtChat.requestFocus();
            layoutSelectImage.setVisibility(View.GONE);
            showSoftKeyboard();
            uiMode = UIMode.INPUT_TEXT_MODE;
        }
    }

    private void hideSoftKeyboard() {
        Utils.closeKeyboard(this, edtChat.getWindowToken());
    }

    private void showSoftKeyboard() {
        Utils.showKeyboard(this, edtChat);
    }

    private void gotoProfileDetailActivity() {
        ProfileDetailItemActivity.startActivity(this,userItem);
    }

    private void gotoListGiftActivity() {
        ListGiftActivity.startActivity(this,userItem);
    }

    private enum UIMode {
        INPUT_TEXT_MODE, SELECT_IMAGE_MODE
    }

    private void updateFollowView(RelationshipItem relationshipItem) {
        if (relationshipItem.isFollowed() == RelationshipItem.FOLLOW) {
            navFollow.changeToSelectedTab();
        } else {
            navFollow.changeToNormalTab();
        }
    }
}