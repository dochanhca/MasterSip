package jp.newbees.mastersip.ui.dialog;

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

    private TextView txtDialogTitle;
    private TextView txtContent;

    public interface OnTextDialogClick {
        void onTextDialogOkClick();
    }

    private OnTextDialogClick onTextDialogClick;

    public TextDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.onTextDialogClick = (OnTextDialogClick) getTargetFragment();
            } catch (ClassCastException e ) {
                throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
            }
        }
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        txtDialogTitle = (TextView) rootView.findViewById(R.id.txt_dialog_title);
        txtContent = (TextView) rootView.findViewById(R.id.txt_dialog_content);

        String content = getArguments().getString(DIALOG_CONTENT);
        String title = getArguments().getString(DIALOG_TITLE);
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
            this.onTextDialogClick.onTextDialogOkClick();
        }
        dismiss();
    }

    public static final TextDialog openTextDialog(Fragment fragment, int requestCode,
                                                  FragmentManager fragmentManager,
                                                  String content, String title) {
        TextDialog textDialog = new TextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_CONTENT, content);
        bundle.putString(DIALOG_TITLE, title);
        textDialog.setArguments(bundle);
        textDialog.setTargetFragment(fragment, requestCode);
        textDialog.show(fragmentManager, "TextDialog");
        return textDialog;
    }
}
