package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 12/14/16.
 */

public class ConfirmCropImageDialog extends BaseDialog implements View.OnClickListener {

    public static interface OnDialogConfirmCropImageClick {
        abstract void onOkClick();
    }

    private OnDialogConfirmCropImageClick onDialogConfirmCropImageClick;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {

        setOnPositiveListener(this);
        setOnNegativeListener(this);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_confirm_crop_image;
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonPositive) {
            this.onDialogConfirmCropImageClick.onOkClick();
        }

        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.onDialogConfirmCropImageClick = (OnDialogConfirmCropImageClick) context;
        } catch (ClassCastException e ) {

        }
    }
}
