package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.os.Handler;
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
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.call.BaseHandleOutgoingCallPresenter;
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by vietbq on 1/11/17.
 */

public abstract class BaseHandleOutgoingCallActivity extends BaseActivity implements BaseHandleOutgoingCallPresenter.OutgoingCallView {
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
    private UserItem callee;
    private int callType;

    private Handler timerHandler = new Handler();

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

    protected abstract String getTextTitle();

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
    public void onCallEnd() {
        this.finish();
    }

    @Override
    public void onCoinChanged(int coin) {
        updateCoinChange(coin);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterEvents();
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

    @Override
    public void onBackPressed() {
//        Prevent user press back button when during a call
    }

    protected abstract int getCallType();
}
