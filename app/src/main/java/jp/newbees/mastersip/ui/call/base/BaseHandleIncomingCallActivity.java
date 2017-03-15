package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
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
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleIncomingCallPresenter;
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.PaymentDialog;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/10/17.
 */

public abstract class BaseHandleIncomingCallActivity extends BaseActivity implements
        BaseHandleIncomingCallPresenter.IncomingCallView {

    protected static final String CALLER = "CALLER";
    protected static final String CALL_ID = "CALL_ID";

    @BindView(R.id.profile_image)
    protected CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    protected HiraginoTextView txtUserName;
    @BindView(R.id.txt_timer)
    protected HiraginoTextView txtTimer;
    @BindView(R.id.img_loading)
    protected ImageView imgLoading;
    @BindView(R.id.btn_reject_call)
    protected ImageView btnRejectCall;
    @BindView(R.id.btn_accept_call)
    protected ImageView btnAcceptCall;
    @BindView(R.id.btn_on_off_mic)
    protected ToggleButton btnOnOffMic;
    @BindView(R.id.btn_cancel_call)
    protected ImageView btnCancelCall;
    @BindView(R.id.btn_on_off_speaker)
    protected ToggleButton btnOnOffSpeaker;
    @BindView(R.id.ll_point)
    protected LinearLayout llPoint;
    @BindView(R.id.txt_point)
    protected HiraginoTextView txtPoint;
    @BindView(R.id.layout_receiving_call_action)
    protected ViewGroup layoutReceivingCallAction;
    @BindView(R.id.layout_voice_calling_action)
    protected ViewGroup layoutVoiceCallingAction;

    private BaseHandleIncomingCallPresenter presenter;

    private int callType;

    private Handler timerHandler = new Handler();
    private UserItem caller;
    private String callId;

    protected abstract int getAcceptCallImage();

    protected abstract String getTitleCall();

    protected abstract int getCallType();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        this.presenter = new BaseHandleIncomingCallPresenter(getApplicationContext(), this);
        presenter.registerEvents();
        this.callType = getCallType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.checkFlashCall();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_in_coming_voice;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.pinpoint)
                .asGif()
                .into(imgLoading);
        txtTimer.setText(getTitleCall());
        btnAcceptCall.setImageResource(getAcceptCallImage());

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        caller = getIntent().getExtras().getParcelable(CALLER);
        callId = getIntent().getExtras().getString(CALL_ID);
        txtUserName.setText(caller.getUsername());

        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (caller.getAvatarItem() != null) {
            Glide.with(this).load(caller.getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);
    }

    @OnClick({R.id.btn_reject_call, R.id.btn_accept_call, R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reject_call:
                rejectCall(caller.getSipItem().getExtension(), callType, callId);
                break;
            case R.id.btn_accept_call:
                acceptCall(callId);
                break;
            case R.id.btn_on_off_mic:
                muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                endCall(caller.getSipItem().getExtension(), callType, callId);
                break;
            case R.id.btn_on_off_speaker:
                enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
        }
    }

    @Override
    public void onCoinChanged(int coin) {
        StringBuilder point = new StringBuilder();
        point.append(String.valueOf(coin)).append(getString(R.string.pt));
        txtPoint.setText(point);
    }

    @Override
    public void onRunningOutOfCoin() {
//        Toast.makeText(getApplicationContext(), "Running out of coin", Toast.LENGTH_LONG).show();
        PaymentDialog.openPaymentDialog(getSupportFragmentManager());
    }

    @Override
    public void onCallEnd() {
        this.finish();
    }

    @Override
    public void onFlashedCall() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    public final void rejectCall(String caller, int callType, String calId) {
        this.presenter.rejectCall(caller, callType, calId);
    }

    public final void acceptCall(String calId) {
        this.presenter.acceptCall(calId);
    }

    public final void endCall(String caller, int callType, String calId) {
        this.presenter.endCall(caller, callType, calId);
    }

    public final void enableSpeaker(boolean enable) {
        this.presenter.enableSpeaker(enable);
    }

    public final void muteMicrophone(boolean mute) {
        this.presenter.muteMicrophone(mute);
    }

    /**
     * start when user during a call
     */
    protected void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTimer, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }
}
