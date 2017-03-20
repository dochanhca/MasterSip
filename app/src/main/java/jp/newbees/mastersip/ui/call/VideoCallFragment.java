package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.event.call.RenderingVideoEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.thread.MyCountingTimerThread;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.call.base.BaseHandleCallActivity;

/**
 * Created by thangit14 on 3/14/17.
 */

public class VideoCallFragment extends BaseFragment implements View.OnTouchListener {
    private static final String USER_ITEM = "USER ITEM";
    private static final String CALL_TYPE = "CALL TYPE";
    @BindView(R.id.videoSurface)
    SurfaceView mVideoView;
    @BindView(R.id.videoCaptureSurface)
    SurfaceView mCaptureView;
    @BindView(R.id.btn_on_off_mic)
    ToggleButton btnOnOffMic;
    @BindView(R.id.btn_on_off_speaker)
    ToggleButton btnOnOffSpeaker;
    @BindView(R.id.btn_on_off_camera)
    ToggleButton btnOnOffCamera;
    @BindView(R.id.txt_name)
    HiraginoTextView txtName;
    @BindView(R.id.txt_time)
    HiraginoTextView txtTime;
    @BindView(R.id.video_frame)
    RelativeLayout videoFrame;
    @BindView(R.id.txt_point)
    HiraginoTextView txtPoint;
    @BindView(R.id.ll_point)
    LinearLayout llPoint;
    @BindView(R.id.layout_video_call_action)
    protected ViewGroup layoutVideoCallAction;

    private AndroidVideoWindowImpl androidVideoWindow;

    private UserItem userItem;
    private Handler timerHandler = new Handler();

    private MyCountingTimerThread myCountingTimerThread;

    private Handler countingHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            hideView();
        }
    };

    public static VideoCallFragment newInstance(UserItem currentUser, int callType) {
        Bundle args = new Bundle();
        args.putParcelable(USER_ITEM, currentUser);
        args.putInt(CALL_TYPE, callType);
        VideoCallFragment fragment = new VideoCallFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_video_call;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);

        userItem = getArguments().getParcelable(USER_ITEM);

        setupView();
        fixZOrder(mVideoView, mCaptureView);
        startCounting();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (androidVideoWindow != null) {
            synchronized (androidVideoWindow) {
                setVideoWindow(androidVideoWindow);
            }
        }
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
    }

    @Override
    public void onDestroy() {
        mCaptureView = null;
        if (androidVideoWindow != null) {
            androidVideoWindow.release();
            androidVideoWindow = null;
        }
        if (myCountingTimerThread != null) {
            myCountingTimerThread.turnOffCounting();
        }
        super.onDestroy();
    }

    private void setupView() {
        bindVideoViewToLinphone();
        mCaptureView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        llPoint.setVisibility(userItem.getGender() == UserItem.MALE ? View.VISIBLE : View.GONE);

        txtName.setText(userItem.getUsername());
        countingCallDuration();
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

    @OnClick({R.id.btn_cancel_call, R.id.btn_on_off_mic, R.id.btn_on_off_speaker,
            R.id.btn_on_off_camera, R.id.img_switch_camera})
    public void onClick(View view) {
        BaseHandleCallActivity activity = (BaseHandleCallActivity) getActivity();

        switch (view.getId()) {
            case R.id.btn_cancel_call:
                activity.endCall();
                break;
            case R.id.btn_on_off_mic:
                activity.muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_on_off_speaker:
                activity.enableSpeaker(!btnOnOffSpeaker.isChecked());
                break;
            case R.id.btn_on_off_camera:
                activity.enableCamera(!btnOnOffCamera.isChecked());
                updateVideoView();
                break;
            case R.id.img_switch_camera:
                activity.switchCamera();
                break;
        }
    }

    @OnTouch({R.id.videoSurface, R.id.videoCaptureSurface})
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.videoSurface:
            case R.id.videoCaptureSurface:
                showView();
                if (myCountingTimerThread != null) {
                    myCountingTimerThread.reset();
                }
                break;
        }
        return true;
    }

    private void updateVideoView() {
        if (btnOnOffCamera.isChecked()) {
            mCaptureView.setVisibility(View.GONE);
            bindingCaptureView(null);
        } else {
            bindingCaptureView(mCaptureView);
            mCaptureView.setVisibility(View.VISIBLE);
        }
    }

    private void bindingCaptureView(SurfaceView mCaptureView) {
        EventBus.getDefault().post(RenderingVideoEvent.getEventForVideoPreview(mCaptureView));
    }


    private void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        EventBus.getDefault().post(RenderingVideoEvent.getEventForVideoRendering(androidVideoWindow));
    }

    private void fixZOrder(SurfaceView video, SurfaceView preview) {
        video.setZOrderOnTop(false);
        preview.setZOrderOnTop(true);
        preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
    }

    private void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTime, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }

    private void hideView() {
        layoutVideoCallAction.setVisibility(View.GONE);
        llPoint.setVisibility(View.GONE);
    }

    private void showView() {
        llPoint.setVisibility(userItem.getGender() == UserItem.FEMALE ? View.GONE : View.VISIBLE);
        layoutVideoCallAction.setVisibility(View.VISIBLE);
    }

    private void startCounting() {
        myCountingTimerThread = new MyCountingTimerThread(countingHandler);
        Thread countingThread = new Thread(myCountingTimerThread);
        countingThread.start();
    }

    public void onCoinChanged(int coin) {
        if (isDetached()) return;

        StringBuilder point = new StringBuilder();
        point.append(String.valueOf(coin)).append(getString(R.string.pt));
        txtPoint.setText(point.toString());
    }
}
