package jp.newbees.mastersip.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.FooterDialogEvent;
import jp.newbees.mastersip.event.call.BusyCallEvent;
import jp.newbees.mastersip.footerdialog.FooterManager;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.CallMaker;
import jp.newbees.mastersip.presenter.CallPresenter;
import jp.newbees.mastersip.ui.call.IncomingVideoChatActivity;
import jp.newbees.mastersip.ui.call.IncomingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.IncomingVoiceActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoChatActivity;
import jp.newbees.mastersip.ui.call.OutgoingVideoVideoActivity;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
import jp.newbees.mastersip.ui.dialog.NotifyRunOutOfCoinDialog;
import jp.newbees.mastersip.ui.dialog.OneButtonDialog;
import jp.newbees.mastersip.ui.dialog.SelectVideoCallDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.payment.PaymentActivity;
import jp.newbees.mastersip.ui.payment.PaymentFragment;
import jp.newbees.mastersip.ui.profile.ProfileDetailItemActivity;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.FileUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 4/3/17.
 */

public abstract class CallActivity extends BaseActivity implements CallPresenter.CallView,
        TextDialog.OnTextDialogPositiveClick,
        CallMaker, SelectVideoCallDialog.OnSelectVideoCallDialog,
        NotifyRunOutOfCoinDialog.NotifyRunOutOfCoinDialogClick, OneButtonDialog.OneButtonDialogClickListener {

    private static final int CONFIRM_REQUEST_ENABLE_VOICE_CALL = 10;
    private static final int CONFIRM_REQUEST_ENABLE_VIDEO_CALL = 11;
    private static final int CONFIRM_MAKE_VIDEO_CALL = 12;
    private static final int CONFIRM_MAKE_VOICE_CALL = 13;
    protected static final int REQUEST_BUY_POINT = 15;
    private static final int REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL = 99;
    private static final int REQUEST_DOWNLOAD_IMAGE = 31;
    private CallPresenter presenter;
    private boolean isMessageDialogShowing;
    private UserItem callee;
    private UserItem currentProfileShowing;
    private boolean fromProfileDetail;
    private BroadcastReceiver wifiBroadcastReceiver;
    private View rootView;

    private View footerDialog;
    private View contentFooterDialog;
    private View paddingView;
    private TextView txtContentFooterDialog;

    private ImageView imgIconFooterDialog;
    private Animation showFooterDialogAnim;
    private Animation hideFooterDialogAnim;
    private boolean isShowingFooterDialog = true;

    private int minPoint;

    public interface ImageDownloadable {
        void requestDownloadImage();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CallPresenter(this.getApplicationContext(), this);
        prepareToShowFooterDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.registerCallEvent();
        this.registerWifiStateChange();
        FooterManager.changeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unregisterCallEvent();
        this.unregisterReceiver(wifiBroadcastReceiver);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.root_view_with_footer_dialog, null);
        ViewGroup containerContent = (ViewGroup) rootView.findViewById(R.id.main_content);
        View contentView = layoutInflater.inflate(layoutResID, null);
        containerContent.addView(contentView);
        super.setContentView(rootView);
    }

    private void registerWifiStateChange() {
        wifiBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null && info.isConnected()) {
                    Logger.e(CallActivity.this.getClass().getSimpleName(), "Start Service");
                    LinphoneService.startLinphone(getApplicationContext());
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, intentFilter);
    }

    /**
     * On Dialog notify not enough point positive clicked
     *
     * @param requestCode
     */
    @Override
    public void onTextDialogOkClick(int requestCode) {
        switch (requestCode) {
            case CONFIRM_REQUEST_ENABLE_VOICE_CALL:
                showLoading();
                presenter.sendMessageRequestEnableSettingCall(callee, Constant.API.VOICE_CALL);
                break;
            case CONFIRM_REQUEST_ENABLE_VIDEO_CALL:
                showLoading();
                presenter.sendMessageRequestEnableSettingCall(callee, Constant.API.VIDEO_CALL);
                break;
            case CONFIRM_MAKE_VIDEO_CALL:
                SelectVideoCallDialog.openDialog(getSupportFragmentManager());
                break;
            case CONFIRM_MAKE_VOICE_CALL:
                this.presenter.checkVoiceCall(callee);
                break;
            case REQUEST_BUY_POINT:
                handleBuyPoint();
                break;
            case REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL:
                isMessageDialogShowing = false;
                showMessageDialog(getString(R.string.call_ended));
                break;
            case REQUEST_DOWNLOAD_IMAGE:
                if (this instanceof ImageDownloadable) {
                    ((ImageDownloadable) this).requestDownloadImage();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Do not call this method directly
     *
     * @param callee
     * @param callID
     */
    @Override
    public final void outgoingVoiceCall(UserItem callee, String callID) {
        OutgoingVoiceActivity.startActivity(this, callee, callID);
    }

    /**
     * Do not call this method directly
     *
     * @param callee
     * @param callID
     */
    @Override
    public final void outgoingVideoCall(UserItem callee, String callID) {
        OutgoingVideoVideoActivity.startActivity(this, callee, callID);
    }

    /**
     * Do not call this method directly
     *
     * @param callee
     * @param callID
     */
    @Override
    public final void outgoingVideoChatCall(UserItem callee, String callID) {
        OutgoingVideoChatActivity.startActivity(this, callee, callID);
    }

    /**
     * Do not call this method directly
     *
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public final void didConnectCallError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    /**
     * Do not call this method directly
     *
     * @param busyCallEvent
     */
    @Override
    public final void onCalleeRejectCall(BusyCallEvent busyCallEvent) {
        String message = callee.getUsername() + getString(R.string.mess_callee_reject_call);
        String positiveTitle = getString(R.string.back_to_profile_detail);
        OneButtonDialog.showDialog(getSupportFragmentManager(), "", message, "", positiveTitle);
    }

    @Override
    public final void didCheckCallError(int errorCode, String errorMessage) {
        if (errorCode == Constant.Error.USER_BUSY) {
            showMessageDialog(callee.getUsername() + getString(R.string.mess_user_busy));
        } else {
            showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
        }
    }

    @Override
    public final void didUserNotEnoughPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        String title, content, positiveTitle;
        if (gender == UserItem.MALE) {
            title = getString(R.string.point_are_missing);
            content = getString(R.string.mess_suggest_buy_point);
            positiveTitle = getString(R.string.add_point);
        } else {
            title = getString(R.string.partner_point_are_missing);
            content = callee.getUsername() + getString(R.string.mess_suggest_missing_point_for_girl);
            positiveTitle = getString(R.string.to_attack);
        }
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_BUY_POINT)
                .setPositiveTitle(positiveTitle)
                .setTitle(title)
                .build(content);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    @Override
    public final void didCallHangUpForGirl() {
        if (isMessageDialogShowing) {
            return;
        }
        showMessageDialog(getString(R.string.call_ended));
    }

    @Override
    public final void didCoinChangedAfterHangUp(int totalCoinChanged, int currentCoin) {
        if (isMessageDialogShowing) {
            return;
        }
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        if (gender == UserItem.FEMALE && totalCoinChanged > 0) {
            showNotifyCoinEarnedForGirl(totalCoinChanged);
        } else if (gender == UserItem.MALE && currentCoin < Constant.Application.MIN_COIN_FOR_CALL) {
            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
            isMessageDialogShowing = true;
        } else {
            showMessageDialog(getString(R.string.call_ended));
        }
    }

    @Override
    public final void didRunOutOfCoin() {
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
            NotifyRunOutOfCoinDialog.openNotifyRunOutOfCoinDialog(getSupportFragmentManager());
            isMessageDialogShowing = true;
        }
    }

    @Override
    public final void didAdminHangUpCall() {
        Logger.e(TAG, "did Admin Hangup Call");
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_SHOW_MESSAGE_DIALOG_AFTER_ADMIN_HANG_UP_CALL)
                .hideNegativeButton(true)
                .build(getString(R.string.mess_admin_hang_up_ca));
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        isMessageDialogShowing = true;
    }

    @Override
    public void didCheckedIncomingVoiceCall(UserItem callUser, String callId) {
        IncomingVoiceActivity.startActivity(this, callUser, callId);
    }

    @Override
    public void didCheckedIncomingVideoCall(UserItem callUser, String callId) {
        IncomingVideoVideoActivity.startActivity(this, callUser, callId);
    }

    @Override
    public void didCheckedIncomingVideoChatCall(UserItem callUser, String callId) {
        IncomingVideoChatActivity.startActivity(this, callUser, callId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BUY_POINT && resultCode == RESULT_OK) {
            showDialogBuyPointSuccess(data);
        }
    }

    @Override
    public final void callVideo(UserItem callee, boolean fromProfileDetail) {
        this.callee = callee;
        this.fromProfileDetail = fromProfileDetail;
        TextDialog textDialog;
        if (callee.getSettings().getVideoCall() == SettingItem.OFF) {
            String content = callee.getUsername() + getString(R.string.mr)
                    + getString(R.string.confirm_request_enable_video_call);
            String positive = getResources().getString(R.string.confirm_request_enable_video_call_positive);
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_REQUEST_ENABLE_VIDEO_CALL)
                    .setPositiveTitle(positive)
                    .build(content);
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        } else {
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_MAKE_VIDEO_CALL)
                    .build(getString(R.string.are_you_sure_make_a_video_call));
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        }
    }

    @Override
    public final void callVoice(UserItem callee, boolean fromProfileDetail) {
        this.callee = callee;
        this.fromProfileDetail = fromProfileDetail;
        TextDialog textDialog;
        if (callee.getSettings().getVoiceCall() == SettingItem.OFF) {
            String content = callee.getUsername() + getString(R.string.mr)
                    + getResources().getString(R.string.confirm_request_enable_voice_call);
            String positive = getResources().getString(R.string.confirm_request_enable_voice_call_positive);
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_REQUEST_ENABLE_VOICE_CALL)
                    .setPositiveTitle(positive)
                    .build(content);
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        } else {
            textDialog = new TextDialog.Builder()
                    .setRequestCode(CONFIRM_MAKE_VOICE_CALL)
                    .build(getString(R.string.are_you_sure_make_a_voice_call));
            textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
        }

    }

    @Override
    public void chat(UserItem chatter) {
        ChatActivity.startChatActivity(this, chatter);
    }

    @Override
    public void gotoProfileFromActivity(UserItem userItem) {
        if (!this.fromProfileDetail ||
                this.currentProfileShowing == null ||
                !userItem.getUserId().equalsIgnoreCase(this.currentProfileShowing.getUserId())) {
            ProfileDetailItemActivity.startActivity(this, userItem);
        }
    }

    @Override
    public void onSelectedVideoCall(SelectVideoCallDialog.VideoCall videoCall) {
        if (videoCall == SelectVideoCallDialog.VideoCall.VIDEO_VIDEO) {
            this.presenter.checkVideoCall(callee);
        } else {
            this.presenter.checkVideoChatCall(callee);
        }
    }

    @Override
    public void onBuyCoinClick() {
        // Redirect to buy point screen
        PaymentActivity.startActivityForResult(this, REQUEST_BUY_POINT);
    }

    @Override
    public void onCancelBuyCoinClick() {
        isMessageDialogShowing = false;
        showMessageDialog(getString(R.string.call_ended));
    }

    @Override
    public void didSendMsgRequestEnableSettingCall(int type) {
        disMissLoading();
    }

    @Override
    public void didSendMsgRequestEnableSettingCallError(String errorMessage, int errorCode) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void onHasFooterDialogEvent(FooterDialogEvent footerDialogEvent) {
        FooterManager.getInstance(this).add(footerDialogEvent);
    }

    @Override
    public void onOneButtonPositiveClick() {
        this.gotoProfileFromActivity(callee);
    }

    private void showNotifyCoinEarnedForGirl(int total) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.call_ended_bonus_point))
                .append(total)
                .append(getString(R.string.pt))
                .append(getString(R.string.i_acquired_it));
        showMessageDialog(message.toString());
    }

    @Override
    public void setShowingProfile(UserItem userItem) {
        this.currentProfileShowing = userItem;
    }

    @Override
    public void onChangeBadgeEvent(int type, int badge) {
        changeBadge(type, badge);
    }

    private void showDialogBuyPointSuccess(Intent data) {
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.settlement_is_completed))
                .append("\n")
                .append(data.getStringExtra(PaymentFragment.POINT))
                .append(getString(R.string.pt))
                .append(getString(R.string.have_been_granted));
        showMessageDialog(message.toString());
    }

    private void changeBadge(int type, int badge) {
        switch (type) {
            case Constant.FOOTER_DIALOG_TYPE.FOOT_PRINT:
                setBudgieFootPrint(badge);
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOLLOW:
                setBudgieFollower(badge);
                break;
            case Constant.FOOTER_DIALOG_TYPE.ONLINE_NOTIFY:
                setBadgeOnline(badge);
                break;
            case Constant.FOOTER_DIALOG_TYPE.MY_MENU:
                setBadgeUserOnlineNotify(badge);
                break;
        }
    }

    /**
     * need call before showFooterDialog method to android draw view
     * @param footerDialogEvent
     */
    public void fillDataToFooterDialog(final FooterDialogEvent footerDialogEvent) {

        txtContentFooterDialog.setText(footerDialogEvent.getMessage());
        imgIconFooterDialog.setImageResource(footerDialogEvent.getIconResourceId());
        contentFooterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(footerDialogEvent);
                hideFooterDialog();
            }
        });
    }

    private void prepareToShowFooterDialog() {
        footerDialog = rootView.findViewById(R.id.footer_dialog_layout);
        contentFooterDialog = footerDialog.findViewById(R.id.content_footer_dialog);
        paddingView = footerDialog.findViewById(R.id.padding_view);

        showFooterDialogAnim = AnimationUtils.loadAnimation(this, R.anim.show_footer_dialog);
        hideFooterDialogAnim = AnimationUtils.loadAnimation(this, R.anim.hide_footer_dialog);
        txtContentFooterDialog = (TextView) footerDialog.findViewById(R.id.txt_content);
        imgIconFooterDialog = (ImageView) footerDialog.findViewById(R.id.img_icon);

        addConstrainToFooterDialog();
        hideFooterDialog();
    }



    public void showFooterDialog(final FooterDialogEvent footerDialogEvent) {
        isShowingFooterDialog = true;

        updateViewToRedrawFooterDialog(footerDialogEvent.getMessage());

        contentFooterDialog.setClickable(true);
        footerDialog.setVisibility(View.VISIBLE);
        footerDialog.startAnimation(showFooterDialogAnim);
        hideFooterDialogAfter(FooterManager.SHOW_TIME + FooterManager.ANIM_TIME);
    }

    private void updateViewToRedrawFooterDialog(String msg){
        txtContentFooterDialog.setText(msg);
    }

    private void hideFooterDialogAfter(int timeDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFooterDialog();
            }
        }, timeDelay);
    }

    public void addConstrainToFooterDialog() {
        if (this instanceof ChatActivity) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) paddingView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.height_footer_chat));
        } else {
            addConstrainWhenKeyboardShowing();
        }
    }

    private void addConstrainWhenKeyboardShowing() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Window mRootWindow = getWindow();
                View view = mRootWindow.getDecorView();
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int screenHeight = view.getHeight();
                int keyboardHeight = screenHeight - (rect.bottom - rect.top);

                int offsetNavigationSystemBar = screenHeight - rootView.getHeight();

                int[] viewPositionsInScreen = new int[2];
                rootView.getLocationInWindow(viewPositionsInScreen);

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int offsetSystemStatusBar = dm.heightPixels - rootView.getMeasuredHeight();

                // update margin layout footer dialog
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) paddingView.getLayoutParams();
                if (keyboardHeight > screenHeight / 3) {
                    layoutParams.setMargins(0, 0, 0, keyboardHeight - offsetNavigationSystemBar + (viewPositionsInScreen[1] - offsetSystemStatusBar));
                } else {
                    layoutParams.setMargins(0, 0, 0, 0);
                }
            }
        });
    }

    private void redirect(FooterDialogEvent footerDialogEvent) {
        switch (footerDialogEvent.getType()) {
            case Constant.FOOTER_DIALOG_TYPE.SEND_GIFT:
            case Constant.FOOTER_DIALOG_TYPE.CHAT_TEXT:
                ChatActivity.startChatActivity(CallActivity.this, footerDialogEvent.getCompetitor());
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOLLOW:
                TopActivity.navigateToFragment(this, TopActivity.FOLLOW_FRAGMENT);
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOOT_PRINT:
                TopActivity.navigateToFragment(this, TopActivity.FOOT_PRINT_FRAGMENT);
                break;
            default:
                break;
        }
    }

    private void hideFooterDialog() {
        if (!isShowingFooterDialog) {
            return;
        }
        isShowingFooterDialog = false;
        contentFooterDialog.setClickable(false);
        clearViewAnimation(footerDialog, hideFooterDialogAnim, View.INVISIBLE);
        footerDialog.startAnimation(hideFooterDialogAnim);
    }

    protected void showConfirmDownloadImageDialog(int type) {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        minPoint = type == Constant.API.DOWN_IMAGE_CHAT
                ? ConfigManager.getInstance().getMinPointDownImageChat()
                : ConfigManager.getInstance().getMinPointDownImageGallery();

        String title = getString(R.string.save_image);
        String content = currentUser.isMale()
                ? String.format(getString(R.string.mess_confirm_down_image_for_male), minPoint)
                : getString(R.string.mess_confirm_down_image_for_female);

        TextDialog textDialog = new TextDialog.Builder()
                .setTitle(title)
                .setRequestCode(REQUEST_DOWNLOAD_IMAGE)
                .build(content);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    protected UserItem getCurrentCallee() {
        return this.callee;
    }

    protected void handleBuyPoint() {
        int gender = ConfigManager.getInstance().getCurrentUser().getGender();
        if (gender == UserItem.MALE) {
            PaymentActivity.startActivityForResult(this, REQUEST_BUY_POINT);
        } else {
            ChatActivity.startChatActivity(this, callee);
        }
    }

    protected void handleDownloadImage(final String imageUrl) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.downloadImageFromUrl(CallActivity.this, imageUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        disMissLoading();
                        // Show dialog download image success
                        showMessageDialog(getString(R.string.mess_down_image_success));
                    }
                });
            }
        }).start();
    }

    protected void showDialogMissingPoint() {
        // Missing point when download image

        String title = getString(R.string.mess_missing_point);
        String content = String.format(getString(R.string.mess_request_buy_point_when_down_image), minPoint);
        String positiveTitle = getString(R.string.add_point);
        TextDialog textDialog = new TextDialog.Builder()
                .setRequestCode(REQUEST_BUY_POINT)
                .setPositiveTitle(positiveTitle)
                .setTitle(title)
                .build(content);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }
}
