package jp.newbees.mastersip.ui.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoButton;

/**
 * Created by ducpv on 3/15/17.
 */

public class PaymentDialog extends DialogFragment {

    @BindView(R.id.recycler_purchase_item)
    RecyclerView recyclerPurchaseItem;
    @BindView(R.id.btn_cancel)
    HiraginoButton btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        View root = inflater.inflate(R.layout.dialog_payment, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.btn_cancel)
    public void onClick() {
        this.dismiss();
    }

    public static void openPaymentDialog(FragmentManager fragmentManager) {
        PaymentDialog paymentDialog = new PaymentDialog();

        Bundle args = new Bundle();
        paymentDialog.setArguments(args);
        paymentDialog.show(fragmentManager, "PaymentDialog");
    }
}
