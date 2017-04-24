package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.CallingFragment;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 3/14/17.
 * use for both incoming and outgoing video call
 */

public class VideoCallFragment extends CallingFragment implements View.OnTouchListener, CallingFragment.CountableToHideAction {
    @BindView(R.id.txt_low_signal)
    TextView txtLowSignal;
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
    @BindView(R.id.btn_cancel_call)
    ImageView btnCancelCall;
    @BindView(R.id.txt_name)
    HiraginoTextView txtName;
    @BindView(R.id.txt_time)
    HiraginoTextView txtTime;
    @BindView(R.id.img_switch_camera)
    ImageView imgSwitchCamera;
    @BindView(R.id.video_frame)
    RelativeLayout videoFrame;
    @BindView(R.id.txt_point)
    HiraginoTextView txtPoint;
    @BindView(R.id.ll_point)
    LinearLayout llPoint;
    @BindView(R.id.layout_video_call_action)
    protected ViewGroup layoutVideoCallAction;

    private AndroidVideoWindowImpl androidVideoWindow;

    private UserItem competitor;

    private Animation moveUpTxtTime;
    private Animation moveDownTxtTime;
    private Animation moveUpTxtPoint;
    private Animation moveDownTxtPoint;
    private Animation fadeIn;
    private Animation fadeOut;
    private boolean isShowingView = true;

    public static VideoCallFragment newInstance(UserItem competitor, String callID) {
        Bundle args = new Bundle();
        args.putParcelable(COMPETITOR, competitor);
        args.putString(CALL_ID, callID);
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

        competitor = getArguments().getParcelable(COMPETITOR);

        moveUpTxtTime = AnimationUtils.loadAnimation(getContext(), R.anim.move_up_txt_time);
        moveDownTxtTime = AnimationUtils.loadAnimation(getContext(), R.anim.move_down_txt_time);
        moveUpTxtPoint = AnimationUtils.loadAnimation(getContext(), R.anim.move_up_txt_point);
        moveDownTxtPoint = AnimationUtils.loadAnimation(getContext(), R.anim.move_down_txt_point);
        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        setupView();
        fixZOrder(mVideoView, mCaptureView);
        startCountingToHideAction();
        keepScreenAwake();
        updateUIWhenStartCalling();
    }

    private void keepScreenAwake() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private void setupView() {
        bindVideoViewToLinphone();
        mCaptureView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        llPoint.setVisibility(competitor.getGender() == UserItem.MALE ? View.VISIBLE : View.GONE);

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
                Logger.e("VideoCallFragment", "SurfaceDestroyed");
            }
        });
    }

    @OnClick({R.id.btn_cancel_call, R.id.btn_on_off_mic, R.id.btn_on_off_speaker,
            R.id.btn_on_off_camera, R.id.img_switch_camera})
    public void onClick(View view) {

        resetCountingToHideAction();
        switch (view.getId()) {
            case R.id.btn_cancel_call:
                terminalCall();
                break;
            case R.id.btn_on_off_mic:
                enableMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_on_off_speaker:
                enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
            case R.id.btn_on_off_camera:
                enableCamera(btnOnOffCamera.isChecked());
                updateVideoView(!btnOnOffCamera.isChecked());
                break;
            case R.id.img_switch_camera:
                switchCamera(mCaptureView);
                break;
            default:break;
        }
    }

    @OnTouch({R.id.videoSurface, R.id.videoCaptureSurface})
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.videoSurface || v.getId() == R.id.videoCaptureSurface) {
            resetCountingToHideAction();
            showView();
        }
        return true;
    }

    private void updateVideoView(boolean enable) {
        if (enable) {
            mCaptureView.setVisibility(View.GONE);
            bindingCaptureView(null);
        } else {
            mCaptureView.setVisibility(View.VISIBLE);
            bindingCaptureView(mCaptureView);
        }
    }

    private void bindingCaptureView(SurfaceView mCaptureView) {
        try {
            LinphoneHandler.getInstance().setPreviewWindow(mCaptureView);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        try {
            LinphoneHandler.getInstance().setVideoWindow(androidVideoWindow);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void fixZOrder(SurfaceView video, SurfaceView preview) {
        video.setZOrderOnTop(false);
        preview.setZOrderOnTop(true);
        preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
    }

    private void hideView() {
        if (!isShowingView) {
            return;
        }
        isShowingView = false;
        layoutVideoCallAction.startAnimation(fadeOut);
        llPoint.startAnimation(fadeOut);
        txtName.startAnimation(fadeOut);
        imgSwitchCamera.startAnimation(fadeOut);
        txtTime.startAnimation(moveUpTxtTime);
        if (llPoint.getVisibility() == View.VISIBLE) {
            llPoint.startAnimation(moveDownTxtPoint);
        }
        setViewsClickable(false);
    }

    private void showView() {
        if (isShowingView) {
            return;
        }
        isShowingView = true;

        layoutVideoCallAction.startAnimation(fadeIn);
        llPoint.startAnimation(fadeIn);
        txtName.startAnimation(fadeIn);
        imgSwitchCamera.startAnimation(fadeIn);
        txtTime.startAnimation(moveDownTxtTime);
        if (llPoint.getVisibility() == View.VISIBLE) {
            llPoint.startAnimation(moveUpTxtPoint);
        }
        setViewsClickable(true);
    }

    private void setViewsClickable(boolean clickable) {
        btnCancelCall.setClickable(clickable);
        btnOnOffCamera.setClickable(clickable);
        btnOnOffMic.setClickable(clickable);
        btnOnOffSpeaker.setClickable(clickable);
        llPoint.setClickable(clickable);
        txtName.setClickable(clickable);
        imgSwitchCamera.setClickable(clickable);
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
        btnOnOffSpeaker.setChecked(isSpeakerEnalbed());
        btnOnOffMic.setChecked(isMicEnalbed());
        btnOnOffCamera.setChecked(true);
        useFrontCamera();
    }

    @Override
    public final void onCallResume() {
        txtLowSignal.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBreakTimeToHide() {
        hideView();
    }
}
