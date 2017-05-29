package jp.newbees.mastersip.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.NavigationLayoutChild;
import jp.newbees.mastersip.customviews.NavigationLayoutGroup;
import jp.newbees.mastersip.event.FooterDialogEvent;
import jp.newbees.mastersip.footerdialog.FooterManager;
import jp.newbees.mastersip.linphone.LinphoneHandler;
import jp.newbees.mastersip.linphone.LinphoneService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.chatting.ChatActivity;
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

    // properties for footer dialog
    private View rootView;
    private View footerDialog;
    private View contentFooterDialog;
    private View paddingView;
    private TextView txtContentFooterDialog;
    private ImageView imgIconFooterDialog;
    private Animation showFooterDialogAnim;
    private Animation hideFooterDialogAnim;
    private boolean isShowingFooterDialog = true;

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

        prepareToShowFooterDialog();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.root_view_with_footer_dialog, null);
        ViewGroup containerContent = (ViewGroup) rootView.findViewById(R.id.main_content);
        View contentView = layoutInflater.inflate(layoutResID, null);
        containerContent.addView(contentView);
        super.setContentView(rootView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        FooterManager.changeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
        if (!hasBottomNavigation()) {
            return;
        }
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
        if (!hasBottomNavigation()) {
            return;
        }
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
        if (!hasBottomNavigation()) {
            return false;
        }
        return isShowNavigationBar;
    }

    public void setBudgieMessage(String value) {
        ConfigManager.getInstance().setUnreadMessage(value);
        if (!hasBottomNavigation()) {
            return;
        }
        if (value.equals("0")) {
            navigationMessage.setShowBoxValue(false);
        } else {
            navigationMessage.showBoxValue(value);
        }
    }

    public void setBudgieFootPrint(String value) {
        ConfigManager.getInstance().setUnReadFootPrint(value);
        if (!hasBottomNavigation()) {
            return;
        }
        if (value.equals("0")) {
            navigationLeg.setShowBoxValue(false);
        } else {
            navigationLeg.showBoxValue(value);
        }
    }

    public void setBudgieFollower(String value) {
        ConfigManager.getInstance().setUnReadFollow(value);
        if (!hasBottomNavigation()) {
            return;
        }
        if (value.equals("0")) {
            navigationHeart.setShowBoxValue(false);
        } else {
            navigationHeart.showBoxValue(value);
        }
    }

    public void setBadgeUserOnline(String value) {
        ConfigManager.getInstance().setUserOnline(value);
    }

    public void setBadgeMyMenuNotify(String value) {
        ConfigManager.getInstance().setUnReadMyMenu(value);
        if (!hasBottomNavigation()) {
            return;
        }
        if (value.equals("0")) {
            navigationMenu.setShowBoxValue(false);
        } else {
            navigationMenu.showBoxValue(value);
        }
    }

    private boolean hasBottomNavigation() {
        return this instanceof BottomNavigation;
    }

    public interface BottomNavigation {
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHasFooterDialogEvent(FooterDialogEvent footerDialogEvent) {
        FooterManager.getInstance(this).add(footerDialogEvent);

    }

    /**
     * need call before showFooterDialog method to android draw view
     *
     * @param footerDialogEvent
     */
    public void fillDataToFooterDialog(final FooterDialogEvent footerDialogEvent) {

        txtContentFooterDialog.setText(footerDialogEvent.getMessage());
        imgIconFooterDialog.setImageResource(footerDialogEvent.getIconResourceId());
        contentFooterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(footerDialogEvent);
                hideFooterDialog();
            }
        });
    }

    private void prepareToShowFooterDialog() {
        footerDialog = rootView.findViewById(R.id.footer_dialog_layout);
        contentFooterDialog = footerDialog.findViewById(R.id.content_footer_dialog);
        paddingView = footerDialog.findViewById(R.id.padding_view);

        showFooterDialogAnim = AnimationUtils.loadAnimation(this, R.anim.show_footer_dialog);
        hideFooterDialogAnim = AnimationUtils.loadAnimation(this, R.anim.hide_footer_dialog);
        txtContentFooterDialog = (TextView) footerDialog.findViewById(R.id.txt_content);
        imgIconFooterDialog = (ImageView) footerDialog.findViewById(R.id.img_icon);

        addConstrainToFooterDialog();
        hideFooterDialog();
    }

    public void showFooterDialog(final FooterDialogEvent footerDialogEvent) {
        isShowingFooterDialog = true;

        updateViewToRedrawFooterDialog(footerDialogEvent.getMessage());

        contentFooterDialog.setClickable(true);
        footerDialog.setVisibility(View.VISIBLE);
        footerDialog.startAnimation(showFooterDialogAnim);
        hideFooterDialogAfter(FooterManager.SHOW_TIME + FooterManager.ANIM_TIME);
    }

    private void updateViewToRedrawFooterDialog(String msg) {
        txtContentFooterDialog.setText(msg);
    }

    private void hideFooterDialogAfter(int timeDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFooterDialog();
            }
        }, timeDelay);
    }

    public void addConstrainToFooterDialog() {
        if (this instanceof ChatActivity) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) paddingView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.height_footer_chat));
        } else {
            addConstrainWhenKeyboardShowing();
        }
    }

    private void addConstrainWhenKeyboardShowing() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Window mRootWindow = getWindow();
                View view = mRootWindow.getDecorView();
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int screenHeight = view.getHeight();
                int keyboardHeight = screenHeight - (rect.bottom - rect.top);

                int offsetNavigationSystemBar = screenHeight - rootView.getHeight();

                int[] viewPositionsInScreen = new int[2];
                rootView.getLocationInWindow(viewPositionsInScreen);

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int offsetSystemStatusBar = dm.heightPixels - rootView.getMeasuredHeight();

                // update margin layout footer dialog
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) paddingView.getLayoutParams();
                if (keyboardHeight > screenHeight / 3) {
                    layoutParams.setMargins(0, 0, 0, keyboardHeight - offsetNavigationSystemBar + (viewPositionsInScreen[1] - offsetSystemStatusBar));
                } else {
                    layoutParams.setMargins(0, 0, 0, 0);
                }
            }
        });
    }

    private void redirect(FooterDialogEvent footerDialogEvent) {
        switch (footerDialogEvent.getType()) {
            case Constant.FOOTER_DIALOG_TYPE.SEND_GIFT:
            case Constant.FOOTER_DIALOG_TYPE.CHAT_TEXT:
                ChatActivity.startChatActivity(this, footerDialogEvent.getCompetitor());
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOLLOW:
                TopActivity.navigateToFragment(this, TopActivity.FOLLOW_FRAGMENT);
                break;
            case Constant.FOOTER_DIALOG_TYPE.FOOT_PRINT:
                TopActivity.navigateToFragment(this, TopActivity.FOOT_PRINT_FRAGMENT);
                break;
            case Constant.FOOTER_DIALOG_TYPE.USER_ONLINE_NOTIFY:
                ConfigManager.getInstance().savePushUserOnl(true);
                TopActivity.navigateToFragment(this, TopActivity.MY_MENU_FRAGMENT_CONTAINER);
            default:
                break;
        }
    }

    private void hideFooterDialog() {
        if (!isShowingFooterDialog) {
            return;
        }
        isShowingFooterDialog = false;
        contentFooterDialog.setClickable(false);
        clearViewAnimation(footerDialog, hideFooterDialogAnim, View.INVISIBLE);
        footerDialog.startAnimation(hideFooterDialogAnim);
    }
}


