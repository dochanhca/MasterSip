package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

    @FunctionalInterface
    public interface OnTextDialogPositiveClick {
        void onTextDialogOkClick(int requestCode);
    }

    private OnTextDialogPositiveClick onTextDialogPositiveClick;

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

        String content = getArguments().getString(DIALOG_CONTENT);
        String title = getArguments().getString(DIALOG_TITLE);
        boolean hiddenNegativeButton = getArguments().getBoolean(HIDDEN_NEGATIVE_BUTTON);

        this.requestCode = getArguments().getInt(REQUEST_CODE);
        String positiveTitle = getArguments().getString(POSITIVE_TITLE);
        if (!"".equals(positiveTitle)) {
            setPositiveButtonContent(positiveTitle);
        }

        String negativeTitle = getArguments().getString(NEGATIVE_TITLE);
        if (!"".equals(negativeTitle)) {
            setNegativeButtonContent(negativeTitle);
        }

        setNegativeButtonInvisible(hiddenNegativeButton);
        txtContent.setText(content);
        if (title.length() > 0) {
            txtDialogTitle.setVisibility(View.VISIBLE);
            txtDialogTitle.setText(title);
        }
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

    /**
     * Open Dialog from fragment
     *
     * @param fragment
     * @param requestCode
     * @param fragmentManager
     * @param content
     * @param title
     * @return
     */
    public static final void openTextDialog(Fragment fragment,
                                            int requestCode,
                                            FragmentManager fragmentManager,
                                            String content,
                                            String title) {
        TextDialog.openTextDialog(fragment, requestCode, fragmentManager, content, title, "", false);
    }

    /**
     * @param fragment
     * @param requestCode
     * @param fragmentManager
     * @param content
     * @param title
     * @param positiveTitle
     */
    public static final void openTextDialog(Fragment fragment,
                                            int requestCode,
                                            FragmentManager fragmentManager,
                                            String content,
                                            String title,
                                            String positiveTitle) {
        TextDialog.openTextDialog(fragment, requestCode, fragmentManager, content, title, positiveTitle, false);
    }

    /**
     * @param fragment
     * @param requestCode
     * @param fragmentManager
     * @param content
     * @param title
     * @param hiddenNegativeButton
     */
    public static final void openTextDialog(Fragment fragment,
                                            int requestCode,
                                            FragmentManager fragmentManager,
                                            String content,
                                            String title,
                                            boolean hiddenNegativeButton) {
        TextDialog.openTextDialog(fragment, requestCode, fragmentManager, content, title, "", hiddenNegativeButton);
    }


    /**
     * @param fragment
     * @param requestCode
     * @param fragmentManager
     * @param content
     * @param title
     * @param positiveTitle
     */
    public static final void openTextDialog(Fragment fragment,
                                            int requestCode,
                                            FragmentManager fragmentManager,
                                            String content,
                                            String title,
                                            String positiveTitle,
                                            boolean hiddenNegativeButton) {
        openTextDialog(fragment, requestCode, fragmentManager, content, title, positiveTitle, "",
                hiddenNegativeButton);
    }

    public static final void openTextDialog(Fragment fragment,
                                             int requestCode,
                                             FragmentManager fragmentManager,
                                             String content,
                                             String title,
                                             String positiveTitle,
                                             String negativeTitle,
                                             boolean hiddenNegativeButton) {
        if (positiveTitle == null)
            positiveTitle = "";

        TextDialog textDialog = new TextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_CONTENT, content);
        bundle.putString(DIALOG_TITLE, title);
        bundle.putInt(TextDialog.REQUEST_CODE, requestCode);
        bundle.putString(TextDialog.POSITIVE_TITLE, positiveTitle);
        bundle.putString(TextDialog.NEGATIVE_TITLE, negativeTitle);
        bundle.putBoolean(TextDialog.HIDDEN_NEGATIVE_BUTTON, hiddenNegativeButton);
        textDialog.setArguments(bundle);
        textDialog.setTargetFragment(fragment, requestCode);
        textDialog.show(fragmentManager, "TextDialog");
    }


    /**
     * @param fragmentManager
     * @param content
     * @param title
     */
    public static final void openTextDialog(FragmentManager fragmentManager,
                                            String content, String title, String positiveTitle,
                                            boolean hiddenNegativeButton) {
        openTextDialog(fragmentManager, -100, content, title, positiveTitle, "", hiddenNegativeButton);
    }

    /**
     * Open Dialog from Activity
     *
     * @param fragmentManager
     * @param requestCode
     * @param content
     * @param title
     * @param positiveTitle
     * @param hiddenNegativeButton
     */
    public static final void openTextDialog(FragmentManager fragmentManager, int requestCode,
                                            String content, String title, String positiveTitle,
                                            boolean hiddenNegativeButton) {
        openTextDialog(fragmentManager, requestCode, content, title, positiveTitle, "",
                hiddenNegativeButton);

    }

    public static final void openTextDialog(FragmentManager fragmentManager, int requestCode,
                                             String content, String title, String positiveTitle,
                                             String negativeTitle, boolean hiddenNegativeButton) {
        TextDialog textDialog = new TextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_CONTENT, content);
        bundle.putString(DIALOG_TITLE, title);
        bundle.putString(POSITIVE_TITLE, positiveTitle);
        bundle.putString(NEGATIVE_TITLE, negativeTitle);
        bundle.putInt(TextDialog.REQUEST_CODE, requestCode);
        bundle.putBoolean(HIDDEN_NEGATIVE_BUTTON, hiddenNegativeButton);
        textDialog.setArguments(bundle);
        textDialog.show(fragmentManager, "TextDialog");
    }
}
