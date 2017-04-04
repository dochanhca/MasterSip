package jp.newbees.mastersip.ui.dialog;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 3/21/17.
 */

public class OneButtonDialog extends DialogFragment {

    public static final String MESSAGE_DIALOG_TITLE = "MESSAGE_DIALOG_TITLE";
    public static final String MESSAGE_DIALOG_CONTENT = "MESSAGE_DIALOG_CONTENT";
    public static final String MESSAGE_DIALOG_NOTE = "MESSAGE_DIALOG_NOTE";
    public static final String MESSAGE_BUTTON_TITLE = "MESSAGE_BUTTON_TITLE";

    private View mRoot;
    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtNote;
    private Button btnPositive;

    private String title;
    private String content;
    private String note;
    private String positiveTitle;

    private OneButtonDialogClickListener oneButtonDialogClickListener;

    public interface OneButtonDialogClickListener {
        void onOneButtonPositiveClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                oneButtonDialogClickListener = (OneButtonDialogClickListener) context;
            } catch (ClassCastException e) {
                Logger.e("OneButtonDialog","Need implement OneButtonDialogClickListener to listener callback");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                oneButtonDialogClickListener = (OneButtonDialogClickListener) getTargetFragment();
            } catch (ClassCastException e) {
                Logger.e("OneButtonDialog","Need implement OneButtonDialogClickListener to listener callback");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        mRoot = inflater.inflate(R.layout.dialog_custom_message, container, false);
        txtTitle = (TextView) mRoot.findViewById(R.id.txt_dialog_title);
        txtContent = (TextView) mRoot.findViewById(R.id.txt_dialog_content);
        txtNote = (TextView) mRoot.findViewById(R.id.txt_dialog_note);

        title = getArguments().getString(MESSAGE_DIALOG_TITLE, "");
        content = getArguments().getString(MESSAGE_DIALOG_CONTENT, "");
        note = getArguments().getString(MESSAGE_DIALOG_NOTE, "");
        positiveTitle = getArguments().getString(MESSAGE_BUTTON_TITLE, "");

        initAction();

        setDialogTitle();
        setDialogContent();
        setDialogNote();
        setPositiveButtonTitle();

        return mRoot;
    }

    private void setPositiveButtonTitle() {
        if (!"".equals(positiveTitle)) {
            btnPositive.setText(positiveTitle);
        }
    }

    private void initAction() {
        btnPositive = (Button) mRoot.findViewById(R.id.btn_positive);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oneButtonDialogClickListener != null) {
                    oneButtonDialogClickListener.onOneButtonPositiveClick();
                }
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

    public static void showDialog(Fragment fragment, FragmentManager fragmentManager,
                                  int requestCode, String title,
                                  String content, String note, String positiveTitle) {
        OneButtonDialog messageDialog = new OneButtonDialog();

        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_DIALOG_TITLE, title);
        bundle.putString(MESSAGE_DIALOG_CONTENT, content);
        bundle.putString(MESSAGE_DIALOG_NOTE, note);
        bundle.putString(MESSAGE_BUTTON_TITLE, positiveTitle);

        messageDialog.setArguments(bundle);
        messageDialog.setTargetFragment(fragment, requestCode);
        messageDialog.show(fragmentManager, "OneButtonDialog");
    }

    public static void showDialog(FragmentManager fragmentManager, String title,
                                  String content, String note, String positiveTitle) {
        OneButtonDialog messageDialog = new OneButtonDialog();

        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_DIALOG_TITLE, title);
        bundle.putString(MESSAGE_DIALOG_CONTENT, content);
        bundle.putString(MESSAGE_DIALOG_NOTE, note);
        bundle.putString(MESSAGE_BUTTON_TITLE, positiveTitle);

        messageDialog.setArguments(bundle);
        messageDialog.show(fragmentManager, "OneButtonDialog");
    }
}
