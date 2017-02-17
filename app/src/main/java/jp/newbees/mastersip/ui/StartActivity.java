package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.TutorialPagerAdapter;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.auth.StartPresenter;
import jp.newbees.mastersip.ui.auth.LoginActivity;
import jp.newbees.mastersip.ui.auth.RegisterBaseActivity;
import jp.newbees.mastersip.ui.auth.RegisterDateOfBirthActivity;
import jp.newbees.mastersip.ui.auth.RegisterProfileMaleActivity;
import jp.newbees.mastersip.ui.auth.TipPageActivity;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class StartActivity extends RegisterBaseActivity implements View.OnClickListener, StartPresenter.StartView {

    public static final String IS_REGISTERED = "IS_REGISTERED";
    private static final String TAG = "StartActivity";

    private Button btnRegister;
    private Button btnLogin;
    private ImageView imgFbLogin;

    private ViewPager pagerTutorial;
    private TutorialPagerAdapter tutorialPagerAdapter;
    private StartPresenter startPresenter;
    private CallbackManager callbackManager;

    @Override
    protected int layoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        startPresenter = new StartPresenter(getApplicationContext(), this);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        imgFbLogin = (ImageView) findViewById(R.id.img_fb_login);
        pagerTutorial = (ViewPager) findViewById(R.id.pager_tutorial);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        imgFbLogin.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        tutorialPagerAdapter = new TutorialPagerAdapter(getApplicationContext(), getDrawableIds());
        pagerTutorial.setAdapter(tutorialPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                goRegisterDOBActivity();
                break;
            case R.id.btn_login:
                goLoginActivity();
                break;
            case R.id.img_fb_login:
                loginByFacebook();
                break;
            default:
                break;
        }
    }

    private void loginByFacebook() {
        showLoading();
        startPresenter.loginFacebook(this, callbackManager);
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void goRegisterDOBActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterDateOfBirthActivity.class);
        startActivity(intent);
    }

    private ArrayList<Integer> getDrawableIds() {
        ArrayList<Integer> drawableIds = new ArrayList<>();
        drawableIds.add(R.drawable.master_sip_logo);
        drawableIds.add(R.drawable.slide_tutorial_page_2);
        drawableIds.add(R.drawable.slide_tutorial_page_3);
        drawableIds.add(R.drawable.slide_tutorial_page_4);
        return drawableIds;
    }

    @Override
    public void didLoginVoIP() {
        disMissLoading();
        startTopScreenWithNewTask();
    }

    @Override
    public void didErrorVoIP(String errorMessage) {
        disMissLoading();
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didLoadFacebookFailure(String errorMessage) {
        disMissLoading();
        Logger.e(TAG, errorMessage);
    }

    @Override
    public void didLoginFacebookMissingBirthday(UserItem userItem) {
        Intent intent = new Intent(getApplicationContext(), RegisterDateOfBirthActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RegisterDateOfBirthActivity.USER_ITEM, userItem);
        bundle.putBoolean(IS_REGISTERED, false);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void didLoginFacebookButNotRegisterOnServer(UserItem userItem) {
        if (userItem.getGender() == UserItem.FEMALE) {
            gotoTipScreen();
        } else {
            gotoMaleProfile();
        }
    }

    @Override
    public void didBirthdayIsBelow18() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).
                setMessage(R.string.mess_notify_user_under_the_age_of_18)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    private void gotoTipScreen() {
        Intent intent = new Intent(getApplicationContext(), TipPageActivity.class);
        startActivity(intent);
    }

    private void gotoMaleProfile() {
        Intent intent = new Intent(getApplicationContext(), RegisterProfileMaleActivity.class);
        startActivity(intent);
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, StartActivity.class);
        activity.startActivity(intent);
    }
}
