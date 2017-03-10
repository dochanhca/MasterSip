package jp.newbees.mastersip.ui.call;

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
import jp.newbees.mastersip.event.call.CoinChangedEvent;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.ui.call.base.BaseHandleOutgoingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class OutgoingVoiceActivity extends BaseHandleOutgoingCallActivity {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    HiraginoTextView txtUserName;
    @BindView(R.id.txt_timer)
    HiraginoTextView txtTimer;
    @BindView(R.id.img_loading)
    ImageView imgLoading;
    @BindView(R.id.btn_on_off_mic)
    ToggleButton btnOnOffMic;
    @BindView(R.id.btn_on_off_speaker)
    ToggleButton btnOnOffSpeaker;
    @BindView(R.id.btn_cancel_call)
    ImageView btnCancelCall;
    @BindView(R.id.ll_point)
    LinearLayout llPoint;
    @BindView(R.id.txt_point)
    HiraginoTextView txtPoint;

    private Handler timerHandler = new Handler();

    @Override
    protected int layoutId() {
        return R.layout.activity_out_going_voice;
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

    @OnClick({R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_on_off_mic:
                super.muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                endCall();
                break;
            case R.id.btn_on_off_speaker:
                super.enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
            default:
                break;
        }
    }

    // start when user during a call
    private void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTimer, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }

    // start when user during a call
    private void updateView() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }
        imgLoading.setVisibility(View.GONE);
    }

    @Override
    public void onCallConnected() {
        countingCallDuration();
        this.updateView();
    }

    @Override
    public void onCallEnd() {
        this.finish();
    }

    @Override
    public void onCoinChanged(CoinChangedEvent event) {
        StringBuilder point = new StringBuilder();
        point.append(" ")
                .append(String.valueOf(event.getCoin()))
                .append(getString(R.string.pt));
        txtPoint.setText(point);
    }

    @Override
    protected int getCallType() {
        return Constant.API.VOICE_CALL;
    }
}
