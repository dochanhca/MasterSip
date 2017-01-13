package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import jp.newbees.mastersip.R;

/**
 * Created by thangit14 on 12/9/16.
 */

public class NavigationLayoutGroup extends LinearLayout {
    private boolean showDivider = false;
    private int dividerColor;

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
    private TypedArray typedArray;

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
    }

    public NavigationLayoutGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(attrs);
    }

    public NavigationLayoutGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView(attrs);
    }

    private void setupView(AttributeSet attrs) {
        setWeightSum(getChildCount());
        setOrientation(HORIZONTAL);

        typedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.NavigationLayout);

        if (typedArray.hasValue(R.styleable.NavigationLayout_showDivider)) {
            showDivider = typedArray.getBoolean(R.styleable.NavigationLayout_showDivider, false);
        }

        if (typedArray.hasValue(R.styleable.NavigationLayout_dividerColor)) {
            dividerColor = typedArray.getColor(R.styleable.NavigationLayout_dividerColor, Color.BLACK);
        }

    }

    private void showDividerOnChildView() {
        for (int i = 0; i < getChildCount() - 1; i++) {
            if (getChildAt(i) instanceof NavigationLayoutChild) {
                NavigationLayoutChild navigationLayoutChild = (NavigationLayoutChild) getChildAt(i);

                navigationLayoutChild.setShowDivider(true);
                navigationLayoutChild.setDividerColor(dividerColor);
            }
        }
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
        if (showDivider) {
            showDividerOnChildView();
        }
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
