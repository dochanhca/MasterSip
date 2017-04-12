package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 1/23/17.
 */

public class TextDialog extends BaseDialog implements View.OnClickListener {

    private static final String DIALOG_CONTENT = "DIALOG_CONTENT";
    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String REQUEST_CODE = "REQUEST_CODE";
    private static final String POSITIVE_TITLE = "POSITIVE_TITLE";
    private static final String NEGATIVE_TITLE = "NEGATIVE_TITLE";
    private static final String HIDDEN_NEGATIVE_BUTTON = "HIDDEN_NEGATIVE_BUTTON";

    private TextView txtDialogTitle;
    private TextView txtContent;
    private int requestCode;
    private OnTextDialogPositiveClick onTextDialogPositiveClick;

    @FunctionalInterface
    public interface OnTextDialogPositiveClick {
        void onTextDialogOkClick(int requestCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.onTextDialogPositiveClick = (OnTextDialogPositiveClick) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(e.getMessage());
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.onTextDialogPositiveClick = (OnTextDialogPositiveClick) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException(e.getMessage());
            }
        }
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        txtDialogTitle = (TextView) rootView.findViewById(R.id.txt_dialog_title);
        txtContent = (TextView) rootView.findViewById(R.id.txt_dialog_content);

        getDataForUpdateView();

        setCancelable(false);
        setOnPositiveListener(this);
        setOnNegativeListener(this);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_text;
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonPositive) {
            this.onTextDialogPositiveClick.onTextDialogOkClick(this.requestCode);
        }
        dismiss();
    }

    private void getDataForUpdateView() {
        String content = getArguments().getString(DIALOG_CONTENT);
        String title = getArguments().getString(DIALOG_TITLE, "");
        boolean hiddenNegativeButton = getArguments().getBoolean(HIDDEN_NEGATIVE_BUTTON, false);
        requestCode = getArguments().getInt(REQUEST_CODE, -1);
        String positiveTitle = getArguments().getString(POSITIVE_TITLE, "");
        String negativeTitle = getArguments().getString(NEGATIVE_TITLE, "");

        txtContent.setText(content);
        setNegativeButtonInvisible(hiddenNegativeButton);

        if (!"".equals(positiveTitle)) {
            setPositiveButtonContent(positiveTitle);
        }

        if (!"".equals(negativeTitle)) {
            setNegativeButtonContent(negativeTitle);
        }

        if (!"".equals(title)) {
            txtDialogTitle.setVisibility(View.VISIBLE);
            txtDialogTitle.setText(title);
        }
    }

    public static final class Builder {
        private final Bundle args;

        public Builder() {
            this.args = new Bundle();
        }

        public Builder setTitle(String title) {
            args.putString(DIALOG_TITLE, title);
            return this;
        }

        public Builder setPositiveTitle(String positiveTitle) {
            args.putString(POSITIVE_TITLE, positiveTitle);
            return this;
        }

        public Builder setNegativeTitle(String negativeTitle) {
            args.putString(NEGATIVE_TITLE, negativeTitle);
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            args.putInt(REQUEST_CODE, requestCode);
            return this;
        }

        public Builder hideNegativeButton(boolean isHidden) {
            args.putBoolean(HIDDEN_NEGATIVE_BUTTON, isHidden);
            return this;
        }

        /**
         * call from activity
         * @param content
         * @return
         */
        public TextDialog build(String content) {
            TextDialog textDialog = new TextDialog();

            args.putString(DIALOG_CONTENT, content);
            textDialog.setArguments(args);
            return textDialog;
        }

        /**
         * call from fragment
         * @param fragment
         * @param content
         * @param requestCode
         * @return
         */
        public TextDialog build(Fragment fragment,
                                String content, int requestCode) {
            TextDialog textDialog = new TextDialog();

            args.putString(DIALOG_CONTENT, content);
            args.putInt(REQUEST_CODE, requestCode);
            textDialog.setArguments(args);
            textDialog.setTargetFragment(fragment, requestCode);
            return textDialog;
        }
    }
}
