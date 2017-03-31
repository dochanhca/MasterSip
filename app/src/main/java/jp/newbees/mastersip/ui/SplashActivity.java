package jp.newbees.mastersip.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.fcm.MyFirebaseMessagingService;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.auth.SplashPresenter;
import jp.newbees.mastersip.ui.auth.RegisterBaseActivity;
import jp.newbees.mastersip.ui.auth.RegisterDateOfBirthActivity;
import jp.newbees.mastersip.ui.top.TopActivity;

import static jp.newbees.mastersip.fcm.MyFirebaseMessagingService.FROM_USER;
import static jp.newbees.mastersip.ui.StartActivity.IS_REGISTERED;

/**
 * Created by ducpv on 2/17/17.
 */

public class SplashActivity extends RegisterBaseActivity implements SplashPresenter.SplashView {

    private SplashPresenter splashPresenter;
    private static final long TIME_DELAY = 500;
    private UserItem userItem;
    private boolean isFromMissCall;

    @Override
    protected int layoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        //Init views
        if (getIntent().getExtras() != null) {
            userItem = getIntent().getExtras().getParcelable(FROM_USER);
            isFromMissCall = getIntent().getExtras().
                    getBoolean(MyFirebaseMessagingService.IS_FROM_MISS_CALL, false);
        }
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        splashPresenter = new SplashPresenter(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               handleLoginUser();
            }
        }, TIME_DELAY);

    }

    private void handleLoginUser() {
        if (checkUserLogin()) {
            splashPresenter.loginVoIP();
        } else {
            handleRegisterException();
        }
    }

    /**
     * User registered
     * if gender = Male redirect to Register Profile Screen
     * else redirect to Tip Page Screen
     */
    private void handleRegisterException() {
        if (getUserItem() == null) {
            StartActivity.startActivity(this);
            return;
        }
        Intent intent = new Intent(getApplicationContext(), RegisterDateOfBirthActivity.class);
        intent.putExtra(IS_REGISTERED, true);
        startActivity(intent);
    }

    @Override
    public void didLoginVoIP() {
        if (userItem == null) {
            startTopScreenWithNewTask();
        } else {
            startTopScreenForPush();
        }
    }

    @Override
    public void didLoginVoIPError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void startTopScreenForPush() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MyFirebaseMessagingService.FROM_USER, userItem);
        bundle.putBoolean(MyFirebaseMessagingService.IS_FROM_MISS_CALL, isFromMissCall);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
