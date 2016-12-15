package jp.newbees.mastersip.ui.dialog;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseDialog extends DialogFragment {

    protected View mRoot;
    private ViewStub mViewStub;
    protected ImageView mButtonPositive;
    protected ImageView mButtonNegative;
    protected ViewGroup mLayoutActions;

    public BaseDialog() {

    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        mRoot = inflater.inflate(R.layout.dialog_base, null);

        mViewStub = (ViewStub) mRoot.findViewById(R.id.view_stub);
        mLayoutActions = (ViewGroup) mRoot.findViewById(R.id.layout_actions);

        mViewStub.setLayoutResource(getLayoutDialog());
        View content = mViewStub.inflate();
        initActions();
        initViews(content, savedInstanceState);

        return mRoot;
    }

    private void initActions() {
        mButtonPositive = (ImageView) mRoot.findViewById(R.id.img_positive);
        mButtonNegative = (ImageView) mRoot.findViewById(R.id.img_negative);
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
        ;
    }

    protected abstract void initViews(View rootView, Bundle savedInstanceState);


    protected abstract int getLayoutDialog();

    protected void hideLayoutActions() {
        mLayoutActions.setVisibility(View.GONE);
    }

}
