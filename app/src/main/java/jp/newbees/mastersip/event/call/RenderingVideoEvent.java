package jp.newbees.mastersip.event.call;

import android.view.SurfaceView;

import org.linphone.mediastream.video.AndroidVideoWindowImpl;

/**
 * Created by thangit14 on 3/14/17.
 */

public class RenderingVideoEvent {
    public enum RenderingEvent{
        ON_VIDEO_RENDERING, ON_PREVIEW
    }

    private RenderingEvent event;

    private AndroidVideoWindowImpl androidVideoWindow;
    private SurfaceView captureView;

    public static RenderingVideoEvent getEventForVideoRendering(AndroidVideoWindowImpl androidVideoWindow) {
        RenderingVideoEvent renderingVideoEvent = new RenderingVideoEvent(RenderingEvent.ON_VIDEO_RENDERING);
        renderingVideoEvent.setAndroidVideoWindow(androidVideoWindow);
        return renderingVideoEvent;
    }

    public static RenderingVideoEvent getEventForVideoPreview(SurfaceView captureView) {
        RenderingVideoEvent renderingVideoEvent = new RenderingVideoEvent(RenderingEvent.ON_PREVIEW);
        renderingVideoEvent.setCaptureView(captureView);
        return renderingVideoEvent;
    }

    private RenderingVideoEvent(RenderingEvent event) {
        this.event = event;
    }

    public RenderingEvent getEvent() {
        return event;
    }

    public void setEvent(RenderingEvent event) {
        this.event = event;
    }

    public AndroidVideoWindowImpl getAndroidVideoWindow() {
        return androidVideoWindow;
    }

    public void setAndroidVideoWindow(AndroidVideoWindowImpl androidVideoWindow) {
        this.androidVideoWindow = androidVideoWindow;
    }

    public SurfaceView getCaptureView() {
        return captureView;
    }

    public void setCaptureView(SurfaceView captureView) {
        this.captureView = captureView;
    }
}
