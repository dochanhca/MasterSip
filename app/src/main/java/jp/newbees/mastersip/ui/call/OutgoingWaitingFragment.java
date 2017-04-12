package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.thread.MyCountingTimerThread;
import jp.newbees.mastersip.ui.call.base.WaitingFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by thangit14 on 3/15/17.
 */

public class OutgoingWaitingFragment extends WaitingFragment implements View.OnClickListener {
    private static final int MAX_WAITING_TIME = 15;

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    HiraginoTextView txtUserName;
    @BindView(R.id.txt_timer)
    HiraginoTextView txtTimer;
    @BindView(R.id.txt_notify_low_signal)
    HiraginoTextView txtNotifyLowSignal;
    @BindView(R.id.img_loading)
    ImageView imgLoading;
    @BindView(R.id.ll_point)
    LinearLayout llPoint;
    @BindView(R.id.txt_point)
    HiraginoTextView txtPoint;
    @BindView(R.id.view_stub_action)
    ViewStub viewStubAction;

    private ToggleButton btnOnOffMic;
    private ToggleButton btnOnOffSpeaker;
    private LinearLayout llOnOffSpeaker;
    private LinearLayout llOnOffMic;
    private ImageView btnCancelCall;
    private TextView txtCancelCall;

    private Handler waitingTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (getCallActivity() != null) {
                terminalCall();
            }
        }
    };
    private MyCountingTimerThread countWaitingTimeThread;

    public static OutgoingWaitingFragment newInstance(UserItem callee, String callID,
                                                      String titleCall, int callType) {

        Bundle args = WaitingFragment.getBundle(callee, callID, 0, titleCall, callType);
        OutgoingWaitingFragment fragment = new OutgoingWaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_outgoing_waiting;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        super.init(mRoot, savedInstanceState);
        ButterKnife.bind(this, mRoot);
        updateView();
        countWaitingTime();
    }

    @Override
    public void onDestroy() {
        if (countWaitingTimeThread != null) {
            countWaitingTimeThread.turnOffCounting();
        }
        super.onDestroy();
    }

    private void updateView() {
        Glide.with(this).load(R.drawable.pinpoint)
                .asGif()
                .into(imgLoading);

        txtTimer.setText(getTitleCall());

        txtUserName.setText(getCompetitor().getUsername());
        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (getCompetitor().getAvatarItem() != null) {
            Glide.with(this).load(getCompetitor().getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);

        if (getCallType() == Constant.API.VIDEO_CHAT_CALL) {
            inflateViewAction(R.layout.layout_calling_two_action);

            if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.MALE) {
                btnOnOffMic.setChecked(false);
                llOnOffMic.setVisibility(View.GONE);
                btnOnOffSpeaker.setChecked(true);
            } else {
                btnOnOffSpeaker.setChecked(false);
                llOnOffSpeaker.setVisibility(View.GONE);
                btnOnOffMic.setChecked(true);
            }
        } else {
            inflateViewAction(R.layout.layout_calling_three_action);
            if (getCallType() == Constant.API.VOICE_CALL) {
                btnOnOffSpeaker.setChecked(false);
                btnOnOffMic.setChecked(true);
            } else if (getCallType() == Constant.API.VIDEO_CALL) {
                btnOnOffSpeaker.setChecked(true);
                btnOnOffMic.setChecked(true);
            }
        }
    }

    private void inflateViewAction(int layout) {
        viewStubAction.setLayoutResource(layout);
        View view = viewStubAction.inflate();
        llOnOffMic = (LinearLayout) view.findViewById(R.id.ll_on_off_mic);
        llOnOffSpeaker = (LinearLayout) view.findViewById(R.id.ll_on_off_speaker);
        btnOnOffMic = (ToggleButton) view.findViewById(R.id.btn_on_off_mic);
        btnOnOffSpeaker = (ToggleButton) view.findViewById(R.id.btn_on_off_speaker);
        btnCancelCall = (ImageView) view.findViewById(R.id.btn_cancel_call);
        txtCancelCall = (TextView) view.findViewById(R.id.txt_cancel_call);
        btnCancelCall.setOnClickListener(this);
        btnOnOffMic.setOnClickListener(this);
        btnOnOffSpeaker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_on_off_mic:
                enableMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                terminalCall();
                break;
            case R.id.btn_on_off_speaker:
                enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
            default:
                break;
        }
    }

    public boolean isSpeakerEnable() {
        return btnOnOffSpeaker.isChecked();
    }

    public boolean isMicEnable() {
        return btnOnOffMic.isChecked();
    }

    @Override
    protected void onCallingBreakTime(Message msg) {
        txtTimer.setText(DateTimeUtils.getTimerCallString(msg.what));
    }

    @Override
    public TextView getTxtPoint() {
        return txtPoint;
    }

    private void countWaitingTime() {
        countWaitingTimeThread = new MyCountingTimerThread(waitingTimeHandler, MAX_WAITING_TIME);
        new Thread(countWaitingTimeThread).start();
    }


    @Override
    public void countingCallDuration() {
        super.countingCallDuration();
        if (countWaitingTimeThread != null) {
            countWaitingTimeThread.turnOffCounting();
            countWaitingTimeThread = null;
        }
    }

    @Override
    public void updateViewWhenVoiceConnected() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }
        imgLoading.setVisibility(View.GONE);

        enableSpeaker(btnOnOffSpeaker.isChecked());
        enableMicrophone(btnOnOffMic.isChecked());
        txtCancelCall.setText(getString(R.string.end));
    }

    @Override
    public void onCallPaused() {
        txtTimer.setVisibility(View.INVISIBLE);
        txtNotifyLowSignal.setVisibility(View.VISIBLE);
    }

    @Override
    public final void onCallResume() {
        txtTimer.setVisibility(View.VISIBLE);
        txtNotifyLowSignal.setVisibility(View.INVISIBLE);
    }
}
