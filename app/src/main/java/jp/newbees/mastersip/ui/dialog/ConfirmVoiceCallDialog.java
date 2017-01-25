package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 1/6/17.
 */

public class ConfirmVoiceCallDialog extends BaseDialog implements View.OnClickListener {

    public interface OnDialogConfirmVoiceCallClick {
        void onOkVoiceCallClick();
    }

    private OnDialogConfirmVoiceCallClick onDialogConfirmVoiceCallClick;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.onDialogConfirmVoiceCallClick = (OnDialogConfirmVoiceCallClick) context;
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling activity must implement DialogClickListener interface");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.onDialogConfirmVoiceCallClick = (OnDialogConfirmVoiceCallClick) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
            }
        }
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        setOnPositiveListener(this);
        setOnNegativeListener(this);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_confirm_voice_call;
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonPositive) {
            this.onDialogConfirmVoiceCallClick.onOkVoiceCallClick();
        }
        dismiss();
    }

    /**
     * Call from fragment
     * @param fragment
     * @param requestCode
     * @param fragmentManager
     */
    public static void openConfirmVoiceCallDialog(Fragment fragment, int requestCode,
                                                  FragmentManager fragmentManager) {
        ConfirmVoiceCallDialog confirmVoiceCallDialog = new ConfirmVoiceCallDialog();

        Bundle bundle = new Bundle();

        confirmVoiceCallDialog.setArguments(bundle);
        confirmVoiceCallDialog.setTargetFragment(fragment, requestCode);
        confirmVoiceCallDialog.show(fragmentManager, "ConfirmVoiceCallDialog");
    }

    /**
     * Call from activity
     * @param fragmentManager
     */
    public static void openConfirmVoiceCallDialog(FragmentManager fragmentManager) {
        ConfirmVoiceCallDialog confirmVoiceCallDialog = new ConfirmVoiceCallDialog();

        Bundle bundle = new Bundle();

        confirmVoiceCallDialog.setArguments(bundle);
        confirmVoiceCallDialog.show(fragmentManager, "ConfirmVoiceCallDialog");
    }
}
