package jp.newbees.mastersip.ui.call.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.PaymentAdOnItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.TopPresenter;
import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.purchase.IabHelper;
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.PaymentDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseActivity implements
        BaseHandleOutgoingCallPresenter.OutgoingCallView, TopPresenter.TopPresenterListener,
        PaymentDialog.OnPaymentDialogClickListener {
    @BindView(R.id.profile_image)
    protected CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    protected HiraginoTextView txtUserName;
    @BindView(R.id.txt_timer)
    protected HiraginoTextView txtTimer;
    @BindView(R.id.img_loading)
    protected ImageView imgLoading;
    @BindView(R.id.btn_on_off_mic)
    protected ToggleButton btnOnOffMic;
    @BindView(R.id.btn_on_off_speaker)
    protected ToggleButton btnOnOffSpeaker;
    @BindView(R.id.btn_cancel_call)
    protected ImageView btnCancelCall;
    @BindView(R.id.ll_point)
    protected LinearLayout llPoint;
    @BindView(R.id.txt_point)
    protected HiraginoTextView txtPoint;

    protected static final String CALLEE = "CALLEE";

    private BaseHandleOutgoingCallPresenter presenter;
    private TopPresenter topPresenter;
    private UserItem callee;
    private int callType;

    private Handler timerHandler = new Handler();

    protected abstract int getCallType();
    protected abstract String getTextTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        this.callType = getCallType();
        this.presenter = new BaseHandleOutgoingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
    }

    protected final UserItem getCallee() {
        if (callee == null) {
            this.callee = getIntent().getExtras().getParcelable(CALLEE);
        }
        return callee;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.pinpoint)
                .asGif()
                .into(imgLoading);

        txtTimer.setText(getTextTitle());
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        txtUserName.setText(getCallee().getUsername());
        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (getCallee().getAvatarItem() != null) {
            Glide.with(this).load(getCallee().getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_out_going_voice;
    }

    @OnClick({R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_on_off_mic:
                muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                endCall();
                break;
            case R.id.btn_on_off_speaker:
                enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getIabHelper() == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (!getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.e("PaymentDialog:", "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onCallEnd() {
        this.finish();
    }

    @Override
    public void onCoinChanged(int coin) {
        updateCoinChange(coin);
    }

    @Override
    public void onRunningOutOfCoin() {
        topPresenter = new TopPresenter(this, this);
        PaymentDialog.openPaymentDialog(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    @Override
    public void onPaymentItemClick(PaymentAdOnItem item) {
        topPresenter.performPurchaseItem(item.getId());
    }

    @Override
    public void onInAppBillingSuccess(String sku, String token) {
        showLoading();
        topPresenter.sendPurchaseResultToServer(TopPresenter.PurchaseStatus.SUCCESS, sku, token);
    }

    @Override
    public void onPurchaseError(int errorCode, String errorMessage, String sku, String token) {
        TopPresenter.PurchaseStatus status;
        if (errorCode == Constant.Error.IN_APP_PURCHASE_NOT_SUCCESS) {
            status = TopPresenter.PurchaseStatus.NOT_SUCCESS;
            topPresenter.sendPurchaseResultToServer(status, sku, token);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_FAIL) {
            status = TopPresenter.PurchaseStatus.FAIL;
            topPresenter.sendPurchaseResultToServer(status, sku, token);
        } else if (errorCode == Constant.Error.IN_APP_PURCHASE_CANCEL) {
            disMissLoading();
            showMessageDialog(getString(R.string.cancel_purchase));
        }
    }

    @Override
    public void onSendPurchaseResultToServerSuccess(int point) {
        disMissLoading();
        showMessageDialog(String.format(getString(R.string.purchase_success), point + ""));
    }

    @Override
    public void onSendPurchaseResultToServerError(int errorCode, String errorMessage) {
        // Send Payment Result to server error
        disMissLoading();
    }

    // start when user during a call
    protected void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTimer, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }

    private void updateCoinChange(int coin) {
        StringBuilder point = new StringBuilder();
        point.append(" ")
                .append(String.valueOf(coin))
                .append(getString(R.string.pt));
        txtPoint.setText(point);
    }

    public final void endCall() {
        this.presenter.endCall(callee, callType);
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    public final void changeCamera() {

    }

    public final void enableCamera(boolean enableCamera) {

    }

    private IabHelper getIabHelper() {
        return topPresenter.getIabHelper();
    }
}
