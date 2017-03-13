package jp.newbees.mastersip.ui.call;

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
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.ui.call.base.BaseHandleIncomingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class IncomingVoiceActivity extends BaseHandleIncomingCallActivity {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    HiraginoTextView txtUserName;
    @BindView(R.id.txt_timer)
    HiraginoTextView txtTimer;
    @BindView(R.id.img_loading)
    ImageView imgLoading;
    @BindView(R.id.btn_reject_call)
    ImageView btnRejectCall;
    @BindView(R.id.btn_accept_call)
    ImageView btnAcceptCall;
    @BindView(R.id.btn_on_off_mic)
    ToggleButton btnOnOffMic;
    @BindView(R.id.btn_cancel_call)
    ImageView btnCancelCall;
    @BindView(R.id.btn_on_off_speaker)
    ToggleButton btnOnOffSpeaker;
    @BindView(R.id.ll_point)
    LinearLayout llPoint;
    @BindView(R.id.txt_point)
    HiraginoTextView txtPoint;
    @BindView(R.id.layout_receiving_call_action)
    ViewGroup layoutReceivingCallAction;
    @BindView(R.id.layout_voice_calling_action)
    ViewGroup layoutVoiceCallingAction;

    private Handler timerHandler = new Handler();

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
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        txtUserName.setText(getCaller().getUsername());

        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (getCaller().getAvatarItem() != null) {
            Glide.with(this).load(getCaller().getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);
    }

    /**
     * start when user during a call
     */
    private void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTimer, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }

    /**
     * start when user during a call
     */
    private void showCallingView() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }

        layoutVoiceCallingAction.setVisibility(View.VISIBLE);
        layoutReceivingCallAction.setVisibility(View.GONE);
        imgLoading.setVisibility(View.GONE);
    }


    @OnClick({R.id.btn_reject_call, R.id.btn_accept_call, R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reject_call:
                super.rejectCall(getCaller().getSipItem().getExtension(), Constant.API.VOICE_CALL, getCallId());
                break;
            case R.id.btn_accept_call:
                super.acceptCall(getCallId());
                break;
            case R.id.btn_on_off_mic:
                super.muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                super.endCall(getCaller().getSipItem().getExtension(), Constant.API.VOICE_CALL, getCallId());
                break;
            case R.id.btn_on_off_speaker:
                super.enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
        }
    }

    @Override
    public void onCallConnected() {
        countingCallDuration();
        enableSpeaker(btnOnOffSpeaker.isChecked());
        this.showCallingView();
    }

    @Override
    public void onCoinChanged(int coint) {
        StringBuilder point = new StringBuilder();
        point.append(String.valueOf(coint)).append(getString(R.string.pt));
        txtPoint.setText(point);
    }

    @Override
    protected int getCallType() {
        return Constant.API.VOICE_CALL;
    }
}
