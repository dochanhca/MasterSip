package jp.newbees.mastersip.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import jp.newbees.mastersip.utils.FontUtils;

/**
 * Created by ducpv on 12/13/16.
 */

public class HiraginoEditText extends EditText {

    public HiraginoEditText(Context context) {
        super(context);
        initFont();
    }

    public HiraginoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public HiraginoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont();
    }


    private void initFont() {
        Typeface font = FontUtils.getInstance().getTypefaceTextView();
        this.setTypeface(font);
    }
}
