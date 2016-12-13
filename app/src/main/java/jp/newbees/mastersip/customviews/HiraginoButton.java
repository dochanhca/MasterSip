package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import jp.newbees.mastersip.utils.FontUtils;

/**
 * Created by ducpv on 12/8/16.
 */

public class HiraginoButton extends Button {

    public HiraginoButton(Context context) {
        super(context);
        initFont();
    }

    public HiraginoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public HiraginoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont();
    }


    private void initFont() {
        Typeface font = FontUtils.getInstance().getTypefaceButton();
        this.setTypeface(font);
    }
}
