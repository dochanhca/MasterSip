package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by thangit14 on 12/9/16.
 */

public class NavigationLayoutGroup extends LinearLayout {
    private OnChildItemClickListener onChildItemClickListener;

    private OnClickListener mOnItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            updateViewChild(v);
            if (onChildItemClickListener != null) {
                onChildItemClickListener.onChildItemClick(v, (Integer) v.getTag());
            }
        }
    };

    private void updateViewChild(View child) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof NavigationLayoutChild) {
                NavigationLayoutChild navigationLayoutChild = (NavigationLayoutChild) getChildAt(i);
                if (child.getId() == navigationLayoutChild.getId()) {
                    navigationLayoutChild.changeToSelectedTab();
                } else {
                    navigationLayoutChild.changeToNormalTab();
                }
            }
        }
    }

    private void updateViewChild(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            NavigationLayoutChild navigationLayoutChild = (NavigationLayoutChild) getChildAt(i);
            if (position == i) {
                navigationLayoutChild.changeToSelectedTab();
            } else {
                navigationLayoutChild.changeToNormalTab();
            }
        }
    }

    public NavigationLayoutGroup(Context context) {
        super(context);
        setupView();
    }

    public NavigationLayoutGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public NavigationLayoutGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView();
    }

    private void setupView() {
        setWeightSum(getChildCount());
        setOrientation(HORIZONTAL);
//        setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupChildView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof NavigationLayoutChild) {
                NavigationLayoutChild navigationLayoutChild = (NavigationLayoutChild) getChildAt(i);

                LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                params.weight = 1.0f;

                navigationLayoutChild.setLayoutParams(params);
                navigationLayoutChild.setmOnViewClickListener(mOnItemClickListener);
                navigationLayoutChild.setTag(i);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setupChildView();
    }

    public interface OnChildItemClickListener {
        void onChildItemClick(View view, int position);
    }

    public void setOnChildItemClickListener(OnChildItemClickListener onChildItemClickListener) {
        this.onChildItemClickListener = onChildItemClickListener;
    }

    public void setSelectedItem(int position) {
        updateViewChild(position);
    }
}
