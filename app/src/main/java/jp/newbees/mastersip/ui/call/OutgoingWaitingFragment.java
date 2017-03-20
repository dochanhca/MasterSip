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
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.thread.CountingTimeThread;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.call.base.BaseHandleOutgoingCallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by thangit14 on 3/15/17.
 */

public class OutgoingWaitingFragment extends BaseFragment {
    private static final String CALLEE = "CALLEE";
    private static final String CALL_TYPE = "CALL_TYPE";
    private static final String TITLE_CALL = "TITLE_CALL";

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

    private UserItem callee;
    private String titleCall;
    private int callType;

    private Handler timerHandler = new Handler();

    public static OutgoingWaitingFragment newInstance(UserItem callee,
                                                      String titleCall, int callType) {

        Bundle args = new Bundle();
        args.putParcelable(CALLEE, callee);
        args.putInt(CALL_TYPE, callType);
        args.putString(TITLE_CALL, titleCall);

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
        ButterKnife.bind(this, mRoot);

        getArgs();

        updateView();
    }

    private void getArgs() {
        Bundle bundle = getArguments();
        callee = bundle.getParcelable(CALLEE);
        titleCall = bundle.getString(TITLE_CALL);
        callType = bundle.getInt(CALL_TYPE);
    }

    private void updateView() {
        Glide.with(this).load(R.drawable.pinpoint)
                .asGif()
                .into(imgLoading);

        txtTimer.setText(titleCall);

        txtUserName.setText(callee.getUsername());
        int imageID = ConfigManager.getInstance().getImageCalleeDefault();
        if (callee.getAvatarItem() != null) {
            Glide.with(this).load(callee.getAvatarItem().getOriginUrl())
                    .error(imageID).placeholder(imageID)
                    .centerCrop()
                    .into(profileImage);
        }
        profileImage.setImageResource(imageID);
        if (callType != Constant.API.VOICE_CALL) {
            btnOnOffSpeaker.setChecked(true);
            enableSpeaker(true);
        } else {
            enableSpeaker(false);
        }
    }

    @OnClick({R.id.btn_on_off_mic, R.id.btn_cancel_call, R.id.btn_on_off_speaker})
    public void onClick(View view) {
        BaseHandleOutgoingCallActivity activity = ((BaseHandleOutgoingCallActivity) getActivity());

        switch (view.getId()) {
            case R.id.btn_on_off_mic:
                activity.muteMicrophone(btnOnOffMic.isChecked());
                break;
            case R.id.btn_cancel_call:
                activity.endCall();
                break;
            case R.id.btn_on_off_speaker:
                activity.enableSpeaker(btnOnOffSpeaker.isChecked());
                break;
            default:
                break;
        }
    }

    public void enableSpeaker(boolean enable) {
        BaseHandleOutgoingCallActivity activity = ((BaseHandleOutgoingCallActivity) getActivity());
        activity.enableSpeaker(enable);
    }

    public boolean isSpeakerEnable() {
        return !btnOnOffSpeaker.isChecked();
    }

    public boolean muteMic() {
        return btnOnOffMic.isChecked();
    }

    public void onCoinChange(int coin) {
        StringBuilder point = new StringBuilder();
        point.append(" ")
                .append(String.valueOf(coin))
                .append(getString(R.string.pt));
        txtPoint.setText(point.toString());
    }

    // start when user during a call
    public void countingCallDuration() {
        CountingTimeThread countingTimeThread = new CountingTimeThread(txtTimer, timerHandler);
        timerHandler.postDelayed(countingTimeThread, 0);
    }

    public void updateViewWhenVoiceConnected() {
        // Only Counting point with female user
        if (ConfigManager.getInstance().getCurrentUser().getGender() == UserItem.FEMALE) {
            llPoint.setVisibility(View.VISIBLE);
        }
        imgLoading.setVisibility(View.GONE);
    }
}