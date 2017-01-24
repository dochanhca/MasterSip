package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by datnx on 12/10/14.
 */
public class CustomScrollView extends ScrollView {

    public interface OnScrollViewListener {
        public void onScrollView(CustomScrollView customScrollView, int x, int y, int oldx, int oldy);
    }

    private OnScrollViewListener scrollViewListener;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScrollViewListener(OnScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollView(this, l, t, oldl, oldt);
        }
    }
}
