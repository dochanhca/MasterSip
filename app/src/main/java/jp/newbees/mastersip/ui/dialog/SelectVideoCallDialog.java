package jp.newbees.mastersip.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;

/**
 * Created by vietbq on 12/6/16.
 */

public class SelectVideoCallDialog extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.ic_close)
    ImageView icClose;
    @BindView(R.id.txt_title_chat_video)
    HiraginoTextView txtTitleChatVideo;
    @BindView(R.id.img_chat_video_opponent)
    ImageView imgChatVideoOpponent;
    @BindView(R.id.img_chat_video_my_self)
    ImageView imgChatVideoMySelf;
    @BindView(R.id.txt_title_call_camera)
    HiraginoTextView txtTitleCallCamera;

    private View mRoot;
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
        ButterKnife.bind(this, mRoot);
        setTitle();
        return mRoot;
    }

    private void setTitle() {
        txtTitleCallCamera.setText(Html.fromHtml(getResources().getString(R.string.title_select_call)));
    }

    @OnClick({R.id.ll_chat_video, R.id.ll_video_video ,R.id.ic_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_chat_video:
                listener.onSelectedVideoCall(VideoCall.CHAT_VIDEO);
                break;
            case R.id.ll_video_video:
                listener.onSelectedVideoCall(VideoCall.VIDEO_VIDEO);
                break;
            case R.id.ic_close:
            default:
                break;
        }
        this.dismiss();
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
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
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

    public static void openDialog(Fragment fragment, int requestCode,
                                  FragmentManager fragmentManager) {
        SelectVideoCallDialog dialog = new SelectVideoCallDialog();
        dialog.setTargetFragment(fragment, requestCode);
        dialog.show(fragmentManager, "SelectVideoCallDialog");
    }

    public static void openDialog(FragmentManager fragmentManager) {
        SelectVideoCallDialog dialog = new SelectVideoCallDialog();
        dialog.show(fragmentManager, "SelectVideoCallDialog");
    }
}
