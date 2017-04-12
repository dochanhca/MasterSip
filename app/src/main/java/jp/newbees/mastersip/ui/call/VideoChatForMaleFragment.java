package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tonicartos.superslim.LayoutManager;

import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.ChatAdapter;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.eventbus.ReceivingReadMessageEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.chatting.BasicChatPresenter;
import jp.newbees.mastersip.presenter.chatting.SendChatTextListener;
import jp.newbees.mastersip.ui.call.base.CallingFragment;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by thangit14 on 4/10/17.
 */

public class VideoChatForMaleFragment extends CallingFragment implements SendChatTextListener {

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

    private AndroidVideoWindowImpl androidVideoWindow;

    private UserItem competitor;
    private String callId;

    private ChatAdapter chatAdapter;
    private BasicChatPresenter basicChatPresenter;

    public static VideoChatForMaleFragment newInstance(UserItem competitor, String callID,
                                                boolean enableSpeaker) {
        Bundle args = new Bundle();
        args.putParcelable(COMPETITOR, competitor);
        args.putBoolean(SPEAKER, enableSpeaker);
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

        competitor = getArguments().getParcelable(COMPETITOR);
        callId = getArguments().getString(CALL_ID);

        basicChatPresenter = new BasicChatPresenter(getContext(), this);
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<BaseChatItem>());

        LayoutManager layoutManager = new LayoutManager(getContext());
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

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

    private void setupView() {
        bindVideoViewToLinphone();

        txtName.setText(competitor.getUsername());
        countingCallDuration();

        boolean enableSpeaker = getArguments().getBoolean(SPEAKER);
        btnOnOffSpeaker.setChecked(enableSpeaker);
        enableSpeaker(enableSpeaker);
        enableCamera(false);
    }

    private void bindVideoViewToLinphone() {
        androidVideoWindow = new AndroidVideoWindowImpl(mVideoView, null, new AndroidVideoWindowImpl.VideoWindowListener() {

            @Override
            public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                mVideoView = surfaceView;
                setVideoWindow(androidVideoWindow);
            }

            @Override
            public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {

            }

            @Override
            public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {

            }
        });
    }

    @OnClick({R.id.btn_cancel_call, R.id.btn_on_off_speaker, R.id.txt_send})
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
        }
    }

    private void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        if (LinphoneHandler.getInstance() != null) {
            LinphoneHandler.getInstance().setVideoWindow(androidVideoWindow);
        }
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
    public final void onCallResume() {
        txtLowSignal.setVisibility(View.INVISIBLE);
    }

    @Override
    public void didSendChatToServer(BaseChatItem baseChatItem) {
        chatAdapter.add(baseChatItem);
        edtChat.setEnabled(true);
        txtSend.setEnabled(true);
        recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    public void didChatError(int errorCode, String errorMessage) {
        // TODO: 4/10/17
//        if (errorCode == Constant.Error.NOT_ENOUGH_POINT) {
//            showDialogNotifyNotEnoughPointForChat(BaseChatItem.ChatType.CHAT_TEXT, 20);
//        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
//        }
    }

    @Override
    public void onStateMessageChange(ReceivingReadMessageEvent receivingReadMessageEvent) {
        chatAdapter.updateOwnerStateMessageToRead(receivingReadMessageEvent.getBaseChatItem());
    }

    private void doSendMessage() {
        String newMessage = edtChat.getText().toString();
        if (!"".equalsIgnoreCase(newMessage)) {
            edtChat.setText("");
            edtChat.setEnabled(false);
            txtSend.setEnabled(false);
            basicChatPresenter.sendText(newMessage, competitor);
        }
    }
}