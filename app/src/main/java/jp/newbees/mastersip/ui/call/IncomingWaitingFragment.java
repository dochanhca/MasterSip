package jp.newbees.mastersip.ui.call;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import org.linphone.core.LinphoneCoreException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.call.base.WaitingFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by thangit14 on 3/13/17.
 */

public class IncomingWaitingFragment extends WaitingFragment {
    @BindView(R.id.profile_image)
    protected CircleImageView profileImage;
    @BindView(R.id.txt_user_name)
    protected HiraginoTextView txtUserName;
    @BindView(R.id.txt_timer)
    protected HiraginoTextView txtTimer;
    @BindView(R.id.txt_notify_low_signal)
    protected HiraginoTextView txtNotifyLowSignal;
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
    @BindView(R.id.txt_cancel_call)
    protected TextView txtCancelCall;
    @BindView(R.id.ll_point)
    protected LinearLayout llPoint;
    @BindView(R.id.txt_point)
    protected HiraginoTextView txtPoint;
    @BindView(R.id.layout_receiving_call_action)
    protected ViewGroup layoutReceivingCallAction;
    @BindView(R.id.layout_voice_calling_action)
    protected ViewGroup layoutVoiceCallingAction;

    public static IncomingWaitingFragment newInstance(UserItem competitor, String callId,
                                                      int acceptCallImage, String titleCall, int callType) {
        Bundle args = WaitingFragment.getBundle(competitor, callId, acceptCallImage, titleCall, callType);
        IncomingWaitingFragment fragment = new IncomingWaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_incoming_wating;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        super.init(mRoot,savedInstanceState);
        ButterKnife.bind(this, mRoot);
        updateView();
    }

    @OnClick({R.id.btn_reject_call, R.id.btn_accept_call, R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btn_reject_call:
                    declineCall();
                    break;
                case R.id.btn_accept_call:
                    acceptCall(getCallId(), getCallType());
                    break;
                case R.id.btn_on_off_mic:
                    enableMicrophone(btnOnOffMic.isChecked());
                    break;
                case R.id.btn_cancel_call:
                    terminalCall();
                    break;
                case R.id.btn_on_off_speaker:
                    enableSpeaker(btnOnOffSpeaker.isChecked());
                    break;
            }
        } catch (LinphoneCoreException e) {
            Logger.e(TAG, e.getMessage());
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() {
        txtUserName.setText(getCompetitor().getUsername());

        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (getCompetitor().getAvatarItem() != null) {
            Glide.with(this).load(getCompetitor().getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);

        Glide.with(this).load(R.drawable.pinpoint)
                .asGif()
                .into(imgLoading);
        txtTimer.setText(getTitleCall());
        btnAcceptCall.setImageResource(getAcceptCallImage());
    }

    @Override
    protected void onCallingBreakTime(Message msg) {
        txtTimer.setText(DateTimeUtils.getTimerCallString(msg.what));
    }

    @Override
    public TextView getTxtPoint() {
        return txtPoint;
    }

    @Override
    protected TextView getTxtCallStatus() {
        return txtNotifyLowSignal;
    }

    @Override
    public void onCallPaused() {
        txtTimer.setVisibility(View.INVISIBLE);
        txtNotifyLowSignal.setVisibility(View.VISIBLE);
    }

    @Override
    protected void updateUIWhenStartCalling() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }

        txtCancelCall.setText(getString(R.string.end));
        layoutVoiceCallingAction.setVisibility(View.VISIBLE);
        layoutReceivingCallAction.setVisibility(View.GONE);
        imgLoading.setVisibility(View.GONE);

        // setup default mic, speaker
        enableSpeaker(false);
        btnOnOffSpeaker.setChecked(false);
        enableMicrophone(true);
        btnOnOffMic.setChecked(true);

        countingCallDuration();
    }

    @Override
    public final void onCallResume() {
        txtTimer.setVisibility(View.VISIBLE);
        txtNotifyLowSignal.setVisibility(View.INVISIBLE);
    }
}
