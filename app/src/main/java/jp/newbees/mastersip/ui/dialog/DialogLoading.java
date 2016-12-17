package jp.newbees.mastersip.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;


/**
 * Created by VietBui on 3/19/14.
 */
public class DialogLoading extends BaseDialog {

    public static final java.lang.String TITLE_DIALOG = "TITLE DIALOG";
    public static final String CONTENT_DIALOG = "CONTENT DIALOG";
    private TextView mTextContent;
    private String contentDialog;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        String textContent = getArguments().getString(CONTENT_DIALOG);
        mTextContent = (TextView) rootView.findViewById(R.id.txt_content);
        mTextContent.setText(textContent);
        setCancelable(false);
        hideLayoutActions();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_loading;
    }

    public void setContentDialog(String contentDialog) {
        mTextContent.setText(contentDialog);
    }

    public void dismissDialog() {
        dismiss();
    }
}
