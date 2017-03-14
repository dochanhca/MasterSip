package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.event.call.RenderingVideoEvent;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 3/14/17.
 */

public class VideoCallFragment extends BaseFragment{

    private SurfaceView mVideoView;
    private SurfaceView mCaptureView;

    private AndroidVideoWindowImpl androidVideoWindow;

    public static VideoCallFragment newInstance() {

        Bundle args = new Bundle();

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
        mVideoView = (SurfaceView) mRoot.findViewById(R.id.videoSurface);
        mCaptureView = (SurfaceView) mRoot.findViewById(R.id.videoCaptureSurface);
        mCaptureView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        fixZOrder(mVideoView, mCaptureView);

        androidVideoWindow = new AndroidVideoWindowImpl(mVideoView,mCaptureView,new AndroidVideoWindowImpl.VideoWindowListener(){

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
                EventBus.getDefault().post(RenderingVideoEvent.getEventForVideoPreview(mCaptureView));
            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {

            }
        });
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

    private void setVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        EventBus.getDefault().post(RenderingVideoEvent.getEventForVideoRendering(androidVideoWindow));
    }

    private void fixZOrder(SurfaceView video, SurfaceView preview) {
        video.setZOrderOnTop(false);
        preview.setZOrderOnTop(true);
        preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
    }
}
