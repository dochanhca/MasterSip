package jp.newbees.mastersip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.dialog.MessageDialog;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseFragment extends Fragment {

    public static final String SHOW_FRAGMENT_ACTION_BAR = "SHOW_FRAGMENT_ACTION_BAR";
    protected View mRoot;

    protected TextView txtActionBarTitle;
    protected String TAG;
    private MessageDialog messageDialog;
    private ImageView imgBackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        Logger.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.e(TAG, "onCreateView");
        mRoot = inflater.inflate(layoutId(), null);
        init(mRoot, savedInstanceState);
        return mRoot;
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e(TAG, "onPause");
        restoreNavigationBarState();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    protected abstract int layoutId();

    protected abstract void init(View rootView, Bundle savedInstanceState);

    protected void setFragmentTitle(String title) {
        txtActionBarTitle = (TextView) mRoot.findViewById(R.id.txt_action_bar_title);
        imgBackButton = (ImageView) mRoot.findViewById(R.id.img_back);
        if (null != imgBackButton) imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageBackPressed();
                getFragmentManager().popBackStack();
            }
        });
        txtActionBarTitle.setText(title);
    }

    protected void showFragmentActionBar(boolean showFragmentActionBar) {
        View view = mRoot.findViewById(R.id.fragment_action_bar);
        if (view != null) {
            view.setVisibility(showFragmentActionBar ? View.VISIBLE : View.GONE);
        }
    }

    protected void hideFragmentActionBar() {
        showFragmentActionBar(false);
    }

    protected void showLoading() {
        ((BaseActivity) getActivity()).showLoading();
    }

    protected void disMissLoading() {
        try {
            ((BaseActivity) getActivity()).disMissLoading();
        } catch (NullPointerException e) {
            //Do nothing
        }
    }

    protected void showToastExceptionVolleyError(int errorCode, String errorMessage) {
        ((BaseActivity) getActivity()).showToastExceptionVolleyError(getActivity().getApplicationContext(),
                errorCode, errorMessage);
    }

    public final void showMessageDialog(String title, String content, String note,
                                        boolean isHideActionButton) {

        if (null == messageDialog) {
            messageDialog = new MessageDialog();
        }

        if (messageDialog.getDialog() != null && messageDialog.getDialog().isShowing()) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(MessageDialog.MESSAGE_DIALOG_TITLE, title);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_CONTENT, content);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_NOTE, note);
        bundle.putBoolean(MessageDialog.IS_HIDE_ACTION_BUTTON, isHideActionButton);

        messageDialog.setArguments(bundle);
        messageDialog.show(getActivity().getFragmentManager(), "MessageDialog");
    }

    protected void disMissMessageDialog() {
        if (null != messageDialog) {
            messageDialog.dismiss();
        }
    }

    public final void showMessageDialog(String message) {
        showMessageDialog("", message, "", false);
    }

    /**
     * clear animation and set visible of view after animation finish
     *
     * @param view
     * @param anim
     * @param visibility
     */
    protected void clearViewAnimation(final View view, Animation anim, final int visibility) {
        ((BaseActivity) getActivity()).clearViewAnimation(view, anim, visibility);
    }

    public void setTransitionAnimation(FragmentTransaction transaction) {
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
    }

    protected boolean isNavigationBarShowing() {
        return ((BaseActivity) getActivity()).isShowNavigationBar();
    }

    /**
     * Show navigation bar if state == INVISIBLE
     */
    protected void restoreNavigationBarState() {
        if (!isNavigationBarShowing()) {
            ((BaseActivity) getActivity()).showNavigation();
        }
    }

    /**
     * set listener from image back in action bar
     */
    protected void onImageBackPressed() {
        //Default do not anything
    }

    /**
     * set listener from button back in android hardware
     */
    protected void setOnBackPressed(BaseActivity.OnBackPressed listener) {
        ((BaseActivity) getActivity()).setOnBackPressed(listener);
    }
}
