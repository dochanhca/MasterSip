package jp.newbees.mastersip.ui.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 1/23/17.
 */

public class TextDialog extends DialogFragment {

    private static final String DIALOG_CONTENT = "DialogContent";
    protected View mRoot;
    protected ImageView mButtonPositive;
    protected ImageView mButtonNegative;
    protected ViewGroup mLayoutActions;
    protected TextView txtDialogHeader;
    private TextView txtContent;
    private View.OnClickListener onNegativeListener;
    private View.OnClickListener onPositiveListener;

    public TextDialog() {

    }

    public static final TextDialog getInstance(String content) {
        TextDialog textDialog = new TextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_CONTENT, content);
        textDialog.setArguments(bundle);
        return textDialog;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        mRoot = inflater.inflate(R.layout.dialog_text, null);

        mLayoutActions = (ViewGroup) mRoot.findViewById(R.id.layout_actions);
        txtDialogHeader = (TextView) mRoot.findViewById(R.id.txt_dialog_header);
        txtContent = (TextView) mRoot.findViewById(R.id.txt_content);

        initActions();
        initViews();
        return mRoot;
    }

    private void initViews() {
        String content = getArguments().getString(DIALOG_CONTENT);
        txtContent.setText(content);
    }

    private void initActions() {
        mButtonPositive = (ImageView) mRoot.findViewById(R.id.img_positive);
        mButtonNegative = (ImageView) mRoot.findViewById(R.id.img_negative);
        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=onPositiveListener){
                    onPositiveListener.onClick(view);
                }
                dismiss();
            }
        });
        mButtonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=onNegativeListener){
                    onNegativeListener.onClick(view);
                }
                dismiss();
            }
        });
    }

    public void setOnNegativeListener(View.OnClickListener onNegativeListener) {
        this.onNegativeListener = onNegativeListener;
    }

    public void setOnPositiveListener(View.OnClickListener onPositiveListener) {
        this.onPositiveListener = onPositiveListener;
    }


    protected void setDialogHeader(String title) {
        txtDialogHeader.setVisibility(View.VISIBLE);
        txtDialogHeader.setText(title);
    }
}
