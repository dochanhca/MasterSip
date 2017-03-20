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
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.call.base.BaseHandleIncomingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;

/**
 * Created by thangit14 on 3/13/17.
 */

public class IncomingWaitingFragment extends BaseFragment {
    private static final String CALLER = "CALLER";
    private static final String CALL_TYPE = "CALL_TYPE";
    private static final String CALL_ID = "CALL_ID";
    private static final String ACCEPT_CALL_IMAGE = "ACCEPT_CALL_IMAGE";
    private static final String TITLE_CALL = "TITLE_CALL";

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

    private Handler timerHandler = new Handler();

    private UserItem caller;
    private String callId;
    private int callType;
    private int acceptCallImage;
    private String titleCall;

    public static IncomingWaitingFragment newInstance(UserItem caller, String callId,
                                                      int acceptCallImage, String titleCall, int callType) {

        Bundle args = new Bundle();
        args.putString(TITLE_CALL, titleCall);
        args.putInt(ACCEPT_CALL_IMAGE, acceptCallImage);
        args.putString(CALL_ID, callId);
        args.putParcelable(CALLER, caller);
        args.putInt(CALL_TYPE, callType);
        IncomingWaitingFragment fragment = new IncomingWaitingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int layoutId() {
        return R.layout.fragment_incomint_wating;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);

        getArgs();
        updateView();
    }

    @OnClick({R.id.btn_reject_call, R.id.btn_accept_call, R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        BaseHandleIncomingCallActivity activity = ((BaseHandleIncomingCallActivity) getActivity());
        switch (view.getId()) {
            case R.id.btn_reject_call:
                activity.rejectCall(caller.getSipItem().getExtension(), callType, callId);
                break;
            case R.id.btn_accept_call:
                activity.acceptCall(callId);
                break;
            case R.id.btn_on_off_mic:
                activity.muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                activity.endCall(caller.getSipItem().getExtension(), callType, callId);
                break;
            case R.id.btn_on_off_speaker:
                activity.enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
        }
    }

    private void updateView() {
        txtUserName.setText(caller.getUsername());

        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (caller.getAvatarItem() != null) {
            Glide.with(this).load(caller.getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);

        Glide.with(this).load(R.drawable.pinpoint)
                .asGif()
                .into(imgLoading);
        txtTimer.setText(titleCall);
        btnAcceptCall.setImageResource(acceptCallImage);
    }

    private void getArgs() {
        Bundle args = getArguments();
        caller = args.getParcelable(CALLER);
        callId = args.getString(CALL_ID);
        acceptCallImage = args.getInt(ACCEPT_CALL_IMAGE);
        titleCall = args.getString(TITLE_CALL);
        callType = args.getInt(CALL_TYPE);
    }

    /**
     * using for voice call only
     */
    public void showCallingViewOnVoiceCall() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }

        layoutVoiceCallingAction.setVisibility(View.VISIBLE);
        layoutReceivingCallAction.setVisibility(View.GONE);
        imgLoading.setVisibility(View.GONE);
    }

    /**
     * start when user during a call
     */
    public void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTimer, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }

    public void onCoinChanged(int coin) {
        if (isDetached()) return;

        StringBuilder point = new StringBuilder();
        point.append(String.valueOf(coin)).append(getString(R.string.pt));
        txtPoint.setText(point);
    }

    public boolean isEnableSpeaker() {
        return btnOnOffSpeaker.isChecked();
    }
}