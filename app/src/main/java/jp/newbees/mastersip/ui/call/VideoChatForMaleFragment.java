package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.customviews.SoftKeyboardListenedRelativeLayout;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.chatting.BasicChatPresenter;
import jp.newbees.mastersip.presenter.chatting.SendChatTextListener;
import jp.newbees.mastersip.ui.call.base.CallingFragment;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 4/10/17.
 */

public class VideoChatForMaleFragment extends CallingFragment implements SendChatTextListener,
        CallingFragment.CountableToHideAction {

    @BindView(R.id.layout_video_chat_call_action)
    ViewGroup layoutActionButton;
    @BindView(R.id.video_frame)
    ViewGroup videoFrame;
    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.txt_low_signal)
    TextView txtLowSignal;
    @BindView(R.id.videoSurface)
    SurfaceView mVideoView;
    @BindView(R.id.txt_name)
    HiraginoTextView txtName;
    @BindView(R.id.txt_time)
    HiraginoTextView txtTime;
    @BindView(R.id.edt_chat)
    HiraginoEditText edtChat;
    @BindView(R.id.txt_send)
    TextView txtSend;
    @BindView(R.id.btn_on_off_speaker)
    ToggleButton btnOnOffSpeaker;
    @BindView(R.id.ll_show_hide_chat_box)
    LinearLayout llShowHideChatBox;
    @BindView(R.id.rl_chat_input)
    RelativeLayout rlChatInput;
    @BindView(R.id.container)
    SoftKeyboardListenedRelativeLayout container;
    @BindView(R.id.txt_show_hide_chat_box)
    HiraginoTextView txtShowHideChatBox;
    @BindView(R.id.ic_arrow)
    ImageView icArrow;
    @BindView(R.id.btn_cancel_call)
    ImageView btnCancelCall;

    private AndroidVideoWindowImpl androidVideoWindow;
    private UserItem competitor;

    private ChatAdapter chatAdapter;
    private BasicChatPresenter basicChatPresenter;
    private Animation fadeOut;
    private Animation fadeIn;
    private boolean isShowChatBox = true;
    private boolean isShowingView = true;

    public static VideoChatForMaleFragment newInstance(UserItem competitor, String callID) {
        Bundle args = new Bundle();
        args.putParcelable(COMPETITOR, competitor);
        args.putString(CALL_ID, callID);
        VideoChatForMaleFragment fragment = new VideoChatForMaleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_video_chat_call_for_male;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        competitor = getArguments().getParcelable(COMPETITOR);

        basicChatPresenter = new BasicChatPresenter(getContext(), this);
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<BaseChatItem>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        container.setListener(mOnSoftKeyboardListener);

        setupView();
        fixZOrder(mVideoView);
        startCountingToHideAction();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (androidVideoWindow != null) {
            synchronized (androidVideoWindow) {
                setVideoWindow(androidVideoWindow);
            }
        }
        basicChatPresenter.registerCallEvent();
    }

    @Override
    public void onPause() {
        if (androidVideoWindow != null) {
            synchronized (androidVideoWindow) {
                /*
                 * this call will destroy native opengl renderer which is used by
				 * androidVideoWindowImpl
				 */
                setVideoWindow(null);
            }
        }
        super.onPause();
        basicChatPresenter.unregisterCallEvent();
    }

    @Override
    public void onDestroy() {
        if (androidVideoWindow != null) {
            androidVideoWindow.release();
            androidVideoWindow = null;
        }
        super.onDestroy();
    }

    private void showHideChatBox(boolean isShow) {
        if (isShow) {
            icArrow.setImageResource(R.drawable.ic_arrow_down);
            txtShowHideChatBox.setText(R.string.hide_chat_box);
            container.addView(recyclerChat);
        } else {
            icArrow.setImageResource(R.drawable.ic_arrow_up);
            txtShowHideChatBox.setText(R.string.show_chat_box);
            container.removeView(recyclerChat);
        }
        updateConstrain(isShow);
    }

    private void updateConstrain(boolean isShow) {
        if (isShow) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llShowHideChatBox.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.video_frame);
            params.removeRule(RelativeLayout.ABOVE);

            RelativeLayout.LayoutParams videoFrameLayoutParams = (RelativeLayout.LayoutParams) videoFrame.getLayoutParams();
            videoFrameLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.height_video_in_video_chat);
            videoFrameLayoutParams.removeRule(RelativeLayout.ABOVE);

        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llShowHideChatBox.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            params.addRule(RelativeLayout.ABOVE, R.id.rl_chat_input);

            RelativeLayout.LayoutParams videoFrameLayoutParams = (RelativeLayout.LayoutParams) videoFrame.getLayoutParams();
            videoFrameLayoutParams.addRule(RelativeLayout.ABOVE, R.id.ll_show_hide_chat_box);
        }
    }

    private void setupView() {
        bindVideoViewToLinphone();

        txtName.setText(competitor.getUsername());
        enableCamera(false);
    }

    private void bindVideoViewToLinphone() {
        androidVideoWindow = new AndroidVideoWindowImpl(mVideoView, null, new AndroidVideoWindowImpl.VideoWindowListener() {

            @Override
            public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                Logger.e(TAG,"onVideoRenderingSurfaceReady");
                mVideoView = surfaceView;
                setVideoWindow(androidVideoWindow);
                fillVideo();
            }

            @Override
            public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {

            }

            @Override
            public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                Logger.e(TAG, "onVideoPreviewSurfaceReady");

            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {

            }
        });
    }

    @OnClick({R.id.btn_cancel_call, R.id.btn_on_off_speaker, R.id.txt_send, R.id.ll_show_hide_chat_box})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_call:
                terminalCall();
                break;
            case R.id.btn_on_off_speaker:
                enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
            case R.id.txt_send:
                doSendMessage();
                break;
            case R.id.ll_show_hide_chat_box:
                isShowChatBox = !isShowChatBox;
                showHideChatBox(isShowChatBox);
                break;
        }
    }

    @OnTouch(R.id.video_frame)
    public boolean onTouch(View v, MotionEvent event) {
        resetCountingToHideAction();
        showView();
        return true;

    }

    private void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        if (LinphoneHandler.isRunning()) {
            LinphoneHandler.getInstance().setVideoWindow(androidVideoWindow);
        }
    }

    private void fillVideo() {
        float landscapeZoomFactor = ((float) mVideoView.getWidth()) / (float) ((3 * mVideoView.getHeight()) / 4);
        LinphoneHandler.getInstance().zoomVideo(landscapeZoomFactor, 0.5f, 0.5f);
    }

    private void fixZOrder(SurfaceView video) {
        video.setZOrderOnTop(false);
    }

    @Override
    protected void onCallingBreakTime(Message msg) {
        txtTime.setText(DateTimeUtils.getTimerCallString(msg.what));
    }

    @Override
    public TextView getTxtPoint() {
        return null;
    }

    @Override
    public void onCallPaused() {
        txtLowSignal.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUIWhenStartCalling() {
        fillVideo();
        countingCallDuration();
        btnOnOffSpeaker.setChecked(isSpeakerEnalbed());
    }

    @Override
    public final void onCallResume() {
        txtLowSignal.setVisibility(View.INVISIBLE);
    }

    @Override
    public void didSendChatToServer(BaseChatItem baseChatItem) {
        chatAdapter.add(baseChatItem);
        edtChat.setEnabled(true);
        txtSend.setEnabled(true);
        recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    public void didChatError(int errorCode, String errorMessage) {
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    private void doSendMessage() {
        String newMessage = edtChat.getText().toString();
        if (!"".equalsIgnoreCase(newMessage)) {
            edtChat.setText("");
            edtChat.setEnabled(false);
            txtSend.setEnabled(false);
            basicChatPresenter.sendVideoChatText(newMessage, competitor);
        }
    }

    @Override
    public void onBreakTimeToHide() {
        hideView();
    }

    private void showView() {
        if (!isShowingView) {
            isShowingView = true;
            layoutActionButton.startAnimation(fadeIn);
            txtName.startAnimation(fadeIn);
            setViewsClickable(true);
        }
    }

    private void hideView() {
        if (isShowingView) {
            isShowingView = false;
            layoutActionButton.startAnimation(fadeOut);
            txtName.startAnimation(fadeOut);
            setViewsClickable(false);
        }
    }

    private void setViewsClickable(boolean clickable) {
        btnCancelCall.setClickable(clickable);
        btnOnOffSpeaker.setClickable(clickable);
    }

    private SoftKeyboardListenedRelativeLayout.SoftKeyboardLsner mOnSoftKeyboardListener = new SoftKeyboardListenedRelativeLayout.SoftKeyboardLsner() {
        @Override
        public void onSoftKeyboardShow() {
            if (!isShowChatBox) {
                llShowHideChatBox.setVisibility(View.GONE);
            }
            llShowHideChatBox.setEnabled(false);
        }

        @Override
        public void onSoftKeyboardHide() {
            if (!isShowChatBox) {
                llShowHideChatBox.setVisibility(View.VISIBLE);
            }
            llShowHideChatBox.setEnabled(true);
        }
    };
}