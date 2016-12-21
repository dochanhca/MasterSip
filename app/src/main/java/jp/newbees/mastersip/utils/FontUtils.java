package jp.newbees.mastersip.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {
    private static FontUtils instance;
    private Typeface typefaceTextView;
    private Typeface typefaceTextViewBold;
    private Typeface typefaceButton;

    public final static FontUtils getInstance() {
        if (null == instance) {
            instance = new FontUtils();
        }
        return instance;
    }

    public final void initFonts(Context context) {
        typefaceTextView = Typeface.createFromAsset(context.getAssets(), "fonts/Hiragino-Pro-W3.ttf");
        typefaceButton = Typeface.createFromAsset(context.getAssets(), "fonts/Hiragino-Pro-N-W4.ttf");
    }

    public Typeface getTypefaceTextView() {
        return typefaceTextView;
    }

    public Typeface getTypefaceTextViewBold() {
        return typefaceTextViewBold;
    }

    public Typeface getTypefaceButton() {
        return typefaceButton;
    }
}
