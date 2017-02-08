package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 1/25/17.
 */

public class ConfirmSendGiftDialog extends BaseDialog implements View.OnClickListener {

    private static final String USER_NAME = "USER_NAME";
    private TextView txtDialogContent;

    public interface OnConfirmSendGiftDialog {
        void onOkConfirmSendGiftClick();
    }

    private OnConfirmSendGiftDialog onConfirmSendGiftDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.onConfirmSendGiftDialog = (OnConfirmSendGiftDialog) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
            }
        }
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        txtDialogContent = (TextView) rootView.findViewById(R.id.txt_dialog_content);

        String userName = getArguments().getString(USER_NAME);
        StringBuilder content = new StringBuilder();
        content.append(userName).append(getString(R.string.notify_follow_user_success));

        txtDialogContent.setText(content.toString());

        setPositiveButtonContent(getString(R.string.send_a_give));

        setOnPositiveListener(this);
        setOnNegativeListener(this);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_confirm_send_gift;
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonPositive) {
            this.onConfirmSendGiftDialog.onOkConfirmSendGiftClick();
        }

        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.onConfirmSendGiftDialog = (OnConfirmSendGiftDialog) context;
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling activity must implement DialogClickListener interface");
            }
        }
    }

    /**
     * Call from fragment
     * @param fragment
     * @param requestCode
     * @param fragmentManager
     */
    public static void openConfirmSendGiftDialog(Fragment fragment, int requestCode,
                                                  FragmentManager fragmentManager, String userName) {
        ConfirmSendGiftDialog confirmSendGiftDialog = new ConfirmSendGiftDialog();

        Bundle bundle = new Bundle();
        bundle.putString(USER_NAME, userName);

        confirmSendGiftDialog.setArguments(bundle);
        confirmSendGiftDialog.setTargetFragment(fragment, requestCode);
        confirmSendGiftDialog.show(fragmentManager, "ConfirmSendGiftDialog");
    }

    public static void openConfirmSendGiftDialog(FragmentManager fragmentManager, String userName) {
        ConfirmSendGiftDialog confirmSendGiftDialog = new ConfirmSendGiftDialog();

        Bundle bundle = new Bundle();
        bundle.putString(USER_NAME, userName);

        confirmSendGiftDialog.setArguments(bundle);
        confirmSendGiftDialog.show(fragmentManager, "ConfirmSendGiftDialog");
    }
}
