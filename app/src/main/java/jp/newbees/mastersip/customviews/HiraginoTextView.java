package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import jp.newbees.mastersip.utils.FontUtils;

/**
 * Created by ducpv on 12/8/16.
 */

public class HiraginoTextView extends AppCompatTextView {

    public HiraginoTextView(Context context) {
        super(context);
        initFont();
    }

    public HiraginoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public HiraginoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont();
    }


    private void initFont() {
        Typeface font = FontUtils.getInstance().getTypefaceTextView();
        this.setTypeface(font);
    }
}
