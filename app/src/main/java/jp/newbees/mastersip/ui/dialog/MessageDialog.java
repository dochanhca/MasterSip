package jp.newbees.mastersip.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 12/9/16.
 */

public class MessageDialog extends DialogFragment {

    public static final String MESSAGE_DIALOG_TITLE = "MESSAGE_DIALOG_TITLE";
    public static final String MESSAGE_DIALOG_CONTENT = "MESSAGE_DIALOG_CONTENT";
    public static final String MESSAGE_DIALOG_NOTE = "MESSAGE_DIALOG_NOTE";
    public static final String IS_HIDE_ACTION_BUTTON = "IS_HIDE_ACTION_BUTTON";

    private View mRoot;
    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtNote;
    private ImageView imgOKButton;

    private String title;
    private String content;
    private String note;
    private boolean isHideActionButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        mRoot = inflater.inflate(R.layout.dialog_message, container, false);
        txtTitle = (TextView) mRoot.findViewById(R.id.txt_dialog_title);
        txtContent = (TextView) mRoot.findViewById(R.id.txt_dialog_content);
        txtNote = (TextView) mRoot.findViewById(R.id.txt_dialog_note);

        title = getArguments().getString(MESSAGE_DIALOG_TITLE, "");
        content = getArguments().getString(MESSAGE_DIALOG_CONTENT, "");
        note = getArguments().getString(MESSAGE_DIALOG_NOTE, "");
        isHideActionButton = getArguments().getBoolean(IS_HIDE_ACTION_BUTTON, false);

        initAction();

        setDialogTitle();
        setDialogContent();
        setDialogNote();

        return mRoot;
    }

    private void initAction() {
        imgOKButton = (ImageView) mRoot.findViewById(R.id.img_ok_button);
        if (isHideActionButton) {
            imgOKButton.setVisibility(View.GONE);
            return;
        }
        imgOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void setDialogTitle() {
        if ("".equals(title)) {
            return;
        }
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
    }

    private void setDialogContent() {
        if ("".equals(content)) {
            return;
        }
        txtContent.setVisibility(View.VISIBLE);
        txtContent.setText(content);
    }

    private void setDialogNote() {
        if ("".equals(note)) {
            return;
        }
        txtNote.setVisibility(View.VISIBLE);
        txtNote.setText(note);
    }

    public void setOnPositiveClickListener(View.OnClickListener onPositiveClickListener) {
        imgOKButton.setOnClickListener(onPositiveClickListener);
    }

}
