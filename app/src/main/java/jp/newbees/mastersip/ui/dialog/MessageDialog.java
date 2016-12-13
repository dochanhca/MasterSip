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

    private View mRoot;
    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtNote;

    private String title, content, note;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        mRoot = inflater.inflate(R.layout.dialog_message, container, false);
        txtTitle = (TextView) mRoot.findViewById(R.id.txt_dialog_title);
        txtContent = (TextView) mRoot.findViewById(R.id.txt_dialog_content);
        txtNote = (TextView) mRoot.findViewById(R.id.txt_dialog_note);
        initAction();

        title = getArguments().getString(MESSAGE_DIALOG_TITLE, "");
        content = getArguments().getString(MESSAGE_DIALOG_CONTENT, "");
        note = getArguments().getString(MESSAGE_DIALOG_NOTE, "");

        setDialogTitle();
        setDialogContent();
        setDialogNote();

        return mRoot;
    }

    private void initAction() {
        ImageView imgOKButton = (ImageView) mRoot.findViewById(R.id.img_ok_button);
        imgOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void setDialogTitle() {
        if (title.equals("")) {
            return;
        }
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
    }

    private void setDialogContent() {
        if (content.equals("")) {
            return;
        }
        txtContent.setVisibility(View.VISIBLE);
        txtContent.setText(content);
    }

    private void setDialogNote() {
        if (note.equals("")) {
            return;
        }
        txtNote.setVisibility(View.VISIBLE);
        txtNote.setText(note);
    }

}
