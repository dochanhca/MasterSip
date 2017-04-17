package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by thangit14 on 1/11/17.
 */

public class SoftKeyboardListenedRelativeLayout extends RelativeLayout {
    private SoftKeyboardLsner listener;

    public SoftKeyboardListenedRelativeLayout(Context context) {
        super(context);
    }

    public SoftKeyboardListenedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SoftKeyboardListenedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        if (actualHeight > proposedheight) {
            listener.onSoftKeyboardShow();
        } else if (actualHeight < proposedheight) {
            listener.onSoftKeyboardHide();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setListener(SoftKeyboardLsner listener) {
        this.listener = listener;
    }

    // Callback
    public interface SoftKeyboardLsner {
        public void onSoftKeyboardShow();

        public void onSoftKeyboardHide();
    }
}
