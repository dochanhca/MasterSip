package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 12/6/16.
 */

public class SelectVideoCallDialog extends DialogFragment implements View.OnClickListener {

    private View mRoot;
    private TextView txtVideoVideo;
    private TextView txtTextVideo;
    private OnSelectVideoCallDialog listener;

    public interface OnSelectVideoCallDialog {
        void onSelectedVideoCall(VideoCall videoCall);
    }

    public enum VideoCall {
        VIDEO_VIDEO, CHAT_VIDEO
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        mRoot = inflater.inflate(R.layout.dialog_select_video_call, null);
        txtVideoVideo = (TextView) mRoot.findViewById(R.id.video_video);
        txtTextVideo = (TextView) mRoot.findViewById(R.id.text_video);
        txtTextVideo.setOnClickListener(this);
        txtVideoVideo.setOnClickListener(this);

        return mRoot;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.video_video) {
            listener.onSelectedVideoCall(VideoCall.VIDEO_VIDEO);
        } else {
            listener.onSelectedVideoCall(VideoCall.CHAT_VIDEO);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.listener = (OnSelectVideoCallDialog) context;
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling activity must implement DialogClickListener interface");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.listener = (OnSelectVideoCallDialog) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
            }
        }
    }

    public static void openDialog(Fragment fragment, int requestCode, OnSelectVideoCallDialog onSelectVideoCallDialog,
                                  FragmentManager fragmentManager) {
        SelectVideoCallDialog dialog = new SelectVideoCallDialog();
        dialog.setTargetFragment(fragment, requestCode);
        dialog.show(fragmentManager, "SelectVideoCallDialog");
    }
}
