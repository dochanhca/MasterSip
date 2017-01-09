package jp.newbees.mastersip.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.call.OutgoingVoiceActivity;
import jp.newbees.mastersip.ui.dialog.ConfirmVoiceCallDialog;
import jp.newbees.mastersip.ui.top.ChatActivity;

/**
 * Created by ducpv on 1/5/17.
 */

public class ProfileDetailFragment extends BaseFragment implements ConfirmVoiceCallDialog.OnDialogConfirmVoiceCallClick {

    private static final int CONFIRM_VOICE_CALL_DIALOG = 10;

    public static ProfileDetailFragment newInstance() {
        Bundle args = new Bundle();
        ProfileDetailFragment fragment = new ProfileDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_profile_detail;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        setFragmentTitle("User Name Here");
    }

    @OnClick({R.id.img_back, R.id.layout_chat, R.id.layout_voice_call, R.id.layout_video_call})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.layout_chat:
                ChatActivity.start(getContext());
                break;
            case R.id.layout_voice_call:
                ConfirmVoiceCallDialog.openConfirmVoiceCallDialogFromFragment(this,
                        CONFIRM_VOICE_CALL_DIALOG, getFragmentManager());
                break;
            case R.id.layout_video_call:
                break;
            default:
                break;
        }
    }

    @Override
    public void onOkClick() {
        Intent intent = new Intent(getActivity().getApplicationContext(), OutgoingVoiceActivity.class);
        startActivity(intent);
    }
}
