package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import jp.newbees.mastersip.R;

/**
 * Created by ducpv on 3/13/17.
 */

public class NotifyRunOutOfCoinDialog extends BaseDialog implements View.OnClickListener {

    public interface NotifyRunOutOfCoinDialogClick {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }

    private NotifyRunOutOfCoinDialogClick notifyRunOutOfCoinDialog;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.notifyRunOutOfCoinDialog = (NotifyRunOutOfCoinDialogClick) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(e.getMessage());
            }
        }
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        setPositiveButtonContent(getString(R.string.purchase_points));
        setCancelable(false);
        setOnPositiveListener(this);
        setOnNegativeListener(this);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_run_of_coin;
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonPositive) {
            notifyRunOutOfCoinDialog.onPositiveButtonClick();
        } else if (v == mButtonNegative) {
            notifyRunOutOfCoinDialog.onNegativeButtonClick();
        }

        dismiss();
    }

    /**
     * Call from activity
     * @param fragmentManager
     */
    public static void openNotifyRunOutOfCoinDialog(FragmentManager fragmentManager) {
        NotifyRunOutOfCoinDialog notifyRunOutOfCoinDialog = new NotifyRunOutOfCoinDialog();
        Bundle bundle = new Bundle();
        notifyRunOutOfCoinDialog.setArguments(bundle);
        notifyRunOutOfCoinDialog.show(fragmentManager, "NotifyRunOutOfCoinDialog");
    }
}
