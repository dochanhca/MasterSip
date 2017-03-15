package jp.newbees.mastersip.event.call;

import android.view.SurfaceView;

/**
 * Created by thangit14 on 3/14/17.
 */

public class VideoCallEvent {
    private SurfaceView mCaptureView;

    public enum VideoEvent {
        USE_FRONT_CAMERA, SWITCH_CAMERA, ENABLE_CAMERA, DISABLE_CAMERA
    }

    private VideoEvent event;

    public VideoCallEvent(VideoEvent event) {
        this.event = event;
    }

    public VideoEvent getEvent() {
        return event;
    }

    public SurfaceView getmCaptureView() {
        return mCaptureView;
    }

    public void setmCaptureView(SurfaceView mCaptureView) {
        this.mCaptureView = mCaptureView;
    }
}
