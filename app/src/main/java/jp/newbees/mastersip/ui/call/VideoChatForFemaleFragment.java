package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
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
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.eventbus.NewChatMessageEvent;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.BaseChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.chatting.BasicChatPresenter;
import jp.newbees.mastersip.presenter.chatting.ReceiveChatTextListener;
import jp.newbees.mastersip.ui.call.base.CallingFragment;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by thangit14 on 4/10/17.
 */

public class VideoChatForFemaleFragment extends CallingFragment implements ReceiveChatTextListener {

    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.txt_low_signal)
    TextView txtLowSignal;
    @BindView(R.id.videoCaptureSurface)
    SurfaceView mCaptureView;
    @BindView(R.id.btn_on_off_mic)
    ToggleButton btnOnOffMic;
    @BindView(R.id.txt_name)
    HiraginoTextView txtName;
    @BindView(R.id.txt_time)
    HiraginoTextView txtTime;
    @BindView(R.id.img_switch_camera)
    ImageView imgSwitchCamera;
    @BindView(R.id.txt_point)
    HiraginoTextView txtPoint;
    @BindView(R.id.videoSurface)
    SurfaceView mVideoView;

    private AndroidVideoWindowImpl androidVideoWindow;

    private UserItem competitor;

    private ChatAdapter chatAdapter;
    private BasicChatPresenter basicChatPresenter;

    public static VideoChatForFemaleFragment newInstance(UserItem competitor, String callID) {
        Bundle args = new Bundle();
        args.putParcelable(COMPETITOR, competitor);
        args.putString(CALL_ID, callID);
        VideoChatForFemaleFragment fragment = new VideoChatForFemaleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_video_chat_call_for_female;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);

        competitor = getArguments().getParcelable(COMPETITOR);

        basicChatPresenter = new BasicChatPresenter(getContext(), this);
        chatAdapter = new ChatAdapter(getActivity().getApplicationContext(), new ArrayList<BaseChatItem>());

        LayoutManager layoutManager = new LayoutManager(getContext());
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);
        setupView();
        fixZOrder(mVideoView, mCaptureView);

        enableSpeaker(false);
        useFrontCamera();
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
        mCaptureView = null;
        if (androidVideoWindow != null) {
            androidVideoWindow.release();
            androidVideoWindow = null;
        }
        super.onDestroy();
    }

    private void setupView() {
        bindVideoViewToLinphone();
        mCaptureView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        txtName.setText(competitor.getUsername());
    }

    private void bindVideoViewToLinphone() {
        androidVideoWindow = new AndroidVideoWindowImpl(mVideoView, mCaptureView, new AndroidVideoWindowImpl.VideoWindowListener() {

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
                mCaptureView = surfaceView;
                bindingCaptureView(mCaptureView);
            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {

            }
        });
    }

    @OnClick({R.id.btn_cancel_call, R.id.btn_on_off_mic,R.id.img_switch_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_call:
                terminalCall();
                break;
            case R.id.btn_on_off_mic:
                enableMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.img_switch_camera:
                switchCamera(mCaptureView);
                break;
        }
    }

    private void bindingCaptureView(SurfaceView mCaptureView) {
        LinphoneHandler.getInstance().setPreviewWindow(mCaptureView);
    }


    private void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        if (LinphoneHandler.isRunning()) {
            LinphoneHandler.getInstance().setVideoWindow(androidVideoWindow);
        }
    }

    private void fixZOrder(SurfaceView video, SurfaceView preview) {
        video.setZOrderOnTop(false);
        preview.setZOrderOnTop(true);
        preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
    }

    @Override
    protected void onCallingBreakTime(Message msg) {
        txtTime.setText(DateTimeUtils.getTimerCallString(msg.what));
    }

    @Override
    public TextView getTxtPoint() {
        return txtPoint;
    }

    @Override
    public void onCallPaused() {
        txtLowSignal.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUIWhenStartCalling() {
        countingCallDuration();

        btnOnOffMic.setChecked(isMicEnalbed());

    }

    @Override
    public final void onCallResume() {
        txtLowSignal.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onChatMessageEvent(NewChatMessageEvent newChatMessageEvent) {
        BaseChatItem chatItem = newChatMessageEvent.getBaseChatItem();
        if (basicChatPresenter.isMessageOfCurrentUser(chatItem.getOwner(), competitor)
                || chatItem.isOwner()) {
            chatAdapter.add(newChatMessageEvent.getBaseChatItem());
            recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }
}