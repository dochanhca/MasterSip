package jp.newbees.mastersip.ui.call.base;

import android.os.Bundle;
import android.view.View;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by thangit14 on 4/7/17.
 *  inherited by incoming and outgoing waiting fragment
 */

public abstract class WaitingFragment extends CallingFragment{
    private static final String COMPETITOR = "COMPETITOR";
    private static final String CALL_TYPE = "CALL_TYPE";
    private static final String CALL_ID = "CALL_ID";
    private static final String ACCEPT_CALL_IMAGE = "ACCEPT_CALL_IMAGE";
    private static final String TITLE_CALL = "TITLE_CALL";

    private UserItem competitor;
    private String callId;
    private String titleCall;
    private int callType;
    private int acceptCallImage;

    public abstract void updateViewWhenVoiceConnected();

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        getArgs();
    }

    private void getArgs() {
        Bundle args = getArguments();
        competitor = args.getParcelable(COMPETITOR);
        callId = args.getString(CALL_ID);
        acceptCallImage = args.getInt(ACCEPT_CALL_IMAGE);
        titleCall = args.getString(TITLE_CALL);
        callType = args.getInt(CALL_TYPE);
    }

    public static Bundle getBundle(UserItem competitor, String callId, int acceptCallImage, String titleCall, int callType) {
        Bundle args = new Bundle();
        args.putString(TITLE_CALL, titleCall);
        args.putInt(ACCEPT_CALL_IMAGE, acceptCallImage);
        args.putString(CALL_ID, callId);
        args.putParcelable(COMPETITOR, competitor);
        args.putInt(CALL_TYPE, callType);
        return args;
    }

    public UserItem getCompetitor() {
        return competitor;
    }

    public String getCallId() {
        return callId;
    }

    public String getTitleCall() {
        return titleCall;
    }

    public int getCallType() {
        return callType;
    }

    public int getAcceptCallImage() {
        return acceptCallImage;
    }
}
