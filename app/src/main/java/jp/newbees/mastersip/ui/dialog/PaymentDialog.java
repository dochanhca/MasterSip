package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.PaymentAdOnAdapter;
import jp.newbees.mastersip.customviews.HiraginoButton;
import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.presenter.PaymentAdOnPresenter;

/**
 * Created by ducpv on 3/15/17.
 */

public class PaymentDialog extends DialogFragment implements PaymentAdOnPresenter.View, PaymentAdOnAdapter.OnItemClickListener {

    @BindView(R.id.recycler_purchase_item)
    RecyclerView recyclerPurchaseItem;
    @BindView(R.id.btn_cancel)
    HiraginoButton btnCancel;

    private PaymentAdOnAdapter adOnAdapter;

    private PaymentAdOnPresenter presenter;
    private OnPaymentDialogClickListener onPaymentDialogClickListener;

    public interface OnPaymentDialogClickListener {
        void onPaymentItemClick(PaymentAdOnItem item);
    }

    public static void openPaymentDialog(FragmentManager fragmentManager) {
        PaymentDialog paymentDialog = new PaymentDialog();

        Bundle args = new Bundle();
        paymentDialog.setArguments(args);
        paymentDialog.show(fragmentManager, "PaymentDialog");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.onPaymentDialogClickListener = (OnPaymentDialogClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_payment, container, false);
        ButterKnife.bind(this, root);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);

        initRecyclerPaymentAdOn();

        presenter = new PaymentAdOnPresenter(getActivity().getApplicationContext(), this);
        presenter.getListPaymentAdOn();

        return root;
    }

    @OnClick(R.id.btn_cancel)
    public void onClick() {
        this.dismiss();
    }

    @Override
    public void didGetListPaymentAdOn(List<PaymentAdOnItem> paymentAdOnItems) {
        adOnAdapter.addAll(paymentAdOnItems);
    }

    @Override
    public void didGetListPaymentAdOnError(int errorCode, String errorMessage) {
        // Get List payment adOn error
    }

    @Override
    public void onItemClick(int position) {
        PaymentAdOnItem item = adOnAdapter.getData().get(position);
        onPaymentDialogClickListener.onPaymentItemClick(item);
    }

    private void initRecyclerPaymentAdOn() {
        adOnAdapter = new PaymentAdOnAdapter(getActivity().getApplicationContext(), new ArrayList<PaymentAdOnItem>());
        adOnAdapter.setOnItemClickListener(this);

        recyclerPurchaseItem.setAdapter(adOnAdapter);
        recyclerPurchaseItem.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }
}
