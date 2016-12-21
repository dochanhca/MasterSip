package jp.newbees.mastersip.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.dialog.DialogLoading;
import jp.newbees.mastersip.ui.dialog.MessageDialog;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Logger;
import jp.newbees.mastersip.utils.MyContextWrapper;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static boolean mIsDialogShowing;
    private boolean mInterrupted;
    private DialogLoading dialogLoadingData;
    private boolean isActivityPaused;
    private String mCurrentContentLoading;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String TAG = getClass().getSimpleName();
    private MessageDialog messageDialog;

    protected ImageView imgBack;
    protected TextView txtActionBarTitle;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, Constant.Application.DEFAULT_LANGUAGE));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.e(TAG, "Create");

        setupSharePreference();

        setContentView(layoutId());
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
    }

    protected abstract int layoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initVariables(Bundle savedInstanceState);

    protected void initHeader(String title) {
        txtActionBarTitle = (TextView) findViewById(R.id.txt_action_bar_title);
        imgBack = (ImageView) findViewById(R.id.img_back);

        txtActionBarTitle.setText(title);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
            dialogLoadingData = new DialogLoading();
            Bundle bundle = new Bundle();
            bundle.putString(DialogLoading.CONTENT_DIALOG, mCurrentContentLoading);
            dialogLoadingData.setArguments(bundle);
            dialogLoadingData.show(getFragmentManager(), "DialogLoadingData");
        }
    }

    public final void disMissLoading() {
        mInterrupted = false;
        mIsDialogShowing = false;
        if (null != dialogLoadingData) {
            dialogLoadingData.dismissDialog();
            dialogLoadingData = null;
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

        if (messageDialog.getDialog() != null && messageDialog.getDialog().isShowing()) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(MessageDialog.MESSAGE_DIALOG_TITLE, title);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_CONTENT, content);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_NOTE, note);
        bundle.putBoolean(MessageDialog.IS_HIDE_ACTION_BUTTON, isHideActionButton);

        messageDialog.setArguments(bundle);
        messageDialog.show(getFragmentManager(), "MessageDialog");
    }

    protected void disMissMessageDialog() {
        if (null != messageDialog) {
            messageDialog.dismiss();
        }
    }

    private void setupSharePreference() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(Constant.Application.PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    protected SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            setupSharePreference();
        }
        return sharedPreferences;
    }

    protected SharedPreferences.Editor getEditor() {
        if (editor == null) {
            setupSharePreference();
        }
        return editor;
    }

    protected boolean checkUserLogin() {
        return sharedPreferences.getBoolean(Constant.Application.LOGIN_FLAG, false);
    }

    protected UserItem getUserItem() {
        Gson gson = new Gson();
        String jUser = sharedPreferences.getString(Constant.Application.USER_ITEM, null);
        return gson.fromJson(jUser, UserItem.class);
    }
}

