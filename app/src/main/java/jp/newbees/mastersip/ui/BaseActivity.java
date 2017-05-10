package jp.newbees.mastersip.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutChild;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.dialog.LoadingDialog;
import jp.newbees.mastersip.ui.dialog.MessageDialog;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.ExceptionVolleyHelper;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyContextWrapper;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseActivity extends AppCompatActivity implements MessageDialog.OnMessageDialogClickListener {

    private boolean mIsMessageDialogShowing;
    private boolean mIsDialogShowing;
    private boolean mInterrupted;
    private LoadingDialog loadingDialog;
    private boolean isActivityPaused;
    private String mCurrentContentLoading;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected String TAG;
    private MessageDialog messageDialog;

    protected ImageView imgBack;
    protected TextView txtActionBarTitle;

    protected boolean isShowNavigationBar;
    protected NavigationLayoutGroup navigationLayoutGroup;
    protected NavigationLayoutChild navigationMessage;
    protected NavigationLayoutChild navigationLeg;
    protected NavigationLayoutChild navigationHeart;
    protected NavigationLayoutChild navigationMenu;
    protected ViewGroup navigationBar;

    private Animation slideDown;
    private Animation slideUp;

    private OnBackPressed onBackPressed;

    public interface OnBackPressed {
        void onBackPressed();
    }

    public void setOnBackPressed(OnBackPressed onBackPressed) {
        this.onBackPressed = onBackPressed;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, Constant.Application.DEFAULT_LANGUAGE));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        if (!Constant.Application.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        TAG = getClass().getSimpleName();
        Logger.e(TAG, "Create");
        setupSharePreference();

        if (this instanceof BottomNavigation) {
            navigationLayoutGroup = (NavigationLayoutGroup) findViewById(R.id.nav_group);
            navigationMessage = (NavigationLayoutChild) findViewById(R.id.nav_message);
            navigationLeg = (NavigationLayoutChild) findViewById(R.id.nav_leg);
            navigationHeart = (NavigationLayoutChild) findViewById(R.id.nav_heart);
            navigationMenu = (NavigationLayoutChild) findViewById(R.id.nav_menu);
            navigationBar = (ViewGroup) findViewById(R.id.navigation_bar);
        }

        initViews(savedInstanceState);
        initVariables(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityPaused = false;
        if (mIsDialogShowing && mInterrupted) {
            mInterrupted = false;
            showLoading(mCurrentContentLoading);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        isActivityPaused = true;
        if (null != loadingDialog && mIsDialogShowing) {
            mInterrupted = true;
            loadingDialog.dismissDialog();
            loadingDialog = null;
        }
    }

    @Override
    public void onMessageDialogOkClick() {
        disMissMessageDialog();
    }

    protected abstract int layoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initVariables(Bundle savedInstanceState);

    public void initHeader(String title) {
        initHeader(title, null);
    }

    public void initHeader(String title, View.OnClickListener onHeaderClickListener) {
        txtActionBarTitle = (TextView) findViewById(R.id.txt_action_bar_title);
        imgBack = (ImageView) findViewById(R.id.img_back);

        txtActionBarTitle.setText(title);
        if (onHeaderClickListener != null) {
            txtActionBarTitle.setOnClickListener(onHeaderClickListener);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageBackPressed();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (onBackPressed != null) {
            onBackPressed.onBackPressed();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!LinphoneService.isRunning()) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            LinphoneHandler.getInstance().adjustVolume(-1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            LinphoneHandler.getInstance().adjustVolume(1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void changeHeaderText(String title) {
        if (txtActionBarTitle != null) {
            txtActionBarTitle.setText(title);
        }
    }

    public void hideActionBar() {
        findViewById(R.id.action_bar).setVisibility(View.GONE);
    }

    public void showSwitchModeInHeader(View.OnClickListener onSwitchModeListener) {
        View view = findViewById(R.id.switch_mode_in_header);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(onSwitchModeListener);
    }


    public final void showLoading() {
        this.showLoading(getString(R.string.loading));
    }

    public final void showLoading(String content) {
        mCurrentContentLoading = content;
        if (mIsDialogShowing) {
            return;
        }
        mIsDialogShowing = true;
        if (isActivityPaused) {
            mInterrupted = true;
        } else {
            mInterrupted = false;
            loadingDialog = new LoadingDialog();
            Bundle bundle = new Bundle();
            bundle.putString(LoadingDialog.CONTENT_DIALOG, mCurrentContentLoading);
            loadingDialog.setArguments(bundle);
            loadingDialog.show(getSupportFragmentManager(), "DialogLoadingData");
        }
    }

    public final void disMissLoading() {
        mInterrupted = false;
        mIsDialogShowing = false;
        if (null != loadingDialog) {
            loadingDialog.dismissDialog();
            loadingDialog = null;
        }
    }

    public final boolean isShowLoading() {
        return mIsDialogShowing;
    }

    protected void showMessageDialog(String title, String content, String note,
                                     boolean isHideActionButton) {
        if (null == messageDialog) {
            messageDialog = new MessageDialog();
        }
        if (mIsMessageDialogShowing) {
            return;
        }

        mIsMessageDialogShowing = true;
        Bundle bundle = new Bundle();
        bundle.putString(MessageDialog.MESSAGE_DIALOG_TITLE, title);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_CONTENT, content);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_NOTE, note);
        bundle.putBoolean(MessageDialog.IS_HIDE_ACTION_BUTTON, isHideActionButton);

        messageDialog.setArguments(bundle);
        messageDialog.show(getSupportFragmentManager(), "MessageDialog");
    }

    protected void showMessageDialog(String content) {
        showMessageDialog("", content, "", false);
    }

    protected void disMissMessageDialog() {
        mIsMessageDialogShowing = false;
        if (null != messageDialog) {
            messageDialog.dismiss();
            messageDialog = null;
        }
    }

    private void setupSharePreference() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public void showToastExceptionVolleyError(Context context, int errorCode, String errorMessage) {
        Logger.e(TAG, "error code = " + errorCode + " : " + errorMessage);
        if (errorCode == Constant.Error.INVALID_TOKEN) {
            handleInvalidToken();
        } else {
            ExceptionVolleyHelper exceptionVolleyHelper = new ExceptionVolleyHelper(context, errorCode, errorMessage);
            if (showCommonErrorDialog(errorCode)) {
                return;
            } else if (!exceptionVolleyHelper.showCommonError()) {
                Toast.makeText(context, "".equals(errorMessage) ? getString(R.string.something_wrong)
                                : errorMessage
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleInvalidToken() {
        disMissLoading();
        ConfigManager.getInstance().resetSettings();

        Intent intentService = new Intent(getApplicationContext(), LinphoneService.class);
        getApplicationContext().stopService(intentService);

        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    public boolean showCommonErrorDialog(int errorCode) {
        switch (errorCode) {
            case Constant.Error.NO_NETWORK:
                showMessageDialog(getString(R.string.network_error), getString(R.string.mess_check_network),
                        "", false);
                return true;
            case Constant.Error.MAX_CALL_ERROR:
                showMessageDialog("", getString(R.string.mess_call_limited), getString(R.string.i_m_sorry_pls_try_again), false);
                return true;
            default:
                return false;
        }
    }

    protected boolean checkUserLogin() {
        return sharedPreferences.getBoolean(Constant.Application.LOGIN_FLAG, false);
    }

    protected UserItem getUserItem() {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        return gson.fromJson(jUser, UserItem.class);
    }

    protected void onImageBackPressed() {
        //Default do not anything
    }

    /**
     * clear animation and set visible of view after animation finish
     *
     * @param view
     * @param anim
     * @param visibility
     */
    protected void clearViewAnimation(final View view, Animation anim, final int visibility) {
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Unused
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(visibility);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Unused
            }
        });
    }

    protected void startTopScreenWithNewTask() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showNavigation() {
        if (slideUp == null) {
            slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_to_show);
        }
        isShowNavigationBar = true;
        clearViewAnimation(navigationBar, slideUp, View.VISIBLE);
        if (navigationBar != null) {
            navigationBar.startAnimation(slideUp);
        }
    }

    public void hideNavigation() {
        if (slideDown == null) {
            slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down_to_hide);
        }
        isShowNavigationBar = false;
        clearViewAnimation(navigationBar, slideDown, View.GONE);
        if (navigationBar != null) {
            navigationBar.startAnimation(slideDown);
        }
    }

    public boolean isShowNavigationBar() {
        return isShowNavigationBar;
    }

    public void setUnreadMessageValue(int value) {
        if (value == 0) {
            navigationMessage.setShowBoxValue(false);
        } else {
            navigationMessage.showBoxValue(value);
        }
    }

    public interface BottomNavigation {
    }
}


