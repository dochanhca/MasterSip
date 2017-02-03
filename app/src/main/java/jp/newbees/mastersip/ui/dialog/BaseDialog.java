package jp.newbees.mastersip.ui.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseDialog extends DialogFragment {

    protected View mRoot;
    private ViewStub mViewStub;
    protected Button mButtonPositive;
    protected Button mButtonNegative;
    protected ViewGroup mLayoutActions;
    protected TextView txtDialogHeader;

    public BaseDialog() {

    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        mRoot = inflater.inflate(R.layout.dialog_base, null);

        mViewStub = (ViewStub) mRoot.findViewById(R.id.view_stub);
        mLayoutActions = (ViewGroup) mRoot.findViewById(R.id.layout_actions);
        txtDialogHeader = (TextView) mRoot.findViewById(R.id.txt_dialog_header);

        mViewStub.setLayoutResource(getLayoutDialog());
        View content = mViewStub.inflate();
        initActions();
        initViews(content, savedInstanceState);

        return mRoot;
    }

    private void initActions() {
        mButtonPositive = (Button) mRoot.findViewById(R.id.btn_positive);
        mButtonNegative = (Button) mRoot.findViewById(R.id.btn_negative);
        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mButtonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setOnNegativeListener(View.OnClickListener onNegativeListener) {
        mButtonNegative.setOnClickListener(onNegativeListener);
    }

    public void setOnPositiveListener(View.OnClickListener onPositiveListener) {
        mButtonPositive.setOnClickListener(onPositiveListener);
    }

    protected abstract void initViews(View rootView, Bundle savedInstanceState);


    protected abstract int getLayoutDialog();

    protected void hideLayoutActions() {
        mLayoutActions.setVisibility(View.GONE);
    }

    protected void setDialogHeader(String title) {
        txtDialogHeader.setVisibility(View.VISIBLE);
        txtDialogHeader.setText(title);
    }

    protected void setPositiveButtonContent(String text) {
        mButtonPositive.setText(text);
    }

    protected void setNegativeButtonInvisible(boolean negativeButtonInvisible) {
        mButtonNegative.setVisibility(negativeButtonInvisible ? View.GONE : View.VISIBLE);
    }

    protected void setNegativeButtonContent(String text) {
        mButtonNegative.setText(text);
    }

}
